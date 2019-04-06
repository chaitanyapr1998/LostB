package com.example.chaitanya.lostb;

public class Users {
    private String Email;
    private String UserId;

    public Users() {
    }

    public Users(String email, String userId) {
        Email = email;
        UserId = userId;
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
}
