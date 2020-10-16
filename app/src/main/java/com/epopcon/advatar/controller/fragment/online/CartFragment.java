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

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.db.MessageDao;
import com.epopcon.advatar.common.model.OnlineBizType;
import com.epopcon.advatar.common.util.event.Event;
import com.epopcon.advatar.common.util.event.EventHandler;
import com.epopcon.advatar.common.util.event.EventTrigger;
import com.epopcon.advatar.controller.activity.online.OnlineStoreWebActivity;
import com.epopcon.advatar.controller.fragment.BaseFragment;
import com.epopcon.advatar.custom.listview.StickyListHeadersAdapter;
import com.epopcon.advatar.custom.listview.StickyListHeadersListView;
import com.epopcon.extra.online.OnlineConstant;
import com.epopcon.extra.online.model.CartDetail;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.epopcon.advatar.common.model.OnlineBizType.onlineStoreNameMap;

public class CartFragment extends BaseFragment {

    private StickyListHeadersListView mListView;
    private RelativeLayout mListHeaderView = null;
    private ListAdapter mListAdapter = null;
    private List<CartDetail> mListItemData = new ArrayList<>();
    private List<String> mStoreList = new ArrayList<>();

    private View mView;

    private ImageView mSyncImg;
    private TextView mTxtCartCount;
    private TextView mTxtRecentCount;
    private Animation rotateAnimation;

    protected DisplayImageOptions mImageLoaderOptions;
    protected MessageDao mMessageDao = MessageDao.getInstance();

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
                                    Toast.makeText(activity, String.format(getActivity().getApplicationContext().getResources().getString(R.string.online_cart_sync_start), NAME), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(activity, String.format(getActivity().getApplicationContext().getResources().getString(R.string.online_cart_sync_end)), Toast.LENGTH_SHORT).show();
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
            if (!mStoreList.contains(cartDetail.getStoreName())) {
                mStoreList.add(cartDetail.getStoreName());
            }
            mListItemData.add(cartDetail);
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
                    cartDetail = mListItemData.get(mListItemData.size()-1);
                } else {
                    return;
                }
            }

            OnlineBizType bizType = new OnlineBizType(activity);
            String storeName = bizType.name(OnlineConstant.valueOf(cartDetail.getStoreName()));

            Intent intent = new Intent(activity, OnlineStoreWebActivity.class);
            intent.putExtra("title", storeName);
            intent.putExtra("url", cartDetail.getDealUrl());

            startActivityForResult(intent, Config.REQ_ONLINE_STORE_WEBPAGE);
        }
    };

    private class ListAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer {

        private List<String> sections;
        private List<CartDetail> items;
        private LayoutInflater inflater;

        ListAdapter(List<String> sections, List<CartDetail> items) {
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
            return items.get(position).getStoreName().hashCode();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
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
                if (section.equals(items.get(i).getStoreName())) {
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
            return sections.indexOf(items.get(position).getStoreName());
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            ListAdapter.HeaderViewHolder holder;

            if (convertView == null) {
                holder = new ListAdapter.HeaderViewHolder();
                convertView = inflater.inflate(R.layout.item_header_payment_list, parent, false);
                holder.storeName = (TextView) convertView.findViewById(R.id.date);
                convertView.setTag(holder);
            } else {
                holder = (ListAdapter.HeaderViewHolder) convertView.getTag();
            }

            CartDetail cartDetail = mListItemData.get(position);
            String storeName = onlineStoreNameMap.get(cartDetail.getStoreName());

            holder.storeName.setText(storeName);

            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_online_cart_list, parent, false);

                holder = new ViewHolder();
                holder.cartType = (TextView) convertView.findViewById(R.id.cart_type);
                holder.dealImg = (ImageView) convertView.findViewById(R.id.img_product);
                holder.title = (TextView) convertView.findViewById(R.id.deal_name);
                holder.options = (TextView) convertView.findViewById(R.id.option_name);
                holder.selectCount = (TextView) convertView.findViewById(R.id.select_count);
                holder.amount = (TextView) convertView.findViewById(R.id.amount);
                holder.deliveryPolicy = (TextView) convertView.findViewById(R.id.delivery_policy);
                holder.avgDelivery = (TextView) convertView.findViewById(R.id.avg_delivery);
                holder.lineHolder = (RelativeLayout) convertView.findViewById(R.id.line_holder);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final CartDetail cartDetail = items.get(position);

            // image
            ImageLoader.getInstance().displayImage(cartDetail.getImgUrl(), holder.dealImg, mImageLoaderOptions);

            RelativeLayout cartTypeHolder = (RelativeLayout) convertView.findViewById(R.id.cart_type_holder);
            RelativeLayout cartTypeLineHolder = (RelativeLayout) convertView.findViewById(R.id.line_cart_type_holder);
            if (position != 0) {
                String beforeCartType = items.get(position -1 ).getCartType();
                String currentCartType = items.get(position).getCartType();
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

            String cartType;
            if (cartDetail.getCartType().equals("Cart")) {
                cartType = "장바구니";
                holder.selectCount.setVisibility(View.VISIBLE);
                holder.selectCount.setText("갯수 : " + cartDetail.getSelectCount());
                holder.options.setVisibility(View.VISIBLE);
                holder.deliveryPolicy.setVisibility(View.VISIBLE);
                holder.avgDelivery.setVisibility(View.VISIBLE);
            } else {
                cartType = "최근 본 상품";
                holder.selectCount.setVisibility(View.GONE);
                holder.options.setVisibility(View.GONE);
                holder.deliveryPolicy.setVisibility(View.GONE);
                holder.avgDelivery.setVisibility(View.GONE);
            }
            holder.cartType.setText(cartType);
            holder.title.setText(cartDetail.getTitle());
            if (TextUtils.isEmpty(cartDetail.getOptions())) {
                holder.options.setVisibility(View.GONE);
            }
            holder.options.setText(cartDetail.getOptions());
            // price
            NumberFormat n = NumberFormat.getNumberInstance(Locale.KOREAN);
            String price;
            price = n.format(cartDetail.getOptionPrice());

            holder.amount.setText(price + " 원");
            String delivery = "";

            if (cartDetail.getDeliveryPolicy() != null) {
                if (cartDetail.getDeliveryPolicy().equals("CONDITION")) {
                    delivery = "조건부 무료";
                } else if (cartDetail.getDeliveryPolicy().equals("FREE")) {
                    delivery = "무료배송";
                } else {
                    delivery = "유료배송";
                }
            }
            holder.deliveryPolicy.setText(delivery);
            if (cartDetail.getAvgDeliveryDays() == 0) {
                holder.avgDelivery.setVisibility(View.GONE);
            }
            holder.avgDelivery.setText("평균 배송일 : " + cartDetail.getAvgDeliveryDays());

            if ((position + 1) < items.size()) {
                if (cartDetail.getStoreName().equals(items.get(position+1).getStoreName()))
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
            TextView amount;
            TextView deliveryPolicy;
            TextView avgDelivery;
            RelativeLayout lineHolder;
        }
    }
}
