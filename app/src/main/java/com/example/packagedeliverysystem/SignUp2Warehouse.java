package com.example.packagedeliverysystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignUp2Warehouse extends AppCompatActivity {

    String fullName, email, userName, password, addressS, resAddressS, aadhaarNumberS;
    int slotsS;
    double lat = 0, lon = 0;

    EditText address, resAddress, aadhaarNumber, slots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2_warehouse);

        Intent intent = getIntent();

        fullName = intent.getStringExtra("fullName");
        email = intent.getStringExtra("email");
        userName = intent.getStringExtra("userName");
        password = intent.getStringExtra("password");

        address = findViewById(R.id.signup_address);
        resAddress = findViewById(R.id.signup_homeAddress);
        aadhaarNumber = findViewById(R.id.signup_aadhaarnumber);
        slots = findViewById(R.id.signup_slots);

    }

    public void callSignUp3(View view) {

        addressS = address.getText().toString();
        resAddressS = resAddress.getText().toString();
        aadhaarNumberS = aadhaarNumber.getText().toString();
        slotsS = Integer.parseInt(slots.getText().toString());

        if (!validateAddress(addressS) | !validateAddress(resAddressS) | !validateAadhaarNumber(aadhaarNumberS))
            return;

        Intent intent2 = new Intent(getApplicationContext(), SignUp3.class);
        intent2.putExtra("dest", "warehouse");
        intent2.putExtra("fullName", fullName);
        intent2.putExtra("email", email);
        intent2.putExtra("userName", userName);
        intent2.putExtra("password", password);
        intent2.putExtra("address", addressS);
        intent2.putExtra("resAddress", resAddressS);
        intent2.putExtra("aadhaarNumber", aadhaarNumberS);
        intent2.putExtra("slots", slotsS);
        intent2.putExtra("lat", lat);
        intent2.putExtra("lon", lon);
        startActivity(intent2);
    }

    private boolean validateAddress(String val) {

        final boolean[] ret = new boolean[1];
        ret[0] = true;

        String url = "https://nominatim.openstreetmap.org/?addressdetails=1&q=" + val + "&format=json&limit=1";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("TAG", "Worked");
                        Log.d("TAG", response.toString());
                        try {
                            JSONObject jsonObject = response.getJSONObject(0);
                            lat = Double.parseDouble(jsonObject.getString("lat"));
                            lon = Double.parseDouble(jsonObject.getString("lon"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("TAG", lat + ", " + lon);
                        if (lat == 0 || lon == 0) {
                            Toast.makeText(getApplicationContext(), "Invalid Address", Toast.LENGTH_SHORT).show();
                            ret[0] = false;
                        } else {
                            Toast.makeText(getApplicationContext(), "Successfully added", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("TAG", "FAiled");
                    }
                });
        requestQueue.add(jsonArrayRequest);
        return ret[0];
    }

    private boolean validateAadhaarNumber(String val) {
        return true;
    }
}