package org.edgexfoundry.samsung.opcua.sample;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.core.Reading;

public class DummyEvent {
	List<Reading> readings = null;

	Reading getDummyReading() {
		Random rand = new Random();
		Reading reading = new Reading();
		// Guide2: Reading must has name which matched with ValueDescriptor
		// which posted in metadata DB.
		reading.setName(DummyValueDescriptor.getName());

		// Guide3: Value can set with setValue(String) method.
		reading.setValue("dummy" + rand.nextLong());
		return reading;
	}

	public DummyEvent() {
		readings = new ArrayList<>();
		readings.add(getDummyReading());
	}

	public Event getDummyEvent(String deviceIdentifier) {
		// Guide1: To construct event, device identifier required.
		// device identifier can be name of device which posted in metadata DB.
		Event dummy = new Event(deviceIdentifier, readings);

		dummy.markPushed(new Timestamp(System.currentTimeMillis()).getTime());
		dummy.setDevice(deviceIdentifier);
		dummy.setOrigin(new Timestamp(System.currentTimeMillis()).getTime());
		return dummy;
	}

}
