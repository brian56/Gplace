package com.dqhuynh.gplace.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.dqhuynh.gplace.R;
import com.dqhuynh.gplace.fragment.MainFragment;
import com.dqhuynh.gplace.fragment.SearchFragment;
import com.kisstools.KissTools;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;

public class MainActivity extends MaterialNavigationDrawer {
    @Override
    public void init(Bundle savedInstanceState) {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_drawer, null);
        setDrawerHeaderCustom(view);

        // create sections
//        this.addSection(newSection(getResources().getString(R.string.menu_my_location), new MainFragment()));
        this.addSection(newSection(getResources().getString(R.string.menu_search), new SearchFragment().newInstance("test")));
        this.addSection(newSection(getResources().getString(R.string.menu_favorite), new MainFragment()));
        this.addSection(newSection(getResources().getString(R.string.menu_history), new MainFragment()));
        this.addSection(newSection(getResources().getString(R.string.menu_option), new MainFragment()));
//        this.addSection(newSection("Section", R.drawable.ic_launcher, new MainFragment()).setSectionColor(Color.parseColor("#03a9f4")));
        // create bottom section
        this.addBottomSection(newSection(getResources().getString(R.string.menu_about),
                R.drawable.abc_ic_go_search_api_mtrl_alpha, new Intent(this, Settings.class)));
    }


    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KissTools.setContext(this);
        this.closeDrawer();
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
