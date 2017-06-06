package rz.thesis.server.serialization.action.management;


import utility.ResultPresenter;

public class SmartwatchEnterLobbyAction extends ManagementAction {
	private static final long serialVersionUID = -3018483622075602666L;
	private String lobby;
	
	public SmartwatchEnterLobbyAction(String lobby) {
		this.lobby=lobby;
	}

	@Override
	public void execute(ResultPresenter resultPresenter) {

	}
}
