package com.example.startopenapp.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.startopenapp.main.history.HistoryFragment;
import com.example.startopenapp.main.home.HomeFragment;
import com.example.startopenapp.main.main_screen.notification.NotificationFragment;
import com.example.startopenapp.main.main_screen.account.AccountFragment;

public class ViewPagerAdapterHome extends FragmentStateAdapter {
    private String stdId;
    public ViewPagerAdapterHome(@NonNull FragmentActivity fragmentActivity, String stdId) {
        super(fragmentActivity);
        this.stdId = stdId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return HomeFragment.newInstance(stdId);
            case 1:
                return NotificationFragment.newInstance(stdId);
            case 2:
                return HistoryFragment.newInstance(stdId);
            case 3:
                return AccountFragment.newInstance(stdId);
            default:
                return HomeFragment.newInstance(stdId);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
