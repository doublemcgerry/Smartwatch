package ga.ustre.smartwatchsensor.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import ga.ustre.smartwatchsensor.R;
import ga.ustre.smartwatchsensor.WebSocketClientManager;
import serialization.action.Action;
import serialization.action.auth.SendCodeAction;
import serialization.action.management.DeviceAnnounceAction;
import utility.RandomUtils;
import utility.ResultPresenter;
import utility.SensorType;

public class FirstActivity extends WearableActivity implements ResultPresenter, WebSocketClientManager.Callbacks {
    private static final String TAG = "galileo/main";

    private ProgressBar pb_searching;
    private TextView tv_progress;
    private WifiManager.WifiLock wifiLock;
    private PowerManager.WakeLock mWakeLock;
    private Handler mWakeLockHandler;
    private String clientId;
    private Button codeButton;
    private WebSocketClientManager client;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        setAmbientEnabled();
        pb_searching = (ProgressBar) findViewById(R.id.pb_first);
        tv_progress = (TextView) findViewById(R.id.tv_first);
        publishMessage("Connessione al server in corso");

        String deviceName= RandomUtils.getDeviceName();
        @SuppressLint("WifiManagerLeak") WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        manager.setWifiEnabled(true);
        wifiLock=manager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF,TAG);

        codeButton = (Button) findViewById(R.id.code_button);
        codeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCodeActivity();
            }
        });
        mWakeLockHandler = new Handler();

        String address = info.getMacAddress();
        clientId = deviceName + " " + address;
        client = new WebSocketClientManager(clientId, URI.create("ws://127.0.0.1:8010"), this);
        client.connect();
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
    public void showIcon(int drawable) {

    }

    @Override
    public void hideIcon() {

    }

    @Override
    public void triggerVibration() {

    }

    @Override
    public void onSuccessfulWebsocketConnection() {
        publishMessage("Connesso al server, In attesa del Codice");
        List<SensorType> sensorTypes = new ArrayList<SensorType>();
        sensorTypes.add(SensorType.HEARTRATE);
        sensorTypes.add(SensorType.MOTION);
        DeviceAnnounceAction action = new DeviceAnnounceAction(clientId,0,0,1,sensorTypes);
        client.sendAction(action);
    }

    @Override
    public void onFailedConnection() {
        publishMessage("Connessione con il server Fallita, nuovo tentativo...");
        client.connect();
    }

    @Override
    public void onMaxReconnectionAttemptsReached() {
        publishMessage("Raggiungto il numero massimo di tentativi, si prega di riavviare l'applicazione...");
    }

    @Override
    public void onActionReceived(Action action) {
        if( action instanceof SendCodeAction){
            SendCodeAction reply = (SendCodeAction) action;
            reply.execute(this);
            this.code = reply.getCode();
            hideProgressBar();
            this.codeButton.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(0, 10, 0, 0); // llp.setMargins(left, top, right, bottom);
            tv_progress.setLayoutParams(llp);
        }
    }

    private void goToCodeActivity(){
        Intent intent = new Intent(FirstActivity.this, CodeActivity.class);
        intent.putExtra("code",this.code);
        client.removeCallback();
        intent.putExtra("wsclient",client);
        startActivity(intent);
        finish();
    }
}
