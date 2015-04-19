package ca.yorku.eecs.cse13261.eecs4443project;

import static ca.yorku.eecs.cse13261.eecs4443project.AppConfig.*;
import static ca.yorku.eecs.cse13261.eecs4443project.Utils.*;
import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class ResultsActivity extends Activity {

    AppConfig config;
    Bundle bundle;
    
    String userInitials;
    String participantCode;
    String dataDirectory;
    String dataFile;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);
        setTitle(R.string.results_title);
        config = getConfig(getResources());
        bundle = getIntent().getExtras();
        
        loadBundle();
        ((TextView)findViewById(R.id.resultsFile)).setText(dataDirectory + "/" + dataFile);
        //((TextView)findViewById(R.id.resultsEmail)).setText("mailto:" + config.EMAIL);
    }

    @Override
    public void onBackPressed() {
        clickExit(null);
    }
    
    /// On Click Callbacks
    
    public void clickExit(View view) {
        goToActivity(this, StartActivity.class, null);
    }

    public void clickEmail(View view) {
        Uri file = Uri.parse("file://" + dataDirectory + "/" + dataFile);
        String subject = String.format("EECS 4443 Project [%s, %s]", participantCode, userInitials);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, config.EMAIL);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_STREAM, file);
        emailIntent.setType("text/csv");
        startActivity(Intent.createChooser(emailIntent, "Send email using..."));
    }

    /// Helper
    
    void loadBundle() {
        userInitials    = bundle.getString  (config.INITIALS_KEY);
        participantCode = bundle.getString  (config.PARTICIPANT_KEY);
        dataDirectory   = bundle.getString  (config.DATADIR_KEY);
        dataFile        = bundle.getString  (config.DATAFILE_KEY);
    }
    
} // ResultsActivity
