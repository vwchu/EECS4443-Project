package ca.yorku.eecs.cse13261.eecs4443project;

import android.app.*;
import android.content.*;
import android.os.*;

public class Utils {

    /**
     * From the given Activity, start a new
     * activity of the given class and pass the
     * given Bundle. Finish the given Activity.
     *
     * @param from
     *      this current activity
     * @param toActivity
     *      activity class to start new activity
     * @param bundle
     *      data bundle to pass to new activity
     */
    static void goToActivity(Activity from, Class<? extends Activity> toActivity, Bundle bundle) {
        Intent intent = new Intent(from.getApplicationContext(), toActivity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        from.startActivity(intent);
        from.finish();
    }

} // Utils
