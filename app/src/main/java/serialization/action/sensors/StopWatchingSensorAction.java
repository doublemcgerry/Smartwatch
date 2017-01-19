package serialization.action.sensors;

import ga.ustre.smartwatchsensor.R;
import serialization.action.Action;
import utility.ResultPresenter;

/**
 * Created by achelius on 03/01/2017.
 */

public class StopWatchingSensorAction extends Action {
    @Override
    public void execute(ResultPresenter resultPresenter) {
        resultPresenter.publishMessage("nun te move!");
        resultPresenter.showIcon(R.drawable.ic_connected);
    }
}
