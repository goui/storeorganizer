package fr.goui.storeorganizer;

import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

public class AppointmentEditionActivity extends AppointmentCreationActivity {

    public static final String INTENT_EXTRA_WORKER_CHANGED = "intent_extra_worker_changed";

    private StoreAppointment _oldAppointment;
    private StoreAppointment _newAppointment;
    private StoreWorker _oldWorker;
    private StoreWorker _newWorker;
    private StoreTask _oldTask;
    private StoreTask _newTask;
    private boolean _hasWorkerChanged;

    @Override
    protected void init() {
        int workerPosition = getIntent().getIntExtra(DetailsFragment.INTENT_EXTRA_WORKER_POSITION, -1);
        int appointmentPosition = getIntent().getIntExtra(DetailsFragment.INTENT_EXTRA_APPOINTMENT_POSITION, -1);
        if (workerPosition != -1) {
            _oldWorker = StoreWorkerModel.getInstance().getStoreWorker(workerPosition);
            if (appointmentPosition != -1) {
                _oldAppointment = _oldWorker.getStoreAppointment(appointmentPosition);
                _newAppointment = new StoreAppointment();
                _oldTask = _oldAppointment.getStoreTask();
                int taskPosition = -1;
                if (_oldTask != null) {
                    for (int i = 0; i < StoreTaskModel.getInstance().getStoreTaskNumber(); i++) {
                        if (_oldTask.equals(StoreTaskModel.getInstance().getStoreTask(i))) {
                            taskPosition = i;
                        }
                    }
                }

                fillInformation(workerPosition, taskPosition);
            }
        }
    }

    @Override
    protected void onWorkerSelected(int position_p) {
        _newWorker = StoreWorkerModel.getInstance().getStoreWorker(position_p);
        updateAppointmentInformation();
    }

    @Override
    protected void onTaskSelected(int position_p) {
        _newTask = StoreTaskModel.getInstance().getStoreTask(position_p);
        updateAppointmentInformation();
    }

    @Override
    protected void updateAppointmentInformation() {
        if (_newTask != null) {
            _newAppointment.setStoreTask(_newTask);
            if (_oldWorker.equals(_newWorker)) {
                _newAppointment.setStartDate(_oldAppointment.getStartDate());
            } else {
                _newAppointment.setStartDate(_newWorker.getNextAvailability());
            }
            _txtStartTime.setText(_newAppointment.getFormattedStartDate());
            _txtEndTime.setText(_newAppointment.getFormattedEndDate());
        }
    }

    private void fillInformation(int workerPosition_p, int taskPosition_p) {
        _etClientsName.setText(_oldAppointment.getClientName());
        _etClientsPhoneNumber.setText(_oldAppointment.getClientPhoneNumber());
        _spinnerWorker.setSelection(workerPosition_p);
        if (_oldTask != null) {
            _spinnerTask.setSelection(taskPosition_p);
        } else {
            _spinnerWorker.setEnabled(false);
            _spinnerWorker.setBackgroundResource(R.color.light_grey);
        }
        _txtStartTime.setText(_oldAppointment.getFormattedStartDate());
        _txtEndTime.setText(_oldAppointment.getFormattedEndDate());
    }

    @Override
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_validate_prestation) {
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

    @Override
    protected boolean confirmAppointment() {
        boolean result = true;
        String errorMessage = checkValidity();
        if (errorMessage == null) {
            _oldAppointment.setClientName(_etClientsName.getText().toString());
            _oldAppointment.setClientPhoneNumber(_etClientsPhoneNumber.getText().toString());
            _oldAppointment.setStoreTask(_newTask);
            _oldAppointment.setStartDate(_newAppointment.getStartDate());
            _oldAppointment.setEndDate(_newAppointment.getEndDate());
            if (!_oldWorker.equals(_newWorker)) {
                _oldWorker.removeStoreAppointment(_oldAppointment);
                _newWorker.addStoreAppointment(_oldAppointment);
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

    @Override
    protected String checkValidity() {
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

    @Override
    protected boolean doesAppointmentOverlap() {
        return false;
    }
}
