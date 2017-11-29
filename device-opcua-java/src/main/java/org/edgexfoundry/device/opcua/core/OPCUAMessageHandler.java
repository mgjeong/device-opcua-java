package org.edgexfoundry.device.opcua.core;

import java.util.concurrent.CompletableFuture;

import org.command.json.format.EdgeElement;
import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.client.EdgeSubRequest;
import org.edge.protocol.opcua.api.common.EdgeCommandType;
import org.edge.protocol.opcua.api.common.EdgeEndpointConfig;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeMessageType;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeRequest;
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Protocol;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;

public class OPCUAMessageHandler {
	private final static EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(OPCUAMessageHandler.class);
	private static OPCUAMessageHandler singleton = null;

	private OPCUAMessageHandler() {
	}

	public static OPCUAMessageHandler getInstance() {
		if (singleton == null) {
			singleton = new OPCUAMessageHandler();
		}
		return singleton;
	}

	public EdgeMessage convertEdgeElementToEdgeMessage(EdgeElement element, String operation, String providerKey,
			Addressable addr, CompletableFuture<EdgeMessage> future) throws Exception {
		EdgeMessage msg = null;

		if (operation.equalsIgnoreCase(EdgeCommandType.CMD_START_CLIENT.getValue())) {
			return getStartMessage(element, providerKey, addr, future);
		} else if (operation.equalsIgnoreCase(EdgeCommandType.CMD_STOP_CLIENT.getValue())) {
			return getStopMessage(element, providerKey, addr, future);
		} else if (operation.equalsIgnoreCase(EdgeCommandType.CMD_READ.getValue())) {
			return getReadMessage(element, providerKey, addr, future);
		} else if (operation.equalsIgnoreCase(EdgeCommandType.CMD_WRITE.getValue())) {
			return getWriteMessage(element, providerKey, addr, future);
		} else if (operation.equalsIgnoreCase(EdgeCommandType.CMD_SUB.getValue())) {
			return getSubMessage(element, providerKey, addr, future);
		} else if (operation.equalsIgnoreCase(EdgeCommandType.CMD_METHOD.getValue())) {
			return getMethodMessage(element, providerKey, addr, future);
		} else if (operation.equalsIgnoreCase(EdgeCommandType.CMD_GET_ENDPOINTS.getValue())) {
			return getEndpointMessage(element, providerKey, addr, future);
		} else {

		}
		return msg;
	}

	private EdgeMessage getStartMessage(EdgeElement element, String providerKey, Addressable addr,
			CompletableFuture<EdgeMessage> future) throws Exception {

		EdgeEndpointConfig endpointConfig = new EdgeEndpointConfig.Builder()
				.setApplicationName(EdgeOpcUaCommon.DEFAULT_SERVER_APP_NAME.getValue())
				.setApplicationUri(EdgeOpcUaCommon.DEFAULT_SERVER_APP_URI.getValue()).build();
		EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr))
				.setConfig(endpointConfig).setFuture(future).build();

		EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().build();
		EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_START_CLIENT)
				.setMessageType(EdgeMessageType.SEND_REQUEST).setRequest(new EdgeRequest.Builder(nodeInfo).build())
				.build();
		return msg;
	}

	private EdgeMessage getStopMessage(EdgeElement element, String providerKey, Addressable addr,
			CompletableFuture<EdgeMessage> future) throws Exception {
		EdgeNodeInfo endpoint = new EdgeNodeInfo.Builder().build();
		EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr)).setFuture(future)
				.build();

		EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_STOP_CLIENT)
				.setRequest(new EdgeRequest.Builder(endpoint).build()).build();
		return msg;
	}

	private EdgeMessage getReadMessage(EdgeElement element, String providerKey, Addressable addr,
			CompletableFuture<EdgeMessage> future) throws Exception {
		EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().setValueAlias(providerKey).build();

		EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr)).setFuture(future)
				.build();
		EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
				.setMessageType(EdgeMessageType.SEND_REQUEST).setRequest(new EdgeRequest.Builder(nodeInfo).build())
				.build();

		return msg;
	}

	private EdgeMessage getWriteMessage(EdgeElement element, String providerKey, Addressable addr,
			CompletableFuture<EdgeMessage> future) throws Exception {
		EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().setValueAlias(providerKey).build();
		EdgeVersatility param = new EdgeVersatility.Builder("OFF").build();

		EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr)).setFuture(future)
				.build();
		EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_WRITE)
				.setMessageType(EdgeMessageType.SEND_REQUEST)
				.setRequest(new EdgeRequest.Builder(nodeInfo).setMessage(param).build()).build();

		return msg;
	}

	private EdgeMessage getSubMessage(EdgeElement element, String providerKey, Addressable addr,
			CompletableFuture<EdgeMessage> future) throws Exception {
		EdgeSubRequest sub = new EdgeSubRequest.Builder(EdgeNodeIdentifier.Edge_Create_Sub).setSamplingInterval(100.0)
				.build();
		EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(providerKey).build();
		EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr)).setFuture(future)
				.build();

		EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB)
				.setMessageType(EdgeMessageType.SEND_REQUEST)
				.setRequest(new EdgeRequest.Builder(ep).setSubReq(sub).build()).build();

		return msg;
	}

	private EdgeMessage getMethodMessage(EdgeElement element, String providerKey, Addressable addr,
			CompletableFuture<EdgeMessage> future) throws Exception {
		EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr)).setFuture(future)
				.build();
		EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(providerKey).build();
		EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_METHOD)
				.setRequest(new EdgeRequest.Builder(ep).setMessage(new EdgeVersatility.Builder(16.0).build()).build())
				.build();
		return msg;
	}

	private EdgeMessage getEndpointMessage(EdgeElement element, String providerKey, Addressable addr,
			CompletableFuture<EdgeMessage> future) throws Exception {
		EdgeEndpointConfig config = new EdgeEndpointConfig.Builder()
				.setApplicationName(EdgeOpcUaCommon.DEFAULT_SERVER_APP_NAME.getValue())
				.setApplicationUri(EdgeOpcUaCommon.DEFAULT_SERVER_APP_URI.getValue()).build();
		EdgeEndpointInfo ep = new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr)).setConfig(config)
				.setFuture(future).build();
		EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().build();
		EdgeMessage msg = new EdgeMessage.Builder(ep).setCommand(EdgeCommandType.CMD_GET_ENDPOINTS)
				.setRequest(new EdgeRequest.Builder(nodeInfo).build()).build();
		return msg;
	}

	public void sendMessage(EdgeMessage msg) throws Exception {
		try {
			ProtocolManager.getProtocolManagerInstance().send(msg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
