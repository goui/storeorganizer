package fr.goui.storeorganizer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

public class LauncherActivity extends AppCompatActivity {

    private View mContentView;

    private ProgressBar mProgressBar;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        mProgressBar = (ProgressBar) findViewById(R.id.activity_launcher_progress_bar);
        mContentView = findViewById(R.id.activity_launcher_continue_text_view);

        mProgressBar.setIndeterminate(true);

        hide();

        mContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(LauncherActivity.this, DetailsActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        });

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        new ProgressTask().execute();
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
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

    private class ProgressTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            int workerMaxId = mSharedPreferences.getInt(getString(R.string.worker_max_id), -1);
            if (workerMaxId != -1) {
                StoreWorkerModel.getInstance().setMaxId(workerMaxId);
                for (int i = 0; i < workerMaxId + 1; i++) {
                    String workersName = mSharedPreferences.getString(getString(R.string.worker) + i, "");
                    if (!workersName.equals("")) {
                        StoreWorkerModel.getInstance().addStoreWorker(workersName, i);
                    }
                }
            } else {
                StoreWorkerModel.getInstance().setMaxId(0);
                StoreWorkerModel.getInstance().addStoreWorker(getString(R.string.worker), 0);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putInt(getString(R.string.worker_max_id), 0);
                editor.putString(getString(R.string.worker) + 0, getString(R.string.worker));
                editor.apply();
            }

            int taskMaxId = mSharedPreferences.getInt(getString(R.string.task_max_id), -1);
            if (taskMaxId != -1) {
                StoreTaskModel.getInstance().setMaxId(taskMaxId);
                for (int i = 0; i < taskMaxId + 1; i++) {
                    String tasksName = mSharedPreferences.getString(getString(R.string.task) + i, "");
                    int tasksDuration = mSharedPreferences.getInt(getString(R.string.task) + i + getString(R.string.duration), 0);
                    if (!tasksName.equals("") && tasksDuration != 0) {
                        StoreTaskModel.getInstance().addStoreTask(tasksName, tasksDuration, i);
                    }
                }
            } else {
                StoreTaskModel.getInstance().setMaxId(0);
                StoreTaskModel.getInstance().addStoreTask(getString(R.string.task), 30, 0);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putInt(getString(R.string.task_max_id), 0);
                editor.putString(getString(R.string.task) + 0, getString(R.string.task));
                editor.putInt(getString(R.string.task) + 0 + getString(R.string.duration), 30);
                editor.apply();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgressBar.setVisibility(View.GONE);
            mContentView.setVisibility(View.VISIBLE);
        }
    }

}
