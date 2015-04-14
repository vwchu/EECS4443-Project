package ca.yorku.eecs.cse13261.eecs4443project;

import android.app.Activity;	
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SetupActivity extends Activity implements OnItemSelectedListener {

    Map<Spinner, String[]> spinValues;
    
    Map<String, String> modes = new HashMap<String, String>();
    String[] orderings;
    String[] order;
    String dataDirectory;
    String dataFile;

    Spinner  setupParticipants;
    Spinner  setupGroups;
    Spinner  setupTrials;
    TextView setupOrdering;
    TextView setupDataDirectory;
    TextView setupDataFile;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);
        setTitle(R.string.setup_title);

        Resources res      = getResources();
        String[] mModes    = res.getStringArray(R.array.configModes);
        String[] modeNames = res.getStringArray(R.array.configModeNames);
        
        for (int i = 0; i < mModes.length; i++) {
            modes.put(mModes[i], modeNames[i]);
        }

        orderings = res.getStringArray(R.array.configOrders);

        configureUI(res);
        setDefaults(res, false);
    }

    // ===== Clicks =====

    /** Called when the "OK" button is pressed. */
    public void clickOK(View view) {
        Bundle b = getSettingBundle();
        
        b.putString("dataDirectory", dataDirectory);
        b.putString("dataFile", dataFile);
        b.putStringArray("ordering", order);
        b.putInt("trialNumber", 1);
        b.putString("mode", order[0]);
        
        Intent i = new Intent(getApplicationContext(), MapActivity.class);
        
        try {
            File directory = new File(Environment.getExternalStorageDirectory() + dataDirectory);
            (new File(directory, dataFile)).createNewFile(); // actually created it
        } catch (IOException e) {
            Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            i = new Intent(getApplicationContext(), StartActivity.class);
        }

        i.putExtras(b);
        saveSettings(b);
        startActivity(i);
        finish();
    }

    /** Called when the "Reset" button is pressed. */
    public void clickReset(View view) {
        setDefaults(this.getResources(), true);
    }

    /** Called when the "Exit" button is pressed. */
    public void clickExit(View view) {
        saveSettings(getSettingBundle());
        Intent i = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(i);
        finish();
    }

    // ===== Helpers =====

    // Defaults

    void setDefaults(Resources r, boolean reset) {
        if (reset) {
            setSpinners(r.getString(R.string.configDefaultParticipant), 
                        r.getString(R.string.configDefaultGroup), 
                        r.getString(R.string.configDefaultTrial));
        } else {
            String appKey = this.getResources().getString(R.string.app_name);
            SharedPreferences settings = getSharedPreferences(appKey, Activity.MODE_PRIVATE);
            setSpinners(settings.getString("participantCode", r.getString(R.string.configDefaultParticipant)), 
                        settings.getString("groupCode"      , r.getString(R.string.configDefaultGroup)), 
                        settings.getString("trials"         , r.getString(R.string.configDefaultTrial)));            
        }
    }

    // Bundles
    
    Bundle getSettingBundle() {
        Bundle b = new Bundle();
        b.putString("participantCode", spinValues.get(setupParticipants)[setupParticipants.getSelectedItemPosition()]);
        b.putString("groupCode", spinValues.get(setupGroups)[setupGroups.getSelectedItemPosition()]);
        b.putInt("trials", Integer.parseInt(spinValues.get(setupTrials)[setupTrials.getSelectedItemPosition()]));
        return b;
    }

    void saveSettings(Bundle b) {
        String appKey = this.getResources().getString(R.string.app_name);
        SharedPreferences settings = getSharedPreferences(appKey, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("participantCode", b.getString("participantCode"));
        editor.putString("groupCode"      , b.getString("groupCode"));
        editor.putString("trials"         , Integer.toString(b.getInt("trials")));
        editor.commit();
    }
    
    // UI
    
    void configureUI(Resources r) {
        uIElements();
        populateSpinners(r);       
        setupParticipants.setOnItemSelectedListener(this);
        setupGroups.setOnItemSelectedListener(this);
    }

    void uIElements() {
        setupParticipants  = (Spinner)  findViewById(R.id.setupParticipant);
        setupGroups        = (Spinner)  findViewById(R.id.setupGroup);
        setupTrials        = (Spinner)  findViewById(R.id.setupTrials);
        setupOrdering      = (TextView) findViewById(R.id.setupOrdering);
        setupDataDirectory = (TextView) findViewById(R.id.setupDataDirectory);
        setupDataFile      = (TextView) findViewById(R.id.setupDataFile);
    }

    void populateSpinners(Resources r) {
        Map<Spinner, Integer> spins = new HashMap<Spinner, Integer>(); 
            spins.put(setupParticipants, r.getInteger(R.integer.configParticipants));
            spins.put(setupGroups      , r.getInteger(R.integer.configGroups)); 
            spins.put(setupTrials      , r.getInteger(R.integer.configTrials));
        Map<Spinner, String> spinFormat = new HashMap<Spinner, String>();
            spinFormat.put(setupParticipants, "P%02d");
            spinFormat.put(setupGroups      , "G%02d");
        Map<Spinner, String[]> spinValues = new HashMap<Spinner, String[]>();
        for (Spinner spinner : spins.keySet()) {
            int count = spins.get(spinner);
            String[] values = new String[count];
            for (int i = 0; i < count; i++) {
                values[i] = spinFormat.containsKey(spinner) 
                        ? String.format(spinFormat.get(spinner), i + 1)
                        : Integer.toString(i + 1);}
            spinValues.put(spinner, values);}
        for (Spinner spinner : spinValues.keySet()) {
            spinner.setAdapter(new ArrayAdapter<CharSequence>(this,
                    R.layout.spinnerstyle, spinValues.get(spinner)));}
        this.spinValues = spinValues;
    }

    void setSpinners(String participant, String group, String trial) {
        Map<Spinner, String> values = new HashMap<Spinner, String>();
           values.put(setupParticipants, participant);
           values.put(setupGroups      , group);
           values.put(setupTrials      , trial);
        for (Spinner spinner : spinValues.keySet()) {
           for (int i = 0; i < spinValues.get(spinner).length; ++i) {
               if (values.get(spinner).equals(spinValues.get(spinner)[i])) {
                   spinner.setSelection(i);}}}
    }

    void setOrdering(String ordering) {
        order = ordering.split(",");
        String text = "";
        for (String mode : order) {
            text += modes.get(mode) + "\n";
        }
        setupOrdering.setText(text);
    }
    
    boolean canUseFile(Resources res, File directory, int block) {
        Bundle b = getSettingBundle();
        dataFile = String.format(res.getString(R.string.dataFilenameFormat), 
                            b.getString("participantCode"),
                            b.getString("groupCode"),
                            String.format("B%02d", block));
        File file = new File(directory, dataFile);
        return !file.exists();
    }

    void updateLogging(Resources res) {
        try {
            dataDirectory = res.getString(R.string.dataDirectory);
            File directory = new File(Environment.getExternalStorageDirectory() + dataDirectory);
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException("Cannot open " + dataDirectory);
            }
            for (int i = 1; ; i++) {
                if (canUseFile(res, directory, i)) {break;}
            }
            File file = new File(directory, dataFile);
            if (!file.createNewFile()) {
                throw new IOException("Cannot open " + dataFile);
            }
            file.delete(); // don't create it quite yet
            setupDataDirectory.setText(dataDirectory);
            setupDataFile.setText(dataFile);
        } catch (Exception e) {
            Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            clickExit(null);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        updateLogging(getResources());
        if (parentView == setupGroups) {
            setOrdering(orderings[position]);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parentView) {
        updateLogging(getResources());
        if (parentView == setupGroups) {
            setOrdering(orderings[0]);
        }
    }

} // SetupActivity
