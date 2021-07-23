package com.example.timeassistant.domain.view.mainActivity.alarmList;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.timeassistant.R;
import com.example.timeassistant.domain.model.Alarm;
import com.example.timeassistant.domain.model.AlarmEntity;
import com.example.timeassistant.domain.model.GsonConverter;
import com.example.timeassistant.domain.view.alarmSettingDialog.AlarmSettingDialog;

import java.util.Calendar;

public class AlarmViewHolder extends RecyclerView.ViewHolder {

    private static final String[] WeekDayString = {"일", "월", "화", "수", "목", "금", "토"};

    private TextView timeTextView, weekDayTextView, textToSpeechTextView;
    private AlarmEntity alarmEntity;

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
        Alarm alarm = GsonConverter.fromStringToType(alarmEntity.getAlarmJson(), Alarm.class);
        this.setTime(alarm);
        this.setWeekday(alarm);
        this.setTextToSpeech(alarm);
    }
    private void setTime(Alarm alarm) {
        String amPm = alarm.getAmPm()== Calendar.AM? "am":"pm";
        String time = String.format("%02d", alarm.getHour())+":"+String.format("%02d", alarm.getMinute());
        this.timeTextView.setText(amPm+" "+ time);
    }
    private void setWeekday(Alarm alarm) {
        String weekDay = "";
        for(int i=0; i<7; i++) if(alarm.getWeekDays()[i]) weekDay+= WeekDayString[i];
        this.weekDayTextView.setText(weekDay);
    }
    private void setTextToSpeech(Alarm alarm) {
        this.textToSpeechTextView.setText(alarm.getTextToSpeech());
    }
}
