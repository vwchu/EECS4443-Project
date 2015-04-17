package ca.yorku.eecs.cse13261.eecs4443project;

import java.io.*;
import java.util.*;
import android.hardware.Camera;
import android.hardware.Camera.*;
import android.graphics.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.*;
import com.google.android.gms.maps.model.*;
import com.qualcomm.snapdragon.sdk.face.*;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing.*;
import android.app.*;
import android.content.res.*;
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
    FaceTrackingMode faceMode;
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
    float   zoomThreshold;
    int     xThreshold;
    int     yThreshold;
    int     startDelay;

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
        if (mode == config.FACE_INPUT) {
            initTime      = System.currentTimeMillis();
            zoomThreshold = config.faceZoomThreshold;
            xThreshold    = config.faceXThreshold;
            yThreshold    = config.faceYThreshold;
            startDelay    = config.faceStartDelay;
            faceMode      = new FaceTrackingMode(getResources());

            ui.crosshair.setVisibility(View.GONE);
            ui.onStart();
        } else {
            zoomThreshold = config.touchZoomThreshold;
            xThreshold    = config.touchXThreshold;
            yThreshold    = config.touchYThreshold;
            startDelay    = config.touchStartDelay;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mode == config.FACE_INPUT) {
            faceMode.stopCamera();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mode == config.FACE_INPUT) {
            if (faceMode.camera != null) {
                faceMode.stopCamera();
            }
            faceMode.startCamera();
        }
    }

    /// CLICK CALLBACKS

    public void clickStart(View view) {
        if (mMap.isMapReady()) {
            initTime = System.currentTimeMillis();
            ui.onStart();

            if (mode == config.TOUCH_INPUT) {
                mMap.googleMap.getUiSettings().setScrollGesturesEnabled(true);
                mMap.googleMap.getUiSettings().setZoomGesturesEnabled(true);
                startExperiment = true;
                mMap.addTarget();
                ringTone();
            }
            timer.run();
        }
    }

    public void clickDone(View view) {
        if (demo) {
            goToActivity(this, StartActivity.class, null);
        }
        // TODO
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
        FrameLayout  camPreview;
        DrawView     fpOverlay;
        ImageView    crosshair;

        MapActivityUI() {
            mapLat      = (TextView)     findViewById(R.id.mapLat);
            mapLong     = (TextView)     findViewById(R.id.mapLong);
            mapZoom     = (TextView)     findViewById(R.id.mapZoom);
            mapTime     = (TextView)     findViewById(R.id.mapTime);
            mapStatus   = (LinearLayout) findViewById(R.id.mapStatus);
            mapStartBtn = (Button)       findViewById(R.id.mapStartBtn);
            mapDoneBtn  = (Button)       findViewById(R.id.mapDoneBtn);
            mapFragment = (MapFragment)  getFragmentManager().findFragmentById(R.id.map);
            camPreview  = (FrameLayout)  findViewById(R.id.cameraPreview);
            fpOverlay   = (DrawView)     findViewById(R.id.fpOverlay);
            crosshair   = (ImageView)    findViewById(R.id.crosshair);

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

        void updateFPOverlay(FaceData[] faces) {
            if (startExperiment) {
                float zoom = mMap.targetZoom - mMap.googleMap.getCameraPosition().zoom;
                zoom = ((float) Math.round(zoom * 10)) / 10;
                ui.fpOverlay.updateView(faces, (zoom >= 0 ? "+" : "") + String.format("%.1f", zoom), null);
            } else {
                long timeRemaining = startDelay - (System.currentTimeMillis() - initTime) / 1000;
                ui.fpOverlay.updateView(faces, null, Long.toString(timeRemaining));
            }
        }

        void updateStatus(double latitude, double longitude, float zoom, long time) {
            long seconds = time / 1000;
            long minutes = seconds / 60;

            latitude  = ((double) Math.round(latitude  * Math.pow(10, 6))) / Math.pow(10, 6);
            longitude = ((double) Math.round(longitude * Math.pow(10, 6))) / Math.pow(10, 6);
            zoom      = ((float)  Math.round(zoom * 10)) / 10;

            mapLat .setText((latitude  >= 0 ? "+" : "") + String.format("%.6f", latitude));
            mapLong.setText((longitude >= 0 ? "+" : "") + String.format("%.6f", longitude));
            mapZoom.setText((zoom      >= 0 ? "+" : "") + String.format("%.1f", zoom));
            mapTime.setText(String.format("%02d:%02d:%03d", minutes, seconds, time % 1000));
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

    class Map implements OnMapReadyCallback, OnMarkerClickListener {

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
            googleMap.getUiSettings().setAllGesturesEnabled(false);
            googleMap.getUiSettings().setCompassEnabled(false);
            googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.setOnMarkerClickListener(this);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initPosition, initZoom));
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

        void updateMapByFaceTracking(MFaceData face, List<MFaceData> faces, MFaceData initFace) {
            CameraPosition position = mMap.googleMap.getCameraPosition();
            Projection projection = googleMap.getProjection();
            LatLngBounds bounds = projection.getVisibleRegion().latLngBounds;
            
            Point center = face.getCenter();
            float diff    = face.getDifference();
            float minDiff = initFace.getDifference();
            float maxDiff = ui.mapFragment.getView().getWidth() / 2;
            
            if (diff > maxDiff) {
                diff = maxDiff;
            } else if (diff < minDiff) {
                diff = minDiff;
            }

            float zoomPercent = (diff - minDiff) / (maxDiff - minDiff);
            float zoom = (config.targetMaxZoom - initZoom) * zoomPercent + initZoom;
            
            if (diff == maxDiff) {
                zoom += 0.01f;
            } else if (diff == minDiff) {
                zoom -= 0.01f;
            }

            if (Math.abs(position.zoom - zoom) < 1.0f) {
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
            }
            if (!face.approxiEqualFocus(faces.get(faces.size() - 1))) {
                LatLng latlng = projection.fromScreenLocation(center);
                if (bounds.contains(latlng)) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                }
            }
        }

        // Override: Do nothing
        @Override public boolean onMarkerClick(Marker marker) {return true;}

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

            Point targetPoint   = projection.toScreenLocation(mMap.targetPosition);
            Point positionPoint = projection.toScreenLocation(position.target);
            LatLng current      = position.target;
            
            if (mode == config.FACE_INPUT) {
                positionPoint = faceMode._face.getCenter();
                current = projection.fromScreenLocation(positionPoint);
            }
            
            long elapsed = System.currentTimeMillis() - initTime;
            double dLat  = mMap.targetPosition.latitude  - current.latitude;
            double dLong = mMap.targetPosition.longitude - current.longitude;
            float dZoom  = mMap.targetZoom - position.zoom;
            Point dPoint = new Point(targetPoint.x - positionPoint.x, targetPoint.y - positionPoint.y);

            ui.updateStatus(dLat, dLong, dZoom, elapsed);
            if (!demo) {
                logger.writeRecord(mMap.targetPosition, mMap.targetZoom, position.target, position.zoom, elapsed);
            }
            if (Math.abs(dZoom) <= zoomThreshold && Math.abs(dPoint.x) <= xThreshold && Math.abs(dPoint.y) <= yThreshold) {
                if (mode == config.FACE_INPUT) {
                    ui.updateFPOverlay(faceMode.faces);
                    faceMode.stopCamera();
                }
                ui.onDone();
                ringTone();
                cancel();
            } else {
                start();
            }
        }

    } // Scheduler

    class FaceTrackingMode implements PreviewCallback {

        static final int FRONT_CAMERA_INDEX = 1;

        Resources res;
        Camera camera;
        FacialProcessing faceProc;
        CameraSurfacePreview csPreview;
        Display    display;
        DrawView   fpView;
        FaceData[] faces;
        List<MFaceData> face = new ArrayList<MFaceData>();
        MFaceData  initFace;
        MFaceData  _face;

        FaceTrackingMode(Resources resources) {
            if (!FacialProcessing.isFeatureSupported(FacialProcessing.FEATURE_LIST.FEATURE_FACIAL_PROCESSING)) {
                throwError("Facial processing is not supported");
            }
            res = resources;
            display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
            startCamera();
        }

        void startCamera() {
            try {
                camera = Camera.open(FRONT_CAMERA_INDEX);
                csPreview = new CameraSurfacePreview(MapActivity.this, camera, faceProc);
                ui.camPreview.addView(csPreview);
                camera.setPreviewCallback(this);
                camera.startPreview();
                if (faceProc == null) {
                    faceProc = FacialProcessing.getInstance();
                    faceProc.setProcessingMode(FP_MODES.FP_MODE_VIDEO);
                }
            } catch (Exception e) {
                throwError("Failed to start facial processing");
            }
        }

        void stopCamera() {
            if (camera != null) {
                camera.stopPreview();
                camera.setPreviewCallback(null);
                ui.camPreview.removeView(csPreview);
                camera.release();
                faceProc.release();
                faceProc = null;
            }
            camera = null;
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            PREVIEW_ROTATION_ANGLE angleEnum = PREVIEW_ROTATION_ANGLE.ROT_0;
            Size previewSize  = camera.getParameters().getPreviewSize();
            int surfaceWidth  = ui.mapFragment.getView().getWidth();
            int surfaceHeight = ui.mapFragment.getView().getHeight();
            int displayAngle  = 0;

            switch (display.getRotation()) {
                case 0: displayAngle =  90; angleEnum = PREVIEW_ROTATION_ANGLE.ROT_90; break;
                case 1: displayAngle =   0; angleEnum = PREVIEW_ROTATION_ANGLE.ROT_0; break;
                case 3: displayAngle = 180; angleEnum = PREVIEW_ROTATION_ANGLE.ROT_180; break;
                case 2: break; // This case is never reached.
            }

            if(faceProc == null) {
                faceProc = FacialProcessing.getInstance();
            }

            if (res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                faceProc.setFrame(data, previewSize.width, previewSize.height, true, angleEnum);
                camera.setDisplayOrientation(displayAngle);
            } else if (res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                faceProc.setFrame(data, previewSize.width, previewSize.height, true, angleEnum);
                camera.setDisplayOrientation(displayAngle);
            }

            faceProc.normalizeCoordinates(surfaceWidth, surfaceHeight);

            int numFaces = faceProc.getNumFaces();
            if (numFaces == 0) {
                ui.updateFPOverlay(null);
                if (!startExperiment) {
                    initTime = System.currentTimeMillis();
                }
            } else {
                faces = faceProc.getFaceData();
                ui.updateFPOverlay(faces);
                if (faces == null || faces.length == 0) {
                    if (!startExperiment) {
                        initTime = System.currentTimeMillis();
                    }
                } else {
                    _face = new MFaceData(faces[0]);
                    if (startExperiment) {
                        if (!face.isEmpty()) {
                            mMap.updateMapByFaceTracking(_face, face, initFace);
                        }
                        face.add(_face);
                    } else {
                        if (initFace == null) {
                            initFace = _face;
                            initTime = System.currentTimeMillis();
                        } else if (!initFace.approxiEquals(_face)) {
                            initFace = _face;
                            initTime = System.currentTimeMillis();
                        } else if ((initTime + startDelay * 1000) <= System.currentTimeMillis()) {
                            initTime = System.currentTimeMillis();
                            initFace = _face;
                            face.add(_face);
                            startExperiment = true;
                            mMap.addTarget();
                            ringTone();
                            timer.run();
                        }
                    }
                }
            }
        }

    } // FaceTrackingMode

    class MFaceData {

        final int tolerance = config.faceErrorTolerance;

        int leftEyeX;
        int leftEyeY;
        int rightEyeX;
        int rightEyeY;

        MFaceData(FaceData fd) {
            if (fd == null) {return;}
            if (fd.leftEye != null) {
                leftEyeX = fd.leftEye.x;
                leftEyeY = fd.leftEye.y;
            }
            if (fd.rightEye != null) {
                rightEyeX = fd.rightEye.x;
                rightEyeY = fd.rightEye.y;
            }
        }

        Point getCenter() {
            return new Point((leftEyeX + rightEyeX) / 2, (leftEyeY + rightEyeY) / 2);
        }

        float getDifference() {
            int x = rightEyeX - leftEyeX;
            int y = rightEyeY - leftEyeY;
            
            return (float) Math.sqrt(x * x + y * y);
        }

        boolean approxiEquals(FaceData other) {
            return approxiEquals(new MFaceData(other));
        }
        boolean approxiEquals(MFaceData other) {
            return Math.abs(leftEyeX  - other.leftEyeX)  <= tolerance &&
                   Math.abs(leftEyeY  - other.leftEyeY)  <= tolerance &&
                   Math.abs(rightEyeX - other.rightEyeX) <= tolerance &&
                   Math.abs(rightEyeY - other.rightEyeY) <= tolerance;
        }

        boolean approxiEqualFocus(MFaceData other) {
            return Math.abs(getCenter().x - other.getCenter().x) <= tolerance &&
                   Math.abs(getCenter().y - other.getCenter().y) <= tolerance;
        }

    } // MFaceData

} // MapActivity
