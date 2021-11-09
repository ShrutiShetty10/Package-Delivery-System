package com.example.packagedeliverysystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Queue;
import java.util.Random;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class WarehouseScan extends AppCompatActivity {

    String username;

    SurfaceView surfaceView;
    EditText txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String intentData = "";
    boolean isEmail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_scan);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        EditText editText = findViewById(R.id.editTextScan);
        button = findViewById(R.id.button6);
        progressBar = findViewById(R.id.progressBar3);
        initViews();
    }

    Button button;
    ProgressBar progressBar;
    String[] allId;
    ArrayList<String> allIdList;


    ArrayList<String> id;
    ArrayList<String> type;
    ArrayList<String> slot;
    ArrayList<String> code;
    ArrayList<String> idfinal;
    ArrayList<String> typefinal;
    ArrayList<String> slotfinal;
    String scanID;
    String personID;


    public void verify(View view) {


        EditText editText = findViewById(R.id.editTextScan);


        button.setEnabled(false);

        progressBar.setVisibility(View.VISIBLE);

        allIdList = new ArrayList<>();
        String[] temp = (editText.getText().toString()).split(" ");
        Log.d("tag", String.valueOf(temp.length));
        editText.setText("");
        if (temp.length < 2) {
            button.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            Log.d("tag", "error due to few variables");

            Toast.makeText(WarehouseScan.this, "Invalid Code", Toast.LENGTH_SHORT).show();
            return;
        }

        //personID=temp[0];
        scanID = temp[0];
        allId = new String[temp.length - 1];
        for (int i = 0; i < temp.length - 1; i++)
            allId[i] = temp[i + 1];


        for (String id : allId)
            allIdList.add(id);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Warehouses").child(username);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int slots = snapshot.child("Slots").getValue(Integer.class);

                id = new ArrayList<>();
                type = new ArrayList<>();
                slot = new ArrayList<>();
                code = new ArrayList<>();
                idfinal = new ArrayList<>();
                typefinal = new ArrayList<>();
                slotfinal = new ArrayList<>();

                Log.d("tag", String.valueOf(slots));
                for (int i = 1; i <= slots; i++) {
                    String state = snapshot.child(String.valueOf(i)).child("State").getValue(String.class);
                    String slotNo = String.valueOf(i);
                    String verificationID = snapshot.child(slotNo).child("VerificationID").getValue(String.class);
                    if (state.equals("Incoming") || state.equals("Outgoing") || state.equals("Collection")) {
                        id.add(snapshot.child(slotNo).child("Code").getValue(String.class));
                        type.add(state);
                        slot.add(slotNo);
                        code.add(verificationID);


                    }

                }

                Boolean flag = false;

                Log.d("tag", "id size" + id.size());
                Log.d("tag", "allid size " + allId.length);
                Log.d("tag", "allidlist size " + allIdList.size());
                Log.d("tag", "scanid " + scanID);


                Log.d("tag", "Current flag status " + flag);
                if (!flag)
                    for (String id1 : allId) {
                        Log.d("tag", id1 + " " + id.contains(id1));
                        if (!id.contains(id1)) {
                            flag = true;
                            break;
                        }
                    }

                Log.d("tag", "Current flag status " + flag);
                if (!flag)
                    for (String id1 : allIdList) {
                        int index = id.indexOf(id1);
                        Log.d("tag", "index " + index);
                        if (!scanID.equals(code.get(index))) {
                            flag = true;
                            break;

                        }
                        idfinal.add(id.get(index));
                        slotfinal.add(slot.get(index));
                        typefinal.add(type.get(index));

                    }
                Log.d("tag", "Current flag status " + flag);
                if (flag) {
                    button.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(WarehouseScan.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                    return;
                }

//                if(!personID.equals("0")) {
//                    FirebaseDatabase database1 = FirebaseDatabase.getInstance();
//                    DatabaseReference reference1 = database1.getReference("DeliveryDetails");
//                    reference1.child(personID).child("WarehouseList").child(username).child("Visited").setValue(true);
//                }

                Log.d("tag", "All clear");
                visited();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("tag", "failed");
            }
        });


    }

    private void visited() {

        String state1 = typefinal.get(0);
        if (state1.equals("Collection")) {
            checkEachSlot();
            return;
        }
        String id1 = idfinal.get(0);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Orders");
        reference.child(id1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PackageOrder order = snapshot.getValue(PackageOrder.class);
                String currentlyWith = order.getCurrentlyWith();
                if (currentlyWith.equals("User")) {
                    checkEachSlot();
                    return;
                }
                FirebaseDatabase database1 = FirebaseDatabase.getInstance();
                DatabaseReference reference1 = database1.getReference("Deliveries");
                String person;
                if (order.isCurrentlyWithDeliveryPerson()) {
                    person = order.getCurrentlyWith();
                } else {
                    person = order.getGoingTo();
                }
                reference1.child(person).child("State").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String code = snapshot.getValue(String.class);
                        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                        DatabaseReference reference2 = database2.getReference("DeliveryDetails");
                        reference2.child(code).child("WarehouseList").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int incoming = (int) snapshot.child("Incoming").getChildrenCount();
                                int outgoing = (int) snapshot.child("Outgoing").getChildrenCount();
                                int sum = incoming + outgoing;
                                if (sum == idfinal.size()) {
                                    reference2.child(code).child("WarehouseList").child(username).child("Visited").setValue(true);
                                    Log.d("tag", "Match");
                                    checkEachSlot();
                                    return;
                                } else {
                                    button.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    Log.d("tag", "Code too short");
                                    Toast.makeText(WarehouseScan.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    String inst = "";
    int n = 1;
    String queue = "";

    private void checkEachSlot() {


        if (slotfinal.size() == 0) {


            progressBar.setVisibility(View.GONE);
            button.setEnabled(true);

            Intent intent = new Intent(getApplicationContext(), WarehouseInstructions.class);
            intent.putExtra("instructions", inst);
            intent.putExtra("username", username);
            startActivity(intent);

            finish();

            return;
        } else {

            String slot1 = slotfinal.get(0);
            String id1 = idfinal.get(0);
            String state = typefinal.get(0);

            idfinal.remove(0);
            typefinal.remove(0);
            slotfinal.remove(0);


            if (state.equals("Incoming")) {
                Log.d("tag", "incoming");


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("Warehouses").child(username);
                //               reference.child(slot1).child("State").setValue("Occupied");

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("Queue")) {
                            Log.d("tag", "Path exists");
                            queue += snapshot.child("Queue").getValue(String.class);
                        }


                        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                        DatabaseReference reference2 = database2.getReference("Orders");
                        reference2.child(id1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                PackageOrder order = snapshot.getValue(PackageOrder.class);
                                Log.d("TAG", "order successfuly loaded");


                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String date = dateFormat.format(new Date());

                                order.addPlace(date + " - At " + username);
                                order.setCurrentSlot(Integer.parseInt(slot1));
                                order.setCurrentlyWith(username);
                                order.setCurrentlyWithDeliveryPerson(false);

                                ArrayList<String> warehouses = order.getWarehouseList();
                                while (!warehouses.get(0).equals(username))
                                    warehouses.remove(0);
                                warehouses.remove(0);
                                order.setWarehouseList(warehouses);


                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Warehouses").child(username);


                                if (warehouses.size() == 0) {
                                    reference.child(slot1).child("State").setValue("Collection");
                                    reference.child(slot1).child("VerificationID").setValue(order.getVid2());
                                    order.setGoingTo("Reciever");
                                    int pin = new Random().nextInt(9000) + 1000;
                                    order.setPin(String.valueOf(pin));
                                    String qr = order.getVid2() + "%20" + order.getUniqueID();

                                    emailReciever(order.getName(), order.getEmail(), order.getNumber(), username, slot1, order.getUniqueID(), qr, order.getPin(), order.getDestCoordinates(), order.getDestWarehouseCoordinates());
                                } else {
                                    reference.child(slot1).child("State").setValue("Occupied");
                                    reference.child(slot1).child("VerificationID").removeValue();
                                    order.setGoingTo("Waiting");


                                    FirebaseDatabase database1 = FirebaseDatabase.getInstance();
                                    DatabaseReference reference1 = database1.getReference("Warehouses").child(username);

                                    if (!queue.equals("")) {

                                        queue += (" " + id1);


                                        reference1.child("Queue").setValue(queue);


                                    } else {
                                        reference1.child("Queue").setValue(id1);
                                    }


                                }

                                FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                                DatabaseReference reference3 = database3.getReference("Orders");
                                reference3.child(id1).setValue(order);


                                inst += (n++) + ". Place " + order.getUniqueID().substring(0, 4) + " at slot " + slot1 + "\n";

                                checkEachSlot();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("tag", "failed");
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else if (state.equals("Collection")) {
                inst += "pin" + id1;
                checkEachSlot();
            } else {
                Log.d("tag", "outgoing");
                FirebaseDatabase database1 = FirebaseDatabase.getInstance();
                DatabaseReference reference1 = database1.getReference("Orders");
                reference1.child(id1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        PackageOrder order = snapshot.getValue(PackageOrder.class);
                        String recieverName = order.getGoingTo();
                        order.setCurrentSlot(0);
                        order.setCurrentlyWith(recieverName);
                        order.setCurrentlyWithDeliveryPerson(true);
                        order.setGoingTo("Warehouse");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String date = dateFormat.format(new Date());

                        order.addPlace(date + " -  With " + recieverName);


                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("Warehouses").child(username);
                        reference.child(slot1).child("State").setValue("Unoccupied");
                        reference.child(slot1).child("VerificationID").removeValue();
                        reference.child(slot1).child("Code").removeValue();


                        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                        DatabaseReference reference3 = database3.getReference("Orders");
                        reference3.child(id1).setValue(order);
                        inst += (n++) + ". Handover " + order.getUniqueID().substring(0, 4) + " from slot " + slot1 + " to " + recieverName + "\n";


                        checkEachSlot();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

        }
    }

    private void emailReciever(String name, String email, String number, String destination, String slot, String id, String qr, String pin, Point dest, Point destWare) {

        Log.d("tag", "Recievers Details");
        Log.d("tag", "name " + name);
        Log.d("tag", "email " + email);
        Log.d("tag", "number " + destination);
        Log.d("tag", "slot " + slot);
        Log.d("tag", "qr " + qr);
        Log.d("tag", "pin " + pin);

        double destx = dest.getX();
        double desty = dest.getY();
        double destWarex = destWare.getX();
        double destWarey = destWare.getY();

        String directions = "https://www.google.com/maps/dir/" + destx + "," + desty + "/" + destWarex + "," + destWarey + "/";


        String inst = "1. Go to " + destination + ", directions: " + directions + "\n2. Scan code: https://chart.apis.google.com/chart?cht=qr&chs=500x500&chl=" + qr + "\n3. Enter pin: " + pin + "\n4. Collect " + id.substring(0, 4) + " from slot " + slot + "\n";
        Log.d("tag", "Inst " + inst);

        String username = "8853.jayeshbadwal.secompa@gmail.com";
        String password = "Jayesh@15";
        String message = inst;
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
            message1.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message1.setSubject("Hello " + name + ", Your parcel is ready to collect");
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


    private void initViews() {
        txtBarcodeValue = findViewById(R.id.editTextScan);
        surfaceView = findViewById(R.id.surfaceView);
    }

    private void initialiseDetectorsAndSources() {

        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(WarehouseScan.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(WarehouseScan.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                //Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(new Runnable() {
                        @Override
                        public void run() {

                            if (barcodes.valueAt(0).email != null) {
                                txtBarcodeValue.removeCallbacks(null);
                                intentData = barcodes.valueAt(0).email.address;
                                txtBarcodeValue.setText(intentData);
                                isEmail = true;
                            } else {
                                isEmail = false;
                                intentData = barcodes.valueAt(0).displayValue;
                                txtBarcodeValue.setText(intentData);

                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }


}