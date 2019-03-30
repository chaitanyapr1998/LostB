package com.example.chaitanya.lostb;

public class Post {
    private String Title;
    private String Date;
    private String Location;
    private String Id;
    private String Email;
    private String Category;
    private String UserId;

    public Post() {
    }

    public Post(String title, String date, String location, String id, String email, String category, String userId) {
        Title = title;
        Date = date;
        Location = location;
        Id = id;
        Email = email;
        Category = category;
        UserId = userId;
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
}
