package com.example.packagedeliverysystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;

public class SignUp3 extends AppCompatActivity {

    String dest;
    String fullName, email, userName, password, address, resAddress, aadhaarNumber, phoneNumberS;
    int slots;
    double lat, lon;

    EditText phoneNumber;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up3);

        Intent intent = getIntent();

        fullName = intent.getStringExtra("fullName");
        email = intent.getStringExtra("email");
        userName = intent.getStringExtra("userName");
        password = intent.getStringExtra("password");
        address = intent.getStringExtra("address");

        dest = intent.getStringExtra("dest");

        if(dest.equals("delivery")) {
            aadhaarNumber = intent.getStringExtra("aadhaarNumber");
        } else if(dest.equals("warehouse")) {
            resAddress = intent.getStringExtra("resAddress");
            aadhaarNumber = intent.getStringExtra("aadhaarNumber");
            slots = intent.getIntExtra("slots", 0);
            lat = intent.getDoubleExtra("lat", 0);
            lon = intent.getDoubleExtra("lon", 0);
        }

        phoneNumber = findViewById(R.id.signup_phone_number);
        countryCodePicker = findViewById(R.id.country_code_picker);

    }

    public void callVerifyOTP(View view) {

        if (!validatePhoneNumber())
            return;

        phoneNumberS = phoneNumber.getText().toString().trim();
        phoneNumberS = "+" + countryCodePicker.getFullNumber() + phoneNumberS;

        Intent intent2 = new Intent(getApplicationContext(), VerifyOTP.class);
        intent2.putExtra("whereToGo", "signUpSuccessfull");
        intent2.putExtra("dest", dest);
        intent2.putExtra("fullName", fullName);
        intent2.putExtra("email", email);
        intent2.putExtra("userName", userName);
        intent2.putExtra("password", password);
        intent2.putExtra("address", address);
        if(dest.equals("delivery")) {
            intent2.putExtra("aadhaarNumber", aadhaarNumber);
        } else if(dest.equals("warehouse")) {
            intent2.putExtra("resAddress", resAddress);
            intent2.putExtra("aadhaarNumber", aadhaarNumber);
            intent2.putExtra("slots", slots);
            intent2.putExtra("lat", lat);
            intent2.putExtra("lon", lon);
        }
        intent2.putExtra("phoneNumber", phoneNumberS);
        startActivity(intent2);
    }

    private boolean validatePhoneNumber() {
        String val = phoneNumber.getText().toString().trim();

        if (val.isEmpty()) {
            phoneNumber.setError("Enter valid phone number");
            return false;
        }
        if (val.contains(" ")) {
            phoneNumber.setError("No White spaces are allowed!");
            return false;
        }
        if (!(val.charAt(0) == '7' || val.charAt(0) == '8' || val.charAt(0) == '9')) {
            phoneNumber.setError("Enter valid phone number");
            return false;
        }
        phoneNumber.setError(null);
        return true;
    }
}