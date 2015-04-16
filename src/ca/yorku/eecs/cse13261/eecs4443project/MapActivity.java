package ca.yorku.eecs.cse13261.eecs4443project;

import java.io.*;
import java.util.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.*;
import android.graphics.*;
import android.hardware.*;
import android.media.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import static ca.yorku.eecs.cse13261.eecs4443project.Utils.*;
import static ca.yorku.eecs.cse13261.eecs4443project.AppConfig.*;

/**
 * Map Activity
 *
 * @author Vincent Chu
 * @version 1.0
 */
public class MapActivity extends Activity {

    static final int INTERVAL = 50; // milliseconds

    AppConfig config;
    MapActivityUI ui;
    DataLogger logger;
    //AccelerometerMode accelMode;
    Scheduler timer;
    Bundle bundle;
    Map mMap;

    boolean demo;
    int     mode;
    String  participantCode;
    String  groupCode;
    String  sessionCode;
    int     trials;
    int[]   order;
    int     runID;
    String  dataDirectory;
    String  dataFile;
    long    initTime;
    boolean startExperiment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        config = getConfig(getResources());
        bundle = getIntent().getExtras();

        loadBundle();

        mMap   = new Map();
        ui     = new MapActivityUI();
        timer  = new Scheduler(INTERVAL, INTERVAL);

        if (!demo) {
            logger = new DataLogger();
            if (runID == 0) {
                logger.writeHeader();
            }
        }
        if (mode == config.ACCELEROMETER_INPUT) {
            //accelMode = new AccelerometerMode();
        } else if (mode == config.FACE_INPUT) {
            // TODO
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mode == config.ACCELEROMETER_INPUT) {
            //accelMode.unregister();
        } else if (mode == config.FACE_INPUT) {
            // TODO
        }
    }

    /// CLICK CALLBACKS

    public void clickStart(View view) {
        if (mMap.isMapReady()) {
            initTime = System.currentTimeMillis();
            ui.onStart();

            if (mode == config.TOUCH_INPUT) {
                startExperiment = true;
                mMap.addTarget();
                ringTone();
            } else if (mode == config.ACCELEROMETER_INPUT) {
                //accelMode.register();
            } else if (mode == config.FACE_INPUT) {
                // TODO
            }
            timer.run();
        }
    }

    public void clickDone(View view) {
        if (demo) {
            goToActivity(this, StartActivity.class, null);
        }
        if (mode == config.ACCELEROMETER_INPUT) {
            //accelMode.unregister();
        } else if (mode == config.FACE_INPUT) {
            // TODO
        }
    }

    /// HELPER

    void loadBundle() {
        demo = bundle.getBoolean (config.DEMO_KEY);
        mode = bundle.getInt     (config.MODE_KEY);

        if (!demo) {
            return;
        }

        participantCode = bundle.getString  (config.PARTICIPANT_KEY);
        groupCode       = bundle.getString  (config.GROUP_KEY);
        sessionCode     = bundle.getString  (config.SESSION_KEY);
        trials          = bundle.getInt     (config.TRIALS_KEY);
        order           = bundle.getIntArray(config.ORDER_KEY);
        runID           = bundle.getInt     (config.RUN_KEY);
        dataDirectory   = bundle.getString  (config.DATADIR_KEY);
        dataFile        = bundle.getString  (config.DATAFILE_KEY);
    }

    void throwError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        goToActivity(this, StartActivity.class, null);
    }
    void throwError(Throwable e) {
        throwError(e.getMessage());
    }

    void ringTone() {
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
    }

    /// INNER CLASSES

    class DataLogger implements Closeable {

        File             file;
        FileOutputStream outStream;
        PrintWriter      writer;

        DataLogger() {
            try {
                file      = new File(dataDirectory, dataFile);
                outStream = new FileOutputStream(file, true);
                writer    = new PrintWriter(outStream, true);
            } catch (IOException e) {
                throwError(e);
            }
        }

        void writeHeader() {
            write("trial"          , "participant", "group",
                  "session"        , "mode"       , "targetLatitude",
                  "targetLongitude", "targetZoom" , "latitude",
                  "longitude"      , "zoom"       , "millis");
        }

        void writeRecord(LatLng target, float targetZoom, LatLng position, float zoom, long millis) {
            writeRecord(runID + 1, participantCode, groupCode, sessionCode, mode,
                        target.latitude, target.longitude, targetZoom,
                        position.latitude, position.longitude, zoom, millis);
        }

        void writeRecord(int    trial          , String participant, String group,
                         String session        , int    mode       , double targetLatitude,
                         double targetLongitude, float  targetZoom , double latitude,
                         double longitude      , float  zoom       , long   millis)
        {
            write(trial          , participant, group,
                  session        , mode       , targetLatitude,
                  targetLongitude, targetZoom , latitude,
                  longitude      , zoom       , millis);
        }

        public void write(Object... elements) {
            writer.println(StringUtils.join(",", elements));
        }

        @Override
        public void close() throws IOException {
            writer.flush();
            writer.close();
            outStream.close();
        }

    } // DataLogger

    class MapActivityUI {

        TextView     mapLat;
        TextView     mapLong;
        TextView     mapZoom;
        TextView     mapTime;
        LinearLayout mapStatus;
        Button       mapStartBtn;
        Button       mapDoneBtn;
        MapFragment  mapFragment;

        MapActivityUI() {
            mapLat      = (TextView)     findViewById(R.id.mapLat);
            mapLong     = (TextView)     findViewById(R.id.mapLong);
            mapZoom     = (TextView)     findViewById(R.id.mapZoom);
            mapTime     = (TextView)     findViewById(R.id.mapTime);
            mapStatus   = (LinearLayout) findViewById(R.id.mapStatus);
            mapStartBtn = (Button)       findViewById(R.id.mapStartBtn);
            mapDoneBtn  = (Button)       findViewById(R.id.mapDoneBtn);
            mapFragment = (MapFragment)  getFragmentManager().findFragmentById(R.id.map);

            mapFragment.getMapAsync(MapActivity.this.mMap);
            fullscreen();
        }

        void fullscreen() {
            getActionBar().hide();
            if (Build.VERSION.SDK_INT < 16) {
                getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        }

        void updateStatus(double latitude, double longitude, float zoom, long time) {
            latitude  = ((double) Math.round(latitude  * Math.pow(10, 6))) / Math.pow(10, 6);
            longitude = ((double) Math.round(longitude * Math.pow(10, 6))) / Math.pow(10, 6);
            zoom      = ((float)  Math.round(zoom * 10)) / 10;

            mapLat .setText((latitude  >= 0 ? "+" : "") + String.format("%.6f", latitude));
            mapLong.setText((longitude >= 0 ? "+" : "") + String.format("%.6f", longitude));
            mapZoom.setText((zoom      >= 0 ? "+" : "") + String.format("%.1f", zoom));
            mapTime.setText(String.format("%02d:%02d:%03d", time / 60 / 1000, time / 1000, time % 1000));
        }

        void onStart() {
            mapStartBtn.setVisibility(View.GONE);
            mapStatus.setVisibility(View.VISIBLE);
        }

        void onDone() {
            mapStatus.setVisibility(View.GONE);
            mapDoneBtn.setVisibility(View.VISIBLE);
        }

    } // MapActivityUI

    class Map implements OnMapReadyCallback {

        GoogleMap googleMap;
        LatLng initPosition;
        LatLng targetPosition;
        float initZoom;
        float targetZoom;

        Map() {
            initPosition = new LatLng(config.defaultLatitude, config.defaultLongitude);
            initZoom = config.defaultZoom;
        }

        @Override
        public void onMapReady(GoogleMap gMap) {
            googleMap = gMap;
            googleMap.getUiSettings().setRotateGesturesEnabled(false);
            googleMap.getUiSettings().setTiltGesturesEnabled(false);
            googleMap.getUiSettings().setCompassEnabled(false);
            googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initPosition, initZoom));
            if (mode != config.TOUCH_INPUT) {
                //googleMap.getUiSettings().setAllGesturesEnabled(false);
            }
        }

        boolean isMapReady() {
            return googleMap != null;
        }

        void addTarget() {
            LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
            LatLng ne = bounds.northeast;
            LatLng sw = bounds.southwest;
            LatLng delta = new LatLng(sw.latitude - ne.latitude, sw.longitude - ne.longitude);

            targetPosition = new LatLng(
                    ne.latitude  + delta.latitude  * 0.2 + Math.random() * delta.latitude  * 0.6,
                    ne.longitude + delta.longitude * 0.2 + Math.random() * delta.longitude * 0.6);
            targetZoom = config.targetMinZoom + (float) Math.random() * (config.targetMaxZoom - config.targetMinZoom);
            googleMap.addMarker(new MarkerOptions().position(targetPosition));
        }

//        void updateMapByAcc(double tiltAngle, double tiltMagnitude, float velocityZ) {
//            double d = tiltMagnitude * accelMode.GAIN;
//            float dX = (float) ( Math.sin(tiltAngle * accelMode.DEGREES_TO_RADIANS) * d);
//            float dY = (float) (-Math.cos(tiltAngle * accelMode.DEGREES_TO_RADIANS) * d);
//
//            googleMap.moveCamera(CameraUpdateFactory.zoomBy(velocityZ / 100));
//            googleMap.moveCamera(CameraUpdateFactory.scrollBy(dX, dY));
//        }

    } // Map

    class Scheduler extends CountDownTimer implements Runnable {

        Scheduler(long delay, long interval) {
            super(delay, interval);
        }

        @Override public void onTick(long millisUntilFinished) {} // Nothing to do
        @Override public void onFinish() { run(); }

        @Override
        public void run() {
            if (!startExperiment) {
                start(); return;
            }
            CameraPosition position = mMap.googleMap.getCameraPosition();
            Projection projection   = mMap.googleMap.getProjection();

            long elapsed = System.currentTimeMillis() - initTime;
            double dLat  = mMap.targetPosition.latitude  - position.target.latitude;
            double dLong = mMap.targetPosition.longitude - position.target.longitude;
            float dZoom  = mMap.targetZoom - position.zoom;

            Point targetPoint   = projection.toScreenLocation(mMap.targetPosition);
            Point positionPoint = projection.toScreenLocation(position.target);
            Point dPoint        = new Point(targetPoint.x - positionPoint.x, targetPoint.y - positionPoint.y);

            ui.updateStatus(dLat, dLong, dZoom, elapsed);
            if (!demo) {
                logger.writeRecord(mMap.targetPosition, mMap.targetZoom, position.target, position.zoom, elapsed);
            }
            if (Math.abs(dZoom) <= 0.1 && Math.abs(dPoint.x) <= 5 && Math.abs(dPoint.y) <= 5) {
                if (mode == config.ACCELEROMETER_INPUT) {
                    //accelMode.unregister();
                } else if (mode == config.FACE_INPUT) {
                    // TODO
                }
                ui.onDone();
                ringTone();
                cancel();
            } else {
                start();
            }
        }

    } // Scheduler

//    class AccelerometerMode implements SensorEventListener {
//
//        final float GAIN = 0.1f;
//        final float RADIANS_TO_DEGREES = 57.2957795f;
//        final float DEGREES_TO_RADIANS = 0.0174532925f;
//        final float[] ALPHA_VELOCITY = { 0.99f, 0.80f, 0.40f, 0.15f };
//        final float[] ALPHA_POSITION = { 0.50f, 0.30f, 0.15f, 0.10f };
//        final float MAX_MAGNITUDE = 45f;
//        final float alpha = ALPHA_POSITION[2];
//
//        SensorManager sensorManager;
//        Sensor sensor;
//        Stack<Float[]> accValues = new Stack<Float[]>();
//        float[] velocity;
//        long time;
//
//        AccelerometerMode() {
//            sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
//            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//            if (sensor == null) {
//                throwError("Accelerometer not supported");
//            }
//            Float[] acc = new Float[3];
//            Arrays.fill(acc, 0f);
//            accValues.push(acc);
//        }
//
//        void register() {
//            initTime = System.currentTimeMillis();
//            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
//        }
//
//        void unregister() {
//            sensorManager.unregisterListener(this);
//        }
//
//        void updateAcceleration(SensorEvent event) {
//            Float[] lastAcc = accValues.peek();
//            Float[] acc = lowPass(event.values, lastAcc, alpha);
//            float accX  =  (acc[0] - lastAcc[0]);
//            float accY  = -(acc[1] - lastAcc[1]);
//            float accZ  =  (acc[2] - lastAcc[2]);
//            float pitch = (float)Math.atan(accY / Math.sqrt(accX * accX + accZ * accZ)) * RADIANS_TO_DEGREES;
//            float roll  = (float)Math.atan(accX / Math.sqrt(accY * accY + accZ * accZ)) * RADIANS_TO_DEGREES;
//            float tiltMagnitude = (float)Math.sqrt(pitch * pitch + roll * roll);
//            float tiltAngle     = tiltMagnitude == 0f ? 0f : (float)Math.asin(roll / tiltMagnitude) * RADIANS_TO_DEGREES;
//
//            if (pitch > 0 && roll > 0) {
//                tiltAngle = 360f - tiltAngle;
//            } else if (pitch > 0 && roll < 0) {
//                tiltAngle = -tiltAngle;
//            } else if (pitch < 0 && roll > 0) {
//                tiltAngle = tiltAngle + 180f;
//            } else if (pitch < 0 && roll < 0) {
//                tiltAngle = tiltAngle + 180f;
//            }
//
//            float deltaTime = (float)(System.currentTimeMillis() - time) / 1000f;
//            velocity[0] += accX * deltaTime;
//            velocity[1] += accY * deltaTime;
//            velocity[2] += accZ * deltaTime;
//
//            time = System.currentTimeMillis();
//            tiltMagnitude = tiltMagnitude > MAX_MAGNITUDE ? MAX_MAGNITUDE : tiltMagnitude;
//            mMap.updateMapByAcc(tiltAngle, tiltMagnitude, velocity[2]);
//            accValues.push(acc);
//        }
//
//        Float[] lowPass(float[] input, Float[] last, float alpha) {
//            Float[] output = new Float[input.length];
//            for (int i = 0; i < input.length; i++) {
//                output[i] = last[i] + alpha * (input[i] - last[i]);
//            }
//            return output;
//        }
//
//        boolean isStationary(double accX, double accY, double accZ) {
//            double g;
//            if (Math.abs(accX) <= 0.5) {
//                g = Math.sqrt(accY * accY + accZ * accZ);
//            } else if (Math.abs(accY) <= 0.5) {
//                g = Math.sqrt(accX * accX + accZ * accZ);
//            } else {
//                return false;
//            }
//            return g >= 9.5 && g <= 10.0;
//        }
//
//        @Override public void onAccuracyChanged(Sensor sensor, int accuracy) { } // Nothing to do
//
//        @Override
//        public void onSensorChanged(SensorEvent event) {
//            if (startExperiment) {
//                updateAcceleration(event);
//            } else if (isStationary(event.values[0], event.values[1], event.values[2])) {
//                if (initTime + 10 * 1000 <= System.currentTimeMillis()) {
//                    velocity = new float[3];
//                    time = System.currentTimeMillis();
//                    startExperiment = true;
//                    mMap.addTarget();
//                    ringTone();
//                }
//            } else {
//                initTime = System.currentTimeMillis();
//            }
//        }
//
//    } // AccelerometerMode

    
    
} // MapActivity
