package fr.goui.storeorganizer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DetailsFragment extends Fragment {

    public static final String ARG_SECTION_NUMBER = "section_number";

    private StoreWorker _currentWorker;

    private List<Object> _items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        // Get the views in the fragment
        TextClock textClock = (TextClock) rootView.findViewById(R.id.fragment_details_text_clock);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_details_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set the on click listener for the clock
        textClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO scroll to most relevant element in list (i.e. current task according to current time)
                // recyclerView.setSelectionFromTop(position, recyclerView.getTop());
                Toast.makeText(getActivity(), "it is " + ((TextClock) v).getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        _currentWorker = StoreWorkerModel.getInstance().getStoreWorker(sectionNumber);
        _items = new ArrayList<>();
        recyclerView.setAdapter(new DetailsRecyclerAdapter(getActivity(), _items));
        // TODO onClick

        return rootView;
    }

    public void notifyDataSetChanged() {
        List<StoreAppointment> appointments = _currentWorker.getAppointments();

        // TODO generate items list
    }

}
