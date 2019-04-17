package com.example.b00641907.mcnicholl_caravans.reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.b00641907.mcnicholl_caravans.R;




public class MyNotificationPublisher extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        showNotification(context, intent.getStringExtra("event_name"), intent.getLongExtra("event_date_time", 0), intent.getStringExtra("event_description"));
    }

    public static void showNotification(Context context, String content, long date_time, String event_description)

    {


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        String channelId = "default_channel_id";
        String channelDescription = "Default Channel";
        NotificationManager notificationManager = (NotificationManager)

                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification;
        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, channelId);
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH; //Set the importance level
                notificationChannel = new NotificationChannel(channelId, channelDescription, importance);
                notificationChannel.setLightColor(Color.GREEN); //Set if it is necesssary
                notificationChannel.enableVibration(true); //Set if it is necesssary
                notificationManager.createNotificationChannel(notificationChannel);
            }
        } else {
            builder = new NotificationCompat.Builder(context);

        }
        notification = builder.setContentTitle(content)

                .setContentText(event_description).setAutoCancel(true)

//                .setSound(alarmSound)
                .setSmallIcon(R.mipmap.ic_launcher)


                .setAutoCancel(true)

                .setPriority(Notification.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(event_description))
                .build();




        notificationManager.notify((int) date_time, notification);

    }
}