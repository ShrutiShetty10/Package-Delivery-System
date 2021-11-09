package com.example.packagedeliverysystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;


public class UserHome extends AppCompatActivity {

    String UserName;
    String useremail;

    private ListView listView;
    private ArrayList<String> arrayList;
    private ArrayAdapter arrayAdapter;
    private ArrayList<PackageOrder> packageOrders;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        Intent intent = getIntent();
        UserName = intent.getStringExtra("username");
        useremail = intent.getStringExtra("useremail");
        Log.d("tag", "user email is " + useremail);
        listView = findViewById(R.id.listView);
        loadCurrentOrders();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("TAG", String.valueOf(i));
                Intent intent;
                if (packageOrders.get(i).getCurrentlyWith().equals("User")) {
                    intent = new Intent(getApplicationContext(), Order.class);
                    intent.putExtra("username", UserName);
                    intent.putExtra("useremail", useremail);
                    intent.putExtra("ORDER", packageOrders.get(i));
                } else {
                    intent = new Intent(getApplicationContext(), Userpage.class);
                    intent.putExtra("id", packageOrders.get(i).getUniqueID());
                }

                startActivity(intent);
            }
        });

    }

    private void loadCurrentOrders() {

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(UserName);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    progressBar.setVisibility(View.GONE);
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.

                    arrayList = new ArrayList<>();
                    packageOrders = new ArrayList<>();


                    for (DataSnapshot d : dataSnapshot.getChildren()) {

                        Log.d("TAG", d.toString());
                        Log.d("TAG", d.getValue().toString());

                        String s = d.getValue(String.class);
                        String state, date;
                        if (s.substring(0, 9).equals("Completed")) {
                            state = " - Completed";
                            date = s.substring(10);
                        } else {
                            state = "";
                            date = s;
                        }

                        arrayList.add(date + " - " + d.getKey().toString().substring(0, 4) + state);

                        DatabaseReference reference = database.getReference("Orders").child(d.getKey());
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                packageOrders.add(snapshot.getValue(PackageOrder.class));
                                Log.d("TAG", "adding");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }

                    arrayAdapter = new ArrayAdapter(UserHome.this, android.R.layout.simple_list_item_1, arrayList);
                    listView.setAdapter(arrayAdapter);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

    }


    public void add(View view) {
        Intent intent = new Intent(this, add.class);
        intent.putExtra("userName", UserName);
        intent.putExtra("userEmail", useremail);
        startActivity(intent);
    }
}

