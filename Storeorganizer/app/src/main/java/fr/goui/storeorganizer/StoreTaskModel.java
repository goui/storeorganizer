package fr.goui.storeorganizer;

import java.util.ArrayList;
import java.util.List;

public class StoreTaskModel {
    private static StoreTaskModel ourInstance = new StoreTaskModel();

    private List<StoreTask> _tasks;

    public static StoreTaskModel getInstance() {
        return ourInstance;
    }

    private StoreTaskModel() {
        _tasks = new ArrayList<>();
    }

    public int getStoreTaskNumber() {
        return _tasks.size();
    }

    public List<StoreTask> getStoreTasks() {
        return _tasks;
    }

    public void addStoreTask(StoreTask storeTask_p) {
        _tasks.add(storeTask_p);
    }

    public void updateStoreTask(int position_p, String name_p, int duration_p) {
        _tasks.get(position_p).setName(name_p);
        _tasks.get(position_p).setDuration(duration_p);
    }

    public void removeStoreTask(int position_p) {
        _tasks.remove(position_p);
    }

}
