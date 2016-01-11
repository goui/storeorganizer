package fr.goui.storeorganizer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class WorkersCategoryFragment extends Fragment {

    private WorkersCategoryRecyclerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings_generic_category, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.fragment_settings_generic_category_text_view);
        textView.setText(getActivity().getString(R.string.workers));
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_settings_generic_category_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new WorkersCategoryRecyclerAdapter(getActivity(), StoreWorkerModel.getInstance().getStoreWorkers());
        recyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        if(id == R.id.action_add) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getActivity().getString(R.string.add_a_worker));
            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StoreWorkerModel.getInstance().addStoreWorker(new StoreWorker(input.getText().toString()));
                    mAdapter.notifyDataSetChanged();
                    // TODO change in shared prefs
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

}
