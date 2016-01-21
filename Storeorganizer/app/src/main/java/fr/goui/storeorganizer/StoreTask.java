package fr.goui.storeorganizer;

public class StoreTask {

    private int _id;

    private String _name;

    private int _duration;

    public StoreTask(String name_p, int duration_p, int id_p) {
        _name = name_p;
        _duration = duration_p;
        _id = id_p;
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public int getDuration() {
        return _duration;
    }

    public void setName(String name_p) {
        _name = name_p;
    }

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
