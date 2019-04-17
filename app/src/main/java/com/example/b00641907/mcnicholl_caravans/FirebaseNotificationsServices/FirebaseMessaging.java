package com.example.b00641907.mcnicholl_caravans.FirebaseNotificationsServices;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.b00641907.mcnicholl_caravans.AppSettings;
import com.example.b00641907.mcnicholl_caravans.R;
import com.example.b00641907.mcnicholl_caravans.reminder.MyNotificationPublisher;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class FirebaseMessaging extends FirebaseMessagingService {

    private static final String TAG = FirebaseMessaging.class.getSimpleName();

    NotificationManager notificationManager;
    private AppSettings appSettings;
    private AlarmManager am;

    private void storeRegIdInPref(String token) {
        appSettings = new AppSettings(getApplicationContext());
        System.out.println("firebasetoken: " + token);
        appSettings.putString(AppSettings.FIREBASE_TOKEN, token);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        storeRegIdInPref(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }


    private void handleDataMessage(JSONObject json) {
        try {
            String event_title = json.getString("title");
            String event_description = json.getString("body");
            String event_date_time = json.getString("datetime");
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy hh:mm a", Locale.US);
//            notifyInstant(event_title);
            if (am == null) {
                am = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
            }
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(String.valueOf(event_date_time)));// all done
                cal.add(Calendar.MINUTE, -5);
//            Date date = dateFormat.parse("");
                ComponentName receiver = new ComponentName(getApplicationContext(), MyNotificationPublisher.class);

                PackageManager pm = getApplicationContext().getPackageManager();

                pm.setComponentEnabledSetting(receiver,

                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,

                        PackageManager.DONT_KILL_APP);


                Intent intent1 = new Intent(getApplicationContext(), MyNotificationPublisher.class);

                intent1.putExtra("event_name", String.valueOf(event_title));
                intent1.putExtra("event_date_time", cal.getTime().getTime());
                intent1.putExtra("event_description", String.valueOf(event_description));
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),

                        (int) System.currentTimeMillis(), intent1,

                        PendingIntent.FLAG_UPDATE_CURRENT);

                System.out.println("reminder for : " + dateFormat.format(cal.getTime()));
                AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);

                if (am != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTime().getTime(), pendingIntent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        am.setExact(AlarmManager.RTC_WAKEUP, cal.getTime().getTime(), pendingIntent);

                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void notifyInstant(String event_title) {

        String channelId = "default_channel_id";
        String channelDescription = "Default Channel";
        NotificationManager notificationManager = (NotificationManager)

                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification;
        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH; //Set the importance level
                notificationChannel = new NotificationChannel(channelId, channelDescription, importance);
                notificationChannel.setLightColor(Color.GREEN); //Set if it is necesssary
                notificationChannel.enableVibration(true); //Set if it is necesssary
                notificationManager.createNotificationChannel(notificationChannel);
            }
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());

        }
        notification = builder.setContentTitle(event_title)

                .setContentText(event_title).setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)

                .setPriority(Notification.PRIORITY_HIGH)
                .build();


        notificationManager.notify(12222, notification);
    }


}