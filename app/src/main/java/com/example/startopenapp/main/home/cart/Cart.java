package com.example.startopenapp.main.home.cart;

import android.util.Base64;

public class Cart {
    private byte[] itemImage;
    private String itemName, itemId, itemPrice, itemAmount;

    public Cart(byte[] itemImage, String itemName, String itemId, String itemPrice, String itemAmount) {
        this.itemImage = itemImage;
        this.itemName = itemName;
        this.itemId = itemId;
        this.itemPrice = itemPrice;
        this.itemAmount = itemAmount;
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

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(String itemAmount) {
        this.itemAmount = itemAmount;
    }

    public String getHinhBase64Product() {
        if (itemImage != null && itemImage.length > 0) {
            return Base64.encodeToString(itemImage, Base64.DEFAULT);
        } else {
            return null; // hoặc trả về một giá trị mặc định nếu hình ảnh là null hoặc rỗng
        }
    }
}
