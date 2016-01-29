package fr.goui.storeorganizer;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class StoreAppointment implements Comparable<StoreAppointment> {

    private StoreTask _storeTask;

    private String _clientName;

    private String _clientPhoneNumber;

    protected Date _startDate;

    protected Date _endDate;

    protected SimpleDateFormat _simpleDateFormat;

    protected String _formattedStartDate;

    private String _formattedEndDate;

    public StoreAppointment() {
        _simpleDateFormat = new SimpleDateFormat("HH:mm");
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

    public Date getStartDate() {
        return _startDate;
    }

    public Date getEndDate() {
        return _endDate;
    }

    public String getFormattedStartDate() {
        return _formattedStartDate;
    }

    public String getFormattedEndDate() {
        return _formattedEndDate;
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

    public void setStartDate(Date startDate_p) {
        _startDate = startDate_p;
        _formattedStartDate = _simpleDateFormat.format(_startDate);
        setEndDate(new Date(_startDate.getTime() + _storeTask.getDuration() * 60000));
    }

    public void setEndDate(Date endDate_p) {
        _endDate = endDate_p;
        _formattedEndDate = _simpleDateFormat.format(_endDate);
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof StoreAppointment) {
            StoreAppointment appointment = (StoreAppointment) o;
            equals = appointment.getClientName().equals(getClientName())
                    && appointment.getClientPhoneNumber().equals(getClientPhoneNumber())
                    && appointment.getStoreTask().equals(getStoreTask())
                    && appointment.getStartDate().equals(getStartDate())
                    && appointment.getEndDate().equals(getEndDate());
        }
        return equals;
    }

    @Override
    public String toString() {
        return _clientName + " - " + _storeTask.getName() + " - " + getFormattedStartDate();
    }

    @Override
    public int compareTo(StoreAppointment another) {
        return Comparators.TIME.compare(this, another);
    }

    public static class Comparators {

        public static Comparator<StoreAppointment> TIME = new Comparator<StoreAppointment>() {
            @Override
            public int compare(StoreAppointment appointment1, StoreAppointment appointment2) {
                return appointment1.getStartDate().compareTo(appointment2.getStartDate());
            }
        };
    }

    public NullStoreAppointment newNullInstance() {
        return new NullStoreAppointment();
    }

    public class NullStoreAppointment extends StoreAppointment {

        @Override
        public void setStartDate(Date startDate_p) {
            _startDate = startDate_p;
            _formattedStartDate = _simpleDateFormat.format(_startDate);
        }

        @Override
        public int getDuration() {
            return (int) (_endDate.getTime() - _startDate.getTime()) / 60000;
        }
    }

}
