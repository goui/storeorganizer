package fr.goui.storeorganizer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Observable;

/**
 * {@code StoreModel} is a singleton class responsible for holding all information about the store.
 */
public class StoreModel extends Observable {

    /**
     * The unique instance of the {@code StoreModel}.
     */
    private static StoreModel _instance = new StoreModel();

    /**
     * The initialization {@code boolean}.
     */
    private boolean _init;

    /**
     * The {@code String} representing the pattern used to format times.
     */
    private static final String DATE_FORMAT_PATTERN = "HH:mm";

    /**
     * The {@code SimpleDateFormat} used to format start and end times.
     */
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);

    /**
     * The minimum allowed time {@code Calendar}.
     */
    private Calendar _calendarMin = Calendar.getInstance();

    /**
     * The maximum allowed time {@code Calendar}.
     */
    private Calendar _calendarMax = Calendar.getInstance();

    /**
     * The {@code Calendar} used to manage starting time.
     */
    private Calendar _calendarStartingTime = Calendar.getInstance();

    /**
     * The {@code Calendar} used to manage ending time.
     */
    private Calendar _calendarEndingTime = Calendar.getInstance();

    /**
     * Getter for the unique instance of {@code StoreModel}.
     *
     * @return the unique instance of {@code StoreModel}
     */
    public static StoreModel getInstance() {
        return _instance;
    }

    /**
     * Private constructor of this singleton class.
     */
    private StoreModel() {
        // we don't want to consider seconds and milliseconds
        _calendarMin.set(Calendar.SECOND, 0);
        _calendarMin.set(Calendar.MILLISECOND, 0);
        _calendarMax.set(Calendar.SECOND, 0);
        _calendarMax.set(Calendar.MILLISECOND, 0);
        _calendarStartingTime.set(Calendar.SECOND, 0);
        _calendarStartingTime.set(Calendar.MILLISECOND, 0);
        _calendarEndingTime.set(Calendar.SECOND, 0);
        _calendarEndingTime.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Method used to know if this model has been initialized.
     * Avoid multiple initialization.
     *
     * @return {@code true} if model initialized, {@code false} otherwise
     */
    public boolean isInit() {
        return _init;
    }

    /**
     * Getter for the starting hour.
     *
     * @return the starting hour {@code int}
     */
    public int getStartingHour() {
        return _calendarStartingTime.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Setter for the starting hour.
     *
     * @param startingHour_p the new starting hour {@code int}
     */
    private void setStartingHour(int startingHour_p) {
        _calendarStartingTime.set(Calendar.HOUR_OF_DAY, startingHour_p);
    }

    /**
     * Getter for the starting minute.
     *
     * @return the starting minute {@code int}
     */
    public int getStartingMinute() {
        return _calendarStartingTime.get(Calendar.MINUTE);
    }

    /**
     * Setter for the starting minute.
     *
     * @param startingMinute_p the new starting minute {@code int}
     */
    private void setStartingMinute(int startingMinute_p) {
        _calendarStartingTime.set(Calendar.MINUTE, startingMinute_p);
    }

    /**
     * Getter for the starting time.
     *
     * @return the starting time {@code Calendar}
     */
    public Calendar getStartingTime() {
        return _calendarStartingTime;
    }

    /**
     * Setter for the starting time.
     *
     * @param startingHour_p   the new starting hour {@code int}
     * @param startingMinute_p the new starting minute {@code int}
     */
    public void setStartingTime(int startingHour_p, int startingMinute_p) {
        _init = true;
        setStartingHour(startingHour_p);
        setStartingMinute(startingMinute_p);
        setChanged();
        notifyObservers();
    }

    /**
     * Getter for the ending hour.
     *
     * @return the ending hour {@code int}
     */
    public int getEndingHour() {
        return _calendarEndingTime.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Setter for the ending hour.
     *
     * @param endingHour_p the new ending hour {@code int}
     */
    private void setEndingHour(int endingHour_p) {
        _calendarEndingTime.set(Calendar.HOUR_OF_DAY, endingHour_p);
    }

    /**
     * Getter for the ending minute.
     *
     * @return the ending minute {@code int}
     */
    public int getEndingMinute() {
        return _calendarEndingTime.get(Calendar.MINUTE);
    }

    /**
     * Setter for the ending minute.
     *
     * @param endingMinute_p the new ending minute {@code int}
     */
    private void setEndingMinute(int endingMinute_p) {
        _calendarEndingTime.set(Calendar.MINUTE, endingMinute_p);
    }

    /**
     * Getter for the ending time.
     *
     * @return the ending time {@code Calendar}
     */
    public Calendar getEndingTime() {
        return _calendarEndingTime;
    }

    /**
     * Setter for the ending time.
     *
     * @param endingHour_p   the new ending hour {@code int}
     * @param endingMinute_p the new ending minute {@code int}
     */
    public void setEndingTime(int endingHour_p, int endingMinute_p) {
        _init = true;
        setEndingHour(endingHour_p);
        setEndingMinute(endingMinute_p);
        setChanged();
        notifyObservers();
    }

    /**
     * Getter for the minimum hour.
     *
     * @return the minimum hour {@code int}
     */
    public int getMinHour() {
        return _calendarMin.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Setter for the minimum hour.
     *
     * @param minHour_p the new minimum hour {@code int}
     */
    private void setMinHour(int minHour_p) {
        _calendarMin.set(Calendar.HOUR_OF_DAY, minHour_p);
    }

    /**
     * Getter for the minimum minute.
     *
     * @return the minimum minute {@code int}
     */
    public int getMinMinute() {
        return _calendarMin.get(Calendar.MINUTE);
    }

    /**
     * Setter for the minimum minute.
     *
     * @param minMinute_p the new minimum minute {@code int}
     */
    private void setMinMinute(int minMinute_p) {
        _calendarMin.set(Calendar.MINUTE, minMinute_p);
    }

    /**
     * Getter for the minimum time.
     *
     * @return the minimum time {@code Calendar}
     */
    public Calendar getMinTime() {
        return _calendarMin;
    }

    /**
     * Setter for the minimum time.
     *
     * @param minHour_p   the new minimum hour {@code int}
     * @param minMinute_p the new minimum minute {@code int}
     */
    public void setMinTime(int minHour_p, int minMinute_p) {
        _init = true;
        setMinHour(minHour_p);
        setMinMinute(minMinute_p);
    }

    /**
     * Getter for the maximum hour.
     *
     * @return the maximum hour {@code int}
     */
    public int getMaxHour() {
        return _calendarMax.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Setter for the maximum hour.
     *
     * @param maxHour_p the new maximum hour {@code int}
     */
    private void setMaxHour(int maxHour_p) {
        _calendarMax.set(Calendar.HOUR_OF_DAY, maxHour_p);
    }

    /**
     * Getter for the maximum minute.
     *
     * @return the maximum minute {@code int}
     */
    public int getMaxMinute() {
        return _calendarMax.get(Calendar.MINUTE);
    }

    /**
     * Setter for the maximum minute.
     *
     * @param maxMinute_p the new maximum minute {@code int}
     */
    private void setMaxMinute(int maxMinute_p) {
        _calendarMax.set(Calendar.MINUTE, maxMinute_p);
    }

    /**
     * Getter for the maximum time.
     *
     * @return the maximum time {@code Calendar}
     */
    public Calendar getMaxTime() {
        return _calendarMax;
    }

    /**
     * Setter for the maximum time.
     *
     * @param maxHour_p   the new maximum hour {@code int}
     * @param maxMinute_p the new maximum minute {@code int}
     */
    public void setMaxTime(int maxHour_p, int maxMinute_p) {
        _init = true;
        setMaxHour(maxHour_p);
        setMaxMinute(maxMinute_p);
    }

    /**
     * The starting time {@code Calendar} formatted into a {@code String}.
     *
     * @return the formatted starting time {@code String}
     */
    public String getFormattedStartingTime() {
        return mSimpleDateFormat.format(_calendarStartingTime.getTime());
    }

    /**
     * The ending time {@code Calendar} formatted into a {@code String}.
     *
     * @return the formatted ending time {@code String}
     */
    public String getFormattedEndingTime() {
        return mSimpleDateFormat.format(_calendarEndingTime.getTime());
    }

    /**
     * The minimum time {@code Calendar} formatted into a {@code String}.
     *
     * @return the formatted minimum time {@code String}
     */
    public String getFormattedMinTime() {
        return mSimpleDateFormat.format(_calendarMin.getTime());
    }

    /**
     * The maximum time {@code Calendar} formatted into a {@code String}.
     *
     * @return the formatted maximum time {@code String}
     */
    public String getFormattedMaxTime() {
        return mSimpleDateFormat.format(_calendarMax.getTime());
    }

}
