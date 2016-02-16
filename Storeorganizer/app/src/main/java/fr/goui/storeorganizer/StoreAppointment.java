package fr.goui.storeorganizer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class StoreAppointment {

    private StoreTask _storeTask;

    private String _clientName;

    private String _clientPhoneNumber;

    private Calendar _startTime;

    private Calendar _endTime;

    public StoreAppointment() {
        _startTime = Calendar.getInstance();
        _endTime = Calendar.getInstance();
    }

    public StoreTask getStoreTask() {
        return _storeTask;
    }

    public String getClientName() {
        return _clientName;
    }

    public String getClientPhoneNumber() {
        return _clientPhoneNumber;
    }

    public Calendar getStartTime() {
        return _startTime;
    }

    public Calendar getEndTime() {
        return _endTime;
    }

    public String getFormattedStartTime() {
        return _startTime.get(Calendar.HOUR_OF_DAY) + ":" + _startTime.get(Calendar.MINUTE);
    }

    public String getFormattedEndTime() {
        return _endTime.get(Calendar.HOUR_OF_DAY) + ":" + _endTime.get(Calendar.MINUTE);
    }

    public int getDuration() {
        return _storeTask != null ? _storeTask.getDuration() : 0;
    }

    public void setStoreTask(StoreTask storeTask_p) {
        _storeTask = storeTask_p;
    }

    public void setClientName(String clientName_p) {
        _clientName = clientName_p;
    }

    public void setClientPhoneNumber(String clientPhoneNumber_p) {
        _clientPhoneNumber = clientPhoneNumber_p;
    }

    public void setStartTime(int startHour_p, int startMinute_p) {
        _startTime.set(Calendar.HOUR_OF_DAY, startHour_p);
        _startTime.set(Calendar.MINUTE, startMinute_p);
        _endTime.setTimeInMillis(_startTime.getTimeInMillis() + getDuration() * 60000);
    }

    public void setEndTime(int endHour_p, int endMinute_p) {
        _endTime.set(Calendar.HOUR_OF_DAY, endHour_p);
        _endTime.set(Calendar.MINUTE, endMinute_p);
    }

    public void setStartTime(Calendar calendar_p) {
        setStartTime(calendar_p.get(Calendar.HOUR_OF_DAY), calendar_p.get(Calendar.MINUTE));
    }

    public void setEndTime(Calendar calendar_p) {
        setEndTime(calendar_p.get(Calendar.HOUR_OF_DAY), calendar_p.get(Calendar.MINUTE));
    }

    public boolean isBefore(Calendar calendar_p) {
        return getEndTime().before(calendar_p);
    }

    public boolean isBefore(StoreAppointment appointment_p) {
        return _endTime.before(appointment_p.getStartTime());
    }

    public boolean isAfter(Calendar calendar_p) {
        return getStartTime().after(calendar_p);
    }

    public boolean isAfter(StoreAppointment appointment_p) {
        return _startTime.after(appointment_p.getEndTime());
    }

    public int gapWith(Calendar calendar_p) {
        int gap;
        if (isAfter(calendar_p)) {
            gap = (int) (_startTime.getTimeInMillis() - calendar_p.getTimeInMillis()) / 60000;
        } else {
            gap = (int) (calendar_p.getTimeInMillis() - _endTime.getTimeInMillis()) / 60000;
        }
        return gap;
    }

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

    public NullStoreAppointment newNullInstance() {
        return new NullStoreAppointment();
    }

    public class NullStoreAppointment extends StoreAppointment {

        @Override
        public void setStartTime(int startHour_p, int startMinute_p) {
            _startTime.set(Calendar.HOUR_OF_DAY, startHour_p);
            _startTime.set(Calendar.MINUTE, startMinute_p);
        }

        @Override
        public int getDuration() {
            return (int) (_endTime.getTimeInMillis() - _startTime.getTimeInMillis()) / 60000;
        }
    }

}
