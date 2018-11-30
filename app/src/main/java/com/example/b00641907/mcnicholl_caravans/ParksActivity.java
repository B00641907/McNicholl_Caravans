package com.example.b00641907.mcnicholl_caravans;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import static android.os.Build.VERSION_CODES.P;

public class ParksActivity extends AppCompatActivity {}

    /*private static final String TAG = "Parks Activity";
    private static final int ERROR_DIALOG_REQUEST = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parks);


        if (isServicesOK()) {
            init();
        }
    }

    private void init () {
        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ParksActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }
        public boolean isServicesOK () {
            Log.d(TAG, "isServicesOK: checking google services version");

            int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ParksActivity.this);
            if (available == ConnectionResult.SUCCESS) {
                //EVERYTHING IS FINE AND THE USER CAN MAKE MAP REQUESTS
                Log.d(TAG, "isServicesOK: Google Play Services is working");
                return true;
            } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
                //an error has occured but it can be resolved
                Log.d(TAG, "isServicesOk: and error occured but it can be resolved");
                Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(ParksActivity.this, available, ERROR_DIALOG_REQUEST);
                dialog.show();
            } else {
                Toast.makeText(this, "You can't make app requests", Toast.LENGTH_SHORT).show();
            }
            return false;
        }




}*/

