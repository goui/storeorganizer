package fr.goui.storeorganizer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;

/**
 * {@code StoreWorkerModel} is a singleton class responsible for creating and getting {@code StoreWorker}s throughout the code.
 * It manages the unique ids given to {@code StoreWorker}s.
 * It has the global information about {@code StoreWorker}s like their number and the current maximum unique id.
 */
public class StoreWorkerModel extends Observable {

    /**
     * The unique instance of the {@code StoreWorkerModel}.
     */
    private static StoreWorkerModel ourInstance = new StoreWorkerModel();

    /**
     * The list of all the {@code StoreWorker}s.
     */
    private List<StoreWorker> _workers;

    /**
     * The current maximum unique id that can be given to {@code StoreWorker}s.
     */
    private int _maxId;

    /**
     * Getter for the unique instance of {@code StoreWorkerModel}.
     *
     * @return the unique instance of {@code StoreWorkerModel}
     */
    public static StoreWorkerModel getInstance() {
        return ourInstance;
    }

    /**
     * Private constructor of this singleton class.
     */
    private StoreWorkerModel() {
        _workers = new ArrayList<>();
    }

    /**
     * Setter for the maximum unique id that can be given to {@code StoreWorker}s.
     * Please do not use.
     *
     * @param maxId_p the maximum unique id
     */
    public void setMaxId(int maxId_p) {
        _maxId = maxId_p;
    }

    /**
     * Getter for the maximum unique id that can be given to {@code StoreWorker}s.
     *
     * @return the maximum unique id {@code int}
     */
    public int getMaxId() {
        return _maxId;
    }

    /**
     * Getter for the list of {@code StoreWorker}s.
     * For creation, deletion and modification please use existing methods.
     *
     * @return the list of {@code StoreWorker}s
     */
    public List<StoreWorker> getStoreWorkers() {
        return _workers;
    }

    /**
     * Getter for the number of {@code StoreWorker}s.
     *
     * @return the number of {@code StoreWorker}s
     */
    public int getStoreWorkersNumber() {
        return _workers.size();
    }

    /**
     * Getter for a specific {@code StoreWorker} given its position.
     *
     * @param position_p the position of the {@code StoreWorker}
     * @return the {@code StoreWorker} at the given position
     */
    public StoreWorker getStoreWorker(int position_p) {
        return _workers.get(position_p);
    }

    /**
     * Adds a {@code StoreWorker} and specifying its unique id.
     * Method used after recuperation in the SharedPreferences.
     * Please do not use.
     *
     * @param name_p the name of the {@code StoreWorker}
     * @param id_p   the unique id of the {@code StoreWorker}
     */
    public void addStoreWorker(String name_p, int id_p) {
        _workers.add(new StoreWorker(name_p, id_p));
    }

    /**
     * Adds a {@code StoreWorker} and returns its unique id after creation.
     *
     * @param name_p the name of the {@code StoreWorker}
     * @return the generated unique id of the {@code StoreWorker}
     */
    public int addStoreWorker(String name_p) {
        _maxId++;
        StoreWorker storeWorker = new StoreWorker(name_p, _maxId);
        _workers.add(storeWorker);
        setChanged();
        notifyObservers(new ObsData(storeWorker, _workers.size() - 1, ObsData.CREATE));
        return _maxId;
    }

    /**
     * Updates the {@code StoreWorker} located at the given position.
     * Its name and duration will be updated.
     *
     * @param position_p the position of the {@code StoreWorker} to update
     * @param name_p     the new name of the {@code StoreWorker}
     * @return the unique id of the updated {@code StoreWorker}
     */
    public int updateStoreWorker(int position_p, String name_p) {
        StoreWorker storeWorker = _workers.get(position_p);
        storeWorker.setName(name_p);
        setChanged();
        notifyObservers(new ObsData(storeWorker, position_p, ObsData.UPDATE));
        return storeWorker.getId();
    }

    /**
     * Removes the {@code StoreWorker} located at the given position.
     *
     * @param position_p the position of the {@code StoreWorker} to remove
     * @return the unique id of the removed {@code StoreWorker}
     */
    public int removeStoreWorker(int position_p) {
        StoreWorker worker = _workers.get(position_p);
        _workers.remove(position_p);
        setChanged();
        notifyObservers(new ObsData(worker, position_p, ObsData.REMOVE));
        return worker.getId();
    }

    /**
     * Getter for the first available {@code StoreWorker}.
     *
     * @return the first available {@code StoreWorker}
     */
    public StoreWorker getFirstAvailableWorker() {
        // TODO fix issue getFirstAvailableWorker and add comments
        Calendar now = Calendar.getInstance();
        StoreWorker firstWorker = _workers.get(0);
        StoreAppointment firstAvailableAppointment = firstWorker.getNextAvailability();
        Calendar firstAvailableTime = now;
        if (firstAvailableAppointment != null) {
            if (firstAvailableAppointment instanceof StoreAppointment.NullStoreAppointment) {
                firstAvailableTime = firstAvailableAppointment.getStartTime();
            } else {
                firstAvailableTime = firstAvailableAppointment.getEndTime();
            }
        }
        for (int i = 1; i < _workers.size(); i++) {
            StoreWorker currentWorker = _workers.get(i);
            StoreAppointment currentAvailableAppointment = currentWorker.getNextAvailability();
            Calendar currentAvailableTime = now;
            if (currentAvailableAppointment != null) {
                if (currentAvailableAppointment instanceof StoreAppointment.NullStoreAppointment) {
                    currentAvailableTime = currentAvailableAppointment.getStartTime();
                } else {
                    currentAvailableTime = currentAvailableAppointment.getEndTime();
                }
            }
            if (currentAvailableTime.before(firstAvailableTime)) {
                firstWorker = _workers.get(i);
            }
        }
        return firstWorker;
    }

    /**
     * Clears all the {@code StoreWorker}s and creates a default one.
     *
     * @param name_p the name of the default {@code StoreWorker}
     */
    public void clear(String name_p) {
        _maxId = 0;
        _workers.clear();
        StoreWorker storeWorker = new StoreWorker(name_p, 0);
        _workers.add(storeWorker);
        setChanged();
        notifyObservers(new ObsData(storeWorker, 0, ObsData.REMOVE_ALL));
    }

    /**
     * {@code ObsData} is a class used to send information when updating observers.
     * It contains a {@code StoreWorker}, its position {@code int} and an update reason {@code int}.
     */
    public class ObsData {
        public static final int CREATE = 0;
        public static final int UPDATE = 1;
        public static final int REMOVE = 2;
        public static final int REMOVE_ALL = 3;
        public StoreWorker worker;
        public int updateReason;
        public int workersPosition;

        public ObsData(StoreWorker worker_p, int workersPosition_p, int updateReason_p) {
            worker = worker_p;
            workersPosition = workersPosition_p;
            updateReason = updateReason_p;
        }
    }

}
