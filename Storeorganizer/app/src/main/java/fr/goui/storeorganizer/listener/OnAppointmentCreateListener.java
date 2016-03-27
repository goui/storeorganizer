package fr.goui.storeorganizer.listener;

/**
 * {@code OnAppointmentCreateListener} is an interface used to trigger appointment creation events.
 */
public interface OnAppointmentCreateListener {

    /**
     * Callback used when an appointment has to be created.
     *
     * @param workerPosition the position of the worker
     */
    void onAppointmentCreate(int workerPosition);
}
