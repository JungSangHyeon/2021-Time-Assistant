package com.example.timeassistant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.timeassistant.database.Alarm;
import com.example.timeassistant.database.AlarmDao;
import com.example.timeassistant.database.AlarmDatabase;
import com.example.timeassistant.database.AlarmEntity;
import com.example.timeassistant.database.GsonConverter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME;
import static com.example.timeassistant.Constant.ALARM_ID_KEY_NAME;

public class AlarmService extends Service {

    private TextToSpeech tts;
    private int originalMediaVolume;

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        AlarmDatabase alarmDatabase = AlarmDatabase.getDatabase(this);
        AlarmDao alarmDao = alarmDatabase.alarmDao();
        LiveData<List<AlarmEntity>> liveData = alarmDao.getData();
        liveData.observeForever(new Observer<List<AlarmEntity>>() {
            @Override
            public void onChanged(List<AlarmEntity> alarmEntities) {
                AlarmEntity targetEntity = getTargetEntity(alarmEntities, intent);
                Alarm targetAlarm = GsonConverter.fromStringToType(targetEntity.getAlarmJson(), Alarm.class);
                startSpeech(targetAlarm.getTextToSpeech());
                setAlarm(targetEntity, targetAlarm);
                liveData.removeObserver(this);
                AlarmService.this.stopSelf();

                Log.e("ALARM RECEIVE SERVICE", targetEntity.getId() + ", " + targetAlarm.getTextToSpeech());
            }
        });
        return START_NOT_STICKY;
    }

    private AlarmEntity getTargetEntity(List<AlarmEntity> alarmEntities, Intent intent) {
        AlarmEntity targetEntity = null;
        int targetEntityId = (int) intent.getLongExtra(ALARM_ID_KEY_NAME, -1);
        for (AlarmEntity alarmEntity : alarmEntities) {
            if (alarmEntity.getId() == targetEntityId) {
                targetEntity = alarmEntity;
                break;
            }
        }
        return targetEntity;
    }

    AudioFocusRequest audioFocusRequest;

    private void startSpeech(String textToSpeech) {
        tts = new TextToSpeech(getApplicationContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        Log.e("TTS", "START");
                        audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                                .build();

                        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                        audioManager.requestAudioFocus(audioFocusRequest);

//                        AudioManager mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
//                        originalMediaVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        Log.e("TTS", "DONE");

                        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                        audioManager.abandonAudioFocusRequest(audioFocusRequest);

//                        AudioManager mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
//                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalMediaVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                    }

                    @Override
                    public void onError(String utteranceId) {
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putFloat(KEY_PARAM_VOLUME, 1f);
                tts.setLanguage(Locale.getDefault());
                tts.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, parameters, "utteranceId");
            }
        });
    }

    public void setAlarm(AlarmEntity alarmEntity, Alarm alarm) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.AM_PM, alarm.getAmPm());
        calendar.set(Calendar.HOUR, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1);
        }

        while (!alarm.getWeekDays()[calendar.get(Calendar.DAY_OF_WEEK) - 1]) {
            calendar.add(Calendar.DATE, 1);
        }

        // TEST
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy년 MM월dd일 HH시mm분ss초");
        String time1 = format2.format(calendar.getTimeInMillis());
        Log.e("ALARM SET AT SERVICE", time1);
        // TEST END

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(ALARM_ID_KEY_NAME, alarmEntity.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmEntity.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}