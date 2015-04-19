package ca.yorku.eecs.cse13261.eecs4443project;

import java.io.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import static ca.yorku.eecs.cse13261.eecs4443project.Utils.*;
import static ca.yorku.eecs.cse13261.eecs4443project.AppConfig.*;

/**
 * Start Activity
 *
 * @author Vincent Chu
 * @version 1.0
 */
public class StartActivity extends Activity {

    AppConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        setTitle(R.string.start_title);
        config = getConfig(getResources());
    }

    /// CLICK CALLBACKS

    /**
     * Called when the "Run Experiment"
     * button is clicked. Go to "Setup
     * Activity".
     */
    public void clickRunExperiment(View view) {
        goToActivity(this, SetupActivity.class, null);
    }

    /**
     * Called when the "Demo Touch"
     * button is clicked. Go to "Map
     * Activity".
     */
    public void clickDemoTouch(View view) {
        clickDemo(view, config.TOUCH_INPUT);
    }

    /**
     * Called when the "Demo Facial Tracking"
     * button is pressed. Go to "Map
     * Activity".
     */
    public void clickDemoFace(View view) {
        clickDemo(view, config.FACE_INPUT);
    }

    /**
     * Called when the "Cleanup Data" button
     * is pressed. Deletes all data files.
     */
    public void clickCleanupData(View view) {
        (new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                return (new AlertDialog.Builder(getActivity()))
                        .setMessage(R.string.start_clean_message)
                        .setPositiveButton(R.string.start_clean_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                File directory = new File(Environment.getExternalStorageDirectory() + config.dataDirectory);
                                for (File file : directory.listFiles()) {
                                    file.delete();
                                }
                                Toast.makeText(StartActivity.this, directory.delete() 
                                        ? config.dataDirectory + " has been deleted."
                                        : config.dataDirectory + " could not be deleted.", 
                                    Toast.LENGTH_LONG).show();
                            }})
                        .setNegativeButton(R.string.start_clean_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                return;
                            }})
                        .create();
            }
        }).show(getFragmentManager(), "DeleteAlertDialogFragment");
    }

    /**
     * Called when the "Instructions" or "Help" button
     * is pressed. Shows the help page.
     */
    public void clickHelp(View view) {
        goToActivity(this, HelpActivity.class, null);
    }
    
    /**
     * Called when the "Exit" button is pressed.
     * Stops the application.
     */
    public void clickExit(View view) {
        super.onDestroy();
        finish();
    }

    /// HELPERS

    /**
     * Go to "Map Activity" in Demo mode
     * with the given input method.
     */
    void clickDemo(View view, int mode) {
        Bundle bundle = new Bundle();
        bundle.putInt(config.MODE_KEY, mode);
        bundle.putBoolean(config.DEMO_KEY, true);
        goToActivity(this, MapActivity.class, bundle);
    }

} // StartActivity
