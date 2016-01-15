package fr.goui.storeorganizer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

public class TasksCategoryFragment extends Fragment {

    private TasksCategoryRecyclerAdapter mAdapter;

    private SharedPreferences mSharedPreferences;

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
        textView.setText(getActivity().getString(R.string.tasks));
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_settings_generic_category_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new TasksCategoryRecyclerAdapter(getActivity(), StoreTaskModel.getInstance().getStoreTasks());
        recyclerView.setAdapter(mAdapter);
        mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_settings_tasks, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_restore_default).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        if (id == R.id.action_add_task) {
            addNewTask();
            return true;
        }
        if (id == R.id.action_restore_tasks) {
            restoreDefault();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewTask() {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText etName = new EditText(getActivity());
        etName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etName.setHint(getString(R.string.name));
        final EditText etDuration = new EditText(getActivity());
        etDuration.setInputType(InputType.TYPE_CLASS_NUMBER);
        etDuration.setHint(getString(R.string.duration));
        layout.addView(etName);
        layout.addView(etDuration);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getActivity().getString(R.string.add_task));
        builder.setView(layout);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (etName.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), getString(R.string.please_specify_a_name), Toast.LENGTH_SHORT).show();
                } else if (etDuration.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), getString(R.string.please_specify_a_duration), Toast.LENGTH_SHORT).show();
                } else {
                    int id = StoreTaskModel.getInstance().addStoreTask(etName.getText().toString(), Integer.parseInt(etDuration.getText().toString()));
                    mAdapter.notifyDataSetChanged();
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putInt(getString(R.string.task_max_id), id);
                    editor.putString(getString(R.string.task) + id, etName.getText().toString());
                    editor.putInt(getString(R.string.task) + id + getString(R.string.duration), Integer.parseInt(etDuration.getText().toString()));
                    editor.apply();
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void restoreDefault() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.question_restore_tasks));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int size = StoreTaskModel.getInstance().getMaxId();
                StoreTaskModel.getInstance().clear(getString(R.string.task), 30);
                mAdapter.notifyDataSetChanged();

                SharedPreferences.Editor editor = mSharedPreferences.edit();
                for (int i = 0; i < size + 1; i++) {
                    editor.remove(getString(R.string.task) + i);
                    editor.remove(getString(R.string.task) + i + getString(R.string.duration));
                }

                editor.putInt(getString(R.string.task_max_id), 0);
                editor.putString(getString(R.string.task) + 0, getString(R.string.task));
                editor.putInt(getString(R.string.task) + 0 + getString(R.string.duration), 30);
                editor.apply();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

}
