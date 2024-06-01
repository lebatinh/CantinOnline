package com.example.startopenapp.main.main_screen.account.money;

import static com.example.startopenapp.display_manager.TimeHelper.getFormattedTime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.startopenapp.R;

import com.example.startopenapp.admin.approve_request.recharge_list.Approve;
import com.example.startopenapp.databinding.ActivityInfoRechargeBinding;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.display_manager.TimeHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoRecharge extends AppCompatActivity {
    TextView tvStk, tvNoidung, tvMoney;
    MoneyViewModel moneyViewModel;
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_recharge);

        tvStk = findViewById(R.id.tvStk);
        tvNoidung = findViewById(R.id.tvNoidung);
        tvMoney = findViewById(R.id.tvMoney);

        ActivityInfoRechargeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_info_recharge);
        moneyViewModel = new ViewModelProvider(this).get(MoneyViewModel.class);
        binding.setMoneyViewModel2(moneyViewModel);
        binding.setLifecycleOwner(this);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String id = intent.getStringExtra("acc_id");
        String tran_id = intent.getStringExtra("transaction_id");
        String type = intent.getStringExtra("type");

        // Gửi dữ liệu lên server
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("acc_id", id);
        dataToSend.put("transaction_id", tran_id);
        dataToSend.put("type", type);

        moneyViewModel.getDataRechargeMoney(dataToSend);

        moneyViewModel.getInfo().observe(this, infoModel -> {
            if (infoModel != null) {
                binding.tvMaDon.setText(tran_id);
                binding.tvTienNap.setText(infoModel.getTiennap());
                binding.tvTrangThai.setText(infoModel.getTrangthai());
                binding.tvTimeTao.setText(infoModel.getTimetao());
                binding.tvMoney.setText(infoModel.getTiennap());
                binding.tvNoidung.setText(infoModel.getNoidung());
            }
        });

        binding.btnHuyNap.setOnClickListener(view1 -> {
            // Gửi dữ liệu lên server
            Map<String, String> data = new HashMap<>();
            data.put("acc_id", id);
            data.put("transaction_id", tran_id);
            data.put("history", getFormattedTime());
            data.put("status", "hủy đơn");
            data.put("type", "nạp tiền");

            moneyViewModel.huyNapTien(data);
        });

        moneyViewModel.getMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(InfoRecharge.this, message, Toast.LENGTH_SHORT).show();
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
}