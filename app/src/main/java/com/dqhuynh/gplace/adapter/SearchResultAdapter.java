package com.dqhuynh.gplace.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dqhuynh.gplace.R;
import com.dqhuynh.gplace.callback.OnLoadMoreListener;
import com.dqhuynh.gplace.common.CommonData;
import com.dqhuynh.gplace.model.Place;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

/**
 * Created by Administrator on 7/13/2015.
 */
public class SearchResultAdapter extends RecyclerView.Adapter {
    private ArrayList<Place> placeList;
    private Context mContext;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private DisplayImageOptions options;
    ImageLoader imageLoader;

    public SearchResultAdapter(ArrayList<Place> places, RecyclerView recyclerView,
                               Context context) {
        this.placeList = places;
this.mContext = context;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_arrow_drop_down_white_24dp)
                .showImageOnFail(R.drawable.ic_cancel_white_24dp)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(3)).build();
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (CommonData.currentSearchOption.canLoadMore() && !loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    // Create new views
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_item_place, null);

            // create ViewHolder

            vh = new PlaceHolder(itemLayoutView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progress_bar_load_more, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }
    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return placeList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        if (imageLoader == null)
//            imageLoader = AppController.getInstance().getImageLoader(mContext);
        if (holder instanceof PlaceHolder) {

            Place singlePlace = (Place) placeList.get(position);

            ((PlaceHolder) holder).tvName.setText(singlePlace.getName());
//            ((PlaceHolder) holder).ivPhoto.setImageUrl(singlePlace.getIcon(), imageLoader);
            ImageLoader.getInstance().displayImage(singlePlace.getIcon(), ((PlaceHolder) holder).ivPhoto, options);

            ((PlaceHolder) holder).tvAddress.setText(singlePlace.getFormatted_address());

            ((PlaceHolder) holder).singePlace= singlePlace;

        } else {

        }
    }

    // Return the size arraylist
    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public static class PlaceHolder extends RecyclerView.ViewHolder {

//        public NetworkImageView ivPhoto;
        public ImageView ivPhoto;
        public TextView tvName;
        public TextView tvAddress;

        public Place singePlace;

        public PlaceHolder(View itemLayoutView) {
            super(itemLayoutView);
//            ivPhoto = (NetworkImageView) itemLayoutView.findViewById(R.id.place_photo);
            ivPhoto = (ImageView) itemLayoutView.findViewById(R.id.place_photo2);
            tvName = (TextView) itemLayoutView.findViewById(R.id.place_name);

            tvAddress = (TextView) itemLayoutView.findViewById(R.id.place_address);
            // Onclick event for the row to show the data in toast
            itemLayoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(
                            v.getContext(),
                            "Data : \n" + singePlace.getName() + " \n"
                                    + singePlace.getFormatted_address(),
                            Toast.LENGTH_SHORT).show();

                }
            });

        }

    }
    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressView progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressView) v.findViewById(R.id.progressLoadMore);
        }
    }
}