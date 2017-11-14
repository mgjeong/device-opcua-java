package org.device.opcua.sample;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edgexfoundry.domain.common.ValueDescriptor;
import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.core.Reading;
import org.edgexfoundry.domain.meta.Device;
import org.springframework.stereotype.Component;

@Component
public class TransformTest extends DummyTest{


	public Event transFormToEvent(EdgeMessage data, Device device)
	{
		if (dummyValueDescriptor == null) {
			dummyValueDescriptor = new DummyValueDescriptor();
		}

		ValueDescriptor valueDesp = dummyValueDescriptor.getDummy();
		
		List<Reading> readings = new ArrayList<Reading>();
		String value = "\""+data.getResponses().get(0).getMessage().toString()+
				" "+"value name="+data.getResponses().get(0).getEdgeNodeInfo().getEdgeNodeID().getIdentifier()+"\"";
		Reading reading = new Reading(valueDesp.getName(), value);
		
		if (device == null) {
			device = dummyDevice.getDummy();
		}
		
		readings.add(reading);
		Event event = new Event(device.getName(), readings);
		event.markPushed(new Timestamp(System.currentTimeMillis()).getTime());
		event.setDevice(device.getName());
		event.setOrigin(new Timestamp(System.currentTimeMillis()).getTime());
		
		return event;
	}
	
	public void sendEventToCoreData(Event event) {

		String ret = new String("");

			try {
				
				ret = eventClient.add(event);
				
				logger.debug("2.[Send]Send transformed Event to coredata successfully msg: " + ret);
			} catch (Exception e) {
				logger.error("[Send]Could not add transformed Event to coredata msg: "
						+ e.getMessage());
			}
		
	}
	
	public List<Event> getTransformedEventFromCoreDataByDeviceId(String deviceId) {
		try {
			List<Event> events = eventClient.eventsForDevice(deviceId, 1);
			logger.debug("3.[Get]get transformed Event from coredata successfully msg: " +"value of event = "+events.get(0).getReadings().get(0).getValue()
																																														/*	+"\n		whole event info : "+events.get(0)*/);
			return events;
		} catch (Exception e) {
			logger.error("[Get]Could not get transformed Event to coredata msg: "
					+ e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	
	public void run() {
		addDummyProfileToMetaData();
		getDummyProfileFromMetaData();
		checkDummyProfileFromMetaData();

		addDummyDeviceToMetaData();
		checkDummyDeviceFromMetaData();

		addDummyValueDescriptorToCoreData();

	}
}
