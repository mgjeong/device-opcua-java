package org.edgexfoundry.device.opcua.sample;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.edgexfoundry.controller.AddressableClient;
import org.edgexfoundry.controller.DeviceServiceClient;
import org.edgexfoundry.domain.meta.AdminState;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.OperatingState;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

@Component
public class DummyDevice {

	private final static EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(DummyDevice.class);

	@Autowired
	private DeviceServiceClient deviceServiceClient;

	@Autowired
	private AddressableClient addressableClient;

	private DummyProfile dummyProfile = new DummyProfile();
	private static DummyAddressable dummyAddr = new DummyAddressable();

	static final String DUMMY_SERVICE = "device-service-opcua";
	static final String DUMMY_NAME = "DummyDevice";
	static final String DUMMY_DESCRIPTION = "This is dummy device";
	static final AdminState DUMMY_ADMIN = AdminState.unlocked;
	static final OperatingState DUMMY_OP = OperatingState.enabled;
	static final long DUMMY_LAST_CONNECTED = 1000000;
	static final long DUMMY_LAST_REPORTED = 2000000;
	static final String[] DUMMY_LABELS = { "OPCUA", "DUMMY" };
	static final String DUMMY_LOCATION = "{40lat;45long}";
	static final long DUMMY_ORIGIN = 123456789;

	private static Device dummy;
	// private static Device dummy2;

	public DummyDevice() {
		dummyProfile = new DummyProfile();
		dummyAddr = new DummyAddressable();
	}

	Device getDummy() {
		if (dummy == null) {
			dummy = new Device();
			dummy.setAdminState(DUMMY_ADMIN);
			dummy.setDescription(DUMMY_DESCRIPTION);
			dummy.setLabels(DUMMY_LABELS);
			dummy.setLastConnected(DUMMY_LAST_CONNECTED);
			dummy.setLastReported(DUMMY_LAST_REPORTED);
			dummy.setLocation(DUMMY_LOCATION);
			dummy.setName(DUMMY_NAME);
			dummy.setOperatingState(DUMMY_OP);
			dummy.setOrigin(DUMMY_ORIGIN);
			dummy.setProfile(dummyProfile.getDummy());

			try {
				addressableClient.add(dummyAddr.getDummy());
			} catch (Exception e) {
				logger.debug("Could not set dummy addressable to metadata msg: " + e.getMessage());
			}

			try {
				dummy.setAddressable(addressableClient.addressableForName(dummyAddr.getDummy().getName()));
			} catch (Exception e) {
				logger.debug("Could not set Addressable for dummy device msg: " + e.getMessage());
				return null;
			}
			try {
				dummy.setService(deviceServiceClient.deviceServiceForName(DUMMY_SERVICE));
			} catch (Exception e) {
				logger.error("Could not get deviceService by name msg: " + e.getMessage());
				e.printStackTrace();
				return null;
			}
		}
		return dummy;
	}

	/*
	 * Device getDummy2(String name) { if (dummy2 == null) { dummy2 = new
	 * Device(); dummy2.setAdminState(DUMMY_ADMIN);
	 * dummy2.setDescription(DUMMY_DESCRIPTION); dummy2.setLabels(DUMMY_LABELS);
	 * dummy2.setLastConnected(DUMMY_LAST_CONNECTED);
	 * dummy2.setLastReported(DUMMY_LAST_REPORTED);
	 * dummy2.setLocation(DUMMY_LOCATION); dummy2.setName(name);
	 * dummy2.setOperatingState(DUMMY_OP); dummy2.setOrigin(DUMMY_ORIGIN);
	 * dummy2.setProfile(dummyProfile.getDummy());
	 * 
	 * try { addressableClient.add(dummyAddr.getDummy2()); } catch (Exception e)
	 * { logger.debug("Could not set dummy addressable to metadata msg: " +
	 * e.getMessage()); }
	 * 
	 * try {
	 * dummy2.setAddressable(addressableClient.addressableForName(dummyAddr.
	 * getDummy2().getName())); } catch (Exception e) {
	 * logger.debug("Could not set Addressable for dummy device msg: " +
	 * e.getMessage()); // return null; } try {
	 * dummy2.setService(deviceServiceClient.deviceServiceForName(serviceName));
	 * } catch (Exception e) {
	 * logger.error("Could not get deviceService by name msg: " +
	 * e.getMessage()); e.printStackTrace(); // return null; } } return dummy2;
	 * }
	 */

	void checkDummyDeviceFromMetaData(Device metaDevice) {
		assertEquals("Device name does not match expected", metaDevice.getName(), dummy.getName());
		assertEquals("Device origin does not match expected", metaDevice.getOrigin(), dummy.getOrigin());
		assertEquals("Device addressable does not match expected", metaDevice.getName(), dummy.getName());
		assertEquals("Device admin state does not match expected", metaDevice.getAdminState(), dummy.getAdminState());
		assertEquals("Device description does not match expected", metaDevice.getDescription(), dummy.getDescription());
		assertArrayEquals("Device labels does not match expected", metaDevice.getLabels(), dummy.getLabels());
		assertEquals("Device last connected does not match expected", metaDevice.getLastConnected(),
				dummy.getLastConnected());
		assertEquals("Device last reported does not match expected", metaDevice.getLastReported(),
				dummy.getLastReported());
		assertEquals("Device location does not match expected", metaDevice.getLocation(), dummy.getLocation());
		assertEquals("Device operating state does not match expected", metaDevice.getOperatingState(),
				dummy.getOperatingState());
		assertEquals("Device profile does not match expected", metaDevice.getProfile().getName(),
				dummy.getProfile().getName());
		assertEquals("Device service does not match expected", metaDevice.getService().getName(),
				dummy.getService().getName());
	}
}
