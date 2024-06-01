package com.example.startopenapp.main.home.order;

import static com.example.startopenapp.display_manager.ImageHelper.decodeBase64ToBitmap;
import static com.example.startopenapp.display_manager.TimeHelper.getFormattedTime;
import static com.example.startopenapp.display_manager.TimeHelper.pickTime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.startopenapp.R;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BuySingleProduct extends AppCompatActivity {
    String acc_id, item_id, payment_method, name, sotien, itemString;
    int quantity;
    private RetrofitManager retrfitManagerBSP;
    ImageView itemImageOder;
    private Button btnOrder;
    private EditText edtName, edtPhone, edtNote;
    private TextView tvAccId, itemQuantityOder, itemIdOder, itemNameOder, itemPriceOder,
            tvTime, tvPrice, tvPayOnline, tvPayOffline;
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_single_product);

        initData();
        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        acc_id = intent.getStringExtra("acc_id");
        item_id = intent.getStringExtra("item_id");
        quantity = intent.getIntExtra("quantity", 0);
        tvAccId.setText(acc_id);
        tvTime.setOnClickListener(view -> pickTime(BuySingleProduct.this, tvTime));

        // Gửi dữ liệu lên server
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("item_id", item_id);
        getInfoItem(dataToSend);

        payment_method = tvPayOnline.getText().toString();
        tvPayOnline.setOnClickListener(view -> {
            tvPayOnline.setBackgroundResource(R.drawable.check_select);
            tvPayOffline.setBackground(null);
            payment_method = tvPayOnline.getText().toString();
        });

        tvPayOffline.setOnClickListener(view -> {
            tvPayOffline.setBackgroundResource(R.drawable.check_select);
            tvPayOnline.setBackground(null);
            payment_method = tvPayOffline.getText().toString();
        });
        btnOrder.setOnClickListener(view -> {
            if (checkValid()) {
                Map<String, String> data = new HashMap<>();
                data.put("acc_id", acc_id);
                data.put("receiver_name", edtName.getText().toString().trim());
                data.put("phonenumber", edtPhone.getText().toString().trim());
                itemString = "ID: " + item_id + " - Tên: " + name
                        + " - Số lượng: " + quantity
                        + " - Giá: " + sotien;
                data.put("order_content", itemString);
                data.put("total_payment_amount", tvPrice.getText().toString().trim());
                data.put("order_time", tvTime.getText().toString().trim());
                data.put("note", edtNote.getText().toString().trim());
                data.put("payment_method", payment_method);
                if ("Tiền tài khoản".equals(payment_method)) {
                    data.put("pay_time", getFormattedTime());
                }
                data.put("order_status", "Đặt đơn");

                orderNewProduct(data);
            } else {
                Toast.makeText(BuySingleProduct.this, "Bạn phải điền đẩy đủ thông tin!", Toast.LENGTH_SHORT).show();
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

    private void orderNewProduct(Map<String, String> data) {
        String phpFilePath = ConnectToServer.getInsert_orders_phpFilePath();
        String urlBuy = ConnectToServer.getInsert_orders_url();

        retrfitManagerBSP = new RetrofitManager(urlBuy);
        retrfitManagerBSP.sendDataToServer(urlBuy, phpFilePath, data, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("responseBSP", response);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    if (responseObject.has("message")) {
                        String message = responseObject.getString("message");
                        if (message.equals("Thanh toán thành công")) {
                            Toast.makeText(BuySingleProduct.this, message, Toast.LENGTH_SHORT).show();

                            if (responseObject.has("data")) {
                                JSONObject dataObject = responseObject.getJSONObject("data");
                                String order_id = dataObject.getString("order_id");

                                // Chuyển sang màn hình InforOrder
                                Intent intent = new Intent(BuySingleProduct.this, InforOrder.class);
                                intent.putExtra("order_id", order_id);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(BuySingleProduct.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(BuySingleProduct.this, "Lỗi không xác định!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BuySingleProduct.this, "Thanh toán thất bại.\nHãy kiểm tra số dư tài khoản của bạn!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(BuySingleProduct.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("errorBP", error);
            }
        });
    }

    private Boolean checkValid() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String time = tvTime.getText().toString().trim();
        String price = tvPrice.getText().toString().trim();
        return !name.isEmpty() && !phone.isEmpty() && !time.isEmpty() && !price.isEmpty() && (quantity != 0);
    }

    private void initData() {
        itemImageOder = findViewById(R.id.itemImageOder);
        itemQuantityOder = findViewById(R.id.itemQuantityOder);
        itemIdOder = findViewById(R.id.itemIdOder);
        itemNameOder = findViewById(R.id.itemNameOder);
        itemPriceOder = findViewById(R.id.itemPriceOder);
        tvAccId = findViewById(R.id.tvAccId);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        tvTime = findViewById(R.id.tvTime);
        tvPrice = findViewById(R.id.tvPrice);
        edtNote = findViewById(R.id.edtNote);
        tvPayOnline = findViewById(R.id.tvPayOnline);
        tvPayOffline = findViewById(R.id.tvPayOffline);
        btnOrder = findViewById(R.id.btnOrder);
    }

    private void getInfoItem(Map<String, String> dataToSend) {
        String phpFilePath = ConnectToServer.getSelect_single_item_phpFilePath();
        String urlBuy = ConnectToServer.getSelect_single_item_url();

        retrfitManagerBSP = new RetrofitManager(urlBuy);
        retrfitManagerBSP.sendDataToServer(urlBuy, phpFilePath, dataToSend, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("responseBSP", response);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    // Kiểm tra xem dữ liệu có chứa thông báo lỗi không
                    if (responseObject.has("message")) {
                        String errorMessage = responseObject.getString("message");
                        // thông báo lỗi
                        Toast.makeText(BuySingleProduct.this, errorMessage, Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        JSONArray jsonArray = responseObject.getJSONArray("data");

                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        String hinhanh = jsonObject.getString("hinhanh");
                        String item_name = jsonObject.getString("item_name");
                        int price = jsonObject.getInt("price");
                        Integer totalprice = quantity * price;

                        Bitmap bitmap = decodeBase64ToBitmap(hinhanh);

                        itemImageOder.setImageBitmap(bitmap);
                        itemQuantityOder.setText(String.valueOf(quantity));
                        itemIdOder.setText(item_id);
                        itemNameOder.setText(item_name);
                        itemPriceOder.setText(String.valueOf(totalprice));
                        tvPrice.setText(String.valueOf(totalprice));

                        name = itemNameOder.getText().toString().trim();
                        sotien = itemPriceOder.getText().toString().trim();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BuySingleProduct.this, "Lỗi phân tích dữ liệu!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(BuySingleProduct.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("errorBP", error);
            }
        });
    }
}