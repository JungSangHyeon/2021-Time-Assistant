package com.example.timeassistant.view.mainActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timeassistant.R;
import com.example.timeassistant.model.AlarmDao;
import com.example.timeassistant.model.AlarmDatabase;
import com.example.timeassistant.view.alarmSettingDialog.AlarmSettingDialog;
import com.example.timeassistant.view.mainActivity.alarmList.AlarmAdapter;

public class MainActivity extends AppCompatActivity {

    private ImageView addImageView;
    private RecyclerView alarmList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find
        this.addImageView = this.findViewById(R.id.mainActivity_titleBar_addImageView);
        this.alarmList = this.findViewById(R.id.mainActivity_alarmList);

        // Set Attribute
        this.alarmList.setLayoutManager(new LinearLayoutManager(this));

        // Set Callback
        this.addImageView.setOnClickListener(this::addAlarm);
        AlarmDatabase alarmDatabase = AlarmDatabase.getDatabase(this);
        AlarmDao alarmDao = alarmDatabase.alarmDao();
        alarmDao.getData().observe(this, o->{
            AlarmAdapter alarmAdapter = new AlarmAdapter( o);
            this.alarmList.setAdapter(alarmAdapter);
        });
    }

    private void addAlarm(View view) {
        new AlarmSettingDialog(this).show();
    }
}