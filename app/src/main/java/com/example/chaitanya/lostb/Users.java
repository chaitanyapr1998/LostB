package com.example.chaitanya.lostb;

public class Users {
    private String Email;
    private String UserId;
    private String Link;

    public Users() {
    }

    public Users(String email, String userId, String link) {
        Email = email;
        UserId = userId;
        Link = link;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }
}
