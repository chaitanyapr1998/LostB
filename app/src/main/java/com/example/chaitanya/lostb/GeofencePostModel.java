package com.example.chaitanya.lostb;

//Model class for geofence post reminder
public class GeofencePostModel {

    private String placeId;
    private String lat;
    private String lon;

    public GeofencePostModel() {
    }

    public GeofencePostModel(String placeId, String lat, String lon) {
        this.placeId = placeId;
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

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
