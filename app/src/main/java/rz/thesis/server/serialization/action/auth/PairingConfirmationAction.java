package rz.thesis.server.serialization.action.auth;

import rz.thesis.server.serialization.action.management.ManagementAction;
import utility.ResultPresenter;

/**
 * Created by lollo on 10/06/2017.
 */

public class PairingConfirmationAction extends ManagementAction {

    private static final long serialVersionUID = -1692216914762512270L;
    private String deviceName;
    private String userName;

    public PairingConfirmationAction(String deviceName, String userName) {
        this.deviceName = deviceName;
        this.userName = userName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public void execute(ResultPresenter resultPresenter) {

    }
}
