package fr.goui.storeorganizer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TasksCategoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    class TasksViewHolder extends RecyclerView.ViewHolder {
        private TextView _txtUpper;
        private TextView _txtLower;

        public TasksViewHolder(View itemView_p) {
            super(itemView_p);
            _txtUpper = (TextView) itemView_p.findViewById(R.id.layout_upper_text_view);
            _txtLower = (TextView) itemView_p.findViewById(R.id.layout_lower_text_view);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent_p, int viewType_p) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent_p.getContext());
        View timeView = inflater.inflate(R.layout.layout_two_text_views, parent_p, false);
        viewHolder = new TasksViewHolder(timeView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder_p, int position_p) {
        TasksViewHolder tasksViewHolder = (TasksViewHolder) holder_p;

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
