package com.epopcon.advatar.controller.activity.brand;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.repo.brand.BrandRepo;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.controller.activity.BaseActivity;
import com.epopcon.advatar.controller.activity.MainActivity;
import com.epopcon.advatar.common.util.SharedPreferenceBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.epopcon.advatar.common.util.MyBrandUtil.getBrandCodeList;
import static com.epopcon.advatar.common.util.MyBrandUtil.getBrandNameList;
import static com.epopcon.advatar.common.util.MyBrandUtil.putBrandCodeList;
import static com.epopcon.advatar.common.util.MyBrandUtil.putBrandNameList;

public class BrandChoiceActivity extends BaseActivity {

    ArrayList<BrandRepo> mBrandList = null;
    ArrayList<BrandRepo> mBrandListCopy = null;

    private EditText mEditSearch = null;
    private TextView mChoiceBrand = null;

    private GridView mGridView = null;
    private GridAdapter mAdapter = null;
    private Button mBtnChoice = null;
    private ImageView mImgLoading;

    private Intent intent = null;

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

        intent = getIntent();

        mBrandList = new ArrayList<>();
        mBrandListCopy = new ArrayList<>();

        mImgLoading = (ImageView) findViewById(R.id.img_loading);
        mGridView = (GridView) findViewById(R.id.card_grid_view);
        mBtnChoice = (Button) findViewById(R.id.btn_end);

        Glide.with(this).asGif().load(R.raw.loading).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(mImgLoading);
        mGridView.setVisibility(View.GONE);

        mChoiceBrand = (TextView) findViewById(R.id.choice_brand);
        mChoiceBrand.setText(getBrandNameList());

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

        mBtnChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0 ; i < mBrandList.size() ; i++) {
                    if (mBrandList.get(i).isMyBrandYn()) {
                        if (!getBrandCodeList().contains(mBrandList.get(i).brandCode)) {
                            putBrandCodeList(getBrandCodeList() + mBrandList.get(i).brandCode + ",");
                        }
                        if (!getBrandNameList().contains(mBrandList.get(i).brandName)) {
                            putBrandNameList(getBrandNameList() + mBrandList.get(i).brandName + ",");
                        }
                    }
                }
                if (getBrandCodeList().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "브랜드를 선택하세요.", Toast.LENGTH_LONG).show();
                } else {

                    try {
                        RestAdvatarProtocol.getInstance().userFavoriteBrands(SharedPreferenceBase.getPrefString(getApplicationContext(), Config.USER_ID, ""), getBrandCodeList(), new RequestListener() {
                            @Override
                            public void onRequestSuccess(int requestCode, Object result) {
                            }

                            @Override
                            public void onRequestFailure(Throwable t) {
                                Toast.makeText(getApplicationContext(), "통신 오류입니다. 버튼을 다시 눌러주세요.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!intent.getBooleanExtra("finish", true)) {
                        Intent mainIntent = new Intent(BrandChoiceActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                    }
                    finish();
                }
            }
        });

        mAdapter = new GridAdapter(getApplicationContext(), R.layout.item_brand_list, mBrandList);
        mGridView.setAdapter(mAdapter);
    }

    private void search(String searchKeyword) {

        if (mBrandListCopy.size() == 0) {
            mBrandListCopy.addAll(mBrandList);
        }
        mBrandList.clear();

        if (searchKeyword.length() == 0) {
            mBrandList.addAll(mBrandListCopy);
        } else {
            for (int i = 0 ; i < mBrandListCopy.size() ; i++) {
                if (mBrandListCopy.get(i).brandName.toLowerCase().contains(searchKeyword)) {
                    mBrandList.add(mBrandListCopy.get(i));
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

    private void refresh() {

        if (mBrandList == null || mBrandList.isEmpty()) {
            try {
                RestAdvatarProtocol.getInstance().getBrandList(30, new RequestListener() {
                    @Override
                    public void onRequestSuccess(int requestCode, Object result) {
                        mImgLoading.setVisibility(View.GONE);
                        mGridView.setVisibility(View.VISIBLE);
                        mBrandList.addAll((List<BrandRepo>) result);

                        for (int i = 0; i < mBrandList.size(); i++) {
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
    }

    private final static Comparator<BrandRepo> brandComparator = new Comparator<BrandRepo>() {
        @Override
        public int compare(BrandRepo brandRepo1, BrandRepo brandRepo2) {
            if (brandRepo1.isMyBrandYn() && brandRepo2.isMyBrandYn()) {
                return 0;
            } else if (brandRepo1.isMyBrandYn() && !brandRepo2.isMyBrandYn()) {
                return -1;
            } else if (!brandRepo1.isMyBrandYn() && brandRepo2.isMyBrandYn()) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getBrandCodeList().isEmpty()) {
                Toast.makeText(getApplicationContext(), "브랜드를 선택하세요.", Toast.LENGTH_LONG).show();
                return false;
            } else {
                finish();
            }
        }

        return super.onKeyDown(keyCode, event);
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
                            putBrandNameList(brandRepo.brandName + ",");
                        } else {
                            putBrandNameList(getBrandNameList() + brandRepo.brandName + ",");
                        }
                        brandRepo.setMyBrandYn(true);
                    } else {
                        String reset = getBrandCodeList().replace(brandRepo.brandCode + ",", "");
                        putBrandCodeList(reset);
                        String reChoice = getBrandNameList().replaceAll(brandRepo.brandName + ",", "");
                        putBrandNameList(reChoice);
                        finalHolder.imgCheck.setVisibility(View.GONE);
                        brandRepo.setMyBrandYn(false);
                    }

                    mChoiceBrand.setText(getBrandNameList());
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
