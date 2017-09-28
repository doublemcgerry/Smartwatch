package ga.ustre.smartwatchsensor.webvisservices;

import java.io.Serializable;
import java.util.List;


/**
 * Created by achelius on 08/04/17.
 */

public class DiscoveryServicesDefinitions implements Serializable {
    private static final long serialVersionUID = -417619365902581094L;
    private List<ServiceDefinition> services;
    public DiscoveryServicesDefinitions(List<ServiceDefinition> services) {
        this.setServices(services);
    }
    public List<ServiceDefinition> getServices() {
        return services;
    }
    public void setServices(List<ServiceDefinition> services) {
        this.services = services;
    }

    public ServiceDefinition getServiceByKey(String key){
        for (ServiceDefinition service:services) {
            if(service.getDescription().equals(key)){
                return service;
            }
        }
        return null;
    }

}
