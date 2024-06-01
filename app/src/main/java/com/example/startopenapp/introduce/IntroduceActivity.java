package com.example.startopenapp.introduce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.example.startopenapp.R;
import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;

import java.util.ArrayList;

public class IntroduceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);

        PaperOnboardingFragment paperOnboardingFragment = PaperOnboardingFragment.newInstance(getPaperOnboardingPageData());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.introduce, paperOnboardingFragment);
        fragmentTransaction.commit();

        paperOnboardingFragment.setOnRightOutListener(() -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = new ClauseFragment();
            transaction.replace(R.id.introduce, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    private ArrayList<PaperOnboardingPage> getPaperOnboardingPageData() {
        PaperOnboardingPage scr1 = new PaperOnboardingPage("An toàn",
                "Mọi thứ đều an toàn kể cả tài khoản hay đồ ăn, thức uống của bạn!",
                Color.parseColor("#678FB4"), R.drawable.security, R.drawable.key);
        PaperOnboardingPage scr2 = new PaperOnboardingPage("Dễ dàng",
                "Đặt món và thưởng thức ngay những hương vị tuyệt vời chỉ với vài phút!",
                Color.parseColor("#65B0B4"), R.drawable.fastfood, R.drawable.shopping_cart);
        PaperOnboardingPage scr3 = new PaperOnboardingPage("Tiết kiệm",
                "Linh hoạt thời gian và thưởng thức ngay mà không cần xếp hàng!",
                Color.parseColor("#9B90BC"), R.drawable.save_money, R.drawable.money);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);

        return elements;
    }
}