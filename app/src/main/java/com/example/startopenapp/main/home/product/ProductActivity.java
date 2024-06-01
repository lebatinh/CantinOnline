package com.example.startopenapp.main.home.product;

import static com.example.startopenapp.display_manager.DialogHelper.DialogWarning1;
import static com.example.startopenapp.display_manager.TimeHelper.isWithinValidTime;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.startopenapp.R;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.main.home.cart.CartActivity;
import com.example.startopenapp.main.home.order.BuySingleProduct;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {
    private String acc_id;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private RetrofitManager retrofitManagerPA;
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        SearchView svProduct = findViewById(R.id.svProduct);
        svProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        // Thiết lập GridLayoutManager cho RecyclerView
        RecyclerView rcvProduct = findViewById(R.id.rcvProduct);
        rcvProduct.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ProductActivity.this, 2);
        rcvProduct.setLayoutManager(gridLayoutManager);

        // Khởi tạo adapter và thiết lập cho RecyclerView
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        rcvProduct.setAdapter(productAdapter);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            acc_id = intent.getStringExtra("acc_id");
            String product_type = intent.getStringExtra("product_type");

            // Gửi dữ liệu lên server
            Map<String, String> dataToSend = new HashMap<>();
            dataToSend.put("product_type", product_type);
            TextView tvTypeProduct = findViewById(R.id.tvTypeProduct);
            tvTypeProduct.setText(product_type);
            GetProduct(dataToSend);
        }
        ImageView cart = findViewById(R.id.cart);
        cart.setOnClickListener(view -> {
            Intent intent1 = new Intent(ProductActivity.this, CartActivity.class);
            intent1.putExtra("acc_id", acc_id);
            startActivity(intent1);
        });

        productAdapter.setOnItemClickListener(position -> {
            Product clickedItem = productList.get(position);
            String itemId = clickedItem.getItemId();
            DialogWarning1(ProductActivity.this, "Bạn muốn mua ngay hay thêm vào giỏ hàng?",
                    "Mua hàng", (dialog, which) -> showQuantityDialogBuy(acc_id,itemId),
                    "Thêm vào giỏ hàng", (dialog, which) -> showQuantityDialog(acc_id, itemId));
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
    private void showQuantityDialogBuy(String acc_id, String itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập số lượng");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String quantityStr = input.getText().toString();
            if (!quantityStr.isEmpty() && !quantityStr.equals("0")){
                int quantity = Integer.parseInt(quantityStr);
                buyNow(acc_id, itemId, quantity);
            }else {
                Toast.makeText(ProductActivity.this, "Số lượng không rỗng hoặc bằng 0!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showQuantityDialog(String acc_id, String itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập số lượng");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String quantityStr = input.getText().toString();
            if (!quantityStr.isEmpty() && !quantityStr.equals("0")){
                int quantity = Integer.parseInt(quantityStr);
                addCart(acc_id, itemId, quantity);
            }else {
                Toast.makeText(ProductActivity.this, "Số lượng không rỗng hoặc bằng 0!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addCart(String acc_id, String itemId, int quantity) {
        // Gửi dữ liệu lên server
        Map<String, String> dataPA = new HashMap<>();
        dataPA.put("acc_id", acc_id);
        dataPA.put("item_id", itemId);
        dataPA.put("quantity", String.valueOf(quantity));

        String phpFilePathNoti = ConnectToServer.getInsert_cart_phpFilePath();
        String urlPA = ConnectToServer.getInsert_cart_url();

        // Khởi tạo RetrofitManager với baseUrl
        retrofitManagerPA = new RetrofitManager(urlPA);
        retrofitManagerPA.sendDataToServer(urlPA, phpFilePathNoti, dataPA, new RetrofitManager
                .DataCallback() {

            @Override
            public void onSuccess(String response) {
                Log.d("response", response);
                Toast.makeText(ProductActivity.this, response, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Log.d("errorAddCart", error);
                Toast.makeText(ProductActivity.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buyNow(String acc_id, String itemId, int quantity) {
        if ((!isWithinValidTime())) {
            Toast.makeText(this, "Thời gian hiện tại không thể đặt hàng!", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(ProductActivity.this, BuySingleProduct.class);
            intent.putExtra("acc_id", acc_id);
            intent.putExtra("item_id", itemId);
            intent.putExtra("quantity", quantity);
            startActivity(intent);
        }
    }


    private void performSearch(String query) {
        ArrayList<Product> searchResults = new ArrayList<>();

        for (Product product : productList) {
            if (product.getItemName().toLowerCase().contains(query.toLowerCase()) ||
                    product.getItemDescription().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(product);
            }
        }

        // Cập nhật dữ liệu trong adapter
        productAdapter.updateData(searchResults);
    }
    private void GetProduct(Map<String, String> dataToSend) {
        String phpFilePath = ConnectToServer.getSelect_item_phpFilePath();
        String urlPA = ConnectToServer.getSelect_item_url();

        retrofitManagerPA = new RetrofitManager(urlPA);
        retrofitManagerPA.sendDataToServer(urlPA, phpFilePath, dataToSend, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("responsePA", response);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    // Kiểm tra xem dữ liệu có chứa thông báo lỗi không
                    if (responseObject.has("message")) {
                        String errorMessage = responseObject.getString("message");
                        // thông báo lỗi
                        Toast.makeText(ProductActivity.this, errorMessage, Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        JSONArray jsonArray = responseObject.getJSONArray("data");
                        productList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String hinhanh = jsonObject.getString("hinhanh");
                            String item_id = jsonObject.getString("item_id");
                            String item_name = jsonObject.getString("item_name");
                            String introduce = jsonObject.getString("introduce");
                            String price = jsonObject.getString("price");

                            // Chuyển chuỗi Base64 thành mảng byte
                            byte[] hinhBytes = Base64.decode(hinhanh, Base64.DEFAULT);

                            // Xóa dữ liệu trước đó và thêm người mới
                            productList.add(new Product(hinhBytes, item_id, item_name, introduce, price));
                        }
                        // Cập nhật RecyclerView
                        Log.d("productList", "Size: " + productList.size());
                        // Cập nhật RecyclerView trên Main Thread
                        runOnUiThread(() -> {
                            // Cập nhật RecyclerView
                            productAdapter.setProductList(productList);
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ProductActivity.this, "Lỗi phân tích dữ liệu!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(ProductActivity.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("errorPA", error);
            }
        });
    }
}