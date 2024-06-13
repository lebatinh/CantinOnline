package com.example.startopenapp.main.home.order.historyorder.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapterHistoryOrders extends FragmentStateAdapter {
    private final String stdId;

    public ViewPagerAdapterHistoryOrders(@NonNull FragmentActivity fragmentActivity, String stdId) {
        super(fragmentActivity);
        this.stdId = stdId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return Bought.newInstance(stdId);
            case 1:
                return Buying.newInstance(stdId);
            case 2:
                return Cancel.newInstance(stdId);
            default:
                return Bought.newInstance(stdId);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
