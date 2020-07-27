package com.epopcon.advatar.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epopcon.advatar.R;

public class MyPageFragment extends BaseFragment {

    private static MyPageFragment instance = null;

    public static MyPageFragment getInstance() {
        if (instance == null) {
            instance = new MyPageFragment();
        }
        return instance;
    }

    private View mView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_mypage, container, false);


        return mView;
    }

    public void refresh() {

    }
}
