package com.example.goshop;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Notification extends Application {
    public static final String NOTIFICATION_ID1 = "notification1";
    public static final String NOTIFICATION_ID2 = "notification2";
    public static final String NOTIFICATION_ID3 = "notification3";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }
    private void createNotificationChannels() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notification1 = new NotificationChannel(
                    NOTIFICATION_ID1,
                    "notification1",
                    NotificationManager.IMPORTANCE_HIGH
            );

            notification1.setDescription("notification1");

            NotificationChannel notification2 = new NotificationChannel(
                    NOTIFICATION_ID2,
                    "notification2",
                    NotificationManager.IMPORTANCE_HIGH
            );

            notification2.setDescription("notification2");

            NotificationChannel notification3 = new NotificationChannel(
                    NOTIFICATION_ID3,
                    "notification3",
                    NotificationManager.IMPORTANCE_HIGH
            );

            notification3.setDescription("notification3");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notification1);
            manager.createNotificationChannel(notification2);
            manager.createNotificationChannel(notification3);
        }
    }
}
