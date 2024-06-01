package com.example.startopenapp.admin.food;

import android.util.Base64;

public class Food {
    private byte[] itemImage;
    private String itemId, itemName, itemDescription, itemPrice;

    public Food(byte[] itemImage, String itemId, String itemName, String itemDescription, String itemPrice) {
        this.itemImage = itemImage;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
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

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getHinhBase64Product() {
        if (itemImage != null && itemImage.length > 0) {
            return Base64.encodeToString(itemImage, Base64.DEFAULT);
        } else {
            return null; // hoặc trả về một giá trị mặc định nếu hình ảnh là null hoặc rỗng
        }
    }
}
