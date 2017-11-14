package org.edgexfoundry.device.opcua.sample;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.core.Reading;
//import org.springframework.data.mongodb.core.mapping.DBRef;

public class DataTransform {


	NamingRule ruleForDeviceName;
	NamingRule ruleForValueDescriptor;
	
	void setRuleForDeviceName(NamingRule rule)
	{
		ruleForDeviceName = rule;
	}
	void setRuleForValueDescriptor(NamingRule rule)
	{
		ruleForValueDescriptor = rule;
	}
	
	
	Reading generateReadingFromEdgeMessage(EdgeMessage data)
	{
			Reading reading = new Reading();
			
			//TODO: need some logic for applying naming rule. (not just copying edgeNodeUri)
			String valueDes = (String)data.getResponses().get(0).getEdgeNodeInfo().getEdgeNodeID().getIdentifier();
			
			if(valueDes == null)
			{
				// this case should be error case. but now, put dummy value descriptor for just simulation.
				valueDes = "DummyValue0098";
			}
			else
			{
				valueDes = valueDes.replace("/","_");
			}
			
			reading.setName(valueDes);
			reading.setValue(data.getResponses().get(0).getMessage().toString());
			
			return reading;
	}
	
	public Event generateEventFromEdgeMessage(EdgeMessage data)
	{
		
		//TODO: need some logic for applying naming rule. (not just copying edgeNodeUri)
		String device = (String)data.getResponses().get(0).getEdgeNodeInfo().getEdgeNodeID().getIdentifier();
		if(device == null)
		{
			// this case should be error case. but now, put dummy device for just simulation.
			device = "DummyDevice";
		}
		else
		{
			device = device.replace("/","_");
		}

		
		List<Reading> readings = new ArrayList<Reading>();
		readings.add(generateReadingFromEdgeMessage(data));
		
		Event event = new Event(device, readings);
		
		event.markPushed(new Timestamp(System.currentTimeMillis()).getTime());
		event.setOrigin(new Timestamp(System.currentTimeMillis()).getTime());
		
		return event;
	}
	
	class NamingRule
	{
		
		
		
		
		
	}

}
