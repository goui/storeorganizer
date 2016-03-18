package fr.goui.storeorganizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
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

    /**
     * The tab layout.
     */
    private TabLayout mTabLayout;

    private Resources mResources;

    private BroadcastReceiver mTimeBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                for (int i = 0; i < StoreWorkerModel.getInstance().getStoreWorkersNumber(); i++) {
                    DetailsFragment fragment = (DetailsFragment) getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.container + ":" + i);
                    fragment.updateList();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mResources = getResources();
        StoreWorkerModel.getInstance().addObserver(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, AppointmentCreationActivity.class);
                intent.putExtra(mResources.getString(R.string.intent_appointment_creation_worker_position), mTabLayout.getSelectedTabPosition());
                startActivityForResult(intent, REQUEST_CODE_CREATE_APPOINTMENT);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mTimeBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    protected void onStop() {
        super.onStop();
        StoreWorkerModel.getInstance().deleteObserver(this);
        if (mTimeBroadcastReceiver != null) {
            unregisterReceiver(mTimeBroadcastReceiver);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_APPOINTMENT) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra(mResources.getString(R.string.intent_appointment_creation_result_worker_position), -1);
                DetailsFragment fragment = (DetailsFragment) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + position);
                fragment.notifyDataSetChanged();
            }
        }
        if (requestCode == REQUEST_CODE_EDIT_APPOINTMENT) {
            if (resultCode == RESULT_OK) {
                int oldWorkerPosition = data.getIntExtra(getString(R.string.intent_appointment_edition_result_old_worker_position), -1);
                int newWorkerPosition = data.getIntExtra(getString(R.string.intent_appointment_edition_result_new_worker_position), -1);
                int oldAppointmentPosition = data.getIntExtra(getString(R.string.intent_appointment_edition_result_old_appointment_position), -1);
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
        } else if (id == R.id.action_go_to_overall) {
            Intent intent = new Intent(DetailsActivity.this, OverallActivity.class);
            startActivity(intent);
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
            if (appointment instanceof NullStoreAppointment) {
                availability = appointment.getFormattedStartTime();
                availability += " during " + appointment.getDuration() + getString(R.string.minutes);
            } else {
                availability = appointment.getFormattedEndTime();
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
            return StoreWorkerModel.getInstance().getStoreWorkersNumber();
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
