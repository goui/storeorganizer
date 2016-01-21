package fr.goui.storeorganizer;

import android.app.TimePickerDialog;
import android.content.Intent;
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

public class AppointmentEditionActivity extends AppCompatActivity {

    public static final String RESULT_INTENT_SORTED_STRING_KEY = "result_intent_sorted_string_key";
    public static final String INTENT_EXTRA_WORKER_CHANGED = "intent_extra_worker_changed";

    private EditText _etClientsName;
    private EditText _etClientsPhoneNumber;
    private Spinner _spinnerWorker;
    private Spinner _spinnerTask;
    private TextView _txtStartTime;
    private TextView _txtEndTime;

    private StoreAppointment _oldAppointment;
    private StoreAppointment _newAppointment;
    private StoreWorker _oldWorker;
    private StoreWorker _newWorker;
    private StoreTask _oldTask;
    private StoreTask _newTask;

    private Calendar _calendarTime = Calendar.getInstance();
    private boolean _isStartTimeModified;
    private boolean _hasWorkerChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_creation);
        setupActionbar();
        setResult(RESULT_CANCELED);

        _etClientsName = (EditText) findViewById(R.id.activity_appointment_creation_clients_name_edit_text);
        _etClientsPhoneNumber = (EditText) findViewById(R.id.activity_appointment_creation_clients_phone_number_edit_text);
        _spinnerWorker = (Spinner) findViewById(R.id.activity_appointment_creation_worker_spinner);
        _spinnerWorker.setAdapter(new WorkerBaseAdapter(this, StoreWorkerModel.getInstance().getStoreWorkers()));
        _spinnerTask = (Spinner) findViewById(R.id.activity_appointment_creation_task_spinner);
        _spinnerTask.setAdapter(new TaskBaseAdapter(this, StoreTaskModel.getInstance().getStoreTasks()));
        _txtStartTime = (TextView) findViewById(R.id.activity_appointment_creation_start_text_view);
        _txtEndTime = (TextView) findViewById(R.id.activity_appointment_creation_end_text_view);

        int workerPosition = getIntent().getIntExtra(DetailsFragment.INTENT_EXTRA_WORKER_POSITION, -1);
        int appointmentPosition = getIntent().getIntExtra(DetailsFragment.INTENT_EXTRA_APPOINTMENT_POSITION, -1);
        if (workerPosition != -1) {
            _oldWorker = StoreWorkerModel.getInstance().getStoreWorker(workerPosition);
            if (appointmentPosition != -1) {
                _oldAppointment = _oldWorker.getStoreAppointment(appointmentPosition);
                _newAppointment = new StoreAppointment();
                _oldTask = _oldAppointment.getStoreTask();
                int taskPosition = -1;
                for (int i = 0; i < StoreTaskModel.getInstance().getStoreTaskNumber(); i++) {
                    if (_oldTask.equals(StoreTaskModel.getInstance().getStoreTask(i))) {
                        taskPosition = i;
                    }
                }

                fillInformation(workerPosition, taskPosition);
            }
        }

        _spinnerWorker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _newWorker = StoreWorkerModel.getInstance().getStoreWorker(position);
                updateAppointmentInformation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        _spinnerTask.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _newTask = StoreTaskModel.getInstance().getStoreTask(position);
                updateAppointmentInformation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        _txtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _isStartTimeModified = true;
                new TimePickerDialog(AppointmentEditionActivity.this,
                        timePickerDialogListener,
                        _calendarTime.get(Calendar.HOUR_OF_DAY),
                        _calendarTime.get(Calendar.MINUTE),
                        true).show();
            }
        });

        _txtEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _isStartTimeModified = false;
                new TimePickerDialog(AppointmentEditionActivity.this,
                        timePickerDialogListener,
                        _calendarTime.get(Calendar.HOUR_OF_DAY),
                        _calendarTime.get(Calendar.MINUTE),
                        true).show();
            }
        });

    }

    private void updateAppointmentInformation() {
        if (_newTask != null) {
            _newAppointment.setStoreTask(_newTask);
            _newAppointment.setStartDate(_newWorker.getNextAvailability(_newWorker.equals(_oldWorker)));
            _txtStartTime.setText(_newAppointment.getFormattedStartDate());
            _txtEndTime.setText(_newAppointment.getFormattedEndDate());
        }
    }

    private void fillInformation(int workerPosition_p, int taskPosition_p) {
        _etClientsName.setText(_oldAppointment.getClientName());
        _etClientsPhoneNumber.setText(_oldAppointment.getClientPhoneNumber());
        _spinnerWorker.setSelection(workerPosition_p);
        _spinnerTask.setSelection(taskPosition_p);
        _txtStartTime.setText(_oldAppointment.getFormattedStartDate());
        _txtEndTime.setText(_oldAppointment.getFormattedEndDate());
    }

    TimePickerDialog.OnTimeSetListener timePickerDialogListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            _calendarTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            _calendarTime.set(Calendar.MINUTE, minute);
            updateTimes();
        }
    };

    private void updateTimes() {
        if (_isStartTimeModified) {
            _newAppointment.setStartDate(_calendarTime.getTime());
            _txtStartTime.setText(_newAppointment.getFormattedStartDate());
            _txtEndTime.setText(_newAppointment.getFormattedEndDate());
        } else {
            _newAppointment.setEndDate(_calendarTime.getTime());
            _txtEndTime.setText(_newAppointment.getFormattedEndDate());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_appointment_creation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_add_prestation) {
            if (confirmAppointment()) {
                Intent intent = new Intent();
                intent.putExtra(INTENT_EXTRA_WORKER_CHANGED, _hasWorkerChanged);
                setResult(RESULT_OK, intent);
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean confirmAppointment() {
        boolean result = true;
        boolean isSortingNeeded = false;
        String errorMessage = checkValidity();
        if (errorMessage == null) {
            _oldAppointment.setClientName(_etClientsName.getText().toString());
            _oldAppointment.setClientPhoneNumber(_etClientsPhoneNumber.getText().toString());
            _oldAppointment.setStoreTask(_newTask);
            _oldAppointment.setStartDate(_newAppointment.getStartDate());
            _oldAppointment.setEndDate(_newAppointment.getEndDate());
            if(!_oldWorker.equals(_newWorker)) {
                _oldWorker.removeStoreAppointment(_oldAppointment);
                _newWorker.addStoreAppointment(_oldAppointment, isSortingNeeded);
                _hasWorkerChanged = true;
            } else {
                _hasWorkerChanged = false;
            }
            // TODO specify if the list of appointments should be sorted or not (filling holes in schedule)
        } else {
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            result = false;
        }
        return result;
    }

    private boolean doesAppointmentOverlap() {
        // TODO overlapping algorithm
        return false;
    }

    private String checkValidity() {
        String errorMessage = null;
        if (_etClientsName.getText().toString().equals("")) {
            errorMessage = getString(R.string.please_specify_a_name);
        } else if (_newAppointment.getEndDate().before(_newAppointment.getStartDate())) {
            errorMessage = getString(R.string.ending_time_cannot_be_prior_to_starting_time);
        } else if (doesAppointmentOverlap()) {
            errorMessage = getString(R.string.appointment_overlaps_with_at_least_another_one);
        }
        return errorMessage;
    }

    private void setupActionbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

}
