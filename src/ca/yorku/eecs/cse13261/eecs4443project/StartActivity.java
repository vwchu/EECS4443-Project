package ca.yorku.eecs.cse13261.eecs4443project;

import android.app.*;
import android.os.*;
import android.view.*;
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
     * Called when the "Demo Accelerometer"
     * button is pressed. Go to "Map
     * Activity".
     */
    public void clickDemoAccel(View view) {
        clickDemo(view, config.ACCELEROMETER_INPUT);
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
