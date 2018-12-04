package com.example.b00641907.mcnicholl_caravans;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class HomePageActivity extends AppCompatActivity {

    //Creating variables for all widgets created on the xml page along with the database.
    private FirebaseAuth firebaseAuth;
    private Button signout,maps2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        firebaseAuth = FirebaseAuth.getInstance();

        //Assigning variables a respective id.
        signout = (Button)findViewById(R.id.btnSignOut);
        maps2 = (Button)findViewById(R.id.btnMaps);

        //Buttons on homepage. Maps button loads GoogleMaps API. Signout returns to SignIn page.
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Signout();

            }
        });

        maps2.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View view) {
                Intent openMap = new Intent(HomePageActivity.this, MapsActivity.class);
                HomePageActivity.this.startActivity(openMap);
            }
        });


    }
    //Creating the menu at the top of the home page.
    @Override
    public boolean onCreateOptionsMenu(Menu nav) {
        getMenuInflater().inflate(R.menu.menu,nav);
        return true;
    }
    //List and Signout options at the top of the home page. Possibility of being used as navigation.
    private void Signout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(HomePageActivity.this, SignInActivity.class));
    }

    private void Parks(){

        startActivity(new Intent(HomePageActivity.this, ParksActivity.class));
    }





    //Linking the menu options to the required pages.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.SignOutMenu:
                Signout();
                return true;
            case R.id.ParksMenu:
                Parks();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }


}
