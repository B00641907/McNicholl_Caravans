package com.example.b00641907.mcnicholl_caravans;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.b00641907.mcnicholl_caravans.adapter.CaravansMeetingsListAdapter;
import com.example.b00641907.mcnicholl_caravans.constant.FireBaseConstants;
import com.example.b00641907.mcnicholl_caravans.model.CaravanMeeting;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MeetingListForCaravans extends BaseActivity implements View.OnClickListener {

    ListView lvData;
    ArrayList<CaravanMeeting> caravansInfoList = new ArrayList<>();
    CaravansMeetingsListAdapter caravansMeetingsListAdapter;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caravans_meeting_list);

        firebaseAuth = FirebaseAuth.getInstance();
        findViewById(R.id.btnBack).setOnClickListener(this);

        lvData = findViewById(R.id.lvData);
        caravansMeetingsListAdapter = new CaravansMeetingsListAdapter(mContext, caravansInfoList);
        lvData.setAdapter(caravansMeetingsListAdapter);


        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference()
                .child(FireBaseConstants.CARAVAN_MEETINGS);

        showProgressDialog();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressDialog();

                // Read Data
                caravansInfoList.clear();
                for (DataSnapshot caravansInfoSnip : dataSnapshot.getChildren()) {
                    if (caravansInfoSnip.exists()) {

                        CaravanMeeting request = caravansInfoSnip.getValue(CaravanMeeting.class);
                        String key = caravansInfoSnip.getKey();

                        if (request != null) {
                            caravansInfoList.add(request);
                        }
                    }
                }
                caravansMeetingsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                hideProgressDialog();
            }
        };

        mPostReference.addValueEventListener(postListener);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btnBack) {
            finish();
        }
    }
}
