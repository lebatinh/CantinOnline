package com.example.startopenapp.admin.sell;

import static com.example.startopenapp.display_manager.ImageHelper.convertImageViewToBase64;
import static com.example.startopenapp.display_manager.ImageHelper.openCamera;
import static com.example.startopenapp.display_manager.ImageHelper.openGallery;
import static com.example.startopenapp.display_manager.ImageHelper.requestCameraPermission;
import static com.example.startopenapp.display_manager.ImageHelper.requestGalleryPermission;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.startopenapp.R;
import com.example.startopenapp.admin.push_noti.SendNotification;
import com.example.startopenapp.display_manager.ImageHelper;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;

import java.util.HashMap;
import java.util.Map;

public class ForSale extends AppCompatActivity {
    private String accId;
    private ImageView imgItemFS;
    private EditText edtNameVP, edtIntroduceVP, edtPriceVP;
    private Button btnSell;
    private NetworkChangeReceiver networkChangeReceiver;
    private TextView tvTypeVP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_sale);

        initData();
        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        accId = intent.getStringExtra("acc_id");

        imgItemFS.setOnClickListener(view -> showImageSelectorDialog(ForSale.this, imgItemFS));

        tvTypeVP.setOnClickListener(this::showPopupMenu);

        btnSell.setOnClickListener(view -> {
            String type = tvTypeVP.getText().toString().trim();
            String name = edtNameVP.getText().toString().trim();
            String hinhanh = convertImageViewToBase64(imgItemFS).trim();
            String introduce = edtIntroduceVP.getText().toString().trim();
            String price = edtPriceVP.getText().toString().trim();
            if (!type.isEmpty() && !name.isEmpty() && !hinhanh.isEmpty() && !introduce.isEmpty()
            && !price.isEmpty()){
                // Gửi dữ liệu lên server
                Map<String, String> dataToSend = new HashMap<>();
                dataToSend.put("acc_id", accId);
                dataToSend.put("product_type", type);
                dataToSend.put("item_name", name);
                dataToSend.put("hinhanh", hinhanh);
                dataToSend.put("introduce", introduce);
                dataToSend.put("price", price);

                insertVatPham(dataToSend);
            }else {
                Toast.makeText(ForSale.this, "Bạn quên chưa điền vào chỗ nào đó rồi!", Toast.LENGTH_SHORT).show();
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

    private void initData() {
        imgItemFS = findViewById(R.id.imgItemFS);
        tvTypeVP = findViewById(R.id.tvTypeVP);
        edtNameVP = findViewById(R.id.edtNameVP);
        edtIntroduceVP = findViewById(R.id.edtIntroduceVP);
        edtPriceVP = findViewById(R.id.edtPriceVP);
        btnSell = findViewById(R.id.btnSell);
    }
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(ForSale.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.type, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.itemSale) {
                tvTypeVP.setText(item.getTitle());
                return true;
            } else if (item.getItemId() == R.id.itemRice) {
                tvTypeVP.setText(item.getTitle());
                return true;
            } else if (item.getItemId() == R.id.itemDrink) {
                tvTypeVP.setText(item.getTitle());
                return true;
            } else if (item.getItemId() == R.id.itemDesserts) {
                tvTypeVP.setText(item.getTitle());
                return true;
            } else if (item.getItemId() == R.id.itemPho) {
                tvTypeVP.setText(item.getTitle());
                return true;
            } else if (item.getItemId() == R.id.itemVegetarian) {
                tvTypeVP.setText(item.getTitle());
                return true;
            } else if (item.getItemId() == R.id.itemSnack) {
                tvTypeVP.setText(item.getTitle());
                return true;
            } else {
                return false;
            }
        });

        popupMenu.show();
    }
    private void insertVatPham(Map<String, String> dataToSend) {
        String phpFilePath = ConnectToServer.getInsert_item_phpFilePath();
        String urlAF = ConnectToServer.getInsert_item_url();

        RetrofitManager retrofitManager = new RetrofitManager(urlAF);
        retrofitManager.sendDataToServer(urlAF, phpFilePath, dataToSend, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("responseFS", response);
                Toast.makeText(ForSale.this, response, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(ForSale.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("errorAF", error);
            }
        });
    }

    private void showImageSelectorDialog(Activity activity, ImageView imageView) {
        // Hiển thị dialog để chọn giữa Camera và Thư viện
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Chọn ảnh")
                .setItems(new CharSequence[]{ "1)Chọn ảnh từ thư viện (nên dùng)", "2)Chụp ảnh\n(xoay ngang điện thoại ngược chiều kim đồng hồ)"}, (dialog, which) -> {
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
}