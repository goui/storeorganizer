package fr.goui.storeorganizer;

import java.util.ArrayList;
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

    public List<StoreWorker> getStoreWorkers() {
        return _workers;
    }

    public int getStoreWorkerNumber() {
        return _workers.size();
    }

    public StoreWorker getStoreWorker(int position_p) {
        return _workers.get(position_p);
    }

    public void addStoreWorker(String name_p, int id_p) {
        _workers.add(new StoreWorker(name_p, id_p));
    }

    public int addStoreWorker(String name_p) {
        _maxId++;
        StoreWorker storeWorker = new StoreWorker(name_p, _maxId);
        _workers.add(storeWorker);
        setChanged();
        notifyObservers(new ObsData(storeWorker, _workers.size() - 1, ObsData.CREATION));
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
        notifyObservers(new ObsData(worker, position_p, ObsData.REMOVAL));
        return worker.getId();
    }

    public class ObsData {
        public static final int CREATION = 0;
        public static final int UPDATE = 1;
        public static final int REMOVAL = 2;
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
