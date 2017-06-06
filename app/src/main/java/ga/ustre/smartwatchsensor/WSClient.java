package ga.ustre.smartwatchsensor;

import org.java_websocket.WebSocketListener;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.Serializable;
import java.net.URI;

import rz.thesis.server.serialization.StringSerializer;
import rz.thesis.server.serialization.action.Action;

/**
 * Created by achelius on 21/12/2016.
 */

public class WSClient extends WebSocketClient implements Serializable {

    private WebSocketListener listener;
    private String clientId;
    public WSClient(URI serverURI,String clientId, WebSocketListener listener) {
        super(serverURI);
        this.clientId=clientId;
        this.listener = listener;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        this.listener.onWebsocketOpen(this.getConnection(),handshakedata);
    }

    @Override
    public void onMessage(String message) {
        this.listener.onWebsocketMessage(this.getConnection(),message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        this.listener.onWebsocketClose(this.getConnection(),code,reason,remote);
    }

    @Override
    public void onError(Exception ex) {
        this.listener.onWebsocketError(this.getConnection(),ex);
    }

    public void sendAction(Action action){
        this.send(StringSerializer.getSerializer().toJson(action, Action.class));
    }

}
