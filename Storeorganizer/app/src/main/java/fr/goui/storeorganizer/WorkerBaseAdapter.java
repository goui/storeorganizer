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
    private LayoutInflater _layoutInflater;

    /**
     * The list of all the {@code StoreWorker}s
     */
    private List<StoreWorker> _workers;

    /**
     * Constructor using a {@code Context} to get its {@code LayoutInflater}.
     *
     * @param context_p the context
     */
    public WorkerBaseAdapter(Context context_p) {
        _layoutInflater = LayoutInflater.from(context_p);
        _workers = StoreWorkerModel.getInstance().getStoreWorkers();
    }

    @Override
    public int getCount() {
        return _workers.size();
    }

    @Override
    public StoreWorker getItem(int position_p) {
        return _workers.get(position_p);
    }

    @Override
    public long getItemId(int position_p) {
        return position_p;
    }

    @Override
    public View getView(int position_p, View convertView_p, ViewGroup parent_p) {
        ViewHolder viewHolder;

        // first time creating the view
        if (convertView_p == null) {

            // inflating the layout
            convertView_p = _layoutInflater.inflate(R.layout.spinner_item_worker, null);

            // creating the view holder and putting inflated views in it
            viewHolder = new ViewHolder();
            viewHolder.txtName = (TextView) convertView_p.findViewById(R.id.spinner_item_worker_name_text_view);

            // keeping reference to the view holder
            convertView_p.setTag(viewHolder);
        }

        // when view has already been created, getting view holder
        else {
            viewHolder = (ViewHolder) convertView_p.getTag();
        }

        // getting the item at the current position
        StoreWorker storeWorker = getItem(position_p);

        // if it's not null, displaying its information in views
        if (storeWorker != null) {
            viewHolder.txtName.setText(storeWorker.getName());
        }

        return convertView_p;
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
