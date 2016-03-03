package fr.goui.storeorganizer;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    private StoreTask mNewTask;

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
    private Calendar mCalendarStartingTime = Calendar.getInstance();

    /**
     * The {@code Calendar} used to manage ending time.
     */
    private Calendar mCalendarEndingTime = Calendar.getInstance();

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
        setContentView(R.layout.activity_appointment_creation);

        // setting action bar
        setupActionbar();

        // setting the result by default to canceled
        setResult(RESULT_CANCELED);

        // getting models
        mStoreWorkerModel = StoreWorkerModel.getInstance();
        mStoreTaskModel = StoreTaskModel.getInstance();

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

        // we don't want to consider seconds and milliseconds
        mNow.set(Calendar.SECOND, 0);
        mNow.set(Calendar.MILLISECOND, 0);
        mCalendarStartingTime.set(Calendar.SECOND, 0);
        mCalendarStartingTime.set(Calendar.MILLISECOND, 0);
        mCalendarEndingTime.set(Calendar.SECOND, 0);
        mCalendarEndingTime.set(Calendar.MILLISECOND, 0);
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

            // getting the next availability
            StoreAppointment appointment = mNewWorker.getNextAvailability();

            // setting the times
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, mNow.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, mNow.get(Calendar.MINUTE));
            // we don't want to consider seconds and milliseconds
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (appointment != null) {
                if (appointment instanceof NullStoreAppointment) {
                    calendar.set(Calendar.HOUR_OF_DAY, appointment.getStartTime().get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE, appointment.getStartTime().get(Calendar.MINUTE));
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, appointment.getEndTime().get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE, appointment.getEndTime().get(Calendar.MINUTE));
                }
            }
            mNewAppointment.setStartTime(calendar);
            calendar.setTimeInMillis(calendar.getTimeInMillis() + mNewAppointment.getDuration() * mConversionMillisecondMinute);
            mNewAppointment.setEndTime(calendar);

            // updating the views
            mTextViewStartingTime.setText(mNewAppointment.getFormattedStartTime());
            mTextViewEndingTime.setText(mNewAppointment.getFormattedEndTime());
        }
    }

    /**
     * When the user has changed the starting time.
     */
    protected void updateStartingTime() {
        mNewAppointment.setStartTime(mCalendarStartingTime);
        mCalendarEndingTime.setTimeInMillis(mCalendarStartingTime.getTimeInMillis() + mNewAppointment.getDuration() * mConversionMillisecondMinute);
        mNewAppointment.setEndTime(mCalendarEndingTime);
        mTextViewStartingTime.setText(mNewAppointment.getFormattedStartTime());
        mTextViewEndingTime.setText(mNewAppointment.getFormattedEndTime());
    }

    /**
     * When the user has changed the ending time.
     */
    protected void updateEndingTime() {
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
        getMenuInflater().inflate(R.menu.menu_appointment_creation, menu);
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
        if (id == R.id.action_validate_prestation) {

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

            // if this worker had no appointment scheduled and the creation leads to creating a gap
            if (mNewWorker.getStoreAppointmentsNumber() == 0 && mNewAppointment.isAfter(mNow)
                    && (mNewAppointment.gapWith(mNow) >= StoreTaskModel.getInstance().getMinTimeInMinutes())) {

                // creating the gap
                NullStoreAppointment nullStoreAppointment = new NullStoreAppointment();
                nullStoreAppointment.setStartTime(mNow.get(Calendar.HOUR_OF_DAY), mNow.get(Calendar.MINUTE));
                nullStoreAppointment.setEndTime(mNewAppointment.getStartTime());
                mNewWorker.addStoreAppointment(nullStoreAppointment);
            }

            // if this worker had at least one appointment scheduled and the creation leads to creating a gap
            else if (mNewWorker.getStoreAppointmentsNumber() > 0 && mNewAppointment.isAfter(mNewWorker.getLastAppointment())
                    && (mNewAppointment.gapWith(mNewWorker.getLastAppointment()) >= StoreTaskModel.getInstance().getMinTimeInMinutes())) {

                // creating the gap
                NullStoreAppointment nullStoreAppointment = new NullStoreAppointment();
                nullStoreAppointment.setStartTime(mNewWorker.getLastAppointment().getEndTime().get(Calendar.HOUR_OF_DAY),
                        mNewWorker.getLastAppointment().getEndTime().get(Calendar.MINUTE));
                nullStoreAppointment.setEndTime(mNewAppointment.getStartTime());
                mNewWorker.addStoreAppointment(nullStoreAppointment);
            }

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
            errorMessage = mResources.getString(R.string.appointment_overlaps_with_at_least_another_one);
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
