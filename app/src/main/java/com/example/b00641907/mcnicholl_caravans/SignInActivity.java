package com.example.b00641907.mcnicholl_caravans;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignInActivity extends AppCompatActivity {

    //Creating variables for all widgets created on the xml page.

    private TextView SignInInfo;
    private Button SignIn;
    private EditText SignInName;
    private EditText SignInPassword;
    private int counter = 5;
    private TextView Registration;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog SigninDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);



        //Assigning variables a respective id.
        SignInInfo = (TextView) findViewById(R.id.tvSigninInfo);
        SignIn = (Button) findViewById(R.id.btnSignin);
        SignInName = (EditText) findViewById(R.id.SigninName);
        SignInPassword = (EditText) findViewById(R.id.etSigninPassword);
        Registration = (TextView) findViewById(R.id.tvRegister);

        SignInInfo.setText("Attempts remaining: 5");

        firebaseAuth = firebaseAuth.getInstance();
        SigninDialog = new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        //Checking information entered by user to verify with database to grant access or not.
        if (user != null) {
            finish();
            startActivity(new Intent(SignInActivity.this, HomePageActivity.class));
        }

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(SignInName.getText().toString(), SignInPassword.getText().toString());
            }
        });

        Registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, RegistrationActivity.class));


            }
        });

    }


    private void validate(String userName, String userPassword) {

        SigninDialog.setMessage("Please wait until you are verified");
        SigninDialog.show();

        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    SigninDialog.dismiss();
                    Toast.makeText(SignInActivity.this, "You have logged in successfully.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignInActivity.this, HomePageActivity.class));
                } else {
                    Toast.makeText(SignInActivity.this, "You have failed to login.", Toast.LENGTH_LONG).show();
                    //User has 5 attempts to enter the right information. If they run out of attempts
                    // the sign in button will be inactive.
                    counter--;
                    SignInInfo.setText("Attempts Remaining" + counter);
                    SigninDialog.dismiss();
                    if (counter == 0) {
                        SignIn.setEnabled(false);
                    }

                }


            }


        });
    }
}