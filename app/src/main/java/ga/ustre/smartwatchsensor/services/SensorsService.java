package ga.ustre.smartwatchsensor.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import ga.ustre.smartwatchsensor.R;
import ga.ustre.smartwatchsensor.UDPDiscovery;
import ga.ustre.smartwatchsensor.WebSocketClientManager;
import ga.ustre.smartwatchsensor.webvisservices.DiscoveryServicesDefinitions;
import rz.thesis.server.serialization.action.Action;
import rz.thesis.server.serialization.action.sensors.SensorDataSendAction;
import utility.MovementType;
import utility.SensorData;

public class SensorsService extends Service implements SensorEventListener, UDPDiscovery.Callbacks, WebSocketClientManager.Callbacks {
    private static final String TAG = "testSen/SensorsService";
    private final static int SENS_GYROSCOPE = Sensor.TYPE_GYROSCOPE;
    private final static int SENS_LINEAR_ACCELERATION = Sensor.TYPE_LINEAR_ACCELERATION;
    private final static int SENS_ROTATION_VECTOR = Sensor.TYPE_ROTATION_VECTOR;

    SensorManager mSensorManager;

    private Sensor mHeartrateSensor;
    private String clientId = UUID.randomUUID().toString();
    private UDPDiscovery discovery;
    private WebSocketClientManager client;
    private ScheduledExecutorService mScheduler;
    private SparseArray<List<float[]>> measurements = new SparseArray<>();

    public SensorsService() {
    }

    @Override
    public void onCreate() {

        this.discovery = new UDPDiscovery(this);
        this.discovery.SendDiscovery();
        //
        startMeasurement();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopMeasurement();
    }

    private void sendNotification(String message) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Galileo");
        builder.setContentText(message);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        startForeground(1, builder.build());
    }

    protected void startMeasurement() {
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));


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
        if (mScheduler != null && !mScheduler.isTerminated()) {
            mScheduler.shutdown();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }


    @Override
    public synchronized void onSensorChanged(SensorEvent event) {
        /*List<float[]> measurementsList = this.measurements.get(event.sensor.getType(),new ArrayList<float[]>());
        if (measurementsList.size()==0){
            this.measurements.append(event.sensor.getType(), measurementsList);
        }
        measurementsList.add(event.values);
        checkMovement();*/
        int maximum = 3;
        if (event.sensor.getType() == 10) {
            if (event.values[0] > maximum || event.values[1] > maximum || event.values[2] > maximum) {
                if (client != null) {
                    client.sendMovement(new SensorDataSendAction("Gerry", new SensorData(1l, MovementType.GENERIC_MOVEMENT)));
                }

            }
        }
    }

    private void checkMovement() {
        List<float[]> measurementsList = this.measurements.get(10, new ArrayList<float[]>());
        float max = Float.MIN_VALUE;
        for (float[] values : measurementsList) {

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {    }


    @Override
    public void onAddressDiscovered(String address, DiscoveryServicesDefinitions defs) {
        client = new WebSocketClientManager(clientId,URI.create("ws://" + address + ":8010"), this);
        client.connect();
    }

    @Override
    public void onProgressUpdate(String message) {
        sendNotification(message);
    }

    @Override
    public void onSuccessfulWebsocketConnection() {
        sendNotification("Ready to analyze");
    }

    @Override
    public void onFailedConnection() {
        sendNotification("Failed to connect to the server, trying to reconnect");
    }

    @Override
    public void onMaxReconnectionAttemptsReached() {
        this.discovery.SendDiscovery();
    }

    @Override
    public void onActionReceived(Action action) {    }
}
