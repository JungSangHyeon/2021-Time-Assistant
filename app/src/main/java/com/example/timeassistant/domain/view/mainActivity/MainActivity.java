package com.example.timeassistant.domain.view.mainActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timeassistant.R;
import com.example.timeassistant.domain.model.AlarmDao;
import com.example.timeassistant.domain.model.AlarmDatabase;
import com.example.timeassistant.domain.view.alarmSettingDialog.AlarmSettingDialog;
import com.example.timeassistant.domain.view.mainActivity.alarmList.AlarmAdapter;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ImageView addImageView;
    private RecyclerView alarmList;
    private TextView emptyAlarmListPlaceHoldTextView;

    // Alarm Delete, Repeat Need

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find
        this.addImageView = this.findViewById(R.id.mainActivity_titleBar_addImageView);
        this.alarmList = this.findViewById(R.id.mainActivity_alarmList);
        this.emptyAlarmListPlaceHoldTextView = this.findViewById(R.id.mainActivity_emptyAlarmListPlaceHoldTextView);

        // Set Attribute
        this.alarmList.setLayoutManager(new LinearLayoutManager(this));

        // Set Callback
        this.addImageView.setOnClickListener(this::addAlarm);
        AlarmDatabase alarmDatabase = AlarmDatabase.getDatabase(this);
        AlarmDao alarmDao = alarmDatabase.alarmDao();
        alarmDao.getData().observe(this, o->{
            AlarmAdapter alarmAdapter = new AlarmAdapter( o);
            this.alarmList.setAdapter(alarmAdapter);
            if(alarmAdapter.getItemCount()!=0){
                this.emptyAlarmListPlaceHoldTextView.setVisibility(View.GONE);
            }else{
                this.emptyAlarmListPlaceHoldTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addAlarm(View view) {
        new AlarmSettingDialog(this).show();
    }
}