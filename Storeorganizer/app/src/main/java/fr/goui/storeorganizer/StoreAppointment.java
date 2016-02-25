package fr.goui.storeorganizer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;

/**
 * {@code StoreAppointment} is a class containing the client's name, the client's phone number,
 * a {@link StoreTask} and 2 {@link Calendar} (one for the start time, one for the end time).
 * This is a {@code Comparable} class.
 */
public class StoreAppointment implements Comparable<StoreAppointment> {

    /**
     * The {@code String} representing the pattern used to format times.
     */
    private static final String DATE_FORMAT_PATTERN = "HH:mm";

    /**
     * The {@code StoreTask} linked to this {@code StoreAppointment}.
     */
    private StoreTask _storeTask;

    /**
     * The {@code String} representing the client's name.
     */
    private String _clientName;

    /**
     * The {@code String} representing the client's phone number.
     */
    private String _clientPhoneNumber;

    /**
     * The {@code Calendar} representing the start time.
     */
    private Calendar _startTime;

    /**
     * The {@code Calendar} representing the end time.
     */
    private Calendar _endTime;

    /**
     * The {@code SimpleDateFormat} used to format start and end times.
     */
    private SimpleDateFormat _simpleDateFormat;

    /**
     * Default constructor instantiating the 2 {@code Calendar} and the {@code SimpleDateFormat}.
     */
    public StoreAppointment() {
        _startTime = Calendar.getInstance();
        _endTime = Calendar.getInstance();
        _simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    }

    /**
     * Getter for the {@code StoreTask}.
     *
     * @return {@code StoreTask}
     */
    public StoreTask getStoreTask() {
        return _storeTask;
    }

    /**
     * Getter for the client's name {@code String}.
     *
     * @return the client's name {@code String}
     */
    public String getClientName() {
        return _clientName;
    }

    /**
     * Getter for the client's phone number {@code String}.
     *
     * @return the client's phone number {@code String}
     */
    public String getClientPhoneNumber() {
        return _clientPhoneNumber;
    }

    /**
     * Getter for the start time {@code Calendar}.
     *
     * @return start time {@code Calendar}
     */
    public Calendar getStartTime() {
        return _startTime;
    }

    /**
     * Getter for the end time {@code Calendar}.
     *
     * @return end time {@code Calendar}
     */
    public Calendar getEndTime() {
        return _endTime;
    }

    /**
     * The start time {@code Calendar} formatted into a {@code String}.
     *
     * @return the formatted start time {@code String}
     */
    public String getFormattedStartTime() {
        return _simpleDateFormat.format(_startTime.getTime());
    }

    /**
     * The end time {@code Calendar} formatted into a {@code String}.
     *
     * @return the formatted end time {@code String}
     */
    public String getFormattedEndTime() {
        return _simpleDateFormat.format(_endTime.getTime());
    }

    /**
     * The duration in minutes of the {@code StoreAppointment}.
     * By default it is the duration in minutes of the {@code StoreTask}.
     *
     * @return {@code int}
     */
    public int getDuration() {
        return _storeTask != null ? _storeTask.getDuration() : 0;
    }

    /**
     * Setter for the {@code StoreTask}.
     *
     * @param storeTask_p the {@code StoreTask}
     */
    public void setStoreTask(StoreTask storeTask_p) {
        _storeTask = storeTask_p;
    }

    /**
     * Setter for the client's name.
     *
     * @param clientName_p the client's name
     */
    public void setClientName(String clientName_p) {
        _clientName = clientName_p;
    }

    /**
     * Setter for the client's phone number.
     *
     * @param clientPhoneNumber_p the client's phone number
     */
    public void setClientPhoneNumber(String clientPhoneNumber_p) {
        _clientPhoneNumber = clientPhoneNumber_p;
    }

    /**
     * Setter for the start time {@code Calendar}.
     * It will compute the end time {@code Calendar} as well based on the duration.
     *
     * @param startHour_p   {@code int} for the hour
     * @param startMinute_p {@code int} for the minutes
     */
    public void setStartTime(int startHour_p, int startMinute_p) {
        _startTime.set(Calendar.HOUR_OF_DAY, startHour_p);
        _startTime.set(Calendar.MINUTE, startMinute_p);
        _endTime.setTimeInMillis(_startTime.getTimeInMillis() + getDuration() * 60000);
    }

    /**
     * Setter for the end time {@code Calendar}.
     *
     * @param endHour_p   {@code int} for the hour
     * @param endMinute_p {@code int} for the minutes
     */
    public void setEndTime(int endHour_p, int endMinute_p) {
        _endTime.set(Calendar.HOUR_OF_DAY, endHour_p);
        _endTime.set(Calendar.MINUTE, endMinute_p);
    }

    /**
     * Setter for the start time {@code Calendar}.
     *
     * @param calendar_p the start time {@code Calendar}
     */
    public void setStartTime(Calendar calendar_p) {
        setStartTime(calendar_p.get(Calendar.HOUR_OF_DAY), calendar_p.get(Calendar.MINUTE));
    }

    /**
     * Setter for the end time {@code Calendar}.
     *
     * @param calendar_p the end time {@code Calendar}
     */
    public void setEndTime(Calendar calendar_p) {
        setEndTime(calendar_p.get(Calendar.HOUR_OF_DAY), calendar_p.get(Calendar.MINUTE));
    }

    /**
     * Checks if this {@code StoreAppointment} is before the parameter {@code Calendar}.
     *
     * @param calendar_p a {@code Calendar}
     * @return {@code true} if before, {@code false} otherwise
     */
    public boolean isBefore(Calendar calendar_p) {
        return _endTime.before(calendar_p);
    }

    /**
     * Checks if this {@code StoreAppointment} is before the parameter {@code StoreAppointment}.
     *
     * @param appointment_p a {@code StoreAppointment}
     * @return {@code true} if before, {@code false} otherwise
     */
    public boolean isBefore(StoreAppointment appointment_p) {
        return _endTime.before(appointment_p.getStartTime());
    }

    /**
     * Checks if this {@code StoreAppointment} is after the parameter {@code Calendar}.
     *
     * @param calendar_p a {@code Calendar}
     * @return {@code true} if after, {@code false} otherwise
     */
    public boolean isAfter(Calendar calendar_p) {
        return _startTime.after(calendar_p);
    }

    /**
     * Checks if this {@code StoreAppointment} is after the parameter {@code StoreAppointment}.
     *
     * @param appointment_p a {@code StoreAppointment}
     * @return {@code true} if after, {@code false} otherwise
     */
    public boolean isAfter(StoreAppointment appointment_p) {
        return _startTime.after(appointment_p.getEndTime());
    }

    /**
     * Computes the gap in minutes between this {@code StoreAppointment} and the parameter {@code Calendar}.
     *
     * @param calendar_p a {@code Calendar}
     * @return an {@code int} which is the gap in minutes
     */
    public int gapWith(Calendar calendar_p) {
        int gap;
        if (isAfter(calendar_p)) {
            gap = (int) (_startTime.getTimeInMillis() - calendar_p.getTimeInMillis()) / 60000;
        } else {
            gap = (int) (calendar_p.getTimeInMillis() - _endTime.getTimeInMillis()) / 60000;
        }
        return gap;
    }

    /**
     * Computes the gap in minutes between this {@code StoreAppointment} and the parameter {@code StoreAppointment}.
     *
     * @param storeAppointment_p a {@code StoreAppointment}
     * @return an {@code int} which is the gap in minutes
     */
    public int gapWith(StoreAppointment storeAppointment_p) {
        int gap;
        if (isAfter(storeAppointment_p)) {
            gap = (int) (_startTime.getTimeInMillis() - storeAppointment_p.getEndTime().getTimeInMillis()) / 60000;
        } else {
            gap = (int) (storeAppointment_p.getStartTime().getTimeInMillis() - _endTime.getTimeInMillis()) / 60000;
        }
        return gap;
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof StoreAppointment) {
            StoreAppointment appointment = (StoreAppointment) o;
            equals = appointment.getClientName().equals(_clientName)
                    && appointment.getClientPhoneNumber().equals(_clientPhoneNumber)
                    && appointment.getStoreTask().equals(_storeTask)
                    && appointment.getStartTime().equals(_startTime)
                    && appointment.getEndTime().equals(_endTime);
        }
        return equals;
    }

    @Override
    public String toString() {
        return _clientName + " - " + _storeTask.getName() + " - " + getFormattedStartTime();
    }

    @Override
    public int compareTo(StoreAppointment another_p) {
        return Comparators.START_TIME.compare(this, another_p);
    }

    /**
     * Creates a new instance of {@code NullStoreAppointment}.
     *
     * @return {@code NullStoreAppointment}
     */
    public NullStoreAppointment newNullInstance() {
        return new NullStoreAppointment();
    }

    /**
     * Comparator class used to compare 2 {@code StoreAppointment}s.
     */
    public static class Comparators {

        /**
         * Comparing 2 {@code StoreAppointment}s depending on their start time.
         */
        public static final Comparator<StoreAppointment> START_TIME = new Comparator<StoreAppointment>() {

            @Override
            public int compare(StoreAppointment appointment1_p, StoreAppointment appointment2_p) {
                return appointment1_p.getStartTime().compareTo(appointment2_p.getStartTime());
            }
        };
    }

    /**
     * A {@code NullStoreAppointment} is an empty {@code StoreAppointment} which represents a gap item.
     * Its duration is the duration in minutes between its start and end times.
     * Setting its start time doesn't compute its end time.
     * Its {@code StoreTask} is {@code null}.
     */
    public class NullStoreAppointment extends StoreAppointment {

        @Override
        public void setStartTime(int startHour_p, int startMinute_p) {
            _startTime.set(Calendar.HOUR_OF_DAY, startHour_p);
            _startTime.set(Calendar.MINUTE, startMinute_p);
        }

        @Override
        public StoreTask getStoreTask() {
            return null;
        }

        @Override
        public int getDuration() {
            return (int) (_endTime.getTimeInMillis() - _startTime.getTimeInMillis()) / 60000;
        }
    }

}
