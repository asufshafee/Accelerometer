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
package com.app.Accelerometer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.app.Accelerometer.Alarm;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/* 
 * usage:  
 * DatabaseSetup.init(egActivityOrContext); 
 * DatabaseSetup.createEntry() or DatabaseSetup.getContactNames() or DatabaseSetup.getDb() 
 * DatabaseSetup.deactivate() then job done 
 */

public class Database extends SQLiteOpenHelper {
    static Database instance = null;
    static SQLiteDatabase database = null;

    static final String DATABASE_NAME = "DBOOPOOOLLLL";
    static final int DATABASE_VERSION = 1;

    public static final String ALARM_TABLE = "alarm";
    public static final String COLUMN_ALARM_ID = "_id";
    public static final String COLUMN_ALARM_ACTIVE = "alarm_active";
    public static final String COLUMN_ALARM_TIME = "alarm_time";
    public static final String COLUMN_ALARM_DATE = "alarm_date";
    public static final String COLUMN_ALARM_END = "alarm_end";
    public static final String COLUMN_ALARM_END_DATE = "alarm_end_date";
    public static final String COLUMN_INTERVAL = "interval";
    public static final String FILENAME = "filename";

    public static void init(Context context) {
        if (null == instance) {
            instance = new Database(context);
        }
    }

    public static SQLiteDatabase getDatabase() {
        if (null == database) {
            database = instance.getWritableDatabase();
        }
        return database;
    }

    public static void deactivate() {
        if (null != database && database.isOpen()) {
            database.close();
        }
        database = null;
        instance = null;
    }

    public static long create(Alarm alarm, String filename) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ALARM_ACTIVE, alarm.getAlarmActive());
        cv.put(COLUMN_ALARM_TIME, alarm.getAlarmTimeString());
        cv.put(COLUMN_ALARM_DATE, alarm.getAlarmDate());
        cv.put(COLUMN_ALARM_END, alarm.getAlarmEndString());
        cv.put(COLUMN_INTERVAL, alarm.getInterval());
        cv.put(FILENAME, filename);
        cv.put(COLUMN_ALARM_END_DATE, alarm.getAlarmEndDate());


        return getDatabase().insert(ALARM_TABLE, null, cv);
    }

    public static int deleteEntry(Alarm alarm) {
        return deleteEntry(alarm.getId());
    }

    public static int deleteEntry(int id) {
        return getDatabase().delete(ALARM_TABLE, COLUMN_ALARM_ID + "=" + id, null);
    }

    public static int deleteAll() {
        return getDatabase().delete(ALARM_TABLE, "1", null);
    }

    public static Alarm getAlarm(int id) {
        // TODO Auto-generated method stub
        String[] columns = new String[]{
                COLUMN_ALARM_ID,
                COLUMN_ALARM_ACTIVE,
                COLUMN_ALARM_TIME,
                COLUMN_ALARM_DATE,
                COLUMN_ALARM_END,
                COLUMN_INTERVAL,
                COLUMN_ALARM_END_DATE
        };
        Cursor c = getDatabase().query(ALARM_TABLE, columns, COLUMN_ALARM_ID + "=" + id, null, null, null,
                null);
        Alarm alarm = null;

        if (c.moveToFirst()) {

//            alarm = new Alarm();
//            alarm.setId(c.getInt(0));
//            alarm.setAlarmActive(c.getInt(1) == 1);
//            alarm.setAlarmTime(c.getString(2));
//            String date = c.getString(3);
//            String[] devide = date.split(":");
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(devide[0]));
//            calendar.set(Calendar.MONTH, Integer.parseInt(devide[1]));
//            calendar.set(Calendar.YEAR, Integer.parseInt(devide[2]));
//            alarm.setAlarmDate(calendar);
//            alarm.setAlarmEnd(c.getString(4));
//            alarm.setInterval(c.getInt(5));

        }
        c.close();
        return alarm;
    }

    public static Cursor getCursor() {
        // TODO Auto-generated method stub
        String[] columns = new String[]{
                COLUMN_ALARM_ID,
                COLUMN_ALARM_ACTIVE,
                COLUMN_ALARM_TIME,
                COLUMN_ALARM_DATE,
                COLUMN_ALARM_END,
                COLUMN_INTERVAL,
                FILENAME,
                COLUMN_ALARM_END_DATE

        };
        return getDatabase().query(ALARM_TABLE, columns, null, null, null, null,
                null);
    }

    Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ALARM_TABLE + " ( "
                + COLUMN_ALARM_ID + " INTEGER primary key autoincrement, "
                + COLUMN_ALARM_ACTIVE + " INTEGER NOT NULL, "
                + COLUMN_ALARM_TIME + " TEXT NOT NULL, "
                + COLUMN_ALARM_DATE + " TEXT NOT NULL, "
                + COLUMN_ALARM_END + " TEXT NOT NULL,"
                + COLUMN_INTERVAL + " TEXT NOT NULL,"
                + FILENAME + " TEXT NOT NULL,"
                + COLUMN_ALARM_END_DATE + " TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ALARM_TABLE);
        onCreate(db);
    }

    public static List<Alarm> getAll(Context context) {
        List<Alarm> alarms = new ArrayList<Alarm>();
//		try {
        Cursor cursor = Database.getCursor();

//			Log.d("",a.getMessage());
//			Toast.makeText(context,a.getMessage(),Toast.LENGTH_LONG).show();
//

        if (cursor.moveToFirst()) {

            do {
//							Toast.makeText(context,String.valueOf(cursor.getInt(0)),Toast.LENGTH_LONG).show();

                // COLUMN_ALARM_ID,
                // COLUMN_ALARM_ACTIVE,
                // COLUMN_ALARM_TIME,
                // COLUMN_ALARM_DAYS,
                // COLUMN_ALARM_DIFFICULTY,
                // COLUMN_ALARM_TONE,
                // COLUMN_ALARM_VIBRATE,
                // COLUMN_ALARM_NAME


                Alarm alarm = new Alarm();
                alarm.setId(cursor.getInt(0));

                String date = cursor.getString(3);
                alarm.setStartDateS(date);
                String[] devide = date.split(":");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(devide[0]));
                calendar.set(Calendar.MONTH, Integer.parseInt(devide[1])-1);
                calendar.set(Calendar.YEAR, Integer.parseInt(devide[2]));
                alarm.setAlarmDate(calendar);
                alarm.setAlarmActive(cursor.getInt(1) == 1);
                alarm.setAlarmTime(cursor.getString(2));


                String dateend = cursor.getString(7);
                alarm.setEndDateS(dateend);
                String[] devideend = dateend.split(":");
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(devideend[0]));
                calendar.set(Calendar.MONTH, Integer.parseInt(devideend[1])-1);
                calendar.set(Calendar.YEAR, Integer.parseInt(devideend[2]));
                alarm.setFilenamme(cursor.getString(6));
                alarm.setAlarmEnDate(calendar);

                alarm.setAlarmEnd(cursor.getString(4));
                alarm.setInterval(cursor.getInt(5));
                alarm.setFilenamme(cursor.getString(6));



                alarms.add(alarm);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return alarms;
    }
    public static List<Alarm> getAllForViewSchedule(Context context) {
        List<Alarm> alarms = new ArrayList<Alarm>();
//		try {
        Cursor cursor = Database.getCursor();

//			Log.d("",a.getMessage());
//			Toast.makeText(context,a.getMessage(),Toast.LENGTH_LONG).show();
//

        if (cursor.moveToFirst()) {

            do {
//							Toast.makeText(context,String.valueOf(cursor.getInt(0)),Toast.LENGTH_LONG).show();

                // COLUMN_ALARM_ID,
                // COLUMN_ALARM_ACTIVE,
                // COLUMN_ALARM_TIME,
                // COLUMN_ALARM_DAYS,
                // COLUMN_ALARM_DIFFICULTY,
                // COLUMN_ALARM_TONE,
                // COLUMN_ALARM_VIBRATE,
                // COLUMN_ALARM_NAME


                Alarm alarm = new Alarm();
                alarm.setId(cursor.getInt(0));

                String date = cursor.getString(3);
                alarm.setStartDateS(date);
                String[] devide = date.split(":");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(devide[0]));
                calendar.set(Calendar.MONTH, Integer.parseInt(devide[1])-1);
                calendar.set(Calendar.YEAR, Integer.parseInt(devide[2]));
                alarm.setAlarmDate(calendar);
                alarm.setAlarmActive(cursor.getInt(1) == 1);
                alarm.setAlarmTime(cursor.getString(2));


                String dateend = cursor.getString(7);
                alarm.setEndDateS(dateend);
                String[] devideend = dateend.split(":");
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(devideend[0]));
                calendar.set(Calendar.MONTH, Integer.parseInt(devideend[1])-1);
                calendar.set(Calendar.YEAR, Integer.parseInt(devideend[2]));
                alarm.setFilenamme(cursor.getString(6));
                alarm.setAlarmEnDate(calendar);

                alarm.setAlarmEnd(cursor.getString(4));
                alarm.setInterval(cursor.getInt(5));
                alarm.setFilenamme(cursor.getString(6));

                if (alarm.getAlarmActive())
                alarms.add(alarm);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return alarms;
    }

    public static int update(Alarm alarm) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ALARM_ACTIVE, alarm.getAlarmActive());
        return getDatabase().update(ALARM_TABLE, cv, "_id=" + alarm.getId(), null);
    }
}