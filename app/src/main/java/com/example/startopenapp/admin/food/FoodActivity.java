package com.example.startopenapp.admin.food;

import static com.example.startopenapp.display_manager.DialogHelper.DialogWarning1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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

public class FoodActivity extends AppCompatActivity {
    private SearchView svFood;
    private RecyclerView rcvFood;
    private List<Food> foodList;
    private FoodAdapter foodAdapter;
    private RetrofitManager retrofitManagerFA;
    private NetworkChangeReceiver networkChangeReceiver;
    private String acc_id;
    private TextView tvTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        Intent intent = getIntent();
        acc_id = intent.getStringExtra("acc_id");

        rcvFood = findViewById(R.id.rcvFood);
        rcvFood.setHasFixedSize(true);
        rcvFood.setLayoutManager(new GridLayoutManager(FoodActivity.this, 2));

        foodList = new ArrayList<>();
        foodAdapter = new FoodAdapter(foodList);
        rcvFood.setAdapter(foodAdapter);

        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("product_type", "Thực đơn");
        getListItem(dataToSend);

        tvTitle = findViewById(R.id.tvTitle);
        svFood = findViewById(R.id.svFood);
        svFood.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        foodAdapter.setOnItemClickListener(position -> {
            Food clickedItem = foodList.get(position);
            String itemId = clickedItem.getItemId();
            DialogWarning1(FoodActivity.this, "Bạn muốn mua ngay hay thêm vào giỏ hàng?",
                    "Sửa thông tin", (dialog, which) -> changeInfoItem(acc_id,itemId),
                    "Xóa vật phẩm", (dialog, which) -> deleteItem(itemId));
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

    private void deleteItem(String itemId) {
        Map<String, String> data = new HashMap<>();
        data.put("item_id", itemId);

        String phpFilePath = ConnectToServer.getDelete_item_phpFilePath();
        String urlPA = ConnectToServer.getDelete_item_url();

        retrofitManagerFA = new RetrofitManager(urlPA);
        retrofitManagerFA.sendDataToServer(urlPA, phpFilePath, data, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("responsePA", response);
                Toast.makeText(FoodActivity.this, response, Toast.LENGTH_SHORT).show();

                if (response.equals("Xóa vật phẩm thành công!")) {
                    // Tìm và xóa vật phẩm khỏi foodList
                    for (int i = 0; i < foodList.size(); i++) {
                        if (foodList.get(i).getItemId().equals(itemId)) {
                            foodList.remove(i);
                            break;
                        }
                    }

                    // Thông báo cho adapter rằng dữ liệu đã thay đổi
                    foodAdapter.notifyDataSetChanged();
                    tvTitle.setText("Danh sách vật phẩm " + "("+foodList.size()+")");
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(FoodActivity.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("errorPA", error);
            }
        });
    }

    private void changeInfoItem(String accId, String item_id) {
        Intent intent = new Intent(FoodActivity.this, FoodInfo.class);
        intent.putExtra("acc_id", accId);
        intent.putExtra("item_id", item_id);
        startActivity(intent);
    }

    private void performSearch(String query) {
        ArrayList<Food> searchResults = new ArrayList<>();
        for (Food food : foodList){
            if (food.getItemId().toLowerCase().contains(query.toLowerCase()) ||
                    food.getItemName().toLowerCase().contains(query.toLowerCase()) ||
                            food.getItemDescription().toLowerCase().contains(query.toLowerCase())){
                searchResults.add(food);
            }
        }

        foodAdapter.updateData(searchResults);
    }

    private void getListItem(Map<String, String> dataToSend) {
        String phpFilePath = ConnectToServer.getSelect_item_phpFilePath();
        String urlPA = ConnectToServer.getSelect_item_url();

        retrofitManagerFA = new RetrofitManager(urlPA);
        retrofitManagerFA.sendDataToServer(urlPA, phpFilePath, dataToSend, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("responsePA", response);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    // Kiểm tra xem dữ liệu có chứa thông báo lỗi không
                    if (responseObject.has("message")) {
                        String errorMessage = responseObject.getString("message");
                        // thông báo lỗi
                        Toast.makeText(FoodActivity.this, errorMessage, Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        JSONArray jsonArray = responseObject.getJSONArray("data");
                        foodList.clear();

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
                            foodList.add(new Food(hinhBytes, item_id, item_name, introduce, price));
                        }
                        // Cập nhật RecyclerView
                        Log.d("productList", "Size: " + foodList.size());
                        tvTitle.setText("Danh sách vật phẩm " + "("+foodList.size()+")");
                        // Cập nhật RecyclerView trên Main Thread
                        runOnUiThread(() -> {
                            // Cập nhật RecyclerView
                            foodAdapter.setFoodList(foodList);
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(FoodActivity.this, "Lỗi phân tích dữ liệu!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(FoodActivity.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("errorPA", error);
            }
        });
    }
}