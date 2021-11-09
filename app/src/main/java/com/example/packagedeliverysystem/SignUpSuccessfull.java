package com.example.packagedeliverysystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SignUpSuccessfull extends AppCompatActivity {

    String dest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_successfull);

        Intent intent = getIntent();

        dest = intent.getStringExtra("dest");
    }

    public void backToLogin(View view) {
        Intent intent2 = new Intent(getApplicationContext(), Login_page.class);
        intent2.putExtra("dest", dest);
        startActivity(intent2);
    }
}