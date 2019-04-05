package com.example.chaitanya.lostb;

public class Post {
    private String Title;
    private String Date;
    private String Location;
    private String Id;
    private String Email;
    private String Category;
    private String UserId;
    private String Description;
    private String Address;
    private String Latitude;
    private String Longitude;
    private String PostedDate;

    public Post() {
    }

    public Post(String title, String date, String location, String id, String email, String category, String userId, String description, String address, String latitude, String longitude, String postedDate) {
        Title = title;
        Date = date;
        Location = location;
        Id = id;
        Email = email;
        Category = category;
        UserId = userId;
        Description = description;
        Address = address;
        Latitude = latitude;
        Longitude = longitude;
        PostedDate = postedDate;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getPostedDate() {
        return PostedDate;
    }

    public void setPostedDate(String postedDate) {
        PostedDate = postedDate;
    }
}
