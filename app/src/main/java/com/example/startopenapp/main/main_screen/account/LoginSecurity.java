package com.example.startopenapp.main.main_screen.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.startopenapp.R;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginSecurity extends AppCompatActivity {
    RetrofitManager retrofitManagerLS;
    TextView tvTimeSignIn, tvTimeLogIn, tvTimeChangePass, tvTimeVerify;
    String accId;
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_security);

        tvTimeSignIn = findViewById(R.id.tvTimeSignIn);
        tvTimeLogIn = findViewById(R.id.tvTimeLogIn);
        tvTimeChangePass = findViewById(R.id.tvTimeChangePass);
        tvTimeVerify = findViewById(R.id.tvTimeVerify);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        accId = intent.getStringExtra("acc_id");

        // Gửi dữ liệu lên server
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("acc_id", accId);
        getHistoryAccount(dataToSend);

        networkChangeReceiver = new NetworkChangeReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    private void getHistoryAccount(Map<String, String> dataToSend) {
        String phpFilePath = ConnectToServer.getSelect_account_history_phpFilePath();
        String urlRI = ConnectToServer.getSelect_account_history_url();

        retrofitManagerLS = new RetrofitManager(urlRI);
        retrofitManagerLS.sendDataToServer(urlRI, phpFilePath, dataToSend, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("response", response);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    // Kiểm tra xem dữ liệu có chứa thông báo lỗi không
                    if (responseObject.has("message")) {
                        String message = responseObject.getString("message");
                        Toast.makeText(LoginSecurity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        // Dữ liệu hợp lệ, lấy giá trị từ responseObject
                        String signInDate = responseObject.getString("signInDate");
                        String secondLastLoginDate = responseObject.getString("secondLastLoginDate");
                        String changePasswordDate = responseObject.getString("changePasswordDate");
                        String verifyAccountDate = responseObject.getString("verifyAccountDate");

                        // Đặt giá trị vào các TextView
                        tvTimeSignIn.setText(signInDate);
                        tvTimeLogIn.setText(secondLastLoginDate);
                        tvTimeChangePass.setText(changePasswordDate);
                        tvTimeVerify.setText(verifyAccountDate);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginSecurity.this, "Error parsing response!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(LoginSecurity.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("error", error);
            }
        });
    }
}