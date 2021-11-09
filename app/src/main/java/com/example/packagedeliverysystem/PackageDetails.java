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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class PackageDetails extends AppCompatActivity {

    String source, destination, name, number;
    String UserName;
    String useremail;
    String email;
    EditText length, width, height, weight;

    Button button;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_details);
        Intent intent=getIntent();
        source=intent.getStringExtra("SOURCE");
        destination=intent.getStringExtra("DESTINATION");
        name=intent.getStringExtra("NAME");
        number=intent.getStringExtra("NUMBER");
        UserName=intent.getStringExtra("USER");
        email=intent.getStringExtra("EMAIL");
        useremail=intent.getStringExtra("useremail");
        button=findViewById(R.id.button);
        progressBar=findViewById(R.id.progressBar2);
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

    public void ContinueToPayment(View view) {
        length=findViewById(R.id.editTextNumberDecimallength);
        width=findViewById(R.id.editTextNumberDecimalwidth);
        height=findViewById(R.id.editTextNumberDecimalheight);
        weight=findViewById(R.id.editTextNumberDecimalweight);
        boolean canContinue = true;
        if (!containsText(length))
            canContinue = false;
        if (!containsText(width))
            canContinue = false;
        if (!containsText(height))
            canContinue = false;
        if (!containsText(weight))
            canContinue = false;
        if (canContinue) {


            button.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            locateLatAndLon(source, destination);





        }
    }

    double latSource=0, lonSource=0, latDest=0, lonDest=0;
    //This method basically gives the source and destination latitudes and longitudes
     void locateLatAndLon(String source, String destination){


         String url="https://nominatim.openstreetmap.org/?addressdetails=1&q="+source+"&format=json&limit=1";

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
                             latSource= Double.parseDouble(jsonObject.getString("lat"));
                             lonSource= Double.parseDouble(jsonObject.getString("lon"));
                         } catch (JSONException e) {
                             e.printStackTrace();
                         }



                         String url="https://nominatim.openstreetmap.org/?addressdetails=1&q="+destination+"&format=json&limit=1";

                         RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

// Request a string response from the provided URL.
                         double finalLatSource = latSource;
                         JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                                 (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                                     @Override
                                     public void onResponse(JSONArray response) {

                                         Log.d("TAG", "Worked");
                                         Log.d("TAG", response.toString());
                                         try {
                                             JSONObject jsonObject = response.getJSONObject(0);
                                             latDest= Double.parseDouble(jsonObject.getString("lat"));
                                             lonDest= Double.parseDouble(jsonObject.getString("lon"));
                                         } catch (JSONException e) {
                                             e.printStackTrace();
                                         }


                                         Log.d("TAG", latSource +", "+lonSource);
                                         Log.d("TAG", latDest +", "+lonDest);
                                         if(latSource==0||lonSource==0){
                                             Toast.makeText(PackageDetails.this, "Source Address may be incorrect", Toast.LENGTH_SHORT).show();
                                             checkWarehouse(new ArrayList<>());
                                             return;
                                         }
                                         if(latDest==0||lonDest==0){
                                             Toast.makeText(PackageDetails.this, "Destination Address may be incorrect", Toast.LENGTH_SHORT).show();
                                             checkWarehouse(new ArrayList<>());
                                             return;
                                         }
                                         getWarehouseList();


                                     }
                                 }, new Response.ErrorListener() {

                                     @Override
                                     public void onErrorResponse(VolleyError error) {
                                         // TODO: Handle error
                                         Log.d("TAG", "FAiled");
                                     }
                                 });



                         requestQueue.add(jsonArrayRequest);



                     }
                 }, new Response.ErrorListener() {

                     @Override
                     public void onErrorResponse(VolleyError error) {
                         // TODO: Handle error
                         Log.d("TAG", "FAiled");
                     }
                 });



         requestQueue.add(jsonArrayRequest);



    }

    ArrayList<Point> pointArrayList;
    ArrayList<String> warehouseNames;
    //This method basically creates an arraylist of the points and another arraylist with the names
    void getWarehouseList(){
         pointArrayList= new ArrayList<>();
         warehouseNames=new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("WarehouseCoordinates");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d("TAG", dataSnapshot.toString());

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    warehouseNames.add(snapshot.getKey());
                    pointArrayList.add(snapshot.getValue(Point.class));
                }
                //Log.d("TAG", pointArrayList.toString());
                Log.d("TAG", warehouseNames.toString());

                algorithm();


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    double multiplier=1.0/111.0;
    Point pointS;
    Point pointD;
    Point src;
    Point dest;


    void algorithm(){
        Log.d("TAG", "startAlgorithm");
        //if there is no warehouse near source or destination return an empty array list
        ArrayList<String> arrayList = new ArrayList<>();
        //coordinates of source warehouse and dest warehouse
        double latSW=0, lonSW=0, latDW=0, lonDW=0;

        //get latsw , lonsw


        double distS=multiplier*1;//dist variable


        pointS=new Point();
        pointS.setX(latSource);
        pointS.setY(lonSource);
        String nameS="";

        for(Point p: pointArrayList){
            double d=pointS.dist(p);
            if(d<distS){
                latSW=p.getX();
                lonSW=p.getY();
                nameS=warehouseNames.get(pointArrayList.indexOf(p));
                distS=d;
            }
        }

        if(nameS.equals("")){
            Toast.makeText(this, "No warehouses found near source", Toast.LENGTH_SHORT).show();
            checkWarehouse(arrayList);
            return;
        }

        Log.d("TAG", nameS+" "+distS);

        //get latdw , londw


        double distD=multiplier*1;//dist variable


        pointD=new Point();
        pointD.setX(latDest);
        pointD.setY(lonDest);
        String nameD="";

        for(Point p: pointArrayList){
            double d=pointD.dist(p);
            if(d<=distD){
                latDW=p.getX();
                lonDW=p.getY();
                nameD=warehouseNames.get(pointArrayList.indexOf(p));
                distD=d;
            }
        }

        if(nameD.equals("")){
            Toast.makeText(this, "No warehouses found near destination", Toast.LENGTH_SHORT).show();
            checkWarehouse(arrayList);
            return;
        }

        Log.d("TAG", nameD+" "+distD);

        if(nameS.equals(nameD)){
            Toast.makeText(this, "Distance is very small", Toast.LENGTH_SHORT).show();
            checkWarehouse(arrayList);
            return;
        }



        //actual code
        arrayList.add(nameS);

        src=new Point();
        src.setX(latSW);
        src.setY(lonSW);

        dest=new Point();
        dest.setX(latDW);
        dest.setY(lonDW);


        //dist
        double dist;
        if(src.dist(dest)<=multiplier*100)
            dist=multiplier*1;
        else if(src.dist(dest)<=multiplier*300)
            dist=multiplier*3;
        else//long
            dist=multiplier*5;

        src.printPoint();
        dest.printPoint();

        Point[] coordinates=getCoordinates(src, dest, dist);

        coordinates[0].printPoint();
        coordinates[1].printPoint();
        coordinates[2].printPoint();
        coordinates[3].printPoint();

        Point a=coordinates[0];
        Point b=coordinates[1];
        Point c=coordinates[2];
        Point d=coordinates[3];

        a.printPoint();
        b.printPoint();
        c.printPoint();
        d.printPoint();


        HashMap<String, Double> hashMap=new HashMap<String, Double>();
        for(Point p: pointArrayList){
            p.printPoint();
            String name = warehouseNames.get(pointArrayList.indexOf(p));

            if(!name.equals(nameS) && !name.equals(nameD) && p.isInside(a,b,c,d)){

                hashMap.put(name, p.dist(src));
            }

        }
        Log.d("TAG", "Size of hash map "+String.valueOf(hashMap.size()));
        hashMap=sortHashMapByValues(hashMap);
        for(String s: hashMap.keySet())
            arrayList.add(s);




        arrayList.add(nameD);
        checkWarehouse(arrayList);


    }

    int slotAssigned;

    void checkWarehouse(ArrayList<String> warehouseList){

        if(warehouseList.size()==0){
            //Toast.makeText(this, "There was an error generating path", Toast.LENGTH_SHORT).show();
            button.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            return;
        }
        PackageOrder packageOrder = new PackageOrder();
        packageOrder.setWarehouseList(warehouseList);
        String nearestWarehouse=packageOrder.getWarehouseList().get(0);
        packageOrder.setGoingTo(nearestWarehouse);
        packageOrder.setVid1(UUID.randomUUID().toString());
        packageOrder.setVid2(UUID.randomUUID().toString());
        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        DatabaseReference reference1 = database1.getReference("Warehouses").child(nearestWarehouse);
        Log.d("TAG", "Reacked");

        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalSlots = snapshot.child("Slots").getValue(Integer.class);
                Log.d("TAG", String.valueOf(totalSlots));
                boolean set=false;
                for (int i = 1; i <= totalSlots; i++) {
                    String currentState=snapshot.child(String.valueOf(i)).child("State").getValue(String.class);
                    Log.d("TAG", currentState);
                    if(currentState.equals("Unoccupied")){
                        reference1.child(String.valueOf(i)).child("State").setValue("Incoming");
                        reference1.child(String.valueOf(i)).child("Code").setValue(packageOrder.getUniqueID());
                        reference1.child(String.valueOf(i)).child("VerificationID").setValue(packageOrder.getVid1());
                        slotAssigned=i;
                        set=true;
                        break;
                    }
                }
                if(set) {
                    Log.d("TAG", "Empty");


                    packageOrder.setUser(UserName);
                    packageOrder.setSource(source);
                    packageOrder.setDestination(destination);
                    packageOrder.setName(name);
                    packageOrder.setNumber(number);
                    packageOrder.setEmail(email);

                    packageOrder.setLength(length.getText().toString());
                    packageOrder.setWidth(width.getText().toString());
                    packageOrder.setHeight(height.getText().toString());
                    packageOrder.setWeight(weight.getText().toString());
                    CheckBox checkBox = findViewById(R.id.checkBox);
                    packageOrder.setFragile(checkBox.isChecked());
                    packageOrder.setCurrentlyWith("User");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = dateFormat.format(new Date());
                    packageOrder.setDate(date);
                    packageOrder.addPlace(date + " Order Placed");
                    packageOrder.setCurrentSlot(slotAssigned);
                    packageOrder.setCurrentlyWithDeliveryPerson(false);

                    packageOrder.setPin("");

                    packageOrder.setSourceCoordinates(pointS);
                    packageOrder.setDestCoordinates(pointD);
                    packageOrder.setSourceWarehouseCoordinates(src);
                    packageOrder.setDestWarehouseCoordinates(dest);
                    packageOrder.setUserEmail(useremail);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference myRef = database.getReference("Orders").child(packageOrder.getUniqueID());
                    myRef.setValue(packageOrder);

                    myRef = database.getReference("Users").child(UserName);
                    myRef.child(packageOrder.getUniqueID()).setValue(date);


                    email(packageOrder);
                    Intent intent = new Intent(getApplicationContext(), Order.class);
                    intent.putExtra("ORDER", packageOrder);
                    intent.putExtra("username", UserName);
                    intent.putExtra("useremail", useremail);


                    Log.d("tag","username is "+ UserName);
                    Log.d("tag","name is"+ name);
                    startActivity(intent);

                }
                else {
                    Log.d("TAG", "Full");
                    Toast.makeText(PackageDetails.this, "Nearest Warehouse is Full, try again later", Toast.LENGTH_SHORT).show();
                    button.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void email(PackageOrder order) {
        Log.d("tag", "Send email to "+order.getUserEmail());

        String username = "8853.jayeshbadwal.secompa@gmail.com";
        String password = "Jayesh@15";
        String message = "You can track your order through the app.";
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
            message1.setSubject("Hello "+order.getUser()+", Your order has been placed");
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

            if(s.equals("Success")) {
                Toast.makeText(getApplicationContext(), "Email sent successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Something went wrong?", Toast.LENGTH_SHORT).show();
            }
        }
    }

    static double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix) {
        double[][] product = new double[3][3];
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    product[i][j] += firstMatrix[i][k] * secondMatrix[k][j];
                }
            }
        }

        return product;
    }

    static Point[] getCoordinates(Point s, Point d, double dist){
        Point coordinates[]=new Point[4];
        double x1=s.getX();
        double y1=s.getY();
        double x2=d.getX();
        double y2=d.getY();
        double m=(y2-y1)/(x2-x1);
        double theta=Math.atan(m);


        //first point
        double x=x1;
        double y=y1+dist;

        double[][] t = { {1,0,0}, {0,1,0}, {x-x1, y-y1, 1}};
        double[][] r = { {Math.cos(theta), Math.sin(theta), 0}, {-Math.sin(theta), Math.cos(theta), 0}, {0, 0, 1}};
        double[][] tInv = { {1,0,0}, {0,1,0}, {x1, y1, 1}};

        double[][] product=multiplyMatrices(multiplyMatrices(t, r), tInv);

        coordinates[0]=new Point();
        coordinates[0].setX(product[2][0]);
        coordinates[0].setY(product[2][1]);


        //second Point
        x=x1;
        y=y1-dist;

        t[2][0]=x-x1;
        t[2][1]=y-y1;
        tInv[2][0]=x1;
        tInv[2][1]=y1;


        product=multiplyMatrices(multiplyMatrices(t, r), tInv);

        coordinates[1]=new Point();
        coordinates[1].setX(product[2][0]);
        coordinates[1].setY(product[2][1]);

        //third point
        x=x2;
        y=y2-dist;

        t[2][0]=x-x2;
        t[2][1]=y-y2;
        tInv[2][0]=x2;
        tInv[2][1]=y2;

        product=multiplyMatrices(multiplyMatrices(t, r), tInv);

        coordinates[2]=new Point();
        coordinates[2].setX(product[2][0]);
        coordinates[2].setY(product[2][1]);

        //fourth point
        x=x2;
        y=y2+dist;

        t[2][0]=x-x2;
        t[2][1]=y-y2;
        tInv[2][0]=x2;
        tInv[2][1]=y2;


        product=multiplyMatrices(multiplyMatrices(t, r), tInv);

        coordinates[3]=new Point();
        coordinates[3].setX(product[2][0]);
        coordinates[3].setY(product[2][1]);





        return coordinates;
    }

    public LinkedHashMap<String, Double> sortHashMapByValues(
            HashMap<String, Double> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Double> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<String, Double> sortedMap =
                new LinkedHashMap<>();

        Iterator<Double> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Double val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Double comp1 = passedMap.get(key);
                Double comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

}