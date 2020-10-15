package com.epopcon.advatar.controller.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.epopcon.advatar.R;
import com.epopcon.advatar.controller.activity.common.MainActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class BaseFragment extends Fragment {

    protected DisplayImageOptions mImageLoaderOptions;

    protected MainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageLoaderOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_loading)
                .showImageForEmptyUri(R.drawable.ic_default_image)
                .showImageOnFail(R.drawable.ic_default_image)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .build();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
