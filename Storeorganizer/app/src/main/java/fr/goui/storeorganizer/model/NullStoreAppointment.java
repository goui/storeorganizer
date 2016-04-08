package fr.goui.storeorganizer.model;

import java.util.Calendar;

/**
 * A {@code NullStoreAppointment} is an empty {@code StoreAppointment} which represents a gap item.
 * Its duration is the duration in minutes between its start and end times.
 * Setting its start time doesn't compute its end time.
 * Its {@code StoreTask} is {@code null}.
 */
public class NullStoreAppointment extends StoreAppointment {

    private static final String GAP = "Gap";

    @Override
    public StoreTask getStoreTask() {
        return null;
    }

    @Override
    public int getDuration() {
        return (int) (_endTime.getTimeInMillis() - _startTime.getTimeInMillis()) / 60000;
    }

    @Override
    public String toString() {
        return GAP + " - " + getFormattedStartTime() + " - " + getFormattedEndTime();
    }
}
