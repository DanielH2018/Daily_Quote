package com.example.dailyquote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {

    private List<String> quoteList = MainActivity.getQuoteList();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm", "Alarm Received!");
        if (("android.intent.action.BOOT_COMPLETED").equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, MyService.class);
            context.startService(serviceIntent);
        } else if(quoteList.size() > 0){
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "${context.packageName}-$name")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Daily Quote:")
                        .setContentText(quoteList.get((int)(quoteList.size() * Math.random())))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(quoteList.get((int)(quoteList.size() * Math.random()))))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(10001, builder.build());
        }
    }
}
