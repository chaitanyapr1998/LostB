package com.example.chaitanya.lostb;

//Model class for chat data
public class ChatModel {
    private String from;
    private String msg;
    private String to;

    public ChatModel() {
    }

    public ChatModel(String from, String msg, String to) {
        this.from = from;
        this.msg = msg;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
