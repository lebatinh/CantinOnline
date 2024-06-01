package com.example.startopenapp.account.login;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.R;
import com.example.startopenapp.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private NetworkChangeReceiver networkChangeReceiver;
    private LGViewModel lgViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo DataBinding
        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        // Khởi tạo ViewModel
        lgViewModel = new ViewModelProvider(this).get(LGViewModel.class);

        // Gán ViewModel cho layout
        binding.setLoginLGViewModel(lgViewModel);

        // Đảm bảo rằng tất cả các biến LiveData được gán cho layout sẽ cập nhật tự động.
        binding.setLifecycleOwner(this);

        SharedPreferences sharedPreferences = getSharedPreferences("checkInstalled", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("isInstalled", true);
        editor.apply();

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(view -> lgViewModel.onClickLogin(LoginActivity.this));

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