package fr.goui.storeorganizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class WorkerBaseAdapter extends BaseAdapter {

    private LayoutInflater _layoutInflater;
    private List<StoreWorker> _workers;

    public WorkerBaseAdapter(Context context_p, List<StoreWorker> workers_p) {
        _layoutInflater = LayoutInflater.from(context_p);
        _workers = workers_p;
    }

    @Override
    public int getCount() {
        return _workers.size();
    }

    @Override
    public Object getItem(int position_p) {
        return _workers.get(position_p);
    }

    @Override
    public long getItemId(int position_p) {
        return position_p;
    }

    @Override
    public View getView(int position_p, View convertView_p, ViewGroup parent_p) {
        ViewHolder viewHolder;
        if(convertView_p == null) {
            convertView_p = _layoutInflater.inflate(R.layout.spinner_item_worker, null);
            viewHolder = new ViewHolder();
            viewHolder.txtName = (TextView) convertView_p.findViewById(R.id.spinner_item_worker_name_text_view);
            convertView_p.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView_p.getTag();
        }

        StoreWorker storeWorker = (StoreWorker) getItem(position_p);
        if(storeWorker != null) {
            viewHolder.txtName.setText(storeWorker.getName());
        }

        return convertView_p;
    }

    class ViewHolder {
        TextView txtName;
    }

}
