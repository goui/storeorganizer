package fr.goui.storeorganizer;

/**
 * {@code OnAppointmentChangeListener} is an interface used to trigger appointment edition and deletion events.
 */
public interface OnAppointmentChangeListener {

    /**
     * Callback used when an appointment has been edited.
     *
     * @param position_p the position of the edited appointment
     */
    void onAppointmentEdit(int position_p);

    /**
     * Callback used when an appointment has been deleted.
     *
     * @param position_p the position of the deleted appointment
     */
    void onAppointmentDelete(int position_p);

}
