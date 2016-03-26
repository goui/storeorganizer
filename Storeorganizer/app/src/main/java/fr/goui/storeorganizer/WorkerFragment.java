package fr.goui.storeorganizer;

import android.content.Intent;
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

import java.util.Calendar;

public class WorkerFragment extends Fragment implements OnAppointmentChangeListener {

    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String INTENT_EXTRA_APPOINTMENT_POSITION = "intent_extra_appointment_position";
    public static final String INTENT_EXTRA_WORKER_POSITION = "intent_extra_worker_position";

    private int _sectionNumber;

    private StoreWorker _currentWorker;

    private TextView _noAppointmentsTextView;

    private RecyclerView _recyclerView;

    private WorkerRecyclerAdapter _workerRecyclerAdapter;

    private Calendar _calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        _currentWorker = StoreWorkerModel.getInstance().getStoreWorker(_sectionNumber);
        _workerRecyclerAdapter = new WorkerRecyclerAdapter(getActivity(), _currentWorker.getStoreAppointments(), this);
        _recyclerView.setAdapter(_workerRecyclerAdapter);
        _calendar = Calendar.getInstance();
        // we don't want to consider seconds and milliseconds
        _calendar.set(Calendar.SECOND, 0);
        _calendar.set(Calendar.MILLISECOND, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_worker, container, false);
        _noAppointmentsTextView = (TextView) rootView.findViewById(R.id.fragment_worker_no_appointments_text_view);
        _recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_worker_recycler_view);
        _recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        _recyclerView.setHasFixedSize(true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        computeNoAppointmentTextViewVisibility();
    }

    @Override
    public void onAppointmentEdit(int position_p) {
        Intent intent = new Intent(getActivity(), AppointmentEditionActivity.class);
        intent.putExtra(INTENT_EXTRA_APPOINTMENT_POSITION, position_p);
        intent.putExtra(INTENT_EXTRA_WORKER_POSITION, _sectionNumber);
        startActivity(intent);
    }

    @Override
    public void onAppointmentDelete(int position_p) {
        _currentWorker.removeStoreAppointment(_currentWorker.getStoreAppointment(position_p));
        notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_worker, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_scroll_to_current) {
            scrollToCurrentAppointment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void scrollToCurrentAppointment() {
        if (_currentWorker.getStoreAppointmentsNumber() > 2) {
            int position = -1;
            for (int i = 0; i < _currentWorker.getStoreAppointmentsNumber(); i++) {
                if (_calendar.after(_currentWorker.getStoreAppointments().get(i).getEndTime())) {
                    position = i + 1;
                }
            }
            ((LinearLayoutManager) _recyclerView.getLayoutManager()).scrollToPositionWithOffset(position + 1, 10);
            Toast.makeText(getActivity(), getString(R.string.scrolling_to_the_current_appointment), Toast.LENGTH_SHORT).show();
        }
    }

    public void updateList() {
        if (_currentWorker.getStoreAppointmentsNumber() > 0 && _currentWorker.getStoreAppointment(0) instanceof NullStoreAppointment) {
            _currentWorker.getStoreAppointment(0).setStartTime(Calendar.getInstance());
        }
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        _workerRecyclerAdapter.notifyDataSetChanged();
        computeNoAppointmentTextViewVisibility();
    }

    private void computeNoAppointmentTextViewVisibility() {
        if (_currentWorker.getStoreAppointmentsNumber() >= 1) {
            _noAppointmentsTextView.setVisibility(View.GONE);
        } else {
            _noAppointmentsTextView.setVisibility(View.VISIBLE);
        }
    }

}
