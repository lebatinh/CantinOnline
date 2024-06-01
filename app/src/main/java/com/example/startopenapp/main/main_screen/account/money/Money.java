package com.example.startopenapp.main.main_screen.account.money;

public class Money {
    String id;
    String name;
    String sodu;

    public Money(String id, String name, String sodu) {
        this.id = id;
        this.name = name;
        this.sodu = sodu;
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

    public String getSodu() {
        return sodu;
    }

    public void setSodu(String sodu) {
        this.sodu = sodu;
    }
}
