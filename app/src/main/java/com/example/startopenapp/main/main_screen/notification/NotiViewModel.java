package com.example.startopenapp.main.main_screen.notification;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotiViewModel extends ViewModel {
    private MutableLiveData<String> selectedTitle = new MutableLiveData<>();
    private MutableLiveData<String> typeNoti = new MutableLiveData<>();
    private MutableLiveData<String> title = new MutableLiveData<>();
    private MutableLiveData<String> notification = new MutableLiveData<>();
    private MutableLiveData<String> time = new MutableLiveData<>();
    private MutableLiveData<String> receiver = new MutableLiveData<>();

    public MutableLiveData<String> getTypeNoti() {
        return typeNoti;
    }

    public void setTypeNoti(MutableLiveData<String> typeNoti) {
        this.typeNoti = typeNoti;
    }

    public MutableLiveData<String> getTitle() {
        return title;
    }

    public void setTitle(MutableLiveData<String> title) {
        this.title = title;
    }

    public MutableLiveData<String> getNotification() {
        return notification;
    }

    public void setNotification(MutableLiveData<String> notification) {
        this.notification = notification;
    }

    public MutableLiveData<String> getTime() {
        return time;
    }

    public void setTime(MutableLiveData<String> time) {
        this.time = time;
    }

    public MutableLiveData<String> getReceiver() {
        return receiver;
    }

    public void setReceiver(MutableLiveData<String> receiver) {
        this.receiver = receiver;
    }

    public MutableLiveData<String> getSelectedTitle() {
        return selectedTitle;
    }

    public void setSelectedTitle(String title) {
        selectedTitle.setValue(title);
    }


}
