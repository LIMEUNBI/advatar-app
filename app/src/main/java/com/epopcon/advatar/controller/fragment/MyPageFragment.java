package com.epopcon.advatar.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.epopcon.advatar.BuildConfig;
import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.util.SharedPreferenceBase;
import com.epopcon.advatar.controller.activity.user.LoginActivity;
import com.epopcon.advatar.controller.activity.online.OnlineListActivity;
import com.epopcon.advatar.controller.activity.user.UserModifyActivity;
import com.epopcon.extra.ExtraClassLoader;

public class MyPageFragment extends BaseFragment {

    private static MyPageFragment instance = null;

    public static MyPageFragment getInstance() {
        if (instance == null) {
            instance = new MyPageFragment();
        }
        return instance;
    }

    private View mView = null;

    private TextView mTxtUserId;
    private TextView mTxtLogout;
    private TextView mTxtModify;

    private Button mBtnOnline;

    private TextView mTxtVersion;

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

        mTxtUserId = (TextView) mView.findViewById(R.id.txt_user_id);
        mTxtLogout = (TextView) mView.findViewById(R.id.txt_logout);
        mTxtModify = (TextView) mView.findViewById(R.id.txt_modify);
        mBtnOnline = (Button) mView.findViewById(R.id.btn_online);
        mTxtVersion = (TextView) mView.findViewById(R.id.txt_version_code);

        mTxtUserId.setText(SharedPreferenceBase.getPrefString(getContext(), Config.USER_ID, null));

        String version = BuildConfig.VERSION_NAME;
        String extraVersion = String.valueOf(ExtraClassLoader.getInstance().getVersionCode());

        mTxtVersion.setText(version + "_" + extraVersion);

        mTxtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferenceBase.putPrefString(getContext(), Config.USER_ID, null);
                SharedPreferenceBase.putPrefString(getContext(), Config.USER_PW, null);

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        mTxtModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserModifyActivity.class);
                startActivity(intent);
            }
        });

        mBtnOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OnlineListActivity.class);
                startActivity(intent);
            }
        });

        return mView;
    }

    public void refresh() {

    }

}
