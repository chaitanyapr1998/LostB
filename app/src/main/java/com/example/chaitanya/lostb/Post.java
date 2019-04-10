package com.example.chaitanya.lostb;

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

    public Post() {
    }

    public Post(String Title, String Date, String Location, String Id, String Email, String Category, String UserId, String Description, String Address, String Latitude, String Longitude, String PostedDate, String Country) {
        title = Title;
        date = Date;
        location = Location;
        id = Id;
        email = Email;
        category = Category;
        userId = UserId;
        description = Description;
        address = Address;
        latitude = Latitude;
        longitude = Longitude;
        postedDate = PostedDate;
        country = Country;
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
}
