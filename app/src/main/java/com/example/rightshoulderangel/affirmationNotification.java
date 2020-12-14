package com.example.rightshoulderangel;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class affirmationNotification extends Application {

    // so here we are creating the channel in which the message is actually sent
    public static final String CHANNEL = "channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }


    // and here we create the notification channel
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Just Rembember that";
            String description = "You are great because";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel);
        }
    }

}
