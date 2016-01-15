package fr.goui.storeorganizer;

import java.util.ArrayList;
import java.util.List;

public class StoreWorker {

    private int _id;

    private String _name;

    private List<StoreAppointment> _appointments;

    public StoreWorker(String name_p, int id_p) {
        _id = id_p;
        _name = name_p;
        _appointments = new ArrayList<>();
    }

    public String getName() {
        return _name;
    }

    public void setName(String name_p) {
        _name = name_p;
    }

    public int getId() {
        return _id;
    }

    public List<StoreAppointment> getAppointments() {
        return _appointments;
    }
}
