package ga.ustre.smartwatchsensor.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URI;
import java.util.Calendar;
import java.util.UUID;

import ga.ustre.smartwatchsensor.R;
import ga.ustre.smartwatchsensor.WebSocketClientManager;
import ga.ustre.smartwatchsensor.interfaces.WebSocketClientCallback;
import ga.ustre.smartwatchsensor.interfaces.WebSocketServerBinder;
import ga.ustre.smartwatchsensor.services.WebSocketManagerService;
import rz.thesis.server.serialization.action.Action;
import rz.thesis.server.serialization.action.auth.ReconnectAction;
import rz.thesis.server.serialization.action.lobby.BindSensorSlotAction;
import rz.thesis.server.serialization.action.sensors.SensorDataSendAction;
import rz.thesis.server.serialization.action.sensors.StartWatchingSensorAction;
import rz.thesis.server.serialization.action.sensors.StopWatchingSensorAction;
import utility.MovementType;
import utility.RandomUtils;
import utility.ActionExecutor;
import utility.SensorData;
import utility.SensorType;

public class MainActivity extends WearableActivity
        implements SensorEventListener,
        WebSocketClientCallback,
        ActionExecutor {
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
    private WebSocketServerBinder client;
    private UUID actionID;
    private String lobbyID;
    private boolean heartClicked;
    private boolean handClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle intentBundle = getIntent().getExtras();
        clientId = intentBundle.getString("clientId");
        actionID = UUID.fromString(intentBundle.getString("ACTIONID"));
        lobbyID = intentBundle.getString("LOBBY");
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));

        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        manager.setWifiEnabled(true);

        WifiManager.WifiLock wifiLock=manager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF,TAG);
        wifiLock.acquire();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        pb_searching = (ProgressBar) findViewById(R.id.pb_searching);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        bt_start_stop = (Button) findViewById(R.id.bt_start_stop);
        bt_start_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (client!=null) {
                    client.sendMovement(new SensorDataSendAction("Gerry", new SensorData(Calendar.getInstance().getTimeInMillis(), MovementType.GENERIC_MOVEMENT)));
                }
            }
        });
        bt_start_stop.setVisibility(View.GONE);

        Intent intent = new Intent(this, WebSocketManagerService.class);
        intent.putExtra("ACTIONID",actionID.toString());
        bindService(intent, mConnection, 0);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            client = (WebSocketServerBinder) service;
            addCallbackToClient();
            publishMessage("In attesa dell'Esperienza");
            //client.connect(clientId, URI.create("ws://192.168.1.21:8010/ws"));
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            client = null;
        }
    };

    private void addCallbackToClient(){
        this.client.addCallback(this);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMeasurement();
        if(this.client!=null){
            try {
                this.client.disconnect();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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
    public void onSuccessfulWebsocketConnection() {
        ReconnectAction action = new ReconnectAction(lobbyID);
        client.sendAction(action);
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
    }

    @Override
    public void onActionReceived(Action action) {
        action.execute(this,client);
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

    @Override
    public void triggerVibration() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] vibrationPattern = {0, 500, 50, 300};
        //-1 - don't repeat
        final int indexInPatternToRepeat = -1;
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
    }

    @Override
    public void saveCode(String code) {

    }

    @Override
    public void changeContext(boolean firstContext) {
        startMeasurement();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bt_start_stop.setVisibility(View.VISIBLE);
            }
        });
    }
}
