package serialization.action.sensors;


import utility.ResultPresenter;
import utility.SensorData;

public class SensorDataSendAction extends SensorsAction {

	private static final long serialVersionUID = 6442731397402415499L;
	private SensorData data;
    private String sender;

    public SensorDataSendAction(String sender, SensorData data) {
        super();
        this.data=data;
        this.sender=sender;
    }

    @Override
    public void execute(ResultPresenter resultPresenter) {

    }
}
