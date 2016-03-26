package fr.goui.storeorganizer;

import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Calendar;

/**
 * {@code AppointmentEditionActivity} is an {@code Activity} used to display a gui to edit a {@link StoreAppointment}.
 * The user can modify the client's name (required), the client's phone number (optional), the {@code StoreTask} (required)
 * and the starting and ending times (required).
 * {@code NullStoreAppointment} can be edited as well.
 */
public class AppointmentEditionActivity extends AppointmentCreationActivity {

    /**
     * The old {@code StoreAppointment}.
     */
    private StoreAppointment mOldAppointment;

    /**
     * The {@code StoreWorker} who was assigned to the old {@code StoreAppointment}.
     */
    private StoreWorker mOldWorker;

    /**
     * A {@code boolean} used to know if the {@code StoreWorker} has been changed.
     */
    private boolean mHasWorkerChanged;

    /**
     * A {@code boolean} used to know if we are editing a {@code NullStoreAppointment}.
     */
    private boolean mIsAGap;

    @Override
    protected void init() {

        // getting the old worker position
        int oldWorkerPosition = getIntent().getIntExtra(WorkerFragment.INTENT_EXTRA_WORKER_POSITION, -1);

        // getting the old appointment position
        int oldAppointmentPosition = getIntent().getIntExtra(WorkerFragment.INTENT_EXTRA_APPOINTMENT_POSITION, -1);

        // if there has been no problem to get the old worker position
        if (oldWorkerPosition != -1) {

            // getting the old worker
            mOldWorker = StoreWorkerModel.getInstance().getStoreWorker(oldWorkerPosition);

            // if there has been no problem to get the old appointment position
            if (oldAppointmentPosition != -1) {

                // getting the old appointment
                mOldAppointment = mOldWorker.getStoreAppointment(oldAppointmentPosition);

                // creating the new appointment
                mNewAppointment = new StoreAppointment();

                // getting the old task
                StoreTask oldTask = mOldAppointment.getStoreTask();

                // copying the task in the new appointment
                mNewAppointment.setStoreTask(oldTask);

                // if there is no old task, old appointment is a gap
                int taskPosition = -1;
                if (oldTask == null) {
                    mIsAGap = true;
                    mHasWorkerChanged = false;
                }

                // if there is an old task, old appointment is not a gap
                else {
                    mIsAGap = false;

                    // searching for the old task position
                    for (int i = 0; i < StoreTaskModel.getInstance().getStoreTaskNumber(); i++) {
                        if (oldTask.equals(StoreTaskModel.getInstance().getStoreTask(i))) {
                            taskPosition = i;
                        }
                    }
                }

                // putting information about the old appointment in the views
                initViewsInformation(oldWorkerPosition, taskPosition);
            }
        }
    }

    /**
     * Method used to initialize views information.
     *
     * @param workerPosition_p the position of the old worker
     * @param taskPosition_p   the position of the new task
     */
    private void initViewsInformation(int workerPosition_p, int taskPosition_p) {

        // putting information about old appointment in the views
        mEditTextClientName.setText(mOldAppointment.getClientName());
        mEditTextClientPhoneNumber.setText(mOldAppointment.getClientPhoneNumber());
        mSpinnerWorker.setSelection(workerPosition_p);

        // if we are editing a gap we don't want to change the worker
        if (mIsAGap) {
            mSpinnerWorker.setEnabled(false);
            mSpinnerWorker.setBackgroundResource(R.color.light_grey);
        }

        // if it is not a gap, selecting the old task
        else {
            mSpinnerTask.setSelection(taskPosition_p);
        }

        // setting starting and ending times text
        mTextViewStartingTime.setText(mOldAppointment.getFormattedStartTime());
        mTextViewEndingTime.setText(mOldAppointment.getFormattedEndTime());
    }

    @Override
    protected void onCheckboxFromChangeListener(boolean isChecked) {
        mTextViewStartingTime.setEnabled(!isChecked);
        if (isChecked) {
            mTextViewStartingTime.setTextColor(ContextCompat.getColor(this, R.color.grey_overlay));
            mTextViewStartingTime.setBackgroundResource(R.color.light_grey);

            // updating starting calendar to old appointment starting time
            mCalendarStartingTime.set(Calendar.HOUR_OF_DAY, mOldAppointment.getStartTime().get(Calendar.HOUR_OF_DAY));
            mCalendarStartingTime.set(Calendar.MINUTE, mOldAppointment.getStartTime().get(Calendar.MINUTE));
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

    @Override
    protected void registerToTimeTickReceiver() {
        // do nothing
        // in edition mode we don't want to be notified every minute
    }

    @Override
    protected void unregisterFromTimeTickReceiver() {
        // do nothing
        // in edition mode we aren't registered
    }

    @Override
    protected void updateAppointmentInformation() {

        // if the task has already been selected
        if (mNewTask != null) {

            // setting the new task
            mNewAppointment.setStoreTask(mNewTask);

            // if the worker has not changed
            if (mOldWorker.equals(mNewWorker)) {

                // using old appointment's starting and ending times
                mNewAppointment.setStartTime(mOldAppointment.getStartTime());
                mTempCalendar.setTimeInMillis(mOldAppointment.getStartTime().getTimeInMillis() + mNewAppointment.getDuration() * mConversionMillisecondMinute);
                mNewAppointment.setEndTime(mTempCalendar);

                // keeping this information
                mHasWorkerChanged = false;
            }

            // if worker has changed
            else {

                // setting the default time
                mTempCalendar.set(Calendar.HOUR_OF_DAY, mNow.get(Calendar.HOUR_OF_DAY));
                mTempCalendar.set(Calendar.MINUTE, mNow.get(Calendar.MINUTE));

                // getting the next availability
                StoreAppointment appointment = mNewWorker.getNextAvailability();

                // if next availability is not null, setting times depending on the type of appointment
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

                // keeping this information
                mHasWorkerChanged = true;
            }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = false;
        int id = item.getItemId();

        // if we press the up button, going back
        if (id == android.R.id.home) {
            onBackPressed();
            ret = true;
        }

        // trying to validate the appointment edition
        else if (id == R.id.action_validate_appointment) {
            if (confirmAppointment()) {
                setResult(RESULT_OK);
                finish();
            }
            ret = true;
        }
        return ret;
    }

    @Override
    protected boolean confirmAppointment() {
        boolean result = true;

        // checking errors
        String errorMessage = checkValidity();

        // if there is no error
        if (errorMessage == null) {

            // setting the client's name and phone number
            mNewAppointment.setClientName(mEditTextClientName.getText().toString());
            mNewAppointment.setClientPhoneNumber(mEditTextClientPhoneNumber.getText().toString());

            // if this was a gap, adding the new appointment to the old worker
            if (mIsAGap) {
                mOldWorker.addStoreAppointment(mNewAppointment);
            }

            // if it was not a gap and the worker changed, moving the appointment to the new worker
            else if (mHasWorkerChanged) {
                mOldWorker.removeStoreAppointment(mOldAppointment);
                mNewWorker.addStoreAppointment(mNewAppointment);
            }

            // if it was not a gap and the worker is the same, simply updating the appointment
            else {
                mOldWorker.removeStoreAppointment(mOldAppointment);
                mOldWorker.addStoreAppointment(mNewAppointment);
            }
        } else {
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            result = false;
        }
        return result;
    }

    @Override
    protected boolean doesAppointmentOverlap() {
        boolean overlaps;

        // if it was not a gap and the worker has not changed
        // we need to know if the new times make the updated appointment overlap
        if (!mIsAGap && !mHasWorkerChanged) {
            mOldWorker.removeStoreAppointment(mOldAppointment);
            overlaps = super.doesAppointmentOverlap();
            mOldWorker.addStoreAppointment(mOldAppointment);
        } else {
            overlaps = super.doesAppointmentOverlap();
        }

        return overlaps;
    }
}
