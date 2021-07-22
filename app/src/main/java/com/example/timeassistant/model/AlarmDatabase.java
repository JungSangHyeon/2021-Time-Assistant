package com.example.timeassistant.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;

@Database(entities = {AlarmEntity.class}, version = 1, exportSchema = false)
public abstract class AlarmDatabase extends androidx.room.RoomDatabase {

    private static volatile AlarmDatabase INSTANCE;

    public static AlarmDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (AlarmDatabase.class){
                if(INSTANCE==null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AlarmDatabase.class, "alarm_database")
                            .fallbackToDestructiveMigration() // 버전 바꾸면 리셋
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract AlarmDao alarmDao();
}
