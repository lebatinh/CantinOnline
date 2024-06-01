package com.example.startopenapp.admin.approve_request;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.startopenapp.R;
import com.example.startopenapp.admin.approve_request.viewpager.ViewPagerAdapterApprove;
import com.example.startopenapp.databinding.ActivityAdminApproveRequestBinding;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.main.HomeActivity;
import com.example.startopenapp.main.main_screen.account.money.MoneyViewModel;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdminApproveRequest extends AppCompatActivity {
    MoneyViewModel moneyViewModel;
    private NetworkChangeReceiver networkChangeReceiver;
    String accId;
    ViewPagerAdapterApprove viewPagerAdapterApprove;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approve_request);
        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        accId = intent.getStringExtra("acc_id");

        ActivityAdminApproveRequestBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_approve_request);
        moneyViewModel = new ViewModelProvider(this).get(MoneyViewModel.class);
        binding.setAppoveRequest(moneyViewModel);
        binding.setLifecycleOwner(this);

        moneyViewModel.getMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(AdminApproveRequest.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        viewPagerAdapterApprove = new ViewPagerAdapterApprove(this, accId);
        binding.viewPagerApprove.setAdapter(viewPagerAdapterApprove);

        new TabLayoutMediator(binding.tablayoutAppove, binding.viewPagerApprove, ((tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Nạp tiền");
                    break;
                case 1:
                    tab.setText("Rút tiền");
                    break;
            }
        })).attach();

        binding.viewPagerApprove.setPageTransformer(new HomeActivity.DepthPageTransformer());

        binding.swipeRefreshLayoutAR.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = new Intent(AdminApproveRequest.this, AdminApproveRequest.class);
                finish();
                startActivity(intent);
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