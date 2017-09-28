package ga.ustre.smartwatchsensor.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ga.ustre.smartwatchsensor.R;
import ga.ustre.smartwatchsensor.interfaces.WebSocketClientCallback;
import ga.ustre.smartwatchsensor.interfaces.WebSocketServerBinder;
import ga.ustre.smartwatchsensor.services.WebSocketManagerService;
import rz.thesis.server.serialization.action.Action;
import rz.thesis.server.serialization.action.auth.AnnounceDemandAction;
import rz.thesis.server.serialization.action.auth.ConnectAction;
import rz.thesis.server.serialization.action.auth.AuthCodeAction;
import rz.thesis.server.serialization.action.lobby.SuccessfulConnectionEvent;
import rz.thesis.server.serialization.action.management.DeviceAnnounceAction;
import utility.Parameters;
import utility.RandomUtils;
import utility.ActionExecutor;
import utility.SensorType;

public class FirstActivity extends WearableActivity implements ActionExecutor, WebSocketClientCallback{
    private static final String TAG = "galileo/main";

    private ProgressBar pb_searching;
    private TextView tv_progress;
    private String clientId;
    private Button codeButton;
    private WebSocketServerBinder client;
    private String code;
    private TextView firstChar;
    private TextView secondChar;
    private TextView thirdChar;
    private TextView fourthChar;
    private UUID actionID;
    private WifiManager.WifiLock wifiLock ;
    private PowerManager.WakeLock wakeLock;
    private String lobbyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        setAmbientEnabled();
        pb_searching = (ProgressBar) findViewById(R.id.pb_first);
        tv_progress = (TextView) findViewById(R.id.tv_first);
        publishMessage("Connessione al server in corso...");

        String deviceName= RandomUtils.getDeviceName();

        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        manager.setWifiEnabled(true);

        wifiLock=manager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF,TAG);
        wifiLock.acquire();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        codeButton = (Button) findViewById(R.id.code_button);
        codeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeContext(true);
            }
        });

        actionID = UUID.randomUUID();

        String address = info.getMacAddress();
        clientId = deviceName + " " + address;
        Intent intent = new Intent(this, WebSocketManagerService.class);
        intent.putExtra("ACTIONID",actionID.toString());
        bindService(intent, mConnection, 0);
        startService(intent);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            client = (WebSocketServerBinder) service;
            addCallbackToClient();

            client.connect(clientId, URI.create("ws://" +getIntent().getStringExtra(Parameters.IP_ADDRESS_PARAMETER) + ":8010/ws"));
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
    public void publishMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_progress.setText(message);
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
    public void saveCode(String code) {
        this.code = code;
    }

    @Override
    public void onSuccessfulWebsocketConnection() {
        publishMessage("Connesso al server, In attesa del Codice");
        client.sendAction(new ConnectAction());
    }

    @Override
    public void onFailedConnection() {
        publishMessage("Connessione con il server Fallita, nuovo tentativo...");
        client.connect(clientId, URI.create("ws://192.168.1.21:8010/ws"));
    }

    @Override
    public void onMaxReconnectionAttemptsReached() {
        publishMessage("Raggiungto il numero massimo di tentativi, si prega di riavviare l'applicazione...");
    }

    @Override
    public void onActionReceived(Action action) {
        action.execute(this,client);
        if(action instanceof SuccessfulConnectionEvent){
            SuccessfulConnectionEvent reply = (SuccessfulConnectionEvent) action;
            startNewActivity(reply.getLobby());
        }
    }

    private void startNewActivity(String lobbyID){
        this.lobbyID = lobbyID;
        client.removeCallback();
        unbindService(mConnection);
        Intent intent = new Intent(FirstActivity.this,MainActivity.class);
        intent.putExtra("clientId",clientId);
        intent.putExtra("ACTIONID",actionID.toString());
        intent.putExtra("LOBBY",lobbyID);
        FirstActivity.this.startActivity(intent);
        wifiLock.release();
        wakeLock.release();
        finish();
    }

    @Override
    public void changeContext(boolean firstContext){
        firstChar = (TextView) findViewById(R.id.first_char);
        secondChar = (TextView) findViewById(R.id.second_char);
        thirdChar = (TextView) findViewById(R.id.third_char);
        fourthChar = (TextView) findViewById(R.id.fourth_char);
        if(code.length() == 4){
            populateCode();
        }
        changeLayout();
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
                codeButton.setVisibility(View.VISIBLE);
                tv_progress.setVisibility(View.GONE);
            }
        });
    }
}
