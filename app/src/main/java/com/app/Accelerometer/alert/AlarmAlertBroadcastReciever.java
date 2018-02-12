/* Copyright 2014 Sheldon Neilson www.neilson.co.za
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.app.Accelerometer.alert;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.app.Accelerometer.Alarm;
import com.app.Accelerometer.database.Database;
import com.app.Accelerometer.main.TmpData;
import com.app.Accelerometer.services.DataRecorderService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmAlertBroadcastReciever extends BroadcastReceiver {


    Context contextin;


    @Override
    public void onReceive(Context context, Intent intent) {
        contextin = context;


        StaticWakeLock.lockOn(context);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MM:yyyy");


        Database.init(context);


        Bundle bundle = intent.getExtras();
        final Alarm alarm = (Alarm) bundle.getSerializable("alarm");
        if (alarm != null) {

            Calendar calendar = Calendar.getInstance();
            Date tomorrow = calendar.getTime();

            String startdate = dateFormat.format(tomorrow);
            String enddate = dateFormat.format(tomorrow);


            // where we will check stop or start service
            Database.init(context);
            if (alarm.getAlarmActive()) {

                if (alarm.getStartDateS().equals(startdate)) {

                    Intent serviceIntent = new Intent(context, DataRecorderService.class);
                    serviceIntent.putExtra("alarm", alarm.getInterval());
                    serviceIntent.putExtra("filename", alarm.getFilenamme());
                    serviceIntent.putExtra("endate", alarm.getAlarmEndDate());
                    if (!isServiceRunning(DataRecorderService.class))
                        context.startService(serviceIntent);
                    TmpData.recordingOn = true;

                    alarm.setAlarmActive(false);
                    Database.update(alarm);
                    alarm.scheduleEnd(context);
//                Toast.makeText(context, alarm.getTimeUntilNextAlarmMessage(" stop"), Toast.LENGTH_LONG).show();
                } else {

                    alarm.schedule(context);
                }


            } else {
                if (alarm.getEndDateS().equals(enddate)) {
                    Intent serviceIntentStop = new Intent(context, DataRecorderService.class);
                    serviceIntentStop.setAction(Intent.ACTION_MAIN);
                    context.stopService(serviceIntentStop);
                    TmpData.recordingOn = true;
                }

            }

        } else {
        }

    }

    public boolean isServiceRunning(Class serviceClassName) {
        ActivityManager activityManager = (ActivityManager) contextin.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningService : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClassName.getName().equals(runningService.service.getClassName()))
                return true;
        }
        return false;
    }

}
