package com.example.packagedeliverysystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class deliveryScan extends AppCompatActivity {

    String s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_scan);
        Intent intent=getIntent();
        String inst=intent.getStringExtra("inst");
        s=intent.getStringExtra("qr");

        TextView textView = findViewById(R.id.textViewTitle1);
        String text=inst;
        textView.setText(text);

        textView = findViewById(R.id.textViewTitle11);
        textView.setText(intent.getStringExtra("name")+"\nInstructions");

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
}