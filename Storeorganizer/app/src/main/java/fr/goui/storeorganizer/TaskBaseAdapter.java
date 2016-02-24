package fr.goui.storeorganizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * {@code TaskBaseAdapter} is a {@code BaseAdapter} used to display a list of {@link StoreTask}s in {@code Spinner}s.
 * It uses the ViewHolder design.
 */
public class TaskBaseAdapter extends BaseAdapter {

    /**
     * The {@code LayoutInflater} used to inflate views.
     */
    private LayoutInflater mLayoutInflater;

    /**
     * The list of all the {@code StoreTask}s
     */
    private List<StoreTask> mTasks;

    /**
     * Constructor using a {@code Context} to get its {@code LayoutInflater}.
     *
     * @param context the context
     */
    public TaskBaseAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mTasks = StoreTaskModel.getInstance().getStoreTasks();
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }

    @Override
    public StoreTask getItem(int position) {
        return mTasks.get(position);
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
            convertView = mLayoutInflater.inflate(R.layout.spinner_item_task, parent);

            // creating the view holder and putting inflated views in it
            viewHolder = new ViewHolder();
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.spinner_item_task_name_text_view);
            viewHolder.txtDuration = (TextView) convertView.findViewById(R.id.spinner_item_task_duration_text_view);

            // keeping reference to the view holder
            convertView.setTag(viewHolder);
        }

        // when view has already been created, getting view holder
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // getting the item at the current position
        StoreTask storeTask = getItem(position);

        // if it's not null, displaying its information in views
        if (storeTask != null) {
            viewHolder.txtName.setText(storeTask.getName());
            viewHolder.txtDuration.setText(storeTask.getDuration() + "min");
        }

        return convertView;
    }

    /**
     * {@code ViewHolder} is a class used to store views.
     * This design prevents them to be inflated every time they are displayed.
     * Here it will store views for displaying a {@code StoreTask}'s information.
     */
    class ViewHolder {
        TextView txtName;
        TextView txtDuration;
    }

}
