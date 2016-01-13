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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TasksCategoryFragment extends Fragment {

    private TasksCategoryRecyclerAdapter mAdapter;

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
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_settings_tasks, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        if (id == R.id.action_add_task) {
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.VERTICAL);
            TextView txtName = new TextView(getActivity());
            txtName.setText(getString(R.string.name));
            final EditText etName = new EditText(getActivity());
            etName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            TextView txtDuration = new TextView(getActivity());
            txtDuration.setText(getString(R.string.duration));
            final EditText etDuration = new EditText(getActivity());
            etDuration.setInputType(InputType.TYPE_CLASS_NUMBER);
            layout.addView(txtName);
            layout.addView(etName);
            layout.addView(txtDuration);
            layout.addView(etDuration);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getActivity().getString(R.string.add_task));
            builder.setView(layout);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (etName.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), getString(R.string.please_specify_a_name), Toast.LENGTH_SHORT).show();
                    } else if (etDuration.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), getString(R.string.please_specify_a_duration), Toast.LENGTH_SHORT).show();
                    } else {
                        int id = StoreTaskModel.getInstance().addStoreTask(etName.getText().toString(), Integer.parseInt(etDuration.getText().toString()));
                        mAdapter.notifyDataSetChanged();
                        // TODO change in shared prefs
                    }
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
