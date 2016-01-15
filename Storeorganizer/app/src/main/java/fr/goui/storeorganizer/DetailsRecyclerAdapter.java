package fr.goui.storeorganizer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class DetailsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_INVALID = -1;
    private static final int TYPE_TIME = 0;
    private static final int TYPE_APPOINTMENT = 1;

    private LayoutInflater _inflater;
    private List<Object> _items;

    class TimeViewHolder extends RecyclerView.ViewHolder {
        TextView txtTimeTextView;

        public TimeViewHolder(View itemView_p) {
            super(itemView_p);
            txtTimeTextView = (TextView) itemView_p.findViewById(R.id.layout_simple_item_1tv_text_view);
        }
    }

    class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView txtAppointmentUpperTextView;
        TextView txtAppointmentLowerTextView;

        public AppointmentViewHolder(View itemView_p) {
            super(itemView_p);
            txtAppointmentUpperTextView = (TextView) itemView_p.findViewById(R.id.layout_simple_item_2tv_upper_text_view);
            txtAppointmentLowerTextView = (TextView) itemView_p.findViewById(R.id.layout_simple_item_2tv_lower_text_view);
        }
    }

    public DetailsRecyclerAdapter(Context context_p, List<Object> items_p) {
        _inflater = LayoutInflater.from(context_p);
        _items = items_p;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent_p, int viewType_p) {
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType_p) {
            case TYPE_TIME:
                View timeView = _inflater.inflate(R.layout.layout_simple_item_1tv, parent_p, false);
                viewHolder = new TimeViewHolder(timeView);
                break;
            case TYPE_APPOINTMENT:
                View appointmentView = _inflater.inflate(R.layout.layout_simple_item_2tv, parent_p, false);
                viewHolder = new AppointmentViewHolder(appointmentView);
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
            case TYPE_APPOINTMENT:
                AppointmentViewHolder appointmentViewHolder = (AppointmentViewHolder) holder_p;
                bindAppointmentViewHolder(appointmentViewHolder, position_p);
                break;
        }
    }

    private void bindTimeViewHolder(TimeViewHolder timeViewHolder_p, int position_p) {
        String strTime = (String) _items.get(position_p);
        if (strTime != null) {
            timeViewHolder_p.txtTimeTextView.setText(strTime);
        }
    }

    private void bindAppointmentViewHolder(AppointmentViewHolder appointmentViewHolder_p, int position_p) {
        StoreAppointment storeAppointment = (StoreAppointment) _items.get(position_p);
        if(storeAppointment != null) {
            appointmentViewHolder_p.txtAppointmentUpperTextView.setText(storeAppointment.getClientName());
            appointmentViewHolder_p.txtAppointmentLowerTextView.setText(storeAppointment.getStoreTask().getDuration());
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
        if (_items.get(position_p) instanceof StoreAppointment) {
            return TYPE_APPOINTMENT;
        }
        return TYPE_INVALID;
    }

}
