package com.app.Accelerometer;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;

import com.app.Accelerometer.telephony.Files_Adopter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Files_List extends AppCompatActivity {


    public static  List<String> dataList = new ArrayList<>();

    private RecyclerView recyclerView;
    private Files_Adopter mAdapter;
    List<String> files = getList(new File("YOUR ROOT"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files__list);

        getSupportActionBar().setTitle("All Data");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        TelephonyManager mngr = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        String androidId = mngr.getImei();
        dataList= getList(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+androidId));
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        mAdapter = new Files_Adopter(dataList, getApplicationContext(), Files_List.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private List<String> getList(File parentDir) {

        ArrayList<String> inFiles = new ArrayList<String>();
        String[] fileNames = parentDir.list();

        if (fileNames!=null)
        for (String fileName : fileNames) {
            if (fileName.toLowerCase().endsWith(".txt") || fileName.toLowerCase().endsWith(".rtf") || fileName.toLowerCase().endsWith(".txd")) {
                inFiles.add( fileName);
            }
        }

        return inFiles;
    }


}
