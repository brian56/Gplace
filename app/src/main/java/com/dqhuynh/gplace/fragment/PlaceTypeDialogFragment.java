package com.dqhuynh.gplace.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.dqhuynh.gplace.R;
import com.dqhuynh.gplace.model.PlaceType;
import com.dqhuynh.gplace.utils.LogUtil;
import com.hb.views.PinnedSectionListView;
import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 6/29/2015.
 */
public class PlaceTypeDialogFragment extends android.support.v4.app.DialogFragment implements View.OnClickListener {
    private static final String TAG = PlaceTypeDialogFragment.class.getSimpleName();
    private static final int DIALOG_RESULT_CODE = 1;
    private PinnedSectionListView mPinnedListView;
    private String[] arrPlaceTypeCode;
    private String[] arrPlaceTypeName;
    private TypedArray arrPlaceTypeIcon;
    //    private ArrayList<PlaceType> arrPlaceType;
    private static ArrayList<PlaceType> arrPlaceType;
    private Button mBtnOk;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_select_place_type, null, false);
        mPinnedListView = (PinnedSectionListView) view.findViewById(R.id.listPlaceType);
        mBtnOk = (Button) view.findViewById(R.id.btnOK);
        mBtnOk.setOnClickListener(this);
        setupListView();
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setupListView() {
        arrPlaceTypeCode = getActivity().getResources().getStringArray(R.array.place_type_code);
        arrPlaceTypeName = getActivity().getResources().getStringArray(R.array.place_type_name);
        arrPlaceTypeIcon = getActivity().getResources().obtainTypedArray(R.array.place_type_icon);
        int total = arrPlaceTypeCode.length < arrPlaceTypeName.length ? arrPlaceTypeCode.length : arrPlaceTypeName.length;
        arrPlaceType = new ArrayList<PlaceType>();
        int sectionPosition = 0;
        int id = -1;
        for (int i = 0; i < total; i++) {
            //get the ID of the drawable
            try {
                id = arrPlaceTypeIcon.getResourceId(i, -1);
                LogUtil.log(TAG, id + "");
            } catch (ArrayIndexOutOfBoundsException e) {
                LogUtil.log(TAG,"ArrayIndexOutOfBoundsException");
            }

            boolean isSection = arrPlaceTypeCode[i].startsWith("section");
            if (isSection && i != 0)
                sectionPosition++;
            PlaceType place = new PlaceType(arrPlaceTypeCode[i], arrPlaceTypeName[i], isSection, sectionPosition, i, false, isSection ? PlaceType.SECTION : PlaceType.ITEM);
            place.setIconId(id);
            if (SearchFragment.selectedPlaceTypes.size() > 0) {
                for (PlaceType p : SearchFragment.selectedPlaceTypes) {
                    if (place.getCode().equals(p.getCode())) {
                        place.setChecked(true);
                        break;
                    }
                }
            }
            arrPlaceType.add(place);
        }
        arrPlaceTypeIcon.recycle();
        for (PlaceType pl : arrPlaceType) {
            LogUtil.log(TAG, pl.toString());
        }

        mPinnedListView.setFastScrollEnabled(false);
        mPinnedListView.setShadowVisible(false);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            mPinnedListView.setFastScrollAlwaysVisible(true);
//        }
        FastScrollAdapter adapter = new FastScrollAdapter(getActivity(), R.layout
                .row_item_select_place_type);

        mPinnedListView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOK:
                SearchFragment.selectedPlaceTypes.clear();
                for (int i = 0; i < mPinnedListView.getAdapter().getCount(); i++) {
                    PlaceType p = (PlaceType) mPinnedListView.getAdapter().getItem(i);
                    if (p.isChecked())
                        SearchFragment.selectedPlaceTypes.add(p);
                }
                getTargetFragment().onActivityResult(getTargetRequestCode(), DIALOG_RESULT_CODE, getActivity().getIntent());
                getDialog().dismiss();
        }
    }

    static class SimpleAdapter extends ArrayAdapter<PlaceType> implements
            PinnedSectionListAdapter {
        private PlaceType mPlaceType;
        private LayoutInflater mInflater;
        Context mContext;
        public int mResource;

        public SimpleAdapter(Context context, int resource) {
            super(context, resource);
            mContext = context;
            mResource = resource;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            prepareData();
        }

        public void prepareData() {
            int sectionsNumber = 0;
            for (PlaceType p : arrPlaceType) {
                if (p.isSection())
                    sectionsNumber++;
            }
            prepareSections(sectionsNumber);
            int sectionPosition = 0, listPosition = 0;
            for (int i = 0; i < sectionsNumber; i++) {
                PlaceType section = arrPlaceType.get(listPosition);
                onSectionAdded(section, sectionPosition);
                add(section);
                listPosition++;
                for (int j = listPosition; j < arrPlaceType.size(); j++) {
                    PlaceType item = arrPlaceType.get(j);
                    if (!item.isSection()) {
                        add(item);
                    } else {
                        listPosition = j;
                        break;
                    }
                }
                sectionPosition++;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getType();
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == PlaceType.SECTION;
        }

        protected void prepareSections(int sectionsNumber) {
        }

        protected void onSectionAdded(PlaceType section, int sectionPosition) {
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            /*convertView = mInflater.inflate(R.layout.row_item_select_place_type, null);
            CheckBox cbPlaceType = (CheckBox) convertView.findViewById(R.id.cbPlaceType);
            CheckBox cbSection = (CheckBox) convertView.findViewById(R.id.cbSection);
            if (getItem(position) != null) {
                if (getItem(position).isSection()) {
                    //section
                    cbPlaceType.setVisibility(View.GONE);
                    cbSection.setVisibility(View.VISIBLE);
                    cbSection.setText(getItem(position).getName());
                    cbSection.setChecked(getItem(position).isChecked());
                    cbSection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getItem(position).setChecked(!getItem(position).isChecked());
                            LogUtil.log("section: ", getItem(position).toString());

                            for (int i = 0; i < getCount(); i++) {
                                if (getItem(position).getSectionPosition() == getItem(i).getSectionPosition() && !getItem(i).isSection()) {
                                    getItem(i).setChecked(getItem(position).isChecked());
                                }
                            }
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    //item
                    cbSection.setVisibility(View.GONE);
                    cbPlaceType.setVisibility(View.VISIBLE);
                    cbPlaceType.setText(getItem(position).getName());
                    cbPlaceType.setChecked(getItem(position).isChecked());
                    cbPlaceType.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getItem(position).setChecked(!getItem(position).isChecked());
                            LogUtil.log("section: ", getItem(position).toString());
                            notifyDataSetChanged();
                        }
                    });
                }
            }*/


            //view holder pattern
            PlaceTypeHolder holder = null;
            if (convertView == null) {
                holder = new PlaceTypeHolder();
                convertView = mInflater.inflate(R.layout.row_item_select_place_type, null);
                holder.cbPlaceType = (CheckBox) convertView.findViewById(R.id.cbPlaceType);
                holder.cbSection = (TextView) convertView.findViewById(R.id.cbSection);
                convertView.setTag(holder);
                holder.cbPlaceType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        PlaceType placeType = (PlaceType) cb.getTag();
                        placeType.setChecked(cb.isChecked());
                        if (placeType.isChecked()) {
                            //SearchFragment.selectedPlaceTypes.add(placeType);
                        } else {
                            // SearchFragment.selectedPlaceTypes.remove(placeType);
                        }
                        LogUtil.log("section: ", placeType.toString());
//                        notifyDataSetChanged();
                    }
                });
            } else {
                holder = (PlaceTypeHolder) convertView.getTag();
            }
            PlaceType placeType = getItem(position);
            if (placeType != null) {
                if (placeType.isSection()) {
                    //section
                    holder.cbPlaceType.setVisibility(View.GONE);
                    holder.cbSection.setVisibility(View.VISIBLE);
                    holder.cbSection.setTextColor(parent.getResources().getColor(R.color.white));
                    holder.cbSection.setBackgroundColor(parent.getResources().getColor(R.color.wallet_holo_blue_light));
                    holder.cbSection.setText(placeType.getName());
                } else {
                    //item
                    holder.cbSection.setVisibility(View.GONE);
                    holder.cbPlaceType.setVisibility(View.VISIBLE);
                    holder.cbPlaceType.setText(placeType.getName());
                    holder.cbPlaceType.setChecked(placeType.isChecked());
                }
            }
            holder.cbPlaceType.setTag(placeType);
            return convertView;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        static class PlaceTypeHolder {
            CheckBox cbPlaceType;
            TextView cbSection;
        }

    }


    static class FastScrollAdapter extends SimpleAdapter implements SectionIndexer {

        private PlaceType[] sections;

        public FastScrollAdapter(Context context, int resource) {
            super(context, resource);
            LogUtil.log("FastScroll", "create new");
        }

        @Override
        protected void prepareSections(int sectionsNumber) {
            sections = new PlaceType[sectionsNumber];
        }

        @Override
        protected void onSectionAdded(PlaceType section, int sectionPosition) {
            sections[sectionPosition] = section;
        }

        @Override
        public PlaceType[] getSections() {
            return sections;
        }

        @Override
        public int getPositionForSection(int section) {
            if (section >= sections.length) {
                section = sections.length - 1;
            }
            return sections[section].getListPosition();
        }

        @Override
        public int getSectionForPosition(int position) {
            if (position >= getCount()) {
                position = getCount() - 1;
            }
            return getItem(position).getSectionPosition();
        }

    }

}
