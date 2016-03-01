package fr.goui.storeorganizer;

import java.util.Calendar;

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
