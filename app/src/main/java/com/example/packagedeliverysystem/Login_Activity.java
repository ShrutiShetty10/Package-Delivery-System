package com.example.packagedeliverysystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Login_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
    }

    public void callLoginWarehouse(View view) {
        Intent intent = new Intent(getApplicationContext(), Login_page.class);
        intent.putExtra("dest", "warehouse");
        startActivity(intent);
    }

    public void callLoginDelivery(View view) {
        Intent intent = new Intent(getApplicationContext(), Login_page.class);
        intent.putExtra("dest", "delivery");
        startActivity(intent);
    }

    public void callLoginUser(View view) {
        Intent intent = new Intent(getApplicationContext(), Login_page.class);
        intent.putExtra("dest", "User");
        startActivity(intent);
    }
}