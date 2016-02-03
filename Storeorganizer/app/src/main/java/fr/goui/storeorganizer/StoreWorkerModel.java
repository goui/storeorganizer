package fr.goui.storeorganizer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

public class StoreWorkerModel extends Observable {

    private static StoreWorkerModel ourInstance = new StoreWorkerModel();

    private List<StoreWorker> _workers;

    private int _maxId;

    public static StoreWorkerModel getInstance() {
        return ourInstance;
    }

    private StoreWorkerModel() {
        _workers = new ArrayList<>();
    }

    public void setMaxId(int maxId_p) {
        _maxId = maxId_p;
    }

    public int getMaxId() {
        return _maxId;
    }

    public List<StoreWorker> getStoreWorkers() {
        return _workers;
    }

    public int getStoreWorkersNumber() {
        return _workers.size();
    }

    public StoreWorker getStoreWorker(int position_p) {
        return _workers.get(position_p);
    }

    public int getStoreWorkerPosition(StoreWorker storeWorker_p) {
        int position = -1;
        for (int i = 0; i < _workers.size(); i++) {
            if (_workers.get(i).getId() == storeWorker_p.getId()) {
                position = i;
            }
        }
        return position;
    }

    public void addStoreWorker(String name_p, int id_p) {
        _workers.add(new StoreWorker(name_p, id_p));
    }

    public int addStoreWorker(String name_p) {
        _maxId++;
        StoreWorker storeWorker = new StoreWorker(name_p, _maxId);
        _workers.add(storeWorker);
        setChanged();
        notifyObservers(new ObsData(storeWorker, _workers.size() - 1, ObsData.CREATE));
        return _maxId;
    }

    public int updateStoreWorker(int position_p, String name_p) {
        StoreWorker storeWorker = _workers.get(position_p);
        storeWorker.setName(name_p);
        setChanged();
        notifyObservers(new ObsData(storeWorker, position_p, ObsData.UPDATE));
        return storeWorker.getId();
    }

    public int removeStoreWorker(int position_p) {
        StoreWorker worker = _workers.get(position_p);
        _workers.remove(position_p);
        setChanged();
        notifyObservers(new ObsData(worker, position_p, ObsData.REMOVE));
        return worker.getId();
    }

    public StoreWorker getFirstAvailableWorker() {
        Date now = new Date();
        StoreWorker firstWorker = _workers.get(0);
        StoreAppointment firstAvailableAppointment = firstWorker.getNextAvailability();
        Date firstAvailableTime = now;
        if (firstAvailableAppointment != null) {
            if (firstAvailableAppointment instanceof StoreAppointment.NullStoreAppointment) {
                firstAvailableTime = firstAvailableAppointment.getStartDate();
            } else {
                firstAvailableTime = firstAvailableAppointment.getEndDate();
            }
        }
        for (int i = 1; i < _workers.size(); i++) {
            StoreWorker currentWorker = _workers.get(i);
            StoreAppointment currentAvailableAppointment = currentWorker.getNextAvailability();
            Date currentAvailableTime = now;
            if (currentAvailableAppointment != null) {
                if (currentAvailableAppointment instanceof StoreAppointment.NullStoreAppointment) {
                    currentAvailableTime = currentAvailableAppointment.getStartDate();
                } else {
                    currentAvailableTime = currentAvailableAppointment.getEndDate();
                }
            }
            if (currentAvailableTime.before(firstAvailableTime)) {
                firstWorker = _workers.get(i);
            }
        }
        return firstWorker;
    }

    public void clear(String name_p) {
        _maxId = 0;
        _workers.clear();
        StoreWorker storeWorker = new StoreWorker(name_p, 0);
        _workers.add(storeWorker);
        setChanged();
        notifyObservers(new ObsData(storeWorker, 0, ObsData.REMOVE_ALL));
    }

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
