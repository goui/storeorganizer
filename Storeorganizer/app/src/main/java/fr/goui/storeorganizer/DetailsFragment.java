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

public class DetailsFragment extends Fragment implements OnAppointmentChangeListener {

    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String INTENT_EXTRA_APPOINTMENT_POSITION = "intent_extra_appointment_position";
    public static final String INTENT_EXTRA_WORKER_POSITION = "intent_extra_worker_position";

    private int _sectionNumber;

    private StoreWorker _currentWorker;

    private TextView _noAppointmentsTextView;

    private RecyclerView _recyclerView;

    private DetailsRecyclerAdapter _detailsRecyclerAdapter;

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
        _detailsRecyclerAdapter = new DetailsRecyclerAdapter(getActivity(), _currentWorker.getStoreAppointments(), this);
        _recyclerView.setAdapter(_detailsRecyclerAdapter);
        _calendar = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        _noAppointmentsTextView = (TextView) rootView.findViewById(R.id.fragment_details_no_appointments_text_view);
        _recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_details_recycler_view);
        _recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    @Override
    public void onAppointmentEdit(int position_p) {
        Intent intent = new Intent(getActivity(), AppointmentEditionActivity.class);
        intent.putExtra(INTENT_EXTRA_APPOINTMENT_POSITION, position_p);
        intent.putExtra(INTENT_EXTRA_WORKER_POSITION, _sectionNumber);
        getActivity().startActivityForResult(intent, DetailsActivity.REQUEST_CODE_EDIT_APPOINTMENT);
    }

    @Override
    public void onAppointmentDelete(int position_p) {
        // TODO simplify appointment deletion
        Calendar now = Calendar.getInstance();

        // if only item, remove appointment
        if (position_p == 0 && _currentWorker.getStoreAppointmentsNumber() == 1) {
            _currentWorker.getStoreAppointments().remove(position_p);
        }

        // if first item and gap after, remove appointment and extend gap between now to its end time
        else if (position_p == 0 && _currentWorker.getStoreAppointment(position_p + 1) instanceof StoreAppointment.NullStoreAppointment) {
            _currentWorker.getStoreAppointment(position_p + 1).setStartTime(now);
            _currentWorker.getStoreAppointments().remove(position_p);
        }

        // if first item and appointment after, replace appointment with created gap between now and next appointment's start time
        else if (position_p == 0 && !(_currentWorker.getStoreAppointment(position_p + 1) instanceof StoreAppointment.NullStoreAppointment)) {
            StoreAppointment gap = new StoreAppointment().newNullInstance();
            gap.setStartTime(now);
            gap.setEndTime(_currentWorker.getStoreAppointment(position_p).getEndTime());
            if (gap.getDuration() >= StoreTaskModel.getInstance().getMinTimeInMinutes()) {
                _currentWorker.getStoreAppointments().set(position_p, gap);
            } else {
                _currentWorker.getStoreAppointments().remove(position_p);
            }
        }

        // if last item and gap before, remove appointment and gap before
        else if (position_p == _currentWorker.getStoreAppointmentsNumber() - 1
                && _currentWorker.getStoreAppointment(position_p - 1) instanceof StoreAppointment.NullStoreAppointment) {
            _currentWorker.getStoreAppointments().remove(position_p - 1);
            _currentWorker.getStoreAppointments().remove(position_p - 1);
        }

        // if last item and appointment before, remove appointment
        else if (position_p == _currentWorker.getStoreAppointmentsNumber() - 1
                && !(_currentWorker.getStoreAppointment(position_p - 1) instanceof StoreAppointment.NullStoreAppointment)) {
            _currentWorker.getStoreAppointments().remove(position_p);
        }

        // if gap only before, extend the gap end time and remove appointment
        else if (_currentWorker.getStoreAppointment(position_p - 1) instanceof StoreAppointment.NullStoreAppointment
                && !(_currentWorker.getStoreAppointment(position_p + 1) instanceof StoreAppointment.NullStoreAppointment)) {
            _currentWorker.getStoreAppointment(position_p - 1).setEndTime(_currentWorker.getStoreAppointment(position_p).getEndTime());
            _currentWorker.getStoreAppointments().remove(position_p);
        }

        // if gap only after, extend the gap start time and remove appointment
        else if (!(_currentWorker.getStoreAppointment(position_p - 1) instanceof StoreAppointment.NullStoreAppointment)
                && _currentWorker.getStoreAppointment(position_p + 1) instanceof StoreAppointment.NullStoreAppointment) {
            _currentWorker.getStoreAppointment(position_p + 1).setStartTime(_currentWorker.getStoreAppointment(position_p).getStartTime());
            _currentWorker.getStoreAppointments().remove(position_p);
        }

        // if gap before and after, change the before gap end time and remove after gap and appointment
        else if (_currentWorker.getStoreAppointment(position_p - 1) instanceof StoreAppointment.NullStoreAppointment
                && _currentWorker.getStoreAppointment(position_p + 1) instanceof StoreAppointment.NullStoreAppointment) {
            _currentWorker.getStoreAppointment(position_p - 1).setEndTime(_currentWorker.getStoreAppointment(position_p + 1).getEndTime());
            _currentWorker.getStoreAppointments().remove(position_p);
            _currentWorker.getStoreAppointments().remove(position_p);
        }

        // if appointment before and after, remove appointment and create gap
        else {
            StoreAppointment gap = new StoreAppointment().newNullInstance();
            gap.setStartTime(_currentWorker.getStoreAppointment(position_p - 1).getEndTime());
            gap.setEndTime(_currentWorker.getStoreAppointment(position_p + 1).getStartTime());
            _currentWorker.getStoreAppointments().set(position_p, gap);
        }

        notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_details_fragment, menu);
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

    public void notifyDataSetChanged() {
        _detailsRecyclerAdapter.notifyDataSetChanged();
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
