package com.example.packagedeliverysystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WarehouseHome extends AppCompatActivity {

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_home);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        Log.d("tag", username);
        listView = findViewById(R.id.listview2);
        progressBar = findViewById(R.id.progressBar4);
        loadData();


    }

    private ListView listView;
    private ArrayList<String> arrayList;
    private ArrayAdapter arrayAdapter;
    ProgressBar progressBar;


    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Warehouses").child(username);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    arrayList = new ArrayList<>();

                    int slots = snapshot.child("Slots").getValue(Integer.class);
                    for (int i = 1; i <= slots; i++) {
                        String state = snapshot.child(String.valueOf(i)).child("State").getValue(String.class);
                        if (state.equals("Unoccupied")) {
                            arrayList.add("Slot " + String.valueOf(i) + " - Unoccupied");
                        } else {
                            String s = snapshot.child(String.valueOf(i)).child("Code").getValue(String.class);
                            String code = s.substring(0, 4);
                            arrayList.add("Slot " + String.valueOf(i) + " - " + state + " - " + code);
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
                    listView.setAdapter(arrayAdapter);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void scan(View view) {
        Intent intent = new Intent(getApplicationContext(), WarehouseScan.class);
        intent.putExtra("username", username);
        startActivity(intent);

    }


}