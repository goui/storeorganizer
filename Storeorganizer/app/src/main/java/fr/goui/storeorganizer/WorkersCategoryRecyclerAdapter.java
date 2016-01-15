package fr.goui.storeorganizer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

public class WorkersCategoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context _context;
    private LayoutInflater _layoutInflater;
    private List<StoreWorker> _workers;
    private SharedPreferences mSharedPreferences;

    class WorkersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        ImageButton btnEdit;
        ImageButton btnDelete;
        int position;

        public WorkersViewHolder(View itemView_p) {
            super(itemView_p);
            textView = (TextView) itemView_p.findViewById(R.id.layout_simple_item_1tv_text_view);
            btnEdit = (ImageButton) itemView_p.findViewById(R.id.layout_simple_item_1tv_edit_button);
            btnDelete = (ImageButton) itemView_p.findViewById(R.id.layout_simple_item_1tv_delete_button);
            itemView_p.setOnClickListener(this);

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editCurrentItem();
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCurrentItem();
                }
            });

            mSharedPreferences = _context.getSharedPreferences(_context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        }

        private void editCurrentItem() {
            StoreWorker storeWorker = _workers.get(position);
            final EditText input = new EditText(_context);
            input.setText(storeWorker.getName());
            input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            input.setHint(_context.getString(R.string.name));

            AlertDialog.Builder builder = new AlertDialog.Builder(_context);
            builder.setTitle(_context.getString(R.string.edit));
            builder.setView(input);
            builder.setPositiveButton(_context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String oldName = textView.getText().toString();
                    String newName = input.getText().toString();
                    boolean modification = false;
                    if(!oldName.equals(newName)) {
                        textView.setText(newName);
                        modification = true;
                    }
                    if(modification) {
                        int id = StoreWorkerModel.getInstance().updateStoreWorker(position, newName);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(_context.getString(R.string.worker) + id, newName);
                        editor.apply();
                    }
                }
            });
            builder.setNegativeButton(_context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }

        private void deleteCurrentItem() {
            AlertDialog.Builder builder = new AlertDialog.Builder(_context);
            builder.setTitle(_context.getString(R.string.question_remove));
            builder.setPositiveButton(_context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(StoreWorkerModel.getInstance().getStoreWorkerNumber() == 1) {
                        Toast.makeText(_context, _context.getString(R.string.cant_remove_last), Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    } else {
                        int id = StoreWorkerModel.getInstance().removeStoreWorker(position);
                        notifyDataSetChanged();
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.remove(_context.getString(R.string.worker) + id);
                        editor.apply();
                    }
                }
            });
            builder.setNegativeButton(_context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }

        private void setPosition(int position_p) {
            position = position_p;
        }

        @Override
        public void onClick(View v) {
            // do nothing
        }

    }

    public WorkersCategoryRecyclerAdapter(Context context_p, List<StoreWorker> workers_p) {
        _context = context_p;
        _layoutInflater = LayoutInflater.from(_context);
        _workers = workers_p;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent_p, int viewType_p) {
        return new WorkersViewHolder(_layoutInflater.inflate(R.layout.layout_simple_item_1tv, parent_p, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder_p, int position_p) {
        StoreWorker worker = _workers.get(position_p);
        if (worker != null) {
            ((WorkersViewHolder) holder_p).setPosition(position_p);
            ((WorkersViewHolder) holder_p).textView.setText(worker.getName());
        }
    }

    @Override
    public int getItemCount() {
        return _workers.size();
    }

}
