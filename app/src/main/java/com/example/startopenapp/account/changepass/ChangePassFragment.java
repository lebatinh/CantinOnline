package com.example.startopenapp.account.changepass;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.startopenapp.R;
import com.example.startopenapp.databinding.FragmentChangePassBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangePassFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePassFragment extends Fragment {
    private CPViewModel cpViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChangePassFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangePassFragment_1.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangePassFragment newInstance(String param1, String param2) {
        ChangePassFragment fragment = new ChangePassFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_change_pass, container, false);

        // Khởi tạo DataBinding
        FragmentChangePassBinding binding = FragmentChangePassBinding.bind(rootView);

        // Khởi tạo ViewModel
        cpViewModel = new ViewModelProvider(requireActivity()).get(CPViewModel.class);

        // Gán ViewModel cho layout và sử dụng viewLifecycleOwner
        binding.setChangePassCPViewModel(cpViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // Thiết lập onClickListener cho thành phần view
        binding.btnContinueCP.setOnClickListener(view -> cpViewModel.switchToConfirmCP(requireActivity()));
        binding.txtExChangeLogin.setOnClickListener(view -> cpViewModel.switchToLogin(requireActivity()));

        // Trả về root view của fragment
        return rootView;
    }
}