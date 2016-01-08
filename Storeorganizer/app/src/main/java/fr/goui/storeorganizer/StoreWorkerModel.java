package fr.goui.storeorganizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galliano on 1/8/16.
 */
public class StoreWorkerModel {
    private static StoreWorkerModel ourInstance = new StoreWorkerModel();

    private List<StoreWorker> _workers;

    public static StoreWorkerModel getInstance() {
        return ourInstance;
    }

    private StoreWorkerModel() {
        _workers = new ArrayList<>();
    }

    public int getStoreWorkerNumber() {
        return _workers.size();
    }

    public StoreWorker getStoreWorker(int position_p) {
        return _workers.get(position_p);
    }

    public void addStoreWorker(StoreWorker worker_p) {
        _workers.add(worker_p);
    }
}
