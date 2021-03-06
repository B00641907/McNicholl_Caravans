package com.example.b00641907.mcnicholl_caravans;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.b00641907.mcnicholl_caravans.adapter.CustomerEventListAdapter;
import com.example.b00641907.mcnicholl_caravans.constant.FireBaseConstants;
import com.example.b00641907.mcnicholl_caravans.model.EventInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerEventActivity extends BaseActivity implements View.OnClickListener {

    ListView lvData;

    ArrayList<EventInfo> dataList = new ArrayList<>();
    CustomerEventListAdapter eventListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_event);

        findViewById(R.id.btnBack).setOnClickListener(this);

        lvData = findViewById(R.id.lvData);
        eventListAdapter = new CustomerEventListAdapter(mContext, dataList);
        lvData.setAdapter(eventListAdapter);
        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventInfo eventInfo = dataList.get(position);
                Intent intent = new Intent(mContext, EventDetailsActivity.class);
                intent.putExtra("event_info", eventInfo);
                startActivity(intent);
            }
        });

        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference()
                .child(FireBaseConstants.DB_EVENT);

        showProgressDialog();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressDialog();

                // Read Data
                dataList.clear();
                for (DataSnapshot infoSnip : dataSnapshot.getChildren()) {
                    if (infoSnip.exists()) {
                        EventInfo request = infoSnip.getValue(EventInfo.class);
                        String key = infoSnip.getKey();

                        if (request != null) {
                            request.saveNodeKey(key);
                            dataList.add(request);
                        }
                    }
                }
                eventListAdapter.notifyDataSetChanged();
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
