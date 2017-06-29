package rz.thesis.server.serialization.action.lobby;

import ga.ustre.smartwatchsensor.interfaces.WebSocketServerBinder;
import utility.ActionExecutor;
import utility.SensorType;

public class BindSensorSlotAction extends LobbyAction {
	private static final long serialVersionUID = 1212827102979125738L;

	private SensorType type;

	public BindSensorSlotAction(SensorType type){
		this.type = type;
	}

	@Override
	public void execute(ActionExecutor actionExecutor, WebSocketServerBinder client) {

	}
}
