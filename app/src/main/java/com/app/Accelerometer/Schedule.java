package com.app.Accelerometer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.Accelerometer.database.Database;
import com.app.Accelerometer.service.AlarmServiceBroadcastReciever;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Schedule extends AppCompatActivity {

    private EditText hourText, minuteText, secText, millisecText, intervalText;
    private EditText EhourText, EminuteText, EsecText, EmillisecText;

    MaterialCalendarView materialCalendarView;
    LinearLayout setcalanderlayout, tomehide;
    Button  datedone,selectdate,setSchedule,enddate;
    private Alarm alarm;




    private int hour;
    private int minute;
    private int second;
    private int milisec;

    Boolean startdatecheck=false;
    Boolean enddatecheck=false;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MM:yyyy");
    Calendar StartDate,EndDate;
    int day,month,year;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        StartDate=Calendar.getInstance();
        EndDate=Calendar.getInstance();

        intervalText = (EditText) findViewById(R.id.interval);
        hourText = (EditText) findViewById(R.id.hourText);
        minuteText = (EditText) findViewById(R.id.minuteText);
        secText = (EditText) findViewById(R.id.secText);
        millisecText = (EditText) findViewById(R.id.millisecText);

        EhourText = (EditText) findViewById(R.id.hourText1);
        EminuteText = (EditText) findViewById(R.id.minuteText1);
        EsecText = (EditText) findViewById(R.id.secText1);
        EmillisecText = (EditText) findViewById(R.id.millisecText1);

        getSupportActionBar().setTitle("Schedule");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setSchedule=(Button)findViewById(R.id.saveSchedule);
        Database.init(getApplicationContext());

        final Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
        milisec = calendar.get(Calendar.MILLISECOND);

        hourText.setText(String.valueOf(hour));
        minuteText.setText(String.valueOf(minute));
        secText.setText(String.valueOf(second));
        millisecText.setText(String.valueOf(milisec));


        EhourText.setText(String.valueOf(hour));
        EminuteText.setText(String.valueOf(minute));
        EsecText.setText(String.valueOf(second));
        EmillisecText.setText(String.valueOf(milisec));
        intervalText.setText("10");

        setMathAlarm(new Alarm());



        setSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hour=Integer.parseInt(hourText.getText().toString());
                minute=Integer.parseInt(minuteText.getText().toString());
                second=Integer.parseInt(secText.getText().toString());
                milisec=Integer.parseInt(millisecText.getText().toString());

                Calendar newAlarmTime = StartDate;



                newAlarmTime.set(Calendar.HOUR_OF_DAY, hour);
                newAlarmTime.set(Calendar.MINUTE, minute);
                newAlarmTime.set(Calendar.SECOND, second);
                newAlarmTime.set(Calendar.MILLISECOND, milisec);
                alarm.setAlarmTime(newAlarmTime);
                alarm.setAlarmDate(newAlarmTime);



                hour=Integer.parseInt(EhourText.getText().toString());
                minute=Integer.parseInt(EminuteText.getText().toString());
                second=Integer.parseInt(EsecText.getText().toString());
                milisec=Integer.parseInt(EmillisecText.getText().toString());

                Calendar newAlarmEnd = EndDate;

                newAlarmEnd.set(Calendar.HOUR_OF_DAY, hour);
                newAlarmEnd.set(Calendar.MINUTE, minute);
                newAlarmEnd.set(Calendar.SECOND, second);
                newAlarmEnd.set(Calendar.MILLISECOND, milisec);
                alarm.setAlarmEnd(newAlarmEnd);
                alarm.setAlarmEnDate(newAlarmEnd);

                alarm.setAlarmEnDate(EndDate);
                alarm.setAlarmActive(true);
                alarm.setInterval(Integer.parseInt(intervalText.getText().toString()));




                TelephonyManager mngr = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
                String androidId = mngr.getImei();
                Database.init(getApplicationContext());
                if (getMathAlarm().getId() < 1) {
                    Database.create(getMathAlarm(),androidId);
                }
                callMathAlarmScheduleService();
//                finish();

                
            }
        });



        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        materialCalendarView.setSelectedDate(Calendar.getInstance());


        setcalanderlayout = (LinearLayout) findViewById(R.id.setdatelayout);
        datedone = (Button) findViewById(R.id.datedone);
        selectdate=(Button)findViewById(R.id.datebtn);
        selectdate.setText("Start Date: " + dateFormat.format(materialCalendarView.getSelectedDate().getDate()));
        enddate=(Button)findViewById(R.id.endDate);
        enddate.setText("End Date: " + dateFormat.format(materialCalendarView.getSelectedDate().getDate()));



        selectdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setcalanderlayout.setVisibility(View.VISIBLE);
                startdatecheck=true;
            }
        });





        datedone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (startdatecheck)
                {
                    try {


                        setcalanderlayout.setVisibility(View.GONE);
                        Date date=materialCalendarView.getSelectedDate().getDate();
                        String dateend = dateFormat.format(date);
                        String[] devideend = dateend.split(":");
                        StartDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(devideend[0]));
                        StartDate.set(Calendar.MONTH, Integer.parseInt(devideend[1])-1);
                        StartDate.set(Calendar.YEAR, Integer.parseInt(devideend[2]));
                        selectdate.setText("Start Date: " +dateend);
                        startdatecheck=false;

                    } catch (Exception a) {
                        Toast.makeText(getApplicationContext(), "Enter Valid Data", Toast.LENGTH_SHORT).show();
                    }
                }

                if (enddatecheck){
                    try {


                        setcalanderlayout.setVisibility(View.GONE);
                        Date date=materialCalendarView.getSelectedDate().getDate();
                        String dateend = dateFormat.format(date);
                        String[] devideend = dateend.split(":");
                        EndDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(devideend[0]));
                        EndDate.set(Calendar.MONTH, Integer.parseInt(devideend[1])-1);
                        EndDate.set(Calendar.YEAR, Integer.parseInt(devideend[2]));
                        enddate.setText("End Date: " +dateend);
                        enddatecheck=false;

                    } catch (Exception a) {
                        Toast.makeText(getApplicationContext(), "Enter Valid Data", Toast.LENGTH_SHORT).show();
                    }
                }



            }
        });



    }
    public Alarm getMathAlarm() {
        return alarm;
    }
    protected void callMathAlarmScheduleService() {
        finish();
        Intent mathAlarmServiceIntent = new Intent(this, AlarmServiceBroadcastReciever.class);
        sendBroadcast(mathAlarmServiceIntent, null);
    }
    public void setMathAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void SetEndDate(View view) {

        setcalanderlayout.setVisibility(View.VISIBLE);
        enddatecheck=true;

    }
}

