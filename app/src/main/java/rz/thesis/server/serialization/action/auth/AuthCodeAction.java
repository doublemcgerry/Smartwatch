package rz.thesis.server.serialization.action.auth;

import ga.ustre.smartwatchsensor.interfaces.WebSocketServerBinder;
import rz.thesis.server.serialization.action.management.ManagementAction;
import utility.ActionExecutor;

/**
 * Created by lollo on 06/06/2017.
 */

public class AuthCodeAction extends ManagementAction {
    private static final long serialVersionUID = 1353808040784186537L;

    private String code;

    public AuthCodeAction(String token) {
        this.code =token;
    }

    public String getCode() {
        return code;
    }

    @Override
    public void execute(ActionExecutor actionExecutor, WebSocketServerBinder client) {
        actionExecutor.saveCode(this.code);
        actionExecutor.publishMessage("Codice Ricevuto!");
        actionExecutor.hideProgressBar();
    }
}
