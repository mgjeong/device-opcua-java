package org.edgexfoundry.device.opcua.coredata;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.core.Reading;

public class EventGenerator {
    private static Reading getReading(String DeviceName, String value) {
        Reading reading = new Reading();
        // Guide2: Reading must has name which matched with ValueDescriptor
        // which posted in metadata DB.
        reading.setName(DeviceName);

        // Guide3: Value can set with setValue(String) method.
        reading.setValue(value);
        return reading;
    }

    public static List<Reading> getReadingList(String DeviceName, String value) {
        List<Reading> readings = new ArrayList<>();
        readings.add(getReading(DeviceName, value));
        return readings;
    }

    public static Event newEvent(String DeviceName, String value) {
        // Guide1: To construct event, device identifier required.
        // device identifier can be name of device which posted in metadata DB.
        List<Reading> readingList = getReadingList(DeviceName, value);

        Event event = new Event(DeviceName, readingList);

        event.markPushed(new Timestamp(System.currentTimeMillis()).getTime());
        event.setDevice(DeviceName);
        event.setOrigin(new Timestamp(System.currentTimeMillis()).getTime());
        return event;
    }
}
