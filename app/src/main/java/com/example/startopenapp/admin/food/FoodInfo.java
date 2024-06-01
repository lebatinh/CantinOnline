package com.example.startopenapp.admin.food;

import static com.example.startopenapp.display_manager.ImageHelper.convertImageViewToBase64;
import static com.example.startopenapp.display_manager.ImageHelper.decodeBase64ToBitmap;
import static com.example.startopenapp.display_manager.ImageHelper.openCamera;
import static com.example.startopenapp.display_manager.ImageHelper.openGallery;
import static com.example.startopenapp.display_manager.ImageHelper.requestCameraPermission;
import static com.example.startopenapp.display_manager.ImageHelper.requestGalleryPermission;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.startopenapp.R;
import com.example.startopenapp.display_manager.ImageHelper;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FoodInfo extends AppCompatActivity {
    private String acc_id, item_id;
    private ImageView imgItemFI;
    private EditText edtNameVPFI, edtIntroduceVPFI, edtPriceVPFI;
    private TextView tvIDVPFI, tvTypeVPFI;
    private NetworkChangeReceiver networkChangeReceiver;
    private Button btnChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_info);

        initData();

        Intent intent = getIntent();
        acc_id = intent.getStringExtra("acc_id");
        item_id = intent.getStringExtra("item_id");

        imgItemFI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageSelectorDialog(FoodInfo.this, imgItemFI);
            }
        });
        // Gửi dữ liệu lên server
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("item_id", item_id);
        getInfoItem(dataToSend);

        btnChange.setOnClickListener(view -> {
            // Gửi dữ liệu lên server
            Map<String, String> data = new HashMap<>();
            data.put("product_type", tvTypeVPFI.getText().toString().trim());
            data.put("item_id", tvIDVPFI.getText().toString().trim());
            data.put("item_name", edtNameVPFI.getText().toString().trim());
            data.put("hinhanh", convertImageViewToBase64(imgItemFI));
            data.put("introduce", edtIntroduceVPFI.getText().toString().trim());
            data.put("price", edtPriceVPFI.getText().toString().trim());

            changeInfoItem(data);
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

    private void changeInfoItem(Map<String, String> data) {
        String phpFilePath = ConnectToServer.getUpdate_item_phpFilePath();
        String url = ConnectToServer.getUpdate_item_url();

        RetrofitManager retrofitManagerFI = new RetrofitManager(url);
        retrofitManagerFI.sendDataToServer(url, phpFilePath, data, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("response", response);
                Toast.makeText(FoodInfo.this, response, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(FoodInfo.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("errorHF", error);
            }
        });
    }

    private void getInfoItem(Map<String, String> dataToSend) {
        String phpFilePath = ConnectToServer.getSelect_single_item_phpFilePath();
        String url = ConnectToServer.getSelect_single_item_url();

        RetrofitManager retrofitManagerFI = new RetrofitManager(url);
        retrofitManagerFI.sendDataToServer(url, phpFilePath, dataToSend, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("response", response);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    // Kiểm tra xem dữ liệu có chứa thông báo lỗi không
                    if (responseObject.has("message")) {
                        String errorMessage = responseObject.getString("message");
                        // thông báo lỗi
                        Toast.makeText(FoodInfo.this, errorMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        // Dữ liệu hợp lệ
                        JSONArray jsonArray = responseObject.getJSONArray("data");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        String product_type = jsonObject.getString("product_type");
                        String item_name = jsonObject.getString("item_name");
                        String hinhanh = jsonObject.getString("hinhanh");
                        String introduce = jsonObject.getString("introduce");
                        String price = jsonObject.getString("price");

                        Bitmap bitmap = decodeBase64ToBitmap(hinhanh);

                        imgItemFI.setImageBitmap(bitmap);
                        tvTypeVPFI.setText(product_type);
                        tvIDVPFI.setText(item_id);
                        edtNameVPFI.setText(item_name);
                        edtIntroduceVPFI.setText(introduce);
                        edtPriceVPFI.setText(price);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(FoodInfo.this, "Error parsing response!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(FoodInfo.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("errorHF", error);
            }
        });
    }

    private void showImageSelectorDialog(Activity activity, ImageView imageView) {
        // Hiển thị dialog để chọn giữa Camera và Thư viện
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Chọn ảnh")
                .setItems(new CharSequence[]{"1)Chọn ảnh từ thư viện (nên dùng)", "2)Chụp ảnh\n(xoay ngang điện thoại ngược chiều kim đồng hồ)"}, (dialog, which) -> {
                    if (which == 0) {
                        requestGalleryPermission(activity, imageView);
                    } else {
                        requestCameraPermission(activity, imageView);
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageHelper.onActivityResult(requestCode, resultCode, data, this);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == ImageHelper.REQUEST_CODE_CAMERA) {
                openCamera(this);
            } else if (requestCode == ImageHelper.REQUEST_CODE_FOLDER) {
                openGallery(this);
            }
        } else {
            Toast.makeText(this, "Bạn cần cấp quyền để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
        }
    }

    private void initData() {
        imgItemFI = findViewById(R.id.imgItemFI);
        edtNameVPFI = findViewById(R.id.edtNameVPFI);
        edtIntroduceVPFI = findViewById(R.id.edtIntroduceVPFI);
        edtPriceVPFI = findViewById(R.id.edtPriceVPFI);
        tvIDVPFI = findViewById(R.id.tvIDVPFI);
        tvTypeVPFI = findViewById(R.id.tvTypeVPFI);
        btnChange = findViewById(R.id.btnChange);
    }
}