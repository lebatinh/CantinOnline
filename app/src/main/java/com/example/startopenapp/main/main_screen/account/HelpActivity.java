package com.example.startopenapp.main.main_screen.account;

import static com.example.startopenapp.display_manager.ImageHelper.convertImageViewToBase64;
import static com.example.startopenapp.display_manager.ImageHelper.openCamera;
import static com.example.startopenapp.display_manager.ImageHelper.openGallery;
import static com.example.startopenapp.display_manager.ImageHelper.requestCameraPermission;
import static com.example.startopenapp.display_manager.ImageHelper.requestGalleryPermission;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.startopenapp.R;
import com.example.startopenapp.display_manager.ImageHelper;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class HelpActivity extends AppCompatActivity {
    RetrofitManager retrofitManagerHelp;
    String accId;
    TextInputEditText edtError, edtDescribe, edtTime;
    ImageView imgError1, imgError2;
    Button btnGuiError;
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        accId = intent.getStringExtra("acc_id");

        initData();

        imgError1.setOnClickListener(view -> {
            showImageSelectorDialog(HelpActivity.this, imgError1);
            imgError1.setBackground(null);
        });
        imgError2.setOnClickListener(view1 -> {
            showImageSelectorDialog(HelpActivity.this, imgError2);
            imgError2.setBackground(null);
        });
        btnGuiError.setOnClickListener(view2 -> {
            // Gửi dữ liệu lên server

            String error = edtError.getText().toString().trim();
            String describeError = edtDescribe.getText().toString().trim();
            String timeError = edtTime.getText().toString().trim();
            String image1 = convertImageViewToBase64(imgError1);
            String image2 = convertImageViewToBase64(imgError2);
            if (!error.isEmpty() && !describeError.isEmpty() && !timeError.isEmpty() && image1 != null && image2 != null){
                Map<String, String> dataToSend = new HashMap<>();

                dataToSend.put("acc_id", accId);
                dataToSend.put("error", error);
                dataToSend.put("describeError", describeError);
                dataToSend.put("timeError", timeError);
                dataToSend.put("image1", image1);
                dataToSend.put("image2", image2);

                sendErrorToServer(dataToSend);
            }else {
                Toast.makeText(HelpActivity.this, "Bạn phải điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void sendErrorToServer(Map<String, String> dataToSend) {
        String phpFilePath = ConnectToServer.getInsert_error_phpFilePath();
        String url = ConnectToServer.getInsert_error_url();

        retrofitManagerHelp = new RetrofitManager(url);
        retrofitManagerHelp.sendDataToServer(url, phpFilePath, dataToSend, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("responseHome", response);
                Toast.makeText(HelpActivity.this, response, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(HelpActivity.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("error", error);
            }
        });
    }

    private void initData() {
        edtError = findViewById(R.id.edtError);
        edtDescribe = findViewById(R.id.edtDescribe);
        edtTime = findViewById(R.id.edtTime);
        imgError1 = findViewById(R.id.imgError1);
        imgError2 = findViewById(R.id.imgError2);
        btnGuiError = findViewById(R.id.btnGuiError);
    }

    private void showImageSelectorDialog(Activity activity, ImageView imageView) {
        // Hiển thị dialog để chọn giữa Camera và Thư viện
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Chọn ảnh")
                .setItems(new CharSequence[]{"1)Chọn ảnh từ thư viện (nên dùng)", "2)Chụp ảnh\n(xoay ngang điện thoại ngược chiều kim đồng hồ)"}, (dialog, which) -> {
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
}