package fr.goui.storeorganizer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * {@code StoreWorker} is a class representing a worker.
 * It contains a name and a list of {@code StoreAppointment}s.
 * It also has an unique id used for storage in the {@code SharedPreferences}.
 */
public class StoreWorker {

    /**
     * The {@code int} representing the unique id of the {@code StoreWorker}.
     */
    private int _id;

    /**
     * The {@code String} representing the name of the {@code StoreWorker}.
     */
    private String _name;

    /**
     * The list of {@code StoreAppointment}s.
     */
    private List<StoreAppointment> _appointments;

    /**
     * Constructor passing the name and the unique id.
     * Please use {@link StoreWorkerModel} for creation, modification or deletion.
     *
     * @param name_p the name of the {@code StoreWorker}
     * @param id_p   the unique id of the {@code StoreWorker}
     */
    public StoreWorker(String name_p, int id_p) {
        _id = id_p;
        _name = name_p;
        _appointments = new ArrayList<>();
    }

    /**
     * Getter for the name of the worker.
     *
     * @return the worker's name {@code String}
     */
    public String getName() {
        return _name;
    }

    /**
     * Setter for the name of the {@code StoreWorker}.
     *
     * @param name_p the new name of the worker
     */
    public void setName(String name_p) {
        _name = name_p;
    }

    /**
     * Getter for the unique id of the worker.
     *
     * @return the worker's unique id {@code int}
     */
    public int getId() {
        return _id;
    }

    /**
     * Getter for the number of {@code StoreAppointment}s.
     *
     * @return the number of appointments {@code int}
     */
    public int getStoreAppointmentsNumber() {
        return _appointments.size();
    }

    /**
     * Getter for a specific {@code StoreAppointment} given its position.
     *
     * @param position_p the position of the {@code StoreAppointment}
     * @return the {@code StoreAppointment} at the given position
     */
    public StoreAppointment getStoreAppointment(int position_p) {
        return _appointments.get(position_p);
    }

    /**
     * Getter for the list of {@code StoreAppointment}s.
     *
     * @return the list of {@code StoreAppointment}s
     */
    public List<StoreAppointment> getStoreAppointments() {
        return _appointments;
    }

    /**
     * Adds a {@code StoreAppointment}.
     *
     * @param storeAppointment_p the {@code StoreAppointment} to add
     */
    public void addStoreAppointment(StoreAppointment storeAppointment_p) {
        _appointments.add(storeAppointment_p);
    }

    /**
     * Removes a {@code StoreAppointment}.
     *
     * @param storeAppointment_p the {@code StoreAppointment} to remove
     */
    public void removeStoreAppointment(StoreAppointment storeAppointment_p) {
        _appointments.remove(storeAppointment_p);
    }

    /**
     * Sorts all {@code StoreAppointment}s depending on their start time.
     */
    public void sortAppointments() {
        Collections.sort(_appointments, StoreAppointment.Comparators.START_TIME);
    }

    /**
     * Getter for the last {@code StoreAppointment}.
     * Returns null if there is no {@code StoreAppointment}.
     *
     * @return the last {@code StoreAppointment}
     */
    public StoreAppointment getLastAppointment() {
        return _appointments != null && _appointments.size() > 0 ? _appointments.get(_appointments.size() - 1) : null;
    }

    /**
     * Checks if there is a {@code StoreAppointment} before the given position.
     *
     * @param position_p the position
     * @return {@code true} if there is a {@code StoreAppointment} before the given position, {@code false} otherwise
     */
    public boolean isThereAppointmentBefore(int position_p) {
        return _appointments != null && _appointments.size() > 0 && position_p > 0;
    }

    /**
     * Checks if there is a {@code StoreAppointment} after the given position.
     *
     * @param position_p the position
     * @return {@code true} if there is a {@code StoreAppointment} after the given position, {@code false} otherwise
     */
    public boolean isThereAppointmentAfter(int position_p) {
        return _appointments != null && _appointments.size() > 0 && position_p < _appointments.size() - 1;
    }

    /**
     * Method used to get the next availability.
     * It can either be the first {@code NullStoreAppointment} or the last {@code StoreAppointment}.
     *
     * @return the first {@code NullStoreAppointment} or the last {@code StoreAppointment}
     */
    public StoreAppointment getNextAvailability() {
        StoreAppointment appointment = null;
        boolean isThereAGap = false;
        for (StoreAppointment currentAppointment : _appointments) {
            if (currentAppointment instanceof NullStoreAppointment
                    && !isThereAGap) {
                appointment = currentAppointment;
                isThereAGap = true;
            }
        }
        if (!isThereAGap) {
            Calendar now = Calendar.getInstance();
            StoreAppointment lastAppointment = getLastAppointment();
            if (lastAppointment != null) {
                if (lastAppointment.getEndTime().after(now)) {
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
