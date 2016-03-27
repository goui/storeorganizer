package fr.goui.storeorganizer;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;

/**
 * {@code DetailsFragment} displays the tabs of all the workers.
 * In each tab there is the list of the worker's appointments.
 */
public class DetailsFragment extends Fragment implements OnTimeTickListener, OnAppointmentCreateListener {

    /**
     * The adapter that will return a fragment for each worker section.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The view pager to swipe among sections.
     */
    private ViewPager mViewPager;

    /**
     * The layout for all the worker tabs.
     */
    private TabLayout mTabLayout;

    /**
     * The worker model we are listening to.
     */
    private StoreWorkerModel mStoreWorkerModel = StoreWorkerModel.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // getting the views
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.fragment_details_view_pager);
        mTabLayout = (TabLayout) rootView.findViewById(R.id.fragment_details_tab_layout);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // activity has been created, initializing objects
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.post(new Runnable() {
            @Override
            public void run() {
                mTabLayout.setupWithViewPager(mViewPager);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // indicating that this fragment has its own menu
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // creating the menu
        inflater.inflate(R.menu.menu_fragment_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // displaying a dialog with next availability information
        if (id == R.id.action_next_availability) {
            displayNextAvailabilityDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method used to display a dialog telling user about workers availability.
     * The dialog is only composed of a {@code TextView}.
     */
    private void displayNextAvailabilityDialog() {
        FragmentActivity activity = getActivity();
        Resources resources = activity.getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(resources.getString(R.string.next_availability));
        View dialogLayout = activity.getLayoutInflater().inflate(R.layout.layout_simple_text_view, null);
        builder.setView(dialogLayout);
        TextView textView = (TextView) dialogLayout.findViewById(R.id.layout_simple_text_view);
        StoreWorker worker = StoreWorkerModel.getInstance().getFirstAvailableWorker();
        StoreAppointment appointment = worker.getNextAvailability();
        String availability = new SimpleDateFormat("HH:mm").format(new Date());
        if (appointment != null) {
            if (appointment instanceof NullStoreAppointment) {
                availability = appointment.getFormattedStartTime();
                availability += " " + resources.getString(R.string.during) + " " + appointment.getDuration() + resources.getString(R.string.minutes);
            } else {
                availability = appointment.getFormattedEndTime();
            }
        }
        textView.setText(worker.getName() + " " + resources.getString(R.string.at) + " " + availability);
        builder.setPositiveButton(resources.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        // updating the tabs and the nested fragments
        mSectionsPagerAdapter.notifyDataSetChanged();
        notifyAllWorkers();
    }

    @Override
    public void onTimeTick() {
        // updating the nested fragments
        notifyAllWorkers();
    }

    @Override
    public void onAppointmentCreate(int workerPosition) {
        // updating the concerned nested fragment
        notifyWorkerFragmentAt(workerPosition);
    }

    /**
     * Method used to update the tabs because something about workers has changed.
     *
     * @param observable the model we are listening to
     * @param data       the data containing the information
     */
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
                case StoreWorkerModel.ObsData.REMOVE: // TODO bug when removing one tab (removed tab is still there)
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
     * Method used to update all the nested fragments.
     */
    private void notifyAllWorkers() {
        for (int i = 0; i < mStoreWorkerModel.getStoreWorkersNumber(); i++) {
            notifyWorkerFragmentAt(i);
        }
    }

    /**
     * Method used to update a specific nested fragment.
     *
     * @param position the position of the nested fragment
     */
    private void notifyWorkerFragmentAt(int position) {
        WorkerFragment fragment = (WorkerFragment) getChildFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.fragment_details_view_pager + ":" + position);
        if (fragment != null) {
            fragment.notifyDataSetChanged();
        }
    }

    /**
     * Method used to know which tab is selected.
     * Useful for appointment creation.
     *
     * @return current tab position
     */
    public int getSelectedTabPosition() {
        return mTabLayout.getSelectedTabPosition();
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
        public Fragment getItem(int position) {
            WorkerFragment fragment = new WorkerFragment();
            Bundle args = new Bundle();
            args.putInt(WorkerFragment.ARG_SECTION_NUMBER, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return mStoreWorkerModel.getStoreWorkersNumber();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mStoreWorkerModel.getStoreWorker(position).getName();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
