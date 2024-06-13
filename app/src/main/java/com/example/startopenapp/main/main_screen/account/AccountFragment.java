package com.example.startopenapp.main.main_screen.account;

import static com.example.startopenapp.display_manager.ImageHelper.decodeBase64ToBitmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.startopenapp.R;
import com.example.startopenapp.account.changepass.ChangePassActivity;
import com.example.startopenapp.account.login.LoginActivity;
import com.example.startopenapp.main.home.order.historyorder.HistoryOrders;
import com.example.startopenapp.main.main_screen.account.money.MoneyManagement;
import com.example.startopenapp.main.account.VerifyAccountActivity;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.opencensus.resource.Resource;

public class AccountFragment extends Fragment {
    private Boolean verifyAcc;
    private String stdId;
    private TextView tvVerifyAcc, tvLoginSecurity, tvHelp, tvLogout, tvChangePass, tvMoneyManage, tvBuyHistory;
    private Spinner spLang;
    private static final String[] languages = {"Tiếng việt","English"};
    private RetrofitManager retrofitManagerHome;

    public AccountFragment() {}

    public static AccountFragment newInstance(String stdId) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString("std_id", stdId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stdId = getArguments().getString("std_id");
        }

        // Gửi dữ liệu lên server
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("acc_id", stdId);

        sendDataToServerAF(dataToSend);
        checkVerifyAcc(dataToSend);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        tvVerifyAcc = view.findViewById(R.id.tvVerifyAcc);

        tvVerifyAcc.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), VerifyAccountActivity.class);
            intent.putExtra("acc_id", stdId);
            intent.putExtra("verified", verifyAcc);
            startActivity(intent);
        });
        tvLoginSecurity = view.findViewById(R.id.tvLoginSecurity);
        tvLoginSecurity.setOnClickListener(view12 -> {
            Intent intent = new Intent(getActivity(), LoginSecurity.class);
            intent.putExtra("acc_id", stdId);
            startActivity(intent);
        });

        tvHelp = view.findViewById(R.id.tvHelp);
        tvHelp.setOnClickListener(view13 -> {
            Intent intent = new Intent(getActivity(), HelpActivity.class);
            intent.putExtra("acc_id", stdId);
            startActivity(intent);
        });

        tvLogout = view.findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(view14 -> startActivity(new Intent(getActivity(), LoginActivity.class)));

        tvChangePass = view.findViewById(R.id.tvChangePass);
        tvChangePass.setOnClickListener(view15 -> startActivity(new Intent(getActivity(), ChangePassActivity.class)));

        tvMoneyManage = view.findViewById(R.id.tvMoneyManage);
        tvMoneyManage.setOnClickListener(view16 -> {
            Intent intent = new Intent(getActivity(), MoneyManagement.class);
            intent.putExtra("acc_id", stdId);
            startActivity(intent);
        });
        tvBuyHistory = view.findViewById(R.id.tvBuyHistory);
        tvBuyHistory.setOnClickListener(view17 -> {
            Intent intent = new Intent(getActivity(), HistoryOrders.class);
            intent.putExtra("acc_id", stdId);
            startActivity(intent);
        });

        spLang = view.findViewById(R.id.spLang);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLang.setAdapter(adapter);
        // Đọc ngôn ngữ đã lưu và đặt giá trị cho Spinner
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String savedLang = sharedPreferences.getString("My_Lang", "vi"); // Mặc định là tiếng Việt
        if (savedLang.equals("vi")) {
            spLang.setSelection(0);
        } else if (savedLang.equals("en")) {
            spLang.setSelection(1);
        }
        spLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedLang = adapterView.getItemAtPosition(i).toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String currentLang = sharedPreferences.getString("My_Lang", "vi");

                if (selectedLang.equals("Tiếng việt") && !currentLang.equals("vi")){
                    editor.putString("My_Lang", "vi");
                    Toast.makeText(requireContext(), "Hãy khởi động lại ứng dụng để thay đổi ngôn ngữ!", Toast.LENGTH_SHORT).show();
                } else if (selectedLang.equals("English") && !currentLang.equals("en")) {
                    editor.putString("My_Lang", "en");
                    Toast.makeText(requireContext(), "Hãy khởi động lại ứng dụng để thay đổi ngôn ngữ!", Toast.LENGTH_SHORT).show();
                }
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }


    //kiểm tra tài khoản
    private void checkVerifyAcc(Map<String, String> data) {
        String phpFilePath = ConnectToServer.getCheck_verify_phpFilePath();
        String urlAF = ConnectToServer.getCheck_verify_url();

        retrofitManagerHome = new RetrofitManager(urlAF);
        retrofitManagerHome.sendDataToServer(urlAF, phpFilePath, data, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("responseHome", response);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    // Kiểm tra xem dữ liệu có chứa thông báo lỗi không
                    if (responseObject.has("message")) {
                        String message = responseObject.getString("message");
                        if ("true".equals(message)) {
                            verifyAcc = true;
                        } else if ("false".equals(message)) {
                            verifyAcc = false;
                        } else {
                            // Thông báo lỗi
                            Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                        }

                        // Cập nhật giao diện sau khi nhận phản hồi
                        updateVerifyAccUI();
                    } else {
                        // Xử lý khi response không có trường "message"
                        Toast.makeText(requireActivity(), "Có vẻ như bạn gặp lỗi!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(requireActivity(), "Lỗi dữ liệu từ máy chủ!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(requireActivity(), "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("errorAF", error);
            }
        });
    }

    // Cập nhật giao diện dựa trên giá trị của verifyAcc
    private void updateVerifyAccUI() {
        // Lấy các drawables hiện tại
        Drawable[] drawables = tvVerifyAcc.getCompoundDrawablesRelative();

        // Lấy các drawables hiện tại của TextView
        Drawable leftDrawable = drawables[0]; // drawableStart
        Drawable topDrawable = drawables[1];
        Drawable rightDrawable = drawables[2]; // drawableEnd
        Drawable bottomDrawable = drawables[3];

        // Chỉ thay đổi drawableEnd
        if (!verifyAcc) {
            rightDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.error_verify);
        } else {
            rightDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.verify_acc_success);
        }

        // Đặt các drawables mới cho TextView
        tvVerifyAcc.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, topDrawable, rightDrawable, bottomDrawable);
        tvVerifyAcc.setCompoundDrawablePadding(8);
    }

    //gửi dữ liệu để kiểm tra
    private void sendDataToServerAF(Map<String, String> data) {
        String phpFilePath = ConnectToServer.getSelect_infor_home_phpFilePath();
        String urlLG = ConnectToServer.getSelect_infor_home_url();

        retrofitManagerHome = new RetrofitManager(urlLG);
        retrofitManagerHome.sendDataToServer(urlLG, phpFilePath, data, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("reponseHome", response);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    // Kiểm tra xem dữ liệu có chứa thông báo lỗi không
                    if (responseObject.has("message")) {
                        String errorMessage = responseObject.getString("message");
                        // thông báo lỗi
                        Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        // Dữ liệu hợp lệ
                        JSONArray jsonArray = responseObject.getJSONArray("data");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String avatarByte = jsonObject.getString("avatar");
                        String name = jsonObject.getString("name");
                        String phone = jsonObject.getString("phonenumber");

                        // Đặt dữ liệu vào các thành phần trong XML
                        setUserData(avatarByte, name, phone);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(requireActivity(), "Error parsing response!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(requireActivity(), "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("error", error);
            }
        });
    }

    private void setUserData(String avatarByte, String name, String stdId) {
        ImageView avatar_fg_acc = requireView().findViewById(R.id.avatar_fg_acc);
        TextView name_fg_acc = requireView().findViewById(R.id.name_fg_acc);
        TextView phone_fg_acc = requireView().findViewById(R.id.phone_fg_acc);

        if (avatarByte != null) {
            Bitmap bitmap = decodeBase64ToBitmap(avatarByte);

            avatar_fg_acc.setImageBitmap(bitmap);
        } else {
            avatar_fg_acc.setImageResource(R.drawable.account);
        }
        name_fg_acc.setText(name);
        phone_fg_acc.setText(stdId);
    }
}