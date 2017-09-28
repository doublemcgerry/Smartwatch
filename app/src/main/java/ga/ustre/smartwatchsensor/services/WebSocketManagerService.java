package ga.ustre.smartwatchsensor.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonParseException;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketListener;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;

import java.io.FileDescriptor;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.UUID;

import ga.ustre.smartwatchsensor.WSClient;
import ga.ustre.smartwatchsensor.interfaces.WebSocketClientCallback;
import ga.ustre.smartwatchsensor.interfaces.WebSocketServerBinder;
import rz.thesis.server.serialization.StringSerializer;
import rz.thesis.server.serialization.action.Action;
import rz.thesis.server.serialization.action.sensors.SensorDataSendAction;

/**
 * Created by lollo on 07/06/2017.
 */

public class WebSocketManagerService extends Service implements WebSocketListener {

    private URI uri;
    private WSClient client;
    private WebSocketClientCallback callbacks;
    private String clientId;
    private UUID ACTIONID;

    private int reconnectionAttempts=0;
    private int maxReconnectionAttempts =100;


    private class WebSocketManagerServiceBinder extends Binder implements WebSocketServerBinder{

        private Intent intent;

        public  WebSocketManagerServiceBinder(Intent intent){
            this.intent = intent;
        }

        @Override
        public void connect(String clientID, URI uri) {
            WebSocketManagerService.this.connect(clientID,uri);
        }

        @Override
        public void sendAction(Action action) {
            WebSocketManagerService.this.sendAction(action);
        }

        @Override
        public void sendMovement(SensorDataSendAction action) {
            WebSocketManagerService.this.sendMovement(action);
        }

        @Override
        public void addCallback(WebSocketClientCallback callback) {
            WebSocketManagerService.this.addCallback(callback);
        }

        @Override
        public void removeCallback() {
            WebSocketManagerService.this.removeCallback();
        }

        @Override
        public void disconnect() throws InterruptedException {
            WebSocketManagerService.this.disconnect();
        }

        @Override
        public void reconnect() {
            WebSocketManagerService.this.reconnect();
        }

        @Override
        public String getClientId() {
            return WebSocketManagerService.this.getClientId();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.ACTIONID = UUID.fromString(intent.getStringExtra("ACTIONID"));
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        this.ACTIONID = UUID.fromString(intent.getStringExtra("ACTIONID"));
        return new WebSocketManagerServiceBinder(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void connect(String clientID, URI uri) {
        this.clientId=clientID;
        this.uri = uri;
        if (this.client == null) {
            this.client = new WSClient(this.uri, clientId, this);
            this.client.connect();
        }
    }

    private void disconnect() throws InterruptedException {
        if (this.client != null) {
            this.client.closeBlocking();
        }
    }

    private String getClientId(){
        return this.clientId;
    }

    private void reconnect() {
        try {
            if (reconnectionAttempts>maxReconnectionAttempts){
                callbacks.onMaxReconnectionAttemptsReached();
                return;
            }else{
                reconnectionAttempts++;
            }

            Thread.sleep(2000);

            this.client = null;
            this.client = new WSClient(this.uri, clientId, this);
            this.client.connect();
        } catch (InterruptedException e) {

        }
    }

    private void sendAction(Action action) {
        if (this.client!=null){
            try{
                action.setSource(ACTIONID);
                this.client.sendAction(action);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void sendMovement(SensorDataSendAction action) {
        if (this.client!=null){
            try{
                this.client.sendAction(action);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void addCallback(WebSocketClientCallback callback) {
        this.callbacks = callback;
    }

    private void removeCallback() {
        this.callbacks = null;
    }

    private String getInterfaceDescriptor() throws RemoteException {
        return null;
    }

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
        return null;
    }

    @Override
    public void onWebsocketHandshakeReceivedAsClient(WebSocket conn, ClientHandshake request, ServerHandshake response) throws InvalidDataException {

    }

    @Override
    public void onWebsocketHandshakeSentAsClient(WebSocket conn, ClientHandshake request) throws InvalidDataException {

    }

    @Override
    public void onWebsocketMessage(WebSocket conn, String message) {
        try{
            Action action= StringSerializer.getSerializer().fromJson(message,Action.class);
            if (action!=null && action.getDestination().equals(ACTIONID)) {
                callbacks.onActionReceived(action);
            }
        }catch (JsonParseException ex){
            Log.d("Galileo","Unknown action received");
        }
    }

    @Override
    public void onWebsocketMessage(WebSocket conn, ByteBuffer blob) {

    }

    @Override
    public void onWebsocketMessageFragment(WebSocket conn, Framedata frame) {

    }

    @Override
    public void onWebsocketOpen(WebSocket conn, Handshakedata d) {
        reconnectionAttempts=0;
        callbacks.onSuccessfulWebsocketConnection();
    }

    @Override
    public void onWebsocketClose(WebSocket ws, int code, String reason, boolean remote) {
        if (remote){
            if (ws != null) {
                ws.close();
            }
            reconnect();
        }
    }

    @Override
    public void onWebsocketClosing(WebSocket ws, int code, String reason, boolean remote) {

    }

    @Override
    public void onWebsocketCloseInitiated(WebSocket ws, int code, String reason) {

    }

    @Override
    public void onWebsocketError(WebSocket conn, Exception ex) {
        if (conn != null) {
            conn.close();
        }
        callbacks.onFailedConnection();
        reconnect();
    }

    @Override
    public void onWebsocketPing(WebSocket conn, Framedata f) {

    }

    @Override
    public void onWebsocketPong(WebSocket conn, Framedata f) {

    }

    @Override
    public String getFlashPolicy(WebSocket conn) throws InvalidDataException {
        return null;
    }

    @Override
    public void onWriteDemand(WebSocket conn) {

    }

    @Override
    public InetSocketAddress getLocalSocketAddress(WebSocket conn) {
        return null;
    }

    @Override
    public InetSocketAddress getRemoteSocketAddress(WebSocket conn) {
        return null;
    }
}
