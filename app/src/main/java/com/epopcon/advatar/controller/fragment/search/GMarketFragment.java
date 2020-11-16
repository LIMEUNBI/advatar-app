package com.epopcon.advatar.controller.fragment.search;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.model.SearchData;
import com.epopcon.advatar.common.network.model.repo.brand.BrandGoodsRepo;
import com.epopcon.advatar.controller.activity.online.OnlineSearchActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GMarketFragment extends Fragment implements OnlineSearchActivity.onFragmentSelectedListener {
    private static final String TAG = GMarketFragment.class.getSimpleName();

    private View mView = null;

    private ListView mListView;

    private ListAdapter mListAdapter = null;
    private ArrayList<BrandGoodsRepo> mListData = new ArrayList<>();
    private RelativeLayout mEmptyLayout;

    DecimalFormat decimalFormat = new DecimalFormat("#,##0");
    private DisplayImageOptions mImageLoaderOptions;

    private SearchData searchData = SearchData.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_11st, container, false);

        mListView = (ListView) mView.findViewById(R.id.list_view);
        mEmptyLayout = (RelativeLayout) mView.findViewById(R.id.empty_layout);
        mListAdapter = new ListAdapter(getContext(), R.layout.item_product_list, mListData);
        mListView.setAdapter(mListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BrandGoodsRepo productInfo = mListData.get(i);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(productInfo.url));
                startActivity(intent);
            }
        });

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh(mImageLoaderOptions);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onFragmentSelected() {
        refresh(mImageLoaderOptions);
    }

    public void refresh(DisplayImageOptions imageLoaderOptions) {

        mListData = searchData.getGmarketData();

        if (mListData.size() == 0) {
            mEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            mEmptyLayout.setVisibility(View.GONE);
        }

        mListAdapter.change();
        mImageLoaderOptions = imageLoaderOptions;
    }

    public class ListAdapter extends ArrayAdapter<BrandGoodsRepo> {
        private List<BrandGoodsRepo> items;
        private Context context;

        public ListAdapter(Context context, int textViewResourceId, ArrayList<BrandGoodsRepo> items) {
            super(context, textViewResourceId, items);
            this.items = items;
            this.context = context;
        }

        @Override
        public BrandGoodsRepo getItem(int position) {
            items.get(position);
            return super.getItem(position);
        }

        public void change() {
            this.items.clear();
            this.items.addAll(mListData);
            this.notifyDataSetChanged();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_product_list, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final BrandGoodsRepo productInfo = items.get(position);

            ImageLoader.getInstance().displayImage(productInfo.goodsImg, holder.productImg, mImageLoaderOptions);
            holder.productName.setText(productInfo.goodsName);
            holder.deliveryInfo.setText(productInfo.deliveryInfo);
            holder.productPrice.setText(decimalFormat.format(productInfo.goodsPrice) + "원");

            return convertView;
        }

        public class ViewHolder {
            ImageView productImg;
            TextView productName;
            TextView productPrice;
            TextView deliveryInfo;
            RelativeLayout layout;

            public ViewHolder(View itemView) {
                productImg = (ImageView) itemView.findViewById(R.id.img_product);
                productName = (TextView) itemView.findViewById(R.id.product_name);
                productPrice = (TextView) itemView.findViewById(R.id.product_price);
                deliveryInfo = (TextView) itemView.findViewById(R.id.delivery_info);
                layout = (RelativeLayout) itemView.findViewById(R.id.list_holder);
            }
        }
    }
}
