package fr.goui.storeorganizer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WorkersCategoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    class WorkersViewHolder extends RecyclerView.ViewHolder {
        TextView _textView;

        public WorkersViewHolder(View itemView_p) {
            super(itemView_p);
            _textView = (TextView) itemView_p.findViewById(R.id.layout_one_text_view);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent_p, int viewType_p) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent_p.getContext());
        View timeView = inflater.inflate(R.layout.layout_one_text_view, parent_p, false);
        viewHolder = new WorkersViewHolder(timeView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder_p, int position_p) {
        WorkersViewHolder tasksViewHolder = (WorkersViewHolder) holder_p;

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
