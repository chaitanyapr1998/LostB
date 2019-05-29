package com.example.chaitanya.lostb;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

//In-app purchase page in the app
public class PurchaseActivity extends AppCompatActivity {
    Button pay, cancel;
    FirebaseUser mUser;
    DatabaseReference ref;
    private ArrayList<PurchaseModel> data;
    public static final String CLIENT_ID = "AdtWe17u7C85peQDkuFeFY9kqSf-uVFnzNVxRE4WmNB_YcqcOAVm28yesykOo3XQ_TXWXX7YBMRv8RUK";
    private int REQUEST_CODE = 1234;
    private static PayPalConfiguration con = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(CLIENT_ID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        setTitle("In-App Purchase");

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, con);
        startService(intent);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        data = new ArrayList<>();

        pay = (Button) findViewById(R.id.payBtn);
        cancel = (Button) findViewById(R.id.cancelBtn);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentBtnClicked();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelPayment();
            }
        });

        getPaymentInfo();
    }

    //When user clicks cancel payment, this method will be triggered to reset the payment data of the user
    private void cancelPayment(){
        String date = String.valueOf(System.currentTimeMillis());
        String amount = "0.00";
        boolean ads = false;
        ref = FirebaseDatabase.getInstance().getReference().child("Purchase").child(mUser.getUid());
        PurchaseModel pm = new PurchaseModel(mUser.getUid(), mUser.getEmail(), date, amount, ads);
        ref.setValue(pm);
        getPaymentInfo();
    }

    //To get payment info of the user (To check paid user or not)
    private void getPaymentInfo(){
        ref = FirebaseDatabase.getInstance().getReference().child("Purchase").child(mUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                if(dataSnapshot.exists()){
                    PurchaseModel pm = dataSnapshot.getValue(PurchaseModel.class);
                    data.add(pm);
                }
                checkIfUserPaid();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Checking to display button
    private void checkIfUserPaid(){
        if(data.size() != 0){
            if(data.get(0).isAds()){
                pay.setText("Already paid");
                pay.setEnabled(false);
            } else {
                pay.setText("Pay");
                pay.setEnabled(true);
            }
        }
    }

    //To open pay pal page
    private void paymentBtnClicked(){
        PayPalPayment payment = new PayPalPayment(new BigDecimal("1.00"), "USD", "Remove Ads", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, con);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, REQUEST_CODE);
    }

    //After coming back from the pay pal page
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                PaymentConfirmation c = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(c != null){
                    try {
                        JSONObject j = new JSONObject(c.toJSONObject().toString());
                        String res = j.getJSONObject("response").getString("state");
                        if(res.equals("approved")){
                            savePaymentInfo();
                            Toast.makeText(PurchaseActivity.this, "Success",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(PurchaseActivity.this, "Fail",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    //After successful payment, uploading the data to the database
    private void savePaymentInfo(){
        String date = String.valueOf(System.currentTimeMillis());
        String amount = "1.00";
        boolean ads = true;
        ref = FirebaseDatabase.getInstance().getReference().child("Purchase").child(mUser.getUid());
        PurchaseModel pm = new PurchaseModel(mUser.getUid(), mUser.getEmail(), date, amount, ads);
        ref.setValue(pm);
        getPaymentInfo();
    }

    //To stop pay pal service
    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}
