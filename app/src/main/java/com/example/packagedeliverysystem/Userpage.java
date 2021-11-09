package com.example.packagedeliverysystem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Userpage extends AppCompatActivity {

    private PackageOrder order;
    ProgressBar progressBar;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userpage);
        Intent intent = getIntent();
        progressBar=findViewById(R.id.progressBar6);
        //order= (PackageOrder) intent.getSerializableExtra("ORDER");
        id=intent.getStringExtra("id");


        load();

    }

    private void load() {

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference("Orders");
        reference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Log.d("tag", "called");
                    order = snapshot.getValue(PackageOrder.class);
                    ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, order.getPlaces());
                    ListView listview = findViewById(R.id.listview2);
                    listview.setAdapter(adapter);
                    Button button = findViewById(R.id.buttonDelete);
                    if (order.getCurrentlyWith().equals("Reciever"))
                        button.setVisibility(View.VISIBLE);

                    progressBar.setVisibility(View.GONE);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void delete(View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users").child(order.getUser()).child(order.getUniqueID());
        reference.removeValue();
        super.finish();

    }

    public void btn(View view) {
        Intent intent=new Intent(getApplicationContext(), deliveryScan.class);
        intent.putExtra("name", order.getName() );
        intent.putExtra("qr", order.getVid2()+" "+order.getUniqueID());
        String inst="1. Go to "+order.getCurrentlyWith()+"\n2. Scan code and enter "+order.getPin()+" for PIN\n3. Collect "+order.getUniqueID().substring(0,4)+" from slot "+order.getCurrentSlot()+"\n";
        intent.putExtra("inst", inst);
        startActivity(intent);
    }
}