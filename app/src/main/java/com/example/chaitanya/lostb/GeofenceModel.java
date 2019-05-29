package com.example.chaitanya.lostb;

//Model class for location reminder
public class GeofenceModel {

    private String PlaceId;
    private String Id;

    public GeofenceModel() {
    }

    public GeofenceModel(String placeId, String id) {
        PlaceId = placeId;
        Id = id;
    }

    public String getPlaceId() {
        return PlaceId;
    }

    public void setPlaceId(String placeId) {
        PlaceId = placeId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
