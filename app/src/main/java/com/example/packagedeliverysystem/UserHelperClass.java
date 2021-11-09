package com.example.packagedeliverysystem;

public class UserHelperClass {

    String fullName, email, userName, password, address, resAddress, aadhaarNumber, phoneNumber;

    public UserHelperClass(String fullName, String email, String userName, String password, String address, String resAddress, String aadhaarNumber, String phoneNumber) {
        this.fullName = fullName;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.address = address;
        this.resAddress = resAddress;
        this.aadhaarNumber = aadhaarNumber;
        this.phoneNumber = phoneNumber;
    }

    public UserHelperClass(String fullName, String email, String userName, String password, String address, String aadhaarNumber, String phoneNumber) {
        this.fullName = fullName;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.address = address;
        this.aadhaarNumber = aadhaarNumber;
        this.phoneNumber = phoneNumber;
    }

    public UserHelperClass(String fullName, String email, String userName, String password, String address, String phoneNumber) {
        this.fullName = fullName;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getResAddress() {
        return resAddress;
    }

    public void setResAddress(String resAddress) {
        this.resAddress = resAddress;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}