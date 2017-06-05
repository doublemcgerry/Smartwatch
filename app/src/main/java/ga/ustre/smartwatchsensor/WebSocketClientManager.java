package ga.ustre.smartwatchsensor;

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

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;

import serialization.StringSerializer;
import serialization.action.Action;
import serialization.action.sensors.SensorDataSendAction;

/**
 * Created by achelius on 22/12/2016.
 */

public class WebSocketClientManager implements WebSocketListener {

    public interface Callbacks{
        void onSuccessfulWebsocketConnection();
        void onFailedConnection();
        void onMaxReconnectionAttemptsReached();
        void onActionReceived(final Action action);
    }
    private URI uri;
    private WSClient client;
    private Callbacks callbacks;
    private String clientId;


    private int reconnectionAttempts=0;

    private int maxReconnectionAttempts =-1;

    public WebSocketClientManager(String clientId, URI uri, Callbacks callbacks)
    {
        this.clientId=clientId;
        this.uri = uri;
        this.callbacks= callbacks;
    }

    public void connect() {
        if (this.client == null) {
            this.client = new WSClient(this.uri, clientId, this);
            this.client.connect();
        }
    }
    public void disconnect() throws InterruptedException {
        if (this.client != null) {
            this.client.closeBlocking();
        }
    }

    @Override
    public void onWebsocketMessage(WebSocket conn, String message) {
        try{
            Action action= StringSerializer.getSerializer().fromJson(message,Action.class);
            if (action!=null) {
                callbacks.onActionReceived(action);
            }
        }catch (JsonParseException ex){
            Log.d("Galileo","Unknown action received");
        }

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
    public void onWebsocketError(WebSocket conn, Exception ex) {
        if (conn != null) {
            conn.close();
        }
        callbacks.onFailedConnection();
        reconnect();
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

    public void sendMovement(SensorDataSendAction action) {
        if (this.client!=null){
            try{
                this.client.sendAction(action);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void sendAction(Action action) {
        if (this.client!=null){
            try{
                this.client.sendAction(action);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
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
    public void onWebsocketMessage(WebSocket conn, ByteBuffer blob) {

    }

    @Override
    public void onWebsocketMessageFragment(WebSocket conn, Framedata frame) {

    }


    @Override
    public void onWebsocketClosing(WebSocket ws, int code, String reason, boolean remote) {

    }

    @Override
    public void onWebsocketCloseInitiated(WebSocket ws, int code, String reason) {

    }


    @Override
    public void onWebsocketPing(WebSocket conn, Framedata f) {

    }

    @Override
    public void onWebsocketPong(WebSocket conn, Framedata f) {

    }

    @Override
    public String getFlashPolicy(WebSocket conn) {
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
