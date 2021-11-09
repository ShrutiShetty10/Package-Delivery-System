package com.example.packagedeliverysystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login_page extends AppCompatActivity {

    String dest;

    EditText userName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Intent intent = getIntent();

        dest = intent.getStringExtra("dest");

        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
    }

    public void callForgotPassword(View view) {
        Intent intent2 = new Intent(getApplicationContext(), ForgotPassword.class);
        intent2.putExtra("dest", dest);
        startActivity(intent2);
    }

    public void callHomescreen(View view) {
        CheckInternet checkInternet = new CheckInternet();

        if (!checkInternet.isConnected(this)) {
            showCustomDialog();
            Log.d("Check", "see");
            return;
        }

        final String _password = password.getText().toString().trim();
        final String _userName = userName.getText().toString().trim();


        Query checkUser = FirebaseDatabase.getInstance().getReference("Registration").child(dest).orderByChild("userName").equalTo(_userName);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userName.setError(null);

                    String systemPassword = dataSnapshot.child(_userName).child("password").getValue(String.class);
                    assert systemPassword != null;
                    if (systemPassword.equals(_password)) {
                        password.setError(null);

                        String _fullName = dataSnapshot.child(_userName).child("fullName").getValue(String.class);
                        String _email = dataSnapshot.child(_userName).child("email").getValue(String.class);

                        Toast.makeText(getApplicationContext(), _fullName + "\n" + _email, Toast.LENGTH_SHORT).show();

                        Intent intent;
                        if(dest.equals("User")) {
                            intent = new Intent(getApplicationContext(), UserHome.class);
                            intent.putExtra("useremail", _email);
                        } else if(dest.equals("delivery"))
                            intent = new Intent(getApplicationContext(), DeliveryHome.class);
                        else
                            intent = new Intent(getApplicationContext(), WarehouseHome.class);
                        intent.putExtra("username", _userName);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "No such user exists!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage("Please connect to the internet to proceed further").setCancelable(false).setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getApplicationContext(), LoginSignUpPage.class));
                finish();
            }
        });

    }
}