package com.app.Accelerometer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.app.Accelerometer.database.Database;

import java.util.ArrayList;
import java.util.List;

public class ViewSchedule extends AppCompatActivity {


    private List<Alarm> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ScheduleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        Database.init(getApplicationContext());
        movieList=Database.getAllForViewSchedule(getApplicationContext());
        mAdapter = new ScheduleAdapter(movieList,getApplicationContext());
        recyclerView.setHasFixedSize(true);

        // vertical RecyclerView
        // keep achedule_list_row.xmlxml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // horizontal RecyclerView
        // keep achedule_list_row.xmlxml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }
}

