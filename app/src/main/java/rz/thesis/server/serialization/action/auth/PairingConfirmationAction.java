package rz.thesis.server.serialization.action.auth;

import ga.ustre.smartwatchsensor.interfaces.WebSocketServerBinder;
import rz.thesis.server.serialization.action.management.ManagementAction;
import utility.ActionExecutor;

/**
 * Created by lollo on 10/06/2017.
 */

public class PairingConfirmationAction extends ManagementAction {

    private static final long serialVersionUID = -1692216914762512270L;
    private String deviceName;
    private String userName;
    private String sessionId;

    public PairingConfirmationAction(String deviceName, String userName, String sessionId) {
        this.deviceName = deviceName;
        this.userName = userName;
        this.sessionId = sessionId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public void execute(ActionExecutor actionExecutor, WebSocketServerBinder client) {
        actionExecutor.publishMessage("Accoppiamento Completato");
    }
}
