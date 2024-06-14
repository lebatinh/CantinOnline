package com.example.startopenapp.main.account;

import static com.example.startopenapp.display_manager.ImageHelper.convertImageViewToBase64;
import static com.example.startopenapp.display_manager.ImageHelper.openCamera;
import static com.example.startopenapp.display_manager.ImageHelper.openGallery;
import static com.example.startopenapp.display_manager.ImageHelper.requestCameraPermission;
import static com.example.startopenapp.display_manager.ImageHelper.requestGalleryPermission;
import static com.example.startopenapp.display_manager.TimeHelper.getFormattedTime;
import static com.example.startopenapp.display_manager.TimeHelper.showDatePickerDialog;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.startopenapp.R;
import com.example.startopenapp.display_manager.ImageHelper;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterInfo extends AppCompatActivity {
    private RetrofitManager retrofitManagerRI;
    private ImageView imgAvtRI, imgStdIDCardRI;
    private TextView tvIDRI, tvPhoneRI, tvNameRI, tvBirthdayRI;
    private EditText tvAddressRI, tvHometownRI;
    private Button btnConfirmRI;
    private String accId;
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_info);
        InitData();
        Intent intent = getIntent();
        accId = intent.getStringExtra("accId");

        // Gửi dữ liệu lên server
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("acc_id", accId);
        getInfoDefault(dataToSend);

        btnConfirmRI.setOnClickListener(view -> registerInfo());

        imgAvtRI.setOnClickListener(view -> showImageSelectorDialog(RegisterInfo.this, imgAvtRI));
        imgStdIDCardRI.setOnClickListener(view -> showImageSelectorDialog(RegisterInfo.this, imgStdIDCardRI));
        tvBirthdayRI.setOnClickListener(view -> showDatePickerDialog(RegisterInfo.this, tvBirthdayRI));
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

    private void showImageSelectorDialog(Activity activity, ImageView imageView) {
        // Hiển thị dialog để chọn giữa Camera và Thư viện
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Chọn ảnh")
                .setItems(new CharSequence[]{ "1)Chọn ảnh từ thư viện (nên dùng)", "2)Chụp ảnh\n(xoay ngang điện thoại ngược chiều kim đồng hồ)"}, (dialog, which) -> {
                    if (which == 0) {
                        requestGalleryPermission(activity, imageView);
                    } else {
                        requestCameraPermission(activity, imageView);
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageHelper.onActivityResult(requestCode, resultCode, data, this);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == ImageHelper.REQUEST_CODE_CAMERA) {
                openCamera(this);
            } else if (requestCode == ImageHelper.REQUEST_CODE_FOLDER) {
                openGallery(this);
            }
        } else {
            Toast.makeText(this, "Bạn cần cấp quyền để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
        }
    }

    private void getInfoDefault(Map<String, String> data) {
        String phpFilePath = ConnectToServer.getSelect_info_acc_phpFilePath();
        String urlRI = ConnectToServer.getSelect_info_acc_url();

        retrofitManagerRI = new RetrofitManager(urlRI);
        retrofitManagerRI.sendDataToServer(urlRI, phpFilePath, data, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);

                    // Kiểm tra xem dữ liệu có chứa thông báo lỗi không
                    if (responseObject.has("message")) {
                        String message = responseObject.getString("message");
                        if (message.equals("Tài khoản chưa xác thực!")){
                            Toast.makeText(RegisterInfo.this, message, Toast.LENGTH_SHORT).show();
                        }else {
                            // thông báo lỗi
                            Toast.makeText(RegisterInfo.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Dữ liệu hợp lệ
                        JSONArray jsonArray = responseObject.getJSONArray("data");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String phone = jsonObject.getString("phonenumber");
                        String name = jsonObject.getString("name");

                        // Đặt dữ liệu vào các thành phần trong XML
                        setUserData(accId,phone, name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterInfo.this, "Error parsing response!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(RegisterInfo.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("error", error);
            }
        });
    }

    private void setUserData(String id, String phone, String name) {
        tvIDRI.setText(id);
        tvPhoneRI.setText(phone);
        tvNameRI.setText(name);
    }
    private void registerInfo() {
        // Gửi dữ liệu lên server
        Map<String, String> data = new HashMap<>();

        String accId = tvIDRI.getText().toString();
        data.put("acc_id", accId);
        String avatar = convertImageViewToBase64(imgAvtRI);
        data.put("avatar", avatar);
        String phonenumber = tvPhoneRI.getText().toString();
        data.put("phonenumber", phonenumber);
        String name = tvNameRI.getText().toString();
        data.put("name", name);
        String birthday = tvBirthdayRI.getText().toString();
        data.put("birthday", birthday);
        String address = tvAddressRI.getText().toString();
        data.put("address", address);
        String hometown = tvHometownRI.getText().toString();
        data.put("hometown", hometown);
        String stdcard_id = convertImageViewToBase64(imgStdIDCardRI);
        data.put("stdcard_img", stdcard_id);
        data.put("type", "Xác minh tài khoản");
        data.put("history", getFormattedTime());

        if (!accId.isEmpty() && !(avatar == null) && !phonenumber.isEmpty() && !name.isEmpty() &&
                !birthday.isEmpty() && !address.isEmpty() && !hometown.isEmpty() && !(stdcard_id == null)){
            registerInfoAccount(data);
        }else {
            Toast.makeText(RegisterInfo.this, "Bạn không được bỏ trống bất kì chỗ nào!", Toast.LENGTH_SHORT).show();
        }
    }
    private void registerInfoAccount(Map<String, String> data) {
        String phpFilePath = ConnectToServer.getInsert_information_user_phpFilePath();
        String urlRI = ConnectToServer.getInsert_information_user_url();

        retrofitManagerRI = new RetrofitManager(urlRI);
        retrofitManagerRI.sendDataToServer(urlRI, phpFilePath, data, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("response", response);
                Toast.makeText(RegisterInfo.this, response , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(RegisterInfo.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("error", error);
            }
        });
    }
    private void InitData() {
        imgAvtRI = findViewById(R.id.imgAvtRI);
        imgStdIDCardRI = findViewById(R.id.imgStdIDCardRI);
        tvIDRI = findViewById(R.id.tvIDRI);
        tvPhoneRI = findViewById(R.id.tvPhoneRI);
        tvNameRI = findViewById(R.id.tvNameRI);
        tvBirthdayRI = findViewById(R.id.tvBirthdayRI);
        tvAddressRI = findViewById(R.id.tvAddressRI);
        tvHometownRI = findViewById(R.id.tvHometownRI);
        btnConfirmRI = findViewById(R.id.btnConfirmRI);
    }
}
