package ga.ustre.smartwatchsensor.activities;

import android.annotation.SuppressLint;
import android.content.Context;
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
import rz.thesis.server.serialization.action.Action;
import rz.thesis.server.serialization.action.auth.SendCodeAction;
import rz.thesis.server.serialization.action.management.DeviceAnnounceAction;
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
    private TextView firstChar;
    private TextView secondChar;
    private TextView thirdChar;
    private TextView fourthChar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        setAmbientEnabled();
        pb_searching = (ProgressBar) findViewById(R.id.pb_first);
        tv_progress = (TextView) findViewById(R.id.tv_first);
        publishMessage("Connessione al server in corso...");

        String deviceName= RandomUtils.getDeviceName();
        @SuppressLint("WifiManagerLeak") WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        manager.setWifiEnabled(true);
        wifiLock=manager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF,TAG);

        codeButton = (Button) findViewById(R.id.code_button);
        codeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeContext();
            }
        });
        mWakeLockHandler = new Handler();

        String address = info.getMacAddress();
        clientId = deviceName + " " + address;
        client = new WebSocketClientManager(clientId, URI.create("ws://192.168.1.21:8010/ws"), this);
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
                pb_searching.setVisibility(View.GONE);
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
            setButtonVisibility();
            hideTextView();
        }
    }

    private void changeContext(){
        firstChar = (TextView) findViewById(R.id.first_char);
        secondChar = (TextView) findViewById(R.id.second_char);
        thirdChar = (TextView) findViewById(R.id.fourth_char);
        fourthChar = (TextView) findViewById(R.id.fourth_char);
        if(code.length() == 4){
            populateCode();
        }
        changeLayout();
    }

    private void setButtonVisibility(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                codeButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void populateCode(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                firstChar.setText(String.valueOf(code.charAt(0)));
                secondChar.setText(String.valueOf(code.charAt(1)));
                thirdChar.setText(String.valueOf(code.charAt(2)));
                fourthChar.setText(String.valueOf(code.charAt(3)));
            }
        });
    }

    private void hideTextView(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_progress.setVisibility(View.GONE);
            }
        });
    }

    private void changeLayout(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout layoutToHide = (LinearLayout) findViewById(R.id.loading_layout);
                layoutToHide.setVisibility(View.GONE);
                LinearLayout layoutToShow = (LinearLayout) findViewById(R.id.code_layout);
                layoutToShow.setVisibility(View.VISIBLE);
            }
        });
    }
}
