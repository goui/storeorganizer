package fr.goui.storeorganizer.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import fr.goui.storeorganizer.R;
import fr.goui.storeorganizer.adapter.WorkerBaseAdapter;
import fr.goui.storeorganizer.model.StoreModel;
import fr.goui.storeorganizer.model.StoreTaskModel;
import fr.goui.storeorganizer.model.StoreWorkerModel;

public class LunchBreakCreationActivity extends AppCompatActivity {

    /**
     * The model managing all the {@code StoreWorker}s.
     */
    private StoreWorkerModel mStoreWorkerModel;

    /**
     * The model managing all the working times.
     */
    private StoreModel mStoreModel;

    /**
     * The android {@code Resources}.
     */
    private Resources mResources;

    /**
     * The millisecond / minute conversion {@code int}.
     */
    protected int mConversionMillisecondMinute;

    /**
     * The {@code Calendar} used to manage starting time.
     */
    protected Calendar mCalendarStartingTime = Calendar.getInstance();

    /**
     * The {@code Calendar} used to manage ending time.
     */
    protected Calendar mCalendarEndingTime = Calendar.getInstance();

    /**
     * The {@code Spinner} used to choose the {@code StoreWorker}.
     */
    protected Spinner mSpinnerWorker;

    /**
     * The {@code Calendar} used to keep a reference on current time.
     */
    protected Calendar mNow = Calendar.getInstance();

    /**
     * The {@code Calendar} used to copy times.
     */
    protected Calendar mTempCalendar = Calendar.getInstance();

    /**
     * The {@code TextView} used to display starting time.
     */
    protected TextView mTextViewStartingTime;

    /**
     * The {@code TextView} used to display ending time.
     */
    protected TextView mTextViewEndingTime;

    /**
     * The time picker dialog listener for starting time.
     */
    private TimePickerDialog.OnTimeSetListener mStartTimePickerDialogListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            mCalendarStartingTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendarStartingTime.set(Calendar.MINUTE, minute);
            updateStartingTime();
        }
    };

    /**
     * The time picker dialog listener for ending time.
     */
    private TimePickerDialog.OnTimeSetListener mEndTimePickerDialogListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            mCalendarEndingTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendarEndingTime.set(Calendar.MINUTE, minute);
            updateEndingTime();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setting layout
        setContentView(R.layout.activity_lunch_break_creation);

        // setting action bar
        setupActionbar();

        // setting the result by default to canceled
        setResult(RESULT_CANCELED);

        // getting models
        mStoreWorkerModel = StoreWorkerModel.getInstance();
        mStoreModel = StoreModel.getInstance();

        // getting resources
        mResources = getResources();
        mConversionMillisecondMinute = mResources.getInteger(R.integer.conversion_millisecond_minute);

        // getting views
        mSpinnerWorker = (Spinner) findViewById(R.id.activity_lunch_break_creation_worker_spinner);
        mSpinnerWorker.setAdapter(new WorkerBaseAdapter(this));
        mTextViewStartingTime = (TextView) findViewById(R.id.activity_lunch_break_creation_start_text_view);
        mTextViewEndingTime = (TextView) findViewById(R.id.activity_lunch_break_creation_end_text_view);

        // the worker selection listener
        mSpinnerWorker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onWorkerSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        // listener to trigger the time picker dialog for the starting time
        mTextViewStartingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(LunchBreakCreationActivity.this,
                        mStartTimePickerDialogListener,
                        mCalendarStartingTime.get(Calendar.HOUR_OF_DAY),
                        mCalendarStartingTime.get(Calendar.MINUTE),
                        true).show();
            }
        });

        // listener to trigger the time picker dialog for the ending time
        mTextViewEndingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(LunchBreakCreationActivity.this,
                        mEndTimePickerDialogListener,
                        mCalendarEndingTime.get(Calendar.HOUR_OF_DAY),
                        mCalendarEndingTime.get(Calendar.MINUTE),
                        true).show();
            }
        });

        // by default times are automatized
        mTextViewStartingTime.setEnabled(false);
        mTextViewEndingTime.setEnabled(false);

        // we don't want to consider seconds and milliseconds
        mNow.set(Calendar.SECOND, 0);
        mNow.set(Calendar.MILLISECOND, 0);
        mTempCalendar.set(Calendar.SECOND, 0);
        mTempCalendar.set(Calendar.MILLISECOND, 0);
        mCalendarStartingTime.set(Calendar.SECOND, 0);
        mCalendarStartingTime.set(Calendar.MILLISECOND, 0);
        mCalendarEndingTime.set(Calendar.SECOND, 0);
        mCalendarEndingTime.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Using the up navigation for this activity.
     */
    private void setupActionbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }
    }

    /**
     * When a new worker is selected by the user.
     *
     * @param position the position of the worker
     */
    protected void onWorkerSelected(int position) {
        // TODO
    }

    /**
     * When the user has changed the starting time.
     */
    protected void updateStartingTime() {
        // TODO
    }

    /**
     * When the user has changed the ending time.
     */
    protected void updateEndingTime() {
        // TODO
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // creating menu
        getMenuInflater().inflate(R.menu.menu_activity_lunch_break_creation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = super.onOptionsItemSelected(item);
        int id = item.getItemId();

        // if we press the up button, going back
        if (id == android.R.id.home) {
            onBackPressed();
            ret = true;
        }

        // trying to validate the appointment creation
        if (id == R.id.action_validate_lunch_break) {

            // TODO
            ret = true;
        }

        return ret;
    }
}
