package com.example.chaitanya.lostb;

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
