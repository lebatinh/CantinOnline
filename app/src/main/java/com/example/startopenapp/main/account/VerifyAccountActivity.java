package com.example.startopenapp.main.account;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.startopenapp.R;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;

public class VerifyAccountActivity extends AppCompatActivity {
    String accId;
    Boolean verified, checkVisible;
    TextView tvVerifyAcc;
    LinearLayout lnrNoVerify, lnrVerified;
    Button btnVerify;
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);
        checkVisible = false;
        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        accId = intent.getStringExtra("acc_id");
        verified = intent.getBooleanExtra("verified", false);

        // Tham chiếu các view
        tvVerifyAcc = findViewById(R.id.tvVerifyAcc);
        lnrNoVerify = findViewById(R.id.lnrNoVerify);
        lnrVerified = findViewById(R.id.lnrVerified);
        btnVerify = findViewById(R.id.btnVerify);

        // Đặt trạng thái ban đầu
        lnrNoVerify.setVisibility(checkVisible ? View.VISIBLE : View.GONE);
        lnrVerified.setVisibility(checkVisible ? View.VISIBLE : View.GONE);

        tvVerifyAcc = findViewById(R.id.tvVerifyAcc);
        tvVerifyAcc.setOnClickListener(view1 -> {
            if (verified){
                // Kiểm tra trạng thái của checkVisible
                if (checkVisible) {
                    // Nếu đang hiển thị thì ẩn đi
                    lnrVerified.setVisibility(View.GONE);
                } else {
                    // Nếu đang ẩn thì hiển thị
                    lnrVerified.setVisibility(View.VISIBLE);
                }
                // Đảo ngược trạng thái của checkVisible
                checkVisible = !checkVisible;

                lnrVerified.setOnLongClickListener(view2 -> {
                    VerifyAccInfo(accId);
                    return true;
                });
            }else {
                // Kiểm tra trạng thái của checkVisible
                if (checkVisible) {
                    // Nếu đang hiển thị thì ẩn đi
                    lnrNoVerify.setVisibility(View.GONE);
                } else {
                    // Nếu đang ẩn thì hiển thị
                    lnrNoVerify.setVisibility(View.VISIBLE);
                }
                // Đảo ngược trạng thái của checkVisible
                checkVisible = !checkVisible;

                btnVerify.setOnClickListener(view3 -> ChangeToVerifyAccRegisterInfo(accId));
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
    private void VerifyAccInfo(String accId) {
        Intent intent = new Intent(VerifyAccountActivity.this, AccountInfo.class);
        intent.putExtra("accId", accId);
        startActivity(intent);
    }
    private void ChangeToVerifyAccRegisterInfo(String accId) {
        Intent intent = new Intent(VerifyAccountActivity.this, RegisterInfo.class);
        intent.putExtra("accId", accId);
        startActivity(intent);
    }
}