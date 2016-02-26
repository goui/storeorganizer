package fr.goui.storeorganizer;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code StoreTaskModel} is a singleton class responsible for creating and getting {@code StoreTask}s throughout the code.
 * It manages the unique ids given to {@code StoreTask}s.
 * It has the global information about {@code StoreTask}s like their number, the minimum duration in minutes and the current maximum unique id.
 */
public class StoreTaskModel {

    /**
     * The unique instance of the {@code StoreTaskModel}.
     */
    private static StoreTaskModel _instance = new StoreTaskModel();

    /**
     * The current maximum unique id that can be given to {@code StoreTask}s.
     */
    private int _maxId;

    /**
     * The list of all the {@code StoreTask}s.
     */
    private List<StoreTask> _tasks;

    /**
     * The duration in minutes of the {@code StoreTask} that takes less time.
     */
    private int _minTimeInMinutes;

    /**
     * Getter for the unique instance of {@code StoreTaskModel}.
     *
     * @return the unique instance of {@code StoreTaskModel}
     */
    public static StoreTaskModel getInstance() {
        return _instance;
    }

    /**
     * Private constructor of this singleton class.
     */
    private StoreTaskModel() {
        _tasks = new ArrayList<>();
    }

    /**
     * Setter for the maximum unique id that can be given to {@code StoreTask}s.
     * Please do not use.
     *
     * @param maxId_p the maximum unique id
     */
    public void setMaxId(int maxId_p) {
        _maxId = maxId_p;
    }

    /**
     * Getter for the maximum unique id that can be given to {@code StoreTask}s.
     *
     * @return the maximum unique id {@code int}
     */
    public int getMaxId() {
        return _maxId;
    }

    /**
     * Getter for the number of {@code StoreTask}s.
     *
     * @return the number of {@code StoreTask}s
     */
    public int getStoreTaskNumber() {
        return _tasks.size();
    }

    /**
     * Getter for the list of {@code StoreTask}s.
     * For creation, deletion and modification please use existing methods.
     *
     * @return the list of {@code StoreTask}s
     */
    public List<StoreTask> getStoreTasks() {
        return _tasks;
    }

    /**
     * Getter for a specific {@code StoreTask} given its position.
     *
     * @param position_p the position of the {@code StoreTask}
     * @return the {@code StoreTask} at the given position
     */
    public StoreTask getStoreTask(int position_p) {
        return _tasks.get(position_p);
    }

    /**
     * Getter for the duration in minutes of the {@code StoreTask} that takes less time.
     *
     * @return the minimum duration in minutes
     */
    public int getMinTimeInMinutes() {
        return _minTimeInMinutes;
    }

    /**
     * Method used to update the minimum duration in minutes in case of {@code StoreTask} creation, modification or deletion.
     *
     * @param duration_p the duration in minutes of the created, modified or deleted {@code StoreTask}
     */
    private void updateMinTime(int duration_p) {
        if (_minTimeInMinutes == 0 || duration_p < _minTimeInMinutes) {
            _minTimeInMinutes = duration_p;
        }
    }

    /**
     * Adds a {@code StoreTask} and specifying its unique id.
     * Method used after recuperation in the SharedPreferences.
     * Please do not use.
     *
     * @param name_p     the name of the {@code StoreTask}
     * @param duration_p the duration in minutes of the {@code StoreTask}
     * @param id_p       the unique id of the {@code StoreTask}
     */
    public void addStoreTask(String name_p, int duration_p, int id_p) {
        _tasks.add(new StoreTask(name_p, duration_p, id_p));
        updateMinTime(duration_p);
    }

    /**
     * Adds a {@code StoreTask} and returns its unique id after creation.
     *
     * @param name_p     the name of the {@code StoreTask}
     * @param duration_p the duration in minutes of the {@code StoreTask}
     * @return the generated unique id of the {@code StoreTask}
     */
    public int addStoreTask(String name_p, int duration_p) {
        _maxId++;
        _tasks.add(new StoreTask(name_p, duration_p, _maxId));
        updateMinTime(duration_p);
        return _maxId;
    }

    /**
     * Updates the {@code StoreTask} located at the given position.
     * Its name and duration will be updated.
     *
     * @param position_p the position of the {@code StoreTask} to update
     * @param name_p     the new name of the {@code StoreTask}
     * @param duration_p the new duration of the {@code StoreTask}
     * @return the unique id of the updated {@code StoreTask}
     */
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

    /**
     * Removes the {@code StoreTask} located at the given position.
     *
     * @param position_p the position of the {@code StoreTask} to remove
     * @return the unique id of the removed {@code StoreTask}
     */
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

    /**
     * Clears all the {@code StoreTask}s and creates a default one.
     *
     * @param name_p     the name of the default {@code StoreTask}
     * @param duration_p the duration in minutes of the default {@code StoreTask}
     */
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
