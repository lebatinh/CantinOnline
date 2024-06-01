package com.example.startopenapp.admin.push_noti.person_list;

import android.util.Base64;

public class Person {
    private String id;
    private byte[] avt;
    private String name;
    private boolean isSelected;

    public Person(String id, byte[] avt, String name) {
        this.id = id;
        this.avt = avt;
        this.name = name;
        this.isSelected = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    // phương thức này trả về chuỗi Base64 của hình ảnh
    public String getHinhBase64() {
        if (avt != null && avt.length > 0) {
            return Base64.encodeToString(avt, Base64.DEFAULT);
        } else {
            return null; // hoặc trả về một giá trị mặc định nếu hình ảnh là null hoặc rỗng
        }
    }

    // phương thức để thiết lập hình ảnh từ chuỗi Base64
    public void setHinhBase64(String base64String) {
        if (base64String != null && !base64String.isEmpty()) {
            avt = Base64.decode(base64String, Base64.DEFAULT);
        }
    }
}
