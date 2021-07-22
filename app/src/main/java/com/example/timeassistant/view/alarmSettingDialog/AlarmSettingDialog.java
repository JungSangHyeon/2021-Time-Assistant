package com.example.timeassistant.view.alarmSettingDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timeassistant.R;
import com.example.timeassistant.model.Alarm;
import com.example.timeassistant.model.AlarmDao;
import com.example.timeassistant.model.AlarmDatabase;
import com.example.timeassistant.model.AlarmEntity;
import com.example.timeassistant.model.GsonConverter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public class AlarmSettingDialog extends Dialog {

    public enum EType {CreateNew, Edit}
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
    
    public AlarmSettingDialog(Context context, EType eType) {
        super(context);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_alarm_setting);
        
        this.amPmChip = this.findViewById(R.id.alarmSettingDialog_amPmChip);
        this.hourEditText = this.findViewById(R.id.alarmSettingDialog_hourEditText);
        this.minuteEditText = this.findViewById(R.id.alarmSettingDialog_minuteEditText);
        this.weekdaysChipGroup = this.findViewById(R.id.alarmSettingDialog_weekDayChipGroup);
        this.textToSpeechEditText = this.findViewById(R.id.alarmSettingDialog_textToSpeechEditText);
        this.deleteButton = this.findViewById(R.id.alarmSettingDialog_deleteButton);
        this.saveButton = this.findViewById(R.id.alarmSettingDialog_saveButton);

        if(eType==EType.CreateNew) this.deleteButton.setVisibility(View.INVISIBLE);
        this.amPmChip.setOnClickListener(this::changeAmPm);
        this.deleteButton.setOnClickListener(this::delete);
        this.saveButton.setOnClickListener(this::save);
    }

    private void changeAmPm(View view) {
        if(this.amPmChip.isChecked()) this.amPmChip.setText("오후");
        else this.amPmChip.setText("오전");
    }

    private void delete(View view) {

    }

    private void save(View view) {
        if( ! isCorrectHourTyped() || ! isCorrectMinuteTyped()) {
            this.makeToastWithText("올바른 시간을 입력해 주세요");
        } else if ( ! isCorrectTextToSpeechTyped()){
            this.makeToastWithText("읽을 내용을 입력해 주세요");
        } else {
            Alarm alarm = new Alarm();
            alarm.setAmPm(this.amPmChip.isChecked()? Calendar.PM : Calendar.AM);
            alarm.setHour(this.getHour());
            alarm.setMinute(this.getMinute());
            alarm.setWeekDays(this.getWeekdays());
            alarm.setTextToSpeech(this.getTextToSpeech());

            AlarmEntity alarmEntity = new AlarmEntity();
            alarmEntity.setAlarmJson(GsonConverter.fromTypeToString(alarm));

            AlarmDatabase alarmDatabase = AlarmDatabase.getDatabase(this.getContext());
            AlarmDao alarmDao = alarmDatabase.alarmDao();

            new Thread(() -> alarmDao.insert(alarmEntity)).start();
            this.dismiss();
        }
    }

    private boolean isCorrectHourTyped() { return this.isCorrectHour(this.getHour()); }
    private int getHour() { return this.getIntFromEditText(this.hourEditText); }
    private boolean isCorrectMinuteTyped() { return this.isCorrectMinute(this.getMinute()); }
    private int getMinute() { return this.getIntFromEditText(this.minuteEditText); }
    private boolean isCorrectTextToSpeechTyped() { return ! this.getTextToSpeech().equals(""); }
    private String getTextToSpeech() { return this.textToSpeechEditText.getText().toString(); }
    private boolean[] getWeekdays() {
        boolean[] weekdays = new boolean[7];
        for(int selectedChipId : this.weekdaysChipGroup.getCheckedChipIds()) {
            weekdays[WeekDayMap.get(selectedChipId)] = true;
        }
        return weekdays;
    }
    private boolean isCorrectHour(int hour) {
        return 0 <= hour && hour <= 12;
    }
    private boolean isCorrectMinute(int minute) {
        return 0 <= minute && minute <= 60;
    }
    private int getIntFromEditText(EditText editText) {
        String typedString = editText.getText().toString();
        if(typedString.equals("")) typedString = "0";
        return Integer.parseInt(typedString);
    }
    private void makeToastWithText(String text) { Toast.makeText(this.getContext(), text, Toast.LENGTH_SHORT).show(); }
}

