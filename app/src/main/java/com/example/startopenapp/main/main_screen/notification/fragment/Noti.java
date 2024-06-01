package com.example.startopenapp.main.main_screen.notification.fragment;

import android.util.Base64;

public class Noti {
    private String title;
    private String notification;
    private String time;

    public Noti(String title, String notification, String time) {
        this.title = title;
        this.notification = notification;
        this.time = time;
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
}
