package com.epopcon.advatar.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.epopcon.advatar.R;
import com.epopcon.advatar.common.network.model.repo.ReviewRepo;

import java.util.ArrayList;
import java.util.List;

public class ReviewFragment extends BaseFragment {

    private static ReviewFragment instance = null;

    public static ReviewFragment getInstance() {
        if (instance == null) {
            instance = new ReviewFragment();
        }
        return instance;
    }

    private View mView = null;

    private ListView mListView;
    private ListAdapter mListAdapter = null;
    private List<ReviewRepo> mContentsList = new ArrayList<>();

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

        mView = inflater.inflate(R.layout.fragment_review, container, false);

        mListView = mView.findViewById(R.id.list_view);

        mListAdapter = new ListAdapter(getActivity().getApplicationContext(), R.layout.item_contents_list, mContentsList);
        mListView.setAdapter(mListAdapter);

        return mView;
    }

    public void refresh() {

        mContentsList.clear();

        mListAdapter.notifyDataSetChanged();
    }

    private class ListAdapter extends ArrayAdapter {

        private Context context;
        private List<ReviewRepo> items;
        private LayoutInflater inflater;

        ListAdapter(Context context, int textViewResourceId, List<ReviewRepo> items) {
            super(context, textViewResourceId, items);
            this.context = context;
            this.items = items;
            this.inflater = LayoutInflater.from(getContext());
        }
    }
}
