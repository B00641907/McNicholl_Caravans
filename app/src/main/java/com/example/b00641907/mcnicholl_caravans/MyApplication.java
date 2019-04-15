package com.example.b00641907.mcnicholl_caravans;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyApplication extends Application implements LocationListener {

    private static MyApplication INSTANCE;
    public static MyApplication getInstance() {
        return INSTANCE;
    }

    private LocationManager locationManager;
    public Location curLocation;
    public static boolean bHasGPS = false;

    private AppSettings appSettings;

    // 1 second
    public static final int GPS_MIN_TIME = 1000;
    // 1 meter
    public static final int GPS_MIN_DISTANCE = 1;

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;
        appSettings = new AppSettings(getApplicationContext());
    }

    public String getAndroidId() {
        TelephonyManager tm =
                (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("ID", "Android ID: " + androidId);
        return androidId;
    }


    @Override
    public void onLocationChanged(Location location) {
        if (!location.hasAccuracy())
            return;

        if (location.getProvider().equals(LocationManager.GPS_PROVIDER))
            bHasGPS = true;

        if (bHasGPS && location.getProvider().equals(LocationManager.NETWORK_PROVIDER))
            return;

        curLocation = location;
        appSettings.setDeviceLat(String.valueOf(curLocation.getLatitude()));
        appSettings.setDeviceLng(String.valueOf(curLocation.getLongitude()));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}