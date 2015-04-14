package ca.yorku.eecs.cse13261.eecs4443project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        setTitle(R.string.start_title);
    }

    public void clickRunExperiment(View view) {
        Intent i = new Intent(getApplicationContext(), SetupActivity.class);
        startActivity(i);
        finish();
    }

    public void clickDemo(View view, int mode) {
        Bundle b = new Bundle();
        b.putInt("mode", mode);
        b.putBoolean("demo", true);

        Intent i = new Intent(getApplicationContext(), MapActivity.class);
        i.putExtras(b);
        startActivity(i);
        finish();
    }

    public void clickDemoTouch(View view) {
        clickDemo(view, MapActivity.TOUCH_INPUT);
    }
    
    public void clickDemoAccel(View view) {
        clickDemo(view, MapActivity.ACCELERATOR_INPUT);
    }
    
    public void clickDemoFace(View view) {
        clickDemo(view, MapActivity.FACE_INPUT);
    }

    /** Called when the "Exit" button is pressed. */
    public void clickExit(View view) {
        super.onDestroy();  // cleanup
        this.finish();      // terminate
    }

}
