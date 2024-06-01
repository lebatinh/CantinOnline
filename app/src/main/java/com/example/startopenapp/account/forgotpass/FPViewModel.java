package com.example.startopenapp.account.forgotpass;

import static com.example.startopenapp.display_manager.DialogHelper.DialogWarning;
import static com.example.startopenapp.display_manager.TimeHelper.getFormattedTime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.startopenapp.R;
import com.example.startopenapp.account.login.LoginActivity;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FPViewModel extends ViewModel {
    private RetrofitManager retrofitManagerFP;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;

    public FPViewModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    // Getter cho LiveData để Activity hoặc Fragment có thể quan sát kết quả xác minh
    public LiveData<Boolean> getVerificationResultLiveData() {
        return verificationResultLiveData;
    }
    private MutableLiveData<String> responseLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> verificationResultLiveData = new MutableLiveData<>();
    private MutableLiveData<String> phoneNumber = new MutableLiveData<>();
    private MutableLiveData<String> id = new MutableLiveData<>();
    private MutableLiveData<String> newpass = new MutableLiveData<>();
    private MutableLiveData<String> renewpass = new MutableLiveData<>();

    public MutableLiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(MutableLiveData<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public MutableLiveData<String> getId() {
        return id;
    }

    public void setId(MutableLiveData<String> id) {
        this.id = id;
    }

    public MutableLiveData<String> getNewpass() {
        return newpass;
    }

    public void setNewpass(MutableLiveData<String> newpass) {
        this.newpass = newpass;
    }

    public MutableLiveData<String> getRenewpass() {
        return renewpass;
    }

    public void setRenewpass(MutableLiveData<String> renewpass) {
        this.renewpass = renewpass;
    }

    //nhập otp
    public void switchToConfirmFG(Activity activity) {
        FPModel FPModel = new FPModel(phoneNumber.getValue(), id.getValue(), newpass.getValue(), renewpass.getValue());
        if (!FPModel.isValidPhone()) {
            DialogWarning(activity, "Cảnh báo!", "Định dạng số điện thoại không đúng!");
        } else if (!FPModel.isValidStdId()) {
            DialogWarning(activity, "Cảnh báo!", "Mã sinh viên không được để trống!");
        } else {
            showOTPDialog(activity);
            Toast.makeText(activity, "Yêu cầu mã OTP đang được khởi tạo!", Toast.LENGTH_SHORT).show();
        }
    }

    //gửi data và đổi mật khẩu
    public void onClickForgotPass(Activity activity) {
        String phoneNumber = getPhoneNumber().getValue();
        String id = getId().getValue();
        String newpass = getNewpass().getValue();

        FPModel FPModel = new FPModel(phoneNumber, id, newpass, renewpass.getValue());
        if (!FPModel.isValidNewPass()) {
            DialogWarning(activity, "Cảnh báo!", "Mật khẩu phải từ 6 kí tự trở lên\n" +
                    "và không có chữ in hoa hoặc kí tự đặc biệt!");
        } else if (!FPModel.isValidReNewPass()) {
            DialogWarning(activity, "Cảnh báo!", "Mật khẩu mới nhập lại PHẢI trùng khớp với mật khẩu mới!");
        } else {
            // Gửi dữ liệu lên server
            Map<String, String> dataToSend = new HashMap<>();

            dataToSend.put("acc_id", id);
            dataToSend.put("phonenumber", phoneNumber);
            dataToSend.put("newpassword", newpass);
            dataToSend.put("history", getFormattedTime());

            sendDataToServerFP(dataToSend, activity);
        }
    }

    // Phương thức để hiển thị dialog nhập mã OTP
    public void showOTPDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View dialogView = inflater.inflate(R.layout.dialog_otp, null);
        TextInputEditText otpEditText = dialogView.findViewById(R.id.editTextOTP);
        TextView resendotp = dialogView.findViewById(R.id.resendotp);

        String phone = "+84" + phoneNumber.getValue();
        sendOtp(phone, activity);

        resendotp.setOnClickListener(view -> resendOtp(phone, activity));

        builder.setView(dialogView)
                .setTitle("Bạn hãy nhập mã OTP vừa được gửi về số điện thoại của bạn")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    String otp = Objects.requireNonNull(otpEditText.getText()).toString().trim();
                    if (!otp.isEmpty()) {
                        verifyOtp(otp);
                    } else {
                        DialogWarning(activity, "Cảnh báo!", "Mã OTP không chính xác");
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Phương thức gửi yêu cầu nhận OTP
    public void sendOtp(String phoneNumber, Activity activity) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Số điện thoại cần xác minh
                        .setTimeout(60L, TimeUnit.SECONDS) // Thời gian timeout
                        .setActivity(activity)              // Activity để mở hộp thoại xác minh số điện thoại
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                // Xác minh tự động hoàn tất, không cần nhập mã OTP
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                // Xác minh thất bại
                                verificationResultLiveData.setValue(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                // Mã OTP đã được gửi
                                mVerificationId = verificationId;
                                mResendToken = token;
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Phương thức gửi lại yêu cầu nhận OTP
    public void resendOtp(String phoneNumber, Activity activity) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Số điện thoại cần xác minh
                        .setTimeout(60L, TimeUnit.SECONDS) // Thời gian timeout
                        .setActivity(activity)              // Activity để mở hộp thoại xác minh số điện thoại
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                // Xác minh tự động hoàn tất, không cần nhập mã OTP
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                // Xác minh thất bại
                                verificationResultLiveData.setValue(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                // Mã OTP đã được gửi
                                mVerificationId = verificationId;
                                mResendToken = token;
                            }
                        })
                        .setForceResendingToken(mResendToken) // Sử dụng token để gửi lại OTP
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Phương thức kiểm tra OTP
    public void verifyOtp(String otp) {
        if (mVerificationId != null && !mVerificationId.isEmpty() && otp != null && !otp.isEmpty()) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
            signInWithPhoneAuthCredential(credential);
        }
    }

    // Phương thức đăng nhập bằng thông tin xác minh số điện thoại
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công
                        verificationResultLiveData.setValue(true);
                    } else {
                        // Đăng nhập thất bại
                        verificationResultLiveData.setValue(false);
                    }
                });
    }

    //chuyển đến màn hình đăng nhập
    public void switchToLogin(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    //gửi dữ liệu để kiểm tra
    private void sendDataToServerFP(Map<String, String> data, Activity activity) {
        String urlFP = ConnectToServer.getUpdate_account_url();
        String phpFilePath = ConnectToServer.getUpdate_account_phpFilePath();

        retrofitManagerFP = new RetrofitManager(urlFP);
        retrofitManagerFP.sendDataToServer(urlFP, phpFilePath, data, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                responseLiveData.setValue(response);
                Toast.makeText(activity, response, Toast.LENGTH_SHORT).show();
                Log.d("reponse" , response);
                switchToLogin(activity);
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(activity, "Gửi yêu cầu thất bại.\nHãy kiểm tra đường truyền mạng!", Toast.LENGTH_SHORT).show();
                Log.d("error", error.toString());
            }
        });
    }
}
