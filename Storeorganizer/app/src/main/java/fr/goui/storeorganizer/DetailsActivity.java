package fr.goui.storeorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Observable;
import java.util.Observer;

public class DetailsActivity extends AppCompatActivity implements Observer {

    private static final int REQUEST_CODE_APPOINTMENT_CREATION = 1;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /**
     * The tab layout.
     */
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        StoreWorkerModel.getInstance().addObserver(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_APPOINTMENT_CREATION) {
            if (resultCode == RESULT_OK) {
                // TODO update model and corresponding fragment
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(DetailsActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof StoreWorkerModel && data instanceof StoreWorkerModel.ObsData) {
            StoreWorkerModel.ObsData obsData = (StoreWorkerModel.ObsData) data;
            switch (obsData.updateReason) {
                case StoreWorkerModel.ObsData.CREATE:
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    mTabLayout.addTab(mTabLayout.newTab().setText(obsData.worker.getName()));
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    break;
                case StoreWorkerModel.ObsData.UPDATE:
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    mTabLayout.getTabAt(obsData.workersPosition).setText(obsData.worker.getName());
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    break;
                case StoreWorkerModel.ObsData.REMOVE:
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    mTabLayout.removeTabAt(obsData.workersPosition);
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    break;
                case StoreWorkerModel.ObsData.REMOVE_ALL:
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    mTabLayout.removeAllTabs();
                    mTabLayout.addTab(mTabLayout.newTab().setText(obsData.worker.getName()));
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position_p) {
            DetailsFragment fragment = new DetailsFragment();
            Bundle args = new Bundle();
            args.putInt(DetailsFragment.ARG_SECTION_NUMBER, position_p);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return StoreWorkerModel.getInstance().getStoreWorkerNumber();
        }

        @Override
        public CharSequence getPageTitle(int position_p) {
            return StoreWorkerModel.getInstance().getStoreWorker(position_p).getName();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
