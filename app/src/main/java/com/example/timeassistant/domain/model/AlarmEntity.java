package com.example.timeassistant.domain.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "alarm_table")
public class AlarmEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "alarmJson")
    private String alarmJson;

}
