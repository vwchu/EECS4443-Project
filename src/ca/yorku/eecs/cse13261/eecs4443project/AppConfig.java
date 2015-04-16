package ca.yorku.eecs.cse13261.eecs4443project;

import java.util.*;
import android.content.res.*;

class AppConfig {

    int      participants;
    int      groups;
    int      trials;
    String[] modes;
    String[] modeNames;
    Map<String, String> modesMap;
    Map<String, Integer> modesIndex;
    String[] orderings;
    int[]    groupOrdering;
    String   dataDirectory;
    String   dataFilenameFormat;
    String   participantFormat;
    String   groupFormat;
    String   sessionFormat;
    String   trialFormat;
    String   defaultParticipant;
    String   defaultGroup;
    String   defaultTrial;
    double   defaultLatitude;
    double   defaultLongitude;
    float    defaultZoom;
    float    targetMinZoom;
    float    targetMaxZoom;

    // Constants
    final String APP;
    final String LOG_KEY;
    final int    TOUCH_INPUT;
    final int    FACE_INPUT;
    final String DEMO_KEY;
    final String MODE_KEY;
    final String PARTICIPANT_KEY;
    final String GROUP_KEY;
    final String SESSION_KEY;
    final String TRIALS_KEY;
    final String ORDER_KEY;
    final String RUN_KEY;
    final String DATADIR_KEY;
    final String DATAFILE_KEY;

    private AppConfig(Resources r) {

        APP                = r.getString(R.string.app_name);
        LOG_KEY            = r.getString(R.string.app_name);
        TOUCH_INPUT        = r.getInteger(R.integer.constTouchModeID);
        FACE_INPUT         = r.getInteger(R.integer.constFaceModeID);
        DEMO_KEY           = r.getString(R.string.constKeyDemo);
        MODE_KEY           = r.getString(R.string.constKeyMode);
        PARTICIPANT_KEY    = r.getString(R.string.constKeyParticipant);
        GROUP_KEY          = r.getString(R.string.constKeyGroup);
        SESSION_KEY        = r.getString(R.string.constKeySession);
        TRIALS_KEY         = r.getString(R.string.constKeyTrials);
        ORDER_KEY          = r.getString(R.string.constKeyOrder);
        RUN_KEY            = r.getString(R.string.constKeyRunID);
        DATADIR_KEY        = r.getString(R.string.constKeyDataFile);
        DATAFILE_KEY       = r.getString(R.string.constKeyDataDirectory);

        participants       = r.getInteger(R.integer.configParticipants);
        groups             = r.getInteger(R.integer.configGroups);
        trials             = r.getInteger(R.integer.configTrials);
        modes              = r.getStringArray(R.array.configModes);
        modeNames          = r.getStringArray(R.array.configModeNames);
        orderings          = r.getStringArray(R.array.configOrders);
        groupOrdering      = r.getIntArray(R.array.configGroupOrdering);
        dataDirectory      = r.getString(R.string.dataDirectory);
        dataFilenameFormat = r.getString(R.string.dataFilenameFormat);
        participantFormat  = r.getString(R.string.configParticipantFormat);
        groupFormat        = r.getString(R.string.configGroupFormat);
        sessionFormat      = r.getString(R.string.configSessionFormat);
        trialFormat        = r.getString(R.string.configTrialFormat);
        defaultParticipant = r.getString(R.string.configDefaultParticipant);
        defaultGroup       = r.getString(R.string.configDefaultGroup);
        defaultTrial       = r.getString(R.string.configDefaultTrial);

        defaultLatitude    = Double.parseDouble(r.getString(R.string.configDefaultLatitude));
        defaultLongitude   = Double.parseDouble(r.getString(R.string.configDefaultLongitude));
        defaultZoom        =  Float.parseFloat (r.getString(R.string.configDefaultZoom));
        targetMinZoom      =  Float.parseFloat (r.getString(R.string.configTargetMinZoom));
        targetMaxZoom      =  Float.parseFloat (r.getString(R.string.configTargetMaxZoom));

        modesMap   = new HashMap<String, String>();
        modesIndex = new HashMap<String, Integer>();

        for (int i = 0; i < modes.length; i += 1) {
            modesMap.put(modes[i], modeNames[i]);
            modesIndex.put(modes[i], i);
        }
    }

    /// STATIC

    private static AppConfig singleton;

    static AppConfig getConfig(Resources r) {
        if (r != null && singleton == null) {
            singleton = new AppConfig(r);
        }
        return singleton;
    }

} // AppConfig
