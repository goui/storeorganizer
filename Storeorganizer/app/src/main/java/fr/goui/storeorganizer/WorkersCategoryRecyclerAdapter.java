package fr.goui.storeorganizer;

import android.content.Context;
import android.content.DialogInterface;
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
    private List<StoreWorker> _workers;

    class WorkersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        ImageButton btnEdit;
        ImageButton btnDelete;
        int position;
        boolean toggle;

        public WorkersViewHolder(View itemView_p) {
            super(itemView_p);
            textView = (TextView) itemView_p.findViewById(R.id.layout_simple_item_text_view);
            btnEdit = (ImageButton) itemView_p.findViewById(R.id.layout_simple_item_edit_button);
            btnDelete = (ImageButton) itemView_p.findViewById(R.id.layout_simple_item_delete_button);
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
        }

        private void editCurrentItem() {
            AlertDialog.Builder builder = new AlertDialog.Builder(_context);
            builder.setTitle(_context.getString(R.string.edit_workers_name));
            final EditText input = new EditText(_context);
            input.setText(textView.getText());
            input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            builder.setView(input);
            builder.setPositiveButton(_context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = input.getText().toString();
                    textView.setText(name);
                    StoreWorkerModel.getInstance().updateStoreWorker(position, name);
                    // TODO change in shared prefs
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
            builder.setTitle(_context.getString(R.string.question_remove_worker));
            builder.setPositiveButton(_context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(StoreWorkerModel.getInstance().getStoreWorkerNumber() == 1) {
                        Toast.makeText(_context, _context.getString(R.string.cant_remove_last_worker), Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    } else {
                        StoreWorkerModel.getInstance().removeStoreWorker(position);
                        notifyItemRemoved(position);
                        // TODO change in shared prefs
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
            if(toggle) {
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
            } else {
                btnEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
            }
            toggle = !toggle;
        }

    }

    public WorkersCategoryRecyclerAdapter(Context context_p, List<StoreWorker> workers_p) {
        _context = context_p;
        _workers = workers_p;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent_p, int viewType_p) {
        LayoutInflater inflater = LayoutInflater.from(parent_p.getContext());
        View timeView = inflater.inflate(R.layout.layout_simple_item, parent_p, false);
        return new WorkersViewHolder(timeView);
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
