package com.example.packagedeliverysystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class DeliveryHome extends AppCompatActivity {

    String username;
    int maxPackages;

    ListView listView;
    TextView textViewTitle;
    EditText editTextSource;
    EditText editTextDestination;
    EditText editTextVehicle;
    TextView textViewPackages;
    Button buttonCheck;
    ProgressBar progressBarMain;
    ProgressBar progressBarCheck;
    SeekBar seekBar;
    Button buttonExit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_home);
        Intent intent=getIntent();
        username=intent.getStringExtra("username");
        maxPackages=3;

        listView=findViewById(R.id.pathList);
        textViewTitle=findViewById(R.id.textView5);
        editTextSource=findViewById(R.id.editTextSource);
        editTextDestination=findViewById(R.id.editTextDestination);
        editTextVehicle=findViewById(R.id.editTextVehicle);
        textViewPackages=findViewById(R.id.textView6);
        buttonCheck=findViewById(R.id.buttonCheck);
        progressBarCheck=findViewById(R.id.progressBarCheck);
        progressBarMain=findViewById(R.id.progressBarMain);
        seekBar = findViewById(R.id.seekBar3);
        buttonExit=findViewById(R.id.buttonEnd);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                String text="Maximum Packages : "+(i+1);
                textViewPackages.setText(text);
                maxPackages=(i+1);
                Log.d("tag", "max packages is "+(i+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("TAG", String.valueOf(i));
                int n=i/2;

                if(i%2==0){
                    if(n<visited.size()&&visited.get(n))
                        Toast.makeText(DeliveryHome.this, "Task Completed", Toast.LENGTH_SHORT).show();
                    else if(n==0||visited.get(n-1)) {
                        Log.d("tag", "openMap");

                        Point s=coordinates.get(n);
                        Point d=coordinates.get(n+1);

                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        intent.putExtra("source", s);
                        intent.putExtra("destination", d);
                        startActivity(intent);

                    }else
                        Toast.makeText(DeliveryHome.this, "Finish previous task", Toast.LENGTH_SHORT).show();
                }else{
                    if(visited.get(n))
                        Toast.makeText(DeliveryHome.this, "Task Completed", Toast.LENGTH_SHORT).show();
                    else if(n==0||visited.get(n-1)){
                        Intent intent=new Intent(getApplicationContext(), deliveryScan.class);
                        intent.putExtra("inst", instruction.get(n));
                        intent.putExtra("qr", codes.get(n));
                        intent.putExtra("name", warehouse.get(n));
                        startActivity(intent);
                    }else
                        Toast.makeText(DeliveryHome.this, "Finish previous task", Toast.LENGTH_SHORT).show();
                }


            }
        });

        loadData();
    }

    String id;

    private void loadData() {



        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference("Deliveries");

        reference.child(username).child("State").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String data=snapshot.getValue(String.class);
                if(data.equals("Waiting")){
                    id="";
                    loadWaiting();
                }
                else{
                    id=data;
                    loadDelivery();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadWaiting() {
        listView.setVisibility(View.GONE);
        buttonExit.setVisibility(View.GONE);


        textViewTitle.setVisibility(View.VISIBLE);
        editTextSource.setVisibility(View.VISIBLE);
        editTextDestination.setVisibility(View.VISIBLE);
        editTextVehicle.setVisibility(View.VISIBLE);
        textViewPackages.setVisibility(View.VISIBLE);
        seekBar.setVisibility(View.VISIBLE);
        buttonCheck.setVisibility(View.VISIBLE);

        progressBarCheck.setVisibility(View.GONE);
        progressBarMain.setVisibility(View.GONE);
    }


    private void loadDelivery() {
        textViewTitle.setVisibility(View.GONE);
        editTextSource.setVisibility(View.GONE);
        editTextDestination.setVisibility(View.GONE);
        editTextVehicle.setVisibility(View.GONE);
        textViewPackages.setVisibility(View.GONE);
        seekBar.setVisibility(View.GONE);
        buttonCheck.setVisibility(View.GONE);
        progressBarCheck.setVisibility(View.GONE);
        buttonExit.setVisibility(View.GONE);

        progressBarMain.setVisibility(View.VISIBLE);


        loadWarehouses();
    }

    ArrayList<String> text;
    ArrayList<String> instruction;
    ArrayList<String> codes;
    ArrayList<String> warehouse;
    ArrayList<Boolean> visited;
    ArrayList<Point> coordinates;

    private void loadWarehouses() {
            FirebaseDatabase database=FirebaseDatabase.getInstance();
            DatabaseReference reference=database.getReference("DeliveryDetails");
            reference.child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        text = new ArrayList<>();
                        instruction = new ArrayList<>();
                        codes = new ArrayList<>();
                        warehouse = new ArrayList<>();
                        visited = new ArrayList<>();
                        coordinates=new ArrayList<>();
                        String temp = snapshot.child("Path").getValue(String.class);
                        d=snapshot.child("destCoordinates").getValue(Point.class);
                        s=snapshot.child("sourceCoordinates").getValue(Point.class);


                        temp = temp.substring(0, temp.length() - 1);


                        String[] warehouses = temp.split(",");
                        for (String wh : warehouses) {
                            Log.d("tag", wh);


                            if (snapshot.child("WarehouseList").child(wh).child("Visited").getValue(Boolean.class) != null) {




                                warehouse.add(wh);
                                visited.add(snapshot.child("WarehouseList").child(wh).child("Visited").getValue(Boolean.class));
                                Log.d("tag", "vistied " + wh + " " + snapshot.child("WarehouseList").child(wh).child("Visited").getValue(Boolean.class));
                                int n = 1;
                                String inst = "";
                                String qr = "";

                                qr += snapshot.child("WarehouseList").child(wh).child("Code").getValue(String.class);

                                for (DataSnapshot incoming : snapshot.child("WarehouseList").child(wh).child("Incoming").getChildren()) {
                                    inst += (n++) + ". Deliver " + incoming.getValue(String.class).substring(0, 4) + " to slot " + incoming.getKey() + "\n";
                                    qr += " " + incoming.getValue(String.class);
                                }

                                for (DataSnapshot outgoing : snapshot.child("WarehouseList").child(wh).child("Outgoing").getChildren()) {
                                    inst += (n++) + ". Collect " + outgoing.getValue(String.class).substring(0, 4) + " from slot " + outgoing.getKey() + "\n";
                                    qr += " " + outgoing.getValue(String.class);
                                }


                                instruction.add(inst);
                                codes.add(qr);
                            }


                        }


                        Log.d("tag", "visited lenght " + visited.size());

                        int length = warehouse.size();
                        text.add("Source to "+warehouse.get(0));
                        for (int i = 0; i < length - 1; i++) {
                            String s = warehouse.get(i);
                            if (visited.get(i))
                                s += " - Visited";

                            text.add(s);
                            s = warehouse.get(i) + " to " + warehouse.get(i + 1);
                            text.add(s);
                        }
                        String s = warehouse.get(length - 1);
                        if (visited.get(length - 1))
                            s += " - Visited";

                        text.add(s);
                        text.add(warehouse.get(warehouse.size()-1)+" to Destination");

                        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, text);

                        boolean flag = false;
                        for (boolean b : visited)
                            if (!b) {
                                flag = true;
                                break;
                            }

                        if (!flag)
                            buttonExit.setVisibility(View.VISIBLE);


                        listView.setAdapter(adapter);
                        n1=0;
                        loadCoordinates();


                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }
    Point s, d;

    int n1;
    private void loadCoordinates() {
        if (n1==warehouse.size()){
            listView.setVisibility(View.VISIBLE);
            coordinates.add(0, s);
            coordinates.add(d);

            progressBarMain.setVisibility(View.GONE);
            return;
        }

        FirebaseDatabase database1=FirebaseDatabase.getInstance();
        DatabaseReference reference1=database1.getReference("WarehouseCoordinates");
        reference1.child(warehouse.get(n1)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Point p=snapshot.getValue(Point.class);
                coordinates.add(p);
                n1++;
                loadCoordinates();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    public void Check(View view) {

        boolean canContinue = true;
        if (!containsText(editTextSource))
            canContinue = false;
        if (!containsText(editTextDestination))
            canContinue = false;
        if (!containsText(editTextVehicle))
            canContinue = false;

        if (canContinue) {


            buttonCheck.setEnabled(false);
            progressBarCheck.setVisibility(View.VISIBLE);

            locateLatAndLon(editTextSource.getText().toString(), editTextDestination.getText().toString());

            

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
                                            Toast.makeText(getApplicationContext(), "Source Address may be incorrect", Toast.LENGTH_SHORT).show();
                                            checkWarehouse(new ArrayList<>());
                                            return;
                                        }
                                        if(latDest==0||lonDest==0){
                                            Toast.makeText(getApplicationContext(), "Destination Address may be incorrect", Toast.LENGTH_SHORT).show();
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

    Point src, dest;

    void algorithm(){
        Log.d("TAG", "startAlgorithm");
        //if there is no warehouse near source or destination return an empty array list
        ArrayList<String> arrayList = new ArrayList<>();
        //coordinates of source warehouse and dest warehouse
        /*
        double latSW=0, lonSW=0, latDW=0, lonDW=0;

        //get latsw , lonsw


        double distS=multiplier*1;//dist variable


        Point pointS=new Point();
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
            Toast.makeText(this, "There was an error generating path", Toast.LENGTH_SHORT).show();
            checkWarehouse(arrayList);
            return;
        }

        Log.d("TAG", nameS+" "+distS);

        //get latdw , londw


        double distD=multiplier*1;//dist variable


        Point pointD=new Point();
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
            Toast.makeText(this, "There was an error generating path", Toast.LENGTH_SHORT).show();
            checkWarehouse(arrayList);
            return;
        }

        Log.d("TAG", nameD+" "+distD);

        if(nameS.equals(nameD)){
            Toast.makeText(this, "Distance is very small", Toast.LENGTH_SHORT).show();
            checkWarehouse(arrayList);
            return;
        }
        */


        //actual code
        //arrayList.add(nameS);

        src=new Point();
        src.setX(latSource);
        src.setY(lonSource);

        dest=new Point();
        dest.setX(latDest);
        dest.setY(lonDest);


        //dist
        double dist;

        Log.d("tag", "source and dest pts");
        src.printPoint();
        dest.printPoint();
        Log.d("tag", "source and dest pts");

        if(latSource==latDest&&lonSource==lonDest){
            checkWarehouse(arrayList);
            return;
        }



        if(src.dist(dest)<=multiplier*100)
            dist=multiplier*1;
        else if(src.dist(dest)<=multiplier*300)
            dist=multiplier*3;
        else//long
            dist=multiplier*5;



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

            if(p.isInside(a,b,c,d)){

                hashMap.put(name, p.dist(src));
            }

        }
        Log.d("TAG", "Size of hash map "+String.valueOf(hashMap.size()));
        hashMap=sortHashMapByValues(hashMap);
        for(String s: hashMap.keySet())
            arrayList.add(s);




        //arrayList.add(nameD);
        Log.d("tag", "List of warehouses");
        for(String s: arrayList)
            Log.d("tag", s);
        checkWarehouse(arrayList);


    }



    void checkWarehouse(ArrayList<String> warehouseList) {

        if (warehouseList.size() <2) {
            Toast.makeText(this, "There was an error generating path", Toast.LENGTH_SHORT).show();
            buttonCheck.setEnabled(true);
            progressBarCheck.setVisibility(View.GONE);
            return;
        }

        Log.d("tag", "Valid");
        warehouseListSize=warehouseList.size();
        n=0;
        warehouseListFinal=warehouseList;
        queues=new ArrayList<>();
        loadQueues();









    }

    ArrayList<String> warehouseListFinal;

    ArrayList<String[]> queues;
    int warehouseListSize;
    int n;

    ArrayList<String> packages;
    HashMap<String, ArrayList<String>> packagePaths;

    private void loadQueues() {
        if(n==warehouseListSize){
            Log.d("tag","Queues loaded");
            packagePaths=new HashMap<>();
            packages=new ArrayList<>();
            for(String[] queue: queues){
                for(String id: queue){
                    if(!packages.contains(id))
                        packages.add(id);
                }
            }
            n=0;

            loadPackagePaths();

            return;
        }


        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference("Warehouses");
        reference.child(warehouseListFinal.get(n)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                n++;

                if(snapshot.hasChild("Queue")){
                    String queue=snapshot.child("Queue").getValue(String.class);
                    Log.d("tag", queue);
                    queues.add(queue.split(" "));
                }else{
                    Log.d("tag", "no queue");

                    queues.add(new String[0]);
                }

                loadQueues();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }




    private void loadPackagePaths() {
        if(n==packages.size()){

            Log.d("tag", "size of packages is "+packages.size());
            Log.d("tag", "size of packagesPaths is "+packagePaths.size());
            emptySlots=new ArrayList<>();
            slotAssignmentfirst=new ArrayList<>();
            n=0;
            loadEmptySlots();

            return;
        }
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference("Orders");
        reference.child(packages.get(n)).child("warehouseList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<String> arrayList=new ArrayList<>();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    arrayList.add(dataSnapshot.getValue(String.class));
                }

                packagePaths.put(packages.get(n), arrayList);
                n++;

                loadPackagePaths();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    ArrayList<ArrayList<String>> emptySlots;
    ArrayList<HashMap<String, String>> slotAssignmentfirst;

    private void loadEmptySlots() {
        if (n==warehouseListFinal.size()){
            uniqueID= UUID.randomUUID().toString();
            assign=false;
            atLeastOneAssign=false;

            for(ArrayList<String> emptySlot: emptySlots) {
                String temp="";
                for (String s : emptySlot) {
                    temp+=s+" ";
                }
                Log.d("tag", temp);
            }
            n=0;
            warehouseCodes=new ArrayList<>();
            warehouseListFinalAssign=new ArrayList<>();
            for(int i=0; i<warehouseListFinal.size(); i++) {
                warehouseCodes.add(UUID.randomUUID().toString());
                warehouseListFinalAssign.add(false);
            }


            startTheMainAlgorithm();
            return;
        }
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference("Warehouses");
        reference.child(warehouseListFinal.get(n)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                n++;
                ArrayList<String> unoccupied=new ArrayList<>();
                HashMap<String, String> hashMap=new HashMap<>();
                int slots=snapshot.child("Slots").getValue(Integer.class);
                for(int i=1; i<=slots; i++){
                    if(snapshot.child(String.valueOf(i)).child("State").getValue(String.class).equals("Unoccupied")){
                        unoccupied.add(String.valueOf(i));
                    }
                    if(snapshot.child(String.valueOf(i)).child("State").getValue(String.class).equals("Occupied")){
                        hashMap.put(snapshot.child(String.valueOf(i)).child("Code").getValue(String.class), String.valueOf(i));
                    }
                }
                emptySlots.add(unoccupied);
                slotAssignmentfirst.add(hashMap);
                loadEmptySlots();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    ArrayList<Boolean> warehouseListFinalAssign;
    ArrayList<String> warehouseCodes;
    String uniqueID;
    boolean assign;
    boolean atLeastOneAssign;
    int index;
    private void startTheMainAlgorithm() {
        if(n==maxPackages){
            if(!atLeastOneAssign) {
                buttonCheck.setEnabled(true);
                progressBarCheck.setVisibility(View.GONE);
                Toast.makeText(this, "No Packages Found", Toast.LENGTH_SHORT).show();
            }else{

                FirebaseDatabase database=FirebaseDatabase.getInstance();
                DatabaseReference reference=database.getReference("DeliveryDetails");
                reference.child(uniqueID).child("source").setValue(editTextSource.getText().toString());
                reference.child(uniqueID).child("sourceCoordinates").setValue(src);
                reference.child(uniqueID).child("destCoordinates").setValue(dest);
                reference.child(uniqueID).child("destination").setValue(editTextDestination.getText().toString());
                reference.child(uniqueID).child("vehicle").setValue(editTextVehicle.getText().toString());
                reference.child(uniqueID).child("maxPackages").setValue(maxPackages);
                reference.child(uniqueID).child("uniqueID").setValue(uniqueID);
                reference.child(uniqueID).child("username").setValue(username);


                String temp="";
                for(int i=0; i<warehouseListFinal.size(); i++)
                    if(warehouseListFinalAssign.get(i))
                        temp+=warehouseListFinal.get(i)+",";

                reference.child(uniqueID).child("Path").setValue(temp);

                FirebaseDatabase database1=FirebaseDatabase.getInstance();
                DatabaseReference reference1=database1.getReference("Deliveries");
                reference1.child(username).child("State").setValue(uniqueID);




                Log.d("tag", "packages assigned");
                buttonCheck.setEnabled(true);
                id=uniqueID;
                //progressBarCheck.setVisibility(View.GONE);
                loadDelivery();
            }
            return;
        }



        for(int i=0; i<warehouseListFinal.size()-1; i++){
            String[] queue=queues.get(i);
            label: for(String id: queue){
                ArrayList<String> paths=packagePaths.get(id);
                for(int j=(paths.size()-1); j>=0; j--){
                    String w=paths.get(j);
                    index=warehouseListFinal.indexOf(w);
                    if(warehouseListFinal.contains(w)&&(index>i)&&emptySlots.get(index).size()>0){



                        Log.d("tag", "adding "+n);
                        Log.d("tag", id);
                        Log.d("tag", warehouseListFinal.get(i));
                        warehouseListFinalAssign.add(i, true);
                        Log.d("tag", warehouseCodes.get(i));
                        Log.d("tag", warehouseListFinal.get(index));
                        warehouseListFinalAssign.add(index, true);
                        Log.d("tag", warehouseCodes.get(index));

                        String slotAssigned=emptySlots.get(index).get(0);
                        emptySlots.get(index).remove(0);

                        String slotCollected=slotAssignmentfirst.get(i).get(id);

                        Log.d("tag", "slot assigned "+ slotAssigned);
                        Log.d("tag", "slot collected "+slotCollected);


                        Log.d("tag", "i: " + String.valueOf(i));
                        Log.d("tag", "w: " + String.valueOf(index));

                        String tempString="";
                        String[] tempQueue=new String[queue.length-1];

                        int m=0;
                        for(int k=0; k<queue.length; k++)
                            if(!queue[k].equals(id)) {
                                tempQueue[m++] = queue[k];
                                tempString+=queue[k]+" ";
                            }

                        tempString.trim();
                        Log.d("tag", "Updated queue for "+warehouseListFinal.get(i)+" "+ tempString);
                        queues.set(i, tempQueue);

                        assign=true;
                        if(!atLeastOneAssign) {
                            atLeastOneAssign = true;


                        }

                        //update state to outgoing
                        //update queue

                        FirebaseDatabase database1=FirebaseDatabase.getInstance();
                        DatabaseReference reference1=database1.getReference("Warehouses");

                        if(!tempString.equals(""))
                            reference1.child(warehouseListFinal.get(i)).child("Queue").setValue(tempString);
                        else
                            reference1.child(warehouseListFinal.get(i)).child("Queue").removeValue();

                        reference1.child(warehouseListFinal.get(i)).child(slotCollected).child("State").setValue("Outgoing");
                        reference1.child(warehouseListFinal.get(i)).child(slotCollected).child("VerificationID").setValue(warehouseCodes.get(i));

                        reference1.child(warehouseListFinal.get(index)).child(slotAssigned).child("State").setValue("Incoming");
                        reference1.child(warehouseListFinal.get(index)).child(slotAssigned).child("Code").setValue(id);
                        reference1.child(warehouseListFinal.get(index)).child(slotAssigned).child("VerificationID").setValue(warehouseCodes.get(index));

                        FirebaseDatabase database2=FirebaseDatabase.getInstance();
                        DatabaseReference reference2=database2.getReference("Orders");
                        reference2.child(id).child("goingTo").setValue(username);


                        FirebaseDatabase database=FirebaseDatabase.getInstance();
                        DatabaseReference reference=database.getReference("DeliveryDetails");
                        reference.child(uniqueID).child("WarehouseList").child(warehouseListFinal.get(i)).child("Outgoing").child(slotCollected).setValue(id);
                        reference.child(uniqueID).child("WarehouseList").child(warehouseListFinal.get(i)).child("Visited").setValue(false);
                        reference.child(uniqueID).child("WarehouseList").child(warehouseListFinal.get(i)).child("Code").setValue(warehouseCodes.get(i));
                        reference.child(uniqueID).child("WarehouseList").child(warehouseListFinal.get(index)).child("Incoming").child(slotAssigned).setValue(id);
                        reference.child(uniqueID).child("WarehouseList").child(warehouseListFinal.get(index)).child("Visited").setValue(false);
                        reference.child(uniqueID).child("WarehouseList").child(warehouseListFinal.get(index)).child("Code").setValue(warehouseCodes.get(index));




                        break label;
                    }
                }
            }
            if(assign) {
                i = index - 1;
                assign=false;
            }

        }
        n++;
        startTheMainAlgorithm();


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

    public void exit(View view) {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference("Deliveries");
        reference.child(username).child(id).setValue("Completed");
        reference.child(username).child("State").setValue("Waiting");


        loadWaiting();
    }
}