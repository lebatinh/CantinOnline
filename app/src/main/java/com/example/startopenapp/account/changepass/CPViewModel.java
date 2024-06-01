package com.example.startopenapp.account.changepass;

import static com.example.startopenapp.display_manager.DialogHelper.DialogWarning;
import static com.example.startopenapp.display_manager.TimeHelper.getFormattedTime;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CPViewModel extends ViewModel {
    private RetrofitManager retrofitManagerCP;
    private RetrofitManager retrofitManagerCP1;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;

    public CPViewModel() {
        mAuth = FirebaseAuth.getInstance();
    }
    // Getter cho LiveData để Activity có thể quan sát kết quả xác minh
    public LiveData<Boolean> getVerificationResultLiveData() {
        return verificationResultLiveData;
    }

    public MutableLiveData<Boolean> checkConnectAccount = new MutableLiveData<>();
    private MutableLiveData<Boolean> verificationResultLiveData = new MutableLiveData<>();
    public MutableLiveData<String> responseLiveData = new MutableLiveData<>();
    public MutableLiveData<String> responseLiveData1 = new MutableLiveData<>();
    private MutableLiveData<String> phoneNumber = new MutableLiveData<>();
    private MutableLiveData<String> oldpass = new MutableLiveData<>();
    private MutableLiveData<String> stdid = new MutableLiveData<>();
    private MutableLiveData<String> newpass = new MutableLiveData<>();
    private MutableLiveData<String> renewpass = new MutableLiveData<>();

    public MutableLiveData<Boolean> getCheckConnectAccount() {
        return checkConnectAccount;
    }

    public MutableLiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(MutableLiveData<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public MutableLiveData<String> getOldpass() {
        return oldpass;
    }

    public void setOldpass(MutableLiveData<String> oldpass) {
        this.oldpass = oldpass;
    }

    public MutableLiveData<String> getStdid() {
        return stdid;
    }

    public void setStdid(MutableLiveData<String> stdid) {
        this.stdid = stdid;
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

    //chuyển màn hình sang phần xác nhận mật khẩu mới
    public void switchToConfirmCP(Activity activity) {
        CPModel cpModel = new CPModel(phoneNumber.getValue(), oldpass.getValue(),stdid.getValue(), newpass.getValue(), renewpass.getValue());
        if (!cpModel.isValidPhone()) {
            DialogWarning(activity, "Cảnh báo!", "Định dạng số điện thoại không đúng!");
        } else if (!cpModel.isValidOldPass()) {
            DialogWarning(activity, "Cảnh báo!", "Mật khẩu phải từ 6 kí tự trở lên\n" +
                    "và không có chữ in hoa hoặc kí tự đặc biệt!");
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
                .setTitle("Bạn hãy nhập mã OTP vừa được gửi về số điện thoại của bạn")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    String otp = Objects.requireNonNull(otpEditText.getText()).toString().trim();
                    //điều kiện xét otp
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

    // xác minh số điện thoại
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // thành công
                        verificationResultLiveData.setValue(true);
                    } else {
                        // thất bại
                        verificationResultLiveData.setValue(false);
                    }
                });
    }
    //xác nhận đổi mật khẩu
    public void onClickChangePass(Activity activity) {
        String phonenumber = getPhoneNumber().getValue();
        String newpassword = getNewpass().getValue();
        String stdID       = getStdid().getValue();

        CPModel cpModel = new CPModel(phonenumber, oldpass.getValue(), stdID, newpassword, renewpass.getValue());
        if (!cpModel.isValidNewPass()) {
            DialogWarning(activity, "Cảnh báo!", "Mật khẩu mới phải từ 6 kí tự trở lên\n" +
                    "và không có chữ in hoa hoặc kí tự đặc biệt!");
        } else if (!cpModel.isValidReNewPass()) {
            DialogWarning(activity, "Cảnh báo!", "Mật khẩu mới nhập lại PHẢI trùng khớp với mật khẩu mới đã nhập trước đó!");
        } else {
            // Gửi dữ liệu lên server khi cần
            Map<String, String> data = new HashMap<>();
            data.put("acc_id", stdID);
            data.put("phonenumber", phonenumber);
            data.put("newpassword", newpassword);
            data.put("history", getFormattedTime());

            sendDataToServerCP1(data, activity);
        }
    }

    //chuyển về màn hình đăng nhập
    public void switchToLogin(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    //gửi dữ liệu để kiểm tra
    public void sendDataToServerCP(Map<String, String> data, Activity activity) {
        String phpFilePath = ConnectToServer.getSelect_account_phpFilePath();
        String urlCP = ConnectToServer.getSelect_account_url();

        retrofitManagerCP = new RetrofitManager(urlCP);
        retrofitManagerCP.sendDataToServer(urlCP, phpFilePath, data, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("response", response);
                responseLiveData.setValue(response);
                if (Objects.equals(response, "Đăng nhập tài khoản thành công!")){
                    checkConnectAccount.setValue(true);
                }
                else if (Objects.equals(response, "Đăng nhập thất bại!\nVui lòng kiểm tra lại dữ liệu nhập!")){
                    Toast.makeText(activity, response, Toast.LENGTH_SHORT).show();
                    checkConnectAccount.setValue(false);
                }else if (Objects.equals(response, "Đã có lỗi xảy ra!")){
                    Toast.makeText(activity, response, Toast.LENGTH_SHORT).show();
                    checkConnectAccount.setValue(false);
                }else {
                    Toast.makeText(activity, "Lỗi xác minh tài khoản!", Toast.LENGTH_SHORT).show();
                    checkConnectAccount.setValue(false);
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(activity, "Gửi yêu cầu thất bại.\nHãy kiểm tra đường truyền mạng!", Toast.LENGTH_SHORT).show();
                Log.d("error", error);
            }
        });
    }

    //gửi dữ liệu để đổi mật khẩu
    private void sendDataToServerCP1(Map<String, String> data, Activity activity) {
        String phpFilePath1 = ConnectToServer.getUpdate_account_phpFilePath();
        String urlCP1 = ConnectToServer.getUpdate_account_url();

        retrofitManagerCP1 = new RetrofitManager(urlCP1);
        retrofitManagerCP1.sendDataToServer(urlCP1, phpFilePath1, data, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                responseLiveData1.setValue(response);
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
