package fr.goui.storeorganizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class StoreWorkerModel extends Observable {

    private static StoreWorkerModel ourInstance = new StoreWorkerModel();

    private List<StoreWorker> _workers;

    private List<String> _workersNames;

    public static StoreWorkerModel getInstance() {
        return ourInstance;
    }

    private StoreWorkerModel() {
        _workers = new ArrayList<>();
        _workersNames = new ArrayList<>();
    }

    public List<StoreWorker> getStoreWorkers() {
        return _workers;
    }

    public List<String> getStoreWorkersNames() {
        return _workersNames;
    }

    public int getStoreWorkerNumber() {
        return _workers.size();
    }

    public StoreWorker getStoreWorker(int position_p) {
        return _workers.get(position_p);
    }

    public void addStoreWorker(StoreWorker worker_p) {
        _workers.add(worker_p);
        _workersNames.add(worker_p.getName());
        setChanged();
        notifyObservers(new ObsData(worker_p, _workers.size() - 1, ObsData.CREATION));
    }

    public void updateStoreWorker(int position_p, String name_p) {
        _workers.get(position_p).setName(name_p);
        _workersNames.set(position_p, name_p);
        setChanged();
        notifyObservers(new ObsData( _workers.get(position_p), position_p, ObsData.UPDATE));
    }

    public class ObsData {
        public static final int CREATION = 0;
        public static final int UPDATE = 1;
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
