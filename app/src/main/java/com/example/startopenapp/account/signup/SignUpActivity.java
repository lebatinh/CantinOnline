package com.example.startopenapp.account.signup;

import static com.example.startopenapp.display_manager.TimeHelper.getFormattedTime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.startopenapp.display_manager.DialogHelper;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.R;
import com.example.startopenapp.databinding.ActivitySignUpBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private NetworkChangeReceiver networkChangeReceiver;
    private SUViewModel suViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Khởi tạo DataBinding
        ActivitySignUpBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        // Khởi tạo ViewModel
        suViewModel = new ViewModelProvider(this).get(SUViewModel.class);

        // Gán ViewModel cho layout
        binding.setSignUpSUViewModel(suViewModel);

        // Đảm bảo rằng tất cả các biến LiveData được gán cho layout sẽ cập nhật tự động.
        binding.setLifecycleOwner(this);

        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();

        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        Button btnContinueSU = findViewById(R.id.btnContinueSU);
        btnContinueSU.setOnClickListener(view -> suViewModel.switchToConfirmSU(SignUpActivity.this));

        TextView txtswithToLoginSU = findViewById(R.id.txtswithToLoginSU);
        txtswithToLoginSU.setOnClickListener(view -> suViewModel.switchToLogin(SignUpActivity.this));

        suViewModel.getVerificationResultLiveData().observe(this, verificationResult -> {
            if (verificationResult) {
                String std_id = suViewModel.getStd_id().getValue();
                String phoneNumber = suViewModel.getPhoneNumber().getValue();
                String name = suViewModel.getName().getValue();
                String password = suViewModel.getPassword().getValue();

                // Gửi dữ liệu lên server khi cần
                Map<String, String> dataToSend = new HashMap<>();
                dataToSend.put("acc_id", std_id);
                dataToSend.put("phonenumber", phoneNumber);
                dataToSend.put("name", name);
                dataToSend.put("password", password);
                dataToSend.put("type", "Đăng ký tài khoản");
                dataToSend.put("history", getFormattedTime());

                suViewModel.sendDataToServerSU(dataToSend, SignUpActivity.this);
            } else {
                DialogHelper.DialogWarning(SignUpActivity.this, "Cảnh báo!", "Xác minh thất bại");
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