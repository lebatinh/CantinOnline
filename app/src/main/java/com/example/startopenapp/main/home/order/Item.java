package com.example.startopenapp.main.home.order;

import android.util.Base64;

public class Item {
    private byte[] imageOder;
    private String itemId, itemName, itemQuantity;
    private Integer itemPrice;

    public Item(byte[] imageOder, String itemId, String itemName, String itemQuantity, Integer itemPrice) {
        this.imageOder = imageOder;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        this.itemPrice = itemPrice;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public Integer getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Integer itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getHinhBase64Product() {
        if (imageOder != null && imageOder.length > 0) {
            return Base64.encodeToString(imageOder, Base64.DEFAULT);
        } else {
            return null; // hoặc trả về một giá trị mặc định nếu hình ảnh là null hoặc rỗng
        }
    }
}
