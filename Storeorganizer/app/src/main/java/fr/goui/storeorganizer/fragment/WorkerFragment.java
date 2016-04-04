package fr.goui.storeorganizer.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Calendar;

import fr.goui.storeorganizer.R;
import fr.goui.storeorganizer.activity.AppointmentEditionActivity;
import fr.goui.storeorganizer.adapter.WorkerRecyclerAdapter;
import fr.goui.storeorganizer.listener.OnAppointmentChangeListener;
import fr.goui.storeorganizer.model.NullStoreAppointment;
import fr.goui.storeorganizer.model.StoreAppointment;
import fr.goui.storeorganizer.model.StoreWorker;
import fr.goui.storeorganizer.model.StoreWorkerModel;

/**
 * {@code WorkerFragment} is a nested fragment displaying the current worker's list of appointments.
 * If the worker has no appointments, a text saying so will be displayed.
 */
public class WorkerFragment extends Fragment implements OnAppointmentChangeListener {

    /**
     * The position of the nested fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * The position of the current worker.
     */
    private int mWorkerPosition;

    /**
     * The current worker.
     */
    private StoreWorker mCurrentWorker;

    /**
     * The text shown when there is no appointment.
     */
    private TextView mNoAppointmentsTextView;

    /**
     * The recycler view displaying the appointments.
     */
    private RecyclerView mRecyclerView;

    /**
     * The recycler view's adapter.
     */
    private WorkerRecyclerAdapter mWorkerRecyclerAdapter;

    /**
     * The project's resources.
     */
    private Resources mResources;

    /**
     * The {@code SharedPreferences}.
     */
    private SharedPreferences mSharedPreferences;

    /**
     * The model for all the {@code StoreWorker}s.
     */
    private StoreWorkerModel mStoreWorkerModel = StoreWorkerModel.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // indicating that this fragment has its own menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        // getting the layout and the views
        View rootView = inflater.inflate(R.layout.fragment_worker, container, false);
        mNoAppointmentsTextView = (TextView) rootView.findViewById(R.id.fragment_worker_no_appointments_text_view);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_worker_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // activity has been created, getting information
        mWorkerPosition = getArguments().getInt(ARG_SECTION_NUMBER);
        mCurrentWorker = mStoreWorkerModel.getStoreWorker(mWorkerPosition);
        mWorkerRecyclerAdapter = new WorkerRecyclerAdapter(getActivity(), mCurrentWorker.getStoreAppointments(), this);
        mRecyclerView.setAdapter(mWorkerRecyclerAdapter);

        // getting the resources
        mResources = getResources();

        // getting the shared prefs
        mSharedPreferences = getActivity().getSharedPreferences(mResources.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        notifyDataSetChanged();
    }

    @Override
    public void onAppointmentEdit(int position) {
        // going to appointment edition screen
        Intent intent = new Intent(getActivity(), AppointmentEditionActivity.class);
        intent.putExtra(mResources.getString(R.string.intent_extra_appointment_position), position);
        intent.putExtra(mResources.getString(R.string.intent_extra_worker_position), mWorkerPosition);
        startActivity(intent);
    }

    @Override
    public void onAppointmentDelete(int position) {
        // removing the appointment
        mCurrentWorker.removeStoreAppointment(mCurrentWorker.getStoreAppointment(position));
        notifyDataSetChanged();

        // removing it in the shared preferences
        // NOTE : as we don't know which one it is in the shared prefs, we rewrite everything
        int nbOfAppointments = mCurrentWorker.getStoreAppointmentsNumber();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(mResources.getString(R.string.worker) + mCurrentWorker.getId() + mResources.getString(R.string.number_of_appointments),
                nbOfAppointments);
        Gson gson = new Gson();
        String json;
        for (int i = 0; i < nbOfAppointments; i++) {
            StoreAppointment currentAppointment = mCurrentWorker.getStoreAppointment(i);
            if (!(currentAppointment instanceof NullStoreAppointment)) {
                json = gson.toJson(currentAppointment);
                editor.putString(mResources.getString(R.string.worker) + mCurrentWorker.getId()
                        + mResources.getString(R.string.appointment) + i, json);
            }
        }
        editor.apply();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // creating the menu
        inflater.inflate(R.menu.menu_fragment_worker, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // scrolling to the current appointment
        if (id == R.id.action_scroll_to_current) {
            scrollToCurrentAppointment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method used to scroll to the current appointment.
     */
    private void scrollToCurrentAppointment() {
        if (mCurrentWorker.getStoreAppointmentsNumber() > 2) {
            int position = -1;
            for (int i = 0; i < mCurrentWorker.getStoreAppointmentsNumber(); i++) {
                if (Calendar.getInstance().after(mCurrentWorker.getStoreAppointments().get(i).getEndTime())) {
                    position = i + 1;
                }
            }
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(position + 1, 10);
            Toast.makeText(getActivity(), getString(R.string.scrolling_to_the_current_appointment), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updates the list of appointments.
     */
    public void notifyDataSetChanged() {
        if (mCurrentWorker.getStoreAppointmentsNumber() > 0 && mCurrentWorker.getStoreAppointment(0) instanceof NullStoreAppointment) {
            mCurrentWorker.getStoreAppointment(0).setStartTime(Calendar.getInstance());
        }
        mWorkerRecyclerAdapter.notifyDataSetChanged();
        computeNoAppointmentTextViewVisibility();
    }

    /**
     * Method used to toggle the no appointments text view.
     */
    private void computeNoAppointmentTextViewVisibility() {
        if (mCurrentWorker.getStoreAppointmentsNumber() >= 1) {
            mNoAppointmentsTextView.setVisibility(View.GONE);
        } else {
            mNoAppointmentsTextView.setVisibility(View.VISIBLE);
        }
    }

}
