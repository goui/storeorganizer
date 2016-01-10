package fr.goui.storeorganizer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TasksCategoryFragment extends Fragment {

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
        // TODO adapter and click
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
            // TODO add a task in the recycler view
        }
        return super.onOptionsItemSelected(item);
    }

}
