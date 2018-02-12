package com.app.Accelerometer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.Accelerometer.database.Database;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MyViewHolder> {

    private List<Alarm> AlarmsList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date, time, end, EndDate;
        public Button cancel;

        public MyViewHolder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.date);
            time = (TextView) view.findViewById(R.id.timestart);
            end = (TextView) view.findViewById(R.id.timeend);
            EndDate = (TextView) view.findViewById(R.id.EndDate);


            cancel = (Button) view.findViewById(R.id.btncancel);
        }
    }


    public ScheduleAdapter(List<Alarm> AlarmList, Context context) {
        this.AlarmsList = AlarmList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.achedule_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Alarm mAlarm = AlarmsList.get(position);
        if (mAlarm.getAlarmActive())

            holder.date.setText("Start Date: " + mAlarm.getStartDateS());
        holder.EndDate.setText("End Date: " + mAlarm.getEndDateS());
        holder.time.setText("Start Time: " + mAlarm.getAlarmScheduleTime());
        holder.end.setText("End Time: " + mAlarm.getAlarmScheduleEndTime());
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlarm.setAlarmActive(false);
                Database.init(mContext);
                Database.update(mAlarm);
                AlarmsList.remove(position);
                notifyDataSetChanged();
            }
        });
    }




    @Override
    public int getItemCount() {
        return AlarmsList.size();
    }
}
