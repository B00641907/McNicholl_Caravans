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

import com.example.b00641907.mcnicholl_caravans.constant.FireBaseConstants;
import com.example.b00641907.mcnicholl_caravans.model.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignInActivity extends BaseActivity {

    //Creating variables for all widgets created on the xml page.

    private TextView SignInInfo;
    private Button SignIn;
    private EditText SignInName;
    private EditText SignInPassword;
    private int counter = 5;
    private TextView Registration;
    private FirebaseAuth firebaseAuth;




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


        FirebaseUser user = firebaseAuth.getCurrentUser();

        //Checking information entered by user to verify with database to grant access or not.
        // Checking User Status
        if (firebaseUser != null) {

            UserInfo userInfo = appSettings.getUser();
            // Check User Role
            if (FireBaseConstants.USER_TYPE_CUSTOMER.equals(userInfo.uRole)) {
                startActivity(new Intent(SignInActivity.this, CustomerHomeActivity.class));
                finish();
            } else if (FireBaseConstants.USER_TYPE_EMPLOYEE.equals(userInfo.uRole)) {
                startActivity(new Intent(SignInActivity.this, EmployeeHomeActivity.class));
                finish();
            } else {
                firebaseAuth.signOut();
            }
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

        showProgressDialog();

        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideProgressDialog();

                if (task.isSuccessful()) {

                    DatabaseReference mUserDetailDabaseReference = FirebaseDatabase.getInstance().getReference()
                            .child(FireBaseConstants.USERS)
                            .child(FirebaseAuth.getInstance().getUid());

                    ValueEventListener mUserDetailsValueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            hideProgressDialog();

                            UserInfo user = null;
                            if (dataSnapshot.exists()) {
                                user = dataSnapshot.getValue(UserInfo.class);

                                // Save Current User Information
                                appSettings.saveUser(user);

                                // Check User Role
                                if (FireBaseConstants.USER_TYPE_CUSTOMER.equals(user.uRole)) {
                                    Toast.makeText(SignInActivity.this, "You have logged in successfully.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignInActivity.this, CustomerHomeActivity.class));
                                    finish();
                                } else if (FireBaseConstants.USER_TYPE_EMPLOYEE.equals(user.uRole)) {
                                    Toast.makeText(SignInActivity.this, "You have logged in successfully.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignInActivity.this, EmployeeHomeActivity.class));
                                    finish();
                                } else {
                                    showAlert(R.string.pending_verification);
                                }
                            } else {
                                showAlert(R.string.msg_encountered_an_unexpected_error);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            hideProgressDialog();
                            showAlert(R.string.msg_encountered_an_unexpected_error);
                        }
                    };

                    showProgressDialog();
                    mUserDetailDabaseReference.addValueEventListener(mUserDetailsValueEventListener);

                } else {
                    Toast.makeText(SignInActivity.this, "You have failed to login.", Toast.LENGTH_LONG).show();
                    //User has 5 attempts to enter the right information. If they run out of attempts
                    // the sign in button will be inactive.
                    counter--;
                    SignInInfo.setText("Attempts Remaining: " + counter);
                    if (counter == 0) {
                        SignIn.setEnabled(false);
                    }
                }
            }
        });
    }
}