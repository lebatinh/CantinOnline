package com.example.startopenapp.account.signup;

import static com.example.startopenapp.display_manager.DialogHelper.DialogWarning;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SUViewModel extends ViewModel {
    public RetrofitManager retrofitManagerSU;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    public SUViewModel() {
        mAuth = FirebaseAuth.getInstance();
    }
    // Getter cho LiveData để Activity hoặc Fragment có thể quan sát kết quả xác minh
    public LiveData<Boolean> getVerificationResultLiveData() {
        return verificationResultLiveData;
    }
    private MutableLiveData<String> responseLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> verificationResultLiveData = new MutableLiveData<>();
    private MutableLiveData<String> phoneNumber = new MutableLiveData<>();
    private MutableLiveData<String> std_id = new MutableLiveData<>();
    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();
    private MutableLiveData<String> repassword = new MutableLiveData<>();

    public MutableLiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(MutableLiveData<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public MutableLiveData<String> getStd_id() {
        return std_id;
    }

    public void setStd_id(MutableLiveData<String> std_id) {
        this.std_id = std_id;
    }

    public MutableLiveData<String> getName() {
        return name;
    }

    public void setName(MutableLiveData<String> name) {
        this.name = name;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(MutableLiveData<String> password) {
        this.password = password;
    }

    public MutableLiveData<String> getRepassword() {
        return repassword;
    }

    public void setRepassword(MutableLiveData<String> repassword) {
        this.repassword = repassword;
    }

    //chuyển qua xác nhận otp
    public void switchToConfirmSU(Activity activity) {
        SUModel suModel = new SUModel(phoneNumber.getValue(), std_id.getValue(),name.getValue() ,password.getValue(), repassword.getValue());
        if (!suModel.isValidPhone()) {
            DialogWarning(activity, "Cảnh báo!", "Định dạng số điện thoại không đúng!");
        } else if (!suModel.isValidStdId()) {
            DialogWarning(activity, "Cảnh báo!", "Mã sinh viên không được bỏ trống!");
        }else if (!suModel.isValidStdName()) {
            DialogWarning(activity, "Cảnh báo!", "Họ và tên không được bỏ trống!");
        } else if (!suModel.isValidPass()) {
            DialogWarning(activity, "Cảnh báo!", "Mật khẩu phải từ 6 kí tự trở lên\n" +
                    "và không có chữ in hoa hoặc kí tự đặc biệt!");
        } else if (!suModel.isValidRePass()) {
            DialogWarning(activity, "Cảnh báo!", "Mật khẩu nhập lại PHẢI trùng khớp với mật khẩu mới!");
        } else {
            showOTPDialog(activity);
            Toast.makeText(activity,"Yêu cầu mã OTP đang được khởi tạo!", Toast.LENGTH_SHORT).show();
        }

    }

    // Phương thức để hiển thị dialog nhập mã OTP
    public void showOTPDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View dialogView = inflater.inflate(R.layout.dialog_otp, null);
        TextInputEditText otpEditText = dialogView.findViewById(R.id.editTextOTP);
        TextView resendotp = dialogView.findViewById(R.id.resendotp);

        String phone = "+84"+ phoneNumber.getValue();
        sendOtp(phone, activity);

        resendotp.setOnClickListener(view -> resendOtp(phone, activity));

        builder.setView(dialogView)
                .setTitle("Nhập mã OTP")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    String otp = otpEditText.getText().toString().trim();
                    //điều kiện xét otp
                    if (!otp.isEmpty()) {
                        verifyOtp(otp);
                    } else {
                        DialogWarning(activity, "Cảnh báo!", "OTP không đúng");
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
        if (mVerificationId != null && !mVerificationId.isEmpty()) {
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

    //chuyển qua đăng nhập
    public void switchToLogin(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    //gửi dữ liệu để kiểm tra
    public void sendDataToServerSU(Map<String, String> data, Activity activity) {
        String phpFilePath = ConnectToServer.getInsert_account_phpFilePath();
        String urlSU = ConnectToServer.getInsert_account_url();

        retrofitManagerSU = new RetrofitManager(urlSU);
        retrofitManagerSU.sendDataToServer(urlSU, phpFilePath, data, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                responseLiveData.setValue(response);
                Toast.makeText(activity, response , Toast.LENGTH_SHORT).show();
                Log.d("response", response.toString());
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
