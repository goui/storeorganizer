package fr.goui.storeorganizer;

public class StoreTask {

    private String _name;

    private int _duration;

    public StoreTask(String name_p, int duration_p) {
        _name = name_p;
        _duration = duration_p;
    }

    public String getName() {
        return _name;
    }

    public int getDuration() {
        return _duration;
    }
}
