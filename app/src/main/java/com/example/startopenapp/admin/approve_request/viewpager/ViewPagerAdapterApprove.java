package com.example.startopenapp.admin.approve_request.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapterApprove extends FragmentStateAdapter {
    private final String stdId;
    public ViewPagerAdapterApprove(@NonNull FragmentActivity fragmentActivity, String stdId) {
        super(fragmentActivity);
        this.stdId = stdId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return RechargeFragment.newInstance(stdId);
            case 1:
                return WithdrawFragment.newInstance(stdId);
            default:
                return RechargeFragment.newInstance(stdId);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
