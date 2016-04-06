package fr.goui.storeorganizer.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import android.widget.Toast;

import fr.goui.storeorganizer.R;
import fr.goui.storeorganizer.model.StoreWorkerModel;
import fr.goui.storeorganizer.adapter.WorkersCategoryRecyclerAdapter;
import fr.goui.storeorganizer.activity.SettingsActivity;

/**
 * {@code WorkersCategoryFragment} is a fragment of {@link SettingsActivity}.
 * It is used to display all the available {@code StoreWorker}s.
 * Users can add, update and delete {@code StoreWorker}s.
 */
public class WorkersCategoryFragment extends Fragment {

    /**
     * The {@code StoreWorker}s recycler view's adapter.
     */
    private WorkersCategoryRecyclerAdapter mAdapter;

    /**
     * The {@code SharedPreferences}.
     */
    private SharedPreferences mSharedPreferences;

    /**
     * The android resources to get project values.
     */
    private Resources mResources;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // indicating that this fragment has its own menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // getting the layout
        View rootView = inflater.inflate(R.layout.fragment_settings_generic_category, container, false);

        // getting the resources
        mResources = getActivity().getResources();

        // getting the views
        TextView textView = (TextView) rootView.findViewById(R.id.fragment_settings_generic_category_text_view);
        textView.setText(mResources.getString(R.string.workers));
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_settings_generic_category_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // creating the adapter for the recycler view
        mAdapter = new WorkersCategoryRecyclerAdapter(getActivity());
        recyclerView.setAdapter(mAdapter);

        // getting the shared prefs
        mSharedPreferences = getActivity().getSharedPreferences(mResources.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // creating menu
        inflater.inflate(R.menu.menu_fragment_settings_workers, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // hiding parent's menu item
        menu.findItem(R.id.action_restore_default).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // if we press the up button, going back
        if (id == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }

        // adding a new worker
        if (id == R.id.action_add_worker) {
            addNewWorker();
            return true;
        }

        // restoring default for the workers
        if (id == R.id.action_restore_workers) {
            restoreDefault();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method used to add a new {@code StoreWorker}.
     * It will display an {@code AlertDialog} to enter the name.
     */
    private void addNewWorker() {

        // creating manually an edit text for the name
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        input.setHint(mResources.getString(R.string.name));

        // creating the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mResources.getString(R.string.add_worker));
        builder.setView(input);
        builder.setPositiveButton(mResources.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // if ok is clicked, checking if information has been entered
                if (!input.getText().toString().isEmpty()) {

                    // adding the worker in the model
                    int id = StoreWorkerModel.getInstance().addStoreWorker(input.getText().toString());
                    mAdapter.notifyDataSetChanged();

                    // adding the worker in the shared prefs
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putInt(mResources.getString(R.string.worker_max_id), id);
                    editor.putString(mResources.getString(R.string.worker) + id, input.getText().toString());
                    editor.apply();
                } else {
                    Toast.makeText(getActivity(), mResources.getString(R.string.please_specify_a_name), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(mResources.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if cancel button was pressed discarding the dialog
                dialog.cancel();
            }
        });
        builder.show();
    }

    /**
     * Method used to restore default for {@code StoreWorker}s.
     * An {@link AlertDialog} will be created asking for confirmation.
     * Once confirmed all information about workers will be erased and a default worker will be created in both {@code SharedPreferences} and model.
     */
    private void restoreDefault() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mResources.getString(R.string.question_restore_workers));
        builder.setPositiveButton(mResources.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // clearing model and creating a default worker
                int size = StoreWorkerModel.getInstance().getMaxId();
                StoreWorkerModel.getInstance().clear(mResources.getString(R.string.worker));
                mAdapter.notifyDataSetChanged();

                // clearing shared prefs
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                for (int i = 0; i < size + 1; i++) {
                    editor.remove(mResources.getString(R.string.worker) + i);
                }

                // creating the default task
                editor.putInt(mResources.getString(R.string.worker_max_id), 0);
                editor.putString(mResources.getString(R.string.worker) + 0, mResources.getString(R.string.worker));
                editor.apply();
            }
        });
        builder.setNegativeButton(mResources.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if cancel button was pressed discarding the dialog
                dialog.cancel();
            }
        });
        builder.show();
    }

}
