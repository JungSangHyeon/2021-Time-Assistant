package com.example.timeassistant.domain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.timeassistant.domain.model.Alarm;
import com.example.timeassistant.domain.model.AlarmDao;
import com.example.timeassistant.domain.model.AlarmDatabase;
import com.example.timeassistant.domain.model.AlarmEntity;
import com.example.timeassistant.domain.model.GsonConverter;

import java.util.List;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("ALARM RECEIVE RECEIVER", intent.getLongExtra("id", -1)+"");

        Intent serviceIntent = new Intent(context, AlarmService.class);
        serviceIntent.putExtra("id", intent.getLongExtra("id", -1));
        context.startService(serviceIntent);
    }

}