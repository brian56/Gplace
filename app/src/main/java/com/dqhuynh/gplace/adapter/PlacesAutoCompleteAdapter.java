package com.dqhuynh.gplace.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.dqhuynh.gplace.R;
import com.dqhuynh.gplace.api.PlaceAutoCompleteRequest;
import com.dqhuynh.gplace.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 6/23/2015.
 */
public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private static final String TAG = PlacesAutoCompleteAdapter.class.getSimpleName();
    public ArrayList<String> resultList;
    private LayoutInflater mInflater;
    Context mContext;
    public int mResource;

    public PlaceAutoCompleteRequest mPlaceAPI = new PlaceAutoCompleteRequest();

    public PlacesAutoCompleteAdapter(Context context, int resource) {
        super(context, resource);

        mContext = context;
        mResource = resource;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // Last item will be the footer
        return resultList.size();
    }

    @Override
    public String getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*final PlaceAutoCompleteHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_item_place_autocomplete, null);
            holder = new PlaceAutoCompleteHolder();
            holder.imvGoogleLogo = (ImageView) convertView.findViewById(R.id.imvGoogleLogo);
            holder.tvAddress = (TextView) convertView.findViewById(R.id.autocompleteText);

            String address = resultList.get(position);
            if(address!=null) {
                if (address.equals("footer")) {
                    convertView.setEnabled(false);
                    convertView.setOnClickListener(null);
                }
            }
            convertView.setTag(holder);

        } else {
            holder = (PlaceAutoCompleteHolder) convertView.getTag();
        }
        String address = resultList.get(position);
        if(address!=null) {
            if (address.equals("footer")) {
                convertView.setEnabled(false);
                convertView.setOnClickListener(null);
                holder.tvAddress.setVisibility(View.GONE);
                holder.imvGoogleLogo.setVisibility(View.VISIBLE);

            } else {
                holder.imvGoogleLogo.setVisibility(View.GONE);
                holder.tvAddress.setVisibility(View.VISIBLE);
                holder.tvAddress.setText(address);
            }
        }
        return convertView;*/
        View view;
        view = mInflater.inflate(R.layout.row_item_autocomplete, null);
        ImageView imvGoogleLogo = (ImageView) view.findViewById(R.id.imvGoogleLogo);
        TextView tvAddress = (TextView) view.findViewById(R.id.autocompleteText);
        String address = "";
        try {
            address = resultList.get(position);
        } catch (IndexOutOfBoundsException e) {
            LogUtil.log(TAG, "IndexOutOfBoundsException");
        } catch (NullPointerException e) {
            LogUtil.log(TAG, "NullPointerException");
        }
        if (!address.equals("")) {
            if (address.equals("footer")) {
                view.setEnabled(false);
                view.setOnClickListener(null);
                tvAddress.setVisibility(View.GONE);
                imvGoogleLogo.setVisibility(View.VISIBLE);

            } else {
                imvGoogleLogo.setVisibility(View.GONE);
                tvAddress.setVisibility(View.VISIBLE);
                tvAddress.setText(address);
            }
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    resultList = mPlaceAPI.autoComplete(constraint.toString());

                    if (resultList != null) {
                        // Footer
                        if (resultList.size() > 0)
                            resultList.add("footer");

                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }

    static class PlaceAutoCompleteHolder {
        TextView tvAddress;
        ImageView imvGoogleLogo;
    }
}
