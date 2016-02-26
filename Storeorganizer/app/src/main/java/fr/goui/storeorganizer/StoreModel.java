package fr.goui.storeorganizer;

import java.util.Observable;

/**
 * {@code StoreModel} is a singleton class responsible for holding all information about the store.
 */
public class StoreModel extends Observable {

    /**
     * The unique instance of the {@code StoreModel}.
     */
    private static StoreModel _instance = new StoreModel();

    /**
     * The {@code int} for the starting hour.
     */
    private int _startingHour;

    /**
     * The {@code int} for the starting minute.
     */
    private int _startingMinute;

    /**
     * The {@code int} for the ending hour.
     */
    private int _endingHour;

    /**
     * The {@code int} for the ending minute.
     */
    private int _endingMinute;

    /**
     * Getter for the unique instance of {@code StoreModel}.
     *
     * @return the unique instance of {@code StoreModel}
     */
    public static StoreModel getInstance() {
        return _instance;
    }

    /**
     * Private constructor of this singleton class.
     */
    private StoreModel() {
    }

    /**
     * Getter for the starting hour.
     *
     * @return the starting hour {@code int}
     */
    public int getStartingHour() {
        return _startingHour;
    }

    /**
     * Setter for the starting hour.
     *
     * @param startingHour_p the new starting hour {@code int}
     */
    private void setStartingHour(int startingHour_p) {
        _startingHour = startingHour_p;
    }

    /**
     * Getter for the starting minute.
     *
     * @return the starting minute {@code int}
     */
    public int getStartingMinute() {
        return _startingMinute;
    }

    /**
     * Setter for the starting minute.
     *
     * @param startingMinute_p the new starting minute {@code int}
     */
    private void setStartingMinute(int startingMinute_p) {
        _startingMinute = startingMinute_p;
    }

    /**
     * Setter for the starting time.
     *
     * @param startingHour_p   the new starting hour {@code int}
     * @param startingMinute_p the new starting minute {@code int}
     */
    public void setStartingTime(int startingHour_p, int startingMinute_p) {
        setStartingHour(startingHour_p);
        setStartingMinute(startingMinute_p);
        setChanged();
        notifyObservers();
    }

    /**
     * Getter for the ending hour.
     *
     * @return the ending hour {@code int}
     */
    public int getEndingHour() {
        return _endingHour;
    }

    /**
     * Setter for the ending hour.
     *
     * @param endingHour_p the new ending hour {@code int}
     */
    private void setEndingHour(int endingHour_p) {
        _endingHour = endingHour_p;
    }

    /**
     * Getter for the ending minute.
     *
     * @return the ending minute {@code int}
     */
    public int getEndingMinute() {
        return _endingMinute;
    }

    /**
     * Setter for the ending minute.
     *
     * @param endingMinute_p the new ending minute {@code int}
     */
    private void setEndingMinute(int endingMinute_p) {
        _endingMinute = endingMinute_p;
    }

    /**
     * Setter for the starting time.
     *
     * @param endingHour_p   the new starting hour {@code int}
     * @param endingMinute_p the new starting minute {@code int}
     */
    public void setEndingTime(int endingHour_p, int endingMinute_p) {
        setEndingHour(endingHour_p);
        setEndingMinute(endingMinute_p);
        setChanged();
        notifyObservers();
    }
}
