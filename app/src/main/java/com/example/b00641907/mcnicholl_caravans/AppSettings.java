package com.example.b00641907.mcnicholl_caravans;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.b00641907.mcnicholl_caravans.model.UserInfo;

public class AppSettings {
    private static final String APP_SHARED_PREFS = "k_prefs";
    public  static final String FIREBASE_TOKEN = "firebase_token";
    private SharedPreferences appSharedPrefs;
    private Editor prefsEditor;
    private String firebaseToken;
    private String LAT = "LAT";
    private String LNG = "LNG";



    public AppSettings(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }
    public void putString(String key, String value) {
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public void clear(){
        prefsEditor.clear();
        prefsEditor.commit();
    }

    private String USER_NAME = "USER_NAME";
    private String USER_EMAIL = "USER_EMAIL";
    private String USER_ROLE = "USER_ROLE";

    public void saveUser(UserInfo userInfo) {
        if (userInfo != null) {
            prefsEditor.putString(USER_NAME, userInfo.uName);
            prefsEditor.putString(USER_EMAIL, userInfo.uEmail);
            prefsEditor.putString(USER_ROLE, userInfo.uRole);

            prefsEditor.commit();
        } else {
            prefsEditor.putString(USER_NAME, "");
            prefsEditor.putString(USER_EMAIL, "");
            prefsEditor.putString(USER_ROLE, "");

            prefsEditor.commit();
        }
    }

    public UserInfo getUser() {
        UserInfo user = new UserInfo();
        user.uName = appSharedPrefs.getString(USER_NAME,"");
        user.uEmail = appSharedPrefs.getString(USER_EMAIL,"");
        user.uRole = appSharedPrefs.getString(USER_ROLE,"");
        return user;
    }
    public String getFirebaseToken() {
        return appSharedPrefs.getString(FIREBASE_TOKEN,"");
    }

    public String getDeviceLat() {
        return appSharedPrefs.getString(LAT, "0.0");
    }

    public void setDeviceLat(String deviceLat) {
        prefsEditor.putString(LAT, deviceLat);
        prefsEditor.apply();
    }

    public String getDeviceLng() {
        return appSharedPrefs.getString(LNG, "0.0");
    }

    public void setDeviceLng(String deviceLng) {
        prefsEditor.putString(LNG, deviceLng);
        prefsEditor.apply();
    }
}


