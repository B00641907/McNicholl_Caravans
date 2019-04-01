package com.example.b00641907.mcnicholl_caravans;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends BaseActivity {

    private View backButton;
    private EditText userName, userEmail, userPassword, userPasswordConfirm;
    private Button regButton;
    private TextView userLogIn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setupUIViews();

        firebaseAuth = FirebaseAuth.getInstance();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = userName.getText().toString().trim();
                final String email = userEmail.getText().toString().trim();
                final String password = userPassword.getText().toString().trim();
                final String confirm = userPasswordConfirm.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isEmailValid(email)) {
                    Toast.makeText(SignupActivity.this, "Please enter correct email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(SignupActivity.this, "Password must be 6 characters at least", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirm)) {
                    Toast.makeText(SignupActivity.this, "Password don't match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                showProgressDialog();
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            UserInfo user = new UserInfo();
                            user.uEmail = email;
                            user.uName = name;
                            user.uRole = "none";

                            showProgressDialog();

                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            firebaseDatabase.getReference().child(FireBaseConstants.USERS).child(firebaseAuth.getCurrentUser().getUid()).setValue(user, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    hideProgressDialog();

                                    if (databaseError == null) {
                                        //sendUserData();
                                        firebaseAuth.signOut();

                                        Toast.makeText(SignupActivity.this, "You have Registered Successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        showAlert(databaseError.getMessage());
                                    }
                                }
                            });
                        } else {
                            hideProgressDialog();
                            Toast.makeText(SignupActivity.this, "You Failed to Register", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        userLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setupUIViews() {
        backButton = findViewById(R.id.btnBack);
        userName = (EditText) findViewById(R.id.etUsername);
        userEmail = (EditText) findViewById(R.id.etUserEmail);
        userPassword = (EditText) findViewById(R.id.etUserPassword);
        userPasswordConfirm = (EditText) findViewById(R.id.edtConfirm);
        regButton = (Button) findViewById(R.id.btnRegister);
        userLogIn = (TextView) findViewById(R.id.tvUserLogin);
    }
    //Validation so all information is entered correctly

    private Boolean validate() {
        Boolean result = true;

        String name = userName.getText().toString();
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        String confirm = userPasswordConfirm.getText().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            result = false;
        }

        if (!isEmailValid(email)) {
            Toast.makeText(this, "Please enter correct email", Toast.LENGTH_SHORT).show();
            result = false;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Password don't match!", Toast.LENGTH_SHORT).show();
            result = false;
        }

        return result;
    }
}
