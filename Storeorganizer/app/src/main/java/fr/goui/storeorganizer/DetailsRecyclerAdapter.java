package fr.goui.storeorganizer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.v7.app.AlertDialog;
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
    private OnAppointmentChangeListener _onAppointmentChangeListener;

    class NullAppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        int position;

        public NullAppointmentViewHolder(View itemView_p) {
            super(itemView_p);
            textView = (TextView) itemView_p.findViewById(R.id.fragment_details_item_null_appointment_text_view);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            _onAppointmentChangeListener.onAppointmentEdit(position);
        }

    }

    class AppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView txtStartTime;
        TextView txtClientsName;
        TextView txtClientsPhone;
        TextView txtTaskName;
        TextView txtEndTime;
        TextView txtState;
        RelativeLayout timeLayout;
        RelativeLayout cardLayout;
        int position;

        public AppointmentViewHolder(View itemView_p) {
            super(itemView_p);
            txtStartTime = (TextView) itemView_p.findViewById(R.id.fragment_details_item_appointment_start_time_text_view);
            txtClientsName = (TextView) itemView_p.findViewById(R.id.fragment_details_item_appointment_clients_name_text_view);
            txtClientsPhone = (TextView) itemView_p.findViewById(R.id.fragment_details_item_appointment_clients_phone_text_view);
            txtTaskName = (TextView) itemView_p.findViewById(R.id.fragment_details_item_appointment_task_name_text_view);
            txtEndTime = (TextView) itemView_p.findViewById(R.id.fragment_details_item_appointment_end_time_text_view);
            txtState = (TextView) itemView_p.findViewById(R.id.fragment_details_item_appointment_state_text_view);
            timeLayout = (RelativeLayout) itemView_p.findViewById(R.id.fragment_details_item_appointment_time_layout);
            cardLayout = (RelativeLayout) itemView_p.findViewById(R.id.fragment_details_item_appointment_layout);
            cardLayout.setOnClickListener(this);
            cardLayout.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            _onAppointmentChangeListener.onAppointmentEdit(position);
        }

        @Override
        public boolean onLongClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(_context);
            builder.setTitle(_context.getString(R.string.question_remove_this_appointment));
            builder.setPositiveButton(_context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    _onAppointmentChangeListener.onAppointmentDelete(position);
                }
            });
            builder.setNegativeButton(_context.getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
            return true;
        }
    }

    public DetailsRecyclerAdapter(Context context_p, List<StoreAppointment> appointments_p, OnAppointmentChangeListener onAppointmentChangeListener_p) {
        _context = context_p;
        _inflater = LayoutInflater.from(context_p);
        _appointments = appointments_p;
        _onAppointmentChangeListener = onAppointmentChangeListener_p;
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
            long startTime = appointment.getStartTime().getTimeInMillis();
            long endTime = appointment.getEndTime().getTimeInMillis();
            holder_p.textView.setText(
                    (endTime - startTime) / 60000 + _context.getString(R.string.minutes) + " " + _context.getString(R.string.gap));
            holder_p.position = position_p;
        }
    }

    private void bindNormalAppointment(AppointmentViewHolder holder_p, int position_p) {
        StoreAppointment appointment = _appointments.get(position_p);
        if (appointment != null) {
            holder_p.txtStartTime.setText(appointment.getFormattedStartTime());
            holder_p.txtClientsName.setText(appointment.getClientName());
            holder_p.txtClientsPhone.setText(appointment.getClientPhoneNumber());
            holder_p.txtTaskName.setText(appointment.getStoreTask().getName());
            holder_p.txtEndTime.setText(appointment.getFormattedEndTime());
            holder_p.position = position_p;

            Calendar now = Calendar.getInstance();
            // we don't want to consider seconds and milliseconds
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);

            // if appointment is in the past
            if (appointment.isBefore(now)) {
                holder_p.cardLayout.setEnabled(false);
                holder_p.cardLayout.setBackgroundResource(R.color.light_grey);
                holder_p.txtState.setVisibility(View.VISIBLE);
                holder_p.txtState.setText(_context.getString(R.string.ended));
                holder_p.timeLayout.setBackgroundResource(R.color.light_grey);
            }

            // if appointment is in progress
            else if (appointment.getStartTime().before(now) && appointment.getEndTime().after(now)) {
                holder_p.cardLayout.setEnabled(true);
                int[] attrs = new int[]{R.attr.selectableItemBackground};
                TypedArray typedArray = _context.obtainStyledAttributes(attrs);
                int backgroundResource = typedArray.getResourceId(0, 0);
                holder_p.cardLayout.setBackgroundResource(backgroundResource);
                typedArray.recycle();
                holder_p.txtState.setVisibility(View.VISIBLE);
                holder_p.txtState.setText(_context.getString(R.string.now));
                holder_p.timeLayout.setBackgroundResource(R.color.colorAccentPale);
            }

            // if appointment is in the future
            else {
                holder_p.cardLayout.setEnabled(true);
                int[] attrs = new int[]{R.attr.selectableItemBackground};
                TypedArray typedArray = _context.obtainStyledAttributes(attrs);
                int backgroundResource = typedArray.getResourceId(0, 0);
                holder_p.cardLayout.setBackgroundResource(backgroundResource);
                typedArray.recycle();
                holder_p.txtState.setVisibility(View.GONE);
                holder_p.timeLayout.setBackgroundResource(android.R.color.transparent);
            }
        }
    }

    @Override
    public int getItemCount() {
        return _appointments.size();
    }

    @Override
    public int getItemViewType(int position_p) {
        int type = TYPE_NORMAL_APPOINTMENT;
        if (_appointments.get(position_p) instanceof NullStoreAppointment) {
            type = TYPE_NULL_APPOINTMENT;
        }
        ;
        return type;
    }
}
