package com.example.admin;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageService extends FirebaseMessagingService {

    @SuppressLint("NewApi")
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String title = remoteMessage.getNotification().getTitle();
        String text = remoteMessage.getNotification().getBody();
        String CHANNEL_ID = "MESSAGE";

        CharSequence name;
         NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Message Notification",
                NotificationManager.IMPORTANCE_HIGH);

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Context context;
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title).setContentText(text).setSmallIcon(R.mipmap.ic_launcher).setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(1, notification.build());


        super.onMessageReceived(remoteMessage);
    }
}
