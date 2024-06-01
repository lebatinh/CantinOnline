package com.example.startopenapp.main.history;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.startopenapp.R;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment {
    private String stdId;
    private HistoryAdapter historyAdapter;
    private List<History> historyList;
    private SearchView searchView;
    private RecyclerView rcvHistory;
    public HistoryFragment() {}

    public static HistoryFragment newInstance(String stdId) {
        HistoryFragment fragment = new HistoryFragment();
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

        System.out.println(stdId);
        GetHistory(dataToSend);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        rcvHistory = view.findViewById(R.id.rcvHistory);
        rcvHistory.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        rcvHistory.setLayoutManager(linearLayoutManager);

        // Khởi tạo list
        historyList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(new ArrayList<>());
        rcvHistory.setAdapter(historyAdapter);

        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                performSearch(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                performSearch(s);
                return true;
            }
        });
        return view;
    }

    private void performSearch(String query) {
        ArrayList<History> searchResults = new ArrayList<>();

        for (History history : historyList.toArray(new History[0])) {
            if (history.getItemType().toLowerCase().contains(query.toLowerCase()) ||
                    history.getItemReceiver().toLowerCase().contains(query.toLowerCase()) ||
                    history.getItemSotienGD().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(history);
            }
        }
        historyAdapter.updateData(searchResults);
    }
    private void GetHistory(Map<String, String> data) {
        String phpFilePathNoti = ConnectToServer.getSelect_balance_change_phpFilePath();
        String urlHF = ConnectToServer.getSelect_balance_change_url();

        // Khởi tạo RetrofitManager với baseUrl
        RetrofitManager retrofitManagerHF = new RetrofitManager(urlHF);
        retrofitManagerHF.sendDataToServer(urlHF, phpFilePathNoti, data, new RetrofitManager
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
                        historyList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String type = jsonObject.getString("type");
                            String receiver = jsonObject.getString("receiver");
                            String time_transaction = jsonObject.getString("time_transaction");
                            String status = jsonObject.getString("status");
                            String money_transaction = jsonObject.getString("money_transaction");

                            // Xóa dữ liệu trước đó và thêm người mới
                            historyList.add(new History(type, receiver, time_transaction, status, money_transaction));
                        }
                        // Cập nhật RecyclerView
                        historyAdapter.setHistoryList(historyList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Lỗi phân tích dữ liệu!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Log.d("errorHisF", error);
                Toast.makeText(requireContext(), "Lỗi gửi yêu cầu cho máy chủ!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}