package com.example.startopenapp.main.home.order;

import static com.example.startopenapp.display_manager.TimeHelper.getFormattedTime;
import static com.example.startopenapp.display_manager.TimeHelper.pickTime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.startopenapp.R;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyProduct extends AppCompatActivity {
    private String acc_id, payment_method;
    private EditText edtName, edtPhone, edtNote;
    private TextView tvAccId, tvTime, tvPrice, tvPayOnline, tvPayOffline;
    private Button btnOrder;
    private RecyclerView rcvOrder;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private RetrofitManager retrfitManagerBP;
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_product);

        initData();
        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        acc_id = intent.getStringExtra("acc_id");
        ArrayList<String> selectedItemIds = intent.getStringArrayListExtra("selected_item_ids");

        Log.d("selectedItemIds", String.valueOf(selectedItemIds));
        tvAccId.setText(acc_id);

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

        tvTime.setOnClickListener(view -> pickTime(BuyProduct.this, tvTime));
        rcvOrder.setHasFixedSize(true);
        rcvOrder.setLayoutManager(new LinearLayoutManager(BuyProduct.this));

        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList);
        rcvOrder.setAdapter(itemAdapter);

        for (String itemId : selectedItemIds) {
            Map<String, String> dataToSend = new HashMap<>();
            dataToSend.put("acc_id", acc_id);
            dataToSend.put("item_id", itemId);
            itemList.clear();
            getInfoItem(dataToSend);
        }

        btnOrder.setOnClickListener(view -> {
            if (checkValid()){
                Map<String, String> data= new HashMap<>();
                data.put("acc_id", acc_id);
                data.put("receiver_name", edtName.getText().toString().trim());
                data.put("phonenumber", edtPhone.getText().toString().trim());
                data.put("order_content", itemAdapter.generateItemStringList());
                data.put("total_payment_amount", tvPrice.getText().toString().trim());
                data.put("order_time", tvTime.getText().toString().trim());
                data.put("note", edtNote.getText().toString().trim());
                data.put("payment_method", payment_method);
                if ("Tiền tài khoản".equals(payment_method)){
                    data.put("pay_time", getFormattedTime());
                }
                data.put("order_status", "Đặt đơn");

                orderNewProduct(data);
            }else {
                Toast.makeText(BuyProduct.this, "Bạn phải điền đẩy đủ thông tin!", Toast.LENGTH_SHORT).show();
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

        RetrofitManager retrfitManagerBP = new RetrofitManager(urlBuy);
        retrfitManagerBP.sendDataToServer(urlBuy, phpFilePath, data, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("responseBP", response);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    if (responseObject.has("message")) {
                        String message = responseObject.getString("message");
                        if (message.equals("Thanh toán thành công")) {
                            Toast.makeText(BuyProduct.this, message, Toast.LENGTH_SHORT).show();

                            if (responseObject.has("data")) {
                                JSONObject dataObject = responseObject.getJSONObject("data");
                                String order_id = dataObject.getString("order_id");

                                // Chuyển sang màn hình InforOrder
                                Intent intent = new Intent(BuyProduct.this, InforOrder.class);
                                intent.putExtra("order_id", order_id);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(BuyProduct.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(BuyProduct.this, "Lỗi không xác định!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BuyProduct.this, "Thanh toán thất bại.\nHãy kiểm tra số dư tài khoản của bạn!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(BuyProduct.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("errorBP", error);
            }
        });
    }


    private Boolean checkValid() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String time = tvTime.getText().toString().trim();
        String price = tvPrice.getText().toString().trim();
        Integer quantity = itemList.size();
        return !name.isEmpty() && !phone.isEmpty() && !time.isEmpty() && !price.isEmpty() && (quantity != 0);
    }

    private void initData() {
        tvAccId = findViewById(R.id.tvAccId);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        tvTime = findViewById(R.id.tvTime);
        rcvOrder = findViewById(R.id.rcvOrder);
        tvPrice = findViewById(R.id.tvPrice);
        edtNote = findViewById(R.id.edtNote);
        tvPayOnline = findViewById(R.id.tvPayOnline);
        tvPayOffline = findViewById(R.id.tvPayOffline);
        btnOrder = findViewById(R.id.btnOrder);
    }

    private void getInfoItem(Map<String, String> dataToSend) {
        String phpFilePath = ConnectToServer.getSelect_cart_phpFilePath();
        String urlBuy = ConnectToServer.getSelect_cart_url();

        retrfitManagerBP = new RetrofitManager(urlBuy);
        retrfitManagerBP.sendDataToServer(urlBuy, phpFilePath, dataToSend, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("responseBP", response);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    // Kiểm tra xem dữ liệu có chứa thông báo lỗi không
                    if (responseObject.has("message")) {
                        String errorMessage = responseObject.getString("message");
                        // thông báo lỗi
                        Toast.makeText(BuyProduct.this, errorMessage, Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        JSONArray jsonArray = responseObject.getJSONArray("data");

                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        String hinhanh = jsonObject.getString("hinhanh");
                        String item_id = jsonObject.getString("item_id");
                        String item_name = jsonObject.getString("item_name");
                        String quantity = jsonObject.getString("quantity");
                        String price = jsonObject.getString("price");
                        Integer totalprice = (Integer.parseInt(quantity))*(Integer.parseInt(price));

                        // Chuyển chuỗi Base64 thành mảng byte
                        byte[] hinhBytes = Base64.decode(hinhanh, Base64.DEFAULT);

                        // Xóa dữ liệu trước đó và thêm người mới
                        itemList.add(new Item(hinhBytes, item_id, item_name, quantity, totalprice));
                        tvPrice.setText(String.valueOf(itemAdapter.getTotalPrice()));
                        runOnUiThread(() -> {
                            // Cập nhật RecyclerView
                            itemAdapter.setOrderList(itemList);
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BuyProduct.this, "Lỗi phân tích dữ liệu!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(BuyProduct.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("errorBP", error);
            }
        });
    }
}