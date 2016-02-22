package fr.goui.storeorganizer;

/**
 * {@code StoreTask} is a class representing a doable task.
 * It contains a name and a duration in minutes.
 * It also has an unique id used for storage in the {@code SharedPreferences}.
 */
public class StoreTask {

    /**
     * The {@code int} representing the unique id of the {@code StoreTask}.
     */
    private int _id;

    /**
     * The {@code String} representing the name of the {@code StoreTask}.
     */
    private String _name;

    /**
     * The {@code int} representing the duration in minutes of the {@code StoreTask}.
     */
    private int _duration;

    /**
     * Constructor passing the name, the duration in minutes and the unique id.
     * Please use {@link StoreTaskModel} for creation, modification or deletion.
     *
     * @param name_p     the name of the task
     * @param duration_p the duration in minutes of the task
     * @param id_p       the unique id of the task
     */
    public StoreTask(String name_p, int duration_p, int id_p) {
        _name = name_p;
        _duration = duration_p;
        _id = id_p;
    }

    /**
     * Getter for the unique id of the task.
     *
     * @return the task's unique id {@code int}
     */
    public int getId() {
        return _id;
    }

    /**
     * Getter for the name of the task.
     *
     * @return the task's name {@code String}
     */
    public String getName() {
        return _name;
    }

    /**
     * Getter for the duration in minutes of the task.
     *
     * @return the task's duration {@code int}
     */
    public int getDuration() {
        return _duration;
    }

    /**
     * Setter for the name of the {@code StoreTask}.
     *
     * @param name_p the new name of the task
     */
    public void setName(String name_p) {
        _name = name_p;
    }

    /**
     * Setter for the duration in minutes of the {@code StoreTask}.
     *
     * @param duration_p the duration of the task
     */
    public void setDuration(int duration_p) {
        _duration = duration_p;
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof StoreTask) {
            StoreTask task = (StoreTask) o;
            equals = task.getId() == getId();
        }
        return equals;
    }

    @Override
    public String toString() {
        return _name;
    }

}
