package fr.goui.storeorganizer;

import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class AppointmentEditionActivity extends AppointmentCreationActivity {

    public static final String INTENT_EXTRA_OLD_WORKER_POSITION = "intent_extra_old_worker_position";
    public static final String INTENT_EXTRA_NEW_WORKER_POSITION = "intent_extra_new_worker_position";
    public static final String INTENT_EXTRA_OLD_APPOINTMENT_POSITION = "intent_extra_appointment_position";

    private StoreAppointment _oldAppointment;
    private StoreWorker _oldWorker;
    private StoreTask _oldTask;
    private StoreTask _newTask;
    private boolean _hasWorkerChanged;
    private boolean _isAGap;
    private int _oldAppointmentPosition;
    private int _oldWorkerPosition;
    private int _newWorkerPosition;

    @Override
    protected void init() {
        _oldWorkerPosition = getIntent().getIntExtra(DetailsFragment.INTENT_EXTRA_WORKER_POSITION, -1);
        _oldAppointmentPosition = getIntent().getIntExtra(DetailsFragment.INTENT_EXTRA_APPOINTMENT_POSITION, -1);
        if (_oldWorkerPosition != -1) {
            _oldWorker = StoreWorkerModel.getInstance().getStoreWorker(_oldWorkerPosition);
            if (_oldAppointmentPosition != -1) {
                _oldAppointment = _oldWorker.getStoreAppointment(_oldAppointmentPosition);
                _newAppointment = new StoreAppointment();
                _oldTask = _oldAppointment.getStoreTask();
                int taskPosition = -1;
                if (_oldTask == null) {
                    _isAGap = true;
                    _hasWorkerChanged = false;
                } else {
                    _isAGap = false;
                    for (int i = 0; i < StoreTaskModel.getInstance().getStoreTaskNumber(); i++) {
                        if (_oldTask.equals(StoreTaskModel.getInstance().getStoreTask(i))) {
                            taskPosition = i;
                        }
                    }
                }

                fillInformation(_oldWorkerPosition, taskPosition);
            }
        }
    }

    @Override
    protected void onWorkerSelected(int position_p) {
        _newWorker = StoreWorkerModel.getInstance().getStoreWorker(position_p);
        _newWorkerPosition = position_p;
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
                _newAppointment.setStartTime(_oldAppointment.getStartTime());
            } else {
                StoreAppointment appointment = _newWorker.getNextAvailability();
                Calendar calendar = _now;
                if (appointment != null) {
                    if (appointment instanceof StoreAppointment.NullStoreAppointment) {
                        calendar = appointment.getStartTime();
                    } else {
                        calendar = appointment.getEndTime();
                    }
                }
                _newAppointment.setStartTime(calendar);
            }
            _txtStartTime.setText(_newAppointment.getFormattedStartTime());
            _txtEndTime.setText(_newAppointment.getFormattedEndTime());
        }
    }

    private void fillInformation(int workerPosition_p, int taskPosition_p) {
        _etClientsName.setText(_oldAppointment.getClientName());
        _etClientsPhoneNumber.setText(_oldAppointment.getClientPhoneNumber());
        _spinnerWorker.setSelection(workerPosition_p);
        if (_isAGap) {
            _spinnerWorker.setEnabled(false);
            _spinnerWorker.setBackgroundResource(R.color.light_grey);
        } else {
            _spinnerTask.setSelection(taskPosition_p);
        }
        _txtStartTime.setText(_oldAppointment.getFormattedStartTime());
        _txtEndTime.setText(_oldAppointment.getFormattedEndTime());
    }

    @Override
    protected void updateTimes() {
        if (_isStartTimeModified) {
            _newAppointment.setStartTime(_calendarTime);
            _txtStartTime.setText(_newAppointment.getFormattedStartTime());
            _txtEndTime.setText(_newAppointment.getFormattedEndTime());
        } else {
            _newAppointment.setEndTime(_calendarTime);
            _txtEndTime.setText(_newAppointment.getFormattedEndTime());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = false;
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            ret = true;
        } else if (id == R.id.action_validate_prestation) {
            if (confirmAppointment()) {
                Intent intent = new Intent();
                intent.putExtra(INTENT_EXTRA_OLD_WORKER_POSITION, _oldWorkerPosition);
                intent.putExtra(INTENT_EXTRA_NEW_WORKER_POSITION, _newWorkerPosition);
                intent.putExtra(INTENT_EXTRA_OLD_APPOINTMENT_POSITION, _oldAppointmentPosition);
                setResult(RESULT_OK, intent);
                finish();
            }
            ret = true;
        }
        return ret;
    }

    @Override
    protected boolean confirmAppointment() {
        // TODO simplify appointment edition
        boolean result = true;
        String errorMessage = checkValidity();
        if (errorMessage == null) {
            _newAppointment.setClientName(_etClientsName.getText().toString());
            _newAppointment.setClientPhoneNumber(_etClientsPhoneNumber.getText().toString());
            if (_isAGap) {
                // if the new appointment replace the gap so there is still a gap before and not after, update the gap and create new appointment after
                if ((_newAppointment.getStartTime().getTimeInMillis() - _oldAppointment.getStartTime().getTimeInMillis()) / 60000
                        >= StoreTaskModel.getInstance().getMinTimeInMinutes()
                        && !((_oldAppointment.getEndTime().getTimeInMillis() - _newAppointment.getEndTime().getTimeInMillis()) / 60000
                        >= StoreTaskModel.getInstance().getMinTimeInMinutes())) {
                    _oldAppointment.setEndTime(_newAppointment.getStartTime());
                    _newWorker.getStoreAppointments().add(_oldAppointmentPosition + 1, _newAppointment);
                }
                // if the new appointment replace the gap so there is still a gap after and not before, update the gap and create new appointment before
                else if ((_oldAppointment.getEndTime().getTimeInMillis() - _newAppointment.getEndTime().getTimeInMillis()) / 60000
                        >= StoreTaskModel.getInstance().getMinTimeInMinutes()
                        && !((_newAppointment.getStartTime().getTimeInMillis() - _oldAppointment.getStartTime().getTimeInMillis()) / 60000
                        >= StoreTaskModel.getInstance().getMinTimeInMinutes())) {
                    _oldAppointment.setStartTime(_newAppointment.getEndTime());
                    _newWorker.getStoreAppointments().add(_oldAppointmentPosition, _newAppointment);
                }
                // if the new appointment replace the gap so there is still a gap before and after, update the gap, create new appointment and new gap after
                else if ((_oldAppointment.getEndTime().getTimeInMillis() - _newAppointment.getEndTime().getTimeInMillis()) / 60000
                        >= StoreTaskModel.getInstance().getMinTimeInMinutes()
                        && (_newAppointment.getStartTime().getTimeInMillis() - _oldAppointment.getStartTime().getTimeInMillis()) / 60000
                        >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                    StoreAppointment.NullStoreAppointment nullStoreAppointment = _newAppointment.newNullInstance();
                    nullStoreAppointment.setStartTime(_newAppointment.getEndTime());
                    nullStoreAppointment.setEndTime(_oldAppointment.getEndTime());
                    _newWorker.getStoreAppointments().add(_oldAppointmentPosition + 1, nullStoreAppointment);
                    _newWorker.getStoreAppointments().add(_oldAppointmentPosition + 1, _newAppointment);
                    _oldAppointment.setEndTime(_newAppointment.getStartTime());
                }
                // if the new appointment replace the gap so there is no other gap, replace the gap with the new appointment
                else {
                    _newWorker.getStoreAppointments().set(_oldAppointmentPosition, _newAppointment);
                }
            } else {
                if (_hasWorkerChanged) {
                    // if the new appointment create an acceptable gap, create the gap and the new appointment
                    if (_newWorker.getStoreAppointmentsNumber() == 0) {
                        if (_newAppointment.getStartTime().after(_now)
                                && ((_newAppointment.getStartTime().getTimeInMillis() - _now.getTimeInMillis()) / 60000)
                                >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                            StoreAppointment.NullStoreAppointment nullStoreAppointment = _newAppointment.newNullInstance();
                            nullStoreAppointment.setStartTime(_now);
                            nullStoreAppointment.setEndTime(_newAppointment.getStartTime());
                            _newWorker.addStoreAppointment(nullStoreAppointment);
                        }
                    } else {
                        if (_newAppointment.getStartTime().after(_newWorker.getLastAppointment().getEndTime())
                                && ((_newAppointment.getStartTime().getTimeInMillis() - _newWorker.getLastAppointment().getEndTime().getTimeInMillis()) / 60000)
                                >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                            StoreAppointment.NullStoreAppointment nullStoreAppointment = _newAppointment.newNullInstance();
                            nullStoreAppointment.setStartTime(_newWorker.getLastAppointment().getEndTime());
                            nullStoreAppointment.setEndTime(_newAppointment.getStartTime());
                            _newWorker.addStoreAppointment(nullStoreAppointment);
                        }
                    }
                    _newWorker.addStoreAppointment(_newAppointment);
                } else {
                    // if there is nothing before nor after
                    if (!_oldWorker.isThereAppointmentBefore(_oldAppointmentPosition)
                            && !_oldWorker.isThereAppointmentAfter(_oldAppointmentPosition)) {
                        // if the new appointment creates an acceptable gap before, creates the gap
                        if (_newAppointment.getStartTime().after(_now)
                                && (_newAppointment.getStartTime().getTimeInMillis() - _now.getTimeInMillis()) / 60000
                                >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                            StoreAppointment.NullStoreAppointment nullStoreAppointment = _newAppointment.newNullInstance();
                            nullStoreAppointment.setStartTime(_now);
                            nullStoreAppointment.setEndTime(_newAppointment.getStartTime());
                            _oldWorker.getStoreAppointments().add(_oldAppointmentPosition, nullStoreAppointment);
                        }
                        // update the appointment
                        _oldAppointment.setStoreTask(_newTask);
                        _oldAppointment.setStartTime(_newAppointment.getStartTime());
                        _oldAppointment.setEndTime(_newAppointment.getEndTime());
                    }
                    // if there is something before and not after
                    else if (_oldWorker.isThereAppointmentBefore(_oldAppointmentPosition)
                            && !_oldWorker.isThereAppointmentAfter(_oldAppointmentPosition)) {
                        // if there is a gap before
                        if (_oldWorker.getStoreAppointment(_oldAppointmentPosition - 1) instanceof StoreAppointment.NullStoreAppointment) {
                            // if the new appointment takes time to the gap to the point the gap is no longer acceptable, remove the gap and update the appointment
                            if (((_newAppointment.getStartTime().getTimeInMillis()
                                    - _oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).getStartTime().getTimeInMillis())
                                    / 60000)
                                    < StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                _oldWorker.getStoreAppointments().remove(_oldAppointmentPosition - 1);
                            }
                            // if the new appointment takes time to the gap but it is still acceptable, update the gap and the appointment
                            else {
                                _oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).setEndTime(_newAppointment.getStartTime());
                            }
                            // update the appointment
                            _oldAppointment.setStoreTask(_newTask);
                            _oldAppointment.setStartTime(_newAppointment.getStartTime());
                            _oldAppointment.setEndTime(_newAppointment.getEndTime());
                        }
                        // if there is an appointment before
                        else {
                            // if the new appointment creates an acceptable gap, creates the gap and update the appointment
                            if (((_newAppointment.getStartTime().getTimeInMillis()
                                    - _oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).getEndTime().getTimeInMillis())
                                    / 60000)
                                    >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                StoreAppointment.NullStoreAppointment nullStoreAppointment = _newAppointment.newNullInstance();
                                nullStoreAppointment.setStartTime(_oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).getEndTime());
                                nullStoreAppointment.setEndTime(_newAppointment.getStartTime());
                                _oldWorker.getStoreAppointments().add(_oldAppointmentPosition, nullStoreAppointment);
                            }
                            _oldAppointment.setStoreTask(_newTask);
                            _oldAppointment.setStartTime(_newAppointment.getStartTime());
                            _oldAppointment.setEndTime(_newAppointment.getEndTime());
                        }
                    }
                    // if there is something after and not before
                    else if (!_oldWorker.isThereAppointmentBefore(_oldAppointmentPosition)
                            && _oldWorker.isThereAppointmentAfter(_oldAppointmentPosition)) {
                        // if there is a gap after
                        if (_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1) instanceof StoreAppointment.NullStoreAppointment) {
                            // if the new appointment takes time to the gap to the point the gap is no longer acceptable, remove the gap and update the appointment
                            if (((_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).getEndTime().getTimeInMillis()
                                    - _newAppointment.getEndTime().getTimeInMillis())
                                    / 60000)
                                    < StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                _oldWorker.getStoreAppointments().remove(_oldAppointmentPosition + 1);
                            }
                            // if the new appointment takes time to the gap but it is still acceptable, update the gap and the appointment
                            else {
                                _oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).setStartTime(_newAppointment.getEndTime());
                            }
                            // update the appointment
                            _oldAppointment.setStoreTask(_newTask);
                            _oldAppointment.setStartTime(_newAppointment.getStartTime());
                            _oldAppointment.setEndTime(_newAppointment.getEndTime());
                        }
                        // if there is an appointment after
                        else {
                            // if the new appointment creates an acceptable gap, creates the gap and update the appointment
                            if (((_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).getStartTime().getTimeInMillis()
                                    - _newAppointment.getEndTime().getTimeInMillis())
                                    / 60000)
                                    >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                StoreAppointment.NullStoreAppointment nullStoreAppointment = _newAppointment.newNullInstance();
                                nullStoreAppointment.setStartTime(_newAppointment.getEndTime());
                                nullStoreAppointment.setEndTime(_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).getStartTime());
                                _oldWorker.getStoreAppointments().add(_oldAppointmentPosition + 1, nullStoreAppointment);
                            }
                            _oldAppointment.setStoreTask(_newTask);
                            _oldAppointment.setStartTime(_newAppointment.getStartTime());
                            _oldAppointment.setEndTime(_newAppointment.getEndTime());
                        }
                    }
                    // if there is something before and after
                    else {
                        // if there is a gap before and after
                        if (_oldWorker.getStoreAppointment(_oldAppointmentPosition - 1) instanceof StoreAppointment.NullStoreAppointment
                                && _oldWorker.getStoreAppointment(_oldAppointmentPosition + 1) instanceof StoreAppointment.NullStoreAppointment) {
                            // if the new appointment consumes the gap before to the point it is not acceptable anymore, remove the gap
                            if (((_newAppointment.getStartTime().getTimeInMillis()
                                    - _oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).getStartTime().getTimeInMillis())
                                    / 60000)
                                    < StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                _oldWorker.getStoreAppointments().remove(_oldAppointmentPosition - 1);
                            }
                            // if not update the gap
                            else {
                                _oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).setEndTime(_newAppointment.getStartTime());
                            }
                            // if the new appointment consumes the gap after to the point it is not acceptable anymore, remove the gap
                            if (((_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).getEndTime().getTimeInMillis()
                                    - _newAppointment.getEndTime().getTimeInMillis())
                                    / 60000)
                                    < StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                _oldWorker.getStoreAppointments().remove(_oldAppointmentPosition + 1);
                            }
                            // if not update the gap
                            else {
                                _oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).setStartTime(_newAppointment.getEndTime());
                            }
                            // update the appointment
                            _oldAppointment.setStoreTask(_newTask);
                            _oldAppointment.setStartTime(_newAppointment.getStartTime());
                            _oldAppointment.setEndTime(_newAppointment.getEndTime());
                        }
                        // if there is a gap before and an appointment after
                        else if (_oldWorker.getStoreAppointment(_oldAppointmentPosition - 1) instanceof StoreAppointment.NullStoreAppointment
                                && !(_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1) instanceof StoreAppointment.NullStoreAppointment)) {
                            // if the new appointment consumes the gap before to the point it is not acceptable anymore, remove the gap
                            if (((_newAppointment.getStartTime().getTimeInMillis()
                                    - _oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).getStartTime().getTimeInMillis())
                                    / 60000)
                                    < StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                _oldWorker.getStoreAppointments().remove(_oldAppointmentPosition - 1);
                            }
                            // if not update the gap
                            else {
                                _oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).setEndTime(_newAppointment.getStartTime());
                            }
                            // if the new appointment creates a gap between its end time and the appointment after, create a gap
                            if (((_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).getStartTime().getTimeInMillis()
                                    - _newAppointment.getEndTime().getTimeInMillis())
                                    / 60000)
                                    >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                StoreAppointment.NullStoreAppointment nullStoreAppointment = _newAppointment.newNullInstance();
                                nullStoreAppointment.setStartTime(_newAppointment.getEndTime());
                                nullStoreAppointment.setEndTime(_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).getStartTime());
                                _oldWorker.getStoreAppointments().add(_oldAppointmentPosition + 1, nullStoreAppointment);
                            }
                            // update the appointment
                            _oldAppointment.setStoreTask(_newTask);
                            _oldAppointment.setStartTime(_newAppointment.getStartTime());
                            _oldAppointment.setEndTime(_newAppointment.getEndTime());
                        }
                        // if there is an appointment before and a gap after
                        else if (!(_oldWorker.getStoreAppointment(_oldAppointmentPosition - 1) instanceof StoreAppointment.NullStoreAppointment)
                                && _oldWorker.getStoreAppointment(_oldAppointmentPosition + 1) instanceof StoreAppointment.NullStoreAppointment) {
                            // if the new appointment consumes the gap after to the point it is not acceptable anymore, remove the gap
                            if (((_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).getEndTime().getTimeInMillis()
                                    - _newAppointment.getEndTime().getTimeInMillis())
                                    / 60000)
                                    < StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                _oldWorker.getStoreAppointments().remove(_oldAppointmentPosition + 1);
                            }
                            // if not update the gap
                            else {
                                _oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).setStartTime(_newAppointment.getEndTime());
                            }
                            // if the new appointment creates a gap between its start time and the appointment before, create a gap
                            if (((_newAppointment.getStartTime().getTimeInMillis()
                                    - _oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).getEndTime().getTimeInMillis())
                                    / 60000)
                                    >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                StoreAppointment.NullStoreAppointment nullStoreAppointment = _newAppointment.newNullInstance();
                                nullStoreAppointment.setStartTime(_oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).getEndTime());
                                nullStoreAppointment.setEndTime(_newAppointment.getStartTime());
                                _oldWorker.getStoreAppointments().add(_oldAppointmentPosition + 1, nullStoreAppointment);
                            }
                            // update the appointment
                            _oldAppointment.setStoreTask(_newTask);
                            _oldAppointment.setStartTime(_newAppointment.getStartTime());
                            _oldAppointment.setEndTime(_newAppointment.getEndTime());
                        }
                        // if there is an appointment before and after
                        else {
                            // if the new appointment creates a gap between its start time and the appointment before, create a gap
                            if (((_newAppointment.getStartTime().getTimeInMillis()
                                    - _oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).getEndTime().getTimeInMillis())
                                    / 60000)
                                    >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                StoreAppointment.NullStoreAppointment nullStoreAppointment = _newAppointment.newNullInstance();
                                nullStoreAppointment.setStartTime(_oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).getEndTime());
                                nullStoreAppointment.setEndTime(_newAppointment.getStartTime());
                                _oldWorker.getStoreAppointments().add(_oldAppointmentPosition, nullStoreAppointment);
                            }
                            // if the new appointment creates a gap between its end time and the appointment after, create a gap
                            if (((_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).getStartTime().getTimeInMillis()
                                    - _newAppointment.getEndTime().getTimeInMillis())
                                    / 60000)
                                    >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                StoreAppointment.NullStoreAppointment nullStoreAppointment = _newAppointment.newNullInstance();
                                nullStoreAppointment.setStartTime(_newAppointment.getEndTime());
                                nullStoreAppointment.setEndTime(_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).getStartTime());
                                _oldWorker.getStoreAppointments().add(_oldAppointmentPosition + 1, nullStoreAppointment);
                            }
                            // update the appointment
                            _oldAppointment.setStoreTask(_newTask);
                            _oldAppointment.setStartTime(_newAppointment.getStartTime());
                            _oldAppointment.setEndTime(_newAppointment.getEndTime());
                        }
                    }
                }
            }
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
        } else if (_newAppointment.getEndTime().before(_newAppointment.getStartTime())) {
            errorMessage = getString(R.string.ending_time_cannot_be_prior_to_starting_time);
        } else if (doesAppointmentOverlap()) {
            errorMessage = getString(R.string.appointment_overlaps_with_at_least_another_one);
        }
        if (_isAGap && _newAppointment.getStartTime().before(_now)) {
            errorMessage = getString(R.string.starting_time_cannot_be_in_the_past);
        }
        return errorMessage;
    }

    @Override
    protected boolean doesAppointmentOverlap() {
        boolean overlaps = false;
        // TODO overlapping algorithm
        return overlaps;
    }
}
