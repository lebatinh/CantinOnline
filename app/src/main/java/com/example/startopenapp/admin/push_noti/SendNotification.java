package com.example.startopenapp.admin.push_noti;

import static com.example.startopenapp.admin.push_noti.setup.FcmNotificationsSender.sendNotificationToTopic;
import static com.example.startopenapp.display_manager.DialogHelper.DialogWarning;
import static com.example.startopenapp.display_manager.TimeHelper.getFormattedTime;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startopenapp.R;
import com.example.startopenapp.admin.push_noti.person_list.Person;
import com.example.startopenapp.admin.push_noti.person_list.PersonAdapter;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.main.main_screen.notification.NotiViewModel;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendNotification extends AppCompatActivity {

    private RetrofitManager retrofitManagerNoti;
    private NotiViewModel notiViewModel;
    private RecyclerView lvChoiceReceiver;
    private PersonAdapter personAdapter;
    private List<Person> personList; // list chứa toàn bộ id người dùng
    private List<String> selectedIdList; //list chứa các id được chọn
    private TextView tvTypeNoti, titleNoti, contentNoti;
    private NetworkChangeReceiver networkChangeReceiver;
    private RadioGroup radiogr;
    private Button btnGuiTb;
    private RadioButton rdAll, rdChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        initView();

        notiViewModel = new ViewModelProvider(this).get(NotiViewModel.class);
        tvTypeNoti.setOnClickListener(this::showPopupMenu);

        lvChoiceReceiver.setHasFixedSize(true);
        lvChoiceReceiver.setLayoutManager(new LinearLayoutManager(this));

        personList = new ArrayList<>();
        personAdapter = new PersonAdapter(personList);
        lvChoiceReceiver.setAdapter(personAdapter);

        // Khởi tạo list
        selectedIdList = new ArrayList<>();

        // Set trạng thái thay đổi cho lvChoiceReceiver khi thành phần của radiogr đc click
        radiogr.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rdAll) {
                lvChoiceReceiver.setVisibility(View.GONE);
            } else if (checkedId == R.id.rdChoice) {
                lvChoiceReceiver.setVisibility(View.VISIBLE);
            }
        });
        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            String id = intent.getStringExtra("id");
            // Gửi dữ liệu lên server
            Map<String, String> dataToSend = new HashMap<>();
            dataToSend.put("acc_id", id);
            GetAllUser(dataToSend);
        }

        btnGuiTb.setOnClickListener(view -> SendNoti());
        notiViewModel.getSelectedTitle().observe(this, title -> tvTypeNoti.setText(title));
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

    private void initView() {
        radiogr = findViewById(R.id.radiogr);
        rdAll = findViewById(R.id.rdAll);
        rdChoice = findViewById(R.id.rdChoice);
        tvTypeNoti = findViewById(R.id.tvTypeNoti);
        titleNoti = findViewById(R.id.titleNoti);
        contentNoti = findViewById(R.id.contentNoti);
        btnGuiTb = findViewById(R.id.btnGuiTb);
        lvChoiceReceiver = findViewById(R.id.lvChoiceReceiver);
    }

    private void SendNoti() {
        getFormattedTime();
        String title = titleNoti.getText().toString();
        String content = contentNoti.getText().toString();

        if (!title.isEmpty() && !content.isEmpty()) {
            if (rdAll.isChecked()) {
                //gửi thông báo
                sendNotificationToTopic("all", title, content);
                SendNotiAll();
            } else if (rdChoice.isChecked()) {
                listSelected();
            } else {
                DialogWarning(SendNotification.this, "Cảnh báo!",
                        "Bạn chưa chọn gửi thông báo với ai!");
            }
        } else {
            DialogWarning(SendNotification.this, "Cảnh báo!",
                    "Bạn chưa viết thông báo mà!");
        }
    }

    private void listSelected() {
        // Lọc ra danh sách các maNv đã được chọn
        selectedIdList.clear();
        for (Person person : personList) {
            if (person.isSelected()) {
                selectedIdList.add(person.getId());
            }
        }

        if (!selectedIdList.isEmpty()) {
            for (String id : selectedIdList) {
                String title = titleNoti.getText().toString();
                String content = contentNoti.getText().toString();

                // Gọi hàm GuiTbChoice và truyền danh sách maNV đã được chọn
                sendNotificationToTopic(id, title, content);
            }
            SendNotiList(selectedIdList);
        } else {
            DialogWarning(SendNotification.this, "Cảnh báo!", "Bạn chưa chọn nhân" +
                    " viên nhận thông báo!");
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(SendNotification.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.noti_nav, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.item_important) {
                notiViewModel.setSelectedTitle("Quan trọng");
                return true;
            } else if (item.getItemId() == R.id.item_endow) {
                notiViewModel.setSelectedTitle("Ưu đãi");
                return true;
            } else if (item.getItemId() == R.id.item_interact) {
                notiViewModel.setSelectedTitle("Tương tác");
                return true;
            } else {
                notiViewModel.setSelectedTitle("Chọn thể loại thông báo");
                return false;
            }
        });

        popupMenu.show();
    }

    public void GetAllUser(Map<String, String> data) {
        String phpFilePathNoti = ConnectToServer.getSelect_user_phpFilePath();
        String urlNoti = ConnectToServer.getSelect_user_url();

        // Khởi tạo RetrofitManager với baseUrl
        retrofitManagerNoti = new RetrofitManager(urlNoti);
        retrofitManagerNoti.sendDataToServer(urlNoti, phpFilePathNoti, data, new RetrofitManager
                .DataCallback() {

            @Override
            public void onSuccess(String response) {
                Log.d("responseSendNoti", response);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    // Kiểm tra xem dữ liệu có chứa thông báo lỗi không
                    if (responseObject.has("message")) {
                        String errorMessage = responseObject.getString("message");
                        // thông báo lỗi
                        Toast.makeText(SendNotification.this, errorMessage, Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        JSONArray jsonArray = responseObject.getJSONArray("data");
                        personList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String avatarByte = jsonObject.getString("avatar");
                            String id = jsonObject.getString("acc_id");
                            String name = jsonObject.getString("name");

                            // Chuyển chuỗi Base64 thành mảng byte
                            byte[] hinhBytes = Base64.decode(avatarByte, Base64.DEFAULT);

                            // Xóa dữ liệu trước đó và thêm người mới
                            personList.add(new Person(id, hinhBytes, name));

                        }// Cập nhật RecyclerView
                        Log.d("PersonListSize", "Size: " + personList.size());
                        // Cập nhật RecyclerView trên Main Thread
                        runOnUiThread(() -> {
                            personAdapter.setPersonList(personList);
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SendNotification.this, "Lỗi phân tích dữ liệu!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Log.d("error", error);
                Toast.makeText(SendNotification.this, "Lỗi gửi yêu cầu cho máy chủ!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SendNotiAll() {
        String phpFilePathNoti = ConnectToServer.getInsert_noti_phpFilePath();
        String urlNoti = ConnectToServer.getInsert_noti_url();

        String type = tvTypeNoti.getText().toString();
        String title = titleNoti.getText().toString();
        String content = contentNoti.getText().toString();
        String timeSend = getFormattedTime();
        // Gửi dữ liệu lên server
        Map<String, String> dataToSend = new HashMap<>();

        dataToSend.put("type", type);
        dataToSend.put("title", title);
        dataToSend.put("notification", content);
        dataToSend.put("timepost", timeSend);
        dataToSend.put("receiver", "all");

        // Khởi tạo RetrofitManager với baseUrl
        retrofitManagerNoti = new RetrofitManager(urlNoti);
        retrofitManagerNoti.sendDataToServer(urlNoti, phpFilePathNoti, dataToSend, new RetrofitManager
                .DataCallback() {

            @Override
            public void onSuccess(String response) {
                Toast.makeText(SendNotification.this, response, Toast.LENGTH_SHORT).show();
                Log.d("response", response);
            }

            @Override
            public void onError(String error) {
                Log.d("error", error);
                Toast.makeText(SendNotification.this, "Lỗi gửi yêu cầu cho máy chủ!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SendNotiList(List<String> selectedMaNvList) {
        String phpFilePathNoti = ConnectToServer.getInsert_noti_phpFilePath();
        String urlNoti = ConnectToServer.getInsert_noti_url();

        String type = tvTypeNoti.getText().toString();
        String title = titleNoti.getText().toString();
        String content = contentNoti.getText().toString();
        String timeSend = getFormattedTime();

        // Gửi dữ liệu lên server
        Map<String, String> dataToSend = new HashMap<>();

        dataToSend.put("type", type);
        dataToSend.put("title", title);
        dataToSend.put("notification", content);
        dataToSend.put("timepost", timeSend);

        // Chuyển danh sách maNV đã chọn thành chuỗi và truyền lên server
        String selectedMaNvJson = new Gson().toJson(selectedMaNvList);
        dataToSend.put("receiver", selectedMaNvJson);

        // Khởi tạo RetrofitManager với baseUrl
        retrofitManagerNoti = new RetrofitManager(urlNoti);
        retrofitManagerNoti.sendDataToServer(urlNoti, phpFilePathNoti, dataToSend, new RetrofitManager
                .DataCallback() {

            @Override
            public void onSuccess(String response) {
                Toast.makeText(SendNotification.this, response, Toast.LENGTH_SHORT).show();
                Log.d("response", response);
            }

            @Override
            public void onError(String error) {
                Log.d("error", error);
                Toast.makeText(SendNotification.this, "Lỗi gửi yêu cầu cho máy chủ!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}