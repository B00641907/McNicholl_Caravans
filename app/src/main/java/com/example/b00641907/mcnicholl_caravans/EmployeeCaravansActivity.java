package com.example.b00641907.mcnicholl_caravans;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.b00641907.mcnicholl_caravans.constant.FireBaseConstants;
import com.example.b00641907.mcnicholl_caravans.model.CaravanInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class EmployeeCaravansActivity extends BaseActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    ListView lvData;
    ArrayList<CaravanInfo> caravansInfoList = new ArrayList<>();
    EmployeeCaravansListAdapter employeeCaravansListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_caravans);

        firebaseAuth = FirebaseAuth.getInstance();
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnAdd).setOnClickListener(this);

        lvData = findViewById(R.id.lvData);
        employeeCaravansListAdapter = new EmployeeCaravansListAdapter(mContext, caravansInfoList);
        lvData.setAdapter(employeeCaravansListAdapter);
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
                employeeCaravansListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                hideProgressDialog();
            }
        };

        mPostReference.addValueEventListener(postListener);
    }

    public void removeCaravan(int position) {
        final CaravanInfo caravanInfo = caravansInfoList.get(position);

        if (caravanInfo.getLock() > 0) {
            showAlert(R.string.error_disable_edit);
            return;
        }

        // Remove Data Node
        showProgressDialog();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(FireBaseConstants.DB_CARAVAN).child(caravanInfo.retrieveNodeKey()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                hideProgressDialog();

                if (databaseError == null) {
                    showAlert(R.string.success_remove_caravan);

                    // Remove Storage Files
                    List<String> imageList = caravanInfo.getImages();
                    for (String imagePath : imageList) {
                        StorageReference fileReference = FirebaseStorage.getInstance().getReferenceFromUrl(imagePath);
                        if (fileReference != null) {
                            fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                        }
                    }
                } else {
                    showToastMessage(databaseError.getMessage());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btnBack) {
            finish();
        } else if (viewId == R.id.btnAdd) {
            startActivity(new Intent(mContext, NewCaravanActivity.class));
        }
    }
}
