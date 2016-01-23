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

    private static final int TYPE_INVALID = -1;
    private static final int TYPE_APPOINTMENT = 0;

    private Context _context;
    private LayoutInflater _inflater;
    private List<StoreAppointment> _appointments;
    private OnAppointmentClickListener _onAppointmentClickListener;

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
        return new AppointmentViewHolder(_inflater.inflate(R.layout.fragment_details_item_appointment, parent_p, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder_p, int position_p) {
        AppointmentViewHolder appointmentViewHolder = (AppointmentViewHolder) holder_p;
        StoreAppointment appointment = _appointments.get(position_p);
        if (appointment != null) {
            appointmentViewHolder.txtStartTime.setText(appointment.getFormattedStartDate());
            appointmentViewHolder.txtClientsName.setText(appointment.getClientName());
            appointmentViewHolder.txtClientsPhone.setText(appointment.getClientPhoneNumber());
            appointmentViewHolder.txtTaskName.setText(appointment.getStoreTask().getName());
            appointmentViewHolder.txtEndTime.setText(appointment.getFormattedEndDate());
            appointmentViewHolder.position = position_p;

            Calendar calendar = Calendar.getInstance();
            int color;
            if (calendar.getTime().after(appointment.getEndDate())) {
                color = R.color.grey_overlay;
            } else {
                color = R.color.colorAccentPale;
            }
            appointmentViewHolder.txtStartTime.setBackgroundResource(color);
            appointmentViewHolder.txtEndTime.setBackgroundResource(color);
        }
    }

    @Override
    public int getItemCount() {
        return _appointments.size();
    }

}
