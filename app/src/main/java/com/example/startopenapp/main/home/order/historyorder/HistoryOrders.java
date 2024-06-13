package com.example.startopenapp.main.home.order.historyorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.example.startopenapp.R;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.main.HomeActivity;
import com.example.startopenapp.main.home.order.historyorder.viewpager.ViewPagerAdapterHistoryOrders;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HistoryOrders extends AppCompatActivity {
    String accId;
    private NetworkChangeReceiver networkChangeReceiver;
    ViewPagerAdapterHistoryOrders viewpager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_orders);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        accId = intent.getStringExtra("acc_id");

        ViewPager2 vpHistoryOrders = findViewById(R.id.vpHistoryOrders);
        TabLayout tabHistoryOrders = findViewById(R.id.tabHistoryOrders);

        viewpager = new ViewPagerAdapterHistoryOrders(this, accId);

        viewpager = new ViewPagerAdapterHistoryOrders(this, accId);
        vpHistoryOrders.setAdapter(viewpager);

        new TabLayoutMediator(tabHistoryOrders, vpHistoryOrders, ((tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Đã mua");
                    break;
                case 1:
                    tab.setText("Đang mua");
                    break;
                case 2:
                    tab.setText("Đã hủy");
                    break;
            }
        })).attach();

        vpHistoryOrders.setPageTransformer(new HomeActivity.DepthPageTransformer());

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