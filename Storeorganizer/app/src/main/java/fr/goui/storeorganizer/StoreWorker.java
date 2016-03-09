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
     * The list of {@code StoreAppointment}s that can be accessed.
     * Can contain gaps (aka {@link NullStoreAppointment}).
     */
    private List<StoreAppointment> _publicAppointmentsList;

    /**
     * The list of {@code StoreAppointment}s we use locally.
     * It is free from gaps.
     */
    private List<StoreAppointment> _internalAppointmentsList;

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
        _publicAppointmentsList = new ArrayList<>();
        _internalAppointmentsList = new ArrayList<>();
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
        return _publicAppointmentsList.size();
    }

    /**
     * Getter for a specific {@code StoreAppointment} given its position.
     *
     * @param position_p the position of the {@code StoreAppointment}
     * @return the {@code StoreAppointment} at the given position
     */
    public StoreAppointment getStoreAppointment(int position_p) {
        return _publicAppointmentsList.get(position_p);
    }

    /**
     * Getter for the list of {@code StoreAppointment}s.
     *
     * @return the list of {@code StoreAppointment}s
     */
    public List<StoreAppointment> getStoreAppointments() {
        return _publicAppointmentsList;
    }

    /**
     * Adds a {@code StoreAppointment}.
     * It will sort the list of {@code StoreAppointment}s
     * and create gap items if needed.
     *
     * @param storeAppointment_p the {@code StoreAppointment} to add
     */
    public void addStoreAppointment(StoreAppointment storeAppointment_p) {
        StoreAppointment lastAppointment = getLastAppointment();
        storeAppointment_p.getStartTime().add(Calendar.SECOND, 1);
        _internalAppointmentsList.add(storeAppointment_p);
        if (lastAppointment != null && storeAppointment_p.getEndTime().before(lastAppointment.getEndTime())) {
            sortAppointments();
        }
        createGapsIfNeeded();
    }

    /**
     * Removes a {@code StoreAppointment}.
     *
     * @param storeAppointment_p the {@code StoreAppointment} to remove
     */
    public void removeStoreAppointment(StoreAppointment storeAppointment_p) {
        _internalAppointmentsList.remove(storeAppointment_p);
        createGapsIfNeeded();
    }

    /**
     * Sorts all {@code StoreAppointment}s depending on their start time.
     */
    public void sortAppointments() {
        Collections.sort(_internalAppointmentsList, StoreAppointment.Comparators.START_TIME);
    }

    /**
     * Getter for the last {@code StoreAppointment}.
     * Returns null if there is no {@code StoreAppointment}.
     *
     * @return the last {@code StoreAppointment}
     */
    public StoreAppointment getLastAppointment() {
        return _publicAppointmentsList != null && _publicAppointmentsList.size() > 0 ? _publicAppointmentsList.get(_publicAppointmentsList.size() - 1) : null;
    }

    /**
     * Checks if there is a {@code StoreAppointment} before the given position.
     *
     * @param position_p the position
     * @return {@code true} if there is a {@code StoreAppointment} before the given position, {@code false} otherwise
     */
    public boolean isThereAppointmentBefore(int position_p) {
        return _publicAppointmentsList != null && _publicAppointmentsList.size() > 0 && position_p > 0;
    }

    /**
     * Checks if there is a {@code StoreAppointment} after the given position.
     *
     * @param position_p the position
     * @return {@code true} if there is a {@code StoreAppointment} after the given position, {@code false} otherwise
     */
    public boolean isThereAppointmentAfter(int position_p) {
        return _publicAppointmentsList != null && _publicAppointmentsList.size() > 0 && position_p < _publicAppointmentsList.size() - 1;
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
        for (StoreAppointment currentAppointment : _publicAppointmentsList) {
            if (currentAppointment instanceof NullStoreAppointment
                    && !isThereAGap) {
                appointment = currentAppointment;
                isThereAGap = true;
            }
        }
        if (!isThereAGap) {
            Calendar now = Calendar.getInstance();
            // we don't want to consider seconds and milliseconds
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);
            StoreAppointment lastAppointment = getLastAppointment();
            if (lastAppointment != null) {
                if (lastAppointment.getEndTime().after(now)) {
                    appointment = lastAppointment;
                }
            }
        }
        return appointment;
    }

    /**
     * Method used to create {@link NullStoreAppointment}s
     * if there are gaps between two consecutive {@code StoreAppointment}s.
     */
    private void createGapsIfNeeded() {

        // resetting the public list of appointments
        _publicAppointmentsList.clear();

        // if there is at least one appointment in the list
        if (_internalAppointmentsList.size() > 0) {

            // copying the first element of the list
            _publicAppointmentsList.add(_internalAppointmentsList.get(0));

            // getting the current time
            Calendar now = Calendar.getInstance();
            // we don't want to consider seconds and milliseconds
            now.add(Calendar.MINUTE, -1);
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);

            // the index to begin the list traversal
            int beginIndex = 1;

            // if we are not dealing with past appointments
            // if there is a gap between now and the first appointment
            if (!_internalAppointmentsList.get(0).isBefore(now)
                    && _internalAppointmentsList.get(0).gapWith(now) >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {

                // creating the gap and adding it to the list
                NullStoreAppointment nullStoreAppointment = new NullStoreAppointment();
                nullStoreAppointment.setStartTime(now);
                nullStoreAppointment.setEndTime(_publicAppointmentsList.get(0).getStartTime());
                _publicAppointmentsList.add(0, nullStoreAppointment);

                // updating the index
                beginIndex = 2;
            }

            // for all items of the list
            for (int i = beginIndex; i < _internalAppointmentsList.size(); i++) {

                // copying current appointment
                _publicAppointmentsList.add(_internalAppointmentsList.get(i));

                // if one of the appointments is not a gap
                // if we are not dealing with past appointments
                // checking if there is a gap between current item and the previous
                if ((!(_publicAppointmentsList.get(i - 1) instanceof NullStoreAppointment) && !(_publicAppointmentsList.get(i) instanceof NullStoreAppointment))
                        && !_publicAppointmentsList.get(i).isBefore(now)
                        && _publicAppointmentsList.get(i).gapWith(_publicAppointmentsList.get(i - 1)) >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {

                    // creating the gap and adding it to the list
                    NullStoreAppointment nullStoreAppointment = new NullStoreAppointment();
                    nullStoreAppointment.setStartTime(_publicAppointmentsList.get(i - 1).getEndTime());
                    nullStoreAppointment.setEndTime(_publicAppointmentsList.get(i).getStartTime());
                    _publicAppointmentsList.add(i, nullStoreAppointment);

                    // updating the index
                    i++;
                }
            }
        }
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
