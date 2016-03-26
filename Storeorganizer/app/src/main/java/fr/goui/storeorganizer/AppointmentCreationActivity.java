package fr.goui.storeorganizer;

import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * {@code AppointmentCreationActivity} is an {@code Activity} used to display a gui to create a {@link StoreAppointment}.
 * The user can enter the client's name (required), the client's phone number (optional), the {@code StoreWorker} (required),
 * the {@code StoreTask} (required) and the starting and ending times (required).
 */
public class AppointmentCreationActivity extends AppCompatActivity {

    /**
     * The model managing all the {@code StoreWorker}s.
     */
    private StoreWorkerModel mStoreWorkerModel;

    /**
     * The model managing all the {@code StoreTask}s.
     */
    private StoreTaskModel mStoreTaskModel;

    /**
     * The model managing all the working times.
     */
    private StoreModel mStoreModel;

    /**
     * The chosen {@code StoreTask}.
     */
    protected StoreTask mNewTask;

    /**
     * The newly created {@code StoreAppointment}.
     */
    protected StoreAppointment mNewAppointment;

    /**
     * The chosen {@code StoreWorker}.
     */
    protected StoreWorker mNewWorker;

    /**
     * The position of the chosen {@code StoreWorker}.
     */
    protected int mNewWorkerPosition;

    /**
     * The {@code Calendar} used to keep a reference on current time.
     */
    protected Calendar mNow = Calendar.getInstance();

    /**
     * The {@code Calendar} used to copy times.
     */
    protected Calendar mTempCalendar = Calendar.getInstance();

    /**
     * The client's name {@code EditText}.
     */
    protected EditText mEditTextClientName;

    /**
     * The client's phone number {@code EditText}.
     */
    protected EditText mEditTextClientPhoneNumber;

    /**
     * The {@code Spinner} used to choose the {@code StoreWorker}.
     */
    protected Spinner mSpinnerWorker;

    /**
     * The {@code Spinner} used to choose the {@code StoreTask}.
     */
    protected Spinner mSpinnerTask;

    /**
     * The {@code TextView} used to display starting time.
     */
    protected TextView mTextViewStartingTime;

    /**
     * The {@code TextView} used to display ending time.
     */
    protected TextView mTextViewEndingTime;

    /**
     * The {@code CheckBox} used to automatize starting time.
     */
    protected CheckBox mCheckboxFrom;

    /**
     * The {@code CheckBox} used to automatize ending time.
     */
    protected CheckBox mCheckboxTo;

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

    /**
     * The time tick intent receiver to get notified every minute.
     * Will update the now {@code Calendar} and starting time.
     */
    private BroadcastReceiver mTimeTickBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                mNow.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                mNow.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
                if (mCheckboxFrom.isChecked() && mNewAppointment.getStartTime().getTimeInMillis() <= mNow.getTimeInMillis()) {
                    mCalendarStartingTime.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                    mCalendarStartingTime.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
                    updateStartingTime();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setting layout
        setContentView(R.layout.activity_appointment_creation);

        // setting action bar
        setupActionbar();

        // setting the result by default to canceled
        setResult(RESULT_CANCELED);

        // getting models
        mStoreWorkerModel = StoreWorkerModel.getInstance();
        mStoreTaskModel = StoreTaskModel.getInstance();
        mStoreModel = StoreModel.getInstance();

        // getting resources
        mResources = getResources();
        mConversionMillisecondMinute = mResources.getInteger(R.integer.conversion_millisecond_minute);

        // getting views
        mEditTextClientName = (EditText) findViewById(R.id.activity_appointment_creation_clients_name_edit_text);
        mEditTextClientPhoneNumber = (EditText) findViewById(R.id.activity_appointment_creation_clients_phone_number_edit_text);
        mSpinnerWorker = (Spinner) findViewById(R.id.activity_appointment_creation_worker_spinner);
        mSpinnerWorker.setAdapter(new WorkerBaseAdapter(this));
        mSpinnerTask = (Spinner) findViewById(R.id.activity_appointment_creation_task_spinner);
        mSpinnerTask.setAdapter(new TaskBaseAdapter(this));
        mTextViewStartingTime = (TextView) findViewById(R.id.activity_appointment_creation_start_text_view);
        mTextViewEndingTime = (TextView) findViewById(R.id.activity_appointment_creation_end_text_view);
        mCheckboxFrom = (CheckBox) findViewById(R.id.activity_appointment_creation_from_checkbox);
        mCheckboxTo = (CheckBox) findViewById(R.id.activity_appointment_creation_to_checkbox);

        // initialization method
        init();

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

        // the task selection listener
        mSpinnerTask.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onTaskSelected(position);
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
                new TimePickerDialog(AppointmentCreationActivity.this,
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
                new TimePickerDialog(AppointmentCreationActivity.this,
                        mEndTimePickerDialogListener,
                        mCalendarEndingTime.get(Calendar.HOUR_OF_DAY),
                        mCalendarEndingTime.get(Calendar.MINUTE),
                        true).show();
            }
        });

        // enabling / disabling starting text view depending on the checkbox state
        mCheckboxFrom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onCheckboxFromChangeListener(isChecked);
            }
        });

        // enabling / disabling ending text view depending on the checkbox state
        mCheckboxTo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTextViewEndingTime.setEnabled(!isChecked);
                if (isChecked) {
                    mTextViewEndingTime.setTextColor(ContextCompat.getColor(AppointmentCreationActivity.this, R.color.grey_overlay));
                    mTextViewEndingTime.setBackgroundResource(R.color.light_grey);
                    updateEndingTime();
                } else {
                    mTextViewEndingTime.setTextColor(ContextCompat.getColor(AppointmentCreationActivity.this, R.color.black));
                    int[] attrs = new int[]{R.attr.selectableItemBackground};
                    TypedArray typedArray = obtainStyledAttributes(attrs);
                    int backgroundResource = typedArray.getResourceId(0, 0);
                    mTextViewEndingTime.setBackgroundResource(backgroundResource);
                    typedArray.recycle();
                }
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

    @Override
    protected void onResume() {
        super.onResume();
        registerToTimeTickReceiver();
    }

    /**
     * Registers to the system time tick intent filter.
     *
     * @see Intent#ACTION_TIME_TICK
     */
    protected void registerToTimeTickReceiver() {
        registerReceiver(mTimeTickBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterFromTimeTickReceiver();
    }

    /**
     * Unregisters from the system time tick intent filter.
     *
     * @see Intent#ACTION_TIME_TICK
     */
    protected void unregisterFromTimeTickReceiver() {
        if (mTimeTickBroadcastReceiver != null) {
            unregisterReceiver(mTimeTickBroadcastReceiver);
        }
    }

    /**
     * Method used to initialize objects when creating the activity.
     * Extending classes should override it if they do something different.
     */
    protected void init() {

        // creating a new appointment
        mNewAppointment = new StoreAppointment();

        // getting the worker at the specified position and selecting it
        int workerPosition = getIntent().getIntExtra(mResources.getString(R.string.intent_appointment_creation_worker_position), 0);
        mSpinnerWorker.setSelection(workerPosition);
        mNewWorker = mStoreWorkerModel.getStoreWorker(workerPosition);
    }

    /**
     * Enables / disables starting text view depending on the checkbox state.
     *
     * @param isChecked the checkbox state
     */
    protected void onCheckboxFromChangeListener(boolean isChecked) {
        mTextViewStartingTime.setEnabled(!isChecked);
        if (isChecked) {
            mTextViewStartingTime.setTextColor(ContextCompat.getColor(this, R.color.grey_overlay));
            mTextViewStartingTime.setBackgroundResource(R.color.light_grey);

            // updating starting calendar to now
            mCalendarStartingTime.set(Calendar.HOUR_OF_DAY, mNow.get(Calendar.HOUR_OF_DAY));
            mCalendarStartingTime.set(Calendar.MINUTE, mNow.get(Calendar.MINUTE));
            updateStartingTime();
        } else {
            mTextViewStartingTime.setTextColor(ContextCompat.getColor(this, R.color.black));
            int[] attrs = new int[]{R.attr.selectableItemBackground};
            TypedArray typedArray = obtainStyledAttributes(attrs);
            int backgroundResource = typedArray.getResourceId(0, 0);
            mTextViewStartingTime.setBackgroundResource(backgroundResource);
            typedArray.recycle();
        }
    }

    /**
     * When a new worker is selected by the user.
     *
     * @param position_p the position of the worker
     */
    protected void onWorkerSelected(int position_p) {
        mNewWorker = mStoreWorkerModel.getStoreWorker(position_p);
        mNewWorkerPosition = position_p;
        updateAppointmentInformation();
    }

    /**
     * When a new task is selected by the user.
     *
     * @param position_p the position of the task
     */
    protected void onTaskSelected(int position_p) {
        mNewTask = mStoreTaskModel.getStoreTask(position_p);
        updateAppointmentInformation();
    }

    /**
     * Method used to update appointment information when the user has made a change.
     */
    protected void updateAppointmentInformation() {

        // if the chosen task is not null
        if (mNewTask != null) {

            // setting it to the appointment
            mNewAppointment.setStoreTask(mNewTask);

            // setting the default time
            mTempCalendar.set(Calendar.HOUR_OF_DAY, mNow.get(Calendar.HOUR_OF_DAY));
            mTempCalendar.set(Calendar.MINUTE, mNow.get(Calendar.MINUTE));

            // getting the next availability
            StoreAppointment appointment = mNewWorker.getNextAvailability();

            // if next availability is not null, getting times depending on the type of appointment
            if (appointment != null) {
                if (appointment instanceof NullStoreAppointment) {
                    mTempCalendar.set(Calendar.HOUR_OF_DAY, appointment.getStartTime().get(Calendar.HOUR_OF_DAY));
                    mTempCalendar.set(Calendar.MINUTE, appointment.getStartTime().get(Calendar.MINUTE));
                } else {
                    mTempCalendar.set(Calendar.HOUR_OF_DAY, appointment.getEndTime().get(Calendar.HOUR_OF_DAY));
                    mTempCalendar.set(Calendar.MINUTE, appointment.getEndTime().get(Calendar.MINUTE));
                }
            }

            // updating times
            mNewAppointment.setStartTime(mTempCalendar);
            mTempCalendar.setTimeInMillis(mTempCalendar.getTimeInMillis() + mNewAppointment.getDuration() * mConversionMillisecondMinute);
            mNewAppointment.setEndTime(mTempCalendar);

            // updating views
            mTextViewStartingTime.setText(mNewAppointment.getFormattedStartTime());
            mTextViewEndingTime.setText(mNewAppointment.getFormattedEndTime());

            // updating calendars
            mCalendarStartingTime.set(Calendar.HOUR_OF_DAY, mNewAppointment.getStartTime().get(Calendar.HOUR_OF_DAY));
            mCalendarStartingTime.set(Calendar.MINUTE, mNewAppointment.getStartTime().get(Calendar.MINUTE));
            mCalendarEndingTime.set(Calendar.HOUR_OF_DAY, mNewAppointment.getEndTime().get(Calendar.HOUR_OF_DAY));
            mCalendarEndingTime.set(Calendar.MINUTE, mNewAppointment.getEndTime().get(Calendar.MINUTE));
        }
    }

    /**
     * When the user has changed the starting time.
     */
    protected void updateStartingTime() {
        mNewAppointment.setStartTime(mCalendarStartingTime);
        mTextViewStartingTime.setText(mNewAppointment.getFormattedStartTime());
        if (mCheckboxTo.isChecked()) {
            updateEndingTime();
        }
    }

    /**
     * When the user has changed the ending time.
     */
    protected void updateEndingTime() {
        if (mCheckboxTo.isChecked()) {
            mCalendarEndingTime.setTimeInMillis(mCalendarStartingTime.getTimeInMillis() + mNewAppointment.getDuration() * mConversionMillisecondMinute);
        }
        mNewAppointment.setEndTime(mCalendarEndingTime);
        mTextViewEndingTime.setText(mNewAppointment.getFormattedEndTime());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // creating menu
        getMenuInflater().inflate(R.menu.menu_activity_appointment_creation, menu);
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
        if (id == R.id.action_validate_appointment) {

            // if there is no error
            if (confirmAppointment()) {

                // passing the worker position, setting the result to ok and finishing this activity
                Intent intent = new Intent();
                intent.putExtra(mResources.getString(R.string.intent_appointment_creation_result_worker_position), mNewWorkerPosition);
                setResult(RESULT_OK, intent);
                finish();
            }
            ret = true;
        }

        return ret;
    }

    /**
     * Method used to create the {@code StoreAppointment} if there is no error.
     *
     * @return {@code true} if there is an error, {@code false} otherwise
     */
    protected boolean confirmAppointment() {
        boolean result = true;

        // checking errors
        String errorMessage = checkValidity();

        // if there is no error
        if (errorMessage == null) {

            // setting the client's name and phone number
            mNewAppointment.setClientName(mEditTextClientName.getText().toString());
            mNewAppointment.setClientPhoneNumber(mEditTextClientPhoneNumber.getText().toString());

            // creating the appointment
            mNewWorker.addStoreAppointment(mNewAppointment);
        }

        // if there is an error, displaying it
        else {
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            result = false;
        }
        return result;
    }

    /**
     * Method used to check if the creation process leads to an appointment overlapping.
     *
     * @return {@code true} if there is an overlapping, {@code false} otherwise
     */
    protected boolean doesAppointmentOverlap() {
        boolean overlaps = false;

        // if there is at least one appointment
        if (mNewWorker.getStoreAppointmentsNumber() >= 1) {
            for (StoreAppointment currentAppointment : mNewWorker.getStoreAppointments()) {

                // we can only overlap an appointment
                if (!(currentAppointment instanceof NullStoreAppointment)) {
                    // being over current appointment's starting time
                    // || being over current appointment's ending time
                    // || being contained in current appointment
                    if ((mNewAppointment.getStartTime().before(currentAppointment.getStartTime())
                            && mNewAppointment.getEndTime().after(currentAppointment.getStartTime()))
                            || (mNewAppointment.getStartTime().before(currentAppointment.getEndTime())
                            && mNewAppointment.getEndTime().after(currentAppointment.getEndTime()))
                            || (mNewAppointment.getStartTime().after(currentAppointment.getStartTime())
                            && mNewAppointment.getEndTime().before(currentAppointment.getEndTime()))) {
                        overlaps = true;
                        break;
                    }
                }
            }
        }

        return overlaps;
    }

    /**
     * Method used to check if there are errors in the creation process.
     *
     * @return {@code true} if there is an error, {@code false} otherwise
     */
    protected String checkValidity() {
        String errorMessage = null;
        if (mEditTextClientName.getText().toString().equals("")) {
            errorMessage = mResources.getString(R.string.please_specify_a_name);
        } else if (mNewAppointment.isBefore(mNow)) {
            errorMessage = mResources.getString(R.string.appointment_cannot_be_in_the_past);
        } else if (mNewAppointment.getEndTime().before(mNewAppointment.getStartTime())) {
            errorMessage = mResources.getString(R.string.ending_time_cannot_be_prior_to_starting_time);
        } else if (doesAppointmentOverlap()) {
            errorMessage = mResources.getString(R.string.appointment_overlapping);
        } else if (mNewAppointment.getStartTime().before(mStoreModel.getStartingTime())) {
            errorMessage = mResources.getString(R.string.starting_time_cannot_be_before) + " "
                    + mStoreModel.getFormattedStartingTime();
        } else if (mNewAppointment.getEndTime().after(mStoreModel.getEndingTime())) {
            errorMessage = mResources.getString(R.string.ending_time_cannot_be_after) + " "
                    + mStoreModel.getFormattedEndingTime();
        }
        return errorMessage;
    }

}
