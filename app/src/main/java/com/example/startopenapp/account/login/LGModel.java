package com.example.startopenapp.account.login;

import android.text.TextUtils;
import android.util.Patterns;

public class LGModel {
    private String numberphone;
    private String pass;
    private String stdid;

    public LGModel(String numberphone, String pass, String stdid) {
        this.numberphone = numberphone;
        this.pass = pass;
        this.stdid = stdid;
    }

    public String getNumberphone() {
        return numberphone;
    }

    public void setNumberphone(String numberphone) {
        this.numberphone = numberphone;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getStdid() {
        return stdid;
    }

    public void setStdid(String stdid) {
        this.stdid = stdid;
    }

    public boolean isValidNumberPhone(){
        return !TextUtils.isEmpty(numberphone) && Patterns.PHONE.matcher(numberphone).matches()
                && numberphone.length() == 10 && numberphone.startsWith("0");
    }

    public boolean isValidPassword(){
        return !TextUtils.isEmpty(pass) && pass.trim().length() >= 6;
    }
    public boolean isValidStdID(){
        return !TextUtils.isEmpty(stdid);
    }
}
