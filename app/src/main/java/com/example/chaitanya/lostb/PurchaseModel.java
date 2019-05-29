package com.example.chaitanya.lostb;

//Model class for user purchase data
public class PurchaseModel {
    private String userId;
    private String email;
    private String paymentDate;
    private String amount;
    private boolean ads;

    public PurchaseModel() {
    }

    public PurchaseModel(String userId, String email, String paymentDate, String amount, boolean ads) {
        this.userId = userId;
        this.email = email;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.ads = ads;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isAds() {
        return ads;
    }

    public void setAds(boolean ads) {
        this.ads = ads;
    }
}
