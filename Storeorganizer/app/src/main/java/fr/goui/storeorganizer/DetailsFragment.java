package fr.goui.storeorganizer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DetailsFragment extends Fragment {

    public static final String ARG_SECTION_NUMBER = "section_number";

    private int _sectionNumber;

    private StoreWorker _currentWorker;

    private List<Object> _items;

    private TextView _noAppointmentsTextView;

    private RecyclerView _recyclerView;

    private DetailsRecyclerAdapter _detailsRecyclerAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        _currentWorker = StoreWorkerModel.getInstance().getStoreWorker(_sectionNumber);
        _items = new ArrayList<>();
        _detailsRecyclerAdapter = new DetailsRecyclerAdapter(getActivity(), _items);
        _recyclerView.setAdapter(_detailsRecyclerAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        // Get the views in the fragment
        TextClock textClock = (TextClock) rootView.findViewById(R.id.fragment_details_text_clock);
        _noAppointmentsTextView = (TextView) rootView.findViewById(R.id.fragment_details_no_appointments_text_view);
        _recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_details_recycler_view);
        _recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set the on click listener for the clock
        textClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO scroll to most relevant element in list (i.e. current task according to current time)
                // recyclerView.setSelectionFromTop(position, recyclerView.getTop());
                Toast.makeText(getActivity(), "it is " + ((TextClock) v).getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        // TODO _recyclerView onClick

        return rootView;
    }

    public void notifyItemAdded() {
        StoreAppointment storeAppointment = _currentWorker.getStoreAppointments().get(_currentWorker.getStoreAppointmentsNumber() - 1);
        if (_currentWorker.getStoreAppointmentsNumber() == 1) {
            _items.add(storeAppointment.getFormattedStartDate());
            _noAppointmentsTextView.setVisibility(View.GONE);
        } else {
            StoreAppointment previousAppointment = _currentWorker.getStoreAppointments().get(_currentWorker.getStoreAppointmentsNumber() - 2);
            if (!previousAppointment.getFormattedEndDate().equals(storeAppointment.getFormattedStartDate())) {
                _items.add(new StoreAppointment().newNullInstance());
                _items.add(storeAppointment.getFormattedStartDate());
            }
        }
        _items.add(storeAppointment);
        _items.add(storeAppointment.getFormattedEndDate());
        _detailsRecyclerAdapter.notifyDataSetChanged();
    }

}
