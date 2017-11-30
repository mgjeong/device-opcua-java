package org.edgexfoundry.device.opcua.emf;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.edge.protocol.opcua.api.client.EdgeResponse;
import org.edge.protocol.opcua.api.common.EdgeMessage;

import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.core.Reading;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;

public class EMFAdapter {
	private final static EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(EMFAdapter.class);
	private static EMFAdapter singleton = null;
	private static final int mPort = 5562;
	private static Publisher pub = null;

	private EMFAdapter() {
		pub = Publisher.getInstance();
		pub.startPublisher(mPort);
	}

	public static EMFAdapter getInstance() {
		if (singleton == null) {
			singleton = new EMFAdapter();
		}

		return singleton;
	}

	public void publish(EdgeMessage data) {
		for (EdgeResponse res : data.getResponses()) {
			logger.info("onMonitoredMessage = {}", res.getMessage().getValue());
		}

		try {
			pub.publishEvent(getEvent(data));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Event getEvent(EdgeMessage data) {

		List<Reading> readings = null;
		readings = new ArrayList<Reading>();
		Reading reading = new Reading();
		reading.setName(data.getResponses().get(0).getEdgeNodeInfo().getValueAlias());
		reading.setValue(data.getResponses().get(0).getMessage().getValue().toString());
		reading.setCreated(0);
		reading.setDevice(data.getEdgeEndpointInfo().getEndpointUri());
		reading.setModified(0);
		reading.setId("id1");
		reading.setOrigin(new Timestamp(System.currentTimeMillis()).getTime());
		reading.setPushed(new Timestamp(System.currentTimeMillis()).getTime());

		readings.add(reading);

		Event event = new Event(data.getEdgeEndpointInfo().getEndpointUri(), readings);
		event.setCreated(0);
		event.setModified(0);
		event.setId("id1");
		event.markPushed(new Timestamp(System.currentTimeMillis()).getTime());
		event.setOrigin(new Timestamp(System.currentTimeMillis()).getTime());

		return event;
	}
}
