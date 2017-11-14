package org.edgexfoundry.device.opcua.emf;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.ProtocolManager.DiscoveryCallback;
import org.edge.protocol.opcua.api.ProtocolManager.ReceivedMessageCallback;
import org.edge.protocol.opcua.api.ProtocolManager.StatusCallback;
import org.edge.protocol.opcua.api.client.EdgeResponse;
import org.edge.protocol.opcua.api.common.EdgeBrowseResult;
import org.edge.protocol.opcua.api.common.EdgeConfigure;
import org.edge.protocol.opcua.api.common.EdgeDevice;
import org.edge.protocol.opcua.api.common.EdgeEndpointConfig;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.example.EdgeEmulator;
import org.edgexfoundry.device.opcua.DataDefaultValue;
import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.core.Reading;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Protocol;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;

public class EMFAdapter {
	private final static EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(EMFAdapter.class);
	private EdgeEmulator OpcUaAdapter;
	private static EMFAdapter singleton = null;
	private static final int mPort = 5562;
	private static Publisher pub = null;
	private String fileName = "rawSampleData.txt";
	
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

	public void startAdapter() {
		// 1. create addressable (by default)
		Addressable addressable = new Addressable(DataDefaultValue.NAME.getValue(), Protocol.TCP,
				DataDefaultValue.ADDRESS.getValue(), DataDefaultValue.PATH.getValue(),
				DataDefaultValue.ADDRESSABLE_PORT);

		// 2. get EdgeEndpoint URI
		String endpointUri = getEndpointUrifromAddressable(addressable);

		// 3. set configure
		EdgeConfigure configure = new EdgeConfigure.Builder().setRecvCallback(receiver)
				.setStatusCallback(statusCallback).setDiscoveryCallback(discoveryCallback).build();

		ProtocolManager protocolManager = ProtocolManager.getProtocolManagerInstance();
		protocolManager.configure(configure);

		// 4. startServer
		EdgeEndpointConfig endpointConfig = new EdgeEndpointConfig.Builder()
				.setApplicationName(EdgeOpcUaCommon.DEFAULT_SERVER_APP_NAME.getValue())
				.setApplicationUri(EdgeOpcUaCommon.DEFAULT_SERVER_APP_URI.getValue()).build();
		EdgeEndpointInfo ep = new EdgeEndpointInfo.Builder(endpointUri).setConfig(endpointConfig).build();
		try {
			OpcUaAdapter = new EdgeEmulator();
			OpcUaAdapter.startServer(ep, fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	ReceivedMessageCallback receiver = new ReceivedMessageCallback() {
		@Override
		public void onResponseMessages(EdgeMessage data) {
			// TODO Auto-generated method stub
			CompletableFuture<EdgeMessage> future = data.getEdgeEndpointInfo().getFuture();
			if (future != null) {
				future.complete(data);
			}
		}

		@Override
		public void onMonitoredMessage(EdgeMessage data) {
			// TODO Auto-generated method stub
			receive(data);

//			for (EdgeResponse res : data.getResponses()) {
//				Event event = EventGenerator.newEvent(
//						res.getEdgeNodeInfo().getValueAlias().replaceAll("/", DataDefaultValue.REPLACE_DEVICE_NAME),
//						res.getMessage().toString());
//			}
		}

		@Override
		public void onErrorMessage(EdgeMessage data) {
			// TODO Auto-generated method stub
			logger.info("[Error] onErrorMessage");
		}

		@Override
		public void onBrowseMessage(EdgeNodeInfo endpoint, List<EdgeBrowseResult> responses, int requestId) {
			// TODO Auto-generated method stub

		}
	};

	DiscoveryCallback discoveryCallback = new DiscoveryCallback() {
		@Override
		public void onFoundEndpoint(EdgeDevice device) {
			// TODO Auto-generated method stub
			logger.info("[Event] onFoundEndpoint");
			for (EdgeEndpointInfo ep : device.getEndpoints()) {
				logger.info("-> {}, {}", ep.getEndpointUri(), ep.getConfig().getSecurityPolicyUri());
			}
		}

		@Override
		public void onFoundDevice(EdgeDevice device) {
			// TODO Auto-generated method stub
			logger.info("[Event] onFoundDevice is not supported");
		}
	};

	StatusCallback statusCallback = new StatusCallback() {

		@Override
		public void onStart(EdgeEndpointInfo ep, EdgeStatusCode status, List<String> attiributeAliasList,
				List<String> methodAliasList, List<String> viewAliasList) {
			// TODO Auto-generated method stub
			logger.info("onStart({})", ep.getEndpointUri());

			if (status == EdgeStatusCode.STATUS_CLIENT_STARTED) {
				try {
					OpcUaAdapter.runClientSub();
					OpcUaAdapter.runAutoUpdateServerWithFile();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (status == EdgeStatusCode.STATUS_SERVER_STARTED) {
				try {
					// Create Namespace and nodes
					OpcUaAdapter.initServerNodes();
					// startClient
					OpcUaAdapter.startClient(ep);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onStop(EdgeEndpointInfo ep, EdgeStatusCode status) {
			// TODO Auto-generated method stub
			logger.info("onStop({})", ep.getEndpointUri());
		}

		@Override
		public void onNetworkStatus(EdgeEndpointInfo ep, EdgeStatusCode status) {
			// TODO Auto-generated method stub
			logger.info("onNetworkStatus: status {} from {}", status, ep.getEndpointUri());
		}
	};

	private void receive(EdgeMessage data) {
		// TODO 7: [Optional] Fill with your own implementation for handling
		// asynchronous data from the driver layer to the device service
		// Device device = null;
		// String result = "";
		// ResourceOperation operation = null;

		// objectCache.put(device, operation, result);
		for (EdgeResponse res : data.getResponses()) {
			logger.info("onMonitoredMessage = {}", res.getMessage().getValue());
		}	

		try {
			pub.publishEvent(getEvent(data));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Event getEvent(EdgeMessage data) {

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

	public String getEndpointUrifromAddressable(Addressable addressable) {
		String endpointUri = "";
		if (addressable.getProtocol() == Protocol.TCP) {
			endpointUri += String.format("%s", "opc.tcp://");
		} else {
			endpointUri += String.format("%s", "http://");
		}

		endpointUri += String.format("%s:%d/%s", addressable.getAddress(), addressable.getPort(),
				addressable.getPath());
		return endpointUri;
	}
}
