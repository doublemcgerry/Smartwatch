package utility;

/**
 * Created by achelius on 03/01/2017.
 */

public class SensorData {
    private long timestamp;
    private MovementType typeMovement;

    public SensorData(long timestamp, MovementType typeMovement) {
        this.timestamp = timestamp;
        this.typeMovement = typeMovement;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public MovementType getTypeMovement() {
        return typeMovement;
    }

    public void setTypeMovement(MovementType typeMovement) {
        this.typeMovement = typeMovement;
    }
}
