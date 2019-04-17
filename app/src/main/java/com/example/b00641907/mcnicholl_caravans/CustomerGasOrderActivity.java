package com.example.b00641907.mcnicholl_caravans;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.example.b00641907.mcnicholl_caravans.adapter.CustomerGasListAdapter;
import com.example.b00641907.mcnicholl_caravans.constant.FireBaseConstants;
import com.example.b00641907.mcnicholl_caravans.model.GasInfo;
import com.example.b00641907.mcnicholl_caravans.model.TransactionInfo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomerGasOrderActivity extends BaseActivity implements View.OnClickListener, CustomerGasListAdapter.QuantitySelectedListener {

    ListView lvGas;
    ArrayList<GasInfo> gasInfoList = new ArrayList<>();
    CustomerGasListAdapter customerGasListAdapter;
    private FirebaseAuth firebaseAuth;
    ArrayList<GasInfo> gas_items_cart;
    TextView submit_order;
    double amount;
    private static final int REQUEST_CODE = 500;
    private FirebaseDatabase firebaseDatabase;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_gas);
        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        findViewById(R.id.btnBack).setOnClickListener(this);
        submit_order = findViewById(R.id.submit_order);
        gas_items_cart = new ArrayList<>();
        lvGas = findViewById(R.id.lvGas);
        customerGasListAdapter = new CustomerGasListAdapter(mContext, gasInfoList,gas_items_cart, this);
        lvGas.setAdapter(customerGasListAdapter);
        submit_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gas_items_cart == null) {
                    Toast.makeText(mContext, "There are no items in your cart.", Toast.LENGTH_SHORT).show();
                    return;
                } else if ((gas_items_cart.size() == 0)) {
                    Toast.makeText(mContext, "There are no items in your cart.", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    showProgressDialog();
                    amount = getTotalAmount();
                    FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
                    Map<String, Object> data = new HashMap<>();
                    mFunctions
                            .getHttpsCallable("addMessage")
                            .call(data)
                            .continueWith(new Continuation<HttpsCallableResult, String>() {
                                @Override
                                public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                                    hideProgressDialog();
                                    HashMap<String, Object> hashMap = (HashMap<String, Object>) task.getResult().getData();
                                    String token = (String) hashMap.get("clientToken");
                                    DropInRequest dropInRequest = new DropInRequest()
                                            .clientToken(token);
                                    startActivityForResult(dropInRequest.getIntent(CustomerGasOrderActivity.this), REQUEST_CODE);

                                    return "";
                                }
                            }).addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Exception e = task.getException();
                                if (e instanceof FirebaseFunctionsException) {
                                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                    FirebaseFunctionsException.Code code = ffe.getCode();
                                    Object details = ffe.getDetails();
                                    System.out.println(String.valueOf(details));
                                }

                            }

                        }
                    });
                }
            }
        });
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

    private double getTotalAmount() {
        amount = 0;
        if (gas_items_cart != null && gas_items_cart.size() > 0) {
            for (int i = 0; i < gas_items_cart.size(); i++) {
                GasInfo gasInfo = gas_items_cart.get(i);
                amount = amount + gasInfo.amountSelected;
            }
        }
        return amount;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btnBack) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                String paymentNonce = result.getPaymentMethodNonce().getNonce();
                sendPaymentNonceToServer(paymentNonce);
                // use the result to update your UI and send the payment method nonce to your server
            } else if (resultCode == RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }
    }

    private void sendPaymentNonceToServer(String paymentNonce) {
        showProgressDialog();
        FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("NONCE", paymentNonce);
        data.put("amount", amount);
        mFunctions
                .getHttpsCallable("checkout")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        hideProgressDialog();
                        JSONObject jsonObject = new JSONObject(String.valueOf(task.getResult().getData()));
                        final JSONObject transaction = jsonObject.getJSONObject("transaction");
                        showProgressDialog();
                        TransactionInfo transactionInfo = new TransactionInfo();
                        transactionInfo.id = transaction.getString("id");
                        transactionInfo.status = transaction.getString("status");
                        transactionInfo.type = transaction.getString("type");
                        transactionInfo.currencyIsoCode = transaction.getString("currencyIsoCode");
                        transactionInfo.amount = transaction.getString("amount");
                        transactionInfo.uEmail = appSettings.getUser().uEmail;
                        transactionInfo.uName = appSettings.getUser().uName;
                        transactionInfo.userID = firebaseAuth.getCurrentUser().getUid();

                        firebaseDatabase.getReference().child(FireBaseConstants.TRANSACTIONS).child(transaction.getString("id") + firebaseAuth.getCurrentUser().getUid()).setValue(transactionInfo, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                hideProgressDialog();

                                if (databaseError == null) {
                                    Intent i=new Intent(CustomerGasOrderActivity.this,AfterOrderActivity.class);
                                    try {
                                        i.putExtra("id",transaction.getString("id"));
                                        i.putExtra("status",transaction.getString("status"));
                                        i.putExtra("type",transaction.getString("type"));
                                        i.putExtra("currencyIsoCode",transaction.getString("currencyIsoCode"));
                                        i.putExtra("amount",transaction.getString("amount"));
                                        i.putExtra("items",gas_items_cart);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(i);
                                    finish();
                                } else {
                                    showAlert(databaseError.getMessage());
                                }
                            }
                        });
                        return "";
                    }
                }).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                hideProgressDialog();
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    if (e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();
                        System.out.println(String.valueOf(details));
                    }

                }

            }
        });


    }



    @Override
    public void onQuantitySelected(int quantity, GasInfo itemInfo, Spinner spinnerGasAmount) {
        if(getTotalQuantity(quantity)>2){
            spinnerGasAmount.setSelection(0);
            Toast.makeText(mContext, "You can't select more than 2 bottles", Toast.LENGTH_SHORT).show();
            return;
        }

        if(quantity==0 && !gas_items_cart.contains(itemInfo)){
            return;
        }
        itemInfo.amountSelected = itemInfo.getPricePerBox() * quantity;
        if (quantity == 0 && gas_items_cart.contains(itemInfo)) {
            gas_items_cart.remove(itemInfo);
        } else {
            itemInfo.amountSelected = itemInfo.getPricePerBox() * quantity;
            gas_items_cart.add(itemInfo);

        }

        if (gas_items_cart.size() > 0) {
            submit_order.setVisibility(View.VISIBLE);
            submit_order.setText(String.format("Submit Order ( %s )", getTotalAmount()));
        } else {
            submit_order.setVisibility(View.GONE);

        }
    }

    private int getTotalQuantity(int quantity) {
        for(int i=0;i<gas_items_cart.size();i++){
            GasInfo gasInfo=gas_items_cart.get(i);
            quantity=quantity+(gasInfo.amountSelected/gasInfo.getPricePerBox() );
        }
        return quantity;
    }


}