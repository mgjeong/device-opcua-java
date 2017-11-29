package org.edgexfoundry.device.opcua.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.ProtocolManager.DiscoveryCallback;
import org.edge.protocol.opcua.api.ProtocolManager.ReceivedMessageCallback;
import org.edge.protocol.opcua.api.ProtocolManager.StatusCallback;
import org.edge.protocol.opcua.api.client.EdgeResponse;
import org.edge.protocol.opcua.api.client.EdgeSubRequest;
import org.edge.protocol.opcua.api.common.EdgeBrowseResult;
import org.edge.protocol.opcua.api.common.EdgeCommandType;
import org.edge.protocol.opcua.api.common.EdgeConfigure;
import org.edge.protocol.opcua.api.common.EdgeDevice;
import org.edge.protocol.opcua.api.common.EdgeEndpointConfig;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeMessageType;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeRequest;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edgexfoundry.controller.EventClient;
import org.edgexfoundry.device.opcua.DataDefaultValue;
import org.edgexfoundry.device.opcua.data.ObjectStore;
import org.edgexfoundry.device.opcua.emf.EMFAdapter;
import org.edgexfoundry.device.opcua.metadata.DeviceEnroller;
import org.edgexfoundry.device.opcua.metadata.MetaDataType;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.Protocol;
import org.edgexfoundry.domain.meta.ResourceOperation;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class OPCUAAdapter {
	private final static EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(OPCUAAdapter.class);
	private static OPCUAAdapter singleton = null;
	private String endpointUri = null;
	private Addressable addressable = null;

	@Autowired
	private DeviceEnroller deviceEnroller;

	@Autowired
	private EventClient eventClient;

	@Autowired
	private ObjectStore objectCache;

	private OPCUAAdapter(DeviceEnroller deviceEnroller, EventClient eventClient, ObjectStore objectCache) {
		this.deviceEnroller = deviceEnroller;
		this.eventClient = eventClient;
		this.objectCache = objectCache;
		
		// register callback
		EdgeConfigure configure = new EdgeConfigure.Builder().setRecvCallback(receiverCallback)
				.setStatusCallback(statusCallback).setDiscoveryCallback(discoveryCallback).build();

		ProtocolManager protocolManager = ProtocolManager.getProtocolManagerInstance();
		protocolManager.configure(configure);
	}

	public static OPCUAAdapter getInstance(DeviceEnroller deviceEnroller, EventClient eventClient,
			ObjectStore objectCache) {

		if (singleton == null) {
			singleton = new OPCUAAdapter(deviceEnroller, eventClient, objectCache);
		}

		return singleton;
	}

	private void receive(EdgeMessage data) {
		// TODO 7: [Optional] Fill with your own implementation for handling
		// asynchronous data from the driver layer to the device service

		for (EdgeResponse res : data.getResponses()) {
			logger.info(
					"[FROM OPCUA Stack] Data received = {} topic={}, endpoint={}, namespace={}, "
							+ "edgenodeuri={}, methodname={}, edgenodeId={} ",
					res.getMessage().getValue(), res.getEdgeNodeInfo().getValueAlias(),
					data.getEdgeEndpointInfo().getEndpointUri(), res.getEdgeNodeInfo().getEdgeNodeID().getNameSpace(),
					res.getEdgeNodeInfo().getEdgeNodeID().getEdgeNodeUri(), res.getEdgeNodeInfo().getMethodName(),
					res.getEdgeNodeInfo().getEdgeNodeID().getEdgeNodeIdentifier());
		}

		Device device = null;
		String result = "";
		ResourceOperation operation = null;

		objectCache.put(device, operation, result);
	}

	ReceivedMessageCallback receiverCallback = new ReceivedMessageCallback() {
		@Override
		public void onResponseMessages(EdgeMessage data) {
			// TODO Auto-generated method stub
			for (EdgeResponse res : data.getResponses()) {
				logger.info("[res] " + res.getMessage().getValue().toString());
			}

			CompletableFuture<EdgeMessage> future = data.getEdgeEndpointInfo().getFuture();
			if (future != null) {
				future.complete(data);
			}
		}

		@Override
		public void onMonitoredMessage(EdgeMessage data) {
			// TODO Auto-generated method stub
			receive(data);
			// publish
			EMFAdapter.getInstance().publish(data);
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
				logger.info("onStart(STATUS_CLIENT_STARTED)");
				try {
					deviceEnroller.initialize();
					testRead();
					testSub();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (status == EdgeStatusCode.STATUS_SERVER_STARTED) {
				logger.info("onStart(STATUS_SERVER_STARTED)");
				// TODO
				// run server
				try {
					OPCUAServerAdapter.getInstance().runServer();
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
			deviceEnroller.cleanCoreData();
			deviceEnroller.cleanMetaData(MetaDataType.ALL);
		}

		@Override
		public void onNetworkStatus(EdgeEndpointInfo ep, EdgeStatusCode status) {
			// TODO Auto-generated method stub
			logger.info("onNetworkStatus: status {} from {}", status, ep.getEndpointUri());
			if (EdgeStatusCode.STATUS_DISCONNECTED == status) {
				deviceEnroller.cleanCoreData();
				deviceEnroller.cleanMetaData(MetaDataType.DEVICE);
				deviceEnroller.cleanMetaData(MetaDataType.DEVICE_PROFILE);
			}
		}
	};

	public void testStartServer() throws Exception {
		// TODO
		// start server
		OPCUAServerAdapter.getInstance().startOPCUAAdapter();
	}

	public void startAdapter() throws Exception {
		// 1. run discovery device
		// TODO
		// we need to support like discovery-seed micro-service

		// 2. create addressable (by default)
		addressable = new Addressable(DataDefaultValue.NAME.getValue(), Protocol.TCP,
				DataDefaultValue.ADDRESS.getValue(), DataDefaultValue.PATH.getValue(),
				DataDefaultValue.ADDRESSABLE_PORT);

		// 3. get EdgeEndpoint URI
		endpointUri = getEndpointUrifromAddressable(addressable);

		EdgeEndpointConfig endpointConfig = new EdgeEndpointConfig.Builder()
				.setApplicationName(EdgeOpcUaCommon.DEFAULT_SERVER_APP_NAME.getValue())
				.setApplicationUri(EdgeOpcUaCommon.DEFAULT_SERVER_APP_URI.getValue()).build();
		EdgeEndpointInfo ep = new EdgeEndpointInfo.Builder(endpointUri).setConfig(endpointConfig).build();

		// startClient
		EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().build();
		EdgeMessage msg = new EdgeMessage.Builder(ep).setCommand(EdgeCommandType.CMD_START_CLIENT)
				.setMessageType(EdgeMessageType.SEND_REQUEST).setRequest(new EdgeRequest.Builder(nodeInfo).build())
				.build();
		ProtocolManager.getProtocolManagerInstance().send(msg);
	}

	private void testRead() throws Exception {
		EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().setValueAlias("/1/cnc14").build();

		EdgeEndpointInfo ep = new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addressable)).setFuture(null)
				.build();
		EdgeMessage msg = null;

		msg = new EdgeMessage.Builder(ep).setCommand(EdgeCommandType.CMD_READ)
				.setMessageType(EdgeMessageType.SEND_REQUEST).setRequest(new EdgeRequest.Builder(nodeInfo).build())
				.build();

		try {
			ProtocolManager.getProtocolManagerInstance().send(msg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void testSub() throws Exception {
		EdgeSubRequest sub = new EdgeSubRequest.Builder(EdgeNodeIdentifier.Edge_Create_Sub).setSamplingInterval(100.0)
				.build();
		EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias("/1/cnc14").build();
		EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addressable))
				.setFuture(null).build();

		EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB)
				.setMessageType(EdgeMessageType.SEND_REQUEST)
				.setRequest(new EdgeRequest.Builder(ep).setSubReq(sub).build()).build();
		ProtocolManager.getProtocolManagerInstance().send(msg);
	}

	private String getEndpointUrifromAddressable(Addressable addressable) {
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
