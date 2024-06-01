package com.example.startopenapp.admin.cashier;

import static com.example.startopenapp.admin.push_noti.setup.FcmNotificationsSender.sendNotificationToTopic;
import static com.example.startopenapp.display_manager.DialogHelper.DialogWarning1;
import static com.example.startopenapp.display_manager.TimeHelper.getFormattedTime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.startopenapp.R;
import com.example.startopenapp.admin.approve_request.recharge_list.Approve;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.main.home.order.BuyProduct;
import com.example.startopenapp.main.home.order.ItemAdapter;
import com.example.startopenapp.main.home.product.Product;
import com.example.startopenapp.main.home.product.ProductActivity;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CashierActivity extends AppCompatActivity {
    private RetrofitManager retrofitManagerCA;
    private List<Bill> billList;
    private RecyclerView rcvBill;
    private BillAdapter billAdapter;
    private NetworkChangeReceiver networkChangeReceiver;
    private String accId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        accId = intent.getStringExtra("acc_id");

        rcvBill = findViewById(R.id.rcvBill);
        rcvBill.setHasFixedSize(true);
        rcvBill.setLayoutManager(new LinearLayoutManager(CashierActivity.this));

        billList = new ArrayList<>();
        billAdapter = new BillAdapter(billList);
        rcvBill.setAdapter(billAdapter);

        // Gửi dữ liệu lên server
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("acc_id", accId);
        GetBill(dataToSend);

        billAdapter.setOnItemClickListener(position -> {
            Bill clickedItem = billList.get(position);
            String orderId = clickedItem.getOderId();
            String id = clickedItem.getAccId();
            DialogWarning1(CashierActivity.this, "Bạn muốn duyệt đơn hay hủy bỏ đơn này?",
                    "Hoàn thành", (dialog, which) -> sendAccept(orderId, id),
                    "Từ chối", (dialog, which) -> sendDenied(orderId, id));
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

    private void sendAccept(String orderId, String id) {
        // Gửi dữ liệu lên server
        Map<String, String> data = new HashMap<>();
        data.put("order_id", orderId);
        data.put("order_status", "Hoàn thành");
        data.put("complete_time", getFormattedTime());
        sendRequestAccept(data, id);
    }
    private void sendDenied(String orderId, String id) {
        // Gửi dữ liệu lên server
        Map<String, String> data = new HashMap<>();
        data.put("order_id", orderId);
        data.put("order_status", "Từ chối");
        data.put("complete_time", getFormattedTime());
        sendRequestDenied(data, id);
    }

    private void sendRequestAccept(Map<String, String> data, String id) {
        String phpFilePathNoti = ConnectToServer.getUpdate_order_phpFilePath();
        String urlCA = ConnectToServer.getUpdate_order_url();

        // Khởi tạo RetrofitManager với baseUrl
        retrofitManagerCA = new RetrofitManager(urlCA);
        retrofitManagerCA.sendDataToServer(urlCA, phpFilePathNoti, data, new RetrofitManager
                .DataCallback() {

            @Override
            public void onSuccess(String response) {
                Log.d("responseCARA", response);
                if (response.equals("success")){
                    Toast.makeText(CashierActivity.this, "Duyệt đơn thành công!",
                            Toast.LENGTH_SHORT).show();
                    sendNotificationToTopic(id, "Thông báo tình trạng đơn hàng!", "Đơn hàng của bạn đã hoàn thành. Bạn có thể xuống cantin nhận đơn nhé! Cảm ơn bạn <3");
                }else if (response.equals("fail")){
                    Toast.makeText(CashierActivity.this, "Duyệt đơn thất bại!",
                            Toast.LENGTH_SHORT).show();
                }else if (response.equals("error")){
                    Toast.makeText(CashierActivity.this, "Lỗi yêu cầu duyệt đơn!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Log.d("errorCARA", error);
                Toast.makeText(CashierActivity.this, "Lỗi gửi yêu cầu cho máy chủ!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sendRequestDenied(Map<String, String> data, String id) {
        String phpFilePathNoti = ConnectToServer.getUpdate_order_phpFilePath();
        String urlCA = ConnectToServer.getUpdate_order_url();

        // Khởi tạo RetrofitManager với baseUrl
        retrofitManagerCA = new RetrofitManager(urlCA);
        retrofitManagerCA.sendDataToServer(urlCA, phpFilePathNoti, data, new RetrofitManager
                .DataCallback() {

            @Override
            public void onSuccess(String response) {
                Log.d("responseCARD", response);
                if (response.equals("success")){
                    Toast.makeText(CashierActivity.this, "Từ chối thành công!",
                            Toast.LENGTH_SHORT).show();
                    sendNotificationToTopic(id, "Thông báo tình trạng đơn hàng!", "Đơn hàng của bạn đã bị từ chối. Bạn hãy đặt lại đơn hàng mới nhé! Cảm ơn bạn <3");
                }else if (response.equals("fail")){
                    Toast.makeText(CashierActivity.this, "Từ chối thất bại!",
                            Toast.LENGTH_SHORT).show();
                }else if (response.equals("error")){
                    Toast.makeText(CashierActivity.this, "Lỗi yêu cầu từ chối đơn!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Log.d("errorCARD", error);
                Toast.makeText(CashierActivity.this, "Lỗi gửi yêu cầu cho máy chủ!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void GetBill(Map<String, String> data) {
        String phpFilePathNoti = ConnectToServer.getSelect_bill_phpFilePath();
        String urlCA = ConnectToServer.getSelect_bill_url();

        // Khởi tạo RetrofitManager với baseUrl
        retrofitManagerCA = new RetrofitManager(urlCA);
        retrofitManagerCA.sendDataToServer(urlCA, phpFilePathNoti, data, new RetrofitManager
                .DataCallback() {

            @Override
            public void onSuccess(String response) {
                Log.d("responseCAGet", response);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    // Kiểm tra xem dữ liệu có chứa thông báo lỗi không
                    if (responseObject.has("message")) {
                        String errorMessage = responseObject.getString("message");
                        // thông báo lỗi
                        Toast.makeText(CashierActivity.this, errorMessage, Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        JSONArray jsonArray = responseObject.getJSONArray("data");
                        billList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String order_id = jsonObject.getString("order_id");
                            String acc_id = jsonObject.getString("acc_id");
                            String price = jsonObject.getString("total_payment_amount");
                            String receiver = jsonObject.getString("receiver_name");
                            String phone = jsonObject.getString("phonenumber");
                            String paymentmethod = jsonObject.getString("payment_method");
                            String time = jsonObject.getString("pay_time");

                            // Xóa dữ liệu trước đó và thêm người mới
                            billList.add(new Bill(order_id,acc_id, price, receiver, phone, paymentmethod, time));
                        }
                        // Cập nhật RecyclerView
                        billAdapter.setBillList(billList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CashierActivity.this, "Lỗi phân tích dữ liệu!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Log.d("errorCAGet", error);
                Toast.makeText(CashierActivity.this, "Lỗi gửi yêu cầu cho máy chủ!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}