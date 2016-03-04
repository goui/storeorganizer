package fr.goui.storeorganizer;

import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

public class OverallActivity extends AppCompatActivity implements Observer, OnAppointmentClickListener {

    private static final float SCALE_FACTOR_MIN_VALUE = 1.0f;
    private static final float SCALE_FACTOR_MAX_VALUE = 3.0f;

    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = SCALE_FACTOR_MIN_VALUE;
    private OverallView mOverallView;
    private LinearLayout mNamesLayout;
    private Point mScreenSize = new Point();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall);

        StoreWorkerModel.getInstance().addObserver(this);
        StoreModel.getInstance().addObserver(this);
        getWindowManager().getDefaultDisplay().getSize(mScreenSize);

        mNamesLayout = (LinearLayout) findViewById(R.id.activity_overall_names_layout);
        fillNamesLayout();

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_overall_content_layout);
        mOverallView = new OverallView(this);
        mOverallView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.addView(mOverallView);

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    private void fillNamesLayout() {
        int nbOfWorkers = StoreWorkerModel.getInstance().getStoreWorkersNumber();
        int cellWidth = mScreenSize.x / (nbOfWorkers + 2);
        for (int i = 0; i < nbOfWorkers; i++) {
            TextView textView = new TextView(this);
            String name = StoreWorkerModel.getInstance().getStoreWorker(i).getName();
            textView.setText(name);
            textView.setTextColor(Color.BLACK);
            textView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            textView.setGravity(Gravity.CENTER);
            textView.measure(0, 0);
            int width = textView.getMeasuredWidth();
            if (width >= cellWidth) {
                textView.setText(String.valueOf(name.charAt(0)));
            }
            mNamesLayout.addView(textView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_overall, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(OverallActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_go_to_details) {
            Intent intent = new Intent(OverallActivity.this, DetailsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAppointmentClicked(StoreAppointment storeAppointment_p) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
        String message = storeAppointment_p.getStoreTask().getName()
                + "\n" + storeAppointment_p.getClientName()
                + "\n" + storeAppointment_p.getFormattedStartTime();
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mScaleGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof StoreWorkerModel) {
            mNamesLayout.removeAllViews();
            fillNamesLayout();
            mOverallView.onWorkersChanged();
        } else if (observable instanceof StoreModel) {
            mOverallView.onWorkingTimesChanged();
        }
    }

    class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(SCALE_FACTOR_MIN_VALUE, Math.min(mScaleFactor, SCALE_FACTOR_MAX_VALUE));
            mOverallView.onScaleChanged(mScaleFactor);
            return true;
        }
    }

}
