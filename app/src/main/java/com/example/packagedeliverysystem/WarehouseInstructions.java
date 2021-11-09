
package com.example.packagedeliverysystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class WarehouseInstructions extends AppCompatActivity {

    String id;
    String inst;
    TextView textView;
    EditText editText;
    Button button;
    ProgressBar progressBar;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_instructions);
        Intent intent = getIntent();
        inst = intent.getStringExtra("instructions");
        username = intent.getStringExtra("username");


        textView = findViewById(R.id.textViewinst);
        editText = findViewById(R.id.editTextNumberPin);
        button = findViewById(R.id.buttonVerify);
        progressBar = findViewById(R.id.progressBar5);

        if (inst.substring(0, 3).equals("pin")) {
            pin();
            id = inst.substring(3);
        } else {
            textView.setText(inst);
        }

    }

    private void pin() {

        editText.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);

        textView.setText("Enter your PIN");
    }


    public void verify(View view) {
        progressBar.setVisibility(View.VISIBLE);
        button.setEnabled(false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Orders");
        Log.d("tag", id);
        reference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PackageOrder order = snapshot.getValue(PackageOrder.class);

                String pin = order.getPin();
                if (editText.getText().toString().equals(pin)) {
                    int slotNo;
                    Log.d("tag", "Pin is correct");
                    order.setCurrentlyWith("Reciever");
                    slotNo = order.getCurrentSlot();
                    order.setCurrentSlot(0);
                    order.setGoingTo("");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = dateFormat.format(new Date());
                    order.addPlace(date + " Order Collected");
                    FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                    DatabaseReference reference3 = database3.getReference("Orders");
                    reference3.child(id).setValue(order);

                    FirebaseDatabase database1 = FirebaseDatabase.getInstance();
                    DatabaseReference reference1 = database1.getReference("Warehouses");
                    reference1.child(username).child(String.valueOf(slotNo)).child("State").setValue("Unoccupied");
                    reference1.child(username).child(String.valueOf(slotNo)).child("VerificationID").removeValue();
                    reference1.child(username).child(String.valueOf(slotNo)).child("Code").removeValue();

                    editText.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);

                    textView.setText("1. Handover " + order.getUniqueID().substring(0, 4) + " from slot " + slotNo + " to " + order.getName());
                    FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                    DatabaseReference reference2 = database2.getReference("Users");
                    String s = "Completed" + order.getDate();
                    reference2.child(order.getUser()).child(order.getUniqueID()).setValue(s);
                    email(order);


                } else {
                    progressBar.setVisibility(View.GONE);
                    button.setEnabled(true);
                    Toast.makeText(WarehouseInstructions.this, "Wrong Pin", Toast.LENGTH_SHORT).show();
                }
                editText.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void email(PackageOrder order) {

        Log.d("tag", "Send email to " + order.getUserEmail());

        String username = "8853.jayeshbadwal.secompa@gmail.com";
        String password = "Jayesh@15";
        String message = "Thank You for using our app.";
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message1 = new MimeMessage(session);
            message1.setFrom(new InternetAddress(username));
            message1.setRecipients(Message.RecipientType.TO, InternetAddress.parse(order.getUserEmail()));
            message1.setSubject("Hello " + order.getUser() + ", Your order has been collected");
            message1.setText(message);
            new SendMail().execute(message1);
            //Toast.makeText(getApplicationContext(), "Email sent successfully", Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private class SendMail extends AsyncTask<Message, String, String> {

        @Override
        protected String doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
                return "Success";
            } catch (MessagingException e) {
                e.printStackTrace();
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("Success")) {
                Toast.makeText(getApplicationContext(), "Email sent successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Something went wrong?", Toast.LENGTH_SHORT).show();
            }
        }
    }
}