package com.example.startopenapp.introduce;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.startopenapp.account.login.LoginActivity;
import com.example.startopenapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClauseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClauseFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private CheckBox checkBox;
    private ImageView img_animation;

    public ClauseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment testFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClauseFragment newInstance(String param1, String param2) {
        ClauseFragment fragment = new ClauseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clause, container, false);

        ImageView imageView = view.findViewById(R.id.img_animation);

        // Load và hiển thị animation từ tệp GIF bằng Glide
        Glide.with(this)
                .asGif()
                .load(R.drawable.continue_animation2)
                .into(imageView);

        checkBox = view.findViewById(R.id.checkBox);
        img_animation = view.findViewById(R.id.img_animation);

        img_animation.setOnClickListener(view1 -> {
            if (!checkBox.isChecked()) {
                Toast.makeText(getActivity(), "Bạn phải đồng ý các điều khoản của ứng dụng", Toast.LENGTH_SHORT).show();
            }else {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        return view;

    }
}