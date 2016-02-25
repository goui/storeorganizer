package fr.goui.storeorganizer;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * {@code CategoriesFragment} is a fragment of {@link SettingsActivity}.
 * It is used to display all the available settings categories.
 * Such as {@link WorkersCategoryFragment} or {@link TasksCategoryFragment}.
 */
public class CategoriesFragment extends Fragment {

    /**
     * The listener called when a list's item is clicked.
     */
    private OnCategoryClickListener mOnCategoryClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // getting the layout
        View rootView = inflater.inflate(R.layout.fragment_settings_categories, container, false);

        // getting the list view displaying the settings categories
        ListView listView = (ListView) rootView.findViewById(R.id.fragment_settings_categories_list_view);

        // getting the resources
        Resources resources = getActivity().getResources();

        // getting the list of categories
        String[] categories = {resources.getString(R.string.workers), resources.getString(R.string.tasks), resources.getString(R.string.working_times)};

        // setting a simple array adapter to the list view
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, categories));

        // list view's item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnCategoryClickListener.onCategoryClicked(position);
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        // getting the listener
        mOnCategoryClickListener = (OnCategoryClickListener) context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        // disposing of the context reference
        mOnCategoryClickListener = null;
        super.onDetach();
    }

}
