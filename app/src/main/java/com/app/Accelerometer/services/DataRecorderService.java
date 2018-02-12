package com.app.Accelerometer.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.app.Accelerometer.Record;
import com.app.Accelerometer.constants.Constants;
import com.app.Accelerometer.main.RecordingMode;
import com.app.Accelerometer.service.GPSTracker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DataRecorderService extends Service {
    // Intent related to display switch in Mainactivity
    private final String activityIntentId = "display-switch-state-change";
    private final String displayDataSwitchIntentName = "displaySwitchChecked";

    // intent related to sending sensor data to Main Activity
    private final String serviceIntentId = "android.intent.action.MAIN";
    private final String sensorDataIntentName = "sensorData";
    private final String gpsDataIntentName = "locationData";

    private long lastTime = 0;
    // Format of TimeStamp to be used in front of each reading
    SimpleDateFormat timeStampFormat = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
    // info regarding to file used for storing the data
    private File sdDirectory ;

    private File dataFile ;
    private FileOutputStream dataOutputStream;
    // WRITE LAG DISABLED the lag file Name
    // private File logFile = new File(sdDirectory, lagFileName);
    // private FileOutputStream logOutputStream;

    // Custom Defined Primary Sensors
    // Accelerometer is not used because we will be using this sensor in this java file only
//    private CustomGyroScope gyroScope;
//    private CustomLightSensor lightSensor;
//    private CustomMagnetometer magnetometer;
//    private CustomGPS gpsSensor;
    private Sensor accelSensor;
    private SensorManager sensorManager;
    GPSTracker gpsTracker;
//    private CustomGravity gravitySensor;
//    private CustomWifi wifiReader;
    // CELLULAR DISABLED cellular data has also been disabled for this version.
    // private CustomCellular cellularReader;

    // for various sensor data and gps data
    // using a global variable to reduce unnecessary allocation
    private StringBuilder allData = new StringBuilder();
    // display data Switch enabled or not in activity
    private boolean displayDataSwitchEnabled = false;
    private SensorEventListener mSensorListener;
    // broadcast receiver corresponding to display switch in the activity
    private BroadcastReceiver displaySwitchReceiver;
    // filter for intents sent by the display data switch in the activity
    IntentFilter intentFilter = new IntentFilter(activityIntentId);
    // tag for debug messages
    private final String TAG = "DataRecorderService";
    // String isBump = "false";

    int interval;
    Runnable run;
    Handler handler;
    Runnable runForFileData;
    Handler handlerForFileData;
    Context mContext;
    @Override
    public void onCreate() {


//        gpsSensor = new CustomGPS(this);


        final Context context = getBaseContext();


        mContext=context;
        scheduleThread(context);


    }
    Record record=new Record();
    public void scheduleThread(final Context context){
        handler=new Handler();
        run=new Runnable() {

            @Override
            public void run() {
                sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

                mSensorListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        getData(context, event);

                    }
                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    }
                };

                // registering sensorEvent Listener for accelerometer
                sensorManager.registerListener(mSensorListener, accelSensor, RecordingMode.getCurrentMode() , handler);
                // registering broadcast Listener for display Data Switch
                registerDisplayDataSwitchStateListener();
                Log.d("","We have to stop this");



            }
        };
        handler.postDelayed(run, interval);
        writedataintoFile();
    }

    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }

    String filename,directoty;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat fileTimeFormat = new SimpleDateFormat("E MMM dd HH:mm:ss.SSS zzz yyyy", Locale.US);
            sdDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+intent.getStringExtra("filename"));
            interval = intent.getIntExtra("alarm",0);
            directoty="/"+intent.getStringExtra("filename");
            filename = fileTimeFormat.format(date)+"   IMEI: "+intent.getStringExtra("filename");
            dataFile = new File(sdDirectory,filename+".txt");


            SharedPreferences.Editor editor = getSharedPreferences("FileData", MODE_PRIVATE).edit();
            editor.putString("filename", filename);
            editor.putInt("interval", interval);
            editor.putString("dir", directoty);

            editor.apply();
            try {
                dataOutputStream = new FileOutputStream(dataFile, true);
                // WRITE LAG DISABLED: lag information has been disabled currently.

            } catch (IOException e) {
                e.printStackTrace();
            }
            gpsTracker=new GPSTracker(this);


            try {
                String header = "# Started @" + fileTimeFormat.format(date) + "\r\nLat:"+gpsTracker.getLatitude()+", Long:"+gpsTracker.getLongitude()+  "\r\n";
                dataOutputStream.write(header.getBytes());
                gpsTracker.stopUsingGPS();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


        return START_STICKY;
    }

    /**
     * this will register a broadcast listener for receiving the stat of displaySwitch in Home
     * and accordingly we will send intents from here to Home containing sensorInformation
     */
    public void registerDisplayDataSwitchStateListener(){
    // broadcast receiver for displayData Switch Information
        displaySwitchReceiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                displayDataSwitchEnabled = intent.getBooleanExtra(displayDataSwitchIntentName,false);
            }
        };
        getBaseContext().registerReceiver(displaySwitchReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {


        Date date = new Date(System.currentTimeMillis());
        if (handler!=null)
            handler.removeCallbacks(run);
        if (handlerForFileData!=null)
            handlerForFileData.removeCallbacks(runForFileData);
        SimpleDateFormat fileTimeFormat = new SimpleDateFormat("E MMM dd HH:mm:ss.SSS zzz yyyy", Locale.US);

        try {
            String header = "# End @" + fileTimeFormat.format(date) + "\r\n";
            dataOutputStream.write(header.getBytes());
        } catch (Exception e) {
            try {
                SharedPreferences prefs = getSharedPreferences("FileData", MODE_PRIVATE);
                String restoredText = prefs.getString("filename", null);
                String dir = prefs.getString("dir", null);
                String header = "# End @" + fileTimeFormat.format(date) + "\r\n";
                filename = restoredText;
                sdDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + dir);
                dataFile = new File(sdDirectory,filename+".txt" );
                dataOutputStream = new FileOutputStream(dataFile, true);
                try {
                    dataOutputStream.write(header.getBytes());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }

        }
//        gpsSensor.unregisterListener();
        if (mSensorListener!=null)
        sensorManager.unregisterListener(mSensorListener);
        try
        {
            getBaseContext().unregisterReceiver(displaySwitchReceiver);
        }catch (Exception a)
        {

        }

        uploadFile();


        super.onDestroy();

    }

    /**
     * this will store sensor data into a file whenever there is a sensorEvent related to Accelerometer
     * @param context
     * @param event
     */
    public void getData(Context context, SensorEvent event){


        appendNormalizedAcceleration(event.values);


        // if display switch is enabled in the activity, only then send the intent which is to be sent to activity
        if (displayDataSwitchEnabled) {
//            String locationData = gpsSensor.getLastLocationInfo();
            // Sending this information back to activity for displaying on view
            Intent intent = new Intent(serviceIntentId);
            intent.putExtra(gpsDataIntentName, "locationData");
            intent.putExtra(sensorDataIntentName, allData.toString());
            Log.d("",allData.toString());
            context.sendBroadcast(intent);
        } else {
        }

        allData.append("\n");
//        try {
//
//            Gson gson=new Gson();
//
//            long tsLong = System.currentTimeMillis()/1000-10;
//            if (tsLong > lastTime) {
//
//                record.setChangeTime(String.valueOf(tsLong-lastTime));
//                lastTime = tsLong;
//                String data=gson.toJson(record);
//                if (data.getBytes()!=null)
//                dataOutputStream.write(data.getBytes());
//            }
//
//            allData.setLength(0);
//        } catch (Exception e) {
//
//
//        }
    }
    public  void writedataintoFile()
    {

        try {

            Gson gson=new Gson();
            SharedPreferences prefs = getSharedPreferences("FileData", MODE_PRIVATE);
            int intervalreal = prefs.getInt("interval", 10);
                record.setChangeTime(String.valueOf(intervalreal));
                String data=gson.toJson(record);
                if (data!=null&& record.getX()!=null )
                    dataOutputStream.write(data.getBytes());

            allData.setLength(0);
        } catch (Exception e) {

            SharedPreferences prefs = getSharedPreferences("FileData", MODE_PRIVATE);
            String restoredText = prefs.getString("filename", null);
            String dir = prefs.getString("dir", null);

            filename = restoredText;
            sdDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + dir);
            dataFile = new File(sdDirectory,filename+".txt" );
            try {
                dataOutputStream = new FileOutputStream(dataFile, true);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            try {
                Gson gson=new Gson();
                SharedPreferences prefs1 = getSharedPreferences("FileData", MODE_PRIVATE);
                int intervalreal = prefs1.getInt("interval", 10);
                record.setChangeTime(String.valueOf(intervalreal));
                String data=gson.toJson(record);
                if (data!=null&& record.getX()!=null)
                    dataOutputStream.write(data.getBytes());
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
        scheduleThreadForFile();
    }

    /**
     * this will normalize acceleration values based on orientation of phone given by GyroScope
     * @param accelerometerValues
     */
    public void appendNormalizedAcceleration(float[] accelerometerValues){


         SimpleDateFormat timeStampFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);

            allData.append(String.format("%.5f", accelerometerValues[0]));
        record.setX(String.valueOf(accelerometerValues[0]));
            allData.append(",");
            allData.append(String.format("%.5f", accelerometerValues[1]));
        record.setY(String.valueOf(accelerometerValues[1]));
            allData.append(",");
            allData.append(String.format("%.5f", accelerometerValues[2]));
        record.setZ(String.valueOf(accelerometerValues[2]));
        record.setTime(timeStampFormat.format(new Date(System.currentTimeMillis())));




    }




    private void uploadFile() {

        SharedPreferences prefs = getSharedPreferences("FileData", MODE_PRIVATE);
        String restoredText = prefs.getString("filename", null);
        String dir = prefs.getString("dir", null);
        //File yourFile = new File(dir, "path/to/the/file/inside/the/sdcard.ext");
        Uri file;
        File sdDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + dir);
        //Get the text file
        File file1 = new File(sdDirectory,restoredText+".txt");
        file=Uri.fromFile(file1);
        if(file!=null)
        {
            FirebaseApp.initializeApp(this);
            FirebaseStorage storage=FirebaseStorage.getInstance();
            StorageReference reference=storage.getReferenceFromUrl("gs://accproject-9954f.appspot.com/");
            StorageReference imagesRef=reference.child("Files/"+restoredText+".txt");
            UploadTask uploadTask = imagesRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                }
            });
        }else {
        }
    }



    public void scheduleThreadForFile(){
        handlerForFileData=new Handler();
        runForFileData=new Runnable() {

            @Override
            public void run() {
                writedataintoFile();
                Log.d("",String.valueOf(interval));
            }
        };
        SharedPreferences prefs = getSharedPreferences("FileData", MODE_PRIVATE);
        int intervalreal = prefs.getInt("interval", 10);
        handlerForFileData.postDelayed(runForFileData, intervalreal);
    }


}
