package com.example.startopenapp.main.home.order.historyorder.model;

import com.example.startopenapp.main.home.order.Item;

import java.util.List;

public class Orders {
    private String orderId;
    private String orderPrice,orderContent, orderPayment, orderTime;

    public Orders(String orderId, String orderPrice, String orderContent, String orderPayment, String orderTime) {
        this.orderId = orderId;
        this.orderPrice = orderPrice;
        this.orderContent = orderContent;
        this.orderPayment = orderPayment;
        this.orderTime = orderTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderContent() {
        return orderContent;
    }

    public void setOrderContent(String orderContent) {
        this.orderContent = orderContent;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getOrderPayment() {
        return orderPayment;
    }

    public void setOrderPayment(String orderPayment) {
        this.orderPayment = orderPayment;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
}
