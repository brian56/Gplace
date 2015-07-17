package com.dqhuynh.gplace.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dqhuynh.gplace.R;
import com.dqhuynh.gplace.views.SlidingUpPanel;
import com.dqhuynh.gplace.views.SlidingUpPanel.OnPanelCloseListener;
import com.dqhuynh.gplace.views.SlidingUpPanel.OnPanelOpenListener;
import com.dqhuynh.gplace.views.SlidingUpPanel.OnPanelScrollListener;

public class MainFragment extends Fragment {

    private static final String ARG_TEXT = "text";
    private static final String TAG = MainFragment.class.getSimpleName();
    private SlidingUpPanel mSlidingUpPanel;
    private ImageView mCoverDown;
    private TextView mCoverHint;
    private Button mClickToClose;
    private Button mClickToOpen;

    public MainFragment() {
    }

    public static MainFragment newInstance(String text) {
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.panel, container, false);
        mSlidingUpPanel = (SlidingUpPanel) view.findViewById(R.id.sliding_up_panel);
        mCoverDown = (ImageView) view.findViewById(R.id.cover_down);
        mCoverHint = (TextView) view.findViewById(R.id.cover_hint);
        mClickToClose = (Button) view.findViewById(R.id.click_to_close);
        mClickToOpen = (Button) view.findViewById(R.id.click_to_open);
        String text = "";
        if (getArguments() != null && getArguments().containsKey(ARG_TEXT)) {
            text = getArguments().getString(ARG_TEXT);
            if (TextUtils.isEmpty(text)) text = "";
        }
        mClickToOpen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanel.openPanel(false);
            }
        });
        mSlidingUpPanel.setOnPanelOpenListener(new OnPanelOpenListener() {
            @Override
            public void onPanelOpened() {
                showToast("Sliding up panel opened!");
            }
        });
        mSlidingUpPanel.setOnPanelCloseListener(new OnPanelCloseListener() {
            @Override
            public void onPanelClosed() {
                showToast("Sliding up panel closed!");
            }
        });
        mSlidingUpPanel.setOnPanelScrolledListener(new OnPanelScrollListener() {
            @Override
            public void onPanelScrolled(float offset) {
                Log.d(TAG, "onPanelScrolled offset=" + offset);
                mCoverDown.setAlpha((int) ((1f - offset) * 255));
            }
        });

        mCoverDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Cover down pressed!");
            }
        });

        mClickToClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanel.closePanel();
            }
        });
        mSlidingUpPanel.openPanel(false);
        return view;
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
