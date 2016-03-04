package fr.goui.storeorganizer;

/**
 * {@code OnAppointmentClickListener} is an interface used to trigger appointment click events.
 */
public interface OnAppointmentClickListener {

    /**
     * Callback used when an appointment has been clicked.
     *
     * @param storeAppointment_p the clicked {@code StoreAppointment}
     */
    void onAppointmentClicked(StoreAppointment storeAppointment_p);
}
