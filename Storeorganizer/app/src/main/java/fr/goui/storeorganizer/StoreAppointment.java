package fr.goui.storeorganizer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StoreAppointment {

    private StoreTask _storeTask;

    private String _clientName;

    private String _clientPhoneNumber;

    private Date _startDate;

    private Date _endDate;

    private SimpleDateFormat _simpleDateFormat;

    private String _formattedStartDate;

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

}
