package fr.goui.storeorganizer.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Observable;

import fr.goui.storeorganizer.listener.OnAppointmentClickListener;
import fr.goui.storeorganizer.listener.OnAppointmentCreateListener;
import fr.goui.storeorganizer.listener.OnTimeTickListener;
import fr.goui.storeorganizer.view.OverallView;
import fr.goui.storeorganizer.R;
import fr.goui.storeorganizer.model.StoreAppointment;
import fr.goui.storeorganizer.model.StoreModel;
import fr.goui.storeorganizer.model.StoreWorkerModel;

/**
 * {@code OverallFragment} is the container of the {@link OverallView}.
 * It also contains a layout displaying the workers names.
 */
public class OverallFragment extends Fragment implements OnAppointmentClickListener, OnTimeTickListener, OnAppointmentCreateListener {

    /**
     * The layout displaying the workers names.
     */
    private LinearLayout mNamesLayout;

    /**
     * The view displaying all appointments for all workers.
     */
    private OverallView mOverallView;

    /**
     * The size of the screen.
     */
    private Point mScreenSize = new Point();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // getting the layout and its views
        View rootView = inflater.inflate(R.layout.fragment_overall, container, false);
        mNamesLayout = (LinearLayout) rootView.findViewById(R.id.fragment_overall_names_layout);
        mOverallView = (OverallView) rootView.findViewById(R.id.fragment_overall_view);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // getting the parent activity
        FragmentActivity activity = getActivity();
        activity.getWindowManager().getDefaultDisplay().getSize(mScreenSize);

        // displaying the workers names
        fillNamesLayout(activity);

        // when the overall view is clicked we receive the event
        mOverallView.setOnAppointmentClickListener(this);
    }

    /**
     * Method used to fill the layout displaying the workers names.
     *
     * @param activity the context
     */
    private void fillNamesLayout(FragmentActivity activity) {
        int nbOfWorkers = StoreWorkerModel.getInstance().getStoreWorkersNumber();
        int cellWidth = mScreenSize.x / (nbOfWorkers + 2);
        for (int i = 0; i < nbOfWorkers; i++) {
            TextView textView = new TextView(activity);
            String name = StoreWorkerModel.getInstance().getStoreWorker(i).getName();
            textView.setText(name);
            textView.setTextColor(Color.BLACK);
            textView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            textView.setGravity(Gravity.CENTER);
            textView.measure(0, 0);
            int width = textView.getMeasuredWidth();
            if (width >= cellWidth) {
                textView.setText(String.valueOf(name.charAt(0)));
            }
            mNamesLayout.addView(textView);
        }
    }

    @Override
    public void onAppointmentClicked(StoreAppointment storeAppointment) {
        FragmentActivity activity = getActivity();

        // when an appointment is clicked in the overall view, we open a dialog displaying all the information
        View dialogLayout = activity.getLayoutInflater().inflate(R.layout.layout_appointment_information, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomDialogTheme);
        builder.setView(dialogLayout);
        TextView txtTaskName = (TextView) dialogLayout.findViewById(R.id.layout_appointment_information_task_name_text_view);
        txtTaskName.setText(storeAppointment.getStoreTask().getName());
        TextView txtClientName = (TextView) dialogLayout.findViewById(R.id.layout_appointment_information_clients_name_text_view);
        txtClientName.setText(storeAppointment.getClientName());
        TextView txtPhoneNumber = (TextView) dialogLayout.findViewById(R.id.layout_appointment_information_clients_phone_text_view);
        txtPhoneNumber.setText(storeAppointment.getClientPhoneNumber());
        builder.setCancelable(true)
                .setTitle(storeAppointment.getFormattedStartTime() + " - " + storeAppointment.getFormattedEndTime())
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    @Override
    public void onTimeTick() {
        mOverallView.invalidate();
    }

    @Override
    public void onAppointmentCreate(int workerPosition) {
        mOverallView.invalidate();
    }

    public void update(Observable observable, Object data) {

        // the workers model has changed, resetting the names layout and notifying the overall view
        if (observable instanceof StoreWorkerModel) {
            mNamesLayout.removeAllViews();
            fillNamesLayout(getActivity());
            mOverallView.onWorkersChanged();
        }

        // the store model has changed, notifying the overall view
        else if (observable instanceof StoreModel) {
            mOverallView.onWorkingTimesChanged();
        }
    }

    /**
     * Redirects the zoom event to {@code OverallView}.
     *
     * @param scaleFactor the zoom factor
     */
    public void onScaleChanged(float scaleFactor) {
        mOverallView.onScaleChanged(scaleFactor);
    }
}
