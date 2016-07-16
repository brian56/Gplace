package com.dqhuynh.gplace.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.Button;

import com.dqhuynh.gplace.R;
import com.dqhuynh.gplace.adapter.PlacesAutoCompleteAdapter;
import com.dqhuynh.gplace.adapter.SearchResultAdapter;
import com.dqhuynh.gplace.api.GeocodingRequest;
import com.dqhuynh.gplace.api.PlaceDetailRequest;
import com.dqhuynh.gplace.api.PlaceNearBySearchRequest;
import com.dqhuynh.gplace.callback.LocationListenerCallback;
import com.dqhuynh.gplace.callback.OnLoadMoreListener;
import com.dqhuynh.gplace.common.CommonData;
import com.dqhuynh.gplace.model.Place;
import com.dqhuynh.gplace.model.PlaceType;
import com.dqhuynh.gplace.model.SearchOptions;
import com.dqhuynh.gplace.utils.GPSTracker;
import com.dqhuynh.gplace.utils.LogUtil;
import com.kisstools.KissTools;
import com.kisstools.utils.CommonUtil;
import com.nostra13.universalimageloader.utils.L;
import com.rey.material.app.Dialog;
import com.rey.material.widget.ProgressView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.Locale;

public class SearchFragment extends Fragment implements LocationListenerCallback, View.OnClickListener {
    private static final String TAG = SearchFragment.class.getSimpleName();
    private static final String ARG_TEXT = "search";
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int REQ_DIALOG_PLACE_TYPE = 1;

    private GeocodingRequest mGeocodingRequest = new GeocodingRequest();
    private PlaceDetailRequest mPlaceDetailRequest = new PlaceDetailRequest();
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private GetCurrentAddressAsyncTask mGetCurrentAddressTask;
    private AutoCompleteTextView autoCompleteView;
    private Animation mZoomOutAnimation;
    private Animation mZoomInAnimation;
    private ImageView mbtnCurrentLocation;
    private ImageView mbtnSpeechInput;
    private ProgressBar mProgressCurrentLocation;
    private ProgressView mPgCurrentLocation;
    private FlowLayout mPlaceTypeContainer;
    private SeekBar mSeekBarRadius;
    private TextView mTvRadius;
    private ImageView mClearText;
    private Button mbtnAddPlaceType;
    private Button mBtnSearch;
    private Location mSearchLocation;
    private RadioButton radProminence;
    private RadioButton radDistance;
    private LinearLayout radiusBlock;
//    private SlidingUpPanel mSlidingUpPanel;
    private SlidingUpPanelLayout mLayout;
    private FrameLayout mMainPanel;
    private GetPlaceLatLngAsyncTask mGetPlaceLatLngTask;
    private Dialog.Builder builder = null;
    private LinearLayout mLlLoadingResult;
    public static ArrayList<PlaceType> selectedPlaceTypes;
    private String sortBy = "";
    private int distance = 5;
    private static final int DIALOG_RESULT_CODE = 1;
    private RecyclerView mRvResults;
    private ArrayList<Place> listResults = new ArrayList<>();
    private SearchResultAdapter mAdapter;
    HandlerThread mHandlerThread;
    Handler mThreadHandler;
    GPSTracker gps;

    public SearchFragment() {
    }


    public static SearchFragment newInstance(String text) {
        SearchFragment fragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        fragment.setArguments(bundle);
        selectedPlaceTypes = new ArrayList<PlaceType>();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        KissTools.setContext(getActivity());
        autoCompleteView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
        mbtnCurrentLocation = (ImageView) view.findViewById(R.id.btnCurrentLocation);
        mPgCurrentLocation = (ProgressView) view.findViewById(R.id.progressCurrentLocation);
        mPlaceTypeContainer = (FlowLayout) view.findViewById(R.id.placeTypeContainer);
        mbtnAddPlaceType = (Button) view.findViewById(R.id.btnAddPlaceType);
        mClearText = (ImageView) view.findViewById(R.id.btnClearText);
        mTvRadius = (TextView) view.findViewById(R.id.tvRadius);
        radDistance = (RadioButton) view.findViewById(R.id.radDistance);
        radProminence = (RadioButton) view.findViewById(R.id.radProminence);
        mSeekBarRadius = (SeekBar) view.findViewById(R.id.seekBarRadius);
        mSeekBarRadius.setEnabled(!radDistance.isChecked());
        mBtnSearch = (Button) view.findViewById(R.id.btnSearch);
        mLlLoadingResult = (LinearLayout) view.findViewById(R.id.llLoadingResult);
        mbtnSpeechInput = (ImageView) view.findViewById(R.id.btnSpeechToText);
        mRvResults = (RecyclerView) view.findViewById(R.id.rvResults);
        mLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
        mMainPanel = (FrameLayout) view.findViewById(R.id.mainPanel);
        radiusBlock = (LinearLayout) view.findViewById(R.id.radiusBlock);
        mZoomInAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.zoom_in);
        mZoomOutAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.zoom_out);
        mBtnSearch.setOnClickListener(this);
        mClearText.setOnClickListener(this);
        mbtnSpeechInput.setOnClickListener(this);
        setupSlidePanel();
        setupResultRecycleView();
        setupSortBy();
        setupRadius();
        setupPlaceAutoCompleteView();
        mbtnAddPlaceType.setOnClickListener(this);

        if (CommonData.currentLocation != null) {
            mbtnCurrentLocation.setVisibility(View.VISIBLE);
            mPgCurrentLocation.setVisibility(View.GONE);
        }
        getLocationOnStart();
        return view;
    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.search_around_here));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity(),
                    getString(R.string.action_example),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void setupSlidePanel() {
//        mSlidingUpPanel.openPanel();
        mLayout.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");

            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");

            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });
    }

    public void setupSortBy() {
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPlaceTypeContainer.requestFocus();
                if (isChecked) {
                    radProminence.setChecked(radProminence == buttonView);
                    radDistance.setChecked(radDistance == buttonView);
                    if (radProminence == buttonView) {
                        radiusBlock.setVisibility(View.VISIBLE);
                        mSeekBarRadius.setEnabled(true);
                        CommonData.currentSearchOption.setRankBy(SearchOptions.PROMINENCE);
                        CommonData.currentSearchOption.setRadius(mSeekBarRadius.getProgress());
                    } else {
                        radiusBlock.setVisibility(View.GONE);
                        CommonData.currentSearchOption.setRankBy(SearchOptions.DISTANCE);
                        CommonData.currentSearchOption.setRadius(0);
                    }
                }
            }
        };
        radProminence.setOnCheckedChangeListener(listener);
        radDistance.setOnCheckedChangeListener(listener);
    }

    public void setupRadius() {
        mSeekBarRadius.setProgress(5);
        mSeekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPlaceTypeContainer.requestFocus();
                if (progress < 1) {
                    seekBar.setProgress(1);
                } else {
                    mTvRadius.setText("Radius: " + progress + " km");
                }
                CommonData.currentSearchOption.setRadius(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public void doSearch() {
        CommonData.currentSearchOption.setPageToken("");
        mRvResults.removeAllViews();
        listResults.clear();
        mAdapter.notifyDataSetChanged();
        mLlLoadingResult.setVisibility(View.VISIBLE);
        SearchTask task = new SearchTask();
        task.execute(CommonData.currentSearchOption);
    }

    /**
     * Load default/saved search options on start/resume
     */
    private void loadSavedSearchOptions() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    /*
    public void setBackPressToClosePanel() {
        if (!mSlidingUpPanel.isOpen()) {
            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        // handle back button's click listener
                        if (!mSlidingUpPanel.isOpen()) {
                            mSlidingUpPanel.openPanel(false);
                        }
                        getView().setFocusableInTouchMode(false);
                        getView().clearFocus();
                        getView().setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                return true;
                            }
                        });
                        return true;
                    }
                    return false;
                }
            });
        } else {
            getView().setFocusableInTouchMode(false);
            getView().clearFocus();
            getView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    return true;
                }
            });
        }
    }
    */

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Stuff to do, dependent on requestCode and resultCode
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    getView().setFocusableInTouchMode(false);
//                    getView().clearFocus();
                    autoCompleteView.requestFocus();
                    CommonUtil.hideIME(getActivity(), autoCompleteView);
                    autoCompleteView.setText(result.get(0));
                    autoCompleteView.setSelection(autoCompleteView.getText().length(), autoCompleteView.getText().length());
                }
                break;
            }
            case REQ_DIALOG_PLACE_TYPE: {
                // This is the return result of your DialogFragment
                if (resultCode == DIALOG_RESULT_CODE) {
                    CommonData.currentSearchOption.setPlaceTypes(selectedPlaceTypes);
                    addPlaceTypeToContainer();
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        mPlaceTypeContainer.requestFocus();
        android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
        switch (v.getId()) {
            case R.id.btnSpeechToText:
                promptSpeechInput();
                break;
            case R.id.btnClearText:
                autoCompleteView.setText("");
                if (CommonData.currentLocation != null) {
                    autoCompleteView.setHint("Search around here");
                }
                CommonData.searchLocation = CommonData.currentLocation;
                CommonData.currentSearchOption.setLocation(CommonData.currentLocation);
                break;
            case R.id.btnAddPlaceType:
                PlaceTypeDialogFragment p = new PlaceTypeDialogFragment();
                p.setTargetFragment(this, REQ_DIALOG_PLACE_TYPE);
                p.show(fm, "place_type");
                break;
            case R.id.btnSearch:
                if (CommonData.currentSearchOption != null) {
//                    Toast.makeText(getActivity(), CommonData.currentSearchOption.toString(),
//                            Toast.LENGTH_LONG).show();
//                    Fragment mFragment = new SearchResultFragment();
//                    MainActivity main = (MainActivity) getActivity();
//                    main.setFragmentChild(mFragment, "Result");
                    if(CommonData.currentSearchOption.getRankBy().compareTo(SearchOptions.DISTANCE)==0 && CommonData.currentSearchOption.getPlaceTypes().size()==0){
                        Toast.makeText(getActivity(), "Please chose a place type!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(mLayout.getPanelState()== SlidingUpPanelLayout.PanelState.EXPANDED)
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    doSearch();
                }
                break;
        }
    }

    @Override
    public void onLocationReceiver() {
        getLocationOnStart();
    }

    /**
     * Add selected place types to the container view after user chose the dialog
    */
    private void addPlaceTypeToContainer() {
        if (selectedPlaceTypes.size() > 0) {
            mPlaceTypeContainer.removeAllViews();
            for (int i = 0; i < selectedPlaceTypes.size(); i++) {
                PlaceType p = selectedPlaceTypes.get(i);
                TextView btn = new TextView(getActivity());
                btn.setText(p.getName());
                btn.setTag(p);
                btn.setBackgroundResource(R.drawable.bg_grey);
                btn.setTextColor(getResources().getColor(R.color.white));
                FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 8, 8, 0); // change this to your liking
                btn.setLayoutParams(params);
                btn.setGravity(Gravity.CENTER);
                btn.setPadding(8, 8, 8, 8);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedPlaceTypes.remove(v.getTag());
                        v.setVisibility(View.GONE);
                    }
                });
                mPlaceTypeContainer.addView(btn);
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // Get rid of our Place API Handlers
        if (mThreadHandler != null) {
            mThreadHandler.removeCallbacksAndMessages(null);
            mHandlerThread.quit();
        }
    }

    /**
     * Start getting current location
     */
    private void getLocationOnStart() {
        // create class object
        gps = new GPSTracker(getActivity(), this);

        // check if GPS enabled
        if (gps.canGetLocation()) {
            if (CommonData.currentLocation == null) {
                CommonData.currentLocation = gps.getLocation();
                CommonData.searchLocation = CommonData.currentLocation;
                CommonData.currentSearchOption.setLocation(CommonData.searchLocation);
                mGetCurrentAddressTask = new GetCurrentAddressAsyncTask();
                mGetCurrentAddressTask.execute(CommonData.currentLocation);
            }
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    /**
     * Do after got current location
     */
    private void doAfterGetCurrentLocation() {
        autoCompleteView.setHint("Search Around here");
        mZoomOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPgCurrentLocation.setVisibility(View.GONE);
                mbtnCurrentLocation.startAnimation(mZoomInAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
//        mProgressCurrentLocation.startAnimation(mZoomOutAnimation);
        mPgCurrentLocation.startAnimation(mZoomOutAnimation);
    }

    /**
     * Setup data for Place auto complete view
     */
    private void setupPlaceAutoCompleteView() {
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getActivity(), R.layout.row_item_autocomplete);
        autoCompleteView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.row_item_autocomplete));

        if (mThreadHandler == null) {
            // Initialize and start the HandlerThread
            // which is basically a Thread with a Looper
            // attached (hence a MessageQueue)
            mHandlerThread = new HandlerThread(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND);
            mHandlerThread.start();

            // Initialize the Handler
            mThreadHandler = new Handler(mHandlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        ArrayList<String> results = mAutoCompleteAdapter.resultList;

                        if (results != null && results.size() > 0) {
                            mAutoCompleteAdapter.notifyDataSetChanged();
                        } else {
                            mAutoCompleteAdapter.notifyDataSetInvalidated();
                        }
                    }
                }
            };
        }
        autoCompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                String description = (String) parent.getItemAtPosition(position);
                if (position != parent.getCount() - 1) {
                    LogUtil.log(TAG, CommonData.listAutoCompletePlaceId.get(position));
                    Toast.makeText(getActivity(), description + "\n" + CommonData.listAutoCompletePlaceId.get(position), Toast.LENGTH_SHORT).show();
                    mGetPlaceLatLngTask = new GetPlaceLatLngAsyncTask();
                    mGetPlaceLatLngTask.execute(CommonData.listAutoCompletePlaceId.get(position));
                } else autoCompleteView.setText("");
            }
        });
        autoCompleteView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String value = s.toString();
                if (value.equals("")) {
                    autoCompleteView.setHint("Search around here");
                    mbtnSpeechInput.setVisibility(View.VISIBLE);
                    mClearText.setVisibility(View.GONE);
                } else {
                    mbtnSpeechInput.setVisibility(View.GONE);
                    mClearText.setVisibility(View.VISIBLE);
                }

                // Remove all callbacks and messages
                mThreadHandler.removeCallbacksAndMessages(null);

                // Now add a new one
                mThreadHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // Background thread
                        if (!value.equals(""))
                            mAutoCompleteAdapter.resultList = mAutoCompleteAdapter.mPlaceAPI.autoComplete(value);
                        if (mAutoCompleteAdapter != null && mAutoCompleteAdapter.resultList != null) {
                            // Footer
                            if (mAutoCompleteAdapter.resultList.size() > 0)
                                mAutoCompleteAdapter.resultList.add("footer");

                            // Post to Main Thread
                            mThreadHandler.sendEmptyMessage(1);
                        }
                    }
                }, 300);
            }

            @Override
            public void afterTextChanged(Editable s) {
//                doAfterTextChanged();
            }
        });
    }

    private void setupResultRecycleView() {
        mRvResults.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRvResults.setLayoutManager(llm);
        mAdapter = new SearchResultAdapter(listResults, mRvResults, getActivity());
        mAdapter.setOnItemClickListener(new SearchResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Place place) {
                Toast.makeText(
                        getActivity(),
                        "Data : \n" + place.getName() + " \n"
                                + place.getFormatted_address(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                SearchTask task = new SearchTask();
                task.execute(CommonData.currentSearchOption);
            }
        });
        mRvResults.setAdapter(mAdapter);
    }

    /**
     * Get current address async task
     * Result: formatted address e.g 1 Cong Hoa st, Tan Binh district, Ho Chi Minh city...
     */
    private class GetCurrentAddressAsyncTask extends AsyncTask<Location, Integer, Place> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Place doInBackground(Location... params) {
            // TODO Auto-generated method stub
            return mGeocodingRequest.geocoding(params[0]);
        }

        protected void onPostExecute(Place result) {
            if (result != null) {
                Toast.makeText(getActivity(), "Your location is" + result.getFormatted_address(), Toast.LENGTH_SHORT).show();
                doAfterGetCurrentLocation();
            }
        }

        protected void onProgressUpdate(Integer... progress) {
        }
    }

    /**
     * Get place's latitude and longitude from place id
     */
    private class GetPlaceLatLngAsyncTask extends AsyncTask<String, Integer, Place> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Place doInBackground(String... params) {
            // TODO Auto-generated method stub
            return mPlaceDetailRequest.placeDetail(params[0]);
        }

        protected void onPostExecute(Place result) {
            if (result != null) {
                Toast.makeText(getActivity(), "Your location is: " + result.getLat() + "\n" + result.getLng(), Toast.LENGTH_SHORT).show();
                mSearchLocation = new Location(LocationManager.NETWORK_PROVIDER);
                mSearchLocation.setLatitude(result.getLat());
                mSearchLocation.setLongitude(result.getLng());
                CommonData.searchLocation = mSearchLocation;
                CommonData.currentSearchOption.setLocation(mSearchLocation);
            }
        }

        protected void onProgressUpdate(Integer... progress) {
        }
    }

    private class SearchTask extends AsyncTask<SearchOptions, Integer, ArrayList<Place>> {
        @Override
        protected void onPreExecute() {
            if (CommonData.currentSearchOption.canLoadMore() && listResults.size() > 0) {
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
            mLlLoadingResult.setVisibility(View.GONE);
            if (listResults.size() > 1 && listResults.get(listResults.size() - 1) == null) {
                listResults.remove(listResults.size() - 1);
                mAdapter.notifyItemRemoved(listResults.size());
            }
            if (result != null) {
                for (Place p : result) {
                    listResults.add(p);
                    mAdapter.notifyItemInserted(listResults.size() - 1);
                }
                LogUtil.log("Search result", listResults.toString());
                mAdapter.setLoaded();
            }
        }
    }
}
