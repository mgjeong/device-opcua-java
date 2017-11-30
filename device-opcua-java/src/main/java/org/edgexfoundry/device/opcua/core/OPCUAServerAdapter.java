package org.edgexfoundry.device.opcua.core;

import org.edge.protocol.opcua.api.common.EdgeEndpointConfig;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.example.EdgeEmulator;
import org.edgexfoundry.device.opcua.DataDefaultValue;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Protocol;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;

public class OPCUAServerAdapter {
	private final static EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(OPCUAServerAdapter.class);
	private EdgeEmulator opcUaEmulator;
	private static OPCUAServerAdapter singleton = null;
	private String endpointUri = null;
	private Addressable addressable = null;

	private OPCUAServerAdapter() {
		opcUaEmulator = new EdgeEmulator();
	}

	public static OPCUAServerAdapter getInstance() {

		if (singleton == null) {
			singleton = new OPCUAServerAdapter();
		}

		return singleton;
	}

	public void runServer() throws Exception {
		// Create Namespace and nodes
		opcUaEmulator.runAutoUpdateServer();
		opcUaEmulator.initServerNodes();
	}

	public void startOPCUAAdapter() throws Exception {
		// 1. run discovery device
		// TODO
		// we need to support like discovery-seed micro-service

		// 2. create addressable (by default)
		addressable = new Addressable(DataDefaultValue.NAME.getValue(), Protocol.TCP,
				DataDefaultValue.ADDRESS.getValue(), DataDefaultValue.PATH.getValue(),
				DataDefaultValue.ADDRESSABLE_PORT);

		// 3. get EdgeEndpoint URI
		endpointUri = getEndpointUrifromAddressable(addressable);

		// 4. startServer
		EdgeEndpointConfig endpointConfig = new EdgeEndpointConfig.Builder()
				.setApplicationName(EdgeOpcUaCommon.DEFAULT_SERVER_APP_NAME.getValue())
				.setApplicationUri(EdgeOpcUaCommon.DEFAULT_SERVER_APP_URI.getValue()).build();
		EdgeEndpointInfo ep = new EdgeEndpointInfo.Builder(endpointUri).setConfig(endpointConfig).build();
		opcUaEmulator.startServer(ep, "");
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
