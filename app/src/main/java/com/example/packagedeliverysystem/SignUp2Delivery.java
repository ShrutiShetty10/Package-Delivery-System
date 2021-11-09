package com.example.packagedeliverysystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SignUp2Delivery extends AppCompatActivity {

    String fullName, email, userName, password, addressS, aadhaarNumberS;

    EditText address, aadhaarNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2_delivery);

        Intent intent = getIntent();

        fullName = intent.getStringExtra("fullName");
        email = intent.getStringExtra("email");
        userName = intent.getStringExtra("userName");
        password = intent.getStringExtra("password");

        address = findViewById(R.id.signup_address);
        aadhaarNumber = findViewById(R.id.signup_aadhaarnumber);

    }

    public void callSignUp3(View view) {

        addressS = address.getText().toString();
        aadhaarNumberS = aadhaarNumber.getText().toString();

        if (!validateAddress(addressS) | !validateAadhaarNumber(aadhaarNumberS))
            return;

        Intent intent2 = new Intent(getApplicationContext(), SignUp3.class);
        intent2.putExtra("dest", "delivery");
        intent2.putExtra("fullName", fullName);
        intent2.putExtra("email", email);
        intent2.putExtra("userName", userName);
        intent2.putExtra("password", password);
        intent2.putExtra("address", addressS);
        intent2.putExtra("aadhaarNumber", aadhaarNumberS);
        startActivity(intent2);
    }

    private boolean validateAddress(String val) {
        return true;
    }

    private boolean validateAadhaarNumber(String val) {
        return true;
    }
}