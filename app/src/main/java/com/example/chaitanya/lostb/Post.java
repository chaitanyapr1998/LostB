package com.example.chaitanya.lostb;

public class Post {
    private String Title;
    private String Date;
    private String Location;

    public Post() {
    }

    public Post(String title, String date, String location) {
        Title = title;
        Date = date;
        Location = location;
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
}
