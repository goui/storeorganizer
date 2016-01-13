package fr.goui.storeorganizer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class DetailsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_INVALID = -1;
    private static final int TYPE_TIME = 0;
    private static final int TYPE_TASK = 1;

    private List<Object> _items;

    class TimeViewHolder extends RecyclerView.ViewHolder {
        TextView txtTimeTextView;

        public TimeViewHolder(View itemView_p) {
            super(itemView_p);
            txtTimeTextView = (TextView) itemView_p.findViewById(R.id.layout_simple_item_1tv_text_view);
        }
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView txtTaskUpperTextView;
        TextView txtTaskLowerTextView;

        public TaskViewHolder(View itemView_p) {
            super(itemView_p);
            txtTaskUpperTextView = (TextView) itemView_p.findViewById(R.id.layout_simple_item_2tv_upper_text_view);
            txtTaskLowerTextView = (TextView) itemView_p.findViewById(R.id.layout_simple_item_2tv_lower_text_view);
        }
    }

    public DetailsRecyclerAdapter(List<Object> items_p) {
        _items = items_p;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent_p, int viewType_p) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent_p.getContext());

        switch (viewType_p) {
            case TYPE_TIME:
                View timeView = inflater.inflate(R.layout.layout_simple_item_1tv, parent_p, false);
                viewHolder = new TimeViewHolder(timeView);
                break;
            case TYPE_TASK:
                View taskView = inflater.inflate(R.layout.layout_simple_item_2tv, parent_p, false);
                viewHolder = new TaskViewHolder(taskView);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder_p, int position_p) {
        switch (holder_p.getItemViewType()) {
            case TYPE_TIME:
                TimeViewHolder timeViewHolder = (TimeViewHolder) holder_p;
                bindTimeViewHolder(timeViewHolder, position_p);
                break;
            case TYPE_TASK:
                TaskViewHolder taskViewHolder = (TaskViewHolder) holder_p;
                bindTaskViewHolder(taskViewHolder, position_p);
                break;
        }
    }

    private void bindTimeViewHolder(TimeViewHolder timeViewHolder_p, int position_p) {
        String strTime = (String) _items.get(position_p);
        if (strTime != null) {
            timeViewHolder_p.txtTimeTextView.setText(strTime);
        }
    }

    private void bindTaskViewHolder(TaskViewHolder taskViewHolder_p, int position_p) {
        StoreTask storeTask = (StoreTask) _items.get(position_p);
        if(storeTask != null) {
            taskViewHolder_p.txtTaskUpperTextView.setText(storeTask.getName());
            taskViewHolder_p.txtTaskLowerTextView.setText(storeTask.getDuration());
        }
    }

    @Override
    public int getItemCount() {
        return _items.size();
    }

    @Override
    public int getItemViewType(int position_p) {
        if (_items.get(position_p) instanceof String) {
            return TYPE_TIME;
        }
        if (_items.get(position_p) instanceof StoreTask) {
            return TYPE_TASK;
        }
        return TYPE_INVALID;
    }

}
