package com.dqhuynh.gplace.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dqhuynh.gplace.R;
import com.dqhuynh.gplace.adapter.SearchResultAdapter;
import com.dqhuynh.gplace.api.PlaceNearBySearchRequest;
import com.dqhuynh.gplace.callback.OnLoadMoreListener;
import com.dqhuynh.gplace.common.CommonData;
import com.dqhuynh.gplace.model.Place;
import com.dqhuynh.gplace.model.SearchOptions;
import com.dqhuynh.gplace.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 7/13/2015.
 */
public class SearchResultFragment extends Fragment {
    private RecyclerView mRvResults;
    private ArrayList<Place> listResults = new ArrayList<>();
    private SearchResultAdapter mAdapter;

    public SearchResultFragment() {

    }

    public static SearchResultFragment newInstance() {
        SearchResultFragment fragment = new SearchResultFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);
        mRvResults = (RecyclerView) view.findViewById(R.id.rvResults);
        setupResultRecycleView();
        SearchTask task = new SearchTask();
        task.execute(CommonData.currentSearchOption);
        Toast.makeText(getActivity(),"create view",Toast.LENGTH_SHORT).show();
        return view;
    }

    private void setupResultRecycleView() {
        mRvResults.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRvResults.setLayoutManager(llm);
        mAdapter = new SearchResultAdapter(listResults, mRvResults, getActivity());
        mRvResults.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                SearchTask task = new SearchTask();
                task.execute(CommonData.currentSearchOption);
            }
        });
    }
    private class SearchTask extends AsyncTask<SearchOptions, Integer, ArrayList<Place>> {
        @Override
        protected void onPreExecute() {
            if(CommonData.currentSearchOption.canLoadMore() && listResults.size()>0) {
                listResults.add(null);
                mAdapter.notifyItemInserted(listResults.size() - 1);
            }
        }

        @Override
        protected ArrayList<Place> doInBackground(SearchOptions... searchOptions) {
            PlaceNearBySearchRequest request = new PlaceNearBySearchRequest();
            return request.placeNearBy(searchOptions[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Place> result) {
            if(listResults.size()>1 && listResults.get(listResults.size()-1)==null) {
                listResults.remove(listResults.size() - 1);
                mAdapter.notifyItemRemoved(listResults.size());
            }
            if (result != null) {
                for (Place p : result) {
                    listResults.add(p);
                }
                LogUtil.log("Search result", listResults.toString());
                mAdapter.notifyDataSetChanged();
                mAdapter.setLoaded();
            }
        }
    }
}
