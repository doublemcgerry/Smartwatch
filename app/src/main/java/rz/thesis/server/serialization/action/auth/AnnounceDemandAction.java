package rz.thesis.server.serialization.action.auth;

import java.util.ArrayList;
import java.util.List;

import ga.ustre.smartwatchsensor.interfaces.WebSocketServerBinder;
import rz.thesis.server.serialization.action.management.DeviceAnnounceAction;
import rz.thesis.server.serialization.action.management.ManagementAction;
import utility.ActionExecutor;
import utility.SensorType;

/**
 * Created by lollo on 29/06/2017.
 */

public class AnnounceDemandAction extends ManagementAction {
    @Override
    public void execute(ActionExecutor actionExecutor, WebSocketServerBinder client) {
        List<SensorType> sensorTypes = new ArrayList<>();
        sensorTypes.add(SensorType.HEARTRATE);
        sensorTypes.add(SensorType.MOTION);
        DeviceAnnounceAction reply = new DeviceAnnounceAction(client.getClientId(),0,0,1,sensorTypes);
        client.sendAction(reply);
    }
}
