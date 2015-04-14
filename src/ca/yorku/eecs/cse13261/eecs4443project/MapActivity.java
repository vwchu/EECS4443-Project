package ca.yorku.eecs.cse13261.eecs4443project;

import java.io.File;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.*;
import com.google.android.gms.maps.model.*;
import android.app.*;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.*;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MapActivity extends Activity implements OnMapReadyCallback, OnCameraChangeListener {

    static final int TOUCH_INPUT = 0;
    static final int ACCELERATOR_INPUT = 1;
    static final int FACE_INPUT = 2;

    TextView mapLat;
    TextView mapLong;
    TextView mapZoom;
    TextView mapTime;
    LinearLayout mapStatus;
    Button mapDoneBtn;

    boolean demo;
    boolean stopped = false;
    String participantCode;
    String groupCode;
    int    trials;
    LatLng startLocation;
    float  startZoom;

    Timer timer;
    PrintWriter dataWriter;
    String[] order;
    int trialNumber;
    String mode;

    GoogleMap googleMap;
    LatLng target;
    double zoom;
    Marker marker;
    long time = 0;

    AcceleratorMode acceleratorMode = new AcceleratorMode();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        fullscreen();
        uiElement();
        initParams();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    void fullscreen() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        getActionBar().hide();
    }

    void initParams() {
        Bundle b = getIntent().getExtras();
        Resources res = getResources();
        
        demo = b.getBoolean("demo");
        mode = b.getString("mode");
        
        if (!demo) {
            participantCode = b.getString("participantCode");
            groupCode = b.getString("groupCode");
            trials = b.getInt("trials");
            order = b.getStringArray("ordering");
            trialNumber = b.getInt("trialNumber");
   
            try {
                dataWriter = new PrintWriter(new File(b.getString("dataDirectory"), b.getString("dataFile")));
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), StartActivity.class));
                finish();
            }
        }

        startZoom = Float.parseFloat(res.getString(R.string.configDefaultZoom));
        zoom = Float.parseFloat(res.getString(R.string.configTargetZoom));
        startLocation = new LatLng(
            Double.parseDouble(res.getString(R.string.configDefaultLatitude)),
            Double.parseDouble(res.getString(R.string.configDefaultLongitude)));

        if ("ACCEL".equals(mode)) {
            acceleratorMode.enableAcceleratorMode();
        }

    }

    void uiElement() {
        mapLat     = (TextView)findViewById(R.id.mapLat);
        mapLong    = (TextView)findViewById(R.id.mapLong);
        mapZoom    = (TextView)findViewById(R.id.mapZoom);
        mapTime    = (TextView)findViewById(R.id.mapTime);
        mapStatus  = (LinearLayout)findViewById(R.id.mapStatus);
        mapDoneBtn = (Button)findViewById(R.id.mapDoneBtn);
    }

    public void clickStart(View view) {
        if (googleMap != null && time == 0) {
            LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
            LatLng northeast = bounds.northeast;
            LatLng southwest = bounds.southwest;
    
            double latDelta = southwest.latitude  - northeast.latitude;
            double lngDelta = southwest.longitude - northeast.longitude;
            double lat = northeast.latitude  + latDelta * 0.2 + Math.random() * latDelta * 0.6;
            double lng = northeast.longitude + lngDelta * 0.2 + Math.random() * lngDelta * 0.6;
    
            target = new LatLng(lat, lng);
            marker = googleMap.addMarker(new MarkerOptions().position(target));
            time = System.currentTimeMillis();

            onCameraChange(googleMap.getCameraPosition());
            view.setVisibility(View.GONE);
            mapStatus.setVisibility(View.VISIBLE);
            
            // TODO
        }
    }

    public void clickDone(View view) {
        if (demo) {
            Intent i = new Intent(getApplicationContext(), StartActivity.class);
            startActivity(i);
            finish();
        } else {
            // TODO
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, startZoom));
        googleMap.setOnCameraChangeListener(this);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        onCameraChange(googleMap.getCameraPosition());
                    }
                });
            }
        }, 0, 100);
    }

    @Override
    public synchronized void onCameraChange(CameraPosition position) {
        if (!stopped && time > 0 && position != null && position.target != null) {
            long elapsed = System.currentTimeMillis() - time;
            double zoomDelta = zoom - position.zoom;
            double latDelta  = target.latitude  - position.target.latitude;
            double longDelta = target.longitude - position.target.longitude;

            mapZoom.setText(String.format(zoomDelta >= 0 ? " +%.1f " : " %.1f ", zoomDelta));
            mapLat.setText(String.format("%.6f", position.target.latitude));
            mapLong.setText(String.format("%.6f", position.target.longitude));
            mapTime.setText(String.format("%02d:%02d:%03d", elapsed / 60 / 1000, elapsed / 1000, elapsed % 1000));
            
            // TODO

            if (Math.abs(zoomDelta) <= 0.05 && Math.abs(latDelta) <= 0.0005 && Math.abs(longDelta) <= 0.0005) {
                timer.cancel();
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                mapStatus.setVisibility(View.GONE);
                mapDoneBtn.setVisibility(View.VISIBLE);
                stopped = true;
            }
        }
    }

    // Accelerator Mode

    class AcceleratorMode implements SensorEventListener {

        /*
         * Below are the alpha values for the low-pass filter. The four values are for the slowest
         * (NORMAL) to fastest (FASTEST) sampling rates, respectively. These values were determined by
         * trial and error. There is a trade-off. Generally, lower values produce smooth but sluggish
         * responses, while higher values produced jerky but fast responses.
         * 
         * Furthermore, there is a difference by device, particularly for the FASTEST setting. For
         * example, the FASTEST sample rate is about 200 Hz on a Nexus 4 but only about 100 Hz on a
         * Samsung Galaxy Tab 10.1.
         * 
         * Fiddle with these, as necessary.
         */
        final float[] ALPHA_VELOCITY = { 0.99f, 0.80f, 0.40f, 0.15f };
        final float[] ALPHA_POSITION = { 0.50f, 0.30f, 0.15f, 0.10f };
        
        final float RADIANS_TO_DEGREES = 57.2957795f;    
        float alpha;
        float[] accValues;
        float x, y, z, pitch, roll;
    
        SensorManager sensorMgr;
        Sensor sensor;
    
        void enableAcceleratorMode() {
            sensorMgr = (SensorManager)getSystemService(SENSOR_SERVICE);
            sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            alpha = ALPHA_POSITION[2];
            if (sensor == null) {
                Toast.makeText(MapActivity.this, "No accelerator", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), StartActivity.class));
                finish();
            }
        }

        /**
         * Low pass filter. The algorithm requires tracking only two numbers - the prior number and the
         * new number. There is a time constant "alpha" which determines the amount of smoothing. Alpha
         * is like a "weight" or "momentum". It determines the effect of the new value on the current
         * smoothed value.
         * 
         * A lower alpha means more smoothing. NOTE: 0 <= alpha <= 1.
         * 
         * See...
         * 
         * http://blog.thomnichols.org/2011/08/smoothing-sensor-data-with-a-low-pass-filter
         */
        protected float[] lowPass(float[] input, float[] output, float alpha) {
            for (int i = 0; i < input.length; i++) {
                output[i] = output[i] + alpha * (input[i] - output[i]);
            }
            return output;
        }
    
        @Override
        public void onSensorChanged(SensorEvent event) {
            accValues = lowPass(event.values.clone(), accValues, alpha);
            x = accValues[0];
            y = -accValues[1]; // MOBIFIED for SAMSUNG DEVICE
            z = accValues[2];
            pitch = (float)Math.atan(y / Math.sqrt(x * x + z * z)) * RADIANS_TO_DEGREES;
            roll = (float)Math.atan(x / Math.sqrt(y * y + z * z)) * RADIANS_TO_DEGREES;
        }
    
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Nothing to do
        }

    } // AcceleratorMode

} // MapActivity
