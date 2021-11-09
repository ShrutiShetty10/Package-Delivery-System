package com.example.packagedeliverysystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Signup_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_);

    }

    public void callSignUpWarehouse(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUp.class);
        intent.putExtra("dest", "warehouse");
        startActivity(intent);
    }

    public void callSignUpDelivery(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUp.class);
        intent.putExtra("dest", "delivery");
        startActivity(intent);
    }

    public void callSignUpUser(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUp.class);
        intent.putExtra("dest", "User");
        startActivity(intent);
    }
}