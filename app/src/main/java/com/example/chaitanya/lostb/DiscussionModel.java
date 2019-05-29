package com.example.chaitanya.lostb;

//Model class for discussions
public class DiscussionModel {
    private String id;
    private String emailId;
    private String msg;
    private String postedDate;

    public DiscussionModel() {
    }

    public DiscussionModel(String id, String emailId, String msg, String postedDate) {
        this.id = id;
        this.emailId = emailId;
        this.msg = msg;
        this.postedDate = postedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }
}
