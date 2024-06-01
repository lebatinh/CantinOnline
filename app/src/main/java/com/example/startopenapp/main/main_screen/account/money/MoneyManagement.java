package com.example.startopenapp.main.main_screen.account.money;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.startopenapp.R;
import com.example.startopenapp.admin.approve_request.AdminApproveRequest;
import com.example.startopenapp.databinding.ActivityMoneyManagementBinding;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;

public class MoneyManagement extends AppCompatActivity {
    String accId;
    MoneyViewModel moneyViewModel;
    private NetworkChangeReceiver networkChangeReceiver;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_management);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        accId = intent.getStringExtra("acc_id");

        ActivityMoneyManagementBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_money_management);
        moneyViewModel = new ViewModelProvider(this).get(MoneyViewModel.class);
        binding.setMoneyViewModel(moneyViewModel);
        binding.setLifecycleOwner(this);

        moneyViewModel.getMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(MoneyManagement.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        moneyViewModel.getInforAcc(accId);

        moneyViewModel.getMoney().observe(this, moneyModel -> {
            if (moneyModel != null){
                binding.tvId.setText(accId);
                binding.tvName.setText(moneyModel.getName());
                String sodu = moneyModel.getSodu();
                if (sodu == null || sodu.equals("null") || sodu.isEmpty()) {
                    sodu = "0";
                }
                binding.tvSodu.setText(sodu);
            }
        });

        binding.tvNapTien.setOnClickListener(view1 -> {
            binding.lnrNapTien.setVisibility(View.VISIBLE);
            binding.lnrRutTien.setVisibility(View.GONE);
        });

        binding.tvRutTien.setOnClickListener(view2 -> {
            binding.lnrNapTien.setVisibility(View.GONE);
            binding.lnrRutTien.setVisibility(View.VISIBLE);
        });
        binding.btnSendYcNap.setOnClickListener(view3 -> moneyViewModel.onClickSendYcNap(MoneyManagement.this));
        moneyViewModel.getMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(MoneyManagement.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSendYcRut.setOnClickListener(view4 -> moneyViewModel.onClickSendYcRut());

        if (accId.equals("admin")){
            binding.imgApproveRequest.setVisibility(View.VISIBLE);
            binding.imgApproveRequest.setOnClickListener(view5 -> {
                Intent intent1 = new Intent(MoneyManagement.this, AdminApproveRequest.class);
                intent1.putExtra("accId", accId);
                startActivity(intent1);
            });
        }else {
            binding.imgApproveRequest.setVisibility(View.GONE);
        }
        networkChangeReceiver = new NetworkChangeReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
        moneyViewModel.getInforAcc(accId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }
}
