package com.epopcon.advatar.controller.activity.brand;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.common.util.SharedPreferenceBase;
import com.epopcon.advatar.controller.activity.common.BaseActivity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class SellerChoiceActivity extends BaseActivity {

    ArrayList<String> mSellerList;
    ArrayList<String> mSellerListCopy;

    private EditText mEditSearch = null;

    private GridView mGridView = null;
    private GridAdapter mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_choice);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarMessage = (TextView) findViewById(R.id.toolbar_title);
        toolbarMessage.setText("셀러 선택");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        mSellerList = new ArrayList<>();
        mSellerListCopy = new ArrayList<>();

        mGridView = (GridView) findViewById(R.id.card_grid_view);
        mGridView.setVisibility(View.VISIBLE);

        mEditSearch = (EditText) findViewById(R.id.edit_search);
        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                search(mEditSearch.getText().toString());
            }
        });
        
        mAdapter = new GridAdapter(getApplicationContext(), R.layout.item_brand_list, mSellerList);
        mGridView.setAdapter(mAdapter);
    }

    private void refresh() {
        if (mSellerList.isEmpty() || mSellerList == null) {
            if (getSellerList() != null && !getSellerList().isEmpty()) {
                mSellerList.addAll(getSellerList());
            } else {
                getSeller();
            }
        }
    }

    private void getSeller() {
        try {
            RestAdvatarProtocol.getInstance().getSellerList(new RequestListener() {
                @Override
                public void onRequestSuccess(int requestCode, Object result) {
                    mSellerList.clear();
                    mSellerList.addAll((List<String>) result);
                    putSellerList(mSellerList);
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

    private void putSellerList(List<String> value) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = objectMapper.writeValueAsString(value);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (!value.isEmpty()) {
            SharedPreferenceBase.putPrefString(getApplicationContext(), Config.SELLER_LIST, jsonString);
        } else {
            SharedPreferenceBase.putPrefString(getApplicationContext(), Config.SELLER_LIST, null);
        }
    }

    private List<String> getSellerList() {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> goodsList = new ArrayList<>();
        String goods = SharedPreferenceBase.getPrefString(getApplicationContext(), Config.SELLER_LIST, null);
        try {
            goodsList = objectMapper.readValue(goods, new TypeReference<List<String>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return goodsList;
    }

    private void search(String searchKeyword) {

        if (mSellerListCopy.size() == 0) {
            mSellerListCopy.addAll(mSellerList);
        }
        mSellerList.clear();

        if (searchKeyword.length() == 0) {
            mSellerList.addAll(mSellerListCopy);
        } else {
            for (int i = 0 ; i < mSellerListCopy.size() ; i++) {
                if (mSellerListCopy.get(i).toLowerCase().contains(searchKeyword)) {
                    mSellerList.add(mSellerListCopy.get(i));
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private class GridAdapter extends ArrayAdapter<String> {

        private ArrayList<String> items;

        public GridAdapter(Context context, int textViewResourceId, ArrayList<String> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_seller_list, null);

                holder.sellerLayout = (RelativeLayout) convertView.findViewById(R.id.brand_layout);
                holder.sellerName = (TextView) convertView.findViewById(R.id.brand_name);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final String sellerName = items.get(position);

            holder.sellerName.setText(sellerName);

            holder.sellerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferenceBase.putPrefString(getContext(), Config.SELLER_NAME, sellerName);
                    finish();
                }
            });

            return convertView;
        }

        private class ViewHolder {
            public RelativeLayout sellerLayout;
            public TextView sellerName;
        }
    }
}
