package com.example.startopenapp.account.changepass;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.startopenapp.display_manager.DialogHelper;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.R;
import com.example.startopenapp.databinding.ActivityChangePassBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

import java.util.HashMap;
import java.util.Map;

public class ChangePassActivity extends AppCompatActivity {
    private NetworkChangeReceiver networkChangeReceiver;
    private CPViewModel cpViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        networkChangeReceiver = new NetworkChangeReceiver();

        // Khởi tạo DataBinding
        ActivityChangePassBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_change_pass);

        // Khởi tạo ViewModel
        cpViewModel = new ViewModelProvider(this).get(CPViewModel.class);

        // Gán ViewModel cho layout
        binding.setChangePass(cpViewModel);

        // Đảm bảo rằng tất cả các biến LiveData được gán cho layout sẽ cập nhật tự động.
        binding.setLifecycleOwner(this);

        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();

        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        cpViewModel.getVerificationResultLiveData().observe(this, verificationResult -> {
            if (verificationResult) {
                String phonenumber = cpViewModel.getPhoneNumber().getValue();
                String password = cpViewModel.getOldpass().getValue();
                String stdID = cpViewModel.getStdid().getValue();

                // Gửi dữ liệu lên server khi cần
                Map<String, String> dataToSend = new HashMap<>();
                dataToSend.put("phonenumber", phonenumber);
                dataToSend.put("password", password);
                dataToSend.put("acc_id", stdID);

                cpViewModel.sendDataToServerCP(dataToSend, ChangePassActivity.this);

                cpViewModel.getCheckConnectAccount().observe(this, checkConnectAccount -> {
                    if (checkConnectAccount) {
                        replaceWithChangePassFragmentConfirm();
                    } else {
                        Toast.makeText(ChangePassActivity.this, "Xác minh tài khoản thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                DialogHelper.DialogWarning(ChangePassActivity.this, "Cảnh báo!", "Xác minh thất bại");
            }
        });

        replaceWithChangePassFragment();
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

    // Hàm đưa ChangePassFragment vào màn hình
    private void replaceWithChangePassFragment() {
        ChangePassFragment changePassFragment = new ChangePassFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, changePassFragment)
                .commit();
    }

    // Hàm đưa ChangePassFragmentConfirm vào màn hình
    public void replaceWithChangePassFragmentConfirm() {
        ChangePassFragmentConfirm changePassFragmentConfirm = new ChangePassFragmentConfirm();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, changePassFragmentConfirm)
                .commit();
    }
}