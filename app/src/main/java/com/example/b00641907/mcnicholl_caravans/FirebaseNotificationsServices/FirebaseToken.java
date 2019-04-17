package com.example.b00641907.mcnicholl_caravans.FirebaseNotificationsServices;

import android.content.SharedPreferences;

import com.example.b00641907.mcnicholl_caravans.AppSettings;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;



public class FirebaseToken extends FirebaseInstanceIdService {
    String main_url,user_id;
    SharedPreferences sharedPreferences;
    protected AppSettings appSettings;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

    }



    private void storeRegIdInPref(String token) {
        System.out.println("firebasetoken: "+token);
        appSettings.putString(AppSettings.FIREBASE_TOKEN,token);
    }

}
