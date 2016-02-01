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

    public void addStoreAppointment(StoreAppointment storeAppointment_p) {
        _appointments.add(storeAppointment_p);
    }

    public void sortAppointments() {
        Collections.sort(_appointments, StoreAppointment.Comparators.TIME);
    }

    public void removeStoreAppointment(StoreAppointment storeAppointment_p) {
        _appointments.remove(storeAppointment_p);
    }

    public StoreAppointment getLastAppointment() {
        return _appointments != null && _appointments.size() > 0 ? _appointments.get(_appointments.size() - 1) : null;
    }

    public boolean isThereAppointmentBefore(int position_p) {
        return _appointments != null && _appointments.size() > 0 && position_p > 0;
    }

    public boolean isThereAppointmentAfter(int position_p) {
        return _appointments != null && _appointments.size() > 0 && position_p < _appointments.size() - 1;
    }

    public StoreAppointment getNextAvailability() {
        StoreAppointment appointment = null;
        boolean isThereAGap = false;
        for (StoreAppointment currentAppointment : _appointments) {
            if (currentAppointment instanceof StoreAppointment.NullStoreAppointment
                    && !isThereAGap) {
                appointment = currentAppointment;
                isThereAGap = true;
            }
        }
        if (!isThereAGap) {
            Date date = new Date();
            StoreAppointment lastAppointment = getLastAppointment();
            if (lastAppointment != null) {
                if (lastAppointment.getEndDate().after(date)) {
                    appointment = lastAppointment;
                }
            }
        }
        return appointment;
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
