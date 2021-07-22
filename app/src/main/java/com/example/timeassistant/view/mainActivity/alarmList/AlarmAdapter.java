package com.example.timeassistant.view.mainActivity.alarmList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timeassistant.R;
import com.example.timeassistant.model.AlarmDao;
import com.example.timeassistant.model.AlarmDatabase;
import com.example.timeassistant.model.AlarmEntity;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmViewHolder> {

    LiveData<List<AlarmEntity>> listLiveData;

    public AlarmAdapter(Context context) {
        AlarmDatabase alarmDatabase = AlarmDatabase.getDatabase(context);
        AlarmDao alarmDao = alarmDatabase.alarmDao();
        listLiveData = alarmDao.getData();
        listLiveData.observe((LifecycleOwner) context, o->this.notifyDataSetChanged());
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent,false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {
        holder.setData(listLiveData.getValue().get(position));
    }

    @Override
    public int getItemCount() {
        return listLiveData.getValue().size();
    }
}
