package com.example.startopenapp.account.forgotpass;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.startopenapp.R;
import com.example.startopenapp.databinding.FragmentForgotPassBinding;
import com.example.startopenapp.databinding.FragmentForgotPassConfirmBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForgotPassFragmentConfirm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgotPassFragmentConfirm extends Fragment {
    private FPViewModel fpViewModel;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ForgotPassFragmentConfirm() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForgotPassFragmentConfirm.
     */
    // TODO: Rename and change types and number of parameters
    public static ForgotPassFragmentConfirm newInstance(String param1, String param2) {
        ForgotPassFragmentConfirm fragment = new ForgotPassFragmentConfirm();
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
        View rootView = inflater.inflate(R.layout.fragment_forgot_pass_confirm, container, false);

        // Khởi tạo DataBinding
        FragmentForgotPassConfirmBinding binding = FragmentForgotPassConfirmBinding.bind(rootView);

        // Khởi tạo ViewModel
        fpViewModel = new ViewModelProvider(requireActivity()).get(FPViewModel.class);

        // Gán ViewModel cho layout và sử dụng viewLifecycleOwner
        binding.setFPConfirm(fpViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // Thiết lập onClickListener cho thành phần view
        binding.btnConfirmFP.setOnClickListener(view -> fpViewModel.onClickForgotPass(requireActivity()));
        binding.backToForgotPass.setOnClickListener(view -> getActivity().getSupportFragmentManager().popBackStack());

        return rootView;
    }
}