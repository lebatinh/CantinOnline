package com.example.startopenapp.account.forgotpass;

import android.text.TextUtils;
import android.util.Patterns;

public class FPModel {
    private String numberphone;
    private String stdId;
    private String newpass;
    private String renewpass;

    public FPModel(String numberphone, String stdId, String newpass, String renewpass) {
        this.numberphone = numberphone;
        this.stdId = stdId;
        this.newpass = newpass;
        this.renewpass = renewpass;
    }

    public String getNumberphone() {
        return numberphone;
    }

    public void setNumberphone(String numberphone) {
        this.numberphone = numberphone;
    }

    public String getStdId() {
        return stdId;
    }

    public void setStdId(String stdId) {
        this.stdId = stdId;
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

    public boolean isValidPhone() {
        return !TextUtils.isEmpty(numberphone) && Patterns.PHONE.matcher(numberphone).matches()
                && numberphone.length() == 10 && numberphone.startsWith("0");
    }
    public boolean isValidStdId() {
        return !TextUtils.isEmpty(stdId);
    }

    public boolean isValidNewPass() {
        return !TextUtils.isEmpty(newpass) && newpass.trim().length() >= 6;
    }

    public boolean isValidReNewPass() {
        return !TextUtils.isEmpty(renewpass) &&
                newpass.trim().toString().equals(renewpass.trim().toString());
    }
}
