package com.example.chaitanya.lostb;

public class ChatModel {
    private String Email;
    private String Message;

    public ChatModel() {
    }

    public ChatModel(String email, String message) {
        Email = email;
        Message = message;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
