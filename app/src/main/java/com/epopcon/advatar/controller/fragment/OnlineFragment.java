package com.epopcon.advatar.controller.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.epopcon.advatar.R;
import com.epopcon.advatar.application.AdvatarApplication;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.db.MessageDao;
import com.epopcon.advatar.common.model.DateManager;
import com.epopcon.advatar.common.model.OnlineBizDetail;
import com.epopcon.advatar.common.model.OnlineBizType;
import com.epopcon.advatar.common.model.OnlineProductInfo;
import com.epopcon.advatar.common.util.event.Event;
import com.epopcon.advatar.common.util.event.EventHandler;
import com.epopcon.advatar.common.util.event.EventTrigger;
import com.epopcon.advatar.controller.activity.online.OnlineLoginActivity;
import com.epopcon.advatar.controller.activity.online.OnlineStoreWebActivity;
import com.epopcon.advatar.custom.listview.StickyListHeadersAdapter;
import com.epopcon.advatar.custom.listview.StickyListHeadersListView;
import com.epopcon.extra.online.OnlineConstant;
import com.epopcon.extra.online.OnlineDeliveryInquiryHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OnlineFragment extends BaseFragment {

    private static OnlineFragment instance = null;

    private TextView monthLabel;
    private Button mOnlineLogin;

    private FrameLayout btnNextMonth;
    private FrameLayout btnPreviousMonth;
    private RelativeLayout mListHeaderView = null;

    private StickyListHeadersListView mListView;
    private ListAdapter mListAdapter = null;
    private List<String> mListHeaderData = new ArrayList<>();
    private List<OnlineProductInfo> mListItemData = new ArrayList<>();
    private List<OnlineProductInfo> mThisMonthData = new ArrayList<>();

    private boolean mContentChanged = true;
    private String mSupportLoginUserEncIdList = "";
    private Animation rotateAnimation;

    // data
    private int mOnlineCount = 0;
    private int mOnlineAmount = 0;
    private RelativeLayout mNoListHolder;
    private RelativeLayout mNoListMonthlyHolder;
    private ImageView mSyncImg;
    private ImageView mSyncImg1;

    private View mView;
    private long mRowId = -1L;

    protected DisplayImageOptions mImageLoaderOptions;
    protected MessageDao mMessageDao = MessageDao.getInstance();
    protected DateManager mDateManager = DateManager.getInstance();

    public static OnlineFragment getInstance() {
        if (instance == null) {
            instance = new OnlineFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_online, container, false);
        mRowId = getActivity().getIntent().getLongExtra(Config.ROW_ID, -1L);

        monthLabel = (TextView) mView.findViewById(R.id.text_month_label);
        mListView = (StickyListHeadersListView) mView.findViewById(R.id.listview);

        mListView.setOnItemClickListener(mListClickListener);

        mListHeaderView = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.header_online_amount, null, false);
        mNoListHolder = (RelativeLayout) mView.findViewById(R.id.no_data_layout);
        mNoListMonthlyHolder = (RelativeLayout) mView.findViewById(R.id.no_data_monthly_layout);
        mOnlineLogin = (Button) mView.findViewById(R.id.btn_online_login);
        mListView.addHeaderView(mListHeaderView, null, false);
        mListAdapter = new ListAdapter(mListHeaderData, mThisMonthData);

        mListView.setAdapter(mListAdapter);
        mListView.setSelection(0);
        mListView.setDrawingListUnderStickyHeader(true);

        rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.card_view_sync_rotate);
        btnNextMonth = (FrameLayout) mView.findViewById(R.id.btn_next);
        btnPreviousMonth = (FrameLayout) mView.findViewById(R.id.btn_previous);
        btnNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDateManager.moveMonthAndSaveMonth(1, DateManager.SAVE_TIME_4);
                mContentChanged = true;
                refresh();
            }
        });

        btnPreviousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDateManager.moveMonthAndSaveMonth(-1, DateManager.SAVE_TIME_4);
                mContentChanged = true;
                refresh();
            }
        });

        mSyncImg = (ImageView) mView.findViewById(R.id.sync_image);
        mSyncImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean running = EventTrigger.getInstance(activity).isRunning(Event.Type.IMPORT_ONLINE_STORE.toString());

                if (running) {
                    Toast.makeText(activity, "동기화가 진행중입니다. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                view.startAnimation(rotateAnimation);

                EventTrigger.getInstance(activity).triggerService(new Event(Event.Type.IMPORT_ONLINE_STORE));
            }
        });

        mSyncImg1 = (ImageView) mView.findViewById(R.id.sync_image1);

        EventTrigger.getInstance(activity).register(this, Event.Type.ON_ONLINE_STORE_UPDATE, new EventHandler() {
            @Override
            public void onEvent(Event event) {
                String driven = event.getObject("driven", Event.Type.IMPORT_ONLINE_STORE.toString());

                if (driven.equals(Event.Type.IMPORT_ONLINE_STORE.toString())) {
                    boolean success = event.getObject("success", false);
                    int action = event.getObject("action", OnlineConstant.ACTION_QUERY_PAYMENT_DETAILS);
                    int status = event.getObject("status", 1);
                    final String NAME = event.getObject("name", "");

                    switch (status) {
                        case Config.EVENT_STATUS_STEP_START:
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, String.format(getContext().getResources().getString(R.string.online_payment_sync_start), NAME), Toast.LENGTH_SHORT).show();
                                    mSyncImg.startAnimation(rotateAnimation);
                                    mSyncImg1.startAnimation(rotateAnimation);
                                }
                            });
                            break;
                        case Config.EVENT_STATUS_STEP_PROGRESS:
                        case Config.EVENT_STATUS_STEP_END:
                            if (action == OnlineConstant.ACTION_QUERY_PAYMENT_DETAILS) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, String.format(getContext().getResources().getString(R.string.online_payment_sync_end)), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

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
                                    Toast.makeText(activity, String.format(getContext().getResources().getString(R.string.online_payment_sync_end)), Toast.LENGTH_SHORT).show();
                                    mSyncImg.clearAnimation();
                                    mSyncImg1.clearAnimation();
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

    public void refresh() {

        if (mListAdapter == null) return;

        mDateManager.moveToSaveTime(mDateManager.SAVE_TIME_4);

        mListHeaderData.clear();
        mListItemData.clear();
        mThisMonthData.clear();

        String supportLoginUserEncIdList = "";
        for (OnlineConstant onlineConstant : OnlineConstant.values()) {
            if (OnlineDeliveryInquiryHelper.hasStoredIdAndPassword(activity, onlineConstant)) {
                supportLoginUserEncIdList += "'" + OnlineDeliveryInquiryHelper.getStoredId(activity, onlineConstant) + "',";
            }
        }

        // 4. CreditMessage : 로그인된 상점의 모든 내역을 구함.
        if (!TextUtils.isEmpty(supportLoginUserEncIdList)) {
            supportLoginUserEncIdList = supportLoginUserEncIdList.substring(0, supportLoginUserEncIdList.length() - 1);
        }
        mSupportLoginUserEncIdList = supportLoginUserEncIdList;

        int totalAmount = 0;
        String groupKey;
        List<String> orderNumList = new ArrayList<>();
        List<OnlineProductInfo> listItemData = mMessageDao.getOnlineStoreProductDetails(mSupportLoginUserEncIdList, 0, Integer.parseInt(mDateManager.getStringEndDt("yyyyMMdd")), 0);
        for (OnlineProductInfo onlineProductInfo : listItemData) {

            groupKey = onlineProductInfo.getOrderDate("yyyy.MM.dd");
            if (!mListHeaderData.contains(groupKey))
                mListHeaderData.add(groupKey);

            mListItemData.add(onlineProductInfo);

            // 성능 이슈로 인하여 추후 CursorAdapter 로 변경해야함.
            // 그때 월 총 금액 구해오는 부분도 같이 수정필요.
            if (onlineProductInfo.getOrderDate() >= Integer.parseInt(mDateManager.getStringStartDt("yyyyMMdd")) &&
                    onlineProductInfo.getOrderDate() <= Integer.parseInt(mDateManager.getStringEndDt("yyyyMMdd"))) {
                if (!orderNumList.contains(onlineProductInfo.getOrderNumber()) && onlineProductInfo.getRefundAmount() == 0) {
                    totalAmount += mMessageDao.getOnlineStoreProductDetailsProductAmount(mSupportLoginUserEncIdList, onlineProductInfo.getOrderNumber());
                    orderNumList.add(onlineProductInfo.getOrderNumber());
                }

                mThisMonthData.add(onlineProductInfo);
            }
        }
        // Set Total Amount & Month
        monthLabel.setText(String.format("%s.%s", mDateManager.getPeriodYear(),mDateManager.getPeriodMonth()));

        mListAdapter.notifyDataSetChanged();

        if (mContentChanged) {

            // Set Next Button
            if (mDateManager.getCurrentMonthType() >= DateManager.THIS_MONTH) {
                btnNextMonth.setEnabled(false);
            } else {
                btnNextMonth.setEnabled(true);
            }

            // Set Previous Button
            int beforeCount = mMessageDao.getOnlineStoreProductDetails(mSupportLoginUserEncIdList, 0, Integer.parseInt(mDateManager.getStringEndDt(-1, "yyyyMMdd")), 0).size();
            if (beforeCount > 0) {
                btnPreviousMonth.setEnabled(true);
            } else {
                btnPreviousMonth.setEnabled(false);
            }
            mOnlineCount = mThisMonthData.size();
            mOnlineAmount = totalAmount;
            setHeaderAmount(mListHeaderView);
        }

        if (mSupportLoginUserEncIdList.isEmpty()) {
            mListView.setVisibility(View.GONE);
            mNoListHolder.setVisibility(View.VISIBLE);
            mOnlineLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), OnlineLoginActivity.class);
                    startActivity(intent);
                }
            });
        } else if(mThisMonthData.size() == 0) {
            mListView.setVisibility(View.GONE);
            mNoListMonthlyHolder.setVisibility(View.VISIBLE);
            mNoListHolder.setVisibility(View.GONE);
            mSyncImg1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean running = EventTrigger.getInstance(activity).isRunning(Event.Type.IMPORT_ONLINE_STORE.toString());

                    if (running) {
                        Toast.makeText(activity, "동기화가 진행중입니다. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mSyncImg1.startAnimation(rotateAnimation);

                    EventTrigger.getInstance(activity).triggerService(new Event(Event.Type.IMPORT_ONLINE_STORE));
                }
            });
        } else {
            mListView.setVisibility(View.VISIBLE);
            mNoListHolder.setVisibility(View.GONE);
            mNoListMonthlyHolder.setVisibility(View.GONE);
            mListAdapter.notifyDataSetChanged();
        }
    }

    private void setHeaderAmount(RelativeLayout headerView) {
        TextView onlineAmount  = (TextView) headerView.findViewById(R.id.amount);
        onlineAmount.setText(String.format(Locale.KOREAN, getContext().getResources().getString(R.string.amount_integer), mOnlineAmount));
    }

    AdapterView.OnItemClickListener mListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            OnlineProductInfo onlineProductInfo;
            // last item 선택 시, position 값이 header item 값과 합쳐진 값으로 넘어오게 됨.
            try {
                onlineProductInfo = (OnlineProductInfo) mListAdapter.getItem(position - mListView.getHeaderViewsCount());
            } catch (Exception e) {
                if (position - mListView.getHeaderViewsCount() > mListAdapter.getCount()) {
                    onlineProductInfo = mListItemData.get(mListItemData.size()-1);
                } else {
                    return;
                }
            }

            OnlineBizType bizType = new OnlineBizType(activity);
            String storeName = bizType.name(OnlineConstant.valueOf(onlineProductInfo.getStoreName()));

            Intent intent = new Intent(activity, OnlineStoreWebActivity.class);
            intent.putExtra("title", storeName);
            intent.putExtra("url", onlineProductInfo.getProductUrl());

            startActivityForResult(intent, Config.REQ_ONLINE_STORE_WEBPAGE);
        }
    };

    private class ListAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer {

        private List<String> sections;
        private List<OnlineProductInfo> items;
        private LayoutInflater inflater;

        ListAdapter(List<String> sections, List<OnlineProductInfo> items) {
            this.sections = sections;
            this.items = items;
            this.inflater = LayoutInflater.from(getContext());
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public long getHeaderId(int position) {
            return items.get(position).getOrderDate("yyyy.MM.dd").hashCode();
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
                if (section.equals(items.get(i).getOrderDate("yyyy.MM.dd"))) {
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
            return sections.indexOf(items.get(position).getOrderDate("yyyy.MM.dd"));
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;

            if (convertView == null) {
                holder = new HeaderViewHolder();
                convertView = inflater.inflate(R.layout.item_header_payment_list, parent, false);
                holder.tvDate = (TextView) convertView.findViewById(R.id.date);
                holder.tvDateAmount = (TextView) convertView.findViewById(R.id.date_amount);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }

            OnlineProductInfo onlineProductInfo = mThisMonthData.get(position);

            String headerDate = onlineProductInfo.getOrderDate("MM월 dd일 (EEE)");
            holder.tvDate.setText(headerDate);

            int totalUsage = mMessageDao.getOnlineStoreProductDetailsDailyAmount(mSupportLoginUserEncIdList, onlineProductInfo.getOrderDate());
            NumberFormat n = NumberFormat.getNumberInstance(Locale.KOREAN);
            String s = n.format(totalUsage);
            holder.tvDateAmount.setText(s + " 원");

            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_online_payment_list, parent, false);

                holder = new ViewHolder();
                holder.shop = (TextView) convertView.findViewById(R.id.store_name);
                holder.totalAmount = (TextView) convertView.findViewById(R.id.total_amount);
                holder.paymentInfo = (TextView) convertView.findViewById(R.id.paymentInfo);
                holder.bizType = (ImageView) convertView.findViewById(R.id.bizType);
                holder.amount = (TextView) convertView.findViewById(R.id.amount);
                holder.deliveryStatus = (TextView) convertView.findViewById(R.id.delivery_status);
                holder.datelinePlaceHolder = (RelativeLayout) convertView.findViewById(R.id.dateChangeSpaceHolder);
                holder.lineHolder = (RelativeLayout) convertView.findViewById(R.id.line_holder);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final OnlineProductInfo onlineProductInfo = items.get(position);

            // image
            ImageLoader.getInstance().displayImage(onlineProductInfo.getProductImageUrl(), holder.bizType, mImageLoaderOptions);

            // payment info
            OnlineBizType bizType = new OnlineBizType(activity);
            final String storeName = bizType.name(OnlineConstant.valueOf(onlineProductInfo.getStoreName()));
            holder.paymentInfo.setText(onlineProductInfo.getProductName());

            RelativeLayout storeHolder = (RelativeLayout) convertView.findViewById(R.id.online_store_holder);
            RelativeLayout storeLineHolder = (RelativeLayout) convertView.findViewById(R.id.line_store_holder);
            if (position != 0) {
                String beforeStore = items.get(position -1 ).getStoreName();
                String currentStore = items.get(position).getStoreName();
                int beforeDate = items.get(position - 1).getOrderDate();
                int currentDate = items.get(position).getOrderDate();
                String beforeOrder = items.get(position - 1).getOrderNumber();
                String currentOrder = items.get(position).getOrderNumber();
                if (!TextUtils.isEmpty(beforeStore)) {
                    if (beforeStore.equals(currentStore)) {
                        if (beforeDate != currentDate) {
                            storeHolder.setVisibility(View.VISIBLE);
                            storeLineHolder.setVisibility(View.VISIBLE);
                        } else {
                            if (!beforeOrder.equals(currentOrder)) {
                                storeHolder.setVisibility(View.VISIBLE);
                                storeLineHolder.setVisibility(View.VISIBLE);
                            } else {
                                storeHolder.setVisibility(View.GONE);
                                storeLineHolder.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        storeHolder.setVisibility(View.VISIBLE);
                        storeLineHolder.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                storeHolder.setVisibility(View.VISIBLE);
                storeLineHolder.setVisibility(View.VISIBLE);
            }

            // shop name
            holder.shop.setText(storeName);
            // delivery Sttus
            holder.deliveryStatus.setText(onlineProductInfo.getStatus());
            // price
            NumberFormat n = NumberFormat.getNumberInstance(Locale.KOREAN);
            String s;
            String price;
            if (onlineProductInfo.getTotalAmount() < 0) {
                s = "0";
            } else {
                s = n.format(onlineProductInfo.getTotalAmount());
            }
            if (onlineProductInfo.getStoreName().equals("NAVER")) {
                int total = mMessageDao.getOnlineStoreProductNaver(onlineProductInfo.getProductName(), onlineProductInfo.getOrderNumber(), onlineProductInfo.getPrice());
                price = n.format(total);
            } else {
                price = n.format(onlineProductInfo.getPrice());
            }
            holder.amount.setText(price + " 원");
            holder.totalAmount.setText(s + "원");

            if ((position + 1) < items.size()) {
                if (onlineProductInfo.getOrderDate("yyyy.MM.dd").equals(items.get(position+1).getOrderDate("yyyy.MM.dd")))
                    holder.lineHolder.setVisibility(View.VISIBLE);
                else
                    holder.lineHolder.setVisibility(View.GONE);
            }
            return convertView;
        }

        private class HeaderViewHolder {
            public TextView tvDate;
            public TextView tvDateAmount;
        }

        private class ViewHolder {
            TextView totalAmount;
            TextView shop;
            TextView paymentInfo;
            ImageView bizType;
            TextView amount;
            TextView deliveryStatus;
            RelativeLayout datelinePlaceHolder;
            RelativeLayout lineHolder;
        }
    }
}
