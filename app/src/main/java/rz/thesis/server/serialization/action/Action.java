package rz.thesis.server.serialization.action;

import java.io.Serializable;

import utility.ResultPresenter;

public abstract class Action implements Serializable{
	private static final long serialVersionUID = 8603051382268444140L;
	public abstract void execute(ResultPresenter resultPresenter);
}
