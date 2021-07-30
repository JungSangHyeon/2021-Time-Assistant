package com.example.timeassistant.mainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timeassistant.R;
import com.example.timeassistant.database.AlarmDao;
import com.example.timeassistant.database.AlarmDatabase;
import com.example.timeassistant.AlarmSettingDialog;
import com.example.timeassistant.database.AlarmEntity;
import com.example.timeassistant.mainActivity.alarmList.AlarmAdapter;

import java.util.List;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME;
import static com.example.timeassistant.Constant.VOLUME_KEY_NAME;

public class MainActivity extends AppCompatActivity {

    private ImageView addImageView;
    private RecyclerView alarmList;
    private TextView emptyAlarmListPlaceHoldTextView;
    private SeekBar volumeSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.addImageView = this.findViewById(R.id.mainActivity_titleBar_addImageView);
        this.alarmList = this.findViewById(R.id.mainActivity_alarmList);
        this.emptyAlarmListPlaceHoldTextView = this.findViewById(R.id.mainActivity_emptyAlarmListPlaceHoldTextView);
        this.volumeSeekBar = this.findViewById(R.id.mainActivity_titleBar_volumeSeekBar);

        AlarmDatabase alarmDatabase = AlarmDatabase.getDatabase(this);
        AlarmDao alarmDao = alarmDatabase.alarmDao();
        alarmDao.getData().observe(this, this::initializeView);
    }

    private void initializeView(List<AlarmEntity> alarmEntities) {
        AlarmAdapter alarmAdapter = new AlarmAdapter(alarmEntities);
        this.alarmList.setLayoutManager(new LinearLayoutManager(this));
        this.alarmList.setAdapter(alarmAdapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.volumeSeekBar.setProgress(prefs.getInt(VOLUME_KEY_NAME, 75));
        this.emptyAlarmListPlaceHoldTextView.setVisibility(alarmAdapter.getItemCount()!=0? View.GONE:View.VISIBLE);

        this.addImageView.setOnClickListener(this::addAlarm);
        this.volumeSeekBar.setOnSeekBarChangeListener(this.volumeChangeListener);
    }

    private int originalMediaVolume;
    private TextToSpeech tts;
    AudioFocusRequest audioFocusRequest;
    Thread beforePlay;
    private SeekBar.OnSeekBarChangeListener volumeChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)*progress/100,
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
            );
            Log.e("VOLUME", "onProgressChanged: "+seekBar.getProgress());
        }
        @Override public void onStartTrackingTouch(SeekBar seekBar) {
            Log.e("VOLUME", "onStartTrackingTouch: "+seekBar.getProgress());
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

            tts = new TextToSpeech(getApplicationContext(), status -> {
                if (status != TextToSpeech.ERROR) {
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            Log.e("TTS", "START");
                            audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK).build();
                            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                            audioManager.requestAudioFocus(audioFocusRequest);
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            beforePlay = new Thread(){
                              @Override
                              public void run(){
                                  try {
                                      Thread.sleep(300);
                                      Bundle parameters = new Bundle();
                                      parameters.putFloat(KEY_PARAM_VOLUME, 1f);
                                      tts.setLanguage(Locale.getDefault());
                                      tts.stop();
                                      tts.speak("볼륨 설정 테스트 입니다. ", TextToSpeech.QUEUE_FLUSH, parameters, "utteranceId");
                                  } catch (InterruptedException e) {
                                  }
                              }
                            };
                            beforePlay.start();
                        }

                        @Override
                        public void onError(String utteranceId) {
                        }
                    });

                    Bundle parameters = new Bundle();
                    parameters.putFloat(KEY_PARAM_VOLUME, 1f);
                    tts.setLanguage(Locale.getDefault());
                    tts.speak("볼륨 설정 테스트 입니다.", TextToSpeech.QUEUE_FLUSH, parameters, "utteranceId");
                }
            });
            originalMediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if(beforePlay!=null) beforePlay.interrupt();
            tts.stop();
            Log.e("VOLUME", "onStopTrackingTouch: "+seekBar.getProgress());
            AudioManager mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalMediaVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(VOLUME_KEY_NAME, seekBar.getProgress());
            editor.apply();
            Log.e("VOLUME", "SET: "+seekBar.getProgress());
        }
    };
    private void addAlarm(View view) {
        new AlarmSettingDialog(this).show();
    }
}