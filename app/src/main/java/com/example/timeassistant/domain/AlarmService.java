package com.example.timeassistant.domain;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.example.timeassistant.domain.model.Alarm;
import com.example.timeassistant.domain.model.AlarmDao;
import com.example.timeassistant.domain.model.AlarmDatabase;
import com.example.timeassistant.domain.model.AlarmEntity;
import com.example.timeassistant.domain.model.GsonConverter;

import java.util.List;
import java.util.Locale;

public class AlarmService extends Service {

    TextToSpeech tts;

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        AlarmDatabase alarmDatabase = AlarmDatabase.getDatabase(this);
        AlarmDao alarmDao = alarmDatabase.alarmDao();
        alarmDao.getData().observeForever(new Observer<List<AlarmEntity>>() {
            @Override
            public void onChanged(List<AlarmEntity> alarmEntities) {
                AlarmEntity targetEntity = null;
                int targetEntityId = intent.getIntExtra("id", -1);
                if (targetEntityId != -1) {
                    for (AlarmEntity alarmEntity : alarmEntities) {
                        if (alarmEntity.getId() == targetEntityId) {
                            targetEntity = alarmEntity;
                            break;
                        }
                    }
                    Alarm targetAlarm = GsonConverter.fromStringToType(targetEntity.getAlarmJson(), Alarm.class);
                    Log.e("ALARM RECEIVE", targetAlarm.getTextToSpeech());

                    tts = new TextToSpeech(getApplicationContext(), status -> {
                        if (status != TextToSpeech.ERROR) {
                            tts.setLanguage(Locale.getDefault());
                            tts.speak(targetAlarm.getTextToSpeech(), TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    });
                }
                alarmDao.getData().removeObserver(this);
            }
        });
        return START_NOT_STICKY;
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}