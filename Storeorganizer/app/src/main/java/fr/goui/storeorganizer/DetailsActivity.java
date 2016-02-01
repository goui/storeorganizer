package fr.goui.storeorganizer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    private static final int REQUEST_CODE_CREATE_APPOINTMENT = 1;
    public static final int REQUEST_CODE_EDIT_APPOINTMENT = 2;
    public static final String INTENT_EXTRA_APPOINTMENT_POSITION = "intent_extra_appointment_position";
    public static final String INTENT_EXTRA_WORKER_POSITION = "intent_extra_worker_position";

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
                Intent intent = new Intent(DetailsActivity.this, AppointmentCreationActivity.class);
                intent.putExtra(AppointmentCreationActivity.INTENT_EXTRA_WORKER_POSITION_STRING_KEY, mTabLayout.getSelectedTabPosition());
                startActivityForResult(intent, REQUEST_CODE_CREATE_APPOINTMENT);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_APPOINTMENT) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra(AppointmentCreationActivity.INTENT_EXTRA_WORKER_POSITION_STRING_KEY, -1);
                DetailsFragment fragment = (DetailsFragment) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + position);
                fragment.notifyDataSetChanged();
            }
        }
        if (requestCode == REQUEST_CODE_EDIT_APPOINTMENT) {
            if (resultCode == RESULT_OK) {
                int oldWorkerPosition = data.getIntExtra(AppointmentEditionActivity.INTENT_EXTRA_OLD_WORKER_POSITION, -1);
                int newWorkerPosition = data.getIntExtra(AppointmentEditionActivity.INTENT_EXTRA_NEW_WORKER_POSITION, -1);
                int oldAppointmentPosition = data.getIntExtra(AppointmentEditionActivity.INTENT_EXTRA_OLD_APPOINTMENT_POSITION, -1);
                DetailsFragment oldFragment = (DetailsFragment) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + oldWorkerPosition);
                if (oldWorkerPosition != newWorkerPosition) {
                    oldFragment.onAppointmentDelete(oldAppointmentPosition);
                    DetailsFragment newFragment = (DetailsFragment) getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.container + ":" + newWorkerPosition);
                    newFragment.notifyDataSetChanged();
                } else {
                    oldFragment.notifyDataSetChanged();
                }
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(DetailsActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_next_availability) {
            displayNextAvailabilityDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayNextAvailabilityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.next_availability));
        View dialogLayout = getLayoutInflater().inflate(R.layout.layout_simple_text_view, null);
        builder.setView(dialogLayout);
        TextView textView = (TextView) dialogLayout.findViewById(R.id.layout_simple_text_view);
        StoreWorker worker = StoreWorkerModel.getInstance().getFirstAvailableWorker();
        StoreAppointment appointment = worker.getNextAvailability();
        String availability = new SimpleDateFormat("HH:mm").format(new Date());
        if (appointment != null) {
            if (appointment instanceof StoreAppointment.NullStoreAppointment) {
                availability = new SimpleDateFormat("HH:mm").format(worker.getNextAvailability().getStartDate());
                availability += " during " + appointment.getDuration() + getString(R.string.minutes);
            } else {
                availability = new SimpleDateFormat("HH:mm").format(worker.getNextAvailability().getEndDate());
            }
        }
        textView.setText(worker.getName() + " " + getString(R.string.at) + " " + availability);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
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
        public long getItemId(int position) {
            return position;
        }
    }
}
