package fr.goui.storeorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.Observable;
import java.util.Observer;

public class OverallActivity extends AppCompatActivity implements Observer {

    private static final float SCALE_FACTOR_MIN_VALUE = 1.0f;
    private static final float SCALE_FACTOR_MAX_VALUE = 3.0f;

    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = SCALE_FACTOR_MIN_VALUE;
    private OverallView mOverallView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall);

        StoreWorkerModel.getInstance().addObserver(this);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_overall_content_layout);
        mOverallView = new OverallView(this);
        mOverallView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.addView(mOverallView);

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mScaleGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof StoreWorkerModel) {
            mOverallView.onWorkersChanged();
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
