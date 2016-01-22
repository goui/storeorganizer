package fr.goui.storeorganizer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Date;

public class DetailsFragment extends Fragment implements OnAppointmentClickListener {

    public static final String ARG_SECTION_NUMBER = "section_number";
    private static final int REQUEST_CODE_EDIT_APPOINTMENT = 2;
    public static final String INTENT_EXTRA_APPOINTMENT_POSITION = "intent_extra_appointment_position";
    public static final String INTENT_EXTRA_WORKER_POSITION = "intent_extra_worker_position";

    private int _sectionNumber;

    private StoreWorker _currentWorker;

    private TextView _noAppointmentsTextView;

    private RecyclerView _recyclerView;

    private DetailsRecyclerAdapter _detailsRecyclerAdapter;

    private Calendar _calendar;

    private OnAllFragmentsChangedListener _onAllFragmentsChangedListener;

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
    public void onAppointmentClick(int position_p) {
        Intent intent = new Intent(getContext(), AppointmentEditionActivity.class);
        intent.putExtra(INTENT_EXTRA_APPOINTMENT_POSITION, position_p);
        intent.putExtra(INTENT_EXTRA_WORKER_POSITION, _sectionNumber);
        startActivityForResult(intent, REQUEST_CODE_EDIT_APPOINTMENT);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _onAllFragmentsChangedListener = (OnAllFragmentsChangedListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _onAllFragmentsChangedListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_APPOINTMENT) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                if (data.getBooleanExtra(AppointmentEditionActivity.INTENT_EXTRA_WORKER_CHANGED, false)) {
                    _onAllFragmentsChangedListener.onAllFragmentsChanged();
                } else {
                    notifyDataSetChanged();
                    Toast.makeText(getActivity(), "appointment edited", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "appointment not edited", Toast.LENGTH_SHORT).show();
            }
        }
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
            Date currentDate = _calendar.getTime();
            int position = -1;
            for (int i = 0; i < _currentWorker.getStoreAppointmentsNumber(); i++) {
                if (currentDate.after(_currentWorker.getStoreAppointments().get(i).getEndDate())) {
                    position = i + 1;
                }
            }
            ((LinearLayoutManager) _recyclerView.getLayoutManager()).scrollToPositionWithOffset(position + 1, 10);
            Toast.makeText(getActivity(), getString(R.string.scrolling_to_the_current_appointment), Toast.LENGTH_SHORT).show();
        }
    }

    public void notifyItemAdded() {
        _detailsRecyclerAdapter.notifyItemInserted(_currentWorker.getStoreAppointmentsNumber() - 1);
        computeNoAppointmentTextViewVisibility();
    }

    public void notifyDataSetChanged() {
        _detailsRecyclerAdapter.notifyDataSetChanged();
        scrollToCurrentAppointment();
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
