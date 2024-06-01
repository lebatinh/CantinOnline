package com.example.startopenapp.main.main_screen.account.money;

public class Info {
    String tiennap;
    String trangthai;
    String timetao;
    String noidung;

    public Info(String tiennap, String trangthai, String timetao, String noidung) {
        this.tiennap = tiennap;
        this.trangthai = trangthai;
        this.timetao = timetao;
        this.noidung = noidung;
    }

    public String getTiennap() {
        return tiennap;
    }

    public void setTiennap(String tiennap) {
        this.tiennap = tiennap;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }

    public String getTimetao() {
        return timetao;
    }

    public void setTimetao(String timetao) {
        this.timetao = timetao;
    }

    public String getNoidung() {
        return noidung;
    }

    public void setNoidung(String noidung) {
        this.noidung = noidung;
    }
}
