package com.epopcon.advatar.controller.activity.online;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.model.CartSearchInfo;
import com.epopcon.advatar.controller.activity.common.BaseActivity;
import com.epopcon.extra.online.model.CartDetail;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class CartSearchActivity extends BaseActivity {

    private final String TAG = CartSearchActivity.class.getSimpleName();

    private ImageView mCurrentImg;
    private TextView mCurrentTitle;
    private TextView mCurrentOption;
    private TextView mCurrentPrice;
    private TextView mCurrentDeliveryAmount;

    private ListView mListView;
    private ListAdapter mAdapter = null;

    private List<CartSearchInfo> mItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_cart_search);

        mCurrentImg = (ImageView) findViewById(R.id.img_product);
        mCurrentTitle = (TextView) findViewById(R.id.deal_name);
        mCurrentOption = (TextView) findViewById(R.id.option_name);
        mCurrentPrice = (TextView) findViewById(R.id.amount);
        mCurrentDeliveryAmount = (TextView) findViewById(R.id.delivery_policy);

        ImageLoader.getInstance().displayImage(getIntent().getStringExtra("imgUrl"), mCurrentImg, mImageLoaderOptions);
        mCurrentTitle.setText(getIntent().getStringExtra("title"));
        if (TextUtils.isEmpty(getIntent().getStringExtra("option"))) {
            mCurrentOption.setVisibility(View.GONE);
        } else {
            mCurrentOption.setVisibility(View.VISIBLE);
        }
        mCurrentOption.setText(getIntent().getStringExtra("option"));
        mCurrentPrice.setText(String.format(getString(R.string.amount_integer), getIntent().getIntExtra("price", 0)) + "원");

        if (getIntent().getIntExtra("deliveryAmount", 0) == 0) {
            mCurrentDeliveryAmount.setVisibility(View.GONE);
        }
        mCurrentDeliveryAmount.setText("배송비 : " + String.format(getString(R.string.amount_integer), getIntent().getIntExtra("deliveryAmount", 0)) + "원");

        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setOnItemClickListener(mListClickListener);
        mItemList = getIntent().getParcelableArrayListExtra("cartSearchInfoList");
        mAdapter = new ListAdapter(getApplicationContext(), R.layout.item_online_cart_search_list, mItemList);

        mListView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
    }

    AdapterView.OnItemClickListener mListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CartSearchInfo cartSearchInfo;
            try {
                cartSearchInfo = (CartSearchInfo) mAdapter.getItem(position - mListView.getHeaderViewsCount());
            } catch (Exception e) {
                if (position - mListView.getHeaderViewsCount() > mAdapter.getCount()) {
                    cartSearchInfo = mItemList.get(mItemList.size()-1);
                } else {
                    return;
                }
            }

            Intent intent = new Intent(getApplicationContext(), OnlineStoreWebActivity.class);
            intent.putExtra("url", cartSearchInfo.getProductUrl());

            startActivityForResult(intent, Config.REQ_ONLINE_STORE_WEBPAGE);
        }
    };

    private class ListAdapter extends ArrayAdapter {

        private List<CartSearchInfo> items;

        public ListAdapter(Context context, int resource, List<CartSearchInfo> itemList) {
            super(context, resource, itemList);
            this.items = itemList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_online_cart_search_list, null);

                holder.productName = (TextView) convertView.findViewById(R.id.product_name);
                holder.productDetail = (TextView) convertView.findViewById(R.id.product_detail);
                holder.category = (TextView) convertView.findViewById(R.id.category);

                holder.reviewCount = (TextView) convertView.findViewById(R.id.review_count);
                holder.amount = (TextView) convertView.findViewById(R.id.amount);
                holder.delivery = (TextView) convertView.findViewById(R.id.delivery_policy);
                holder.purchaseCount = (TextView) convertView.findViewById(R.id.purchase_count);
                holder.sellerName = (TextView) convertView.findViewById(R.id.seller_name);

                holder.sellerLayout = (LinearLayout) convertView.findViewById(R.id.seller_layout);

                holder.sellerLayout1 = (RelativeLayout) convertView.findViewById(R.id.seller_layout1);
                holder.sellerLayout2 = (RelativeLayout) convertView.findViewById(R.id.seller_layout2);
                holder.sellerLayout3 = (RelativeLayout) convertView.findViewById(R.id.seller_layout3);
                holder.sellerLayout4 = (RelativeLayout) convertView.findViewById(R.id.seller_layout4);
                holder.sellerLayout5 = (RelativeLayout) convertView.findViewById(R.id.seller_layout5);

                holder.seller1 = (TextView) convertView.findViewById(R.id.seller1);
                holder.seller2 = (TextView) convertView.findViewById(R.id.seller2);
                holder.seller3 = (TextView) convertView.findViewById(R.id.seller3);
                holder.seller4 = (TextView) convertView.findViewById(R.id.seller4);
                holder.seller5 = (TextView) convertView.findViewById(R.id.seller5);

                holder.sellerPrice1 = (TextView) convertView.findViewById(R.id.seller_price1);
                holder.sellerPrice2 = (TextView) convertView.findViewById(R.id.seller_price2);
                holder.sellerPrice3 = (TextView) convertView.findViewById(R.id.seller_price3);
                holder.sellerPrice4 = (TextView) convertView.findViewById(R.id.seller_price4);
                holder.sellerPrice5 = (TextView) convertView.findViewById(R.id.seller_price5);

                holder.sellerDelivery1 = (TextView) convertView.findViewById(R.id.seller_delivery1);
                holder.sellerDelivery2 = (TextView) convertView.findViewById(R.id.seller_delivery2);
                holder.sellerDelivery3 = (TextView) convertView.findViewById(R.id.seller_delivery3);
                holder.sellerDelivery4 = (TextView) convertView.findViewById(R.id.seller_delivery4);
                holder.sellerDelivery5 = (TextView) convertView.findViewById(R.id.seller_delivery5);

                holder.line = (View) convertView.findViewById(R.id.view);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final CartSearchInfo cartSearchInfo = items.get(position);

            holder.productName.setText(cartSearchInfo.getProductName());

            if (TextUtils.isEmpty(cartSearchInfo.getProductDetail())) {
                holder.productDetail.setVisibility(View.GONE);
            }
            holder.productDetail.setText(cartSearchInfo.getProductDetail());

            holder.category.setText(cartSearchInfo.getProductCate());
            holder.reviewCount.setText("리뷰 : " + cartSearchInfo.getProductReviewCount());

            holder.amount.setText(String.format(getString(R.string.amount_integer), cartSearchInfo.getProductPrice()) + "원");
            if (TextUtils.isEmpty(cartSearchInfo.getDeliveryInfo())) {
                holder.delivery.setVisibility(View.GONE);
            } else {
                holder.delivery.setVisibility(View.VISIBLE);
            }
            holder.delivery.setText(cartSearchInfo.getDeliveryInfo());

            if (cartSearchInfo.getProductPurchaseCount() == 0) {
                holder.purchaseCount.setVisibility(View.GONE);
            }
            holder.purchaseCount.setText("구매 갯수 : " + cartSearchInfo.getProductPurchaseCount());

            if (cartSearchInfo.getSellerStore1() != null) {
                holder.sellerLayout1.setVisibility(View.VISIBLE);
                holder.sellerName.setVisibility(View.GONE);
                holder.seller1.setText(cartSearchInfo.getSellerStore1());
                holder.sellerPrice1.setText(String.format(getString(R.string.amount_integer), cartSearchInfo.getSellerPrice1()) + "원");
                holder.sellerDelivery1.setText(cartSearchInfo.getSellerDelivery1());
                holder.sellerLayout1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CartSearchActivity.this, OnlineStoreWebActivity.class);
                        intent.putExtra("url", cartSearchInfo.getSellerUrl1());
                        startActivity(intent);
                    }
                });
            } else {
                holder.sellerLayout1.setVisibility(View.INVISIBLE);
                holder.sellerLayout.setVisibility(View.GONE);
                holder.sellerName.setVisibility(View.VISIBLE);
                holder.sellerName.setText(cartSearchInfo.getSellerName());
            }

            if (cartSearchInfo.getSellerStore2() != null) {
                holder.sellerLayout2.setVisibility(View.VISIBLE);
                holder.seller2.setText(cartSearchInfo.getSellerStore2());
                holder.sellerPrice2.setText(String.format(getString(R.string.amount_integer), cartSearchInfo.getSellerPrice2()) + "원");
                holder.sellerDelivery2.setText(cartSearchInfo.getSellerDelivery2());
                holder.sellerLayout2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CartSearchActivity.this, OnlineStoreWebActivity.class);
                        intent.putExtra("url", cartSearchInfo.getSellerUrl2());
                        startActivity(intent);
                    }
                });
            } else {
                holder.sellerLayout2.setVisibility(View.INVISIBLE);
            }

            if (cartSearchInfo.getSellerStore3() != null) {
                holder.sellerLayout3.setVisibility(View.VISIBLE);
                holder.seller3.setText(cartSearchInfo.getSellerStore3());
                holder.sellerPrice3.setText(String.format(getString(R.string.amount_integer), cartSearchInfo.getSellerPrice3()) + "원");
                holder.sellerDelivery3.setText(cartSearchInfo.getSellerDelivery3());
                holder.sellerLayout3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CartSearchActivity.this, OnlineStoreWebActivity.class);
                        intent.putExtra("url", cartSearchInfo.getSellerUrl3());
                        startActivity(intent);
                    }
                });
            } else {
                holder.sellerLayout3.setVisibility(View.INVISIBLE);
            }

            if (cartSearchInfo.getSellerStore4() != null) {
                holder.sellerLayout4.setVisibility(View.VISIBLE);
                holder.seller4.setText(cartSearchInfo.getSellerStore4());
                holder.sellerPrice4.setText(String.format(getString(R.string.amount_integer), cartSearchInfo.getSellerPrice4()) + "원");
                holder.sellerDelivery4.setText(cartSearchInfo.getSellerDelivery4());
                holder.sellerLayout4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CartSearchActivity.this, OnlineStoreWebActivity.class);
                        intent.putExtra("url", cartSearchInfo.getSellerUrl4());
                        startActivity(intent);
                    }
                });
            } else {
                holder.sellerLayout4.setVisibility(View.INVISIBLE);
            }

            if (cartSearchInfo.getSellerStore5() != null) {
                holder.sellerLayout5.setVisibility(View.VISIBLE);
                holder.seller5.setText(cartSearchInfo.getSellerStore5());
                holder.sellerPrice5.setText(String.format(getString(R.string.amount_integer), cartSearchInfo.getSellerPrice5()) + "원");
                holder.sellerDelivery5.setText(cartSearchInfo.getSellerDelivery5());
                holder.sellerLayout5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CartSearchActivity.this, OnlineStoreWebActivity.class);
                        intent.putExtra("url", cartSearchInfo.getSellerUrl5());
                        startActivity(intent);
                    }
                });
            } else {
                holder.sellerLayout5.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

        private class ViewHolder {
            public TextView productName;
            public TextView productDetail;
            public TextView category;
            public TextView reviewCount;

            public TextView amount;
            public TextView delivery;
            public TextView purchaseCount;
            public TextView sellerName;

            public LinearLayout sellerLayout;

            public RelativeLayout sellerLayout1;
            public RelativeLayout sellerLayout2;
            public RelativeLayout sellerLayout3;
            public RelativeLayout sellerLayout4;
            public RelativeLayout sellerLayout5;

            public TextView seller1;
            public TextView seller2;
            public TextView seller3;
            public TextView seller4;
            public TextView seller5;

            public TextView sellerPrice1;
            public TextView sellerPrice2;
            public TextView sellerPrice3;
            public TextView sellerPrice4;
            public TextView sellerPrice5;

            public TextView sellerDelivery1;
            public TextView sellerDelivery2;
            public TextView sellerDelivery3;
            public TextView sellerDelivery4;
            public TextView sellerDelivery5;

            public View line;
        }
    }
}
