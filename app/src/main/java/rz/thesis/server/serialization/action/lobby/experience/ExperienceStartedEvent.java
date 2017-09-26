package rz.thesis.server.serialization.action.lobby.experience;

import ga.ustre.smartwatchsensor.interfaces.WebSocketServerBinder;
import rz.thesis.server.serialization.action.lobby.LobbyAction;
import rz.thesis.server.serialization.experience.ExperienceDevicesStatus;
import utility.ActionExecutor;

/**
 * Created by lollo on 26/09/2017.
 */

public class ExperienceStartedEvent extends LobbyAction {
    private static final long serialVersionUID = 7684472324843580729L;

    @Override
    public void execute(ActionExecutor actionExecutor, WebSocketServerBinder client) {
        actionExecutor.publishMessage("Esperienza Iniziata");
        actionExecutor.hideProgressBar();
        actionExecutor.changeContext(true);
    }
}
