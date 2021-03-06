package com.example.timeassistant.database;

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
    long insert(AlarmEntity alarmEntity);

    @Update
    void update(AlarmEntity... alarmEntities);

    @Delete
    void delete(AlarmEntity alarmEntity);

    @Query("SELECT * FROM alarm_table ORDER BY timeValueForSort")
    LiveData<List<AlarmEntity>> getData();
}
