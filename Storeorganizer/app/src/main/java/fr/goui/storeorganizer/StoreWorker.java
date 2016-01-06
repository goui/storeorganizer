package fr.goui.storeorganizer;

import java.util.ArrayList;
import java.util.List;

public class StoreWorker {

    private static int _id;

    private String _name;

    private List<StoreTask> _tasks;

    public StoreWorker(String name_p) {
        _id++;
        _name = name_p;
        _tasks = new ArrayList<>();
    }

    public String getName() {
        return _name;
    }

    public static int getId() {
        return _id;
    }

    public List<StoreTask> getTasks() {
        return _tasks;
    }
}
