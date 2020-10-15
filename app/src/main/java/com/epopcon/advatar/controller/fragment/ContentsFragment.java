package com.epopcon.advatar.controller.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.repo.brand.BrandContentsRepo;
import com.epopcon.advatar.common.network.model.repo.brand.BrandGoodsRepo;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.common.util.SharedPreferenceBase;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.epopcon.advatar.common.util.MyBrandUtil.getBrandNameList;

public class ContentsFragment extends BaseFragment {

    private View mView = null;
    private static ContentsFragment instance = null;

    public static ContentsFragment getInstance() {
        if (instance == null) {
            instance = new ContentsFragment();
        }
        return instance;
    }

    private ListView mListView;
    private ListAdapter mListAdapter = null;
    private List<BrandContentsRepo> mContentsList = new ArrayList<>();

    private String YOUTUBE_API_KEY = "AIzaSyCDe-64pnK38qKnbuCHqCAIAwdJ4vwExyg";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_contents, container, false);

        mListView = mView.findViewById(R.id.list_view);

        mListAdapter = new ListAdapter(getActivity().getApplicationContext(), R.layout.item_contents_list, mContentsList);
        mListView.setAdapter(mListAdapter);

        return mView;
    }

    public void refresh() {

        if (mContentsList == null || mContentsList.isEmpty()) {
            String[] brands = SharedPreferenceBase.getPrefString(getContext(), Config.MY_BRAND_LIST, "").split(",");

            List<String> brandCodes = new ArrayList<>();
            for (int i = 0; i < brands.length; i++) {
                brandCodes.add(brands[i]);
            }
            try {
                RestAdvatarProtocol.getInstance().getBrandContentsList(brandCodes, new RequestListener() {
                    @Override
                    public void onRequestSuccess(int requestCode, Object result) {
                        mContentsList.addAll((List<BrandContentsRepo>) result);
                        mListAdapter.notifyDataSetChanged();
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

    private class YoutubeAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
                final JsonFactory JSON_FACTORY = new JacksonFactory();
                final long NUMBER_OF_VIDEOS_RETURNED = 3;

                YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                    public void initialize(HttpRequest request) throws IOException {
                    }
                }).setApplicationName("youtube-search-sample").build();

                YouTube.Search.List search = youtube.search().list("id,snippet");

                search.setKey(YOUTUBE_API_KEY);

                String[] brandList = getBrandNameList().split(",");
                String searchKeyword = "";
                for (int i = 0 ; i < brandList.length ; i++) {
                    if (brandList[i].equals(" ")) {
                        continue;
                    }
                    searchKeyword = brandList[i];

                    search.setQ(searchKeyword);
                    search.setType("video");

                    search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
                    SearchListResponse searchResponse = search.execute();

                    List<SearchResult> searchResultList = searchResponse.getItems();

                    if (searchResultList != null) {
                        prettyPrint(searchResultList.iterator());
                    }

                }
            } catch (GoogleJsonResponseException e) {
                System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                        + e.getDetails().getMessage());
                System.err.println("There was a service error 2: " + e.getLocalizedMessage() + " , " + e.toString());
            } catch (IOException e) {
                System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
            } catch (Throwable t) {
                t.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mListAdapter.notifyDataSetChanged();
        }

        public void prettyPrint(Iterator<SearchResult> iteratorSearchResults) {
            if (!iteratorSearchResults.hasNext()) {
                System.out.println(" There aren't any results for your query.");
            }

            StringBuilder sb = new StringBuilder();

            while (iteratorSearchResults.hasNext()) {
                SearchResult singleVideo = iteratorSearchResults.next();
                ResourceId rId = singleVideo.getId();

                if (rId.getKind().equals("youtube#video")) {
                    String youtube = "https://youtube.com/watch?v=";
                    Thumbnail thumbnail = (Thumbnail) singleVideo.getSnippet().getThumbnails().get("default");
                    BrandContentsRepo brandContentsRepo = new BrandContentsRepo();
                    brandContentsRepo.contentsImg = thumbnail.getUrl();
                    brandContentsRepo.contentsText = singleVideo.getSnippet().getTitle();
                    brandContentsRepo.contentsUrl = youtube + singleVideo.getId().getVideoId();

                    mContentsList.add(brandContentsRepo);
                    Log.d("ContentsFragment", "title : " + brandContentsRepo.contentsText + ", channelId : " + singleVideo.getSnippet().getChannelId());
                    sb.append("ID : " + singleVideo.getSnippet().getChannelId());
                    sb.append("\n");
                }
            }

        }
    }

    private class ListAdapter extends ArrayAdapter<BrandContentsRepo> {

        private List<BrandContentsRepo> items;

        ListAdapter(Context context, int textViewResourceId, List<BrandContentsRepo> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_contents_list, null);

                holder.contentsLayout = (RelativeLayout) convertView.findViewById(R.id.item_layout);
                holder.contentsImg = (ImageView) convertView.findViewById(R.id.img_contents);
                holder.adsImg = (ImageView) convertView.findViewById(R.id.img_ads);
                holder.brandName = (TextView) convertView.findViewById(R.id.brand_name);
                holder.contentsTitle = (TextView) convertView.findViewById(R.id.contents_title);
                holder.contentsTxt = (TextView) convertView.findViewById(R.id.contents_text);
                holder.line = (View) convertView.findViewById(R.id.view);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final BrandContentsRepo brandContentsRepo = items.get(position);

            if (brandContentsRepo.adYn == 0) {
                holder.adsImg.setVisibility(View.VISIBLE);
            } else {
                holder.adsImg.setVisibility(View.GONE);
            }
            ImageLoader.getInstance().displayImage(brandContentsRepo.contentsImg, holder.contentsImg, mImageLoaderOptions);
            holder.brandName.setText(brandContentsRepo.brandName);

            holder.contentsTitle.setText(brandContentsRepo.contentsTitle);
            holder.contentsTxt.setText(brandContentsRepo.contentsText);

            holder.contentsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (brandContentsRepo.contentsUrl != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(brandContentsRepo.contentsUrl));
                        startActivity(intent);
                    }
                }
            });

            if (position == items.size() - 1) {
                holder.line.setVisibility(View.GONE);
            } else {
                holder.line.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        private class ViewHolder {
            public RelativeLayout contentsLayout;
            public ImageView contentsImg;
            public ImageView adsImg;
            public TextView brandName;
            public TextView contentsTitle;
            public TextView contentsTxt;
            public View line;
        }
    }
}
