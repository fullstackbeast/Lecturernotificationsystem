package com.ultraplex.lecturernotificationsystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String s = intent.getStringExtra("message");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyTimetable")
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("Time for lecturer!")
                .setContentText(s)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200, builder.build());
    }
}
