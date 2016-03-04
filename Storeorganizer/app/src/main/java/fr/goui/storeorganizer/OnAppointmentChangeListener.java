package fr.goui.storeorganizer;

/**
 * {@code OnAppointmentChangeListener} is an interface used to trigger appointment edition and deletion events.
 */
public interface OnAppointmentChangeListener {

    /**
     * Callback used when an appointment has to be edited.
     *
     * @param position_p the position of the be edited appointment
     */
    void onAppointmentEdit(int position_p);

    /**
     * Callback used when an appointment has to be deleted.
     *
     * @param position_p the position of the be deleted appointment
     */
    void onAppointmentDelete(int position_p);

}
