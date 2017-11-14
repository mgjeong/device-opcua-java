package org.edgexfoundry.device.opcua.sample;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edgexfoundry.controller.AddressableClient;
import org.edgexfoundry.controller.CmdClient;
import org.edgexfoundry.controller.DeviceClient;
import org.edgexfoundry.controller.DeviceProfileClient;
import org.edgexfoundry.controller.EventClient;
import org.edgexfoundry.controller.ValueDescriptorClient;
import org.edgexfoundry.device.opcua.data.DeviceStore;
import org.edgexfoundry.device.opcua.data.ProfileStore;
import org.edgexfoundry.domain.Command;
import org.edgexfoundry.domain.CommandDevice;
import org.edgexfoundry.domain.common.ValueDescriptor;
import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.core.Reading;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.DeviceProfile;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DummyTest {
	protected final static EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(DummyTest.class);

	@Autowired
	private DeviceClient deviceClient;

	@Autowired
	private DeviceProfileClient deviceProfileClient;

	@Autowired
	private AddressableClient addressableClient;

	@Autowired
	private ValueDescriptorClient valueDescriptorClient;

	@Autowired
	public EventClient eventClient;

	@Autowired
	protected DummyDevice dummyDevice;

	@Autowired
	protected CmdClient cmdClient;

	@Autowired
	DeviceStore devices;

	@Autowired
	private ProfileStore profiles;

	private static DummyAddressable dummyAddressable;
	protected static DummyValueDescriptor dummyValueDescriptor;

	private static DummyProfile dummyProfile;
	private static DeviceProfile profile;
	private static Device device;

	public DummyTest() {

		if (dummyAddressable == null) {
			dummyAddressable = new DummyAddressable();

		}
		if (dummyProfile == null) {
			dummyProfile = new DummyProfile();
		}
		if (profile == null) {
			profile = dummyProfile.getDummy();
		}
	}

	DeviceProfile getDummyProfile() {
		if (profile == null) {
			profile = dummyProfile.getDummy();
		}
		return profile;
	}

	DeviceProfile getDummyProfileFromMetaData() {
		if (profile == null) {
			profile = dummyProfile.getDummy();
		}
		return deviceProfileClient.deviceProfileForName(profile.getName());
	}

	DeviceProfile addDummyProfileToMetaData() {
		if (profile == null) {
			profile = dummyProfile.getDummy();
		}

		// push dummy profile to metadata db.
		try {
			logger.debug("Add dummy device profile successfully msg: " + deviceProfileClient.add(profile));
			profile = deviceProfileClient.deviceProfileForName(profile.getName());
		} catch (Exception e) {
			logger.error("Could not add dummy profile to metadata msg: " + e.getMessage());
			return null;
		}

		return profile;
	}

	void checkDummyProfileFromMetaData() {
		if (profile == null) {
			profile = dummyProfile.getDummy();
		}

		DeviceProfile metaProfile = deviceProfileClient.deviceProfileForName(profile.getName());

		dummyProfile.checkDummyProfileFromMetaData(metaProfile);
	}

	public Device getDummyDevice() {
		return dummyDevice.getDummy();
	}

	public Device getDummyDeviceFromMetaData() {
		try {
			return deviceClient.deviceForName(device.getName());
		} catch (Exception e) {
			logger.debug("Could not get device for name msg: " + e.getMessage());
			return null;
		}
	}

	void checkDummyDeviceFromMetaData() {
		Device metaDevice = deviceClient.deviceForName(device.getName());
		dummyDevice.checkDummyDeviceFromMetaData(metaDevice);
	}

	Device addDummyDeviceToMetaData() {
		if (device == null) {
			device = dummyDevice.getDummy();
		}

		try {
			logger.debug("Add dummy device successfully msg: " + deviceClient.add(device));
			device = deviceClient.deviceForName(device.getName());
			devices.add(device);
			profiles.addDevice(device);
		} catch (Exception e) {
			logger.error("Could not add dummy device to metadata msg: " + e.getMessage());
			return null;
		}

		return device;
	}

	ValueDescriptor addDummyValueDescriptorToCoreData() {
		if (dummyValueDescriptor == null) {
			dummyValueDescriptor = new DummyValueDescriptor();
		}

		ValueDescriptor valueDesp = dummyValueDescriptor.getDummy();

		try {
			logger.debug("Add dummy vauleDescriptor successfully msg: " + valueDescriptorClient.add(valueDesp));
		} catch (Exception e) {
			logger.error("Could not add dummy vauleDescriptor to coredata msg: " + e.getMessage());
		}

		return valueDesp;
	}

	String addDummyEventToCoreData() {
		DummyEvent dummyEvent = new DummyEvent();
		Event event = null;
		if (null != device) {
			event = dummyEvent.getDummyEvent(device.getName());
		} else {
			event = dummyEvent.getDummyEvent(dummyDevice.getDummy().getName());
		}

		String ret = new String("");
		try {
			ret = eventClient.add(event);
			logger.debug("Add dummy Event successfully msg: " + ret);
			return ret;
		} catch (Exception e) {
			logger.error("Could not add dummy Event to coredata msg: " + e.getMessage());
		}

		return ret;
	}

	void addDummyEventsToCoreData() {

		String ret = new String("");
		for (int i = 0; i < 5; i++) {
			try {
				DummyEvent dummyEvent = new DummyEvent();
				Event event = null;
				if (null != device) {
					event = dummyEvent.getDummyEvent(device.getName());
				} else {
					event = dummyEvent.getDummyEvent(dummyDevice.getDummy().getName());
				}

				ret = eventClient.add(event);
				logger.debug("Add dummy Event successfully msg: " + ret);
			} catch (Exception e) {
				logger.error("Could not add dummy Event to coredata msg: " + e.getMessage());
			}
		}
	}

	Event getDummyEventFromCoreDataById(String id) {
		try {
			Event event = eventClient.event(id);
			logger.debug("get dummy Event successfully msg: " + event);
			return event;
		} catch (Exception e) {
			logger.error("Could not get dummy Event to coredata msg: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public List<Event> getDummyEventFromCoreDataByDeviceId(String deviceId) {
		try {
			List<Event> events = eventClient.eventsForDevice(deviceId, 5);
			logger.debug("get dummy Events successfully msg: " + events);
			return events;
		} catch (Exception e) {
			logger.error("Could not get dummy Event to coredata msg: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	List<CommandDevice> getCommandDevices() {
		try {
			List<CommandDevice> cmdDevices = cmdClient.devices();
			logger.debug("CommandDevice from Metadata " + cmdDevices);
			return cmdDevices;
		} catch (Exception e) {
			logger.error("Could not get CommandDevices from metadata");
			e.printStackTrace();
			return null;
		}

	}

	void executeDummyGetCommand() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<CommandDevice> cmds = getCommandDevices();
		CommandDevice cmd = cmds.get(0);

		String ret = "error";
		try {
			ret = cmdClient.get(cmd.getId(), cmd.getCommands().get(0).getId());
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.debug("Result of command " + cmd.getName() + " is: " + ret);
	}

	void issueDummyGetCommand() {
		// wait for sync between core services
		new Thread(() -> executeDummyGetCommand()).start();
	}

	public void cleanUp() {
		try {
			logger.debug("remove dummy device from  metadata ret: "
					+ deviceClient.deleteByName(dummyDevice.getDummy().getName()));
		} catch (Exception e) {
			logger.debug("Could not remove dummy device profile from metadata msg: " + e.getMessage());
		}
		try {
			logger.debug("remove dummy addressable from  metadata ret: "
					+ addressableClient.deleteByName(dummyAddressable.getDummy().getName()));
		} catch (Exception e) {
			logger.debug("Could not remove dummy addressable from metadata msg: " + e.getMessage());
		}
		try {
			logger.debug("remove dummy profile from  metadata ret: "
					+ deviceProfileClient.deleteByName(dummyProfile.getDummy().getName()));
		} catch (Exception e) {
			logger.debug("Could not remove dummy profile from metadata msg: " + e.getMessage());
		}

		try {
			logger.debug("delete dummy vauleDescriptor successfully msg: "
					+ valueDescriptorClient.deleteByName(dummyValueDescriptor.getDummy().getName()));
		} catch (Exception e) {
			logger.error("Could not delete dummy vauleDescriptor to coredata msg: " + e.getMessage());
		}

		try {
			logger.debug("delete dummy event successfully msg: " + eventClient.delete(device.getName()));
		} catch (Exception e) {
			logger.error("Could not delete dummy event to coredata msg: " + e.getMessage());
		}
	}

	public void run() {
		addDummyProfileToMetaData();
		getDummyProfileFromMetaData();
		// TODO remove annotaion for check data
		// checkDummyProfileFromMetaData();

		addDummyDeviceToMetaData();
		Device deviceFromMeta = getDummyDeviceFromMetaData();
		// TODO remove annotaion for check data
		// checkDummyDeviceFromMetaData();

		addDummyValueDescriptorToCoreData();
		addDummyEventsToCoreData();
		getDummyEventFromCoreDataByDeviceId(deviceFromMeta.getName());

		issueDummyGetCommand();

		// put annotation for test with command service
		// cleanUp();
	}
}
