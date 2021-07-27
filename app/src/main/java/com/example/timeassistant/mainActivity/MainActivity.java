package com.example.timeassistant.mainActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

public class MainActivity extends AppCompatActivity {

    private ImageView addImageView;
    private RecyclerView alarmList;
    private TextView emptyAlarmListPlaceHoldTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.addImageView = this.findViewById(R.id.mainActivity_titleBar_addImageView);
        this.alarmList = this.findViewById(R.id.mainActivity_alarmList);
        this.emptyAlarmListPlaceHoldTextView = this.findViewById(R.id.mainActivity_emptyAlarmListPlaceHoldTextView);

        AlarmDatabase alarmDatabase = AlarmDatabase.getDatabase(this);
        AlarmDao alarmDao = alarmDatabase.alarmDao();
        alarmDao.getData().observe(this, this::initializeView);
    }

    private void initializeView(List<AlarmEntity> alarmEntities) {
        AlarmAdapter alarmAdapter = new AlarmAdapter(alarmEntities);
        this.alarmList.setLayoutManager(new LinearLayoutManager(this));
        this.alarmList.setAdapter(alarmAdapter);

        this.emptyAlarmListPlaceHoldTextView.setVisibility(alarmAdapter.getItemCount()!=0? View.GONE:View.VISIBLE);

        this.addImageView.setOnClickListener(this::addAlarm);
    }

    private void addAlarm(View view) {
        new AlarmSettingDialog(this).show();
    }
}