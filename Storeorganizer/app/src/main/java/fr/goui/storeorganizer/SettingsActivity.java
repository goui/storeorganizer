package fr.goui.storeorganizer;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * {@code SettingsActivity} is the activity used to customize workers, tasks and other app's settings.
 */
public class SettingsActivity extends AppCompatActivity implements OnCategoryClickListener {

    /**
     * The constant for the workers' category.
     */
    private static final int CATEGORY_WORKERS = 0;

    /**
     * The constant for the tasks' category.
     */
    private static final int CATEGORY_TASKS = 1;

    /**
     * The {@code SharedPreferences}.
     */
    private SharedPreferences mSharedPreferences;

    /**
     * The android resources to get project values.
     */
    private Resources mResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setting the layout
        setContentView(R.layout.activity_settings);

        // getting the resources
        mResources = getResources();

        // setting up the action bar
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

        // getting the shared prefs
        mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
    }

    /**
     * Method used to set up the action bar.
     */
    private void setupActionbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onCategoryClicked(int position_p) {

        // creating the right fragment depending on the category clicked
        Fragment fragment = null;
        switch (position_p) {
            case CATEGORY_WORKERS:
                fragment = new WorkersCategoryFragment();
                break;
            case CATEGORY_TASKS:
                fragment = new TasksCategoryFragment();
                break;
        }

        // putting the selected fragment to the front
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_settings_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // creating menu
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // if we press the up button going back
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        // if we press the restore default button restoring default
        if (id == R.id.action_restore_default) {
            restoreDefault();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method used to restore default for workers and tasks.
     * An {@link AlertDialog} will be created asking for confirmation.
     * Once confirmed all information about workers and tasks will be erased and a default worker and a default task will be created in both {@code SharedPreferences} and models.
     */
    private void restoreDefault() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(mResources.getString(R.string.question_restore_default));
        builder.setPositiveButton(mResources.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // clearing models and creating default worker and task
                StoreWorkerModel.getInstance().clear(getString(R.string.worker));
                StoreTaskModel.getInstance().clear(getString(R.string.task), mResources.getInteger(R.integer.task_default_duration));

                // clearing shared prefs
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.clear();

                // creating the default worker
                editor.putInt(mResources.getString(R.string.worker_max_id), 0);
                editor.putString(mResources.getString(R.string.worker) + 0, mResources.getString(R.string.worker));

                // creating the default task
                editor.putInt(mResources.getString(R.string.task_max_id), 0);
                editor.putString(mResources.getString(R.string.task) + 0, mResources.getString(R.string.task));
                editor.putInt(mResources.getString(R.string.task) + 0 + mResources.getString(R.string.duration),
                        mResources.getInteger(R.integer.task_default_duration));

                // putting them in the shared prefs
                editor.apply();
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

}
