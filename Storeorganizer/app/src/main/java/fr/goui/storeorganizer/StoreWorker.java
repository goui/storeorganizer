package fr.goui.storeorganizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StoreWorker {

    private int _id;

    private String _name;

    private List<StoreAppointment> _appointments;

    public StoreWorker(String name_p, int id_p) {
        _id = id_p;
        _name = name_p;
        _appointments = new ArrayList<>();
    }

    public String getName() {
        return _name;
    }

    public void setName(String name_p) {
        _name = name_p;
    }

    public int getId() {
        return _id;
    }

    public int getStoreAppointmentsNumber() {
        return _appointments.size();
    }

    public StoreAppointment getStoreAppointment(int position_p) {
        return _appointments.get(position_p);
    }

    public List<StoreAppointment> getStoreAppointments() {
        return _appointments;
    }

    public void addStoreAppointment(StoreAppointment storeAppointment_p, boolean needToBeSorted_p) {
        _appointments.add(storeAppointment_p);
        if (needToBeSorted_p) {
            sortAppointments();
        }
    }

    public void sortAppointments() {
        Collections.sort(_appointments, StoreAppointment.Comparators.TIME);
    }

    public void removeStoreAppointment(StoreAppointment storeAppointment_p) {
        _appointments.remove(storeAppointment_p);
    }

    public Date getNextAvailability() {
        Date date = new Date();
        if (_appointments.size() > 0) {
            StoreAppointment lastAppointment = _appointments.get(_appointments.size() - 1);
            if (lastAppointment.getEndDate().after(date)) {
                date = _appointments.get(_appointments.size() - 1).getEndDate();
            }
        }
        return date;
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof StoreWorker) {
            StoreWorker worker = (StoreWorker) o;
            equals = worker.getId() == getId();
        }
        return equals;
    }

    @Override
    public String toString() {
        return _name;
    }

}
