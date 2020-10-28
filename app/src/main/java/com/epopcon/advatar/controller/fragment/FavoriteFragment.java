package com.epopcon.advatar.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.repo.online.OnlineSharedUrlRepo;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.common.util.SharedPreferenceBase;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends BaseFragment {

    private View mView = null;
    private static FavoriteFragment instance = null;

    public static FavoriteFragment getInstance() {
        if (instance == null) {
            instance = new FavoriteFragment();
        }
        return instance;
    }

    private ListView mListView;
    private ListAdapter mAdapter = null;

    private List<OnlineSharedUrlRepo> mLinkInfoList = new ArrayList<>();

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

        mView = inflater.inflate(R.layout.fragment_contents, container, false);

        mListView = (ListView) mView.findViewById(R.id.list_view);
        mAdapter = new ListAdapter(getContext(), R.layout.item_online_shared_url, mLinkInfoList);

        mListView.setAdapter(mAdapter);

        return mView;
    }

    public void refresh() {

        mLinkInfoList.clear();

        String userId = SharedPreferenceBase.getPrefString(getContext(), Config.USER_ID, null);
        try {
            RestAdvatarProtocol.getInstance().getOnlineSharedUrlList(userId, new RequestListener() {
                @Override
                public void onRequestSuccess(int requestCode, Object result) {
                    mLinkInfoList.addAll((List<OnlineSharedUrlRepo>) result);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onRequestFailure(Throwable t) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ListAdapter extends ArrayAdapter<OnlineSharedUrlRepo> {

        private List<OnlineSharedUrlRepo> items;

        ListAdapter(Context context, int textViewResourceId, List<OnlineSharedUrlRepo> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_online_shared_url, null);

                holder.productImg = (ImageView) convertView.findViewById(R.id.product_img);
                holder.productName = (TextView) convertView.findViewById(R.id.product_name);
                holder.siteName = (TextView) convertView.findViewById(R.id.site_name);
                holder.productPrice = (TextView) convertView.findViewById(R.id.product_price);
                holder.deliveryAmount = (TextView) convertView.findViewById(R.id.delivery_amount);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final OnlineSharedUrlRepo sharedLinkInfo = items.get(position);

            ImageLoader.getInstance().displayImage(sharedLinkInfo.productImg, holder.productImg, mImageLoaderOptions);

            holder.productName.setText(sharedLinkInfo.productName);
            holder.siteName.setText(sharedLinkInfo.siteName);
            holder.productPrice.setText(String.format(getString(R.string.amount_integer), sharedLinkInfo.productPrice) + "원");
            holder.deliveryAmount.setText("배송비 " + String.format(getString(R.string.amount_integer), sharedLinkInfo.deliveryAmount) + "원");

            return convertView;
        }

        private class ViewHolder {
            public ImageView productImg;
            public TextView productName;
            public TextView siteName;
            public TextView productPrice;
            public TextView deliveryAmount;
        }
    }
}
