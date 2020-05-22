package com.example.dailyquote;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class MyService extends IntentService {


    public MyService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        //Get current time in a calendar
        Calendar calendar = Calendar.getInstance();
        Calendar calendarNow = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendarNow.setTimeInMillis(System.currentTimeMillis());
        //Set Alarm time to 8:00 a.m
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);

        //If time has already passed, add a day so the alarm doesn't fire immediately
        if(calendar.before(calendarNow)){
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}

