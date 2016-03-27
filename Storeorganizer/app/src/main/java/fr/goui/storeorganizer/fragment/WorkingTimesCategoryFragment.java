package fr.goui.storeorganizer.fragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import fr.goui.storeorganizer.R;
import fr.goui.storeorganizer.model.StoreModel;
import fr.goui.storeorganizer.activity.SettingsActivity;

/**
 * {@code WorkingTimesCategoryFragment} is a fragment of {@link SettingsActivity}.
 * It is used to display the working times for the store users can modify.
 */
public class WorkingTimesCategoryFragment extends Fragment {

    /**
     * The {@code SharedPreferences}.
     */
    private SharedPreferences mSharedPreferences;

    /**
     * The android resources to get project values.
     */
    private Resources mResources;

    /**
     * The {@code Calendar} used to manage starting time.
     */
    private Calendar mCalendarStartingTime = Calendar.getInstance();

    /**
     * The {@code Calendar} used to manage ending time.
     */
    private Calendar mCalendarEndingTime = Calendar.getInstance();

    /**
     * The {@code TextView} for starting time.
     */
    private TextView mTxtStart;

    /**
     * The {@code TextView} for ending time.
     */
    private TextView mTxtEnd;

    /**
     * The model storing information about working times.
     */
    private StoreModel mStoreModel = StoreModel.getInstance();

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // getting resources
        mResources = getActivity().getResources();

        // getting shared prefs
        mSharedPreferences = getActivity().getSharedPreferences(mResources.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // setting starting time
        mCalendarStartingTime.set(Calendar.HOUR_OF_DAY, mStoreModel.getStartingHour());
        mCalendarStartingTime.set(Calendar.MINUTE, mStoreModel.getStartingMinute());
        mTxtStart.setText(mStoreModel.getFormattedStartingTime());

        // setting ending time
        mCalendarEndingTime.set(Calendar.HOUR_OF_DAY, mStoreModel.getEndingHour());
        mCalendarEndingTime.set(Calendar.MINUTE, mStoreModel.getEndingMinute());
        mTxtEnd.setText(mStoreModel.getFormattedEndingTime());

        // we don't want to consider seconds and milliseconds
        mCalendarStartingTime.set(Calendar.SECOND, 0);
        mCalendarStartingTime.set(Calendar.MILLISECOND, 0);
        mCalendarEndingTime.set(Calendar.SECOND, 0);
        mCalendarEndingTime.set(Calendar.MILLISECOND, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // getting the layout
        View rootView = inflater.inflate(R.layout.fragment_settings_working_time_category, container, false);

        // getting the views
        mTxtStart = (TextView) rootView.findViewById(R.id.fragment_settings_working_times_start_text_view);
        mTxtEnd = (TextView) rootView.findViewById(R.id.fragment_settings_working_times_end_text_view);

        // listener to trigger the time picker dialog for the starting time
        mTxtStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(),
                        mStartTimePickerDialogListener,
                        mStoreModel.getStartingHour(),
                        mStoreModel.getStartingMinute(),
                        true).show();
            }
        });

        // listener to trigger the time picker dialog for the ending time
        mTxtEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(),
                        mEndTimePickerDialogListener,
                        mStoreModel.getEndingHour(),
                        mStoreModel.getEndingMinute(),
                        true).show();
            }
        });

        return rootView;
    }

    /**
     * Method used to update the starting time in both settings and {@code SharedPreferences}.
     */
    private void updateStartingTime() {

        // getting potential error message
        String errorMessage = checkValidity();

        // if there is no error
        if (errorMessage == null) {

            // getting starting times
            int startingHour = mCalendarStartingTime.get(Calendar.HOUR_OF_DAY);
            int startingMinute = mCalendarStartingTime.get(Calendar.MINUTE);

            // updating in the model
            mStoreModel.setStartingTime(startingHour, startingMinute);

            // updating in settings view
            mTxtStart.setText(mStoreModel.getFormattedStartingTime());

            // updating in shared prefs
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt(mResources.getString(R.string.starting_hour), startingHour);
            editor.putInt(mResources.getString(R.string.starting_minute), startingMinute);
            editor.apply();
        }

        // resetting calendar and displaying detected error
        else {
            mCalendarStartingTime.set(Calendar.HOUR_OF_DAY, mStoreModel.getStartingHour());
            mCalendarStartingTime.set(Calendar.MINUTE, mStoreModel.getStartingMinute());
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method used to update the ending time in both settings and {@code SharedPreferences}.
     */
    private void updateEndingTime() {

        // getting potential error message
        String errorMessage = checkValidity();

        // if there is no error
        if (errorMessage == null) {

            // getting ending times
            int endingHour = mCalendarEndingTime.get(Calendar.HOUR_OF_DAY);
            int endingMinute = mCalendarEndingTime.get(Calendar.MINUTE);

            // updating in the model
            mStoreModel.setEndingTime(endingHour, endingMinute);

            // updating in settings view
            mTxtEnd.setText(mStoreModel.getFormattedEndingTime());

            // updating in shared prefs
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt(mResources.getString(R.string.ending_hour), endingHour);
            editor.putInt(mResources.getString(R.string.ending_minute), endingMinute);
            editor.apply();
        }

        // resetting calendar and displaying detected error
        else {
            mCalendarEndingTime.set(Calendar.HOUR_OF_DAY, mStoreModel.getEndingHour());
            mCalendarEndingTime.set(Calendar.MINUTE, mStoreModel.getEndingMinute());
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method used to check if chosen times are valid.
     * Starting time cannot be before minimum one.
     * Ending time cannot be after maximum one.
     * Ending time cannot be before starting time.
     *
     * @return error message {@code String}, {@code null} if no error
     */
    private String checkValidity() {
        String errorMessage = null;
        if (mCalendarStartingTime.before(mStoreModel.getMinTime())) {
            errorMessage = mResources.getString(R.string.starting_time_cannot_be_before) + " " + mStoreModel.getFormattedMinTime();
        } else if (mCalendarEndingTime.after(mStoreModel.getMaxTime())) {
            errorMessage = mResources.getString(R.string.ending_time_cannot_be_after) + " " + mStoreModel.getFormattedMaxTime();
        } else if (mCalendarEndingTime.before(mCalendarStartingTime)) {
            errorMessage = mResources.getString(R.string.ending_time_cannot_be_prior_to_starting_time);
        }
        return errorMessage;
    }
}
