package com.example.b00641907.mcnicholl_caravans;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.b00641907.mcnicholl_caravans.adapter.EmployeeEventListAdapter;
import com.example.b00641907.mcnicholl_caravans.constant.FireBaseConstants;
import com.example.b00641907.mcnicholl_caravans.model.EventInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class EmployeeEventActivity extends BaseActivity implements View.OnClickListener {

    ListView lvData;

    ArrayList<EventInfo> dataList = new ArrayList<>();
    EmployeeEventListAdapter eventListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_event);

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnAdd).setOnClickListener(this);

        lvData = findViewById(R.id.lvData);
        eventListAdapter = new EmployeeEventListAdapter(mContext, dataList);
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

    // Remove Event
    public void removeEvent(int position) {
        final EventInfo eventInfo = dataList.get(position);

        if (eventInfo.getLock() > 0) {
            showAlert(R.string.error_disable_edit);
            return;
        }

        // Remove Data Node
        showProgressDialog();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(FireBaseConstants.DB_EVENT).child(eventInfo.retrieveNodeKey()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                hideProgressDialog();

                if (databaseError == null) {
                    showAlert(R.string.success_remove_caravan);

                    // Remove Storage Files
                    StorageReference fileReference = FirebaseStorage.getInstance().getReferenceFromUrl(eventInfo.getImage());
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
        } else if(viewId == R.id.btnAdd) {
            startActivity(new Intent(mContext, NewEventActivity.class));
        }
    }
}
