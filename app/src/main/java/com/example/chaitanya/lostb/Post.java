package com.example.chaitanya.lostb;

//Model class for lost and found items
public class Post {
    private String title;
    private String date;
    private String location;
    private String id;
    private String email;
    private String category;
    private String userId;
    private String description;
    private String address;
    private String latitude;
    private String longitude;
    private String postedDate;
    private String country;
    private String street;
    private String tit_cou_cat;

    public Post() {
    }

    public Post(String title, String date, String location, String id, String email, String category, String userId, String description, String address, String latitude, String longitude, String postedDate, String country, String street, String tit_cou_cat) {
        this.title = title;
        this.date = date;
        this.location = location;
        this.id = id;
        this.email = email;
        this.category = category;
        this.userId = userId;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postedDate = postedDate;
        this.country = country;
        this.street = street;
        this.tit_cou_cat = tit_cou_cat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTit_cou_cat() {
        return tit_cou_cat;
    }

    public void setTit_cou_cat(String tit_cou_cat) {
        this.tit_cou_cat = tit_cou_cat;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
