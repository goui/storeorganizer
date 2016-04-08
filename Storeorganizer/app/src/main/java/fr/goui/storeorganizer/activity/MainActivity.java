package fr.goui.storeorganizer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import fr.goui.storeorganizer.adapter.BottomSheetRecyclerAdapter;
import fr.goui.storeorganizer.fragment.DetailsFragment;
import fr.goui.storeorganizer.listener.OnAppointmentCreateListener;
import fr.goui.storeorganizer.listener.OnBottomSheetItemClickListener;
import fr.goui.storeorganizer.listener.OnTimeTickListener;
import fr.goui.storeorganizer.fragment.OverallFragment;
import fr.goui.storeorganizer.R;
import fr.goui.storeorganizer.model.StoreModel;
import fr.goui.storeorganizer.model.StoreWorkerModel;

/**
 * {@code MainActivity} is the main screen of the application. It navigates by means of a navigation drawer.
 * It can access {@link DetailsFragment}, {@link OverallFragment} and {@link SettingsActivity}.
 * By default the displayed fragment is {@code DetailsFragment}.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Observer, OnBottomSheetItemClickListener {

    /**
     * Constant for the bottom sheet add appointment item.
     */
    private static final int BOTTOM_SHEET_ADD_APPOINTMENT = 0;

    /**
     * Constant for the bottom sheet add lunch break item.
     */
    private static final int BOTTOM_SHEET_LUNCH_BREAK = 1;

    /**
     * The manager used to display fragments.
     */
    private FragmentManager mFragmentManager;

    /**
     * The layout for the navigation drawer.
     */
    private DrawerLayout mDrawerLayout;

    /**
     * The fragment currently displayed.
     */
    private Fragment mCurrentFragment;

    /**
     * Android resources.
     */
    private Resources mResources;

    /**
     * The gesture detector used to track zoom in / zoom out gestures.
     */
    private ScaleGestureDetector mScaleGestureDetector;

    /**
     * The current zoom value.
     */
    private float mScaleFactor;

    /**
     * The min zoom value.
     */
    private int mScaleFactorMin;

    /**
     * The max zoom value
     */
    private int mScaleFactorMax;

    /**
     * The worker model.
     */
    private StoreWorkerModel mStoreWorkerModel = StoreWorkerModel.getInstance();

    /**
     * The store model.
     */
    private StoreModel mStoreModel = StoreModel.getInstance();

    /**
     * The bottom sheet dialog.
     */
    private BottomSheetDialog mBottomSheetDialog;

    /**
     * The bottom sheet behavior used to extend or collapse the bottom sheet dialog.
     */
    private BottomSheetBehavior mBottomSheetBehavior;

    /**
     * The window layout inflater.
     */
    private LayoutInflater mLayoutInflater;

    /**
     * The list of bottom sheet string items.
     */
    private List<String> mBottomSheetItemsTexts;

    /**
     * The list of bottom sheet integer icon items.
     */
    private List<Integer> mBottomSheetItemsIcons;

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

        // setting the layout
        setContentView(R.layout.activity_main);

        // getting the views
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        // linking the toolbar to this activity
        setSupportActionBar(toolbar);

        // getting the android resources
        mResources = getResources();

        // click listener for the floating action button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

        // getting the fragment manager
        mFragmentManager = getSupportFragmentManager();

        // displaying DetailsFragment
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        mCurrentFragment = new DetailsFragment();
        transaction.replace(R.id.main_content, mCurrentFragment).commit();

        // affecting the navigation drawer listener to this activity
        navigationView.setNavigationItemSelectedListener(this);

        // linking the hamburger menu to the navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // subscribing to the models
        mStoreWorkerModel.addObserver(this);
        mStoreModel.addObserver(this);

        // getting the zoom values
        mScaleFactorMin = mResources.getInteger(R.integer.scale_factor_min_value);
        mScaleFactorMax = mResources.getInteger(R.integer.scale_factor_max_value);
        mScaleFactor = mScaleFactorMin;

        // creating the zoom gesture listener
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        // getting the layout inflater
        mLayoutInflater = getLayoutInflater();

        // adding items for the bottom sheet
        mBottomSheetItemsTexts = new ArrayList<>();
        mBottomSheetItemsTexts.add(mResources.getString(R.string.add_appointment));
        mBottomSheetItemsTexts.add(mResources.getString(R.string.add_lunch_break));
        mBottomSheetItemsIcons = new ArrayList<>();
        mBottomSheetItemsIcons.add(R.drawable.ic_vector_appointment);
        mBottomSheetItemsIcons.add(R.drawable.ic_vector_lunch_break);

        // the bottom sheet
        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if an appointment has been created, sending the information to the current fragment
        if (requestCode == mResources.getInteger(R.integer.intent_request_code_appointment_creation)) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra(mResources.getString(R.string.intent_appointment_creation_result_worker_position), mResources.getInteger(R.integer.invalid_position));
                if (mCurrentFragment != null && position != mResources.getInteger(R.integer.invalid_position)) {
                    ((OnAppointmentCreateListener) mCurrentFragment).onAppointmentCreate(position);
                }
            }
        }
    }

    /**
     * Method used to expand the bottom sheet dialog.
     */
    private void showBottomSheetDialog() {
        mBottomSheetDialog = new BottomSheetDialog(this);
        View view = mLayoutInflater.inflate(R.layout.layout_bottom_sheet, null);
        int peekHeight = view.getMeasuredHeight();
        mBottomSheetBehavior.setPeekHeight(peekHeight);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.layout_bottom_sheet_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BottomSheetRecyclerAdapter(this, mBottomSheetItemsTexts, mBottomSheetItemsIcons, this));

        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }

    @Override
    public void onBottomSheetItemClick(int position) {

        // adding whatever the user has chosen
        switch (position) {
            case BOTTOM_SHEET_ADD_APPOINTMENT:
                Intent intent = new Intent(MainActivity.this, AppointmentCreationActivity.class);
                if (mCurrentFragment instanceof DetailsFragment) {
                    intent.putExtra(mResources.getString(R.string.intent_appointment_creation_worker_position), ((DetailsFragment) mCurrentFragment).getSelectedTabPosition());
                }
                startActivityForResult(intent, mResources.getInteger(R.integer.intent_request_code_appointment_creation));
                break;
            case BOTTOM_SHEET_LUNCH_BREAK:
                // TODO lunch break
                break;
        }

        // collapsing and dismissing the bottom sheet dialog
        mBottomSheetDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // registering the time tick broadcast receiver
        registerReceiver(mTimeBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    protected void onStop() {
        super.onStop();
        // unregistering the time tick broadcast receiver
        if (mTimeBroadcastReceiver != null) {
            unregisterReceiver(mTimeBroadcastReceiver);
        }
    }

    @Override
    public void onBackPressed() {
        // if the drawer is open, closing it if the user presses back
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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
        if (id == R.id.nav_details) { // displaying details fragment
            mCurrentFragment = new DetailsFragment();
            transaction.replace(R.id.main_content, mCurrentFragment).commit();
            ret = true;
        } else if (id == R.id.nav_overall) { // displaying overall fragment
            mCurrentFragment = new OverallFragment();
            transaction.replace(R.id.main_content, mCurrentFragment).commit();
            ret = true;
        } else if (id == R.id.nav_settings) { // navigating to the settings activity
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            ret = false;
        }

        // closing the navigation drawer when a choice has been made
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return ret;
    }

    @Override
    public void update(Observable observable, Object data) {
        // model update callback
        if (mCurrentFragment instanceof DetailsFragment) {
            ((DetailsFragment) mCurrentFragment).update(observable, data);
        } else if (mCurrentFragment instanceof OverallFragment) {
            ((OverallFragment) mCurrentFragment).update(observable, data);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // intercepting touch events for zoom behavior
        if (mCurrentFragment instanceof OverallFragment) {
            mScaleGestureDetector.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * Custom gesture listener used to track pinch zoom gestures.
     */
    class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // zooming in a range of 1x to 3x
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(mScaleFactorMin, Math.min(mScaleFactor, mScaleFactorMax));
            ((OverallFragment) mCurrentFragment).onScaleChanged(mScaleFactor);
            return true;
        }
    }
}
