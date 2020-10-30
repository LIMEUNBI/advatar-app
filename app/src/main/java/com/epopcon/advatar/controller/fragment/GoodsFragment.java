package com.epopcon.advatar.controller.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.param.online.OnlinePickProductParam;
import com.epopcon.advatar.common.network.model.repo.brand.BrandGoodsRepo;
import com.epopcon.advatar.common.network.model.repo.brand.BrandRepo;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.common.util.SharedPreferenceBase;
import com.epopcon.advatar.controller.activity.brand.BrandChoiceActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    private GridView mGridView;
    private GridAdapter mGridAdapter = null;
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

        mGridView = mView.findViewById(R.id.grid_view);
        mGridView.setVisibility(View.GONE);

        mImgLoading = mView.findViewById(R.id.img_loading);

        Glide.with(this).asGif().load(R.raw.loading).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(mImgLoading);

        mGridAdapter = new GridAdapter(getActivity().getApplicationContext(), R.layout.item_goods_list, mGoodsList);
        mGridView.setAdapter(mGridAdapter);

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
                        mGridAdapter.notifyDataSetChanged();
                        mGridView.setVisibility(View.VISIBLE);
                        mImgLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onRequestFailure(Throwable t) {
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mGridAdapter.notifyDataSetChanged();
            mGridView.setVisibility(View.VISIBLE);
            mImgLoading.setVisibility(View.GONE);
        }
    }

    private class GridAdapter extends ArrayAdapter<BrandGoodsRepo> {

        private List<BrandGoodsRepo> items;

        public GridAdapter(Context context, int textViewResourceId, List<BrandGoodsRepo> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_goods_list, null);

                holder.goodsImg = (ImageView) convertView.findViewById(R.id.img_goods);
                holder.pickImg = (ImageView) convertView.findViewById(R.id.img_pick);
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

            if (brandGoodsRepo.pickYn) {
                holder.pickImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_heart_full));
            } else {
                holder.pickImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_heart_empty));
            }

            holder.pickImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (brandGoodsRepo.pickYn) {
                        holder.pickImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_heart_empty));
                        brandGoodsRepo.setPickYn(false);
                        String userId = SharedPreferenceBase.getPrefString(getContext(), Config.USER_ID, null);
                        try {
                            RestAdvatarProtocol.getInstance().onlinePickCancel(userId, brandGoodsRepo.siteName, brandGoodsRepo.url, new RequestListener() {
                                @Override
                                public void onRequestSuccess(int requestCode, Object result) {

                                }

                                @Override
                                public void onRequestFailure(Throwable t) {

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        holder.pickImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_heart_full));
                        brandGoodsRepo.setPickYn(true);
                        OnlinePickProductParam onlinePickProductParam = new OnlinePickProductParam();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateTime = dateFormat.format(new Date());

                        onlinePickProductParam.userId = SharedPreferenceBase.getPrefString(getContext(), Config.USER_ID, null);
                        onlinePickProductParam.collectionType = "A";
                        onlinePickProductParam.siteName = brandGoodsRepo.siteName;
                        onlinePickProductParam.productName = brandGoodsRepo.goodsName;
                        onlinePickProductParam.productPrice = brandGoodsRepo.goodsPrice;
                        onlinePickProductParam.deliveryAmount = 0;
                        onlinePickProductParam.productImg = brandGoodsRepo.goodsImg;
                        onlinePickProductParam.productUrl = brandGoodsRepo.url;
                        onlinePickProductParam.dateTime = dateTime;
                        try {
                            RestAdvatarProtocol.getInstance().onlinePickProduct(onlinePickProductParam, new RequestListener() {
                                @Override
                                public void onRequestSuccess(int requestCode, Object result) {

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
            });

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
            public ImageView pickImg;
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
