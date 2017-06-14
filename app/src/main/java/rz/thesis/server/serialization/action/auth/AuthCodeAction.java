package rz.thesis.server.serialization.action.auth;

import rz.thesis.server.serialization.action.management.ManagementAction;
import utility.ResultPresenter;

/**
 * Created by lollo on 06/06/2017.
 */

public class AuthCodeAction extends ManagementAction {
    private static final long serialVersionUID = 1353808040784186537L;

    private String code;

    public AuthCodeAction(String token) {
        this.code =token;
    }

    public String getCode() {
        return code;
    }

    @Override
    public void execute(ResultPresenter resultPresenter) {
        resultPresenter.publishMessage("Codice Ricevuto!");
    }
}
