package ca.yorku.eecs.cse13261.eecs4443project;

import java.io.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import static ca.yorku.eecs.cse13261.eecs4443project.Utils.*;
import static ca.yorku.eecs.cse13261.eecs4443project.AppConfig.*;

/**
 * Setup Activity
 *
 * This activtiy implements the start
 * view of the applications.
 *
 * @author Vincent Chu
 * @version 1.0
 */
public class SetupActivity extends Activity {

    static final String __SESSION__   = "__session__";

    AppConfig config;
    SetupActivityUI ui;
    Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);
        setTitle(R.string.setup_title);
        config = getConfig(getResources());
        ui = new SetupActivityUI();
        bundle = new Bundle();
    }

    /// CLICK CALLBACKS
    
    public void clickOK(View view) {
        updateBundle();
        createDataFile();
        goToActivity(this, MapActivity.class, bundle);
    }

    public void clickReset(View view) {
        ui.setDefaults(true);
    }

    public void clickExit(View view) {
        if (view != null) {
            saveSettings();
        }
        goToActivity(this, StartActivity.class, null);
    }

    /// HELPERS

    void updateBundle() {
        bundle.putBoolean (config.DEMO_KEY, false);
        bundle.putString  (config.PARTICIPANT_KEY, ui.getSpinnerValue(ui.setupParticipants));
        bundle.putString  (config.GROUP_KEY, ui.getSpinnerValue(ui.setupGroups));
        bundle.putString  (config.SESSION_KEY, bundle.getString(__SESSION__));
        bundle.putInt     (config.TRIALS_KEY, Integer.parseInt(ui.getSpinnerValue(ui.setupTrials)));
        bundle.putIntArray(config.ORDER_KEY, getModeOrdering(ui.setupGroups.getSelectedItemPosition()));
        bundle.putInt     (config.RUN_KEY, 0);
        bundle.putString  (config.DATADIR_KEY, Environment.getExternalStorageDirectory() + config.dataDirectory);
        bundle.putString  (config.DATAFILE_KEY, ui.setupDataFile.getText().toString());
        bundle.putInt     (config.MODE_KEY, bundle.getIntArray(config.ORDER_KEY)[bundle.getInt(config.RUN_KEY)]);
    }

    void saveSettings() {
        SharedPreferences settings = getSharedPreferences(config.APP, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(config.PARTICIPANT_KEY, ui.getSpinnerValue(ui.setupParticipants));
        editor.putString(config.GROUP_KEY      , ui.getSpinnerValue(ui.setupGroups));
        editor.putString(config.TRIALS_KEY     , ui.getSpinnerValue(ui.setupTrials));
        editor.commit();
    }

    void throwError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        clickExit(null);
    }
    void throwError(Throwable e) {
        throwError(e.getMessage());
    }

    int[] getModeOrdering(int groupIdx) {
        String[] ordering = config.orderings[config.groupOrdering[groupIdx]].split(",");
        int[] modeOrder = new int[ordering.length];
        for (int i = 0; i < ordering.length; i += 1) {
            modeOrder[i] = config.modesIndex.get(ordering[i]);
        }
        return modeOrder;
    }
    
    void checkDataDirectory() {
        File directory = new File(Environment.getExternalStorageDirectory() + config.dataDirectory);
        if (!directory.exists() && !directory.mkdirs()) {
            throwError("Cannot open " + directory.getName());
        }
    }

    void createDataFile() {
        try {
            File directory = new File(bundle.getString(config.DATADIR_KEY));
            File dataFile  = new File(directory, bundle.getString(config.DATAFILE_KEY));
            dataFile.createNewFile();
        } catch (IOException e) {
            throwError(e);
        }
    }
    
    String getDataFileName() {
        File directory = new File(Environment.getExternalStorageDirectory() + config.dataDirectory);
        for (int i = 1; ; i += 1) {
            String participant = ui.getSpinnerValue(ui.setupParticipants);
            String group       = ui.getSpinnerValue(ui.setupGroups);
            String session     = String.format(config.sessionFormat, i);
            String dataFile    = String.format(config.dataFilenameFormat, participant, group, session);
            if (!(new File(directory, dataFile)).exists()) {
                bundle.putString(__SESSION__, session);
                return dataFile;
            }
        }
    }
 
    /// INNER CLASSES
    
    class SetupActivityUI implements OnItemSelectedListener {
        
        Spinner  setupParticipants;
        Spinner  setupGroups;
        Spinner  setupTrials;
        TextView setupOrdering;
        TextView setupDataDirectory;
        TextView setupDataFile;        
        
        SetupActivityUI() {
            setupParticipants  = (Spinner)  findViewById(R.id.setupParticipant);
            setupGroups        = (Spinner)  findViewById(R.id.setupGroup);
            setupTrials        = (Spinner)  findViewById(R.id.setupTrials);
            setupOrdering      = (TextView) findViewById(R.id.setupOrdering);
            setupDataDirectory = (TextView) findViewById(R.id.setupDataDirectory);
            setupDataFile      = (TextView) findViewById(R.id.setupDataFile);

            configSpinner(setupParticipants, config.participants, config.participantFormat);
            configSpinner(setupGroups, config.groups, config.groupFormat);
            configSpinner(setupTrials, config.trials, config.trialFormat);
            
            setupParticipants.setOnItemSelectedListener(this);
            setupGroups.setOnItemSelectedListener(this);
            
            checkDataDirectory();
            setupDataDirectory.setText(config.dataDirectory);

            setDefaults(false);
        }

        void setDefaults(boolean reset) {
            SharedPreferences settings = getSharedPreferences(config.APP, Activity.MODE_PRIVATE);
            String defaultParticipant  = config.defaultParticipant;
            String defaultGroup        = config.defaultGroup;
            String defaultTrial        = config.defaultTrial;
            
            if (!reset) {
                defaultParticipant = settings.getString(config.PARTICIPANT_KEY, defaultParticipant);
                defaultGroup       = settings.getString(config.GROUP_KEY      , defaultGroup);
                defaultTrial       = settings.getString(config.TRIALS_KEY     , defaultTrial);
            }

            setSpinner(setupParticipants, defaultParticipant);
            setSpinner(setupGroups, defaultGroup);
            setSpinner(setupTrials, defaultTrial);
        }

        void configSpinner(Spinner spinner, int count, String format) {
            String[] values = new String[count];
            for (int i = 0; i < count; i += 1) { 
                values[i] = String.format(format, i + 1);
            }
            spinner.setAdapter(new ArrayAdapter<CharSequence>(SetupActivity.this, R.layout.spinnerstyle, values));
        }

        void setSpinner(Spinner spinner, String value) {
            for (int i = 0; i < spinner.getCount(); i += 1) { 
                if (spinner.getAdapter().getItem(i).equals(value)) {
                    spinner.setSelection(i);
                }
            }
        }

        String getSpinnerValue(Spinner spinner) {
            return spinner.getAdapter().getItem(spinner.getSelectedItemPosition()).toString();
        }

        void setOrdering(String ordering) {
            String[] modes  = ordering.split(",");
            String[] labels = new String[modes.length];
            for (int i = 0; i < modes.length; i += 1) {
                labels[i] = config.modesMap.get(modes[i]);
            }
            setupOrdering.setText(StringUtils.join("\n", labels));
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            setupDataFile.setText(getDataFileName());
            if (parent == setupGroups) {
                setOrdering(config.orderings[config.groupOrdering[position]]);
            }
            updateBundle();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            setupDataFile.setText(getDataFileName());
            if (parent == setupGroups) {
                setOrdering(config.orderings[config.groupOrdering[0]]);
            }
            updateBundle();
        }

    } // SetupActivityUI
    
} // SetupActivity
