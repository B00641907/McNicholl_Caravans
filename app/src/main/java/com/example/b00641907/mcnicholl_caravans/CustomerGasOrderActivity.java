package com.example.b00641907.mcnicholl_caravans;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.b00641907.mcnicholl_caravans.adapter.CustomerGasListAdapter;
import com.example.b00641907.mcnicholl_caravans.constant.FireBaseConstants;
import com.example.b00641907.mcnicholl_caravans.model.GasInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerGasOrderActivity extends BaseActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    ListView lvGas;

    ArrayList<GasInfo> gasInfoList = new ArrayList<>();
    CustomerGasListAdapter customerGasListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_gas);

        firebaseAuth = FirebaseAuth.getInstance();
        findViewById(R.id.btnBack).setOnClickListener(this);

        lvGas = findViewById(R.id.lvGas);
        customerGasListAdapter = new CustomerGasListAdapter(mContext, gasInfoList);
        lvGas.setAdapter(customerGasListAdapter);

        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference()
                .child(FireBaseConstants.DB_GAS);

        showProgressDialog();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressDialog();

                // Read Data
                gasInfoList.clear();
                for (DataSnapshot gasInfoSnip : dataSnapshot.getChildren()) {
                    if (gasInfoSnip.exists()) {
                        GasInfo request = gasInfoSnip.getValue(GasInfo.class);
                        String key = gasInfoSnip.getKey();

                        if (request != null) {
                            request.saveNodeKey(key);
                            gasInfoList.add(request);
                        }
                    }
                }
                customerGasListAdapter.notifyDataSetChanged();
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