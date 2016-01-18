package fr.goui.storeorganizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class TaskBaseAdapter extends BaseAdapter {

    private LayoutInflater _layoutInflater;
    private List<StoreTask> _tasks;

    public TaskBaseAdapter(Context context_p, List<StoreTask> tasks_p) {
        _layoutInflater = LayoutInflater.from(context_p);
        _tasks = tasks_p;
    }

    @Override
    public int getCount() {
        return _tasks.size();
    }

    @Override
    public Object getItem(int position_p) {
        return _tasks.get(position_p);
    }

    @Override
    public long getItemId(int position_p) {
        return position_p;
    }

    @Override
    public View getView(int position_p, View convertView_p, ViewGroup parent_p) {
        ViewHolder viewHolder;
        if(convertView_p == null) {
            convertView_p = _layoutInflater.inflate(R.layout.spinner_item_task, null);
            viewHolder = new ViewHolder();
            viewHolder.txtName = (TextView) convertView_p.findViewById(R.id.spinner_item_task_name_text_view);
            viewHolder.txtDuration = (TextView) convertView_p.findViewById(R.id.spinner_item_task_duration_text_view);
            convertView_p.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView_p.getTag();
        }

        StoreTask storeTask = (StoreTask) getItem(position_p);
        if(storeTask != null) {
            viewHolder.txtName.setText(storeTask.getName());
            viewHolder.txtDuration.setText(storeTask.getDuration() + "min");
        }

        return convertView_p;
    }

    class ViewHolder {
        TextView txtName;
        TextView txtDuration;
    }

}
