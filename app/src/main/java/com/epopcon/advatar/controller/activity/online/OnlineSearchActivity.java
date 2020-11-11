package com.epopcon.advatar.controller.activity.online;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.model.OnlineStoreParser;
import com.epopcon.advatar.common.model.SearchData;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.repo.brand.BrandGoodsRepo;
import com.epopcon.advatar.controller.activity.common.BaseActivity;
import com.epopcon.advatar.controller.fragment.search.AuctionFragment;
import com.epopcon.advatar.controller.fragment.search.CoupangFragment;
import com.epopcon.advatar.controller.fragment.search.GMarketFragment;
import com.epopcon.advatar.controller.fragment.search.NaverFragment;
import com.epopcon.advatar.controller.fragment.search.TmonFragment;
import com.epopcon.advatar.controller.fragment.search.WemapFragment;
import com.epopcon.advatar.controller.fragment.search._11stFragment;
import com.epopcon.advatar.custom.customwidget.CustomViewPager;
import com.google.android.material.tabs.TabLayout;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnlineSearchActivity extends BaseActivity {

    private static final String TAG = OnlineSearchActivity.class.getSimpleName();

    private static final String KEY = "connection";
    private static final String INDEX = "startIndex";

    private CustomViewPager mViewPager = null;
    private int mCurrIndex = 0;
    private Button mBtnSearch;
    private EditText mEditSearch;

    private ArrayList<BrandGoodsRepo> m11stListData = new ArrayList<>();
    private ArrayList<BrandGoodsRepo> mAuctionListData = new ArrayList<>();
    private ArrayList<BrandGoodsRepo> mCoupangListData = new ArrayList<>();
    private ArrayList<BrandGoodsRepo> mWemapListData = new ArrayList<>();
    private ArrayList<BrandGoodsRepo> mTmonListData = new ArrayList<>();
    private ArrayList<BrandGoodsRepo> mGmarketListData = new ArrayList<>();
    private ArrayList<BrandGoodsRepo> mNaverListData = new ArrayList<>();

    private CoupangFragment coupangFragment;
    private AuctionFragment auctionFragment;
    private WemapFragment wemapFragment;
    private TmonFragment tmonFragment;
    private _11stFragment _11stFragment;
    private GMarketFragment gMarketFragment;
    private NaverFragment naverFragment;

    private URL url;
    private final String USER_AGENT = "Mozilla/5.0";

    private TabLayout mTabLayout;
    private CustomViewPagerAdapter mCustomAdapter;

    private SearchData mSearchData = SearchData.getInstance();

    public static void startActivity(Context context, int startTabIndex) {
        Intent intent = new Intent(context, OnlineSearchActivity.class);
        intent.putExtra(INDEX, startTabIndex);
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_search);

        int startTabIndex = getIntent().getIntExtra(INDEX, 0);
        mCurrIndex = startTabIndex;

        mBtnSearch = (Button) findViewById(R.id.btn_search);
        mEditSearch = (EditText) findViewById(R.id.edit_search);

        mTabLayout = (TabLayout) findViewById(R.id.tab);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.online_11st));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.online_gmarket));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.online_auction));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.online_naverstore));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.online_coupang));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.online_tmon));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.online_wemap));

        mViewPager = (CustomViewPager) findViewById(R.id.viewpager);
        mCustomAdapter = new CustomViewPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());

        mViewPager.setAdapter(mCustomAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrIndex = position;
                refresh();
                mViewPager.setPagingEnabled();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBtnSearch.setClickable(false);
                hideSoftKeyboard(mEditSearch);
                getDataForSearch();
            }
        });
    }

    private void getDataForSearch() {

        m11stListData.clear();
        mGmarketListData.clear();
        mAuctionListData.clear();
        mNaverListData.clear();
        mCoupangListData.clear();
        mTmonListData.clear();
        mWemapListData.clear();

        mSearchData.clear();

        new Thread() {
            public void run() {
                try {
                    OnlineStoreParser.getDataFor11st(mEditSearch.getText().toString(), new RequestListener() {
                        @Override
                        public void onRequestSuccess(int requestCode, Object result) {
                            m11stListData.addAll((List<BrandGoodsRepo>) result);
                            mSearchData.set_11stData(m11stListData);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            refresh();
                                        }
                                    });
                                }
                            }).start();
                        }

                        @Override
                        public void onRequestFailure(Throwable t) {

                        }
                    });
                    OnlineStoreParser.getDataForGmarket(mEditSearch.getText().toString(), new RequestListener() {
                        @Override
                        public void onRequestSuccess(int requestCode, Object result) {
                            mGmarketListData.addAll((List<BrandGoodsRepo>) result);
                            mSearchData.setGmarketData(mGmarketListData);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            refresh();
                                        }
                                    });
                                }
                            }).start();
                        }

                        @Override
                        public void onRequestFailure(Throwable t) {

                        }
                    });
//                    mAuctionListData.addAll(OnlineStoreParser.getDataForAuction(mEditSearch.getText().toString()));
                    OnlineStoreParser.getDataForNaver(mEditSearch.getText().toString(), new RequestListener() {
                        @Override
                        public void onRequestSuccess(int requestCode, Object result) {
                            mNaverListData.addAll((List<BrandGoodsRepo>) result);
                            mSearchData.setNaverData(mNaverListData);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            refresh();
                                        }
                                    });
                                }
                            }).start();
                        }

                        @Override
                        public void onRequestFailure(Throwable t) {

                        }
                    });
                    OnlineStoreParser.getDataForCoupang(mEditSearch.getText().toString(), new RequestListener() {
                        @Override
                        public void onRequestSuccess(int requestCode, Object result) {
                            mCoupangListData.addAll((List<BrandGoodsRepo>) result);
                            mSearchData.setCoupangData(mCoupangListData);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            refresh();
                                        }
                                    });
                                }
                            }).start();
                        }

                        @Override
                        public void onRequestFailure(Throwable t) {

                        }
                    });
                    OnlineStoreParser.getDataForTmon(mEditSearch.getText().toString(), new RequestListener() {
                        @Override
                        public void onRequestSuccess(int requestCode, Object result) {
                            mTmonListData.addAll((List<BrandGoodsRepo>) result);
                            mSearchData.setTmonData(mTmonListData);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            refresh();
                                        }
                                    });
                                }
                            }).start();
                        }

                        @Override
                        public void onRequestFailure(Throwable t) {

                        }
                    });
                    OnlineStoreParser.getDataForWemap(mEditSearch.getText().toString(), new RequestListener() {
                        @Override
                        public void onRequestSuccess(int requestCode, Object result) {
                            mWemapListData.addAll((List<BrandGoodsRepo>) result);
                            mSearchData.setWemapData(mWemapListData);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            refresh();
                                        }
                                    });
                                }
                            }).start();
                        }

                        @Override
                        public void onRequestFailure(Throwable t) {

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public interface onFragmentSelectedListener {
        void onFragmentSelected();
    }

    public void refresh() {
        mCustomAdapter.notifyDataSetChanged();
        mBtnSearch.setClickable(true);

        if (mCurrIndex == 0) {
            _11stFragment.refresh(mImageLoaderOptions);
        } else if (mCurrIndex == 1) {
            gMarketFragment.refresh(mImageLoaderOptions);
        } else if (mCurrIndex == 2) {
            auctionFragment.refresh(mImageLoaderOptions);
        } else if (mCurrIndex == 3) {
            naverFragment.refresh(mImageLoaderOptions);
        } else if (mCurrIndex == 4) {
            coupangFragment.refresh(mImageLoaderOptions);
        } else if (mCurrIndex == 5) {
            tmonFragment.refresh(mImageLoaderOptions);
        } else if (mCurrIndex == 6) {
            wemapFragment.refresh(mImageLoaderOptions);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class CustomViewPagerAdapter extends FragmentStatePagerAdapter {
        private int mPageCount;

        public CustomViewPagerAdapter(FragmentManager fm, int mPageCount) {
            super(fm);
            this.mPageCount = mPageCount;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    _11stFragment = new _11stFragment();
                    return _11stFragment;
                case 1:
                    gMarketFragment = new GMarketFragment();
                    return gMarketFragment;

                case 2:
                    auctionFragment = new AuctionFragment();
                    return auctionFragment;

                case 3:
                    naverFragment = new NaverFragment();
                    return naverFragment;

                case 4:
                    coupangFragment = new CoupangFragment();
                    return coupangFragment;

                case 5:
                    tmonFragment = new TmonFragment();
                    return tmonFragment;

                case 6:
                    wemapFragment = new WemapFragment();
                    return wemapFragment;

                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            return mPageCount;
        }
    }

    protected void hideSoftKeyboard(View view) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
