package com.example.chaitanya.lostb;

public class GeofencePostModel {

    private String lat;
    private String lon;

    public GeofencePostModel() {
    }

    public GeofencePostModel(String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
