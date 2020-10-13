package com.epopcon.advatar.controller.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.repo.brand.BrandGoodsRepo;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.common.util.SharedPreferenceBase;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class GoodsFragment extends BaseFragment {

    private static GoodsFragment instance = null;

    public static GoodsFragment getInstance() {
        if (instance == null) {
            instance = new GoodsFragment();
        }
        return instance;
    }

    private View mView = null;

    private ListView mListView;
    private ListAdapter mListAdapter = null;
    private List<BrandGoodsRepo> mGoodsList = new ArrayList<>();

    private ImageView mImgLoading;

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

        mView = inflater.inflate(R.layout.fragment_goods, container, false);

        mListView = mView.findViewById(R.id.list_view);
        mListView.setVisibility(View.GONE);

        mImgLoading = mView.findViewById(R.id.img_loading);

        Glide.with(this).asGif().load(R.raw.loading).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(mImgLoading);

        mListAdapter = new ListAdapter(getActivity().getApplicationContext(), R.layout.item_contents_list, mGoodsList);
        mListView.setAdapter(mListAdapter);

        return mView;
    }

    public void refresh() {

        if (mGoodsList.isEmpty() || mGoodsList == null) {

            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                Calendar cal = new GregorianCalendar(Locale.KOREA);
                cal.add(Calendar.DATE, -2);
                String today = simpleDateFormat.format(cal.getTime());
                String[] brands = SharedPreferenceBase.getPrefString(getContext(), Config.MY_BRAND_LIST, "").split(",");

                List<String> brandCodes = new ArrayList<>();
                for (int i = 0; i < brands.length; i++) {
                    brandCodes.add(brands[i]);
                }

                RestAdvatarProtocol.getInstance().getBrandGoodsList(brandCodes, today, 100, new RequestListener() {
                    @Override
                    public void onRequestSuccess(int requestCode, Object result) {
                        mGoodsList.addAll((List<BrandGoodsRepo>) result);
                        mListAdapter.notifyDataSetChanged();
                        mListView.setVisibility(View.VISIBLE);
                        mImgLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onRequestFailure(Throwable t) {
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ListAdapter extends ArrayAdapter<BrandGoodsRepo> {

        private List<BrandGoodsRepo> items;

        ListAdapter(Context context, int textViewResourceId, List<BrandGoodsRepo> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_goods_list, null);

                holder.goodsImg = (ImageView) convertView.findViewById(R.id.img_goods);
                holder.brandName = (TextView) convertView.findViewById(R.id.brand_name);
                holder.siteName = (TextView) convertView.findViewById(R.id.site_name);
                holder.goodsCate = (TextView) convertView.findViewById(R.id.goods_cate);
                holder.goodsName = (TextView) convertView.findViewById(R.id.goods_name);
                holder.optionName = (TextView) convertView.findViewById(R.id.option_name);
                holder.goodsPrice = (TextView) convertView.findViewById(R.id.goods_price);
                holder.sellAmount = (TextView) convertView.findViewById(R.id.sell_amount);
                holder.avgPoint = (TextView) convertView.findViewById(R.id.avgPoint);
                holder.deliveryInfo = (TextView) convertView.findViewById(R.id.delivery_info);
                holder.shopImg = (ImageView) convertView.findViewById(R.id.img_shop);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final BrandGoodsRepo brandGoodsRepo = items.get(position);

            ImageLoader.getInstance().displayImage(brandGoodsRepo.goodsImg, holder.goodsImg, mImageLoaderOptions);
            NumberFormat n = NumberFormat.getNumberInstance(Locale.KOREAN);

            holder.brandName.setText(brandGoodsRepo.brandName);
            holder.siteName.setText(brandGoodsRepo.siteName);
            holder.goodsCate.setText(brandGoodsRepo.goodsCate1 + " > " + brandGoodsRepo.goodsCate2 + " > " + brandGoodsRepo.goodsCate3);
            holder.goodsName.setText(brandGoodsRepo.goodsName);
            holder.shopImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(brandGoodsRepo.url));
                    startActivity(intent);
                }
            });

            String optionName = "";
            if (brandGoodsRepo.optionName.isEmpty()) {
                optionName = "단일 옵션";
            } else  {
                optionName = brandGoodsRepo.optionName;
            }
            holder.optionName.setText(optionName);
            holder.goodsPrice.setText(n.format(Math.abs(brandGoodsRepo.goodsPrice)) + "원");
            holder.sellAmount.setText("판매량 : " + brandGoodsRepo.sellAmount);
            holder.avgPoint.setText("평점 : " + brandGoodsRepo.avgPoint);
            holder.deliveryInfo.setText("배송정보 : " + brandGoodsRepo.deliveryInfo);

            return convertView;
        }

        private class ViewHolder {
            public ImageView goodsImg;
            public TextView brandName;
            public TextView siteName;
            public TextView goodsCate;
            public TextView goodsName;
            public TextView optionName;
            public TextView goodsPrice;
            public TextView sellAmount;
            public TextView avgPoint;
            public TextView deliveryInfo;
            public ImageView shopImg;
        }
    }
}
