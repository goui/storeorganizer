package fr.goui.storeorganizer;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * {@code TasksCategoryFragment} is a fragment of {@link SettingsActivity}.
 * It is used to display all the available {@code StoreTask}s.
 * Users can add, update and delete {@code StoreTask}s.
 */
public class TasksCategoryFragment extends Fragment {

    /**
     * The {@code StoreTask}s recycler view's adapter.
     */
    private TasksCategoryRecyclerAdapter mAdapter;

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
        textView.setText(mResources.getString(R.string.tasks));
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_settings_generic_category_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // creating the adapter for the recycler view
        mAdapter = new TasksCategoryRecyclerAdapter(getActivity());
        recyclerView.setAdapter(mAdapter);

        // getting the shared prefs
        mSharedPreferences = getActivity().getSharedPreferences(mResources.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // creating menu
        inflater.inflate(R.menu.menu_settings_tasks, menu);
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

        // adding a new task
        if (id == R.id.action_add_task) {
            addNewTask();
            return true;
        }

        // restoring default for the tasks
        if (id == R.id.action_restore_tasks) {
            restoreDefault();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method used to add a new {@code StoreTask}.
     * It will display an {@code AlertDialog} to enter the name and the task duration.
     */
    private void addNewTask() {

        // creating layout and views manually
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        // edit text for the name
        final EditText etName = new EditText(getActivity());
        etName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etName.setHint(mResources.getString(R.string.name));

        // edit text for the duration in minutes
        final EditText etDuration = new EditText(getActivity());
        etDuration.setInputType(InputType.TYPE_CLASS_NUMBER);
        etDuration.setHint(mResources.getString(R.string.duration_in_minutes));
        layout.addView(etName);
        layout.addView(etDuration);

        // creating the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mResources.getString(R.string.add_task));
        builder.setView(layout);
        builder.setPositiveButton(mResources.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // if ok is clicked, checking if information has been entered
                if (etName.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), mResources.getString(R.string.please_specify_a_name), Toast.LENGTH_SHORT).show();
                } else if (etDuration.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), mResources.getString(R.string.please_specify_a_duration), Toast.LENGTH_SHORT).show();
                } else {

                    // adding the task in the model
                    int id = StoreTaskModel.getInstance().addStoreTask(etName.getText().toString(), Integer.parseInt(etDuration.getText().toString()));
                    mAdapter.notifyDataSetChanged();

                    // adding the task in the shared prefs
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putInt(mResources.getString(R.string.task_max_id), id);
                    editor.putString(mResources.getString(R.string.task) + id, etName.getText().toString());
                    editor.putInt(mResources.getString(R.string.task) + id + mResources.getString(R.string.duration),
                            Integer.parseInt(etDuration.getText().toString()));
                    editor.apply();
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
     * Method used to restore default for {@code StoreTask}s.
     * An {@link AlertDialog} will be created asking for confirmation.
     * Once confirmed all information about tasks will be erased and a default task will be created in both {@code SharedPreferences} and model.
     */
    private void restoreDefault() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mResources.getString(R.string.question_restore_tasks));
        builder.setPositiveButton(mResources.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // clearing model and creating a default task
                int size = StoreTaskModel.getInstance().getMaxId();
                StoreTaskModel.getInstance().clear(mResources.getString(R.string.task), 30);
                mAdapter.notifyDataSetChanged();

                // clearing shared prefs
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                for (int i = 0; i < size + 1; i++) {
                    editor.remove(mResources.getString(R.string.task) + i);
                    editor.remove(mResources.getString(R.string.task) + i + mResources.getString(R.string.duration));
                }

                // creating the default task
                editor.putInt(mResources.getString(R.string.task_max_id), 0);
                editor.putString(mResources.getString(R.string.task) + 0, mResources.getString(R.string.task));
                editor.putInt(mResources.getString(R.string.task) + 0 + mResources.getString(R.string.duration), 30);
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
