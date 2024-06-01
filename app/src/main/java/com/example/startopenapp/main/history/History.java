package com.example.startopenapp.main.history;
public class History {
    private String itemType,itemReceiver, itemTime, itemStatus, itemSotienGD;

    public History(String itemType, String itemReceiver, String itemTime, String itemStatus, String itemSotienGD) {
        this.itemType = itemType;
        this.itemReceiver = itemReceiver;
        this.itemTime = itemTime;
        this.itemStatus = itemStatus;
        this.itemSotienGD = itemSotienGD;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemReceiver() {
        return itemReceiver;
    }

    public void setItemReceiver(String itemReceiver) {
        this.itemReceiver = itemReceiver;
    }

    public String getItemTime() {
        return itemTime;
    }

    public void setItemTime(String itemTime) {
        this.itemTime = itemTime;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getItemSotienGD() {
        return itemSotienGD;
    }

    public void setItemSotienGD(String itemSotienGD) {
        this.itemSotienGD = itemSotienGD;
    }
}
