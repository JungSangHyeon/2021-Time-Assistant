package com.example.timeassistant.domain;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.timeassistant.domain.model.Alarm;
import com.example.timeassistant.domain.model.AlarmDao;
import com.example.timeassistant.domain.model.AlarmDatabase;
import com.example.timeassistant.domain.model.AlarmEntity;
import com.example.timeassistant.domain.model.GsonConverter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AlarmService extends Service {

    TextToSpeech tts;
    static int i = 0;

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        i++;
        AlarmDatabase alarmDatabase = AlarmDatabase.getDatabase(this);
        AlarmDao alarmDao = alarmDatabase.alarmDao();
        LiveData<List<AlarmEntity>> liveData = alarmDao.getData();
        liveData.observeForever(new Observer<List<AlarmEntity>>() {
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
                    if(targetEntity!=null){
                        Alarm targetAlarm = GsonConverter.fromStringToType(targetEntity.getAlarmJson(), Alarm.class);
                        Log.e("ALARM RECEIVE"+" "+i, targetAlarm.getTextToSpeech());

                        tts = new TextToSpeech(getApplicationContext(), status -> {
                            if (status != TextToSpeech.ERROR) {
                                tts.setLanguage(Locale.getDefault());
                                tts.speak(targetAlarm.getTextToSpeech()+" "+i, TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        });
                        setAlarm(targetEntity, targetAlarm);
                    }
                }
                liveData.removeObserver(this);
                AlarmService.this.stopSelf();
            }
        });
        return START_NOT_STICKY;
    }

    public void setAlarm(AlarmEntity alarmEntity, Alarm alarm) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.AM_PM, alarm.getAmPm());
        calendar.set(Calendar.HOUR, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        calendar.set(Calendar.SECOND, 0);

        calendar.add(Calendar.DATE, 1);

        while(!alarm.getWeekDays()[calendar.get(Calendar.DAY_OF_WEEK)-1]) {
            calendar.add(Calendar.DATE, 1);
        }

        // TEST
        SimpleDateFormat format2 = new SimpleDateFormat ( "yyyy년 MM월dd일 HH시mm분ss초");
        String time1 = format2.format(calendar.getTimeInMillis());
        Log.e("ALARM SET AT RECEIVER", time1);
        // TEST END

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("id", alarmEntity.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmEntity.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}