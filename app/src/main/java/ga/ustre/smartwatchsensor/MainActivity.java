package ga.ustre.smartwatchsensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import serialization.action.Action;
import serialization.action.management.SmartwatchEnterLobbyAction;
import serialization.action.sensors.SensorDataSendAction;
import utility.MovementType;
import utility.RandomUtils;
import utility.ResultPresenter;
import utility.SensorData;

public class MainActivity extends WearableActivity
        implements SensorEventListener, UDPDiscovery.Callbacks,
        WebSocketClientManager.Callbacks,
        ResultPresenter{
    private static final String TAG = "galileo/main";
    private final static int SENS_GYROSCOPE = Sensor.TYPE_GYROSCOPE;
    private final static int SENS_LINEAR_ACCELERATION = Sensor.TYPE_LINEAR_ACCELERATION;
    private final static int SENS_ROTATION_VECTOR = Sensor.TYPE_ROTATION_VECTOR;


    SensorManager mSensorManager;

    private BoxInsetLayout mContainerView;
    private ProgressBar pb_searching;
    private TextView tv_progress;
    private ImageView iv_icon;
    private Button bt_start_stop;

    private String clientId ;
    private UDPDiscovery discovery;
    private WebSocketClientManager client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String deviceName= RandomUtils.getDeviceName();
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();
        clientId = deviceName + " " + address;
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        pb_searching = (ProgressBar) findViewById(R.id.pb_searching);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        bt_start_stop = (Button) findViewById(R.id.bt_start_stop);
        bt_start_stop.setText("Move");
        bt_start_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (client!=null) {
                    client.sendMovement(new SensorDataSendAction("Gerry", new SensorData(Calendar.getInstance().getTimeInMillis(), MovementType.GENERIC_MOVEMENT)));
                }
            }
        });
        this.discovery = new UDPDiscovery(this);
        this.discovery.SendDiscovery();
        startMeasurement();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.discovery.isWorking()){
            this.discovery.stopDiscovery();
        }
        stopMeasurement();
        if(this.client!=null){
            try {
                this.client.disconnect();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void updateDisplay() {
        /*if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTextView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            mTextView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.GONE);
        }*/
    }

    protected void startMeasurement() {



        Sensor gyroscopeSensor = mSensorManager.getDefaultSensor(SENS_GYROSCOPE);
        Sensor linearAccelerationSensor = mSensorManager.getDefaultSensor(SENS_LINEAR_ACCELERATION);
        Sensor rotationVectorSensor = mSensorManager.getDefaultSensor(SENS_ROTATION_VECTOR);


        // Register the listener
        if (mSensorManager != null) {

            if (linearAccelerationSensor != null) {
                mSensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No linear accelerometer found");
            }


            if (gyroscopeSensor != null) {
                mSensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No Gyroscope Sensor found");
            }

            if (rotationVectorSensor != null) {
                mSensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Rotation Vector Sensor found");
            }


        }
    }
    private void stopMeasurement() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int maximum = 3;
        if (sensorEvent.sensor.getType() == 10) {
            if (sensorEvent.values[0] > maximum || sensorEvent.values[1] > maximum || sensorEvent.values[2] > maximum) {
                if (client != null) {
                    client.sendMovement(new SensorDataSendAction("Gerry", new SensorData(1l, MovementType.GENERIC_MOVEMENT)));
                }

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    public void onAddressDiscovered(final String address) {
        hideProgressBar();
        publishMessage("Trovato server a:" + address);
        client = new WebSocketClientManager(clientId, URI.create("ws://" + address + ":8091"), this);
        client.connect();
    }

    @Override
    public void onProgressUpdate(final String message) {
        publishMessage(message);
    }

    @Override
    public void onSuccesfulWebsocketConnection() {
        publishMessage("Connected to the server");
        showIcon(R.drawable.ic_connected);
        SmartwatchEnterLobbyAction eza = new SmartwatchEnterLobbyAction(this.clientId.toString());
        client.sendAction(eza);
    }

    @Override
    public void onFailedConnection() {
        publishMessage("Connection failed");

    }

    @Override
    public void onMaxReconnectionAttemptsReached() {
        publishMessage("Reconnection failed");
        hideIcon();
        showProgressBar();
        this.discovery.SendDiscovery();
    }

    @Override
    public void onActionReceived(Action action) {
        if (action != null) {
            action.execute(this);
        }
    }

    @Override
    public void publishMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_progress.setText(message);
            }
        });
    }

    @Override
    public void showProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pb_searching.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pb_searching.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void showIcon(final int drawable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_icon.setImageDrawable(getDrawable(drawable));
                iv_icon.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void hideIcon() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_icon.setVisibility(View.INVISIBLE);
            }
        });
    }
}
