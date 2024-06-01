package com.example.startopenapp.main.home.cart;

import static com.example.startopenapp.display_manager.TimeHelper.isWithinValidTime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.example.startopenapp.R;
import com.example.startopenapp.display_manager.NetworkChangeReceiver;
import com.example.startopenapp.main.home.order.BuyProduct;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartInteractionListener, CartAdapter.TotalPriceUpdateListener  {
    String acc_id;
    private CartAdapter cartAdapter;
    private List<Cart> cartList;
    private List<String> cartSelect;
    private RetrofitManager retrofitManagerCart;
    private TextView tvTotalMoneyCart, tvSizeCart;
    private Map<String, Integer> initialQuantities = new HashMap<>();
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            acc_id = intent.getStringExtra("acc_id");

            // Gửi dữ liệu lên server
            Map<String, String> dataToSend = new HashMap<>();
            dataToSend.put("acc_id", acc_id);
            GetCart(dataToSend);
        }

        RecyclerView rcvCart = findViewById(R.id.rcvCart);
        rcvCart.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CartActivity.this);
        rcvCart.setLayoutManager(linearLayoutManager);

        cartList = new ArrayList<>();
        cartSelect = new ArrayList<>();
        cartAdapter = new CartAdapter(cartList, this, cartSelect, this);
        rcvCart.setAdapter(cartAdapter);

        TextView tvDeleteAllCart = findViewById(R.id.tvDeleteAllCart);
        tvDeleteAllCart.setOnClickListener(view -> {
            // Gửi dữ liệu lên server
            Map<String, String> dataToSend = new HashMap<>();
            dataToSend.put("acc_id", acc_id);
            dataToSend.put("item_id", "all");

            deleteCart(dataToSend);
        });

        TextView tvOrder = findViewById(R.id.tvOrder);
        tvOrder.setOnClickListener(view -> sendSelectedCartItems());

        CheckBox checkAll = findViewById(R.id.checkAll);
        checkAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Chọn tất cả các item trong RecyclerView
                for (Cart cart : cartList) {
                    if (!cartSelect.contains(cart.getItemId())) {
                        cartSelect.add(cart.getItemId());
                    }
                }
                if (!cartSelect.isEmpty()) {
                    // Nếu có ít nhất một mặt hàng được chọn, cập nhật tổng số tiền
                    updateTotalPrice();
                }
                cartAdapter.notifyDataSetChanged();
            } else {
                // Bỏ chọn tất cả các item trong RecyclerView
                cartSelect.clear();
                tvTotalMoneyCart.setText("");
                cartAdapter.notifyDataSetChanged();
            }
        });
        tvTotalMoneyCart = findViewById(R.id.tvTotalMoneyCart);
        tvSizeCart = findViewById(R.id.tvSizeCart);
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
    private void initInitialQuantities() {
        for (Cart cart : cartList) {
            initialQuantities.put(cart.getItemId(), Integer.parseInt(cart.getItemAmount()));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkAndUpdateQuantities();
    }

    private void checkAndUpdateQuantities() {
        if (initialQuantities != null) {
            Log.d("initialQuantities", initialQuantities.toString());
            for (Cart cart : cartList) {
                String itemId = cart.getItemId();
                Integer initialQuantity = initialQuantities.get(itemId);
                if (initialQuantity != null) {
                    int currentQuantity = Integer.parseInt(cart.getItemAmount());

                    Log.d("currentQuantity", String.valueOf(currentQuantity));
                    if (currentQuantity != initialQuantity.intValue()) {
                        // Số lượng đã thay đổi
                        if (currentQuantity == 0) {
                            // Nếu số lượng mới bằng 0, xóa mặt hàng
                            // Có hàm xử lý rồi
                        } else {
                            // Nếu số lượng mới khác 0, cập nhật mặt hàng
                            updateCartItem(itemId, currentQuantity);
                        }
                    }
                }
            }
        }else {
            Log.d("initialQuantities", "null");
        }
    }

    private void updateTotalPrice() {
        double totalPrice = cartAdapter.getTotalPrice();
        tvTotalMoneyCart.setText(String.valueOf(totalPrice));
    }

    private void updateCartItem(String itemId, int currentQuantity) {
        Map<String, String> data = new HashMap<>();
        data.put("acc_id", acc_id);
        data.put("item_id", itemId);
        data.put("quantity", String.valueOf(currentQuantity));

        updateCart(data);
    }

    private void updateCart(Map<String, String> data) {
        String phpFilePath = ConnectToServer.getUpdate_cart_phpFilePath();
        String urlCart = ConnectToServer.getUpdate_cart_url();

        retrofitManagerCart = new RetrofitManager(urlCart);
        retrofitManagerCart.sendDataToServer(urlCart, phpFilePath, data, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("responseCA", response);
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(CartActivity.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("errorCA", error);
            }
        });
    }

    @Override
    public void onItemAmountZero(Cart cart) {
        String itemId = cart.getItemId();

        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("acc_id", acc_id);
        dataToSend.put("item_id", itemId);

        Log.d("itemId", itemId);
        deleteCart(dataToSend);
        updateTotalPrice();
        tvSizeCart.setText(String.valueOf(cartList.size()));
        cartAdapter.notifyDataSetChanged();
    }
    private void sendSelectedCartItems() {
        if (cartSelect.isEmpty()) {
            Toast.makeText(this, "Không có item được chọn!", Toast.LENGTH_SHORT).show();
        } else if ((!isWithinValidTime())) {
            Toast.makeText(this, "Thời gian hiện tại không thể đặt hàng!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(CartActivity.this, BuyProduct.class);
            intent.putStringArrayListExtra("selected_item_ids", new ArrayList<>(cartSelect));
            intent.putExtra("acc_id", acc_id);
            startActivity(intent);
        }
    }
    private void deleteCart(Map<String, String> dataToSend) {
        String phpFilePath = ConnectToServer.getDelete_cart_phpFilePath();
        String urlCart = ConnectToServer.getDelete_cart_url();

        retrofitManagerCart = new RetrofitManager(urlCart);
        retrofitManagerCart.sendDataToServer(urlCart, phpFilePath, dataToSend, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("responseCA", response);
                Toast.makeText(CartActivity.this, response, Toast.LENGTH_SHORT).show();

                if (dataToSend.get("item_id").equals("all")) {
                    cartList.clear();
                } else {
                    String itemIdToRemove = dataToSend.get("item_id");
                    for (int i = 0; i < cartList.size(); i++) {
                        if (cartList.get(i).getItemId().equals(itemIdToRemove)) {
                            cartList.remove(i);
                            break;
                        }
                    }
                }

                // Cập nhật lại RecyclerView và các TextView liên quan trên UI thread
                runOnUiThread(() -> {
                    cartAdapter.notifyDataSetChanged();
                    updateTotalPrice();
                    tvSizeCart.setText(String.valueOf(cartList.size()));
                });
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(CartActivity.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("errorCA", error);
            }
        });
    }

    private void GetCart(Map<String, String> dataToSend) {
        String phpFilePath = ConnectToServer.getSelect_cart_phpFilePath();
        String urlCart = ConnectToServer.getSelect_cart_url();

        retrofitManagerCart = new RetrofitManager(urlCart);
        retrofitManagerCart.sendDataToServer(urlCart, phpFilePath, dataToSend, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("responseCA", response);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    // Kiểm tra xem dữ liệu có chứa thông báo lỗi không
                    if (responseObject.has("message")) {
                        String errorMessage = responseObject.getString("message");
                        // thông báo lỗi
                        Toast.makeText(CartActivity.this, errorMessage, Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        JSONArray jsonArray = responseObject.getJSONArray("data");
                        cartList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String hinhanh = jsonObject.getString("hinhanh");
                            String item_id = jsonObject.getString("item_id");
                            String item_name = jsonObject.getString("item_name");
                            String price = jsonObject.getString("price");
                            String quantity = jsonObject.getString("quantity");

                            // Chuyển chuỗi Base64 thành mảng byte
                            byte[] hinhBytes = Base64.decode(hinhanh, Base64.DEFAULT);

                            // Xóa dữ liệu trước đó và thêm người mới
                            cartList.add(new Cart(hinhBytes, item_name, item_id, price, quantity));
                        }
                        tvSizeCart.setText(String.valueOf(cartList.size()));
                        runOnUiThread(() -> {
                            // Cập nhật RecyclerView
                            cartAdapter.setCartList(cartList);
                            // Khởi tạo số lượng ban đầu của từng mặt hàng
                            initInitialQuantities();
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CartActivity.this, "Lỗi phân tích dữ liệu!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(CartActivity.this, "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("errorCA", error);
            }
        });
    }

    @Override
    public void onTotalPriceUpdated(double totalPrice) {
        tvTotalMoneyCart.setText(String.valueOf(totalPrice));
    }
}