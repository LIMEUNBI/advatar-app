package com.epopcon.advatar.controller.fragment.online;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.db.MessageDao;
import com.epopcon.advatar.common.model.CartSearchInfo;
import com.epopcon.advatar.common.model.CartSearchParser;
import com.epopcon.advatar.common.util.event.Event;
import com.epopcon.advatar.common.util.event.EventHandler;
import com.epopcon.advatar.common.util.event.EventTrigger;
import com.epopcon.advatar.controller.activity.online.OnlineCartSearchActivity;
import com.epopcon.advatar.controller.activity.online.OnlineStoreWebActivity;
import com.epopcon.advatar.controller.fragment.BaseFragment;
import com.epopcon.advatar.custom.listview.StickyListHeadersAdapter;
import com.epopcon.advatar.custom.listview.StickyListHeadersListView;
import com.epopcon.extra.common.utils.ExecutorPool;
import com.epopcon.extra.online.model.CartDetail;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.epopcon.advatar.common.model.OnlineBizType.onlineStoreNameMap;
import static com.epopcon.extra.common.ExtraContext.runOnUiThread;

public class CartFragment extends BaseFragment {

    private StickyListHeadersListView mListView;
    private RelativeLayout mListHeaderView = null;
    private ListAdapter mListAdapter = null;
    private List<CartDetails> mListItemData = new ArrayList<>();
    private List<String> mStoreList = new ArrayList<>();

    private View mView;

    private ImageView mSyncImg;
    private TextView mTxtCartCount;

    private TextView mTxtRecentCount;
    private Animation rotateAnimation;

    protected DisplayImageOptions mImageLoaderOptions;
    protected MessageDao mMessageDao = MessageDao.getInstance();

    class CartDetails {
        private CartDetail cartDetail;
        private int lowestYn = 0;

        public CartDetail getCartDetail() {
            return cartDetail;
        }

        public void setCartDetail(CartDetail cartDetail) {
            this.cartDetail = cartDetail;
        }

        public int getLowestYn() {
            return lowestYn;
        }

        public void setLowestYn(int lowestYn) {
            this.lowestYn = lowestYn;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static CartFragment instance = null;

    public static CartFragment getInstance() {
        if (instance == null) {
            instance = new CartFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_online_cart, container, false);

        mListView = (StickyListHeadersListView) mView.findViewById(R.id.listview);
        mListHeaderView = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.header_online_cart, null, false);
        mListView.setOnItemClickListener(mListClickListener);
        mListView.addHeaderView(mListHeaderView, null, false);

        rotateAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.card_view_sync_rotate);
        mSyncImg = (ImageView) mListHeaderView.findViewById(R.id.sync_image);
        mSyncImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean running = EventTrigger.getInstance(activity).isRunning(Event.Type.IMPORT_ONLINE_STORE.toString());

                if (running) {
                    Toast.makeText(activity, "동기화가 진행중입니다. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                view.startAnimation(rotateAnimation);

                EventTrigger.getInstance(activity).triggerService(new Event(Event.Type.IMPORT_ONLINE_STORE_CART));
            }
        });

        mTxtCartCount = (TextView) mListHeaderView.findViewById(R.id.cart_count);
        mTxtRecentCount = (TextView) mListHeaderView.findViewById(R.id.recent_count);

        mListAdapter = new ListAdapter(mStoreList, mListItemData);

        mListView.setAdapter(mListAdapter);
        mListView.setSelection(0);
        mListView.setDrawingListUnderStickyHeader(true);

        EventTrigger.getInstance(activity).register(this, Event.Type.ON_ONLINE_STORE_UPDATE, new EventHandler() {
            @Override
            public void onEvent(Event event) {
                String driven = event.getObject("driven", Event.Type.IMPORT_ONLINE_STORE_CART.toString());

                if (driven.equals(Event.Type.IMPORT_ONLINE_STORE_CART.toString())) {
                    boolean success = event.getObject("success", false);
                    int status = event.getObject("status", 1);
                    final String NAME = event.getObject("name", "");

                    switch (status) {
                        case Config.EVENT_STATUS_STEP_START:
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, NAME + "의 장바구니 상품정보 동기화를 시작합니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        case Config.EVENT_STATUS_STEP_PROGRESS:
                        case Config.EVENT_STATUS_STEP_END:
                            if (success) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refresh();
                                    }
                                });
                            }
                            break;
                        case Config.EVENT_STATUS_ALL_END:
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refresh();
                                    Toast.makeText(activity, "온라인 쇼핑몰 장바구니 상품정보 동기화를 완료하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                    }
                }
            }
        });

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void setHeaderDate() {
        int cartCount = mMessageDao.getOnlineStoreCartCount("Cart");
        int recentCount = mMessageDao.getOnlineStoreCartCount("Recent");

        mTxtCartCount.setText(cartCount + "");
        mTxtRecentCount.setText(recentCount + "");
    }

    public void refresh() {

        setHeaderDate();
        if (mListAdapter == null) return;

        mStoreList.clear();
        mListItemData.clear();

        List<CartDetail> listItemData = mMessageDao.getOnlineStoreCartDetails();
        for (CartDetail cartDetail : listItemData) {
            CartDetails listItems = new CartDetails();
            if (!mStoreList.contains(cartDetail.getStoreName())) {
                mStoreList.add(cartDetail.getStoreName());
            }
            listItems.setCartDetail(cartDetail);
            mListItemData.add(listItems);
        }

        mListAdapter.notifyDataSetChanged();
    }

    AdapterView.OnItemClickListener mListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CartDetail cartDetail;
            // last item 선택 시, position 값이 header item 값과 합쳐진 값으로 넘어오게 됨.
            try {
                cartDetail = (CartDetail) mListAdapter.getItem(position - mListView.getHeaderViewsCount());
            } catch (Exception e) {
                if (position - mListView.getHeaderViewsCount() > mListAdapter.getCount()) {
                    cartDetail = mListItemData.get(mListItemData.size()-1).getCartDetail();
                } else {
                    return;
                }
            }

            Intent intent = new Intent(activity, OnlineStoreWebActivity.class);
            intent.putExtra("url", cartDetail.getDealUrl());

            startActivityForResult(intent, Config.REQ_ONLINE_STORE_WEBPAGE);
        }
    };

    private class ListAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer {

        private List<String> sections;
        private List<CartDetails> items;
        private LayoutInflater inflater;

        ListAdapter(List<String> sections, List<CartDetails> items) {
            this.sections = sections;
            this.items = items;
            this.inflater = LayoutInflater.from(getActivity().getApplicationContext());
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public long getHeaderId(int position) {
            return items.get(position).cartDetail.getStoreName().hashCode();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return items.get(position).getCartDetail();
        }

        @Override
        public Object[] getSections() {
            return sections.toArray(new String[sections.size()]);
        }

        @Override
        public int getPositionForSection(int sectionIndex) {
            if (sectionIndex >= sections.size()) {
                sectionIndex = sections.size() - 1;
            } else if (sectionIndex < 0) {
                sectionIndex = 0;
            }

            int position = 0;
            String section = sections.get(sectionIndex);

            for (int i = 0; i < items.size(); i++) {
                if (section.equals(items.get(i).cartDetail.getStoreName())) {
                    position = i;
                    break;
                }
            }
            return position;
        }

        @Override
        public int getSectionForPosition(int position) {
            if (position >= items.size()) {
                position = items.size() - 1;
            } else if (position < 0) {
                position = 0;
            }
            return sections.indexOf(items.get(position).cartDetail.getStoreName());
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            ListAdapter.HeaderViewHolder holder;

            if (convertView == null) {
                holder = new ListAdapter.HeaderViewHolder();
                convertView = inflater.inflate(R.layout.item_header_store_list, parent, false);
                holder.storeName = (TextView) convertView.findViewById(R.id.store_name);
                convertView.setTag(holder);
            } else {
                holder = (ListAdapter.HeaderViewHolder) convertView.getTag();
            }

            final CartDetails cartDetail = mListItemData.get(position);
            final String storeName = onlineStoreNameMap.get(cartDetail.getCartDetail().getStoreName());

            holder.storeName.setText(storeName);

            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_online_cart_list, parent, false);

                holder = new ViewHolder();
                holder.cartType = (TextView) convertView.findViewById(R.id.cart_type);
                holder.dealImg = (ImageView) convertView.findViewById(R.id.img_product);
                holder.title = (TextView) convertView.findViewById(R.id.deal_name);
                holder.options = (TextView) convertView.findViewById(R.id.option_name);
                holder.selectCount = (TextView) convertView.findViewById(R.id.select_count);
                holder.search = (TextView) convertView.findViewById(R.id.txt_search);
                holder.loading = (ImageView) convertView.findViewById(R.id.img_loading);
                holder.searchLayout = (RelativeLayout) convertView.findViewById(R.id.search_layout);
                holder.amount = (TextView) convertView.findViewById(R.id.amount);
                holder.deliveryPolicy = (TextView) convertView.findViewById(R.id.delivery_policy);
                holder.lineHolder = (RelativeLayout) convertView.findViewById(R.id.line_holder);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final CartDetails cartDetail = items.get(position);

            // image
            ImageLoader.getInstance().displayImage(cartDetail.getCartDetail().getImgUrl(), holder.dealImg, mImageLoaderOptions);
            Glide.with(getContext()).asGif().load(R.raw.loading).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(holder.loading);
            holder.loading.setVisibility(View.GONE);

            RelativeLayout cartTypeHolder = (RelativeLayout) convertView.findViewById(R.id.cart_type_holder);
            RelativeLayout cartTypeLineHolder = (RelativeLayout) convertView.findViewById(R.id.line_cart_type_holder);
            if (position != 0) {
                String beforeCartType = items.get(position -1 ).getCartDetail().getCartType();
                String currentCartType = items.get(position).getCartDetail().getCartType();
                if (!TextUtils.isEmpty(beforeCartType)) {
                    if (beforeCartType.equals(currentCartType)) {
                        cartTypeHolder.setVisibility(View.GONE);
                        cartTypeLineHolder.setVisibility(View.GONE);
                    } else {
                        cartTypeHolder.setVisibility(View.VISIBLE);
                        cartTypeLineHolder.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                cartTypeHolder.setVisibility(View.VISIBLE);
                cartTypeLineHolder.setVisibility(View.VISIBLE);
            }

            final String cartType;
            if (cartDetail.getCartDetail().getCartType().equals("Cart")) {
                cartType = "장바구니";
                holder.selectCount.setVisibility(View.VISIBLE);
                holder.selectCount.setText("갯수 : " + cartDetail.getCartDetail().getSelectCount());
                holder.options.setVisibility(View.VISIBLE);
                holder.deliveryPolicy.setVisibility(View.VISIBLE);
            } else {
                cartType = "최근 본 상품";
                holder.selectCount.setVisibility(View.GONE);
                holder.options.setVisibility(View.GONE);
                holder.deliveryPolicy.setVisibility(View.GONE);
            }
            holder.cartType.setText(cartType);
            holder.title.setText(cartDetail.getCartDetail().getTitle());
            if (TextUtils.isEmpty(cartDetail.getCartDetail().getOptions())) {
                holder.options.setVisibility(View.GONE);
            }
            holder.options.setText(cartDetail.getCartDetail().getOptions());
            // price
            NumberFormat n = NumberFormat.getNumberInstance(Locale.KOREAN);
            String price;
            price = n.format(cartDetail.getCartDetail().getOptionPrice());

            holder.search.setVisibility(View.VISIBLE);
            holder.searchLayout.setVisibility(View.VISIBLE);
            holder.search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.loading.setVisibility(View.VISIBLE);
                    ExecutorPool.NETWORK.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String baseSearchUrl = "https://search.shopping.naver.com/search/all?query=";

                                List<CartSearchInfo> cartSearchInfos;
                                URL url = new URL(baseSearchUrl + cartDetail.getCartDetail().getTitle());
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("GET");

                                int responseCode = connection.getResponseCode();
                                if (responseCode == 200) {
                                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                    String line;
                                    String content = "";
                                    while ((line = br.readLine()) != null) {
                                        content += line;
                                    }
                                    cartSearchInfos = CartSearchParser.parseCartSearch(content);

                                    if (cartSearchInfos != null && cartSearchInfos.size() > 0) {
                                        Intent intent = new Intent(getActivity(), OnlineCartSearchActivity.class);
                                        intent.putExtra("title", cartDetail.getCartDetail().getTitle());
                                        intent.putExtra("option", cartDetail.getCartDetail().getOptions());
                                        intent.putExtra("price", cartDetail.getCartDetail().getOptionPrice());
                                        intent.putExtra("imgUrl", cartDetail.getCartDetail().getImgUrl());
                                        intent.putExtra("deliveryAmount", cartDetail.getCartDetail().getDeliveryAmount());
                                        intent.putParcelableArrayListExtra("cartSearchInfoList", (ArrayList<CartSearchInfo>) cartSearchInfos);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                holder.loading.setVisibility(View.GONE);
                                                cartDetail.setLowestYn(1);
                                            }
                                        });

                                        startActivity(intent);
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(), "최저가 검색 결과가 없는 상품입니다.", Toast.LENGTH_LONG).show();
                                                holder.searchLayout.setVisibility(View.GONE);
                                                cartDetail.setLowestYn(0);
                                            }
                                        });
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            if (cartDetail.getLowestYn() == 0) {
                holder.searchLayout.setVisibility(View.VISIBLE);
            } else {
                holder.searchLayout.setVisibility(View.GONE);
            }

            holder.amount.setText(price + " 원");
            String delivery = "";

            if (cartDetail.getCartDetail().getDeliveryPolicy() != null) {
                if (cartDetail.getCartDetail().getDeliveryPolicy().equals("CONDITION")) {
                    delivery = "조건부 무료";
                } else if (cartDetail.getCartDetail().getDeliveryPolicy().equals("FREE")) {
                    delivery = "무료배송";
                } else if (cartDetail.getCartDetail().getDeliveryPolicy().equals("AFTER")) {
                    delivery = "착불";
                } else {
                    delivery = "유료배송";
                }
            }
            holder.deliveryPolicy.setText(delivery);

            if ((position + 1) < items.size()) {
                if (cartDetail.getCartDetail().getStoreName().equals(items.get(position+1).getCartDetail().getStoreName()))
                    holder.lineHolder.setVisibility(View.VISIBLE);
                else
                    holder.lineHolder.setVisibility(View.GONE);
            }
            return convertView;
        }

        private class HeaderViewHolder {
            public TextView storeName;
        }

        private class ViewHolder {
            TextView cartType;
            ImageView dealImg;
            TextView title;
            TextView options;
            TextView selectCount;
            TextView search;
            ImageView loading;
            RelativeLayout searchLayout;
            TextView amount;
            TextView deliveryPolicy;
            RelativeLayout lineHolder;
        }
    }
}
