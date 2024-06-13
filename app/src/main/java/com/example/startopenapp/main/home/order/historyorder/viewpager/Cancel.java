package com.example.startopenapp.main.home.order.historyorder.viewpager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.startopenapp.R;
import com.example.startopenapp.main.home.order.historyorder.model.Orders;
import com.example.startopenapp.main.home.order.historyorder.model.OrdersAdapter;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cancel extends Fragment {
    String stdId;
    private List<Orders> ordersList;
    private OrdersAdapter adapter;
    private RetrofitManager retrofitManagerB;
    private RecyclerView rcvCancel;
    public Cancel() {}

    public static Cancel newInstance(String stdId) {
        Cancel fragment = new Cancel();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cancel_order, container, false);
        rcvCancel = view.findViewById(R.id.rcvCancel);
        rcvCancel.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        rcvCancel.setLayoutManager(layoutManager);

        ordersList = new ArrayList<>();
        adapter = new OrdersAdapter(new ArrayList<>());
        rcvCancel.setAdapter(adapter);

        // Gửi dữ liệu lên server
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("acc_id", stdId);
        dataToSend.put("order_status", "Từ chối");
        GetCancel(dataToSend);
        return view;
    }

    private void GetCancel(Map<String, String> dataToSend) {
        String phpFilePath = ConnectToServer.getSelect_orders_history_phpFilePath();
        String url = ConnectToServer.getSelect_orders_history_url();

        // Khởi tạo RetrofitManager với baseUrl
        retrofitManagerB = new RetrofitManager(url);
        retrofitManagerB.sendDataToServer(url, phpFilePath, dataToSend, new RetrofitManager
                .DataCallback() {

            @Override
            public void onSuccess(String response) {
                Log.d("response", response);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    // Kiểm tra xem dữ liệu có chứa thông báo lỗi không
                    if (responseObject.has("message")) {
                        String errorMessage = responseObject.getString("message");
                        // thông báo lỗi
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        JSONArray jsonArray = responseObject.getJSONArray("data");
                        ordersList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String order_id = jsonObject.getString("order_id");
                            String total_payment_amount = jsonObject.getString("total_payment_amount");
                            String content = jsonObject.getString("order_content");

                            // Chuyển đổi nội dung đơn hàng thành SpannableString để hiển thị xuống dòng
                            SpannableString order_content = new SpannableString(content.replace("\\n", "\n"));

                            String payment_method = jsonObject.getString("payment_method");
                            String complete_time = jsonObject.getString("complete_time");

                            // Xóa dữ liệu trước đó và thêm người mới
                            ordersList.add(new Orders(order_id, total_payment_amount, order_content.toString(), payment_method, complete_time));
                        }
                        // Cập nhật RecyclerView
                        adapter.setOrdersList(ordersList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Lỗi phân tích dữ liệu!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Log.d("error", error);
                Toast.makeText(requireContext(), "Lỗi gửi yêu cầu cho máy chủ!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}