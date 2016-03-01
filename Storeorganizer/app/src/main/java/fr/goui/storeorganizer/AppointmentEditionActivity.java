package fr.goui.storeorganizer;

import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Calendar;

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
                mNewAppointment = new StoreAppointment();
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
        mNewWorker = StoreWorkerModel.getInstance().getStoreWorker(position_p);
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
            mNewAppointment.setStoreTask(_newTask);
            if (_oldWorker.equals(mNewWorker)) {
                mNewAppointment.setStartTime(_oldAppointment.getStartTime());
            } else {
                StoreAppointment appointment = mNewWorker.getNextAvailability();
                Calendar calendar = mNow;
                if (appointment != null) {
                    if (appointment instanceof NullStoreAppointment) {
                        calendar = appointment.getStartTime();
                    } else {
                        calendar = appointment.getEndTime();
                    }
                }
                mNewAppointment.setStartTime(calendar);
            }
            mTextViewStartingTime.setText(mNewAppointment.getFormattedStartTime());
            mTextViewEndingTime.setText(mNewAppointment.getFormattedEndTime());
        }
    }

    private void fillInformation(int workerPosition_p, int taskPosition_p) {
        mEditTextClientName.setText(_oldAppointment.getClientName());
        mEditTextClientPhoneNumber.setText(_oldAppointment.getClientPhoneNumber());
        mSpinnerWorker.setSelection(workerPosition_p);
        if (_isAGap) {
            mSpinnerWorker.setEnabled(false);
            mSpinnerWorker.setBackgroundResource(R.color.light_grey);
        } else {
            mSpinnerTask.setSelection(taskPosition_p);
        }
        mTextViewStartingTime.setText(_oldAppointment.getFormattedStartTime());
        mTextViewEndingTime.setText(_oldAppointment.getFormattedEndTime());
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
            mNewAppointment.setClientName(mEditTextClientName.getText().toString());
            mNewAppointment.setClientPhoneNumber(mEditTextClientPhoneNumber.getText().toString());
            if (_isAGap) {
                // if the new appointment replace the gap so there is still a gap before and not after, update the gap and create new appointment after
                if ((mNewAppointment.getStartTime().getTimeInMillis() - _oldAppointment.getStartTime().getTimeInMillis()) / 60000
                        >= StoreTaskModel.getInstance().getMinTimeInMinutes()
                        && !((_oldAppointment.getEndTime().getTimeInMillis() - mNewAppointment.getEndTime().getTimeInMillis()) / 60000
                        >= StoreTaskModel.getInstance().getMinTimeInMinutes())) {
                    _oldAppointment.setEndTime(mNewAppointment.getStartTime());
                    mNewWorker.getStoreAppointments().add(_oldAppointmentPosition + 1, mNewAppointment);
                }
                // if the new appointment replace the gap so there is still a gap after and not before, update the gap and create new appointment before
                else if ((_oldAppointment.getEndTime().getTimeInMillis() - mNewAppointment.getEndTime().getTimeInMillis()) / 60000
                        >= StoreTaskModel.getInstance().getMinTimeInMinutes()
                        && !((mNewAppointment.getStartTime().getTimeInMillis() - _oldAppointment.getStartTime().getTimeInMillis()) / 60000
                        >= StoreTaskModel.getInstance().getMinTimeInMinutes())) {
                    _oldAppointment.setStartTime(mNewAppointment.getEndTime());
                    mNewWorker.getStoreAppointments().add(_oldAppointmentPosition, mNewAppointment);
                }
                // if the new appointment replace the gap so there is still a gap before and after, update the gap, create new appointment and new gap after
                else if ((_oldAppointment.getEndTime().getTimeInMillis() - mNewAppointment.getEndTime().getTimeInMillis()) / 60000
                        >= StoreTaskModel.getInstance().getMinTimeInMinutes()
                        && (mNewAppointment.getStartTime().getTimeInMillis() - _oldAppointment.getStartTime().getTimeInMillis()) / 60000
                        >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                    NullStoreAppointment nullStoreAppointment = new NullStoreAppointment();
                    nullStoreAppointment.setStartTime(mNewAppointment.getEndTime());
                    nullStoreAppointment.setEndTime(_oldAppointment.getEndTime());
                    mNewWorker.getStoreAppointments().add(_oldAppointmentPosition + 1, nullStoreAppointment);
                    mNewWorker.getStoreAppointments().add(_oldAppointmentPosition + 1, mNewAppointment);
                    _oldAppointment.setEndTime(mNewAppointment.getStartTime());
                }
                // if the new appointment replace the gap so there is no other gap, replace the gap with the new appointment
                else {
                    mNewWorker.getStoreAppointments().set(_oldAppointmentPosition, mNewAppointment);
                }
            } else {
                if (_hasWorkerChanged) {
                    // if the new appointment create an acceptable gap, create the gap and the new appointment
                    if (mNewWorker.getStoreAppointmentsNumber() == 0) {
                        if (mNewAppointment.getStartTime().after(mNow)
                                && ((mNewAppointment.getStartTime().getTimeInMillis() - mNow.getTimeInMillis()) / 60000)
                                >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                            NullStoreAppointment nullStoreAppointment = new NullStoreAppointment();
                            nullStoreAppointment.setStartTime(mNow);
                            nullStoreAppointment.setEndTime(mNewAppointment.getStartTime());
                            mNewWorker.addStoreAppointment(nullStoreAppointment);
                        }
                    } else {
                        if (mNewAppointment.getStartTime().after(mNewWorker.getLastAppointment().getEndTime())
                                && ((mNewAppointment.getStartTime().getTimeInMillis() - mNewWorker.getLastAppointment().getEndTime().getTimeInMillis()) / 60000)
                                >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                            NullStoreAppointment nullStoreAppointment = new NullStoreAppointment();
                            nullStoreAppointment.setStartTime(mNewWorker.getLastAppointment().getEndTime());
                            nullStoreAppointment.setEndTime(mNewAppointment.getStartTime());
                            mNewWorker.addStoreAppointment(nullStoreAppointment);
                        }
                    }
                    mNewWorker.addStoreAppointment(mNewAppointment);
                } else {
                    // if there is nothing before nor after
                    if (!_oldWorker.isThereAppointmentBefore(_oldAppointmentPosition)
                            && !_oldWorker.isThereAppointmentAfter(_oldAppointmentPosition)) {
                        // if the new appointment creates an acceptable gap before, creates the gap
                        if (mNewAppointment.getStartTime().after(mNow)
                                && (mNewAppointment.getStartTime().getTimeInMillis() - mNow.getTimeInMillis()) / 60000
                                >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                            NullStoreAppointment nullStoreAppointment = new NullStoreAppointment();
                            nullStoreAppointment.setStartTime(mNow);
                            nullStoreAppointment.setEndTime(mNewAppointment.getStartTime());
                            _oldWorker.getStoreAppointments().add(_oldAppointmentPosition, nullStoreAppointment);
                        }
                        // update the appointment
                        _oldAppointment.setStoreTask(_newTask);
                        _oldAppointment.setStartTime(mNewAppointment.getStartTime());
                        _oldAppointment.setEndTime(mNewAppointment.getEndTime());
                    }
                    // if there is something before and not after
                    else if (_oldWorker.isThereAppointmentBefore(_oldAppointmentPosition)
                            && !_oldWorker.isThereAppointmentAfter(_oldAppointmentPosition)) {
                        // if there is a gap before
                        if (_oldWorker.getStoreAppointment(_oldAppointmentPosition - 1) instanceof NullStoreAppointment) {
                            // if the new appointment takes time to the gap to the point the gap is no longer acceptable, remove the gap and update the appointment
                            if (((mNewAppointment.getStartTime().getTimeInMillis()
                                    - _oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).getStartTime().getTimeInMillis())
                                    / 60000)
                                    < StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                _oldWorker.getStoreAppointments().remove(_oldAppointmentPosition - 1);
                            }
                            // if the new appointment takes time to the gap but it is still acceptable, update the gap and the appointment
                            else {
                                _oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).setEndTime(mNewAppointment.getStartTime());
                            }
                            // update the appointment
                            _oldAppointment.setStoreTask(_newTask);
                            _oldAppointment.setStartTime(mNewAppointment.getStartTime());
                            _oldAppointment.setEndTime(mNewAppointment.getEndTime());
                        }
                        // if there is an appointment before
                        else {
                            // if the new appointment creates an acceptable gap, creates the gap and update the appointment
                            if (((mNewAppointment.getStartTime().getTimeInMillis()
                                    - _oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).getEndTime().getTimeInMillis())
                                    / 60000)
                                    >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                NullStoreAppointment nullStoreAppointment = new NullStoreAppointment();
                                nullStoreAppointment.setStartTime(_oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).getEndTime());
                                nullStoreAppointment.setEndTime(mNewAppointment.getStartTime());
                                _oldWorker.getStoreAppointments().add(_oldAppointmentPosition, nullStoreAppointment);
                            }
                            _oldAppointment.setStoreTask(_newTask);
                            _oldAppointment.setStartTime(mNewAppointment.getStartTime());
                            _oldAppointment.setEndTime(mNewAppointment.getEndTime());
                        }
                    }
                    // if there is something after and not before
                    else if (!_oldWorker.isThereAppointmentBefore(_oldAppointmentPosition)
                            && _oldWorker.isThereAppointmentAfter(_oldAppointmentPosition)) {
                        // if there is a gap after
                        if (_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1) instanceof NullStoreAppointment) {
                            // if the new appointment takes time to the gap to the point the gap is no longer acceptable, remove the gap and update the appointment
                            if (((_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).getEndTime().getTimeInMillis()
                                    - mNewAppointment.getEndTime().getTimeInMillis())
                                    / 60000)
                                    < StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                _oldWorker.getStoreAppointments().remove(_oldAppointmentPosition + 1);
                            }
                            // if the new appointment takes time to the gap but it is still acceptable, update the gap and the appointment
                            else {
                                _oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).setStartTime(mNewAppointment.getEndTime());
                            }
                            // update the appointment
                            _oldAppointment.setStoreTask(_newTask);
                            _oldAppointment.setStartTime(mNewAppointment.getStartTime());
                            _oldAppointment.setEndTime(mNewAppointment.getEndTime());
                        }
                        // if there is an appointment after
                        else {
                            // if the new appointment creates an acceptable gap, creates the gap and update the appointment
                            if (((_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).getStartTime().getTimeInMillis()
                                    - mNewAppointment.getEndTime().getTimeInMillis())
                                    / 60000)
                                    >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                NullStoreAppointment nullStoreAppointment = new NullStoreAppointment();
                                nullStoreAppointment.setStartTime(mNewAppointment.getEndTime());
                                nullStoreAppointment.setEndTime(_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).getStartTime());
                                _oldWorker.getStoreAppointments().add(_oldAppointmentPosition + 1, nullStoreAppointment);
                            }
                            _oldAppointment.setStoreTask(_newTask);
                            _oldAppointment.setStartTime(mNewAppointment.getStartTime());
                            _oldAppointment.setEndTime(mNewAppointment.getEndTime());
                        }
                    }
                    // if there is something before and after
                    else {
                        // if there is a gap before and after
                        if (_oldWorker.getStoreAppointment(_oldAppointmentPosition - 1) instanceof NullStoreAppointment
                                && _oldWorker.getStoreAppointment(_oldAppointmentPosition + 1) instanceof NullStoreAppointment) {
                            // if the new appointment consumes the gap before to the point it is not acceptable anymore, remove the gap
                            if (((mNewAppointment.getStartTime().getTimeInMillis()
                                    - _oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).getStartTime().getTimeInMillis())
                                    / 60000)
                                    < StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                _oldWorker.getStoreAppointments().remove(_oldAppointmentPosition - 1);
                            }
                            // if not update the gap
                            else {
                                _oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).setEndTime(mNewAppointment.getStartTime());
                            }
                            // if the new appointment consumes the gap after to the point it is not acceptable anymore, remove the gap
                            if (((_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).getEndTime().getTimeInMillis()
                                    - mNewAppointment.getEndTime().getTimeInMillis())
                                    / 60000)
                                    < StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                _oldWorker.getStoreAppointments().remove(_oldAppointmentPosition + 1);
                            }
                            // if not update the gap
                            else {
                                _oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).setStartTime(mNewAppointment.getEndTime());
                            }
                            // update the appointment
                            _oldAppointment.setStoreTask(_newTask);
                            _oldAppointment.setStartTime(mNewAppointment.getStartTime());
                            _oldAppointment.setEndTime(mNewAppointment.getEndTime());
                        }
                        // if there is a gap before and an appointment after
                        else if (_oldWorker.getStoreAppointment(_oldAppointmentPosition - 1) instanceof NullStoreAppointment
                                && !(_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1) instanceof NullStoreAppointment)) {
                            // if the new appointment consumes the gap before to the point it is not acceptable anymore, remove the gap
                            if (((mNewAppointment.getStartTime().getTimeInMillis()
                                    - _oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).getStartTime().getTimeInMillis())
                                    / 60000)
                                    < StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                _oldWorker.getStoreAppointments().remove(_oldAppointmentPosition - 1);
                            }
                            // if not update the gap
                            else {
                                _oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).setEndTime(mNewAppointment.getStartTime());
                            }
                            // if the new appointment creates a gap between its end time and the appointment after, create a gap
                            if (((_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).getStartTime().getTimeInMillis()
                                    - mNewAppointment.getEndTime().getTimeInMillis())
                                    / 60000)
                                    >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                NullStoreAppointment nullStoreAppointment = new NullStoreAppointment();
                                nullStoreAppointment.setStartTime(mNewAppointment.getEndTime());
                                nullStoreAppointment.setEndTime(_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).getStartTime());
                                _oldWorker.getStoreAppointments().add(_oldAppointmentPosition + 1, nullStoreAppointment);
                            }
                            // update the appointment
                            _oldAppointment.setStoreTask(_newTask);
                            _oldAppointment.setStartTime(mNewAppointment.getStartTime());
                            _oldAppointment.setEndTime(mNewAppointment.getEndTime());
                        }
                        // if there is an appointment before and a gap after
                        else if (!(_oldWorker.getStoreAppointment(_oldAppointmentPosition - 1) instanceof NullStoreAppointment)
                                && _oldWorker.getStoreAppointment(_oldAppointmentPosition + 1) instanceof NullStoreAppointment) {
                            // if the new appointment consumes the gap after to the point it is not acceptable anymore, remove the gap
                            if (((_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).getEndTime().getTimeInMillis()
                                    - mNewAppointment.getEndTime().getTimeInMillis())
                                    / 60000)
                                    < StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                _oldWorker.getStoreAppointments().remove(_oldAppointmentPosition + 1);
                            }
                            // if not update the gap
                            else {
                                _oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).setStartTime(mNewAppointment.getEndTime());
                            }
                            // if the new appointment creates a gap between its start time and the appointment before, create a gap
                            if (((mNewAppointment.getStartTime().getTimeInMillis()
                                    - _oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).getEndTime().getTimeInMillis())
                                    / 60000)
                                    >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                NullStoreAppointment nullStoreAppointment = new NullStoreAppointment();
                                nullStoreAppointment.setStartTime(_oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).getEndTime());
                                nullStoreAppointment.setEndTime(mNewAppointment.getStartTime());
                                _oldWorker.getStoreAppointments().add(_oldAppointmentPosition + 1, nullStoreAppointment);
                            }
                            // update the appointment
                            _oldAppointment.setStoreTask(_newTask);
                            _oldAppointment.setStartTime(mNewAppointment.getStartTime());
                            _oldAppointment.setEndTime(mNewAppointment.getEndTime());
                        }
                        // if there is an appointment before and after
                        else {
                            // if the new appointment creates a gap between its start time and the appointment before, create a gap
                            if (((mNewAppointment.getStartTime().getTimeInMillis()
                                    - _oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).getEndTime().getTimeInMillis())
                                    / 60000)
                                    >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                NullStoreAppointment nullStoreAppointment = new NullStoreAppointment();
                                nullStoreAppointment.setStartTime(_oldWorker.getStoreAppointment(_oldAppointmentPosition - 1).getEndTime());
                                nullStoreAppointment.setEndTime(mNewAppointment.getStartTime());
                                _oldWorker.getStoreAppointments().add(_oldAppointmentPosition, nullStoreAppointment);
                            }
                            // if the new appointment creates a gap between its end time and the appointment after, create a gap
                            if (((_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).getStartTime().getTimeInMillis()
                                    - mNewAppointment.getEndTime().getTimeInMillis())
                                    / 60000)
                                    >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                                NullStoreAppointment nullStoreAppointment = new NullStoreAppointment();
                                nullStoreAppointment.setStartTime(mNewAppointment.getEndTime());
                                nullStoreAppointment.setEndTime(_oldWorker.getStoreAppointment(_oldAppointmentPosition + 1).getStartTime());
                                _oldWorker.getStoreAppointments().add(_oldAppointmentPosition + 1, nullStoreAppointment);
                            }
                            // update the appointment
                            _oldAppointment.setStoreTask(_newTask);
                            _oldAppointment.setStartTime(mNewAppointment.getStartTime());
                            _oldAppointment.setEndTime(mNewAppointment.getEndTime());
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
        if (mEditTextClientName.getText().toString().equals("")) {
            errorMessage = getString(R.string.please_specify_a_name);
        } else if (mNewAppointment.getEndTime().before(mNewAppointment.getStartTime())) {
            errorMessage = getString(R.string.ending_time_cannot_be_prior_to_starting_time);
        } else if (doesAppointmentOverlap()) {
            errorMessage = getString(R.string.appointment_overlaps_with_at_least_another_one);
        }
        if (_isAGap && mNewAppointment.getStartTime().before(mNow)) {
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
