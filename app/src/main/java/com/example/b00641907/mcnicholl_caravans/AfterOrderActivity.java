package com.example.b00641907.mcnicholl_caravans;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.b00641907.mcnicholl_caravans.adapter.OrderedItemsListAdapter;
import com.example.b00641907.mcnicholl_caravans.model.GasInfo;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AfterOrderActivity extends BaseActivity implements View.OnClickListener {

    ListView lvGas;
    OrderedItemsListAdapter orderedItemsListAdapter;
    ArrayList<GasInfo> gas_items_cart;
    TextView transaction_id,payment_status,transaction_type,total_amount;
    double amount;
    private static final int REQUEST_CODE = 500;
    private FirebaseDatabase firebaseDatabase;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_order);

        findViewById(R.id.btnBack).setOnClickListener(this);
        transaction_id = findViewById(R.id.transaction_id);
        payment_status = findViewById(R.id.payment_status);
        transaction_type = findViewById(R.id.transaction_type);
        total_amount = findViewById(R.id.total_amount);
        transaction_id.setText(getIntent().getStringExtra("id"));
        payment_status.setText(getIntent().getStringExtra("status"));
        transaction_type.setText(getIntent().getStringExtra("type"));
        total_amount.setText(String.format("%s %s", getIntent().getStringExtra("currencyIsoCode"), getIntent().getStringExtra("amount")));
        gas_items_cart = getIntent().getParcelableArrayListExtra("items");
        lvGas = findViewById(R.id.lvGas);
        orderedItemsListAdapter = new OrderedItemsListAdapter(mContext, gas_items_cart, null);
        lvGas.setAdapter(orderedItemsListAdapter);

    }



    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btnBack) {
            finish();
        }
    }




}
