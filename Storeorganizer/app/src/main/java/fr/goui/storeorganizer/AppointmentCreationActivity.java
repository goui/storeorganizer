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
import java.util.Date;

public class AppointmentCreationActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_WORKER_POSITION_STRING_KEY = "result_intent_position_string_key";

    private StoreTask _newTask;

    protected StoreAppointment _newAppointment;
    protected StoreWorker _newWorker;
    protected int _newWorkerPosition;
    protected Date _now = new Date();
    protected EditText _etClientsName;
    protected EditText _etClientsPhoneNumber;
    protected Spinner _spinnerWorker;
    protected Spinner _spinnerTask;
    protected TextView _txtStartTime;
    protected TextView _txtEndTime;
    protected boolean _isStartTimeModified;
    protected Calendar _calendarTime = Calendar.getInstance();
    protected TimePickerDialog.OnTimeSetListener _timePickerDialogListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            _calendarTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            _calendarTime.set(Calendar.MINUTE, minute);
            updateTimes();
        }
    };

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

        init();

        _spinnerWorker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onWorkerSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        _spinnerTask.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onTaskSelected(position);
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
                new TimePickerDialog(AppointmentCreationActivity.this,
                        _timePickerDialogListener,
                        _calendarTime.get(Calendar.HOUR_OF_DAY),
                        _calendarTime.get(Calendar.MINUTE),
                        true).show();
            }
        });

        _txtEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _isStartTimeModified = false;
                new TimePickerDialog(AppointmentCreationActivity.this,
                        _timePickerDialogListener,
                        _calendarTime.get(Calendar.HOUR_OF_DAY),
                        _calendarTime.get(Calendar.MINUTE),
                        true).show();
            }
        });
    }

    protected void init() {
        _newAppointment = new StoreAppointment();
        int workerPosition = getIntent().getIntExtra(INTENT_EXTRA_WORKER_POSITION_STRING_KEY, 0);
        _spinnerWorker.setSelection(workerPosition);
        _newWorker = StoreWorkerModel.getInstance().getStoreWorker(workerPosition);
    }

    protected void onWorkerSelected(int position_p) {
        _newWorker = StoreWorkerModel.getInstance().getStoreWorker(position_p);
        _newWorkerPosition = position_p;
        updateAppointmentInformation();
    }

    protected void onTaskSelected(int position_p) {
        _newTask = StoreTaskModel.getInstance().getStoreTask(position_p);
        updateAppointmentInformation();
    }

    protected void updateAppointmentInformation() {
        if (_newTask != null) {
            _newAppointment.setStoreTask(_newTask);
            _newAppointment.setStartDate(_newWorker.getNextAvailability());
            _txtStartTime.setText(_newAppointment.getFormattedStartDate());
            _txtEndTime.setText(_newAppointment.getFormattedEndDate());
        }
    }

    protected void updateTimes() {
        if (_isStartTimeModified) {
            _newAppointment.setStartDate(_calendarTime.getTime());
            _txtStartTime.setText(_newAppointment.getFormattedStartDate());
            _txtEndTime.setText(_newAppointment.getFormattedEndDate());
        } else {
            _newAppointment.setEndDate(_calendarTime.getTime());
            _txtEndTime.setText(_newAppointment.getFormattedEndDate());
        }
    }

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
        getMenuInflater().inflate(R.menu.menu_appointment_creation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            ret = true;
        } else if (id == R.id.action_validate_prestation) {
            if (confirmAppointment()) {
                Intent intent = new Intent();
                intent.putExtra(INTENT_EXTRA_WORKER_POSITION_STRING_KEY, _newWorkerPosition);
                setResult(RESULT_OK, intent);
                finish();
            }
            ret = true;
        }
        return ret;
    }

    protected boolean confirmAppointment() {
        boolean result = true;
        String errorMessage = checkValidity();
        if (errorMessage == null) {
            _newAppointment.setClientName(_etClientsName.getText().toString());
            _newAppointment.setClientPhoneNumber(_etClientsPhoneNumber.getText().toString());
            if (_newWorker.getStoreAppointmentsNumber() == 0) {
                if (_newAppointment.getStartDate().after(_now)
                        && ((_newAppointment.getStartDate().getTime() - _now.getTime()) / 60000) >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                    StoreAppointment.NullStoreAppointment nullStoreAppointment = _newAppointment.newNullInstance();
                    nullStoreAppointment.setStartDate(_now);
                    nullStoreAppointment.setEndDate(_newAppointment.getStartDate());
                    _newWorker.addStoreAppointment(nullStoreAppointment);
                }
            } else {
                if (_newAppointment.getStartDate().after(_newWorker.getLastAppointment().getEndDate())
                        && ((_newAppointment.getStartDate().getTime() - _newWorker.getLastAppointment().getEndDate().getTime()) / 60000) >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                    StoreAppointment.NullStoreAppointment nullStoreAppointment = _newAppointment.newNullInstance();
                    nullStoreAppointment.setStartDate(_newWorker.getLastAppointment().getEndDate());
                    nullStoreAppointment.setEndDate(_newAppointment.getStartDate());
                    _newWorker.addStoreAppointment(nullStoreAppointment);
                }
            }
            _newWorker.addStoreAppointment(_newAppointment);
        } else {
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            result = false;
        }
        return result;
    }

    protected boolean doesAppointmentOverlap() {
        boolean overlaps = false;
        if (_newWorker.getStoreAppointmentsNumber() > 0) {
            StoreAppointment firstAppointment = _newWorker.getStoreAppointment(0);
            StoreAppointment lastAppointment = _newWorker.getLastAppointment();
            if (_newAppointment.getStartDate().before(firstAppointment.getStartDate())
                    && _newAppointment.getEndDate().after(firstAppointment.getStartDate())) {
                overlaps = true;
            }
            if (_newAppointment.getStartDate().before(lastAppointment.getEndDate())
                    && _newAppointment.getEndDate().after(lastAppointment.getEndDate())) {
                overlaps = true;
            }
            if (_newAppointment.getStartDate().after(firstAppointment.getStartDate())
                    && _newAppointment.getEndDate().before(lastAppointment.getEndDate())) {
                overlaps = true;
            }
        }
        return overlaps;
    }

    protected String checkValidity() {
        String errorMessage = null;
        if (_etClientsName.getText().toString().equals("")) {
            errorMessage = getString(R.string.please_specify_a_name);
        } else if (_newAppointment.getStartDate().before(_now)) {
            errorMessage = getString(R.string.starting_time_cannot_be_in_the_past);
        } else if (_newAppointment.getEndDate().before(_newAppointment.getStartDate())) {
            errorMessage = getString(R.string.ending_time_cannot_be_prior_to_starting_time);
        } else if (doesAppointmentOverlap()) {
            errorMessage = getString(R.string.appointment_overlaps_with_at_least_another_one);
        }
        return errorMessage;
    }

}
