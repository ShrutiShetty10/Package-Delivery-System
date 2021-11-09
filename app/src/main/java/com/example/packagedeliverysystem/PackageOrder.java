package com.example.packagedeliverysystem;

        import java.io.Serializable;
        import java.util.ArrayList;
        import java.util.UUID;

public class PackageOrder implements Serializable {
    private String source, destination, name, number, length, width, height, weight, user, email;
    private final String uniqueID;
    private String date;
    private ArrayList<String> places;
    private int currentSlot;
    private ArrayList<String> warehouseList;
    private String pin;
    private boolean currentlyWithDeliveryPerson;
    String vid1, vid2;

    String userEmail;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    Point sourceCoordinates;
    Point destCoordinates;
    Point sourceWarehouseCoordinates;
    Point destWarehouseCoordinates;

    public Point getSourceCoordinates() {
        return sourceCoordinates;
    }

    public void setSourceCoordinates(Point sourceCoordinates) {
        this.sourceCoordinates = sourceCoordinates;
    }

    public Point getDestCoordinates() {
        return destCoordinates;
    }

    public void setDestCoordinates(Point destCoordinates) {
        this.destCoordinates = destCoordinates;
    }

    public Point getSourceWarehouseCoordinates() {
        return sourceWarehouseCoordinates;
    }

    public void setSourceWarehouseCoordinates(Point sourceWarehouseCoordinates) {
        this.sourceWarehouseCoordinates = sourceWarehouseCoordinates;
    }

    public Point getDestWarehouseCoordinates() {
        return destWarehouseCoordinates;
    }

    public void setDestWarehouseCoordinates(Point destWarehouseCoordinates) {
        this.destWarehouseCoordinates = destWarehouseCoordinates;
    }

    public String getVid1() {
        return vid1;
    }

    public void setVid1(String vid1) {
        this.vid1 = vid1;
    }

    public String getVid2() {
        return vid2;
    }

    public void setVid2(String vid2) {
        this.vid2 = vid2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isCurrentlyWithDeliveryPerson() {
        return currentlyWithDeliveryPerson;
    }

    public void setCurrentlyWithDeliveryPerson(boolean currentlyWithDeliveryPerson) {
        this.currentlyWithDeliveryPerson = currentlyWithDeliveryPerson;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public ArrayList<String> getWarehouseList() {
        return warehouseList;
    }

    public void setWarehouseList(ArrayList<String> warehouseList) {
        this.warehouseList = warehouseList;
    }

    public int getCurrentSlot() {
        return currentSlot;
    }

    public void setCurrentSlot(int currentSlot) {
        this.currentSlot = currentSlot;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGoingTo() {
        return goingTo;
    }

    public void setGoingTo(String goingTo) {
        this.goingTo = goingTo;
    }

    private boolean isFragile;
    String currentlyWith;
    String goingTo;

    public String getCurrentlyWith() {
        return currentlyWith;
    }

    public void setCurrentlyWith(String currentlyWith) {
        this.currentlyWith = currentlyWith;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    PackageOrder(){
        uniqueID= UUID.randomUUID().toString();
        places=new ArrayList<String>();

    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public ArrayList<String> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<String> places) {
        this.places = places;
    }

    public void addPlace(String s){
        places.add(s);
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public boolean isFragile() {
        return isFragile;
    }

    public void setFragile(boolean fragile) {
        isFragile = fragile;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}

