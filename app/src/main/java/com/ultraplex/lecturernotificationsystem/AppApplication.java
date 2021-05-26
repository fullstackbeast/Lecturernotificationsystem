package com.ultraplex.lecturernotificationsystem;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class AppApplication extends android.app.Application {

    public static final String CHANNEL_ID = "notifyTimetable";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TimetableReminderChannel";
            String description = "Channel for Timetable Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);

            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
