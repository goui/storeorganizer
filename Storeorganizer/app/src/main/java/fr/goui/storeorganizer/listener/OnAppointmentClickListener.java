package fr.goui.storeorganizer.listener;

import fr.goui.storeorganizer.model.StoreAppointment;

/**
 * {@code OnAppointmentClickListener} is an interface used to trigger appointment click events.
 */
public interface OnAppointmentClickListener {

    /**
     * Callback used when an appointment has been clicked.
     *
     * @param storeAppointment the clicked {@code StoreAppointment}
     */
    void onAppointmentClicked(StoreAppointment storeAppointment);
}
