package rz.thesis.server.serialization.action;

import java.io.Serializable;
import java.util.UUID;

import ga.ustre.smartwatchsensor.interfaces.WebSocketServerBinder;
import utility.ActionExecutor;

public abstract class Action implements Serializable{
	private static final long serialVersionUID = 8603051382268444140L;

    protected UUID source;
    protected UUID destination;

	public abstract void execute(ActionExecutor actionExecutor, WebSocketServerBinder client);

    public UUID getSource() {
        return source;
    }

    public void setSource(UUID source) {
        this.source = source;
    }

    public UUID getDestination() {
        return destination;
    }

    public void setDestination(UUID destination) {
        this.destination = destination;
    }
}
