package com.example.startopenapp.main.main_screen.notification;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.startopenapp.R;
import com.example.startopenapp.admin.push_noti.SendNotification;
import com.example.startopenapp.main.HomeActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class NotificationFragment extends Fragment {
    private TabLayout tablayoutNoti;
    private ViewPagerAdapterNoti viewPagerAdapterNoti;
    private String stdId;
    private ViewPager2 viewPagerNoti;

    public NotificationFragment() {
    }

    public static NotificationFragment newInstance(String stdId) {
        NotificationFragment fragment = new NotificationFragment();
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
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        tablayoutNoti = view.findViewById(R.id.tablayoutNoti);
        viewPagerNoti = view.findViewById(R.id.viewpagerNoti);

        viewPagerAdapterNoti = new ViewPagerAdapterNoti(this, stdId);
        viewPagerNoti.setAdapter(viewPagerAdapterNoti);

        new TabLayoutMediator(tablayoutNoti, viewPagerNoti, ((tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Quan trọng");
                    break;
                case 1:
                    tab.setText("Ưu đãi");
                    break;
                case 2:
                    tab.setText("Tương tác");
                    break;
            }
        })).attach();

        // hiệu ứng chuyển tab
        viewPagerNoti.setPageTransformer(new HomeActivity.DepthPageTransformer());

        //kiểm tra admin
        ImageView admin_noti_add = view.findViewById(R.id.admin_noti_add);
        if (stdId.equals("admin")) {
            admin_noti_add.setVisibility(View.VISIBLE);
            admin_noti_add.setOnClickListener(view1 -> {
                // Tạo Intent và gửi dữ liệu đi
                Intent intent = new Intent(requireActivity(), SendNotification.class);
                intent.putExtra("id", stdId);
                startActivity(intent);
            });
        } else {
            admin_noti_add.setVisibility(View.GONE);
        }
        return view;
    }
}