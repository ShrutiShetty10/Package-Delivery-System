package com.example.packagedeliverysystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class add extends AppCompatActivity {

    String UserName;
    String useremail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Intent intent=getIntent();
        UserName=intent.getStringExtra("userName");
        useremail=intent.getStringExtra("userEmail");



    }

    private boolean containsText(EditText text) {
        String val = text.getText().toString().trim();
        if (val.isEmpty()) {
            text.setError("Field can not be empty");
            return false;
        } else {
            text.setError(null);
            return true;
        }
    }

    private boolean isValid(EditText text) {
        String email = text.getText().toString().trim();
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        if (email.matches(regex)) {
            text.setError(null);
            return true;
        } else {
            text.setError("Enter valid email");
            return false;
        }
    }

    public void continueToPackageDetails(View view) {
        EditText sourceAddress=findViewById(R.id.editTextSourceAddress), destinationAddress=findViewById(R.id.editTextDestinationAddress), personName=findViewById(R.id.editTextTextPersonName), number=findViewById(R.id.editTextNumber), email=findViewById(R.id.editTextEmailAddress);

        boolean canContinue=true;
        if(!containsText(sourceAddress))
            canContinue=false;
        if(!containsText(destinationAddress))
            canContinue=false;
        if(!containsText(personName))
            canContinue=false;
        if(!validatePhoneNumber(number))
            canContinue=false;
        if(!isValid(email))
            canContinue=false;

        if(canContinue){
            Intent intent = new Intent(this, PackageDetails.class);
            intent.putExtra("SOURCE", sourceAddress.getText().toString());
            intent.putExtra("DESTINATION", destinationAddress.getText().toString());
            intent.putExtra("NAME", personName.getText().toString());
            intent.putExtra("NUMBER", number.getText().toString());
            intent.putExtra("USER", UserName);
            intent.putExtra("EMAIL", email.getText().toString());
            intent.putExtra("useremail", useremail);

            startActivity(intent);


        }

    }

    private boolean validatePhoneNumber(EditText phoneNumber) {
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
        if(val.length()!=10){
            phoneNumber.setError("Length must be 10 digits");
            return false;
        }
        phoneNumber.setError(null);
        return true;
    }




}