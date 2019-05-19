package com.example.chaitanya.lostb;

public class LocationModel {
    private String Time;
    private String Address;
    private String Uid;
    private String Country;
    private String Street;
    private String Key;

    public LocationModel() {
    }

    public LocationModel(String time, String address, String uid, String country, String street, String key) {
        Time = time;
        Address = address;
        Uid = uid;
        Country = country;
        Street = street;
        Key = key;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String street) {
        Street = street;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }
}
