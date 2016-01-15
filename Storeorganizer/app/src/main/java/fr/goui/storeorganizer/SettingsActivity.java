package fr.goui.storeorganizer;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity implements OnCategoryClickListener {

    private static final int CATEGORY_WORKERS = 0;
    private static final int CATEGORY_TASKS = 1;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupActionbar();

        // Check that the activity is using the layout version with
        // the fragment_settings_container FrameLayout
        if (findViewById(R.id.fragment_settings_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            CategoriesFragment categoriesFragment = new CategoriesFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            categoriesFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_settings_container, categoriesFragment).commit();
        }

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
    }

    private void setupActionbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onCategoryClicked(int position_p) {
        Fragment fragment = null;
        switch (position_p) {
            case CATEGORY_WORKERS:
                fragment = new WorkersCategoryFragment();
                break;
            case CATEGORY_TASKS:
                fragment = new TasksCategoryFragment();
                break;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_settings_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_restore_default) {
            restoreDefault();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void restoreDefault() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.question_restore_default));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StoreWorkerModel.getInstance().clear(getString(R.string.worker));
                StoreTaskModel.getInstance().clear(getString(R.string.task), 30);

                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.clear();

                editor.putInt(getString(R.string.worker_max_id), 0);
                editor.putString(getString(R.string.worker) + 0, getString(R.string.worker));
                editor.putInt(getString(R.string.task_max_id), 0);
                editor.putString(getString(R.string.task) + 0, getString(R.string.task));
                editor.putInt(getString(R.string.task) + 0 + getString(R.string.duration), 30);
                editor.apply();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

}
