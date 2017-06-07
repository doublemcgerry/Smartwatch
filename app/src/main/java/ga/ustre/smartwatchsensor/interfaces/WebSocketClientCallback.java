package ga.ustre.smartwatchsensor.interfaces;

import android.os.IBinder;

import rz.thesis.server.serialization.action.Action;

/**
 * Created by lollo on 07/06/2017.
 */

public interface WebSocketClientCallback {
    void onSuccessfulWebsocketConnection();
    void onFailedConnection();
    void onMaxReconnectionAttemptsReached();
    void onActionReceived(final Action action);
}
