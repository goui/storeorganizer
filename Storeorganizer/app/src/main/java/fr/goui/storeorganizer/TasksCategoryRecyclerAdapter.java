package fr.goui.storeorganizer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * {@code TasksCategoryRecyclerAdapter} is a {@code RecyclerView.Adapter}.
 * It is used to display {@code StoreTask}s in the {@code RecyclerView} of the {@code TasksCategoryFragment}.
 */
public class TasksCategoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * The {@code Context} used to get {@code Resources} and {@code LayoutInflater}.
     */
    private Context mContext;

    /**
     * The {@code LayoutInflater} used to inflate views.
     */
    private LayoutInflater mLayoutInflater;

    /**
     * The list of all the {@code StoreTask}s.
     */
    private List<StoreTask> mTasks;

    /**
     * The {@code SharedPreferences}.
     */
    private SharedPreferences mSharedPreferences;

    /**
     * The android resources to get project values.
     */
    private Resources mResources;

    /**
     * Constructor using a {@code Context} to get its {@code LayoutInflater}.
     *
     * @param context the context
     */
    public TasksCategoryRecyclerAdapter(Context context) {
        mContext = context;
        mResources = mContext.getResources();
        mLayoutInflater = LayoutInflater.from(mContext);
        mTasks = StoreTaskModel.getInstance().getStoreTasks();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // creating the view holder with the inflated layout
        return new TasksViewHolder(mLayoutInflater.inflate(R.layout.fragment_settings_item_task, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // getting the item at the current position
        StoreTask task = mTasks.get(position);
        if (task != null) {

            // if not null, displaying its information
            ((TasksViewHolder) holder).setPosition(position);
            ((TasksViewHolder) holder).mTxtName.setText(task.getName());
            ((TasksViewHolder) holder).mTxtDuration.setText(task.getDuration() + "min");
        }
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    /**
     * {@code TasksViewHolder} is a {@code RecyclerView.ViewHolder} used to store inflated views for one task.
     */
    class TasksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // the views for one item
        private TextView mTxtName;
        private TextView mTxtDuration;
        private ImageButton mBtnEdit;
        private ImageButton mBtnDelete;
        private int mPosition;

        /**
         * Constructor getting all the views in the passed layout and setting listener to them if needed.
         *
         * @param itemView the layout
         */
        public TasksViewHolder(View itemView) {
            super(itemView);

            // getting all the views for this item
            mTxtName = (TextView) itemView.findViewById(R.id.fragment_settings_item_task_name_text_view);
            mTxtDuration = (TextView) itemView.findViewById(R.id.fragment_settings_item_task_duration_text_view);
            mBtnEdit = (ImageButton) itemView.findViewById(R.id.fragment_settings_item_task_edit_button);
            mBtnDelete = (ImageButton) itemView.findViewById(R.id.fragment_settings_item_task_delete_button);
            itemView.setOnClickListener(this);

            // item edition listener
            mBtnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editCurrentItem();
                }
            });

            // item deletion listener
            mBtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCurrentItem();
                }
            });

            // getting the shared prefs
            mSharedPreferences = mContext.getSharedPreferences(mResources.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        }

        /**
         * Method used to edit the item at the current position.
         */
        private void editCurrentItem() {

            // getting the item at the current position
            StoreTask storeTask = mTasks.get(mPosition);

            // creating the layout manually
            LinearLayout layout = new LinearLayout(mContext);
            layout.setOrientation(LinearLayout.VERTICAL);

            // creating an edit text for the name of the task
            final EditText etName = new EditText(mContext);
            etName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            etName.setText(storeTask.getName());
            etName.setHint(mContext.getString(R.string.name));

            // creating an edit text for the task's duration
            final EditText etDuration = new EditText(mContext);
            etDuration.setInputType(InputType.TYPE_CLASS_NUMBER);
            etDuration.setText(String.valueOf(storeTask.getDuration()));
            etDuration.setHint(mResources.getString(R.string.duration));
            layout.addView(etName);
            layout.addView(etDuration);

            // creating the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(mResources.getString(R.string.edit));
            builder.setView(layout);
            builder.setPositiveButton(mResources.getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // the name and duration before edition
                    String oldName = mTxtName.getText().toString();
                    String oldDuration = mTxtDuration.getText().toString();

                    // tha name and duration after edition
                    String newName = etName.getText().toString();
                    String newDuration = etDuration.getText().toString();

                    // if name has been modified, displaying it
                    boolean modification = false;
                    if (!oldName.equals(newName)) {
                        mTxtName.setText(newName);
                        modification = true;
                    }
                    // if duration has been modified, displaying it
                    if (!oldDuration.equals(newDuration)) {
                        mTxtDuration.setText(newDuration + "min");
                        modification = true;
                    }

                    // if there has been a modification
                    if (modification) {

                        // updating task in the model
                        int id = StoreTaskModel.getInstance().updateStoreTask(mPosition, newName, Integer.parseInt(newDuration));
                        Toast.makeText(mContext, mResources.getString(R.string.modification_will_appear_for_later_tasks), Toast.LENGTH_LONG).show();

                        // updating the task in the shared prefs
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(mResources.getString(R.string.task) + id, newName);
                        editor.putInt(mResources.getString(R.string.task) + id + mResources.getString(R.string.duration), Integer.parseInt(newDuration));
                        editor.apply();
                    }
                }
            });
            builder.setNegativeButton(mResources.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // if cancel button was pressed discarding the dialog
                    dialog.cancel();
                }
            });
            builder.show();
        }

        /**
         * Method used to delete the item at the current position.
         */
        private void deleteCurrentItem() {

            // creating an alert dialog asking for confirmation
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(mResources.getString(R.string.question_remove));
            builder.setPositiveButton(mResources.getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // if there is only one task, informing the user that it can't be deleted
                    if (StoreTaskModel.getInstance().getStoreTaskNumber() == 1) {
                        Toast.makeText(mContext, mResources.getString(R.string.cant_remove_last), Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }

                    // if there is more than one task
                    else {

                        // removing task from the model
                        int id = StoreTaskModel.getInstance().removeStoreTask(mPosition);

                        // making adapter redraw everything
                        notifyDataSetChanged();

                        // removing task from the shared prefs
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.remove(mResources.getString(R.string.task) + id);
                        editor.remove(mResources.getString(R.string.task) + id + mResources.getString(R.string.duration));
                        editor.apply();
                    }
                }
            });
            builder.setNegativeButton(mResources.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // if cancel button was pressed discarding the dialog
                    dialog.cancel();
                }
            });
            builder.show();
        }

        /**
         * Method used to set the current position.
         *
         * @param position the current position
         */
        public void setPosition(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            // do nothing
            // it has been added to display the ripple effect
        }
    }
}
