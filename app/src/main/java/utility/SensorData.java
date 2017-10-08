package utility;

import java.util.Map;

/**
 * Created by achelius on 03/01/2017.
 */

public class SensorData {
    private long timestamp;
    private String type;
    private Map<String,String> data;

    public SensorData(long timestamp,String type,Map<String,String> data) {
        this.timestamp = timestamp;
        this.type=type;
        this.data=data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
