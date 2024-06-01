package com.example.startopenapp.account.forgotpass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.example.startopenapp.display_manager.DialogHelper;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.R;
import com.example.startopenapp.databinding.ActivityForgotPassBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

public class ForgotPassActivity extends AppCompatActivity{
    private NetworkChangeReceiver networkChangeReceiver;
    private FPViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        networkChangeReceiver = new NetworkChangeReceiver();

        // Khởi tạo DataBinding
        ActivityForgotPassBinding binding = DataBindingUtil.setContentView(ForgotPassActivity.this, R.layout.activity_forgot_pass);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(ForgotPassActivity.this).get(FPViewModel.class);

        // Gán ViewModel cho layout
        binding.setForgotFGViewModel(viewModel);

        // Đảm bảo rằng tất cả các biến LiveData được gán cho layout sẽ cập nhật tự động.
        binding.setLifecycleOwner(ForgotPassActivity.this);

        FirebaseApp.initializeApp(ForgotPassActivity.this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();

        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        viewModel.getVerificationResultLiveData().observe(ForgotPassActivity.this, verificationResult -> {
            if (verificationResult) {
                replaceWithForgotPassFragmentConfirm();
            } else {
                DialogHelper.DialogWarning(ForgotPassActivity.this, "Cảnh báo!", "Xác minh thất bại");
            }
        });

        replaceWithForgotPassFragment();
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

    // Hàm đưa ForgotPassFragment vào màn hình
    private void replaceWithForgotPassFragment() {
        ForgotPassFragment forgotPassFragment = new ForgotPassFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_FP, forgotPassFragment)
                .commit();
    }

    // Hàm đưa ForgotPassFragmentConfirm vào màn hình
    private void replaceWithForgotPassFragmentConfirm() {
        ForgotPassFragmentConfirm forgotPassFragmentConfirm = new ForgotPassFragmentConfirm();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_FP, forgotPassFragmentConfirm)
                .commit();
    }
}
