package com.example.timeassistant.view.mainActivity.alarmList;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.timeassistant.R;
import com.example.timeassistant.model.Alarm;
import com.example.timeassistant.model.AlarmEntity;
import com.example.timeassistant.model.GsonConverter;

import java.util.Arrays;

public class AlarmViewHolder extends RecyclerView.ViewHolder {

    private TextView timeTextView, weekDayTextView, textToSpeechTextView;

    public AlarmViewHolder(View itemView) {
        super(itemView);

        this.timeTextView = itemView.findViewById(R.id.alarmItem_timeTextView);
        this.weekDayTextView = itemView.findViewById(R.id.alarmItem_weekDayTextView);
        this.textToSpeechTextView = itemView.findViewById(R.id.alarmItem_textToSpeechTextView);
    }

    public void setData(AlarmEntity alarmEntity) {
        Alarm alarm = GsonConverter.fromStringToType(alarmEntity.getAlarmJson(), Alarm.class);
        this.timeTextView.setText(alarm.getAmPm()+", "+ alarm.getHour()+", "+alarm.getMinute());
        this.weekDayTextView.setText(Arrays.toString(alarm.getWeekDays()));
        this.textToSpeechTextView.setText(alarm.getTextToSpeech());
    }
}
