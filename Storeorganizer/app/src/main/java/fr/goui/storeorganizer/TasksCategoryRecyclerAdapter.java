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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TasksCategoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context _context;
    private List<StoreTask> _tasks;
    private SharedPreferences mSharedPreferences;

    class TasksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtUpper;
        TextView txtLower;
        ImageButton btnEdit;
        ImageButton btnDelete;
        int position;

        public TasksViewHolder(View itemView_p) {
            super(itemView_p);
            txtUpper = (TextView) itemView_p.findViewById(R.id.layout_simple_item_2tv_upper_text_view);
            txtLower = (TextView) itemView_p.findViewById(R.id.layout_simple_item_2tv_lower_text_view);
            btnEdit = (ImageButton) itemView_p.findViewById(R.id.layout_simple_item_2tv_edit_button);
            btnDelete = (ImageButton) itemView_p.findViewById(R.id.layout_simple_item_2tv_delete_button);
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
            StoreTask storeTask = _tasks.get(position);
            LinearLayout layout = new LinearLayout(_context);
            layout.setOrientation(LinearLayout.VERTICAL);
            final EditText etName = new EditText(_context);
            etName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            etName.setText(storeTask.getName());
            etName.setHint(_context.getString(R.string.name));
            final EditText etDuration = new EditText(_context);
            etDuration.setInputType(InputType.TYPE_CLASS_NUMBER);
            etDuration.setText(String.valueOf(storeTask.getDuration()));
            etDuration.setHint(_context.getString(R.string.duration));
            layout.addView(etName);
            layout.addView(etDuration);

            AlertDialog.Builder builder = new AlertDialog.Builder(_context);
            builder.setTitle(_context.getString(R.string.edit));
            builder.setView(layout);
            builder.setPositiveButton(_context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String oldName = txtUpper.getText().toString();
                    String oldDuration = txtLower.getText().toString();
                    String newName = etName.getText().toString();
                    String newDuration = etDuration.getText().toString();
                    boolean modification = false;
                    if (!oldName.equals(newName)) {
                        txtUpper.setText(newName);
                        modification = true;
                    }
                    if (!oldDuration.equals(newDuration)) {
                        txtLower.setText(newDuration + "min");
                        modification = true;
                    }
                    if (modification) {
                        int id = StoreTaskModel.getInstance().updateStoreTask(position, newName, Integer.parseInt(newDuration));
                        Toast.makeText(_context, _context.getString(R.string.modification_will_appear_for_later_tasks), Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(_context.getString(R.string.task) + id, newName);
                        editor.putInt(_context.getString(R.string.task) + id + _context.getString(R.string.duration), Integer.parseInt(newDuration));
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
                    if (StoreTaskModel.getInstance().getStoreTaskNumber() == 1) {
                        Toast.makeText(_context, _context.getString(R.string.cant_remove_last), Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    } else {
                        int id = StoreTaskModel.getInstance().removeStoreTask(position);
                        notifyDataSetChanged();
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.remove(_context.getString(R.string.task) + id);
                        editor.remove(_context.getString(R.string.task) + id + _context.getString(R.string.duration));
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

        public void setPosition(int position_p) {
            position = position_p;
        }

        @Override
        public void onClick(View v) {
            // do nothing
        }
    }

    public TasksCategoryRecyclerAdapter(Context context_p, List<StoreTask> tasks_p) {
        _context = context_p;
        _tasks = tasks_p;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent_p, int viewType_p) {
        LayoutInflater inflater = LayoutInflater.from(parent_p.getContext());
        View timeView = inflater.inflate(R.layout.layout_simple_item_2tv, parent_p, false);
        return new TasksViewHolder(timeView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder_p, int position_p) {
        StoreTask task = _tasks.get(position_p);
        if (task != null) {
            ((TasksViewHolder) holder_p).setPosition(position_p);
            ((TasksViewHolder) holder_p).txtUpper.setText(task.getName());
            ((TasksViewHolder) holder_p).txtLower.setText(task.getDuration() + "min");
        }
    }

    @Override
    public int getItemCount() {
        return _tasks.size();
    }
}
