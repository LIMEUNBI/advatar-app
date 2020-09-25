package com.epopcon.advatar.controller.activity.online;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.model.OnlineStoreLoginManager;
import com.epopcon.advatar.common.util.DialogClickListener;
import com.epopcon.advatar.common.util.DialogUtil;
import com.epopcon.advatar.common.model.OnlineBizType;
import com.epopcon.advatar.common.model.OnlineType;
import com.epopcon.advatar.common.util.SharedPreferenceBase;
import com.epopcon.advatar.controller.activity.BaseActivity;
import com.epopcon.extra.ExtraClassLoader;
import com.epopcon.extra.online.OnlineConstant;
import com.epopcon.extra.online.OnlineDeliveryInquiry;
import com.epopcon.extra.online.OnlineDeliveryInquiryHandler;
import com.epopcon.extra.online.OnlineDeliveryInquiryHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OnlineListActivity extends BaseActivity {
    private final String TAG = OnlineListActivity.class.getSimpleName();

    private ListView mListView;

    private ListAdapter mAdapter = null;
    private Button button = null;

    private final String KEY_LIST_POSITION = "KEY_LIST_POSITION";
    private int firstVisible;

    private OnlineBizType bizType = null;

    private Map<String, Set<String>> removedOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_list);

        mListView = (ListView) findViewById(R.id.list_view);

        removedOrders = (Map) SharedPreferenceBase.getPrefObject(this, Config.ONLINE_REMOVED_ORDERS);
        if (removedOrders == null)
            removedOrders = new HashMap<>();

        if (savedInstanceState != null) {
            firstVisible = savedInstanceState.getInt(KEY_LIST_POSITION);
        }

        bizType = new OnlineBizType(this);

        mAdapter = new ListAdapter(bizType.groupCodes(), bizType.types());

        mListView.setAdapter(mAdapter);
        mListView.setSelection(firstVisible);

    }

    private void setLoginButton(Button button, boolean logoff) {
        if (button != null) {
            if (logoff) {
                button.setText("연동해제");
                button.setTextColor(getResources().getColor(R.color.text_a6a6a6));
            } else {
                button.setText("연동하기");
                button.setTextColor(getResources().getColor(R.color.blue_002080));
            }
        }
    }

    private void login(View v) {
        String code = (String) v.getTag();
        button = (Button) v;

        Intent intent = new Intent(this, OnlineLoginActivity.class);
        intent.putExtra("code", code);

        startActivityForResult(intent, Config.REQ_SETTING_ONLINE_STORE_LOGIN);
    }

    private void logout(View v) {
        final String code = (String) v.getTag();
        final Button button = (Button) v;

        AlertDialog.Builder builder = new AlertDialog.Builder(OnlineListActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_default, null);

        TextView title = (TextView) view.findViewById(R.id.alert_title);
        TextView content = (TextView) view.findViewById(R.id.alert_content);

        TextView cancel = (TextView) view.findViewById(R.id.btn_cancel);
        TextView confirm = (TextView) view.findViewById(R.id.btn_confirm);

        title.setText(R.string.dialog_logout_online_title);
        content.setText(R.string.dialog_logout_online_content);

        cancel.setText(R.string.dialog_button_cancel);
        confirm.setText(R.string.dialog_logout_online_confirm);

        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OnlineDeliveryInquiry inquiry = null;
                try {
                    OnlineConstant type = OnlineConstant.valueOf(code);
                    inquiry = ExtraClassLoader.getInstance().newOnlineDeliveryInquiry(type, new OnlineDeliveryInquiryHandler());

                    if (inquiry != null) {
                        inquiry.removeIdAndPassword();
                    }
                    // 로그아웃 상태로 변경
                    OnlineDeliveryInquiryHelper.setStatus(getApplicationContext(), type, OnlineConstant.ONLINE_STORE_PROCESS_STATUS_NOT_LOGIN);
                    // 주문 삭제 내역 제거
                    List<String> list = new ArrayList(removedOrders.keySet());
                    for(String orderNumber : list) {
                        if(orderNumber.startsWith(type.toString()))
                            removedOrders.remove(orderNumber);
                    }
                    mMessageDao.deleteOnlineStore(code);
                    SharedPreferenceBase.putPrefObject(OnlineListActivity.this, Config.ONLINE_REMOVED_ORDERS, removedOrders);

                    setLoginButton(button, false);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OnlineListActivity.this, R.string.settings_online_store_login_on_success, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OnlineListActivity.this, R.string.settings_online_store_login_on_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    if (inquiry != null)
                        inquiry.destory();
                    dialog.cancel();
                }
            }
        });
    }

    private class ListAdapter extends BaseAdapter implements SectionIndexer {

        private List<String[]> items;
        private List<String> sections;
        private LayoutInflater inflater;

        ListAdapter(List<String> sections, List<String[]> items) {
            this.sections = sections;
            this.items = items;
            this.inflater = LayoutInflater.from(getApplicationContext());
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ListAdapter.ViewHolder();
                convertView = inflater.inflate(R.layout.item_online_store_list, parent, false);

                holder.onlineLogo = (ImageView) convertView.findViewById(R.id.online_store_logo);
                holder.button = (Button) convertView.findViewById(R.id.online_store_login);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String[] data = items.get(position);

            String code = data[1];
            String name = data[2];

            OnlineConstant onlineConstant = OnlineType.onlineStoreCodeMap.get(code);
            holder.onlineLogo.setImageResource(OnlineType.onlineStoreLogoMap.get(onlineConstant));

            if (!OnlineDeliveryInquiryHelper.hasStoredIdAndPassword(OnlineListActivity.this, OnlineConstant.valueOf(code))) {
                setLoginButton(holder.button, false);
            } else {
                setLoginButton(holder.button, true);
            }

            holder.button.setTag(code);
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String code = (String) v.getTag();
                    if (OnlineStoreLoginManager.getInstance().getOnlineStoreStatus(OnlineBizType.onlineStoreMap.get(code))) {
                        if (!OnlineDeliveryInquiryHelper.hasStoredIdAndPassword(OnlineListActivity.this, OnlineConstant.valueOf(code))) {
                            login(v);
                        } else {
                            logout(v);
                        }
                    } else {
                        String title1 = "";
                        String content1;
                        content1 = String.format(getString(R.string.online_login_off));

                        DialogUtil.showCommonDialog(getApplicationContext(), OnlineListActivity.this,
                                title1, content1, true, false,
                                "확인", "",
                                new DialogClickListener() {
                                    @Override
                                    public void onPositiveClick() {

                                    }

                                    @Override
                                    public void onNegativeClick() {

                                    }
                                });
                    }
                }
            });

            return convertView;
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
                if (section.equals(items.get(i)[0])) {
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
            return sections.indexOf(items.get(position)[0]);
        }

        private class ViewHolder {
            public ImageView onlineLogo;
            public Button button;
        }
    }

}
