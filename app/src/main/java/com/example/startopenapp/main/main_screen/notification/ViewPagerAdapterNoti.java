package com.example.startopenapp.main.main_screen.notification;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.startopenapp.main.main_screen.notification.fragment.EndowFragment;
import com.example.startopenapp.main.main_screen.notification.fragment.ImportantFragment;
import com.example.startopenapp.main.main_screen.notification.fragment.InteractFragment;

public class ViewPagerAdapterNoti extends FragmentStateAdapter {
    private final String stdId;
    public ViewPagerAdapterNoti(@NonNull NotificationFragment fragmentActivity, String stdId) {
        super(fragmentActivity);
        this.stdId = stdId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return ImportantFragment.newInstance(stdId);
            case 1:
                return EndowFragment.newInstance(stdId);
            case 2:
                return InteractFragment.newInstance(stdId);
            default:
                return ImportantFragment.newInstance(stdId);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
