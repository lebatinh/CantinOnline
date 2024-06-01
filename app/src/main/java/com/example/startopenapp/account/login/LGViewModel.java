package com.example.startopenapp.account.login;

import static com.example.startopenapp.display_manager.DialogHelper.DialogWarning;
import static com.example.startopenapp.display_manager.TimeHelper.getFormattedTime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.startopenapp.account.changepass.ChangePassActivity;
import com.example.startopenapp.account.forgotpass.ForgotPassActivity;
import com.example.startopenapp.account.signup.SignUpActivity;
import com.example.startopenapp.main.HomeActivity;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LGViewModel extends ViewModel {
    private RetrofitManager retrofitManagerLG;
    private MutableLiveData<String> responseLiveData = new MutableLiveData<>();
    private MutableLiveData<String> phoneNumber = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();
    private MutableLiveData<String> stdid = new MutableLiveData<>();
    public MutableLiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(MutableLiveData<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(MutableLiveData<String> password) {
        this.password = password;
    }

    public MutableLiveData<String> getStdid() {
        return stdid;
    }

    public void setStdid(MutableLiveData<String> stdid) {
        this.stdid = stdid;
    }

    //xử lý đăng nhập
    public void onClickLogin(Context context) {
        String phonenumber = getPhoneNumber().getValue();
        String pass = getPassword().getValue();
        String stdId = getStdid().getValue();
        LGModel LGModel = new LGModel(phonenumber, pass, stdId);

        if (!LGModel.isValidNumberPhone()) {
            DialogWarning(context, "Cảnh báo!", "Định dạng số điện thoại không đúng!");
        } else if (!LGModel.isValidPassword()) {
            DialogWarning(context, "Cảnh báo!", "Mật khẩu phải từ 6 kí tự trở lên\n" +
                    "và không có chữ in hoa hoặc kí tự đặc biệt!");
        } else {
            // Gửi dữ liệu lên server
            Map<String, String> dataToSend = new HashMap<>();
            dataToSend.put("phonenumber", phonenumber);
            dataToSend.put("password", pass);
            dataToSend.put("acc_id", stdId);
            dataToSend.put("type", "Đăng nhập");
            dataToSend.put("history", getFormattedTime());

            sendDataToServerLG(dataToSend, (Activity) context);
        }
    }
    //chuyển qua quên mật khẩu
    public void switchToForgotPass(Context context){
        Intent intent = new Intent(context, ForgotPassActivity.class);
        context.startActivity(intent);
    }

    //chuyển qua đăng ký
    public void switchToSignUp(Context context){
        Intent intent = new Intent(context, SignUpActivity.class);
        context.startActivity(intent);
    }
    //chuyển qua đăng ký
    public void switchToChangePass(Context context){
        Intent intent = new Intent(context, ChangePassActivity.class);
        context.startActivity(intent);
    }
    //chuyển qua home
    public void switchToHome(Context context){
        Intent intent = new Intent(context, HomeActivity.class);
        String stdId = getStdid().getValue();
        intent.putExtra("acc_id", stdId);
        context.startActivity(intent);
    }
    //gửi dữ liệu để kiểm tra
    private void sendDataToServerLG(Map<String, String> data, Activity activity) {
        String phpFilePath = ConnectToServer.getSelect_account_phpFilePath();
        String urlLG = ConnectToServer.getSelect_account_url();

        retrofitManagerLG = new RetrofitManager(urlLG);
        retrofitManagerLG.sendDataToServer(urlLG, phpFilePath, data, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                responseLiveData.setValue(response);
                if (Objects.equals(response, "Đăng nhập tài khoản thành công!")){
                    switchToHome(activity);
                    Toast.makeText(activity, response , Toast.LENGTH_SHORT).show();
                    Log.d("response", response.toString());
                }else {
                    Toast.makeText(activity, response , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(activity, "Gửi yêu cầu thất bại.\nHãy kiểm tra đường truyền mạng!", Toast.LENGTH_SHORT).show();
                Log.d("error", error);
            }
        });
    }
}
