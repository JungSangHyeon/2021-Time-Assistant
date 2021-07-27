package com.example.timeassistant.mainActivity.alarmList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.timeassistant.R;
import com.example.timeassistant.database.AlarmEntity;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmViewHolder> {

    private List<AlarmEntity> alarmEntities;

    public AlarmAdapter(List<AlarmEntity> alarmEntities) {
        this.alarmEntities = alarmEntities;
    }

    @NotNull
    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent,false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {
        holder.setData(this.alarmEntities.get(position));
    }

    @Override
    public int getItemCount() {
        return this.alarmEntities.size();
    }
}
