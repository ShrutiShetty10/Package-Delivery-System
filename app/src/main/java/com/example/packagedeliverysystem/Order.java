package com.example.packagedeliverysystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class Order extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Order.this, UserHome.class);

        intent.putExtra("username", user);
        intent.putExtra("useremail", useremail);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private PackageOrder order;
    private String s;
    private String user;
    String useremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Intent intent=getIntent();
        order= (PackageOrder) intent.getSerializableExtra("ORDER");
        user=intent.getStringExtra("username");
        useremail=intent.getStringExtra("useremail");


        TextView textView = findViewById(R.id.textViewTitle1);
        String text="1. Please keep your parcel in a sealed box\n2. Write "+order.getUniqueID().substring(0,4)+" on the box.\n3. Go to "+order.getGoingTo()+"\n4. Deliver " + order.getUniqueID().substring(0, 4) + " to slot " + order.getCurrentSlot() + "\n";
        textView.setText(text);


        this.s=order.getVid1()+" "+order.getUniqueID().toString();//replace with intent
        drawQRCode();



    }

    void drawQRCode(){

        String url="https://chart.apis.google.com/chart?cht=qr&chs=500x500&chl="+this.s;
        System.out.println(url);
        final ImageView imageView = findViewById(R.id.imageView1);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageView.setImageBitmap(response);
            }
        }, 500, 500, ImageView.ScaleType.CENTER_CROP, // Image scale type
                Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(imageRequest);



    }


    public void map(View view) {

        Log.d("tag", "open map for");
        Log.d("tag", "Source "+order.getSource());
        Log.d("tag", "Destination "+order.getGoingTo());
        Point s=order.getSourceCoordinates();
        Point d=order.getSourceWarehouseCoordinates();

        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("source", s);
        intent.putExtra("destination", d);
        startActivity(intent);

    }
}