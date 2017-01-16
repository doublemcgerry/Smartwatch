package serialization.action.sensors;

import ga.ustre.smartwatchsensor.R;
import serialization.action.Action;
import utility.ResultPresenter;

/**
 * Created by achelius on 03/01/2017.
 */

public class StartWatchingSensorAction extends Action {

    @Override
    public void execute(ResultPresenter resultPresenter) {
        resultPresenter.showIcon(R.drawable.ic_hand);
    }
}
