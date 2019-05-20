package com.example.chaitanya.lostb;

public class GeoPostMarkModel {
    private String id;
    private String lostId;
    private String userId;

    public GeoPostMarkModel() {
    }

    public GeoPostMarkModel(String id, String lostId, String userId) {
        this.id = id;
        this.lostId = lostId;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLostId() {
        return lostId;
    }

    public void setLostId(String lostId) {
        this.lostId = lostId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
