package rz.thesis.server.serialization.action.sensors;


import ga.ustre.smartwatchsensor.interfaces.WebSocketServerBinder;
import utility.ActionExecutor;
import utility.SensorData;

public class SensorDataSendAction extends SensorsAction {

	private static final long serialVersionUID = 6442731397402415499L;
	private SensorData data;

    public SensorDataSendAction( SensorData data) {
        super();
        this.data=data;
    }

    @Override
    public void execute(ActionExecutor actionExecutor, WebSocketServerBinder client) {

    }
}
