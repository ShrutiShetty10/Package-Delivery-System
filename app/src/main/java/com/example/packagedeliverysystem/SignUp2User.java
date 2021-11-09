package com.example.packagedeliverysystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SignUp2User extends AppCompatActivity {

    String fullName, email, userName, password, addressS;

    EditText address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2_user);

        Intent intent = getIntent();

        fullName = intent.getStringExtra("fullName");
        email = intent.getStringExtra("email");
        userName = intent.getStringExtra("userName");
        password = intent.getStringExtra("password");

        address = findViewById(R.id.signup_address);

    }

    public void callSignUp3(View view) {

        addressS = address.getText().toString();

        if (!validateAddress(addressS))
            return;

        Intent intent2 = new Intent(getApplicationContext(), SignUp3.class);
        intent2.putExtra("dest", "User");
        intent2.putExtra("fullName", fullName);
        intent2.putExtra("email", email);
        intent2.putExtra("userName", userName);
        intent2.putExtra("password", password);
        intent2.putExtra("address", addressS);
        startActivity(intent2);
    }

    private boolean validateAddress(String val) {
        return true;
    }
}