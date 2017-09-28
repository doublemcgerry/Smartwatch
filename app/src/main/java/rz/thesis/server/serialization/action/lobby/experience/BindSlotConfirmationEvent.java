package rz.thesis.server.serialization.action.lobby.experience;

import ga.ustre.smartwatchsensor.interfaces.WebSocketServerBinder;
import rz.thesis.server.serialization.action.lobby.LobbyAction;
import utility.ActionExecutor;

/**
 * Created by lollo on 26/09/2017.
 */

public class BindSlotConfirmationEvent extends LobbyAction {
    @Override
    public void execute(ActionExecutor actionExecutor, WebSocketServerBinder client) {
        actionExecutor.publishMessage("Sei Stato Selezionato Per l'esperienza");
    }
}
