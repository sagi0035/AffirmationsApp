package com.example.rightshoulderangel;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;


public class affirmationsBroadcast extends BroadcastReceiver {


    NotificationManagerCompat notificationManagerCompat;


    @Override
    public void onReceive(Context context, Intent intent) {

        // so first we get the array list
        ArrayList<String> affirmations = intent.getStringArrayListExtra("The Array List");



        // then we set a random integer on the range of the array list's size so we later get a random list item
        int randint = (int) (Math.random() * (affirmations.size()));
        // then we get the id for the channel
        int id = intent.getIntExtra("id",-1);

        notificationManagerCompat = NotificationManagerCompat.from(context);

        // and here is were we set our message!!
        Notification notification = new NotificationCompat.Builder(context,affirmationNotification.CHANNEL)
                .setSmallIcon(R.drawable.ic_message).
                        setContentTitle("A reminder that")
                .setContentText(affirmations.get(randint))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();


        if (id!=-1) {
            notificationManagerCompat.notify(id,notification);
        } else {
            notificationManagerCompat.notify(1,notification);
        }


    }



}
