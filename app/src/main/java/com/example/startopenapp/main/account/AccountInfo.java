package com.example.startopenapp.main.account;

import static com.example.startopenapp.display_manager.ImageHelper.decodeBase64ToBitmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.startopenapp.R;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountInfo extends AppCompatActivity {
    private RetrofitManager retrofitManagerAI;
    ImageView imgAvt, imgStdIDCard;
    TextView tvID, tvPhone, tvName, tvBirthday, tvAddress, tvHometown;
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        InitData();

        Intent intent = getIntent();
        String accId = intent.getStringExtra("accId");

        // Gửi dữ liệu lên server
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("acc_id", accId);
        getInfoAccount(dataToSend);
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

    private void InitData(){
        imgAvt = findViewById(R.id.imgAvt);
        imgStdIDCard = findViewById(R.id.imgStdIDCard);
        tvID = findViewById(R.id.tvID);
        tvPhone = findViewById(R.id.tvPhone);
        tvName = findViewById(R.id.tvName);
        tvBirthday = findViewById(R.id.tvBirthday);
        tvAddress = findViewById(R.id.tvAddress);
        tvHometown = findViewById(R.id.tvHometown);
    }
    private void getInfoAccount(Map<String, String> data) {
        String phpFilePath = ConnectToServer.getSelect_info_phpFilePath();
        String urlLG = ConnectToServer.getSelect_info_url();

        retrofitManagerAI = new RetrofitManager(urlLG);
        retrofitManagerAI.sendDataToServer(urlLG, phpFilePath, data, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);

                    // Kiểm tra xem dữ liệu có chứa thông báo lỗi không
                    if (responseObject.has("message")) {
                        String errorMessage = responseObject.getString("message");
                        if (errorMessage == "Tài khoản chưa xác thực!"){
                            Toast.makeText(AccountInfo.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }else {
                            // thông báo lỗi
                            Toast.makeText(AccountInfo.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Dữ liệu hợp lệ
                        JSONArray jsonArray = responseObject.getJSONArray("data");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String id = jsonObject.getString("acc_id");
                        String avatarByte = jsonObject.getString("avatar");
                        String phone = jsonObject.getString("phonenumber");
                        String name = jsonObject.getString("name");
                        String address = jsonObject.getString("address");
                        String hometown = jsonObject.getString("hometown");
                        String stdcard_id = jsonObject.getString("stdcard_id");

                        // Đặt dữ liệu vào các thành phần trong XML
                        setUserData(id,avatarByte,phone, name,address,hometown,stdcard_id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AccountInfo.this, "Error parsing response!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(AccountInfo.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("error", error);
            }
        });
    }

    private void setUserData(String id, String avatarByte, String phone, String name, String address,
                             String hometown, String stdcardId) {
        if (avatarByte != null) {
            Bitmap bitmap = decodeBase64ToBitmap(avatarByte);

            imgAvt.setImageBitmap(bitmap);
        }else {
            imgAvt.setImageResource(R.drawable.account);
        }
        if (stdcardId != null) {
            Bitmap bitmap = decodeBase64ToBitmap(stdcardId);

            imgStdIDCard.setImageBitmap(bitmap);
        }

        tvID.setText(id);
        tvPhone.setText(phone);
        tvName.setText(name);
        tvAddress.setText(address);
        tvHometown.setText(hometown);
    }
}