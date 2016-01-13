package fr.goui.storeorganizer;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        mProgressBar = (ProgressBar)findViewById(R.id.activity_launcher_progress_bar);
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

    private class ProgressTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // TODO load shared prefs or fill them with default file
            // TODO load models with shared prefs
            StoreWorkerModel.getInstance().setMaxId(2);
            StoreWorkerModel.getInstance().addStoreWorker("Worker1", 0);
            StoreWorkerModel.getInstance().addStoreWorker("Worker2", 1);
            StoreWorkerModel.getInstance().addStoreWorker("Worker3", 2);

            StoreTaskModel.getInstance().setMaxId(2);
            StoreTaskModel.getInstance().addStoreTask("Task1", 30, 0);
            StoreTaskModel.getInstance().addStoreTask("Task2", 45, 1);
            StoreTaskModel.getInstance().addStoreTask("Task3", 60, 2);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgressBar.setVisibility(View.GONE);
            mContentView.setVisibility(View.VISIBLE);
        }
    }

}
