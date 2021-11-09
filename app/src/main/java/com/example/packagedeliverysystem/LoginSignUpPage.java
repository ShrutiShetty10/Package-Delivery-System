package com.example.packagedeliverysystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class LoginSignUpPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_sign_up_page);

    }

    public void callLogin(View view) {
        startActivity(new Intent(getApplicationContext(), Login_Activity.class));
    }

    public void callSignUp(View view) {
        startActivity(new Intent(getApplicationContext(), Signup_Activity.class));
    }
}