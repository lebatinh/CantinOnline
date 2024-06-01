package com.example.startopenapp.main.main_screen.notification;

import android.text.TextUtils;
import android.widget.ImageView;

public class NotiModel {
    private ImageView image;
    private String typenoti;
    private String title;
    private String notification;
    private String time;
    private String receiver;

    public NotiModel(ImageView image, String typenoti, String title, String notification, String time,
                     String receiver) {
        this.image = image;
        this.typenoti = typenoti;
        this.title = title;
        this.notification = notification;
        this.time = time;
        this.receiver = receiver;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public String getTypenoti() {
        return typenoti;
    }

    public void setTypenoti(String typenoti) {
        this.typenoti = typenoti;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    boolean isValidNotification(){
        return !TextUtils.isEmpty(typenoti) && !TextUtils.isEmpty(title) && !TextUtils.isEmpty(notification)
                && !TextUtils.isEmpty(time);
    }
}
