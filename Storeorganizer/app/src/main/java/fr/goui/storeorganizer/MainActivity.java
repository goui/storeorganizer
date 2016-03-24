package fr.goui.storeorganizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager mFragmentManager;

    private FloatingActionButton mFloatingActionButton;

    private Fragment mCurrentFragment;

    private Resources mResources;

    /**
     * The broadcast receiver to be notified every minute of the clock.
     */
    private BroadcastReceiver mTimeBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0 && mCurrentFragment != null) {
                ((OnTimeTickListener) mCurrentFragment).onTimeTick();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        mResources = getResources();

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AppointmentCreationActivity.class);
                if (mCurrentFragment instanceof DetailsFragment) {
                    intent.putExtra(mResources.getString(R.string.intent_appointment_creation_worker_position), ((DetailsFragment) mCurrentFragment).getSelectedTabPosition());
                }
                startActivityForResult(intent, mResources.getInteger(R.integer.intent_request_code_appointment_creation));
            }
        });

        mFragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        mCurrentFragment = new DetailsFragment();
        transaction.replace(R.id.main_content, mCurrentFragment).commit();

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if an appointment has been created
        if (requestCode == mResources.getInteger(R.integer.intent_request_code_appointment_creation)) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra(mResources.getString(R.string.intent_appointment_creation_result_worker_position), mResources.getInteger(R.integer.invalid_position));
                if (mCurrentFragment != null && position != mResources.getInteger(R.integer.invalid_position)) {
                    ((OnAppointmentCreateListener) mCurrentFragment).onAppointmentCreate(position);
                }
            }
        }

        // if an appointment has been updated
        if (requestCode == mResources.getInteger(R.integer.intent_request_code_appointment_edition)) {
            if (resultCode == RESULT_OK) {
                int oldWorkerPosition = data.getIntExtra(getString(R.string.intent_appointment_edition_result_old_worker_position), mResources.getInteger(R.integer.invalid_position));
                int newWorkerPosition = data.getIntExtra(getString(R.string.intent_appointment_edition_result_new_worker_position), mResources.getInteger(R.integer.invalid_position));
                int oldAppointmentPosition = data.getIntExtra(getString(R.string.intent_appointment_edition_result_old_appointment_position), mResources.getInteger(R.integer.invalid_position));
                // TODO result from appointment edition
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mTimeBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTimeBroadcastReceiver != null) {
            unregisterReceiver(mTimeBroadcastReceiver);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        boolean ret = false;
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_details) {
            mCurrentFragment = new DetailsFragment();
            transaction.replace(R.id.main_content, mCurrentFragment).commit();
            ret = true;
        } else if (id == R.id.nav_overall) {
            mCurrentFragment = new OverallFragment();
            transaction.replace(R.id.main_content, mCurrentFragment).commit();
            ret = true;
        } else if (id == R.id.nav_settings) {
            mCurrentFragment = null;
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            ret = false;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return ret;
    }
}
