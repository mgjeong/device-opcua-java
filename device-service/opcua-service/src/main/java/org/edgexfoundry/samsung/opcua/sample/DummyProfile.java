package org.edgexfoundry.samsung.opcua.sample;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.edgexfoundry.domain.meta.Command;
import org.edgexfoundry.domain.meta.DeviceObject;
import org.edgexfoundry.domain.meta.DeviceProfile;
import org.edgexfoundry.domain.meta.Get;
import org.edgexfoundry.domain.meta.ProfileProperty;
import org.edgexfoundry.domain.meta.ProfileResource;
import org.edgexfoundry.domain.meta.PropertyValue;
import org.edgexfoundry.domain.meta.Put;
import org.edgexfoundry.domain.meta.ResourceOperation;
import org.edgexfoundry.domain.meta.Response;
import org.edgexfoundry.domain.meta.Units;

public class DummyProfile {
	final String COMMAND_PREFIX = "/api/v1/device/{deviceId}/";
	final String DUMMY_PROFILE_NAME = "DummyProfile";
	final String DUMMY_MAUFACTURER = "DummyManufacturer";
	final String DUMMY_MODEL = "DummyModel";
	final String[] DUMMY_LABELS = { "Dummylabe1", "Dummylabel2" };
	final String DUMMY_DESCRIPTION = "This is dummy for test";
	final String DUMMY_OBJ = "{key1:value1, key2:value2}";
	final List<DeviceObject> DUMMY_DEVICE_RES = new ArrayList<DeviceObject>();
	final List<ProfileResource> DUMMY_PROFILE_RES = new ArrayList<ProfileResource>();
	final List<Command> DUMMY_COMMAND = new ArrayList<Command>();

	final String HTTP_OK = "200";
	final String HTTP_ACCEPTED = "202";

	private static DeviceProfile dummy;

	PropertyValue getDummyValue() {
		PropertyValue dValue = new PropertyValue();
		dValue.setType("dummyType");
		dValue.setReadWrite("dummyReadWrite");
		dValue.setMinimum("dummyMinimum");
		dValue.setMaximum("dummyMaximum");
		dValue.setDefaultValue("dummyDefauly");
		dValue.setSize("10");
		dValue.setPrecision("dummyPrecision");
		// TODO Analyze LSB
		// dValue.setLSB("1");
		return dValue;
	}

	Units getDummyUnits() {
		Units dUnits = new Units();
		dUnits.setType("dummyType");
		dUnits.setReadWrite("dummyReadWrite");
		dUnits.setDefaultValue("dummyDefaultValue");
		return dUnits;
	}

	ProfileProperty getDummyProperty() {
		ProfileProperty dProp = new ProfileProperty();
		PropertyValue dValue = getDummyValue();
		dProp.setValue(dValue);
		Units dUnits = getDummyUnits();
		dProp.setUnits(dUnits);
		return dProp;
	}

	DeviceObject getDummyObject() {
		DeviceObject dObj = new DeviceObject();
		dObj.setName("DummyObject");
		dObj.setTag("DummyTag");
		dObj.setDescription("DummyDescription");

		ProfileProperty dProp = getDummyProperty();
		dObj.setProperties(dProp);
		return dObj;
	}

	ResourceOperation getDummyGetOperation(String name) {
		ResourceOperation dummyOp = new ResourceOperation();
		dummyOp.setIndex("1");
		dummyOp.setOperation("DummyOperationGet()");
		dummyOp.setObject("DummyObject");
		dummyOp.setProperty("value");
		dummyOp.setParameter("DummyParameterGet");
		// TODO Do not make cycle between resource name and command name
		// dummyOp.setResource("DummyResourceGet");

		return dummyOp;
	}

	ResourceOperation getDummyPutOperation(String name) {
		ResourceOperation dummyOp = new ResourceOperation();
		dummyOp.setIndex("1");
		dummyOp.setOperation("DummyOperationSet()");
		dummyOp.setObject("DummyObject");
		dummyOp.setProperty("value");
		dummyOp.setParameter("DummyParameterSet");
		// TODO Do not make cycle between resource name and command name
		// dummyOp.setResource("DummyResourceSet");
		return dummyOp;
	}

	ProfileResource getDummyResource(String name) {
		ProfileResource prfRes = new ProfileResource();
		prfRes.setName(name);

		List<ResourceOperation> getList = new ArrayList<ResourceOperation>();
		// TODO set secondary and mappings
		getList.add(getDummyGetOperation(name));
		prfRes.setGet(getList);

		List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
		// TODO set secondary and mappings
		setList.add(getDummyPutOperation(name));
		prfRes.setSet(setList);
		return prfRes;
	}

	Get getDummyGet(String path, List<String> expected) {

		Get DummyGet = new Get();
		DummyGet.setPath(path);
		DummyGet.addResponse(new Response(HTTP_OK, "ok", expected));
		return DummyGet;
	}

	Put getDummyPut(String path, List<String> expected) {

		Put DummyPut = new Put();
		DummyPut.setPath(path);
		DummyPut.setParameterNames(expected);
		DummyPut.addResponse(new Response(HTTP_ACCEPTED, "ok", expected));
		return DummyPut;
	}

	Command getDummyCmd(String cmdName, Get get, Put put) {
		Command dummyCmd = new Command();
		dummyCmd.setName(cmdName);
		if (get != null) {
			dummyCmd.setGet(get);
		}

		if (put != null) {
			dummyCmd.setPut(put);
		}

		return dummyCmd;
	}

	List<String> getDummyList(String expected) {
		List<String> dummyExpected = new ArrayList<>();
		dummyExpected.add(expected);
		return dummyExpected;
	}

	public DummyProfile() {
		if (dummy == null) {
			dummy = new DeviceProfile();
			dummy.setOrigin(new Timestamp(System.currentTimeMillis()).getTime());
			dummy.setCreated(new Timestamp(System.currentTimeMillis()).getTime());
			dummy.setName(DUMMY_PROFILE_NAME);
			dummy.setManufacturer(DUMMY_MAUFACTURER);
			dummy.setModel(DUMMY_MODEL);
			dummy.setLabels(DUMMY_LABELS);
			dummy.setDescription(DUMMY_DESCRIPTION);
			dummy.setObjects(DUMMY_OBJ);

			// set DUMMY_DEVICE_RES
			DUMMY_DEVICE_RES.add(getDummyObject());
			dummy.setDeviceResources(DUMMY_DEVICE_RES);

			// set DUMMY_PROFILE_RES
			DUMMY_PROFILE_RES.add(getDummyResource("getAlone"));
			DUMMY_PROFILE_RES.add(getDummyResource("putAlone"));
			DUMMY_PROFILE_RES.add(getDummyResource("getMulti"));
			DUMMY_PROFILE_RES.add(getDummyResource("putMulti"));
			dummy.setResources(DUMMY_PROFILE_RES);

			// set DUMMY_COMMAND
			DUMMY_COMMAND.add(getDummyCmd("dummyGetAlone",
					getDummyGet(COMMAND_PREFIX + "getAlone", getDummyList("getAlone")), null));

			DUMMY_COMMAND.add(getDummyCmd("dummyPutAlone", null,
					getDummyPut(COMMAND_PREFIX + "putAlone", getDummyList("putAlone"))));

			DUMMY_COMMAND.add(
					getDummyCmd("dummyGetPutMulti", getDummyGet(COMMAND_PREFIX + "getMulti", getDummyList("getMulti")),
							getDummyPut(COMMAND_PREFIX + "putMulti", getDummyList("putMulti"))));

			dummy.setCommands(DUMMY_COMMAND);
		}
	}

	DeviceProfile getDummy() {
		return dummy;
	}

	void checkDummyProfileFromMetaData(DeviceProfile metaProfile) {
		assertEquals("Device profile name does not match expected", dummy.getName(), metaProfile.getName());
		assertEquals("Device profile commands list size does not match expected", dummy.getCommands().size(),
				metaProfile.getCommands().size());
		assertEquals("Device profile description does not match expected", dummy.getDescription(),
				metaProfile.getDescription());
		assertArrayEquals("Device profile labels does not match expected", dummy.getLabels(), metaProfile.getLabels());
		assertEquals("Device profile manufacturer does not match expected", dummy.getManufacturer(),
				metaProfile.getManufacturer());
		assertEquals("Device profile model does not match expected", dummy.getModel(), metaProfile.getModel());
		assertEquals("Device profile object does not match expected", dummy.getObjects(), metaProfile.getObjects());
		assertNotNull("Device profile modified date is null", metaProfile.getModified());
		assertNotNull("Device profile created date is null", metaProfile.getCreated());
	}

}
