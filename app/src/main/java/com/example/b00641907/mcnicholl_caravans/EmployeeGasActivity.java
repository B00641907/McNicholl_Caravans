package com.example.b00641907.mcnicholl_caravans;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.b00641907.mcnicholl_caravans.adapter.EmployeeGasListAdapter;
import com.example.b00641907.mcnicholl_caravans.constant.FireBaseConstants;
import com.example.b00641907.mcnicholl_caravans.model.GasInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EmployeeGasActivity extends BaseActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    ListView lvGas;

    ArrayList<GasInfo> gasInfoList = new ArrayList<>();
    EmployeeGasListAdapter employeeGasListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_gas);

        firebaseAuth = FirebaseAuth.getInstance();
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnAdd).setOnClickListener(this);

        lvGas = findViewById(R.id.lvGas);
        employeeGasListAdapter = new EmployeeGasListAdapter(mContext, gasInfoList);
        lvGas.setAdapter(employeeGasListAdapter);

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
                employeeGasListAdapter.notifyDataSetChanged();
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
        } else if (viewId == R.id.btnAdd) {
            startActivity(new Intent(mContext, NewGasActivity.class));
        }
    }
}
