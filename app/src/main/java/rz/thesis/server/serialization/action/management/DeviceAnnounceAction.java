package rz.thesis.server.serialization.action.management;

import java.util.List;

import utility.ResultPresenter;
import utility.SensorType;

/**
 * Created by lollo on 06/06/2017.
 */

public class DeviceAnnounceAction extends ActorAnnounceAction {

    private static final long serialVersionUID = -3018483622075602666L;

    private List<SensorType> sensorTypes;

    public DeviceAnnounceAction(String name, int major, int minor, int revision, List<SensorType> sensorTypes){
        super(name,major,minor,revision);
        this.sensorTypes = sensorTypes;
    }

    @Override
    public void execute(ResultPresenter resultPresenter) {

    }
}