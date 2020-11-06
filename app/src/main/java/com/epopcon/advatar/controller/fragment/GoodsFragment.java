package com.epopcon.advatar.controller.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.param.online.OnlinePickProductParam;
import com.epopcon.advatar.common.network.model.repo.brand.BrandGoodsRepo;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.common.util.SharedPreferenceBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import pl.pzienowicz.autoscrollviewpager.AutoScrollViewPager;

public class GoodsFragment extends BaseFragment {

    private static GoodsFragment instance = null;

    public static GoodsFragment getInstance() {
        if (instance == null) {
            instance = new GoodsFragment();
        }
        return instance;
    }

    private View mView = null;

    private GridViewWithHeaderAndFooter mGridView;
    private GridAdapter mGridAdapter = null;
    private List<BrandGoodsRepo> mGoodsList = new ArrayList<>();

    private ImageView mImgLoading;

    private RelativeLayout mSortLayout;
    private RelativeLayout mFilterLayout;

    private List<BrandGoodsRepo> mRecommendGoodsList = new ArrayList<>();

    private AutoScrollViewPager mRecommendPager;
    private AutoScrollAdapter mRecommendAdapter;

    private LinearLayout mListHeaderView;

    private TextView mTxtSort;

    private ListView mDrawerListView;
    private DrawerLayout drawer;

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

        mGridView = (GridViewWithHeaderAndFooter) mView.findViewById(R.id.grid_view);
        mGridView.setVisibility(View.GONE);

        mImgLoading = mView.findViewById(R.id.img_loading);

        Glide.with(this).asGif().load(R.raw.loading).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(mImgLoading);

        mListHeaderView = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.header_recommend_item, null, false);
        mGridView.addHeaderView(mListHeaderView);

        mGridAdapter = new GridAdapter(getActivity().getApplicationContext(), R.layout.item_goods_list, mGoodsList, mRecommendGoodsList);
        mGridView.setAdapter(mGridAdapter);

        mRecommendPager = (AutoScrollViewPager) mListHeaderView.findViewById(R.id.pager_banner);
        mRecommendAdapter = new AutoScrollAdapter(mRecommendGoodsList);
        mRecommendPager.setAdapter(mRecommendAdapter);

        mSortLayout = (RelativeLayout) mView.findViewById(R.id.sort_layout);
        mFilterLayout = (RelativeLayout) mView.findViewById(R.id.filter_layout);

        mTxtSort = (TextView) mView.findViewById(R.id.txt_sort);

//        drawer = (DrawerLayout) mView.findViewById(R.id.drawer);
//        mDrawerListView = (ListView) mView.findViewById(R.id.drawer_list);

//        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                switch (i) {
//
//                }
//
//                if (drawer.isDrawerOpen(Gravity.RIGHT)) {
//                    drawer.closeDrawer(Gravity.RIGHT);
//                }
//            }
//        });

        mSortLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String orderBy;
                if (mTxtSort.getText().toString().equals("판매량순")) {
                    mTxtSort.setText("매출액순");
                    orderBy = "TOTAL_SELL_PRICE";
                } else {
                    mTxtSort.setText("판매량순");
                    orderBy = "SELL_AMOUNT";
                }
                SharedPreferenceBase.putPrefString(getContext(), Config.GOODS_ORDER_BY, orderBy);
                getBrandGoodsList(orderBy);
            }
        });

        mFilterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawer.isDrawerOpen(Gravity.RIGHT)) {
                    drawer.openDrawer(Gravity.RIGHT);
                }
            }
        });

        return mView;
    }

    public void refresh() {

        if (mGoodsList.isEmpty() || mGoodsList == null) {
            if (getGoodsList() != null && !getGoodsList().isEmpty()) {
                mGoodsList.addAll(getGoodsList());
                mImgLoading.setVisibility(View.GONE);
                mGridAdapter.notifyDataSetChanged();
                mGridView.setVisibility(View.VISIBLE);
            }
            String orderBy = SharedPreferenceBase.getPrefString(getContext(), Config.GOODS_ORDER_BY, "SELL_AMOUNT");
            getBrandGoodsList(orderBy);

        } else {
            mGridAdapter.notifyDataSetChanged();
            mGridView.setVisibility(View.VISIBLE);
            mImgLoading.setVisibility(View.GONE);
        }

        getRecommendGoodsList();
    }

    private void getRecommendGoodsList() {
//        String userId = SharedPreferenceBase.getPrefString(getContext(), Config.USER_ID, null);
//        try {
//            RestAdvatarProtocol.getInstance().getRecommendGoodsList(userId, new RequestListener() {
//                @Override
//                public void onRequestSuccess(int requestCode, Object result) {
//                    mRecommendGoodsList.clear();
//                    mRecommendGoodsList.addAll((List<BrandGoodsRepo>) result);
//                    mHorizontalAdapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onRequestFailure(Throwable t) {
//
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        mRecommendGoodsList.clear();

        // 임시 데이터
        BrandGoodsRepo brandGoodsRepo1 = new BrandGoodsRepo();
        brandGoodsRepo1.brandName = "헤라";
        brandGoodsRepo1.goodsName = "헤라 블랙 쿠션 파운데이션 본품 15g + 리필 15g";
        brandGoodsRepo1.goodsPrice = 28900;
        brandGoodsRepo1.goodsImg = "https://thumbnail7.coupangcdn.com/thumbnails/remote/492x492ex/image/product/image/vendoritem/2019/08/02/3125474810/deaf75f8-decb-421f-8727-542853e6bbcc.jpg";
        brandGoodsRepo1.url = "https://www.coupang.com/vp/products/22674288?itemId=88123406&vendorItemId=71731113747";

        BrandGoodsRepo brandGoodsRepo2 = new BrandGoodsRepo();
        brandGoodsRepo2.brandName = "달바";
        brandGoodsRepo2.goodsName = "[달바] 달바 프리미엄 미스트세럼 7병+필링패드";
        brandGoodsRepo2.goodsPrice = 79900;
        brandGoodsRepo2.goodsImg = "http://gdimg.gmarket.co.kr/1813802785/still/600";
        brandGoodsRepo2.url = "http://item.gmarket.co.kr/Item?goodscode=1813802785";

        BrandGoodsRepo brandGoodsRepo3 = new BrandGoodsRepo();
        brandGoodsRepo3.brandName = "이니스프리";
        brandGoodsRepo3.goodsName = "이니스프리 그린티 씨드 세럼";
        brandGoodsRepo3.goodsPrice = 13790;
        brandGoodsRepo3.goodsImg = "https://thumbnail10.coupangcdn.com/thumbnails/remote/492x492ex/image/product/image/vendoritem/2019/09/20/3571986112/95ef64f6-4d85-464a-a33e-60174585af5a.jpg";
        brandGoodsRepo3.url = "https://www.coupang.com/vp/products/68153339?itemId=227480670&vendorItemId=4618168759";

        BrandGoodsRepo brandGoodsRepo4 = new BrandGoodsRepo();
        brandGoodsRepo4.brandName = "마몽드";
        brandGoodsRepo4.goodsName = "마몽드 로즈워터 토너 500ml + 아모레퍼시픽 기초케어 3종";
        brandGoodsRepo4.goodsPrice = 14180;
        brandGoodsRepo4.goodsImg = "https://thumbnail10.coupangcdn.com/thumbnails/remote/492x492ex/image/retail/images/56901504824934-e931884d-e9d0-4c33-8f22-f82b4d0f1b68.jpg";
        brandGoodsRepo4.url = "https://www.coupang.com/vp/products/215532311?itemId=659560202&vendorItemId=4708668382";

        BrandGoodsRepo brandGoodsRepo5 = new BrandGoodsRepo();
        brandGoodsRepo5.brandName = "일리윤";
        brandGoodsRepo5.goodsName = "[일리윤] 일리윤 세라마이드아토집중크림 500ML 2개";
        brandGoodsRepo5.goodsPrice = 29900;
        brandGoodsRepo5.goodsImg = "http://gdimg1.gmarket.co.kr/goods_image2/exlarge_moreimg/182/406/1824062680/1824062680_00.jpg";
        brandGoodsRepo5.url = "http://item.gmarket.co.kr/Item?goodscode=1824062680";

        mRecommendGoodsList.add(brandGoodsRepo1);
        mRecommendGoodsList.add(brandGoodsRepo2);
        mRecommendGoodsList.add(brandGoodsRepo3);
        mRecommendGoodsList.add(brandGoodsRepo4);
        mRecommendGoodsList.add(brandGoodsRepo5);

        mRecommendPager.isCycle();
        mRecommendPager.setInterval(5000);
        mRecommendPager.startAutoScroll();
        mRecommendAdapter.notifyDataSetChanged();

    }

    private void getBrandGoodsList(String orderBy) {
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

            String userId = SharedPreferenceBase.getPrefString(getContext(), Config.USER_ID, null);
            RestAdvatarProtocol.getInstance().getBrandGoodsList(userId, brandCodes, today, 100, orderBy, new RequestListener() {
                @Override
                public void onRequestSuccess(int requestCode, Object result) {
                    mGoodsList.clear();
                    mGoodsList.addAll((List<BrandGoodsRepo>) result);
                    putGoodsList((List<BrandGoodsRepo>) result);
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
    }

    private void putGoodsList(List<BrandGoodsRepo> value) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = objectMapper.writeValueAsString(value);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (!value.isEmpty()) {
            SharedPreferenceBase.putPrefString(getContext(), Config.GOODS_LIST, jsonString);
        } else {
            SharedPreferenceBase.putPrefString(getContext(), Config.GOODS_LIST, null);
        }
    }

    private List<BrandGoodsRepo> getGoodsList() {
        ObjectMapper objectMapper = new ObjectMapper();
        List<BrandGoodsRepo> goodsList = new ArrayList<>();
        String goods = SharedPreferenceBase.getPrefString(getContext(), Config.GOODS_LIST, null);
        try {
            goodsList = objectMapper.readValue(goods, new TypeReference<List<BrandGoodsRepo>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return goodsList;
    }

    public class AutoScrollAdapter extends PagerAdapter {
        private List<BrandGoodsRepo> items;
        private LayoutInflater inflater;

        public AutoScrollAdapter(List<BrandGoodsRepo> items) {
            this.items = items;
            this.inflater = LayoutInflater.from(getContext());
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_online_recommend, null);

            BrandGoodsRepo item = items.get(position);

            ImageView imageView = view.findViewById(R.id.ad_product_image);

            ImageLoader.getInstance().displayImage(item.goodsImg, imageView, mImageLoaderOptions);
            TextView brandName = view.findViewById(R.id.brand_name);
            TextView productName = view.findViewById(R.id.product_name);
            TextView productPrice = view.findViewById(R.id.product_price);

            brandName.setText(item.brandName);
            productName.setText(item.goodsName);
            NumberFormat n = NumberFormat.getNumberInstance(Locale.KOREAN);
            productPrice.setText(n.format(item.goodsPrice) + "원");

            container.addView(view);

            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private class GridAdapter extends ArrayAdapter<BrandGoodsRepo> {

        private List<BrandGoodsRepo> items;
        private List<BrandGoodsRepo> header;

        public GridAdapter(Context context, int textViewResourceId, List<BrandGoodsRepo> items, List<BrandGoodsRepo> header) {
            super(context, textViewResourceId, items);
            this.items = items;
            this.header = header;
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
                    if (brandGoodsRepo.getPickYn()) {
                        holder.pickImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_heart_empty));
                        brandGoodsRepo.setPickYn(false);
                        String userId = SharedPreferenceBase.getPrefString(getContext(), Config.USER_ID, null);
                        try {
                            RestAdvatarProtocol.getInstance().onlinePickCancel(userId, brandGoodsRepo.collectSite, brandGoodsRepo.url, new RequestListener() {
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
                        onlinePickProductParam.siteName = brandGoodsRepo.collectSite;
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
            holder.sellAmount.setText("판매량 : " + brandGoodsRepo.sellAmount + " 매출액 : " + brandGoodsRepo.totalSellPrice);
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
