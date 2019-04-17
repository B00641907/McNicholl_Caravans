package com.example.b00641907.mcnicholl_caravans.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.b00641907.mcnicholl_caravans.constant.FireBaseConstants;
import com.example.b00641907.mcnicholl_caravans.model.Reminder;
import com.example.b00641907.mcnicholl_caravans.reminder.MyNotificationPublisher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class SyncReminderService extends Service {

    private AlarmManager am;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        syncReminders();
        return START_NOT_STICKY;

    }

    private void syncReminders() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot infoSnip : dataSnapshot.getChildren()) {
                    if (infoSnip.exists()) {
                        Reminder reminder = infoSnip.getValue(Reminder.class);
                        String key = infoSnip.getKey();
                        if (key != null && key.contains(firebaseAuth.getCurrentUser().getUid())) {
                            handleDataMessage(reminder);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                stopSelf();
            }
        };
        firebaseDatabase.getReference().child(FireBaseConstants.REMINDERS).addListenerForSingleValueEvent(postListener);

    }


    private void handleDataMessage(Reminder reminder) {

        String event_title = reminder.event_title;
        String event_description = reminder.event_description;
        String event_date_time = reminder.event_date_time;
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy hh:mm a", Locale.US);


        try {

            Calendar current_time = Calendar.getInstance();
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(String.valueOf(event_date_time)));// all done
            cal.add(Calendar.MINUTE, -5);


            if (cal.before(current_time)) {
                return;
            }

            if (am == null) {
                am = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
            }
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


    }
}
