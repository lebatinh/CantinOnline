package com.example.startopenapp.main.home;

import static com.example.startopenapp.display_manager.ImageHelper.decodeBase64ToBitmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.startopenapp.R;
import com.example.startopenapp.admin.cashier.CashierActivity;
import com.example.startopenapp.admin.food.FoodActivity;
import com.example.startopenapp.admin.sell.ForSale;
import com.example.startopenapp.main.home.cart.CartActivity;
import com.example.startopenapp.main.home.product.ProductActivity;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private String stdId;
    private LinearLayout lnrAdmin;
    private ImageView imgCart, imgGiamGia, imgRice, imgDrink, imgDesserts, imgPho, imgVegetarian,
            imgSnack, imgMenu, imgSell, imgCashier, imgList;
    public HomeFragment() {}

    public static HomeFragment newInstance(String stdId) {
        HomeFragment fragment = new HomeFragment();
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

        sendDataToServerHF(dataToSend);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initData(view);

        if (Objects.equals(stdId, "admin")){
            lnrAdmin.setVisibility(View.VISIBLE);
        }else {
            lnrAdmin.setVisibility(View.GONE);
        }
        imgCart.setOnClickListener(view19 -> {
            Intent intent1 = new Intent(requireContext(), CartActivity.class);
            intent1.putExtra("acc_id", stdId);
            startActivity(intent1);
        });
        imgGiamGia.setOnClickListener(view12 -> {
            Intent intent = new Intent(requireContext(), ProductActivity.class);
            intent.putExtra("product_type", "Vật phẩm khác");
            intent.putExtra("acc_id", stdId);
            startActivity(intent);
        });
        imgRice.setOnClickListener(view13 -> {
            Intent intent = new Intent(requireContext(), ProductActivity.class);
            intent.putExtra("product_type", "Cơm");
            intent.putExtra("acc_id", stdId);
            startActivity(intent);
        });
        imgDrink.setOnClickListener(view14 -> {
            Intent intent = new Intent(requireContext(), ProductActivity.class);
            intent.putExtra("product_type", "Đồ uống");
            intent.putExtra("acc_id", stdId);
            startActivity(intent);
        });
        imgDesserts.setOnClickListener(view15 -> {
            Intent intent = new Intent(requireContext(), ProductActivity.class);
            intent.putExtra("product_type", "Món tráng miệng");
            intent.putExtra("acc_id", stdId);
            startActivity(intent);
        });
        imgPho.setOnClickListener(view16 -> {
            Intent intent = new Intent(requireContext(), ProductActivity.class);
            intent.putExtra("product_type", "Bún, Phở, Mì");
            intent.putExtra("acc_id", stdId);
            startActivity(intent);
        });
        imgVegetarian.setOnClickListener(view17 -> {
            Intent intent = new Intent(requireContext(), ProductActivity.class);
            intent.putExtra("product_type", "Trái cây");
            intent.putExtra("acc_id", stdId);
            startActivity(intent);
        });
        imgSnack.setOnClickListener(view18 -> {
            Intent intent = new Intent(requireContext(), ProductActivity.class);
            intent.putExtra("product_type", "Đồ ăn vặt");
            intent.putExtra("acc_id", stdId);
            startActivity(intent);
        });
        imgMenu.setOnClickListener(view110 -> {
            Intent intent = new Intent(requireContext(), ProductActivity.class);
            intent.putExtra("product_type", "Thực đơn");
            intent.putExtra("acc_id", stdId);
            startActivity(intent);
        });
        imgSell.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireContext(), ForSale.class);
            intent.putExtra("acc_id", stdId);
            startActivity(intent);
        });
        imgCashier.setOnClickListener(view111 -> {
            if (stdId.equals("admin")){
                Intent intent = new Intent(requireContext(), CashierActivity.class);
                intent.putExtra("acc_id", stdId);
                startActivity(intent);
            }
        });
        imgList.setOnClickListener(view112 -> {
            if (stdId.equals("admin")){
                Intent intent = new Intent(requireContext(), FoodActivity.class);
                intent.putExtra("acc_id", stdId);
                startActivity(intent);
            }
        });
        return view;
    }

    private void initData(View view) {
        imgCart = view.findViewById(R.id.imgCart);
        imgGiamGia = view.findViewById(R.id.imgGiamGia);
        imgRice = view.findViewById(R.id.imgRice);
        imgDrink = view.findViewById(R.id.imgDrink);
        imgDesserts = view.findViewById(R.id.imgDesserts);
        imgPho = view.findViewById(R.id.imgPho);
        imgVegetarian = view.findViewById(R.id.imgVegetarian);
        imgSnack = view.findViewById(R.id.imgSnack);
        imgMenu = view.findViewById(R.id.imgMenu);
        imgSell = view.findViewById(R.id.imgSell);
        lnrAdmin = view.findViewById(R.id.lnrAdmin);
        imgCashier = view.findViewById(R.id.imgCashier);
        imgList = view.findViewById(R.id.imgList);
    }


    //gửi dữ liệu để kiểm tra
    private void sendDataToServerHF(Map<String, String> data) {
        String phpFilePath = ConnectToServer.getSelect_infor_home_phpFilePath();
        String url = ConnectToServer.getSelect_infor_home_url();

        RetrofitManager retrofitManagerHome = new RetrofitManager(url);
        retrofitManagerHome.sendDataToServer(url, phpFilePath, data, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("response", response);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    // Kiểm tra xem dữ liệu có chứa thông báo lỗi không
                    if (responseObject.has("message")) {
                        String errorMessage = responseObject.getString("message");
                        // thông báo lỗi
                        Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        // Dữ liệu hợp lệ
                        JSONArray jsonArray = responseObject.getJSONArray("data");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String avatarByte = jsonObject.getString("avatar");
                        String name = jsonObject.getString("name");

                        // Đặt dữ liệu vào các thành phần trong XML
                        setUserData(avatarByte, name, stdId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(requireActivity(), "Error parsing response!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                Toast.makeText(requireActivity(), "Error send data to server!", Toast.LENGTH_SHORT).show();
                Log.d("errorHF", error);
            }
        });
    }

    private void setUserData(String avatarByte, String name, String stdId) {
        ImageView avatarImg = requireView().findViewById(R.id.avatarImg);
        TextView tvName = requireView().findViewById(R.id.tvName);
        TextView tvId = requireView().findViewById(R.id.tvId);

        if (avatarByte != null) {
            Bitmap bitmap = decodeBase64ToBitmap(avatarByte);

            avatarImg.setImageBitmap(bitmap);
        }else {
            avatarImg.setImageResource(R.drawable.account);
        }
        tvName.setText(name);
        tvId.setText(stdId);
    }
}