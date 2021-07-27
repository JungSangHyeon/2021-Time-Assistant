package com.example.timeassistant.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "alarm_table")
public class AlarmEntity { // implements Comparable<AlarmEntity>

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "timeValueForSort")
    private int timeValueForSort;

    @ColumnInfo(name = "alarmJson")
    private String alarmJson;

//    @Override
//    public int compareTo(AlarmEntity alarmEntity) {
//        Alarm thisAlarm = GsonConverter.fromStringToType(this.alarmJson, Alarm.class);
//        Alarm otherAlarm = GsonConverter.fromStringToType(alarmEntity.alarmJson, Alarm.class);
//
//        int thisAlarmTimeValue = thisAlarm.getAmPm()*12*60 + thisAlarm.getHour()*60 + thisAlarm.getMinute();
//        int otherAlarmTimeValue = otherAlarm.getAmPm()*12*60 + otherAlarm.getHour()*60 + otherAlarm.getMinute();
//
//        return Integer.compare(thisAlarmTimeValue, otherAlarmTimeValue);
//    }
}
