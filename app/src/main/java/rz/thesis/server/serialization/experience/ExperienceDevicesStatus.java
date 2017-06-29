package rz.thesis.server.serialization.experience;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import utility.SensorType;


//TODO maximum numbers check
public class ExperienceDevicesStatus {

	private List<UUID> screens = new ArrayList<>();
	private int maxScreens = 1;
	private Map<SensorType, List<UUID>> sensors = new HashMap<>();
	private Map<SensorType, Integer> neededSensors;

	public int getMaxScreens() {
		return maxScreens;
	}

	public void setMaxScreens(int maxScreens) {
		this.maxScreens = maxScreens;
	}

	public Map<SensorType, List<UUID>> getSensors() {
		return sensors;
	}

	public Map<SensorType, Integer> getNeededSensors() {
		return neededSensors;
	}
}
