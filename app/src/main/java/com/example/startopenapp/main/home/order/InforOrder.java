package com.example.startopenapp.main.home.order;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.startopenapp.R;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InforOrder extends AppCompatActivity {
    private TextView tvOrderId, tvAccId, tvName, tvPhone, tvTime, tvOrderContent, tvPrice, tvNote, tvPaymentMethod, tvPaymentTime;
    private LinearLayout lnrPaymentTime;
    private RetrofitManager retrofitManagerIO;
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor_order);

        Intent intent = getIntent();
        String order_id = intent.getStringExtra("order_id");

        initData();
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("order_id", order_id);
        getInfoOrder(dataToSend);
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

    private void initData() {
        tvOrderId = findViewById(R.id.tvOrderId);
        tvAccId = findViewById(R.id.tvAccId);
        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvTime = findViewById(R.id.tvTime);
        tvOrderContent = findViewById(R.id.tvOrderContent);
        tvPrice = findViewById(R.id.tvPrice);
        tvNote = findViewById(R.id.tvNote);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvPaymentTime = findViewById(R.id.tvPaymentTime);
        lnrPaymentTime = findViewById(R.id.lnrPaymentTime);
    }

    private void getInfoOrder(Map<String, String> dataToSend) {
        String phpFilePath = ConnectToServer.getSelect_orders_phpFilePath();
        String urlBuy = ConnectToServer.getSelect_orders_url();

        retrofitManagerIO = new RetrofitManager(urlBuy);
        retrofitManagerIO.sendDataToServer(urlBuy, phpFilePath, dataToSend, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("responseIO", response);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    // Kiểm tra xem dữ liệu có chứa thông báo lỗi không
                    if (responseObject.has("message")) {
                        String errorMessage = responseObject.getString("message");
                        // thông báo lỗi
                        Toast.makeText(InforOrder.this, errorMessage, Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        JSONArray jsonArray = responseObject.getJSONArray("data");

                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        String order_id = jsonObject.getString("order_id");
                        String acc_id = jsonObject.getString("acc_id");
                        String receiver_name = jsonObject.getString("receiver_name");
                        String phonenumber = jsonObject.getString("phonenumber");
                        String order_content = jsonObject.getString("order_content");
                        String total_payment_amount = jsonObject.getString("total_payment_amount");
                        String order_time = jsonObject.getString("order_time");
                        String note = jsonObject.getString("note");
                        String payment_method = jsonObject.getString("payment_method");
                        String pay_time = jsonObject.getString("pay_time");

                        tvOrderId.setText(order_id);
                        tvAccId.setText(acc_id);
                        tvName.setText(receiver_name);
                        tvPhone.setText(phonenumber);
                        tvTime.setText(order_time);

                        // Chuyển đổi nội dung đơn hàng thành SpannableString để hiển thị xuống dòng
                        SpannableString orderContentSpannable = new SpannableString(order_content.replace("\\n", "\n"));
                        tvOrderContent.setText(orderContentSpannable);

                        tvPrice.setText(total_payment_amount);
                        tvNote.setText(note);
                        tvPaymentMethod.setText(payment_method);
                        if (pay_time != null && pay_time.isEmpty()){
                            lnrPaymentTime.setVisibility(View.GONE);
                        }else {
                            lnrPaymentTime.setVisibility(View.VISIBLE);
                            tvPaymentTime.setText(pay_time);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(InforOrder.this, "Lỗi phân tích dữ liệu!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(InforOrder.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("errorIO", error);
            }
        });
    }
}