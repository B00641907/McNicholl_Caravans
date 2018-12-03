package com.example.b00641907.mcnicholl_caravans;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    //Creating variables for all widgets created on the xml page.

    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private int counter = 5;
    private TextView userRegistration;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // if(isServicesOK()){
            //init();
       // }

        //Assigning variables an respective id.
        Name = (EditText) findViewById(R.id.etName);
        Password = (EditText) findViewById(R.id.etPassword);
        Info = (TextView) findViewById(R.id.tvInfo);
        Login = (Button) findViewById(R.id.btnSignIn);
        userRegistration = (TextView) findViewById(R.id.tvRegister);

        Info.setText("Attempts remaining: 5");

        firebaseAuth = firebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            finish();
            startActivity(new Intent(MainActivity.this, SecondActivity.class));
        }

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(Name.getText().toString(), Password.getText().toString());
            }
        });

        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));


            }
        });

    }

   // private void init(){

   // }

    //public boolean isServicesOK(){
      //  Log.d(TAG, "isServicesOK: checking google services version");

       // int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        // if(available == ConnectionResult.SUCCESS){
            //EVERYTHING IS FINE AND THE USER CAN MAKE MAP REQUESTS
           // Log.d(TAG, "isServicesOK: Google Play Services is working");
           // return true;
        //}
       // else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error has occured but it can be resolved
           // Log.d(TAG, "isServicesOk: and error occured but it can be resolved");
           // Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
          //  dialog.show();
       // }else{
          //  Toast.makeText(this, "You can't make app requests", Toast.LENGTH_SHORT).show();
       // }
        //return false;
    //}

    // Function for validating if details entered are correct by matching them with the details registered in the database.

    private void validate(String userName, String userPassword) {

        progressDialog.setMessage("Please wait until you are verified");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "You have logged in successfully.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, SecondActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "You have failed to login.", Toast.LENGTH_SHORT).show();
                    counter--;
                    Info.setText("Attempts Remaining" + counter);
                    progressDialog.dismiss();
                    if (counter == 0) {
                        Login.setEnabled(false);
                    }

                }


            }


        });
    }
}