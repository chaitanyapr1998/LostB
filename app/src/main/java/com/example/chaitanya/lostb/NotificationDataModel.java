package com.example.chaitanya.lostb;

//Model class for sending caht notification
public class NotificationDataModel {
    private String user;
    private String title;
    private String msg;
    private String sent;
    private String toEmail;

    public NotificationDataModel() {
    }

    public NotificationDataModel(String user, String title, String msg, String sent, String toEmail) {
        this.user = user;
        this.title = title;
        this.msg = msg;
        this.sent = sent;
        this.toEmail = toEmail;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }
}
