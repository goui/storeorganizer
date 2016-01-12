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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class DetailsActivity extends AppCompatActivity implements Observer {

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

    /**
     * The index of the tab to remove.
     */
    private int mTabToBeRemoved = -1;

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
                case StoreWorkerModel.ObsData.CREATION:
                    mTabLayout.addTab(mTabLayout.newTab().setText(obsData.worker.getName()));
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    break;
                case StoreWorkerModel.ObsData.UPDATE:
                    mTabLayout.getTabAt(obsData.workersPosition).setText(obsData.worker.getName());
                    break;
                case StoreWorkerModel.ObsData.REMOVAL:
                    if(obsData.workersPosition == mTabLayout.getSelectedTabPosition()) {
                        mTabToBeRemoved = obsData.workersPosition;
                    } else {
                        mTabLayout.removeTabAt(obsData.workersPosition);
                        mSectionsPagerAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mTabToBeRemoved != -1) {
            mSectionsPagerAdapter.notifyDataSetChanged();
            mTabLayout.removeTabAt(mTabToBeRemoved);
            mSectionsPagerAdapter.notifyDataSetChanged();
            mTabToBeRemoved = -1;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_details, container, false);

            // Get the views in the fragment
            TextClock textClock = (TextClock) rootView.findViewById(R.id.fragment_details_text_clock);
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_details_recycler_view);

            // Set the on click listener for the clock
            textClock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO scroll to most relevant element in list (i.e. current task according to current time)
                    // recyclerView.setSelectionFromTop(position, mListView.getTop());
                    Toast.makeText(getActivity(), "it is " + ((TextClock) v).getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });

            // Set up the list view with custom adapter
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            List<Object> objects = createListForAdapter(StoreWorkerModel.getInstance().getStoreWorker(sectionNumber).getTasks());
            recyclerView.setAdapter(new DetailsRecyclerAdapter(objects));

            // TODO on click

            return rootView;
        }

        private List<Object> createListForAdapter(List<StoreTask> tasks_p) {
            // TODO
            return new ArrayList<>();
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
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(PlaceholderFragment.ARG_SECTION_NUMBER, position_p);
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

    }
}
