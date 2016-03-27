package fr.goui.storeorganizer.adapter;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fr.goui.storeorganizer.R;
import fr.goui.storeorganizer.model.StoreWorker;
import fr.goui.storeorganizer.model.StoreWorkerModel;

/**
 * {@code WorkersCategoryRecyclerAdapter} is a {@code RecyclerView.Adapter}.
 * It is used to display {@code StoreWorker}s in the {@code RecyclerView} of the {@code WorkersCategoryFragment}.
 */
public class WorkersCategoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * The {@code Context} used to get {@code Resources} and {@code LayoutInflater}.
     */
    private Context mContext;

    /**
     * The {@code LayoutInflater} used to inflate views.
     */
    private LayoutInflater mLayoutInflater;

    /**
     * The list of all the {@code StoreWorker}s.
     */
    private List<StoreWorker> mWorkers;

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
    public WorkersCategoryRecyclerAdapter(Context context) {
        mContext = context;
        mResources = mContext.getResources();
        mLayoutInflater = LayoutInflater.from(mContext);
        mWorkers = StoreWorkerModel.getInstance().getStoreWorkers();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // creating the view holder with the inflated layout
        return new WorkersViewHolder(mLayoutInflater.inflate(R.layout.fragment_settings_item_worker, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // getting the item at the current position
        StoreWorker worker = mWorkers.get(position);
        if (worker != null) {

            // if not null, displaying its information
            ((WorkersViewHolder) holder).setPosition(position);
            ((WorkersViewHolder) holder).mTxtName.setText(worker.getName());
        }
    }

    @Override
    public int getItemCount() {
        return mWorkers.size();
    }

    /**
     * {@code WorkersViewHolder} is a {@code RecyclerView.ViewHolder} used to store inflated views for one worker.
     */
    class WorkersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // the views for one item
        private TextView mTxtName;
        private ImageButton mBtnEdit;
        private ImageButton mBtnDelete;
        private int mPosition;

        /**
         * Constructor getting all the views in the passed layout and setting listener to them if needed.
         *
         * @param itemView the layout
         */
        public WorkersViewHolder(View itemView) {
            super(itemView);

            // getting all the views for this item
            mTxtName = (TextView) itemView.findViewById(R.id.fragment_settings_item_worker_name_text_view);
            mBtnEdit = (ImageButton) itemView.findViewById(R.id.fragment_settings_item_worker_edit_button);
            mBtnDelete = (ImageButton) itemView.findViewById(R.id.fragment_settings_item_worker_delete_button);
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
            StoreWorker storeWorker = mWorkers.get(mPosition);

            // creating an edit text for the name of the worker
            final EditText input = new EditText(mContext);
            input.setText(storeWorker.getName());
            input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            input.setHint(mResources.getString(R.string.name));

            // creating the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(mResources.getString(R.string.edit));
            builder.setView(input);
            builder.setPositiveButton(mResources.getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // the name before edition
                    String oldName = mTxtName.getText().toString();
                    String newName = input.getText().toString();

                    // if name has been modified, displaying it
                    boolean modification = false;
                    if (!oldName.equals(newName)) {
                        mTxtName.setText(newName);
                        modification = true;
                    }

                    // if there has been a modification
                    if (modification) {

                        // updating worker in the model
                        int id = StoreWorkerModel.getInstance().updateStoreWorker(mPosition, newName);

                        // updating the worker in the shared prefs
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(mResources.getString(R.string.worker) + id, newName);
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

                    // if there is only one worker, informing the user that it can't be deleted
                    if (StoreWorkerModel.getInstance().getStoreWorkersNumber() == 1) {
                        Toast.makeText(mContext, mResources.getString(R.string.cant_remove_last), Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }

                    // if there is more than one task
                    else {

                        // removing worker from the model
                        int id = StoreWorkerModel.getInstance().removeStoreWorker(mPosition);

                        // making adapter redraw everything
                        notifyDataSetChanged();

                        // removing task from the shared prefs
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.remove(mResources.getString(R.string.worker) + id);
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
        private void setPosition(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            // do nothing
            // it has been added to display the ripple effect
        }

    }

}
