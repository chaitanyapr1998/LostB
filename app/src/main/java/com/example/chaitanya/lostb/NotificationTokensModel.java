package com.example.chaitanya.lostb;

//Model class for notification token
public class NotificationTokensModel {

    private String Token;

    public NotificationTokensModel() {
    }

    public NotificationTokensModel(String token) {
        Token = token;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
