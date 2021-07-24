package com.example.timeassistant.domain.view.mainActivity.alarmList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.timeassistant.R;
import com.example.timeassistant.domain.model.AlarmEntity;

import java.util.Collections;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmViewHolder> {

    List<AlarmEntity> alarmEntities;

    public AlarmAdapter(List<AlarmEntity> alarmEntities) {
        this.alarmEntities = alarmEntities;
        Collections.sort(alarmEntities);
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent,false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {
        holder.setData(alarmEntities.get(position));
    }

    @Override
    public int getItemCount() {
        return alarmEntities.size();
    }
}
