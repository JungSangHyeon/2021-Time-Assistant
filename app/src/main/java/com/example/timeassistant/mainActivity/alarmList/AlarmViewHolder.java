package com.example.timeassistant.mainActivity.alarmList;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.timeassistant.AlarmSettingDialog;
import com.example.timeassistant.R;
import com.example.timeassistant.database.Alarm;
import com.example.timeassistant.database.AlarmEntity;
import com.example.timeassistant.database.GsonConverter;

import java.util.Calendar;
import java.util.Locale;

public class AlarmViewHolder extends RecyclerView.ViewHolder {

    private static final String[] WeekDayString = {"일", "월", "화", "수", "목", "금", "토"};
    private static final String AM = "am", PM = "pm";

    private TextView timeTextView, weekDayTextView, textToSpeechTextView;
    private AlarmEntity alarmEntity;
    private Alarm alarm;

    public AlarmViewHolder(View itemView) {
        super(itemView);

        this.timeTextView = itemView.findViewById(R.id.alarmItem_timeTextView);
        this.weekDayTextView = itemView.findViewById(R.id.alarmItem_weekDayTextView);
        this.textToSpeechTextView = itemView.findViewById(R.id.alarmItem_textToSpeechTextView);

        itemView.setOnClickListener(this::edit);
    }

    private void edit(View view) {
        new AlarmSettingDialog(this.itemView.getContext(), this.alarmEntity).show();
    }

    public void setData(AlarmEntity alarmEntity) {
        this.alarmEntity=alarmEntity;
        this.alarm = GsonConverter.fromStringToType(alarmEntity.getAlarmJson(), Alarm.class);
        this.initializeView();
    }

    private void initializeView() {
        this.setTime();
        this.setWeekday();
        this.setTextToSpeech();
    }
    private void setTime() {
        String amPm = this.alarm.getAmPm()== Calendar.AM? AM:PM;
        String hour = this.getLength2String(this.alarm.getHour());
        String minute = this.getLength2String(this.alarm.getMinute());
        String time = amPm+" "+hour+":"+minute;
        this.timeTextView.setText(time);
    }
    private void setWeekday() {
        StringBuilder weekDay = new StringBuilder();
        for(int i=0; i<7; i++) if(this.alarm.getWeekDays()[i]) weekDay.append(WeekDayString[i]);
        this.weekDayTextView.setText(weekDay.toString());
    }
    private void setTextToSpeech() {
        this.textToSpeechTextView.setText(this.alarm.getTextToSpeech());
    }

    private String getLength2String(int i){ return String.format(Locale.getDefault(), "%02d", i); }
}
