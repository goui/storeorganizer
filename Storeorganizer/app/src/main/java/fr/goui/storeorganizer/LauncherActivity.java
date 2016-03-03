package fr.goui.storeorganizer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Calendar;

/**
 * {@code LauncherActivity} is the first activity started.
 * It is displayed in fullscreen and presents a {@code ProgressBar} followed by a {@code TextView}.
 * Its main objective is to make the user wait until all {@link SharedPreferences} has been loaded.
 * The loaded {@code SharedPreferences} will be used to create all the models used in the app.
 */
public class LauncherActivity extends AppCompatActivity {

    /**
     * The "click to continue" {@code TextView}.
     */
    private View mContentView;

    /**
     * The temporary {@code ProgressBar}.
     */
    private ProgressBar mProgressBar;

    /**
     * The {@code SharedPreferences}.
     */
    private SharedPreferences mSharedPreferences;

    /**
     * The android resources to get project values.
     */
    private Resources mResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setting the layout
        setContentView(R.layout.activity_launcher);

        // getting the resources
        mResources = getResources();

        // getting the views
        mProgressBar = (ProgressBar) findViewById(R.id.activity_launcher_progress_bar);
        mContentView = findViewById(R.id.activity_launcher_continue_text_view);

        // indeterminate progress bar
        mProgressBar.setIndeterminate(true);

        // hiding the GUI
        hide();

        // text view touch listener finishing this activity and starting the next one
        mContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(LauncherActivity.this, DetailsActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        });

        // getting the shared prefs
        mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        // launching the shared prefs recuperation in background
        new ProgressTask().execute();
    }

    /**
     * Method used to hide the GUI and display this activity in fullscreen.
     */
    private void hide() {

        // hiding the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // making the views take all the available space
        mProgressBar.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    /**
     * The background task used to get all the information from the {@code SharedPreferences}.
     * If there is nothing in the {@code SharedPreferences}, default values will be created.
     * Once everything is recuperated app models will be created.
     */
    private class ProgressTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            // getting workers information in the shared prefs and putting it in the model
            createWorkers();

            // getting tasks information in the shared prefs and putting it in the model
            createTasks();

            // getting store information in the shared prefs and putting it in the model
            getStoreInfo();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            // when it is done display the text view
            mProgressBar.setVisibility(View.GONE);
            mContentView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Method used to get information about the workers and put it in the corresponding model.
     */
    private void createWorkers() {
        StoreWorkerModel storeWorkerModel = StoreWorkerModel.getInstance();

        // getting the unique max id for the workers
        int workerMaxId = mSharedPreferences.getInt(mResources.getString(R.string.worker_max_id), -1);

        // if there is information about workers
        if (workerMaxId != -1) {

            // creating workers model and setting the unique max id
            storeWorkerModel.setMaxId(workerMaxId);

            // create all the workers in the model
            for (int i = 0; i < workerMaxId + 1; i++) {
                String workersName = mSharedPreferences.getString(mResources.getString(R.string.worker) + i, "");
                if (!workersName.equals("")) {
                    storeWorkerModel.addStoreWorker(workersName, i);
                }
            }
        }

        // if there is nothing about workers in the shared prefs, creating the model and a default worker
        // then putting it in the shared prefs
        else {
            storeWorkerModel.setMaxId(0);
            storeWorkerModel.addStoreWorker(mResources.getString(R.string.worker), 0);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt(mResources.getString(R.string.worker_max_id), 0);
            editor.putString(mResources.getString(R.string.worker) + 0, mResources.getString(R.string.worker));
            editor.apply();
        }
    }

    /**
     * Method used to get information about the tasks and put it in the corresponding model.
     */
    private void createTasks() {
        StoreTaskModel storeTaskModel = StoreTaskModel.getInstance();

        // getting the unique max id for the tasks
        int taskMaxId = mSharedPreferences.getInt(mResources.getString(R.string.task_max_id), -1);

        // if there is information about tasks
        if (taskMaxId != -1) {

            // creating tasks model and setting the unique max id
            storeTaskModel.setMaxId(taskMaxId);

            // create all the tasks in the model
            for (int i = 0; i < taskMaxId + 1; i++) {
                String tasksName = mSharedPreferences.getString(mResources.getString(R.string.task) + i, "");
                int tasksDuration = mSharedPreferences.getInt(mResources.getString(R.string.task) + i + mResources.getString(R.string.duration), 0);
                if (!tasksName.equals("") && tasksDuration != 0) {
                    storeTaskModel.addStoreTask(tasksName, tasksDuration, i);
                }
            }
        }

        // if there is nothing about tasks in the shared prefs, creating the model and a default task
        // then putting it in the shared prefs
        else {
            storeTaskModel.setMaxId(0);
            storeTaskModel.addStoreTask(mResources.getString(R.string.task), mResources.getInteger(R.integer.default_task_duration), 0);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt(mResources.getString(R.string.task_max_id), 0);
            editor.putString(mResources.getString(R.string.task) + 0, mResources.getString(R.string.task));
            editor.putInt(mResources.getString(R.string.task) + 0 + mResources.getString(R.string.duration), mResources.getInteger(R.integer.default_task_duration));
            editor.apply();
        }
    }

    /**
     * Method used to get store information from the store model.
     */
    private void getStoreInfo() {
        StoreModel storeModel = StoreModel.getInstance();

        // setting the min and max calendars
        int minHour = mResources.getInteger(R.integer.minimum_starting_hour);
        int minMinute = mResources.getInteger(R.integer.minimum_starting_minute);
        storeModel.setMinTime(minHour, minMinute);
        int maxHour = mResources.getInteger(R.integer.maximum_ending_hour);
        int maxMinute = mResources.getInteger(R.integer.maximum_ending_minute);
        storeModel.setMaxTime(maxHour, maxMinute);

        // getting saved starting time in the shared prefs and putting it in the model
        int startingHour = mSharedPreferences.getInt(mResources.getString(R.string.starting_hour),
                mResources.getInteger(R.integer.default_starting_hour));
        int startingMinute = mSharedPreferences.getInt(mResources.getString(R.string.starting_minute),
                mResources.getInteger(R.integer.default_starting_minute));
        storeModel.setStartingTime(startingHour, startingMinute);

        // getting saved ending time in the shared prefs and putting it in the model
        int endingHour = mSharedPreferences.getInt(mResources.getString(R.string.ending_hour),
                mResources.getInteger(R.integer.default_ending_hour));
        int endingMinute = mSharedPreferences.getInt(mResources.getString(R.string.ending_minute),
                mResources.getInteger(R.integer.default_ending_minute));
        storeModel.setEndingTime(endingHour, endingMinute);

    }

}
