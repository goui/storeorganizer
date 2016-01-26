package fr.goui.storeorganizer;

import java.util.ArrayList;
import java.util.List;

public class StoreTaskModel {

    private static StoreTaskModel ourInstance = new StoreTaskModel();

    private int _maxId;

    private List<StoreTask> _tasks;

    private int _minTimeInMinutes;

    public static StoreTaskModel getInstance() {
        return ourInstance;
    }

    private StoreTaskModel() {
        _tasks = new ArrayList<>();
    }

    public void setMaxId(int maxId_p) {
        _maxId = maxId_p;
    }

    public int getMaxId() {
        return _maxId;
    }

    public int getStoreTaskNumber() {
        return _tasks.size();
    }

    public List<StoreTask> getStoreTasks() {
        return _tasks;
    }

    public StoreTask getStoreTask(int position_p) {
        return _tasks.get(position_p);
    }

    public int getMinTimeInMinutes() {
        return _minTimeInMinutes;
    }

    private void updateMinTime(int duration_p) {
        if (_minTimeInMinutes == 0 || duration_p < _minTimeInMinutes) {
            _minTimeInMinutes = duration_p;
        }
    }

    public void addStoreTask(String name_p, int duration_p, int id_p) {
        _tasks.add(new StoreTask(name_p, duration_p, id_p));
        updateMinTime(duration_p);
    }

    public int addStoreTask(String name_p, int duration_p) {
        _maxId++;
        _tasks.add(new StoreTask(name_p, duration_p, _maxId));
        updateMinTime(duration_p);
        return _maxId;
    }

    public int updateStoreTask(int position_p, String name_p, int duration_p) {
        StoreTask storeTask = _tasks.get(position_p);
        storeTask.setName(name_p);
        if (storeTask.getDuration() == _minTimeInMinutes) {
            _minTimeInMinutes = 0;
        }
        storeTask.setDuration(duration_p);
        for (StoreTask task : _tasks) {
            updateMinTime(task.getDuration());
        }
        return storeTask.getId();
    }

    public int removeStoreTask(int position_p) {
        StoreTask storeTask = _tasks.get(position_p);
        _tasks.remove(position_p);
        if (storeTask.getDuration() == _minTimeInMinutes) {
            _minTimeInMinutes = 0;
            for (StoreTask task : _tasks) {
                updateMinTime(task.getDuration());
            }
        }
        return storeTask.getId();
    }

    public void clear(String name_p, int duration_p) {
        int size = _tasks.size() - 1;
        for (int i = 0; i < size; i++) {
            removeStoreTask(0);
        }
        _minTimeInMinutes = 0;
        updateStoreTask(0, name_p, duration_p);
        _maxId = 0;
    }

}
