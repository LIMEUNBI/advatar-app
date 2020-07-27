package com.epopcon.advatar.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.repo.BrandRepo;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.common.util.SharedPreferenceBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BrandChoiceActivity extends BaseActivity {

    ArrayList<BrandRepo> mBrandList = null;

    private EditText mEditSearch = null;
    private Button mBtnSearch = null;
    private TextView mChoicedBrand = null;

    private GridView mGridView = null;
    private GridAdapter mAdapter = null;
    private Button mBtnChoice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_choice);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarMessage = (TextView) findViewById(R.id.toolbar_title);
        toolbarMessage.setText("선호 브랜드 선택");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        mBrandList = new ArrayList<>();

        mChoicedBrand = (TextView) findViewById(R.id.choice_brand);

        mChoicedBrand.setText(getBrandNameList());

        mEditSearch = (EditText) findViewById(R.id.edit_search);
        mBtnSearch = (Button) findViewById(R.id.btn_search);

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0 ; i < mBrandList.size() ; i++) {
                    if (mBrandList.get(i).brandName.equals(mEditSearch.getText().toString())) {
                        mBrandList.get(i).setMyBrandYn(true);
                        if (getBrandCodeList().equals("")) {
                            putBrandCodeList(mBrandList.get(i).brandCode + ",");
                        } else {
                            putBrandCodeList(getBrandCodeList() + mBrandList.get(i).brandCode + ",");
                        }
                        if (getBrandNameList().equals("")) {
                            putBrandCodeList(mBrandList.get(i).brandName + ", ");
                        } else {
                            if (!getBrandNameList().contains(mBrandList.get(i).brandName)) {
                                putBrandNameList(getBrandNameList() + mBrandList.get(i).brandName + ", ");
                            }
                        }
                        mChoicedBrand.setText(getBrandNameList());

                        Collections.sort(mBrandList, brandComparator);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        mGridView = (GridView) findViewById(R.id.card_grid_view);
        mBtnChoice = (Button) findViewById(R.id.btn_end);

        mBtnChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                putBrandCodeList("");
                for (int i = 0 ; i < mBrandList.size() ; i++) {
                    if (mBrandList.get(i).isMyBrandYn()) {
                        putBrandCodeList(getBrandCodeList() + mBrandList.get(i).brandCode + ",");
                    }
                }
                if (getBrandCodeList().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "브랜드를 선택하세요.", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(BrandChoiceActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        mAdapter = new GridAdapter(getApplicationContext(), R.layout.item_brand_list, mBrandList);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {

        mBrandList.clear();

        try {
            RestAdvatarProtocol.getInstance().getBrandList(20, new RequestListener() {
                @Override
                public void onRequestSuccess(int requestCode, Object result) {
                    mBrandList.addAll((List<BrandRepo>) result);

                    for (int i = 0 ; i < mBrandList.size() ; i++) {
                        if (getBrandCodeList().contains(mBrandList.get(i).brandCode)) {
                            mBrandList.get(i).setMyBrandYn(true);
                        } else {
                            mBrandList.get(i).setMyBrandYn(false);
                        }
                    }

                    Collections.sort(mBrandList, brandComparator);
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

    private final static Comparator<BrandRepo> brandComparator = new Comparator<BrandRepo>() {
        @Override
        public int compare(BrandRepo brandRepo1, BrandRepo brandRepo2) {
            return brandRepo1.isMyBrandYn() && brandRepo2.isMyBrandYn() ? 0 : brandRepo1.isMyBrandYn() && !brandRepo2.isMyBrandYn() ? -1 : 1;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(BrandChoiceActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    private void putBrandCodeList(String brandCode) {
        SharedPreferenceBase.putPrefString(getApplicationContext(), Config.MY_BRAND_LIST, brandCode);
    }

    private void putBrandNameList(String brandName) {
        SharedPreferenceBase.putPrefString(getApplicationContext(), Config.MY_BRAND_NAME, brandName);
    }

    private String getBrandCodeList() {
        return SharedPreferenceBase.getPrefString(getApplicationContext(), Config.MY_BRAND_LIST, "");
    }

    private String getBrandNameList() {
        return SharedPreferenceBase.getPrefString(getApplicationContext(), Config.MY_BRAND_NAME, "");
    }

    private class GridAdapter extends ArrayAdapter<BrandRepo> {

        private ArrayList<BrandRepo> items;

        public GridAdapter(Context context, int textViewResourceId, ArrayList<BrandRepo> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_brand_list, null);

                holder.brandLayout = (RelativeLayout) convertView.findViewById(R.id.brand_layout);
                holder.brandName = (TextView) convertView.findViewById(R.id.brand_name);
                holder.imgCheck = (ImageView) convertView.findViewById(R.id.img_check);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            Collections.sort(items, brandComparator);
            final BrandRepo brandRepo = items.get(position);

            holder.brandName.setText(brandRepo.brandName);

            if (getBrandCodeList().contains(brandRepo.brandCode)) {
                brandRepo.setMyBrandYn(true);
            } else {
                brandRepo.setMyBrandYn(false);
            }

            if (brandRepo.isMyBrandYn()) {
                holder.imgCheck.setVisibility(View.VISIBLE);
            } else {
                holder.imgCheck.setVisibility(View.GONE);
            }

            final ViewHolder finalHolder = holder;
            holder.brandLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!brandRepo.isMyBrandYn()) {
                        finalHolder.imgCheck.setVisibility(View.VISIBLE);
                        if (getBrandCodeList().equals("")) {
                            putBrandCodeList(brandRepo.brandCode + ",");
                        } else {
                            putBrandCodeList(getBrandCodeList() + brandRepo.brandCode + ",");
                        }
                        if (getBrandNameList().equals("")) {
                            putBrandNameList(brandRepo.brandName + ", ");
                        } else {
                            putBrandNameList(getBrandNameList() + brandRepo.brandName + ", ");
                        }
                        brandRepo.setMyBrandYn(true);
                    } else {
                        String reset = getBrandCodeList().replace(brandRepo.brandCode + ",", "");
                        putBrandCodeList(reset);
                        finalHolder.imgCheck.setVisibility(View.GONE);
                        brandRepo.setMyBrandYn(false);
                        String reChoice = getBrandNameList().replaceAll(brandRepo.brandName + ", ", "");
                        putBrandNameList(reChoice);
                    }

                    Collections.sort(mBrandList, brandComparator);
                    mChoicedBrand.setText(getBrandNameList());
                }
            });

            return convertView;
        }

        private class ViewHolder {
            public RelativeLayout brandLayout;
            public TextView brandName;
            public ImageView imgCheck;
        }
    }
}
