package com.app.Accelerometer.constants;

import android.hardware.SensorManager;

import com.app.Accelerometer.services.DataRecorderService;


/**
 * Prikshit Kumar
 * <kprikshit22@gmail.com/kprikshit@iitrpr.ac.in>
 * CSE, IIT Ropar
 * Created on: 08-02-2015
 */
public final class Constants {
    //App related information
    public final static String APP_NAME = "Data Recorder";
    public final static String DIRECTORY = "/Data_Recorder";
    public final static String DATA_FILE_NAME = "sensorData.txt";

    // Recording related info
    public final static String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public final static int FAST_RECORDING_MODE = SensorManager.SENSOR_DELAY_NORMAL;
    public final static int BATTERY_SAVER_RECORDING_MODE = SensorManager.SENSOR_DELAY_NORMAL;

    // Various class information
    public final static Class RECORDING_CLASS = DataRecorderService.class;

    // Misc Info
    public static int BATTERY_SAVER_LEVEL = 30;


}
