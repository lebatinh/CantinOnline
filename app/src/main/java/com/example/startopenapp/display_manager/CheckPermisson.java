package com.example.startopenapp.display_manager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import com.example.startopenapp.R;
import com.example.startopenapp.account.login.LoginActivity;
import com.example.startopenapp.introduce.IntroduceActivity;

public class CheckPermisson extends AppCompatActivity {

    private static final int REQUEST_CODE_INTERNET_PERMISSION = 1001;
    private static final int REQUEST_CODE_SMS_PERMISSION = 1002;
    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 1003; // Thêm mã yêu cầu quyền notification
    private boolean isInstalled;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_permisson);
        checkInstallation();
        checkPermission();
        networkChangeReceiver = new NetworkChangeReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
    }

    private void checkPermission() {
        if (!hasSMSPermission()) {
            requestSMSPermission();
        } else if (!hasNotificationPermission()) { // Kiểm tra quyền notification nếu chưa được cấp
            requestNotificationPermission();
        } else {
            proceedToMainScreen();
        }
    }

    private boolean hasSMSPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
            }
        }
        return true; // Trả về true trên các phiên bản Android cũ hơn API level 26
    }

    private void requestSMSPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, REQUEST_CODE_SMS_PERMISSION);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFICATION_PERMISSION);
            }
        }
    }

    private void checkInstallation() {
        SharedPreferences sharedPreferences = getSharedPreferences("checkInstalled", Context.MODE_PRIVATE);
        isInstalled = sharedPreferences.getBoolean("isInstalled", false);
    }

    private void proceedToMainScreen() {
        Intent intent;
        if (isInstalled) {
            intent = new Intent(CheckPermisson.this, LoginActivity.class);
        } else {
            intent = new Intent(CheckPermisson.this, IntroduceActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_INTERNET_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermission();
            } else {
                Toast.makeText(this, "Ứng dụng cần quyền truy cập Internet để hoạt động.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermission(); // Kiểm tra quyền notification sau khi đã có quyền SMS
            } else {
                Toast.makeText(this, "Ứng dụng cần quyền đọc SMS để hoạt động.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                proceedToMainScreen(); // Tiến hành vào màn hình chính sau khi đã có quyền notification
            } else {
                Toast.makeText(this, "Ứng dụng cần quyền post notification để hoạt động.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}