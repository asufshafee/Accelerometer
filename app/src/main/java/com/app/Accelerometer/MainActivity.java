package com.app.Accelerometer;


import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;

import com.app.Accelerometer.constants.Constants;
import com.app.Accelerometer.main.TmpData;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static boolean recordSwitchEnabled = true;
    private boolean batterySwitchEnabled = false;
    TextView time;

    Button setschedule,openfilefolder,viewSchedule;
    Runnable run;
    Handler handler;

    // Class corresponding to Recorder service
    public final Class serviceClassName = Constants.RECORDING_CLASS;

    // Received Intent Properties
    public final String serviceIntentId = "android.intent.action.MAIN";
    // name of variables received in intent from service
    public final String sensorDataIntentName = "sensorData";
    public final String gpsDataIntentName = "locationData";

    // To be sent Intent Properties
    // name of variable in intent to be sent to service for state of display Data button
    public final String displaySwitchIntentName = "displaySwitchChecked";
    // intent id to be sent to service
    public final String activityIntentId = "display-switch-state-change";

    // savedInstanceState Bundle variable names etc
    public final String displaySwitchStateBundleName = "displayDataSwitchState";
    public final String TAG = "Home";

    private Toolbar toolbar;
    private WifiManager wifiManager;
    private LocationManager locationManager;
    private BroadcastReceiver dataIntentReceiver;
    private BroadcastReceiver stateIntentReceiver;
    int uploaderAlarmInterval = 60 * 60;//in secs;//
    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // fai! we dont have an accelerometer!
        }




        viewSchedule=(Button)findViewById(R.id.viewschedule);
        viewSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ViewSchedule.class);
                startActivity(intent);
            }
        });
        setschedule=(Button)findViewById(R.id.setschedule);
        setschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Schedule.class);
                startActivity(intent);
            }
        });
        openfilefolder=(Button)findViewById(R.id.openfilefolder);
        openfilefolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Files_List.class);
                startActivity(intent);



            }
        });

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.BODY_SENSORS,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.LOCATION_HARDWARE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE
                },
                100);

        // creating Directory Data_Recorder here
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_PHONE_STATE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            TelephonyManager mngr = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
            String androidId = mngr.getImei();
            File sdDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+androidId);
            if (sdDirectory.exists() && sdDirectory.isDirectory()) {
                //directory is present, no need to do anything here

            } else {

                sdDirectory.mkdirs();
            }
        }

        // for checking whether wifi is on or not.
        wifiManager = (WifiManager) this.getSystemService(this.WIFI_SERVICE);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Intent serviceIntent = new Intent(getBaseContext(), serviceClassName);
        serviceIntent.setAction(Intent.ACTION_MAIN);


        broadcastDisplaySwitchState(true);
        scheduleThread();


    }

    ;

    @Override
    public void onStart() {
        super.onStart();
        registerButtonListeners(this);

    }


    /**
     * on Resume, value of variable displayDataEnabled will be automatically restored
     * and register the broadcast listener
     */
    @Override
    public void onResume() {
        super.onResume();
        updateRecordSwitch();
        registerBroadcastListeners();
        broadcastDisplaySwitchState(true);
        scheduleThread();
    }

    /**
     * Start/ Stop alarm for automatic recording
     */


    @Override
    public void onPause() {
        super.onPause();
        // unregister broadcast receivers
        this.unregisterReceiver(this.dataIntentReceiver);
        this.unregisterReceiver(this.stateIntentReceiver);
        broadcastDisplaySwitchState(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler!=null)
            handler.removeCallbacks(run);
        broadcastDisplaySwitchState(false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(displaySwitchStateBundleName, true);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }



    /**
     * this will update the switch state based on whether the recording service is running or not
     */
    Boolean check=true;
    public void updateRecordSwitch() {
        final TextView status = (TextView) findViewById(R.id.Status);


        if (isServiceRunning(serviceClassName)) {

            recordSwitchEnabled = true;
            status.setText("Recording");
            TmpData.recordingOn = true;

        } else {

            status.setText("Not Recording");
            TmpData.recordingOn = false;
        }
        scheduleThread();
    }

    public void scheduleThread(){
        handler=new Handler();
        run=new Runnable() {

            @Override
            public void run() {

                updateRecordSwitch();
//                Logger.d("","checking");
            }
        };
        handler.postDelayed(run, 1000);
    }

    public void registerButtonListeners(final Context context) {
        // Switches and their Listeners
        final Switch recordSwitch = (Switch) findViewById(R.id.recordingSwitch);
        Switch batterySwitch = (Switch) findViewById(R.id.batterySwitch);

        // Before doing anything check whether the service is running or not
        updateRecordSwitch();
        // check whether the GPS is turned on or not here only,instead of checking in the service.
        // after checking, start/stop service accordingly.
        // Record Data Switch Listener
        recordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                //Start the background Service
                if (isChecked) {
                    // if GPS is enabled, only then start the service, otherwise not
                    if (isGPSEnabled) {
                        // change the alarms accordingly
                        //  bumpButton.setVisibility(View.VISIBLE);
//                        if(TmpData.isStartAlarmRunning()){
//                            Logger.d(TAG+"/recordSwitchListener","cancelling AutoStart alarm");
//                            AlarmManagers.cancelAlarm(context, Constants.AUTO_START_RECORDING_CLASS);
//                            TmpData.setStartAlarmRunning(false);
//                        }
//                        if(!TmpData.isStopAlarmRunning()){
//                            Logger.d(TAG+"/recordSwitchListener","starting AutoStop alarm");
//                            AlarmManagers.startAlarm(context, Constants.AUTO_STOP_RECORDING_CLASS);
//                            TmpData.setStopAlarmRunning(true);
//                        }
                        // if recording is not on, then start the recording, other wise not
//                        if(!TmpData.recordingOn) {
                        Intent serviceIntent = new Intent(getBaseContext(), serviceClassName);
                        serviceIntent.setAction(Intent.ACTION_MAIN);
                        startService(serviceIntent);
                        TmpData.recordingOn = true;
                        // if display data is switched on before record data, then send a broadcast intent to service mentioning
                        // that the display data is enabled so it should start sending data
                        if (true)
                            broadcastDisplaySwitchState(true);
//                        }
                    } else {
                        //show GPS settings
                        showGPSSettingsAlert();
                        // bumpButton.setVisibility(View.INVISIBLE);
                        // set the switched to off state
                        recordSwitch.setChecked(false);
                        MainActivity.recordSwitchEnabled = false;
                    }
                    // after all this, we ask user for WiFi permissions
                    if (!wifiManager.isWifiEnabled()) {
                        //showWiFiSettingsAlert();
                    }
                } else {
//                    if(TmpData.isStopAlarmRunning()){
//                        Logger.d(TAG+"/recordSwitchListener","cancelling AutoStop alarm");
//                        AlarmManagers.cancelAlarm(context, Constants.AUTO_STOP_RECORDING_CLASS);
//                        TmpData.setStopAlarmRunning(false);
//                    }
//                    if(!TmpData.isStartAlarmRunning()){
//                        Logger.d(TAG+"/recordSwitchListener","starting AutoStart alarm");
//                        AlarmManagers.startAlarm(context, Constants.AUTO_START_RECORDING_CLASS);
//                        TmpData.setStartAlarmRunning(true);
//                    }
//                    if(TmpData.recordingOn) {
                    stopService(new Intent(getBaseContext(), serviceClassName));
                    TmpData.recordingOn = false;
//                    }
                }
                MainActivity.recordSwitchEnabled = isChecked;
            }
        });


        /**
         batterySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
        Log.i("Home", "Starting battery saver now");
        RecordingMode.setCurrentMode(Constants.BATTERY_SAVER_RECORDING_MODE);
        }
        else {
        Log.i("Home", "Fast recording now");
        RecordingMode.setCurrentMode(Constants.FAST_RECORDING_MODE);
        }
        }
        });
         */
    }

    /**
     * Display Data Switch listeners
     * The idea behind adding a new Broadcast Listener is to stop sending the unnecessary intents from service
     * when the display data is switch off.
     */


    public void registerBroadcastListeners() {
        // receiver for intent sent by service for displaying data
        dataIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateSensorCard(intent.getStringExtra(sensorDataIntentName));
//

            }
        };
        this.registerReceiver(dataIntentReceiver, new IntentFilter(serviceIntentId));
        // receiver for intents sent by Auto Start and Stop
        stateIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                broadcastDisplaySwitchState(true);

            }
        };
        this.registerReceiver(stateIntentReceiver, new IntentFilter("auto.recording.state"));
    }

    /**
     * update the sensor data onto the sensorDataCard
     * List Format:
     * Accel(3), Gyro(3), Magneto(3), Light(1)
     * All data should be in String format
     *
     * @param sensorData
     */
    public void updateSensorCard(String sensorData) {
        if (sensorData != null || sensorData != "") {
            TextView accelData = (TextView) findViewById(R.id.accelData);
            String[] array = sensorData.split(",");

            try
            {
//                accelData.setText("X: " + array[0] + "\n Y: " + array[1] + "\nX: " + array[2]);
                updateRecordSwitch();

            }catch (Exception a)
            {

            }
            // in place of 0 , there is timestamp now.

        }
    }
    //            TextView time = (TextView) findViewById(R.id.timeData);





    /**
     * the GPS is disabled alert dialog box
     * also, allows to go to settings to change the location settings
     */
    public void showGPSSettingsAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        alertDialog.setTitle("Change Location Settings?");
        alertDialog.setMessage("Location has been disabled. Do you want to go to settings to switch it on?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            /**
             * What happens when user clicks on settings.
             * @param dialog
             * @param which
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Show the location settings to user when settings is pressed
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            //when click button is pressed
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    /**
     * This alert dialog box is shown when WiFi is disabled.
     */
    public void showWiFiSettingsAlert() {
        android.app.AlertDialog.Builder wifiDialog = new android.app.AlertDialog.Builder(this);
        wifiDialog.setTitle("Switch on WiFi?");
        wifiDialog.setMessage("WiFi access is required for better results. Do you want to switch it on? (Optional)");
        wifiDialog.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
            /**
             * What happens when user clicks on settings.
             * @param dialog
             * @param which
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                wifiManager.setWifiEnabled(true);
                dialog.cancel();
            }
        });

        wifiDialog.setNegativeButton("No, Thanks", new DialogInterface.OnClickListener() {
            //when click button is pressed
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        wifiDialog.show();
    }


    /**
     * Checks whether the service associated with the app is running or not.
     * GLOBAL Variable: serviceClassName: class associated with the service running in background
     *
     * @return true if service is running, false otherwise
     */
    public boolean isServiceRunning(Class serviceClassName) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningService : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClassName.getName().equals(runningService.service.getClassName()))
                return true;
        }
        return false;
    }

    /**
     * broadcast the display switch state for to be received by the service
     *
     * @param broadcastInfo a boolean variable for broadcasting the state of DisplayData Switch to service
     */
    public void broadcastDisplaySwitchState(boolean broadcastInfo) {
        Intent intent = new Intent(activityIntentId).putExtra(displaySwitchIntentName, broadcastInfo);
        getApplicationContext().sendBroadcast(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        TextView accelData = (TextView) findViewById(R.id.accelData);
        accelData.setText("X: " + event.values[0] + "\n Y: " + event.values[1] + "\nX: " + event.values[2]);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * This will cancel all the alarms and hence no data will be recorded
     * @param context
     */


    /**
     * update constants based on values stored in shared preferences
     */


    //    @Override
//    public void onSensorChanged(SensorEvent event) {
//
//        time.setText("X: "+event.values[0] + "'\n Y: "  + event.values[1] + "\nX: " + event.values[2]);
//
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//        Toast.makeText(getApplicationContext(),String.valueOf(accuracy),Toast.LENGTH_LONG).show();
//
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == 100){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                TelephonyManager mngr = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
                String androidId = mngr.getImei();
                File sdDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+androidId);
                if (sdDirectory.exists() && sdDirectory.isDirectory()) {
                    //directory is present, no need to do anything here

                } else {

                    sdDirectory.mkdirs();
                }
            }else{
                finish();
                //Displaying another toast if permission is not granted
            }
        }
    }

}
