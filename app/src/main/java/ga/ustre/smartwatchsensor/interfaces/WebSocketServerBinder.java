package ga.ustre.smartwatchsensor.interfaces;

import android.os.IBinder;

import java.net.URI;

import rz.thesis.server.serialization.action.Action;
import rz.thesis.server.serialization.action.sensors.SensorDataSendAction;

/**
 * Created by lollo on 07/06/2017.
 */

public interface WebSocketServerBinder{
    void connect(String clientID, URI uri);
    void sendAction(final Action action);
    void addCallback(WebSocketClientCallback callback);
    void removeCallback();
    void disconnect() throws InterruptedException;
    void reconnect();
    String getClientId();
}
