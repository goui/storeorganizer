package fr.goui.storeorganizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * {@code WorkerBaseAdapter} is a {@code BaseAdapter} used to display a list of {@link StoreWorker}s in {@code Spinner}s.
 * It uses the ViewHolder design.
 */
public class WorkerBaseAdapter extends BaseAdapter {

    /**
     * The {@code LayoutInflater} used to inflate views.
     */
    private LayoutInflater mLayoutInflater;

    /**
     * The list of all the {@code StoreWorker}s
     */
    private List<StoreWorker> mWorkers;

    /**
     * Constructor using a {@code Context} to get its {@code LayoutInflater}.
     *
     * @param context_p the context
     */
    public WorkerBaseAdapter(Context context_p) {
        mLayoutInflater = LayoutInflater.from(context_p);
        mWorkers = StoreWorkerModel.getInstance().getStoreWorkers();
    }

    @Override
    public int getCount() {
        return mWorkers.size();
    }

    @Override
    public StoreWorker getItem(int position) {
        return mWorkers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        // first time creating the view
        if (convertView == null) {

            // inflating the layout
            convertView = mLayoutInflater.inflate(R.layout.spinner_item_worker, parent);

            // creating the view holder and putting inflated views in it
            viewHolder = new ViewHolder();
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.spinner_item_worker_name_text_view);

            // keeping reference to the view holder
            convertView.setTag(viewHolder);
        }

        // when view has already been created, getting view holder
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // getting the item at the current position
        StoreWorker storeWorker = getItem(position);

        // if it's not null, displaying its information in views
        if (storeWorker != null) {
            viewHolder.txtName.setText(storeWorker.getName());
        }

        return convertView;
    }

    /**
     * {@code ViewHolder} is a class used to store views.
     * This design prevents them to be inflated every time they are displayed.
     * Here it will store views for displaying a {@code StoreWorker}'s information.
     */
    class ViewHolder {
        TextView txtName;
    }

}
