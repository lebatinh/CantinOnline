package com.example.startopenapp.main.main_screen.notification.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class ImportantFragment extends Fragment {
    private String stdId;
    RecyclerView rcvImportant;
    private List<Noti> listNoti;
    private NotiAdapter notiAdapter;
    private RetrofitManager retrofitManagerIF;

    public ImportantFragment() {}

    public static ImportantFragment newInstance(String stdId) {
        ImportantFragment fragment = new ImportantFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_important, container, false);

        listNoti = new ArrayList<>();

        rcvImportant = view.findViewById(R.id.rcvImportant);
        rcvImportant.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        rcvImportant.setLayoutManager(linearLayoutManager);

        notiAdapter = new NotiAdapter(new ArrayList<>());
        rcvImportant.setAdapter(notiAdapter);

        // Gửi dữ liệu lên server
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("receiver", stdId);
        dataToSend.put("type", "Quan trọng");
        GetImportantNoti(dataToSend);

        SwipeRefreshLayout swipeRefreshLayout1 = view.findViewById(R.id.swipeRefreshLayout1);
        swipeRefreshLayout1.setOnRefreshListener(() -> {
            // Gửi dữ liệu lên server
            Map<String, String> data = new HashMap<>();
            data.put("receiver", stdId);
            data.put("type", "Quan trọng");
            GetImportantNoti(data);
        });
        return view;
    }

    public void GetImportantNoti(Map<String, String> data) {
        String phpFilePathNoti = ConnectToServer.getSelect_noti_phpFilePath();
        String urlIF= ConnectToServer.getSelect_noti_url();

        retrofitManagerIF = new RetrofitManager(urlIF);
        retrofitManagerIF.sendDataToServer(urlIF, phpFilePathNoti, data, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.has("message")) {
                        String errorMessage = responseObject.getString("message");
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        JSONArray jsonArray = responseObject.getJSONArray("data");
                        listNoti.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String title = jsonObject.getString("title");
                            String noti = jsonObject.getString("notification");
                            String time = jsonObject.getString("timepost");

                            listNoti.add(new Noti(title, noti, time));
                        }

                        notiAdapter.setListNoti(listNoti);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("e", e.getMessage());
                    Toast.makeText(requireContext(), "Lỗi phân tích dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Log.d("error", error);
                Toast.makeText(requireContext(), "Lỗi gửi yêu cầu cho máy chủ!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
