package fr.goui.storeorganizer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CategoriesFragment extends Fragment {

    /**
     * The listener called when a list's item is clicked.
     */
    private OnCategoryClickListener onCategoryClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings_categories, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.fragment_settings_categories_list_view);
        String[] categories = {getActivity().getString(R.string.workers), getActivity().getString(R.string.tasks)};
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, categories));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onCategoryClickListener.onCategoryClicked(position);
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        onCategoryClickListener = (OnCategoryClickListener) context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        onCategoryClickListener = null;
        super.onDetach();
    }

}
