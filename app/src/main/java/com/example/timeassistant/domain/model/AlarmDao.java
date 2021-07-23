package com.example.timeassistant.domain.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlarmDao {

    @Insert
    void insert(AlarmEntity alarmEntity);

    @Update
    void update(AlarmEntity... alarmEntities);

    @Delete
    void delete(AlarmEntity alarmEntity);

    @Query("SELECT * FROM alarm_table")
    LiveData<List<AlarmEntity>> getData();
}
