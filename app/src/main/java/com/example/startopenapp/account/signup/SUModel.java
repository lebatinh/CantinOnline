package com.example.startopenapp.account.signup;

import android.text.TextUtils;
import android.util.Patterns;

public class SUModel {
    private String numberphone;
    private String std_id;
    private String name;
    private String password;
    private String repassword;

    public SUModel(String numberphone, String std_id, String name, String password, String repassword) {
        this.numberphone = numberphone;
        this.std_id = std_id;
        this.name = name;
        this.password = password;
        this.repassword = repassword;
    }

    public String getNumberphone() {
        return numberphone;
    }

    public void setNumberphone(String numberphone) {
        this.numberphone = numberphone;
    }

    public String getStd_id() {
        return std_id;
    }

    public void setStd_id(String std_id) {
        this.std_id = std_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepassword() {
        return repassword;
    }

    public void setRepassword(String repassword) {
        this.repassword = repassword;
    }

    public boolean isValidPhone() {
        return !TextUtils.isEmpty(numberphone) && Patterns.PHONE.matcher(numberphone).matches()
                && numberphone.length() == 10 && numberphone.startsWith("0");
    }
    public boolean isValidStdId() {
        return !TextUtils.isEmpty(std_id);
    }

    public boolean isValidStdName() {
        return !TextUtils.isEmpty(name);
    }
    public boolean isValidPass() {
        return !TextUtils.isEmpty(password) && password.trim().length() >= 6;
    }

    public boolean isValidRePass() {
        return !TextUtils.isEmpty(repassword) &&
                repassword.trim().toString().equals(password.trim().toString());
    }
}
