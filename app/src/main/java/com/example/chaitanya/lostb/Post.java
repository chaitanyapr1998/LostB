package com.example.chaitanya.lostb;

public class Post {
    private String mTitle;
    private String mDate;
    private String mLoc;

    public Post() {
    }

    public Post(String mTitle, String mDate, String mLoc) {
        this.mTitle = mTitle;
        this.mDate = mDate;
        this.mLoc = mLoc;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmLoc() {
        return mLoc;
    }
}
