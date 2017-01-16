package ga.ustre.smartwatchsensor;

import java.io.Serializable;

/**
 * Created by achelius on 21/12/2016.
 */

public class MessageContainer implements Serializable {

    final int type;
    final int accuracy;
    final long timestamp;
    final float[] values;
    public MessageContainer(int type, int accuracy, long timestamp, float[] values) {
        this.type=type;
        this.accuracy=accuracy;
        this.timestamp=timestamp;
        this.values=values;
    }
}
