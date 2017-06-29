package rz.thesis.server.serialization.action.lobby;


import ga.ustre.smartwatchsensor.interfaces.WebSocketServerBinder;
import utility.ActionExecutor;

public class SuccessfulConnectionEvent extends LobbyAction {
	private static final long serialVersionUID = 8229417269965927669L;

	private String lobby;

	public SuccessfulConnectionEvent(String lobby) {
		this.lobby = lobby;
	}

	@Override
	public void execute(ActionExecutor actionExecutor, WebSocketServerBinder client) {
        actionExecutor.publishMessage("In attesa dell'Esperienza");
	}

	public String getLobby() {
		return lobby;
	}
}
