package fr.goui.storeorganizer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class DetailsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NORMAL_APPOINTMENT = 0;
    private static final int TYPE_NULL_APPOINTMENT = 1;

    private Context _context;
    private LayoutInflater _inflater;
    private List<StoreAppointment> _appointments;
    private OnAppointmentClickListener _onAppointmentClickListener;

    class NullAppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        int position;

        public NullAppointmentViewHolder(View itemView_p) {
            super(itemView_p);
            textView = (TextView) itemView_p.findViewById(R.id.fragment_details_item_null_appointment_text_view);
            itemView_p.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            _onAppointmentClickListener.onAppointmentClick(position);
        }

    }

    class AppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtStartTime;
        TextView txtClientsName;
        TextView txtClientsPhone;
        TextView txtTaskName;
        TextView txtEndTime;
        int position;

        public AppointmentViewHolder(View itemView_p) {
            super(itemView_p);
            txtStartTime = (TextView) itemView_p.findViewById(R.id.fragment_details_item_appointment_start_time_text_view);
            txtClientsName = (TextView) itemView_p.findViewById(R.id.fragment_details_item_appointment_clients_name_text_view);
            txtClientsPhone = (TextView) itemView_p.findViewById(R.id.fragment_details_item_appointment_clients_phone_text_view);
            txtTaskName = (TextView) itemView_p.findViewById(R.id.fragment_details_item_appointment_task_name_text_view);
            txtEndTime = (TextView) itemView_p.findViewById(R.id.fragment_details_item_appointment_end_time_text_view);
            RelativeLayout layout = (RelativeLayout) itemView_p.findViewById(R.id.fragment_details_item_appointment_layout);
            layout.setOnClickListener(this);
            itemView_p.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            _onAppointmentClickListener.onAppointmentClick(position);
        }

    }

    public DetailsRecyclerAdapter(Context context_p, List<StoreAppointment> appointments_p, OnAppointmentClickListener onAppointmentClickListener_p) {
        _context = context_p;
        _inflater = LayoutInflater.from(context_p);
        _appointments = appointments_p;
        _onAppointmentClickListener = onAppointmentClickListener_p;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent_p, int viewType_p) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType_p) {
            case TYPE_NORMAL_APPOINTMENT:
                viewHolder = new AppointmentViewHolder(_inflater.inflate(R.layout.fragment_details_item_appointment, parent_p, false));
                break;
            case TYPE_NULL_APPOINTMENT:
                viewHolder = new NullAppointmentViewHolder(_inflater.inflate(R.layout.fragment_details_item_null_appointment, parent_p, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder_p, int position_p) {
        switch (holder_p.getItemViewType()) {
            case TYPE_NORMAL_APPOINTMENT:
                bindNormalAppointment((AppointmentViewHolder) holder_p, position_p);
                break;
            case TYPE_NULL_APPOINTMENT:
                bindNullAppointment((NullAppointmentViewHolder) holder_p, position_p);
                break;
        }
    }

    private void bindNullAppointment(NullAppointmentViewHolder holder_p, int position_p) {
        StoreAppointment appointment = _appointments.get(position_p);
        if (appointment != null) {
            long startTime = appointment.getStartDate().getTime();
            long endTime = appointment.getEndDate().getTime();
            holder_p.textView.setText(
                    (endTime - startTime) / 60000 + _context.getString(R.string.minutes) + " " + _context.getString(R.string.gap));
        }
    }

    private void bindNormalAppointment(AppointmentViewHolder holder_p, int position_p) {
        StoreAppointment appointment = _appointments.get(position_p);
        if (appointment != null) {
            holder_p.txtStartTime.setText(appointment.getFormattedStartDate());
            holder_p.txtClientsName.setText(appointment.getClientName());
            holder_p.txtClientsPhone.setText(appointment.getClientPhoneNumber());
            holder_p.txtTaskName.setText(appointment.getStoreTask().getName());
            holder_p.txtEndTime.setText(appointment.getFormattedEndDate());
            holder_p.position = position_p;

            Calendar calendar = Calendar.getInstance();
            int color;
            if (calendar.getTime().after(appointment.getEndDate())) {
                color = R.color.grey_overlay;
            } else {
                color = R.color.colorAccentPale;
            }
            holder_p.txtStartTime.setBackgroundResource(color);
            holder_p.txtEndTime.setBackgroundResource(color);
        }
    }

    @Override
    public int getItemCount() {
        return _appointments.size();
    }

    @Override
    public int getItemViewType(int position_p) {
        int type = TYPE_NORMAL_APPOINTMENT;
        if (_appointments.get(position_p) instanceof StoreAppointment.NullStoreAppointment) {
            type = TYPE_NULL_APPOINTMENT;
        }
        ;
        return type;
    }
}
