package com.example.b00641907.mcnicholl_caravans;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.b00641907.mcnicholl_caravans.adapter.CustomerCaravansListAdapter;
import com.example.b00641907.mcnicholl_caravans.constant.FireBaseConstants;
import com.example.b00641907.mcnicholl_caravans.model.CaravanInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerCaravansActivity extends BaseActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    ListView lvData;
    ArrayList<CaravanInfo> caravansInfoList = new ArrayList<>();
    CustomerCaravansListAdapter customerCaravansListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_caravans);

        firebaseAuth = FirebaseAuth.getInstance();
        findViewById(R.id.btnBack).setOnClickListener(this);

        lvData = findViewById(R.id.lvData);
        customerCaravansListAdapter = new CustomerCaravansListAdapter(mContext, caravansInfoList);
        lvData.setAdapter(customerCaravansListAdapter);
        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent = new Intent(mContext, CustomerCaravanViewActivity.class);
                    intent.putExtra("caravan_info", caravansInfoList.get(position));
                    startActivity(intent);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference()
                .child(FireBaseConstants.DB_CARAVAN);

        showProgressDialog();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressDialog();

                // Read Data
                caravansInfoList.clear();
                for (DataSnapshot caravansInfoSnip : dataSnapshot.getChildren()) {
                    if (caravansInfoSnip.exists()) {

                        CaravanInfo request = caravansInfoSnip.getValue(CaravanInfo.class);
                        String key = caravansInfoSnip.getKey();

                        if (request != null) {
                            request.saveNodeKey(key);
                            caravansInfoList.add(request);
                        }
                    }
                }
                customerCaravansListAdapter.notifyDataSetChanged();
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
