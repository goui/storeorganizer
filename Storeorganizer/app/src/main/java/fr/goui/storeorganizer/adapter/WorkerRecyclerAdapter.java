package fr.goui.storeorganizer.adapter;

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

import fr.goui.storeorganizer.R;
import fr.goui.storeorganizer.listener.OnAppointmentChangeListener;
import fr.goui.storeorganizer.model.NullStoreAppointment;
import fr.goui.storeorganizer.model.StoreAppointment;

/**
 * {@code WorkerRecyclerAdapter} manages the list of a worker's two-type list of appointments.
 */
public class WorkerRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * Constant for the normal appointment type.
     */
    private static final int TYPE_NORMAL_APPOINTMENT = 0;

    /**
     * Constant for the null appointment type.
     */
    private static final int TYPE_NULL_APPOINTMENT = 1;

    /**
     * The context.
     */
    private Context mContext;

    /**
     * The layout inflater.
     */
    private LayoutInflater mLayoutInflater;

    /**
     * The list of appointments.
     */
    private List<StoreAppointment> mAppointments;

    /**
     * The appointment click listener.
     */
    private OnAppointmentChangeListener mOnAppointmentChangeListener;

    /**
     * Constructor.
     *
     * @param context                     the context
     * @param appointments                the list of appointments
     * @param onAppointmentChangeListener the appointment click listener
     */
    public WorkerRecyclerAdapter(Context context, List<StoreAppointment> appointments, OnAppointmentChangeListener onAppointmentChangeListener) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAppointments = appointments;
        mOnAppointmentChangeListener = onAppointmentChangeListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_NORMAL_APPOINTMENT:
                viewHolder = new AppointmentViewHolder(mLayoutInflater.inflate(R.layout.fragment_worker_item_appointment, parent, false));
                break;
            case TYPE_NULL_APPOINTMENT:
                viewHolder = new NullAppointmentViewHolder(mLayoutInflater.inflate(R.layout.fragment_worker_item_null_appointment, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_NORMAL_APPOINTMENT:
                bindNormalAppointment((AppointmentViewHolder) holder, position);
                break;
            case TYPE_NULL_APPOINTMENT:
                bindNullAppointment((NullAppointmentViewHolder) holder, position);
                break;
        }
    }

    /**
     * Method used to display the data at the specified position for a null appointment.
     *
     * @param holder   the view holder
     * @param position the position
     */
    private void bindNullAppointment(NullAppointmentViewHolder holder, int position) {
        StoreAppointment appointment = mAppointments.get(position);
        if (appointment != null) {
            long startTime = appointment.getStartTime().getTimeInMillis();
            long endTime = appointment.getEndTime().getTimeInMillis();
            holder.textView.setText(
                    (endTime - startTime) / 60000 + mContext.getString(R.string.minutes) + " " + mContext.getString(R.string.gap));
            holder.position = position;
        }
    }

    /**
     * Method used to display the data at the specified position for a normal appointment.
     *
     * @param holder   the view holder
     * @param position the position
     */
    private void bindNormalAppointment(AppointmentViewHolder holder, int position) {
        StoreAppointment appointment = mAppointments.get(position);
        if (appointment != null) {
            holder.txtStartTime.setText(appointment.getFormattedStartTime());
            holder.txtClientsName.setText(appointment.getClientName());
            holder.txtClientsPhone.setText(appointment.getClientPhoneNumber());
            holder.txtTaskName.setText(appointment.getStoreTask().getName());
            holder.txtEndTime.setText(appointment.getFormattedEndTime());
            holder.position = position;

            Calendar now = Calendar.getInstance();
            // we don't want to consider seconds and milliseconds
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);

            // if appointment is in the past
            if (appointment.isBefore(now)) {
                holder.cardLayout.setEnabled(false);
                holder.cardLayout.setBackgroundResource(R.color.light_grey);
                holder.txtState.setVisibility(View.VISIBLE);
                holder.txtState.setText(mContext.getString(R.string.ended));
                holder.timeLayout.setBackgroundResource(R.color.light_grey);
            }

            // if appointment is in progress
            else if (appointment.getStartTime().before(now) && appointment.getEndTime().after(now)) {
                holder.cardLayout.setEnabled(true);
                int[] attrs = new int[]{R.attr.selectableItemBackground};
                TypedArray typedArray = mContext.obtainStyledAttributes(attrs);
                int backgroundResource = typedArray.getResourceId(0, 0);
                holder.cardLayout.setBackgroundResource(backgroundResource);
                typedArray.recycle();
                holder.txtState.setVisibility(View.VISIBLE);
                holder.txtState.setText(mContext.getString(R.string.now));
                holder.timeLayout.setBackgroundResource(R.color.colorAccentPale);
            }

            // if appointment is in the future
            else {
                holder.cardLayout.setEnabled(true);
                int[] attrs = new int[]{R.attr.selectableItemBackground};
                TypedArray typedArray = mContext.obtainStyledAttributes(attrs);
                int backgroundResource = typedArray.getResourceId(0, 0);
                holder.cardLayout.setBackgroundResource(backgroundResource);
                typedArray.recycle();
                holder.txtState.setVisibility(View.GONE);
                holder.timeLayout.setBackgroundResource(android.R.color.transparent);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mAppointments.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type = TYPE_NORMAL_APPOINTMENT;
        if (mAppointments.get(position) instanceof NullStoreAppointment) {
            type = TYPE_NULL_APPOINTMENT;
        }
        return type;
    }

    /**
     * The view holder for a null appointment.
     */
    class NullAppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        int position;

        public NullAppointmentViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.fragment_worker_item_null_appointment_text_view);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnAppointmentChangeListener.onAppointmentEdit(position);
        }

    }

    /**
     * The view holder for a normal appointment.
     */
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

        public AppointmentViewHolder(View itemView) {
            super(itemView);
            txtStartTime = (TextView) itemView.findViewById(R.id.fragment_worker_item_appointment_start_time_text_view);
            txtClientsName = (TextView) itemView.findViewById(R.id.layout_appointment_information_clients_name_text_view);
            txtClientsPhone = (TextView) itemView.findViewById(R.id.layout_appointment_information_clients_phone_text_view);
            txtTaskName = (TextView) itemView.findViewById(R.id.layout_appointment_information_task_name_text_view);
            txtEndTime = (TextView) itemView.findViewById(R.id.fragment_worker_item_appointment_end_time_text_view);
            txtState = (TextView) itemView.findViewById(R.id.fragment_worker_item_appointment_state_text_view);
            timeLayout = (RelativeLayout) itemView.findViewById(R.id.fragment_worker_item_appointment_time_layout);
            cardLayout = (RelativeLayout) itemView.findViewById(R.id.fragment_worker_item_appointment_layout);
            cardLayout.setOnClickListener(this);
            cardLayout.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnAppointmentChangeListener.onAppointmentEdit(position);
        }

        @Override
        public boolean onLongClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(mContext.getString(R.string.question_remove_this_appointment));
            builder.setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mOnAppointmentChangeListener.onAppointmentDelete(position);
                }
            });
            builder.setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
            return true;
        }
    }
}
