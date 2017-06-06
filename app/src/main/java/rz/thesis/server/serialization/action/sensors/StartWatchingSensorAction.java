package rz.thesis.server.serialization.action.sensors;

import ga.ustre.smartwatchsensor.R;
import rz.thesis.server.serialization.action.Action;
import utility.ResultPresenter;

/**
 * Created by achelius on 03/01/2017.
 */

public class StartWatchingSensorAction extends Action {

    @Override
    public void execute(ResultPresenter resultPresenter) {
        resultPresenter.publishMessage("Movete!");
        resultPresenter.showIcon(R.drawable.ic_hand);
        resultPresenter.triggerVibration();
    }
}
