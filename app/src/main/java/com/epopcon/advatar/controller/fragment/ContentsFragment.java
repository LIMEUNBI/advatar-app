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
import com.epopcon.advatar.common.network.model.repo.ContentsRepo;
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
    private List<ContentsRepo> mContentsList = new ArrayList<>();

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

        mContentsList.clear();

        ContentsRepo contentsRepo;

        contentsRepo = new ContentsRepo();
        contentsRepo.contentsNum = 1;
        contentsRepo.contentsImg = "https://images-kr.amorepacificmall.com/fileupload/plandisplay/2020/07/22/PC_HM_BANNER_mamonde_2007_3w_1.jpg";
        contentsRepo.contentsText = "마몽드 X O!Oi COMING SOON!\n2020.07.22~2020.07.26";
        mContentsList.add(contentsRepo);

        contentsRepo = new ContentsRepo();
        contentsRepo.contentsNum = 2;
        contentsRepo.contentsImg = "https://images-kr.amorepacificmall.com/fileupload/plandisplay/2020/07/17/PC_HM_BANNER_3925_2007_3w111.jpg";
        contentsRepo.contentsText = "이니스프리! 여름피부, 모공 완전 정복! 최대 60%\n2020.07.20~2020.07.31";
        mContentsList.add(contentsRepo);

        contentsRepo = new ContentsRepo();
        contentsRepo.contentsNum = 3;
        contentsRepo.contentsImg = "https://images-kr.amorepacificmall.com/fileupload/plandisplay/2020/07/17/PC_HM_BANNER_soon_2007_3w.jpg";
        contentsRepo.contentsText = "[순플러스]브랜드 위크(7월)\n2020.07.20~2020.07.26";
        mContentsList.add(contentsRepo);

        contentsRepo = new ContentsRepo();
        contentsRepo.contentsNum = 4;
        contentsRepo.contentsImg = "https://images-kr.amorepacificmall.com/fileupload/plandisplay/2020/07/17/PC_HM_BANNER_3919_2007_3w.jpg";
        contentsRepo.contentsText = "아이오페 더 비타민 C23%\n2020.07.20~2020.07.26";
        mContentsList.add(contentsRepo);

        contentsRepo = new ContentsRepo();
        contentsRepo.contentsNum = 5;
        contentsRepo.contentsImg = "https://images-kr.amorepacificmall.com/fileupload/plandisplay/2020/07/17/PC_HM_BANNER_3909_2007_3w_1594971807642.jpg";
        contentsRepo.contentsText = "착한 클렌징, B.LAB 신규 입점 기념 ~10%\n2020.07.20~2020.08.02";
        mContentsList.add(contentsRepo);

        contentsRepo = new ContentsRepo();
        contentsRepo.contentsNum = 6;
        contentsRepo.contentsImg = "https://images-kr.amorepacificmall.com/fileupload/plandisplay/2020/07/14/PC_HM_BANNER_3879_2007_2w.jpg";
        contentsRepo.contentsText = "헤라 시그니아 7월 페이백 이벤트\n2020.07.15~2020.07.31";
        mContentsList.add(contentsRepo);

        mListAdapter.notifyDataSetChanged();

//        YoutubeAsyncTask task = new YoutubeAsyncTask();
//        task.execute();

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
                    ContentsRepo contentsRepo = new ContentsRepo();
                    contentsRepo.contentsImg = thumbnail.getUrl();
                    contentsRepo.contentsText = singleVideo.getSnippet().getTitle();
                    contentsRepo.contentsUrl = youtube + singleVideo.getId().getVideoId();

                    mContentsList.add(contentsRepo);
                    Log.d("ContentsFragment", "title : " + contentsRepo.contentsText + ", channelId : " + singleVideo.getSnippet().getChannelId());
                    sb.append("ID : " + singleVideo.getSnippet().getChannelId());
                    sb.append("\n");
                }
            }

        }
    }

    private class ListAdapter extends ArrayAdapter<ContentsRepo> {

        private List<ContentsRepo> items;

        ListAdapter(Context context, int textViewResourceId, List<ContentsRepo> items) {
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
                holder.contentsTxt = (TextView) convertView.findViewById(R.id.contents_text);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ContentsRepo contentsRepo = items.get(position);

//            holder.contentsLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contentsRepo.contentsUrl));
//                    startActivity(intent);
//                }
//            });

            ImageLoader.getInstance().displayImage(contentsRepo.contentsImg, holder.contentsImg, mImageLoaderOptions);
            holder.contentsTxt.setText(contentsRepo.contentsText);

            return convertView;
        }

        private class ViewHolder {
            public RelativeLayout contentsLayout;
            public ImageView contentsImg;
            public TextView contentsTxt;
        }
    }
}
