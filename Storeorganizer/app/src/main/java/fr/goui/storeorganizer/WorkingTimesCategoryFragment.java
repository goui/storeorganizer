package fr.goui.storeorganizer;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * {@code WorkingTimesCategoryFragment} is a fragment of {@link SettingsActivity}.
 * It is used to display the working times for the store users can modify.
 */
public class WorkingTimesCategoryFragment extends Fragment {

    /**
     * The {@code String} representing the pattern used to format times.
     */
    private static final String DATE_FORMAT_PATTERN = "HH:mm";

    /**
     * The {@code SharedPreferences}.
     */
    private SharedPreferences mSharedPreferences;

    /**
     * The android resources to get project values.
     */
    private Resources mResources;

    /**
     * The {@code Calendar} used to manage times.
     */
    private Calendar mCalendarTime = Calendar.getInstance();

    /**
     * The {@code TextView} for starting time.
     */
    private TextView mTxtStart;

    /**
     * The {@code TextView} for ending time.
     */
    private TextView mTxtEnd;

    private int mStartingHour;

    private int mStartingMinute;

    private int mEndingHour;

    private int mEndingMinute;

    /**
     * The {@code SimpleDateFormat} used to format start and end times.
     */
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);

    /**
     * The time picker dialog listener for starting time.
     */
    private TimePickerDialog.OnTimeSetListener mStartTimePickerDialogListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            mCalendarTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendarTime.set(Calendar.MINUTE, minute);
            updateStartingTime();
        }
    };

    /**
     * The time picker dialog listener for ending time.
     */
    private TimePickerDialog.OnTimeSetListener mEndTimePickerDialogListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            mCalendarTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendarTime.set(Calendar.MINUTE, minute);
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

        // getting saved starting times in the shared prefs
        mStartingHour = mSharedPreferences.getInt(mResources.getString(R.string.starting_hour), mResources.getInteger(R.integer.default_starting_hour));
        mStartingMinute = mSharedPreferences.getInt(mResources.getString(R.string.starting_minute), mResources.getInteger(R.integer.default_starting_minute));

        // updating in settings view
        mCalendarTime.set(Calendar.HOUR_OF_DAY, mStartingHour);
        mCalendarTime.set(Calendar.MINUTE, mStartingMinute);
        mTxtStart.setText(mSimpleDateFormat.format(mCalendarTime.getTime()));

        // getting saved ending times in the shared prefs
        mEndingHour = mSharedPreferences.getInt(mResources.getString(R.string.ending_hour), mResources.getInteger(R.integer.default_ending_hour));
        mEndingMinute = mSharedPreferences.getInt(mResources.getString(R.string.ending_minute), mResources.getInteger(R.integer.default_ending_minute));

        // updating in settings view
        mCalendarTime.set(Calendar.HOUR_OF_DAY, mEndingHour);
        mCalendarTime.set(Calendar.MINUTE, mEndingMinute);
        mTxtEnd.setText(mSimpleDateFormat.format(mCalendarTime.getTime()));

        // resetting calendar time
        mCalendarTime = Calendar.getInstance();
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
                        mStartingHour,
                        mStartingMinute,
                        true).show();
            }
        });

        // listener to trigger the time picker dialog for the ending time
        mTxtEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(),
                        mEndTimePickerDialogListener,
                        mEndingHour,
                        mEndingMinute,
                        true).show();
            }
        });

        return rootView;
    }

    /**
     * Method used to update the starting time in both settings and {@code SharedPreferences}.
     */
    private void updateStartingTime() {

        // updating values
        mStartingHour = mCalendarTime.get(Calendar.HOUR_OF_DAY);
        mStartingMinute = mCalendarTime.get(Calendar.MINUTE);

        // updating in settings view
        mTxtStart.setText(mSimpleDateFormat.format(mCalendarTime.getTime()));

        // updating in shared prefs
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(mResources.getString(R.string.starting_hour), mCalendarTime.get(Calendar.HOUR_OF_DAY));
        editor.putInt(mResources.getString(R.string.starting_minute), mCalendarTime.get(Calendar.MINUTE));
        editor.apply();
    }

    /**
     * Method used to update the ending time in both settings and {@code SharedPreferences}.
     */
    private void updateEndingTime() {

        // updating values
        mEndingHour = mCalendarTime.get(Calendar.HOUR_OF_DAY);
        mEndingMinute = mCalendarTime.get(Calendar.MINUTE);

        // updating in settings view
        mTxtEnd.setText(mSimpleDateFormat.format(mCalendarTime.getTime()));

        // updating in shared prefs
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(mResources.getString(R.string.ending_hour), mCalendarTime.get(Calendar.HOUR_OF_DAY));
        editor.putInt(mResources.getString(R.string.ending_minute), mCalendarTime.get(Calendar.MINUTE));
        editor.apply();
    }

}
