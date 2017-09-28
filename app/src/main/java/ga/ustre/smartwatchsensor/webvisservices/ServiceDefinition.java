package ga.ustre.smartwatchsensor.webvisservices;

import java.io.Serializable;

/**
 * Created by achelius on 08/04/17.
 */

public class ServiceDefinition implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -6241006753677586790L;

    private int port;
    private String description;

    public ServiceDefinition(String description,int port) {
        this.port=port;
        this.description=description;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
