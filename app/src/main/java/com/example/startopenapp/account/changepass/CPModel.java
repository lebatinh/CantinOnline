package com.example.startopenapp.account.changepass;

import android.text.TextUtils;
import android.util.Patterns;
public class CPModel {
    private String numberphone;
    private String oldpass;
    private String stdid;
    private String newpass;
    private String renewpass;

    public CPModel(String numberphone, String oldpass, String stdid, String newpass, String renewpass) {
        this.numberphone = numberphone;
        this.oldpass = oldpass;
        this.stdid = stdid;
        this.newpass = newpass;
        this.renewpass = renewpass;
    }

    public String getNumberphone() {
        return numberphone;
    }

    public void setNumberphone(String numberphone) {
        this.numberphone = numberphone;
    }

    public String getOldpass() {
        return oldpass;
    }

    public void setOldpass(String oldpass) {
        this.oldpass = oldpass;
    }

    public String getStdid() {
        return stdid;
    }

    public void setStdid(String stdid) {
        this.stdid = stdid;
    }

    public String getNewpass() {
        return newpass;
    }

    public void setNewpass(String newpass) {
        this.newpass = newpass;
    }

    public String getRenewpass() {
        return renewpass;
    }

    public void setRenewpass(String renewpass) {
        this.renewpass = renewpass;
    }

    public boolean isValidPhone(){
        return !TextUtils.isEmpty(numberphone) && Patterns.PHONE.matcher(numberphone).matches()
                && numberphone.length() == 10 && numberphone.startsWith("0");
    }
    public boolean isValidOldPass(){
        return !TextUtils.isEmpty(oldpass) && oldpass.trim().length() >= 6;
    }
    public boolean isValidStdID(){
        return !TextUtils.isEmpty(stdid);
    }
    public boolean isValidNewPass(){
        return !TextUtils.isEmpty(newpass) && newpass.trim().length() >= 6;
    }
    public boolean isValidReNewPass(){
        return !TextUtils.isEmpty(renewpass) &&
                newpass.trim().toString().equals(renewpass.trim().toString());
    }
}
