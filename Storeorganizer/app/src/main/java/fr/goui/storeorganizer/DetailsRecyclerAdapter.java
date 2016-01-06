package fr.goui.storeorganizer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TIME = 0;
    private static final int TYPE_TASK = 1;

    class TimeViewHolder extends RecyclerView.ViewHolder {
        TextView txtTimeTextView;

        public TimeViewHolder(View itemView) {
            super(itemView);
            txtTimeTextView = (TextView) itemView.findViewById(R.id.fragment_details_time_item_text_view);
        }
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView txtTaskUpperTextView;
        TextView txtTaskLowerTextView;

        public TaskViewHolder(View itemView) {
            super(itemView);
            txtTaskUpperTextView = (TextView) itemView.findViewById(R.id.fragment_details_task_item_upper_text_view);
            txtTaskLowerTextView = (TextView) itemView.findViewById(R.id.fragment_details_task_item_lower_text_view);
        }
    }

    public DetailsRecyclerAdapter() {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TYPE_TIME:
                View timeView = inflater.inflate(R.layout.fragment_details_time_item, parent, false);
                viewHolder = new TimeViewHolder(timeView);
                break;
            case TYPE_TASK:
                View taskView = inflater.inflate(R.layout.fragment_details_task_item, parent, false);
                viewHolder = new TaskViewHolder(taskView);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_TIME:
                TimeViewHolder timeViewHolder = (TimeViewHolder) holder;
                bindTimeViewHolder(timeViewHolder, position);
                break;
            case TYPE_TASK:
                TaskViewHolder taskViewHolder = (TaskViewHolder) holder;
                bindTaskViewHolder(taskViewHolder, position);
                break;
        }
    }

    private void bindTimeViewHolder(TimeViewHolder timeViewHolder_p, int position_p) {
        // TODO
    }

    private void bindTaskViewHolder(TaskViewHolder taskViewHolder_p, int position_p) {
        // TODO
    }

    @Override
    public int getItemCount() {
        // TODO
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        // TODO
        return super.getItemViewType(position);
    }
}
