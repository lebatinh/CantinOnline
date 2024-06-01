package com.example.startopenapp.admin.approve_request.viewpager;

import static com.example.startopenapp.display_manager.DialogHelper.DialogWarning1;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.startopenapp.R;
import com.example.startopenapp.admin.approve_request.recharge_list.Approve;
import com.example.startopenapp.admin.approve_request.recharge_list.ApproveAdapter;
import com.example.startopenapp.databinding.FragmentRechargeBinding;
import com.example.startopenapp.databinding.FragmentWithdrawBinding;
import com.example.startopenapp.display_manager.TimeHelper;
import com.example.startopenapp.main.main_screen.account.money.MoneyViewModel;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WithdrawFragment extends Fragment implements ApproveAdapter.OnItemLongClickListener {
    private String stdId;
    private List<Approve> approveList;
    private MoneyViewModel moneyViewModel;
    private ApproveAdapter approveAdapter;
    private RetrofitManager retrofitManagerRF;
    public WithdrawFragment() {}

    public static WithdrawFragment newInstance(String stdId) {
        WithdrawFragment fragment = new WithdrawFragment();
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
        FragmentWithdrawBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_withdraw, container, false);

        moneyViewModel = new ViewModelProvider(this).get(MoneyViewModel.class);
        binding.setWithdrawViewModel(moneyViewModel);
        binding.setLifecycleOwner(this);

        moneyViewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        binding.rcvWithdraw.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.rcvWithdraw.setLayoutManager(layoutManager);

        // Khởi tạo list
        approveList = new ArrayList<>();
        approveAdapter = new ApproveAdapter(new ArrayList<>(), false);
        approveAdapter.setOnItemLongClickListener(this);
        binding.rcvWithdraw.setAdapter(approveAdapter);

        // Gửi dữ liệu lên server
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("type", "rút tiền");
        dataToSend.put("status", "chờ duyệt");
        GetWithdraw(dataToSend);

        return binding.getRoot();
    }
    @Override
    public void onItemLongClick(Approve approve) {
        String accId = approve.getAccid();
        String transactionId = approve.getMadon();
        showDialog(accId, transactionId);
    }
    private void showDialog(String accId, String transactionId) {
        // Tạo dialog
        DialogWarning1(requireContext(), "Bạn muốn làm gì?",
                "Từ chối đơn", (dialog, which) -> huyRecharge(accId, transactionId),
                "Phê duyệt đơn", (dialog, which) -> duyetRecharge(accId, transactionId));
    }
    public void duyetRecharge(String acc_id, String transaction_id){
        // Gửi dữ liệu lên server
        Map<String, String> dataRecharge = new HashMap<>();
        dataRecharge.put("acc_id", acc_id);
        dataRecharge.put("transaction_id", transaction_id);
        dataRecharge.put("history", TimeHelper.getFormattedTime());
        dataRecharge.put("status", "đã duyệt");
        dataRecharge.put("type", "rút tiền");

        moneyViewModel.recharge(dataRecharge);
    }
    public void huyRecharge(String acc_id, String transaction_id){
        // Gửi dữ liệu lên server
        Map<String, String> dataRecharge = new HashMap<>();
        dataRecharge.put("acc_id", acc_id);
        dataRecharge.put("transaction_id", transaction_id);
        dataRecharge.put("history", TimeHelper.getFormattedTime());
        dataRecharge.put("status", "từ chối");
        dataRecharge.put("type", "rút tiền");

        moneyViewModel.recharge(dataRecharge);
    }
    public void GetWithdraw(Map<String, String> data) {
        String phpFilePathNoti = ConnectToServer.getSelect_approve_request_phpFilePath();
        String urlWF = ConnectToServer.getSelect_approve_request_url();

        // Khởi tạo RetrofitManager với baseUrl
        retrofitManagerRF = new RetrofitManager(urlWF);
        retrofitManagerRF.sendDataToServer(urlWF, phpFilePathNoti, data, new RetrofitManager
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
                        approveList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String acc_id = jsonObject.getString("acc_id");
                            String transaction_id = jsonObject.getString("transaction_id");
                            String sotien = jsonObject.getString("sotien");
                            String history = jsonObject.getString("history");
                            String loinhan = jsonObject.getString("loinhan");

                            // Xóa dữ liệu trước đó và thêm người mới
                            approveList.add(new Approve(acc_id,transaction_id, sotien, history, loinhan));
                        }
                        // Cập nhật RecyclerView
                        approveAdapter.setApproveList(approveList);
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