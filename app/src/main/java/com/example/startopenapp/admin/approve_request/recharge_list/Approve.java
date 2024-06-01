package com.example.startopenapp.admin.approve_request.recharge_list;

public class Approve {
    private String accid;
    private String madon;
    private String sotien;
    private String time;
    private String loinhan;

    public Approve(String accid, String madon, String sotien, String time, String loinhan) {
        this.accid = accid;
        this.madon = madon;
        this.sotien = sotien;
        this.time = time;
        this.loinhan = loinhan;
    }

    public String getAccid() {
        return accid;
    }

    public void setAccid(String accid) {
        this.accid = accid;
    }

    public String getMadon() {
        return madon;
    }

    public void setMadon(String madon) {
        this.madon = madon;
    }

    public String getSotien() {
        return sotien;
    }

    public void setSotien(String sotien) {
        this.sotien = sotien;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLoinhan() {
        return loinhan;
    }

    public void setLoinhan(String loinhan) {
        this.loinhan = loinhan;
    }
}
