package com.example.startopenapp.admin.push_noti.setup;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.startopenapp.R;
import com.example.startopenapp.account.login.LoginActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessageService extends FirebaseMessagingService {
    private NotificationManager notificationManager;
    private static int notificationId = 0;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 300, 300, 300};
        vibrator.vibrate(pattern, -1);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID");

        Intent resultIntent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, resultIntent, PendingIntent.FLAG_IMMUTABLE);

        builder.setContentTitle(message.getNotification().getTitle());
        builder.setContentText(message.getNotification().getBody());
        builder.setContentIntent(pendingIntent);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message.getNotification().getBody()));
        builder.setAutoCancel(true);
        builder.setVibrate(pattern);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setPriority(Notification.PRIORITY_MAX);

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelID = "Notification";
            NotificationChannel channel = new NotificationChannel(
                    channelID, "Name", NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(pattern);
            channel.canBypassDnd();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                channel.canBubble();
            }

            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelID);
        }

        notificationManager.notify(notificationId, builder.build());
        notificationId++;
    }
}
