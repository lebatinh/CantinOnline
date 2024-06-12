package com.example.startopenapp.admin.cashier;

public class Bill {
    private String oderId,accId ,content, price, receiver, phone, paymentmethod, time;

    public Bill(String oderId, String accId, String content, String price, String receiver, String phone, String paymentmethod, String time) {
        this.oderId = oderId;
        this.accId = accId;
        this.content = content;
        this.price = price;
        this.receiver = receiver;
        this.phone = phone;
        this.paymentmethod = paymentmethod;
        this.time = time;
    }

    public String getOderId() {
        return oderId;
    }

    public void setOderId(String oderId) {
        this.oderId = oderId;
    }

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPaymentmethod() {
        return paymentmethod;
    }

    public void setPaymentmethod(String paymentmethod) {
        this.paymentmethod = paymentmethod;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
