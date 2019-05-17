package com.example.chaitanya.lostb;

public class NData {
    private String user;
    private String title;
    private String msg;
    private String sent;

    public NData() {
    }

    public NData(String user, String title, String msg, String sent) {
        this.user = user;
        this.title = title;
        this.msg = msg;
        this.sent = sent;
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
}
