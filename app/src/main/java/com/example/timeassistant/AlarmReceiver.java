package com.example.timeassistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.example.timeassistant.Constant.ALARM_ID_KEY_NAME;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("ALARM RECEIVE RECEIVER", intent.getLongExtra(ALARM_ID_KEY_NAME, -1)+"");

        Intent serviceIntent = new Intent(context, AlarmService.class);
        serviceIntent.putExtra(ALARM_ID_KEY_NAME, intent.getLongExtra(ALARM_ID_KEY_NAME, -1));
        context.startService(serviceIntent);
    }
}