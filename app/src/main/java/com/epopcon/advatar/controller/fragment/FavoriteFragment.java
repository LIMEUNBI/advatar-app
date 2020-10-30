package com.epopcon.advatar.controller.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.param.online.OnlinePickProductParam;
import com.epopcon.advatar.common.network.model.repo.online.OnlinePickProductRepo;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.common.util.DialogClickListener;
import com.epopcon.advatar.common.util.DialogUtil;
import com.epopcon.advatar.common.util.SharedPreferenceBase;
import com.epopcon.extra.common.utils.ExecutorPool;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.epopcon.advatar.common.util.Utils.getApplicationContext;

public class FavoriteFragment extends BaseFragment {

    private View mView = null;
    private static FavoriteFragment instance = null;

    public static FavoriteFragment getInstance() {
        if (instance == null) {
            instance = new FavoriteFragment();
        }
        return instance;
    }

    private SwipeMenuListView mListView;
    private ListAdapter mAdapter = null;

    private FloatingActionButton mFab;

    private List<OnlinePickProductRepo> mLinkInfoList = new ArrayList<>();

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

        mListView = (SwipeMenuListView) mView.findViewById(R.id.list_view);
        mAdapter = new ListAdapter(getContext(), R.layout.item_online_shared_url, mLinkInfoList);
        mListView.setMenuCreator(creator);

        mListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
                // swipe start
                mListView.smoothOpenMenu(position);
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
                mListView.smoothOpenMenu(position);
            }
        });

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        OnlinePickProductParam onlinePickProductParam = new OnlinePickProductParam();
                        onlinePickProductParam.userId = SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_ID, null);
                        onlinePickProductParam.siteName = mLinkInfoList.get(position).siteName;
                        onlinePickProductParam.productName = mLinkInfoList.get(position).productName;
                        onlinePickProductParam.productPrice = mLinkInfoList.get(position).productPrice;
                        onlinePickProductParam.deliveryAmount = mLinkInfoList.get(position).deliveryAmount;
                        onlinePickProductParam.collectionType = mLinkInfoList.get(position).collectionType;
                        onlinePickProductParam.productImg = mLinkInfoList.get(position).productImg;
                        onlinePickProductParam.productUrl = mLinkInfoList.get(position).productUrl;
                        onlinePickProductParam.dateTime = mLinkInfoList.get(position).dateTime;
                        DialogUtil.showSharedUrlDialog(getActivity(), mLinkInfoList.get(position).productUrl, onlinePickProductParam, new DialogClickListener() {
                            @Override
                            public void onPositiveClick() {
                                refresh();
                            }

                            @Override
                            public void onNegativeClick() {

                            }
                        });
                        break;
                }
                return false;
            }
        });

        mFab = (FloatingActionButton) mView.findViewById(R.id.fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogUtil.showSharedUrlDialog(getActivity(), null, null, new DialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                        refresh();
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
            }
        });

        mListView.setAdapter(mAdapter);

        return mView;
    }

    public void refresh() {

        mLinkInfoList.clear();

        String userId = SharedPreferenceBase.getPrefString(getContext(), Config.USER_ID, null);
        try {
            RestAdvatarProtocol.getInstance().getOnlinePickProductList(userId, new RequestListener() {
                @Override
                public void onRequestSuccess(int requestCode, Object result) {
                    mLinkInfoList.addAll((List<OnlinePickProductRepo>) result);
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

    private String getStoreName(String siteName) {
        String storeName = "";

        switch (siteName) {
            case "top.11st.co.kr":
                storeName = "11번가";
                break;
            case "top.gmarket.co.kr":
                storeName = "G마켓";
                break;
            case "top.auction.co.kr":
                storeName = "옥션";
                break;
            case "top.naverstore.com":
                storeName = "네이버 스토어";
                break;
            case "top.interpark.com":
                storeName = "인터파크";
                break;
            case "top.coupang.com":
                storeName = "쿠팡";
                break;
            case "top.tmon.co.kr":
                storeName = "티몬";
                break;
            case "top.wemakeprice.com":
                storeName = "위메프";
                break;
            case "top.ssg.com":
                storeName = "SSG";
                break;
            case "top.lotteon.com":
                storeName = "롯데ON";
                break;
            case "top.hyundaihamall.com":
                storeName = "현대H몰";
                break;
            case "top.cjmall.com":
                storeName = "CJ몰";
                break;
            case "top.akmall.com":
                storeName = "AK몰";
                break;
        }

        return storeName;
    }

    private class ListAdapter extends ArrayAdapter<OnlinePickProductRepo> {

        private List<OnlinePickProductRepo> items;

        ListAdapter(Context context, int textViewResourceId, List<OnlinePickProductRepo> items) {
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
                holder.collectionType = (TextView) convertView.findViewById(R.id.collection_type);
                holder.productPrice = (TextView) convertView.findViewById(R.id.product_price);
                holder.deliveryAmount = (TextView) convertView.findViewById(R.id.delivery_amount);
                holder.line = (View) convertView.findViewById(R.id.view);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final OnlinePickProductRepo sharedLinkInfo = items.get(position);

            ImageLoader.getInstance().displayImage(sharedLinkInfo.productImg, holder.productImg, mImageLoaderOptions);

            holder.productName.setText(sharedLinkInfo.productName);
            String siteName = getStoreName(sharedLinkInfo.siteName);
            if (TextUtils.isEmpty(siteName)) {
                holder.siteName.setVisibility(View.GONE);
            }
            holder.siteName.setText(siteName);
            holder.productPrice.setText(String.format(getString(R.string.amount_integer), sharedLinkInfo.productPrice) + "원");
            holder.deliveryAmount.setText("배송비 " + String.format(getString(R.string.amount_integer), sharedLinkInfo.deliveryAmount) + "원");

            String type = "";
            if (sharedLinkInfo.collectionType != null) {
                if (sharedLinkInfo.collectionType.equals("A")) {
                    type = "수집주기 1시간";
                } else if (sharedLinkInfo.collectionType.equals("B")){
                    type = "수집주기 3시간";
                }
            }

            holder.collectionType.setText(type);

            if (position == items.size() -1) {
                holder.line.setVisibility(View.GONE);
            } else {
                holder.line.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        private class ViewHolder {
            public ImageView productImg;
            public TextView productName;
            public TextView siteName;
            public TextView collectionType;
            public TextView productPrice;
            public TextView deliveryAmount;
            public View line;
        }
    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
            openItem.setBackground(new ColorDrawable(Color.rgb(245, 245, 245)));
            openItem.setWidth(200);
            openItem.setTitle("설정");
            openItem.setTitleSize(12);
            openItem.setTitleColor(Color.BLACK);
            menu.addMenuItem(openItem);
        }
    };


}
