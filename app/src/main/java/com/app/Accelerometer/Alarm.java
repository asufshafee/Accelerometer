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
package com.app.Accelerometer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


import com.app.Accelerometer.alert.AlarmAlertBroadcastReciever;

import java.io.Serializable;
import java.security.cert.CertPathBuilderSpi;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Alarm implements Serializable {





	private static final long serialVersionUID = 8699489847426803789L;
	private int id;
	private Boolean alarmActive = true;
	private Calendar alarmTime =Calendar.getInstance();
	private Calendar alarmDate = Calendar.getInstance();
	private Calendar alarmEnd = Calendar.getInstance();
	private Calendar alarmDateEnd = Calendar.getInstance();
	private Calendar alarmDateStart = Calendar.getInstance();
	private int Interval=100;
	String StartDateS,EndDateS;

	public String getStartDateS() {
		return StartDateS;
	}

	public void setStartDateS(String startDateS) {
		StartDateS = startDateS;
	}

	public String getEndDateS() {
		return EndDateS;
	}

	public void setEndDateS(String endDateS) {
		EndDateS = endDateS;
	}

	private String filenamme;
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MM:yyyy");


	public String getFilenamme() {
		return filenamme;
	}

	public void setFilenamme(String filenamme) {
		this.filenamme = filenamme;
	}

	public void setAlarmEnd(Calendar alarmEnd) {
		this.alarmEnd = alarmEnd;
	}

	public Alarm() {

	}



	/**
	 * @return the alarmActive
	 */
	public Boolean getAlarmActive() {
		return alarmActive;
	}

	public void setAlarmDate(Calendar alarmDate1) {
		this.alarmDateStart=alarmDate1;
	}
	public void setAlarmEnDate(Calendar alarmDate) {
		this.alarmDateEnd=alarmDate;
	}

	public String getAlarmDate() {

		Date tomorrow = alarmDateStart.getTime();
//		return String.valueOf(alarmDate.get(Calendar.DAY_OF_MONTH))+":"+String.valueOf(alarmDate.get(Calendar.MONTH))+":"+String.valueOf(alarmDate.get(Calendar.YEAR));
		return dateFormat.format(tomorrow);
	}
	public String getAlarmEndDate() {

		Date tomorrow = alarmDateEnd.getTime();
//		return String.valueOf(alarmDate.get(Calendar.DAY_OF_MONTH))+":"+String.valueOf(alarmDate.get(Calendar.MONTH))+":"+String.valueOf(alarmDate.get(Calendar.YEAR));
		return dateFormat.format(tomorrow);
	}
	public String getAlarmScheduleTime() {
		SimpleDateFormat fileTimeFormat = new SimpleDateFormat("HH:mm:ss:SSS");
		return fileTimeFormat.format(alarmTime.getTime());

	}
	public String getAlarmScheduleEndTime() {
		SimpleDateFormat fileTimeFormat = new SimpleDateFormat("HH:mm:ss:SSS");
		return fileTimeFormat.format(alarmDateEnd.getTime());

	}

	/**
	 * @param alarmActive
	 *            the alarmActive to set
	 */
	public void setAlarmActive(Boolean alarmActive) {
		this.alarmActive = alarmActive;
	}

	public String getAlarmEndString() {

		String time = "";
		if (alarmEnd.get(Calendar.HOUR_OF_DAY) <= 9)
			time += "0";
		time += String.valueOf(alarmEnd.get(Calendar.HOUR_OF_DAY));
		time += ":";

		if (alarmEnd.get(Calendar.MINUTE) <= 9)
			time += "0";
		time += String.valueOf(alarmEnd.get(Calendar.MINUTE));

		time += ":";

		if (alarmEnd.get(Calendar.SECOND) <= 9)
			time += "0";
		time += String.valueOf(alarmEnd.get(Calendar.SECOND));

		time += ":";

		if (alarmEnd.get(Calendar.MILLISECOND) <= 9)
			time += "0";
		time += String.valueOf(alarmEnd.get(Calendar.MILLISECOND));



		return time;
	}

	/**
	 * @return the alarmTime
	 */
	public String getAlarmTimeString() {

		String time = "";
		if (alarmTime.get(Calendar.HOUR_OF_DAY) <= 9)
			time += "0";
		time += String.valueOf(alarmTime.get(Calendar.HOUR_OF_DAY));
		time += ":";

		if (alarmTime.get(Calendar.MINUTE) <= 9)
			time += "0";
		time += String.valueOf(alarmTime.get(Calendar.MINUTE));

		time += ":";

		if (alarmTime.get(Calendar.SECOND) <= 9)
			time += "0";
		time += String.valueOf(alarmTime.get(Calendar.SECOND));

		time += ":";

		if (alarmTime.get(Calendar.MILLISECOND) <= 9)
			time += "0";
		time += String.valueOf(alarmTime.get(Calendar.MILLISECOND));



		return time;
	}

	/**
	 * @param alarmTime
	 *            the alarmTime to set
	 */
	public void setAlarmTime(Calendar alarmTime) {
		this.alarmTime = alarmTime;
	}

	/**
	 * @param alarmTime
	 *            the alarmTime to set
	 */
	public void setAlarmTime(String alarmTime) {

		String[] timePieces = alarmTime.split(":");

		Calendar newAlarmTime = Calendar.getInstance();
		newAlarmTime.set(Calendar.HOUR_OF_DAY,
				Integer.parseInt(timePieces[0]));
		newAlarmTime.set(Calendar.MINUTE, Integer.parseInt(timePieces[1]));
		newAlarmTime.set(Calendar.SECOND, Integer.parseInt(timePieces[2]));
		newAlarmTime.set(Calendar.MILLISECOND, Integer.parseInt(timePieces[3]));
		setAlarmTime(newAlarmTime);
	}

	public void setAlarmEnd(String alarmTime) {

		String[] timePieces = alarmTime.split(":");

		Calendar newAlarmTime = Calendar.getInstance();
		newAlarmTime.set(Calendar.HOUR_OF_DAY,
				Integer.parseInt(timePieces[0]));
		newAlarmTime.set(Calendar.MINUTE, Integer.parseInt(timePieces[1]));
		newAlarmTime.set(Calendar.SECOND, Integer.parseInt(timePieces[2]));
		newAlarmTime.set(Calendar.MILLISECOND, Integer.parseInt(timePieces[3]));
		setAlarmEnd(newAlarmTime);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Calendar getAlarmTime() {
//		if (alarmTime.before(Calendar.getInstance()))
//			alarmTime.add(Calendar.DAY_OF_MONTH, 1);
		return alarmTime;
	}
	public Calendar getAlarmEnd() {
		if (alarmEnd.before(getAlarmTime()))
			alarmEnd.add(Calendar.DAY_OF_MONTH, 1);
		return alarmEnd;
	}
	public void schedule(Context context) {

		setAlarmActive(true);


		Intent myIntent = new Intent(context, AlarmAlertBroadcastReciever.class);
		myIntent.putExtra("alarm", this);
		myIntent.putExtra("alarmid", getId());

//		alarmTime.set(alarmDate.get(Calendar.YEAR),alarmDate.get(Calendar.MONTH),alarmDate.get(Calendar.DAY_OF_MONTH));
		final int _id = (int) System.currentTimeMillis();
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, _id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		alarmManager.set(AlarmManager.RTC_WAKEUP, getAlarmTime().getTimeInMillis(),  pendingIntent);

	}

	public void scheduleEnd(Context context) {

		Intent myIntent = new Intent(context, AlarmAlertBroadcastReciever.class);
		myIntent.putExtra("alarm", this);

//		alarmEnd.set(alarmDate.get(Calendar.YEAR),alarmDate.get(Calendar.MONTH),alarmDate.get(Calendar.DAY_OF_MONTH));
		final int _id = (int) System.currentTimeMillis();

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, _id, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		alarmManager.set(AlarmManager.RTC_WAKEUP, getAlarmEnd().getTimeInMillis(), pendingIntent);

	}
	/**
	 * @return the interval
	 */
	public int getInterval() {
		return Interval;
	}

	/**
	 * @param interval
	 *            the alarmTime to set
	 */
	public void setInterval(int interval) {
		Interval = interval;
	}
	public String getTimeUntilNextAlarmMessage( String check){
		long timeDifference ;

		if (check.equals("Sound"))
		{
			 timeDifference = getAlarmTime().getTimeInMillis() - System.currentTimeMillis();

		}else {
			 timeDifference = getAlarmEnd().getTimeInMillis() - System.currentTimeMillis();

		}

		long days = timeDifference / (1000 * 60 * 60 * 24);
		long hours = timeDifference / (1000 * 60 * 60) - (days * 24);
		long minutes = timeDifference / (1000 * 60) - (days * 24 * 60) - (hours * 60);
		long seconds = timeDifference / (1000) - (days * 24 * 60 * 60) - (hours * 60 * 60) - (minutes * 60);
		String alert = "Alarm will "+check+" in";
		if (days > 0) {
			alert += String.format(
					"%d days, %d hours, %d minutes and %d seconds", days,
					hours, minutes, seconds);
		} else {
			if (hours > 0) {
				alert += String.format("%d hours, %d minutes and %d seconds",
						hours, minutes, seconds);
			} else {
				if (minutes > 0) {
					alert += String.format("%d minutes, %d seconds", minutes,
							seconds);
				} else {
					alert += String.format("%d seconds", seconds);
				}
			}
		}
		return alert;
	}
}
