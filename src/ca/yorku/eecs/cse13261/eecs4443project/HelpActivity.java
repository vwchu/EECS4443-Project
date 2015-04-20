package ca.yorku.eecs.cse13261.eecs4443project;

import static ca.yorku.eecs.cse13261.eecs4443project.Utils.*;
import android.app.*;
import android.os.*;
import android.view.*;

public class HelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        setTitle(R.string.help_title);
    }

    @Override
    public void onBackPressed() {
        clickExit(null);
    }

    /// On Click Callbacks

    public void clickExit(View view) {
        goToActivity(this, StartActivity.class, null);
    }

} // HelpActivity
