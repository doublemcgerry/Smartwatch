package rz.thesis.server.serialization.action.auth;

import java.security.PublicKey;

import ga.ustre.smartwatchsensor.interfaces.WebSocketServerBinder;
import rz.thesis.server.serialization.action.management.ManagementAction;
import utility.ActionExecutor;

public class ReconnectAction extends ManagementAction {
	private static final long serialVersionUID = 1658463358559129074L;

	private String lobby;

    public ReconnectAction(String lobby){
        this.lobby =lobby;
    }

	@Override
	public void execute(ActionExecutor actionExecutor, WebSocketServerBinder client) {

	}
}
