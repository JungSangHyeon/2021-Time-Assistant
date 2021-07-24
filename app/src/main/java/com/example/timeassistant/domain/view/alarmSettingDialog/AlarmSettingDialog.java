package com.example.timeassistant.domain.view.alarmSettingDialog;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timeassistant.R;
import com.example.timeassistant.domain.AlarmReceiver;
import com.example.timeassistant.domain.model.Alarm;
import com.example.timeassistant.domain.model.AlarmDao;
import com.example.timeassistant.domain.model.AlarmEntity;
import com.example.timeassistant.domain.model.AlarmDatabase;
import com.example.timeassistant.domain.model.GsonConverter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AlarmSettingDialog extends Dialog {

    public static final Map<Integer, Integer> WeekDayMap = new HashMap<Integer, Integer>() {{
        put(R.id.alarmSettingDialog_sunChip, 0);
        put(R.id.alarmSettingDialog_monChip, 1);
        put(R.id.alarmSettingDialog_tusChip, 2);
        put(R.id.alarmSettingDialog_wenChip, 3);
        put(R.id.alarmSettingDialog_thuChip, 4);
        put(R.id.alarmSettingDialog_friChip, 5);
        put(R.id.alarmSettingDialog_satChip, 6);
    }};

    private Chip amPmChip;
    private EditText hourEditText, minuteEditText;
    private ChipGroup weekdaysChipGroup;
    private EditText textToSpeechEditText;
    private Button deleteButton, saveButton;

    private AlarmEntity alarmEntity;
    private Alarm editTarget;
    
    public AlarmSettingDialog(Context context) {
        super(context);
        this.commonConstructor();
        this.deleteButton.setVisibility(View.INVISIBLE);
    }
    public AlarmSettingDialog(Context context, AlarmEntity alarmEntity) {
        super(context);
        this.alarmEntity=alarmEntity;
        this.editTarget = GsonConverter.fromStringToType(alarmEntity.getAlarmJson(), Alarm.class);;
        this.commonConstructor();
        this.deleteButton.setOnClickListener(this::delete);
        this.setEditData();
    }

    private void setEditData() {
        if (this.editTarget.getAmPm() == 1) {
            this.amPmChip.setChecked(true);
            this.amPmChip.setText("오후");
        }
        this.hourEditText.setText(Integer.toString(this.editTarget.getHour()));
        this.minuteEditText.setText(Integer.toString(this.editTarget.getMinute()));
        for(int i=0; i<7; i++){
            if(this.editTarget.getWeekDays()[i]) {
                Chip chip = this.findViewById(this.weekdaysChipGroup.getChildAt(i).getId());
                chip.setChecked(true);
            }
        }
        this.textToSpeechEditText.setText(this.editTarget.getTextToSpeech());
    }

    private void commonConstructor(){
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_alarm_setting);

        this.amPmChip = this.findViewById(R.id.alarmSettingDialog_amPmChip);
        this.hourEditText = this.findViewById(R.id.alarmSettingDialog_hourEditText);
        this.minuteEditText = this.findViewById(R.id.alarmSettingDialog_minuteEditText);
        this.weekdaysChipGroup = this.findViewById(R.id.alarmSettingDialog_weekDayChipGroup);
        this.textToSpeechEditText = this.findViewById(R.id.alarmSettingDialog_textToSpeechEditText);
        this.deleteButton = this.findViewById(R.id.alarmSettingDialog_deleteButton);
        this.saveButton = this.findViewById(R.id.alarmSettingDialog_saveButton);

        this.amPmChip.setOnClickListener(this::changeAmPm);
        this.saveButton.setOnClickListener(this::save);
    }

    private void changeAmPm(View view) {
        if(this.amPmChip.isChecked()) this.amPmChip.setText("오후");
        else this.amPmChip.setText("오전");
    }

    private void delete(View view) {
        AlarmDatabase alarmDatabase = AlarmDatabase.getDatabase(this.getContext());
        AlarmDao alarmDao = alarmDatabase.alarmDao();
        new Thread(() -> alarmDao.delete(this.alarmEntity)).start();
        // Remove Alarm

        Log.e("ALARM REMOVE", alarmEntity.getId()+"");

        Intent intent = new Intent(this.getContext(), AlarmReceiver.class);
        intent.putExtra("id", alarmEntity.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getContext(), alarmEntity.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        this.dismiss();
    }

    private void save(View view) {
        if (!isCorrectHourTyped() || !isCorrectMinuteTyped()) {
            this.makeToastWithText("올바른 시간을 입력해 주세요");
        } else if (!isCorrectWeekDay()) {
            this.makeToastWithText("요일을 선택해 주세요");
        } else if (!isCorrectTextToSpeechTyped()) {
            this.makeToastWithText("읽을 내용을 입력해 주세요");
        } else {
            Alarm alarm = this.createAlarm();
            AlarmEntity alarmEntity;
            if(this.alarmEntity==null){
                alarmEntity = this.createAlarmEntity(alarm);

                AlarmDatabase alarmDatabase = AlarmDatabase.getDatabase(this.getContext());
                AlarmDao alarmDao = alarmDatabase.alarmDao();
                new Thread(() -> {
                    long id = alarmDao.insert(alarmEntity);
                    this.setAlarm(alarmEntity, alarm, id);
                }).start();
            }else{
                alarmEntity = this.alarmEntity;
                alarmEntity.setAlarmJson(GsonConverter.fromTypeToString(alarm));

                AlarmDatabase alarmDatabase = AlarmDatabase.getDatabase(this.getContext());
                AlarmDao alarmDao = alarmDatabase.alarmDao();
                new Thread(() -> {
                    alarmDao.update(alarmEntity);
                    this.setAlarm(alarmEntity, alarm, alarmEntity.getId());
                }).start();
            }
             // TODO CALL THIS AFTER ID SET
            this.dismiss();
        }
    }

    private boolean isCorrectWeekDay() {
        boolean result = false;
        for(boolean isChecked : this.getWeekdays()) result|=isChecked;
        return result;
    }

    public void setAlarm(AlarmEntity alarmEntity, Alarm alarm, long id) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.AM_PM, alarm.getAmPm());
        calendar.set(Calendar.HOUR, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        calendar.set(Calendar.SECOND, 0);

        if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1);
        }

        while(!alarm.getWeekDays()[calendar.get(Calendar.DAY_OF_WEEK)-1]) {
            calendar.add(Calendar.DATE, 1);
        }

        // TEST
        SimpleDateFormat format2 = new SimpleDateFormat ( "yyyy년 MM월dd일 HH시mm분ss초");
        String time1 = format2.format(calendar.getTimeInMillis());
        Log.e("ALARM SET", id+", "+time1);
        // TEST END

        Intent intent = new Intent(this.getContext(), AlarmReceiver.class);
        intent.putExtra("id", alarmEntity.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getContext(), (int) id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private boolean isCorrectHourTyped() { return this.isCorrectHour(this.getHour()); }
    private boolean isCorrectMinuteTyped() { return this.isCorrectMinute(this.getMinute()); }
    private boolean isCorrectTextToSpeechTyped() { return ! this.getTextToSpeech().equals(""); }
    private boolean isCorrectHour(int hour) {
        return 0 <= hour && hour <= 12;
    }
    private boolean isCorrectMinute(int minute) {
        return 0 <= minute && minute <= 60;
    }

    private int getHour() { return this.getIntFromEditText(this.hourEditText); }
    private int getMinute() { return this.getIntFromEditText(this.minuteEditText); }
    private boolean[] getWeekdays() {
        boolean[] weekdays = new boolean[7];
        for(int selectedChipId : this.weekdaysChipGroup.getCheckedChipIds()) {
            weekdays[WeekDayMap.get(selectedChipId)] = true;
        }
        return weekdays;
    }
    private String getTextToSpeech() { return this.textToSpeechEditText.getText().toString(); }

    private int getIntFromEditText(EditText editText) {
        String typedString = editText.getText().toString();
        if(typedString.equals("")) typedString = "0";
        return Integer.parseInt(typedString);
    }

    private void makeToastWithText(String text) { Toast.makeText(this.getContext(), text, Toast.LENGTH_SHORT).show(); }

    private Alarm createAlarm() {
        Alarm alarm = new Alarm();
        alarm.setAmPm(this.amPmChip.isChecked()? Calendar.PM : Calendar.AM);
        alarm.setHour(this.getHour());
        alarm.setMinute(this.getMinute());
        alarm.setWeekDays(this.getWeekdays());
        alarm.setTextToSpeech(this.getTextToSpeech());
        return alarm;
    }
    private AlarmEntity createAlarmEntity(Alarm alarm) {
        AlarmEntity alarmEntity = new AlarmEntity();
        alarmEntity.setAlarmJson(GsonConverter.fromTypeToString(alarm));
        return alarmEntity;
    }
}

