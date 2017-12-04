/******************************************************************
 *
 * Copyright 2017 Samsung Electronics All Rights Reserved.
 *
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 ******************************************************************/

package org.edgexfoundry.device.opcua.adapter;

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
import org.edgexfoundry.device.opcua.DataDefaultValue;
import org.edgexfoundry.device.opcua.adapter.emf.EMFAdapter;
import org.edgexfoundry.device.opcua.metadata.MetaDataType;
import org.edgexfoundry.device.opcua.opcua.OPCUADriver.DriverCallback;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.Protocol;
import org.edgexfoundry.domain.meta.ResourceOperation;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;

public class OPCUAAdapter {
  private final static EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(OPCUAAdapter.class);
  private static OPCUAAdapter singleton = null;
  private String endpointUri = null;
  private Addressable addressable = null;
  private DriverCallback driverCallback = null;

  private OPCUAAdapter(DriverCallback callback) {
    this.driverCallback = callback;

    // register callback
    EdgeConfigure configure = new EdgeConfigure.Builder().setRecvCallback(receiverCallback)
        .setStatusCallback(statusCallback).setDiscoveryCallback(discoveryCallback).build();

    ProtocolManager protocolManager = ProtocolManager.getProtocolManagerInstance();
    protocolManager.configure(configure);
  }

  public static OPCUAAdapter getInstance(DriverCallback callback) {

    if (singleton == null) {
      singleton = new OPCUAAdapter(callback);
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
          data.getEdgeEndpointInfo().getEndpointUri(),
          res.getEdgeNodeInfo().getEdgeNodeID().getNameSpace(),
          res.getEdgeNodeInfo().getEdgeNodeID().getEdgeNodeUri(),
          res.getEdgeNodeInfo().getMethodName(),
          res.getEdgeNodeInfo().getEdgeNodeID().getEdgeNodeIdentifier());
    }

    Device device = null;
    String result = "";
    ResourceOperation operation = null;

    driverCallback.onReceive(device, operation, result);
  }

  ReceivedMessageCallback receiverCallback = new ReceivedMessageCallback() {
    @Override
    public void onResponseMessages(EdgeMessage data) {
      // TODO Auto-generated method stub
      for (EdgeResponse res : data.getResponses()) {
        logger.info("[res] " + res.getMessage().getValue().toString());
      }

      CompletableFuture<String> future = data.getEdgeEndpointInfo().getFuture();
      // TODO
      // response format based command data model should be generated in this place.
      String responseFormat = "default_response";
      responseFormat = OPCUAMessageHandler.getInstance().convertEdgeMessagetoEdgeElement(data);
      if (future != null && responseFormat != null) {
        future.complete(responseFormat);
      } else {
        logger.error("EdgeElement data of the response message is invalid");
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
    public void onBrowseMessage(EdgeNodeInfo endpoint, List<EdgeBrowseResult> responses,
        int requestId) {
      // TODO Auto-generated method stub

    }
  };

  DiscoveryCallback discoveryCallback = new DiscoveryCallback() {
    @Override
    public void onFoundEndpoint(EdgeDevice device) {
      // TODO Auto-generated method stub
      logger.info("[Event] onFoundEndpoint");
      EdgeEndpointInfo epInfo = null;
      for (EdgeEndpointInfo ep : device.getEndpoints()) {
        epInfo = ep;
        logger.info("-> {}, {}", ep.getEndpointUri(), ep.getConfig().getSecurityPolicyUri());
      }

      CompletableFuture<String> future = epInfo.getFuture();

      // TODO
      // response format based command data model should be generated in this place.
      String responseFormat = "default_endpoint_res";
      responseFormat = OPCUAMessageHandler.getInstance().convertEdgeDevicetoEdgeElement(device);
      if (future != null && responseFormat != null) {
        future.complete(responseFormat);
      } else {
        logger.error("EdgeElement data of the onFoundEndpoint callback is invalid");
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
    public void onStart(EdgeEndpointInfo ep, EdgeStatusCode status,
        List<String> attiributeAliasList, List<String> methodAliasList,
        List<String> viewAliasList) {
      // TODO Auto-generated method stub
      logger.info("onStart({})", ep.getEndpointUri());

      if (status == EdgeStatusCode.STATUS_CLIENT_STARTED) {
        logger.info("onStart(STATUS_CLIENT_STARTED)");
        try {
          driverCallback.onInit();
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
      driverCallback.onDeleteCoreData();
      driverCallback.onDeleteMetaData(MetaDataType.ALL);
    }

    @Override
    public void onNetworkStatus(EdgeEndpointInfo ep, EdgeStatusCode status) {
      // TODO Auto-generated method stub
      logger.info("onNetworkStatus: status {} from {}", status, ep.getEndpointUri());
      if (EdgeStatusCode.STATUS_DISCONNECTED == status) {
        driverCallback.onDeleteCoreData();
        driverCallback.onDeleteMetaData(MetaDataType.DEVICE);
        driverCallback.onDeleteMetaData(MetaDataType.DEVICE_PROFILE);
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
    endpointUri = OPCUAMessageHandler.getInstance().getEndpointUrifromAddressable(addressable);

    EdgeEndpointConfig endpointConfig = new EdgeEndpointConfig.Builder()
        .setApplicationName(EdgeOpcUaCommon.DEFAULT_SERVER_APP_NAME.getValue())
        .setApplicationUri(EdgeOpcUaCommon.DEFAULT_SERVER_APP_URI.getValue()).setViewNodeFlag(true)
        .build();
    EdgeEndpointInfo ep =
        new EdgeEndpointInfo.Builder(endpointUri).setConfig(endpointConfig).build();

    // startClient
    EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().build();
    EdgeMessage msg = new EdgeMessage.Builder(ep).setCommand(EdgeCommandType.CMD_START_CLIENT)
        .setMessageType(EdgeMessageType.SEND_REQUEST)
        .setRequest(new EdgeRequest.Builder(nodeInfo).build()).build();
    ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  private void testGetEndpoint() throws Exception {
    logger.info("testGetEndpoint()");
    EdgeEndpointInfo ep = new EdgeEndpointInfo.Builder(
        OPCUAMessageHandler.getInstance().getEndpointUrifromAddressable(addressable))
            .setFuture(null).build();
    EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().build();
    EdgeMessage msg = new EdgeMessage.Builder(ep).setCommand(EdgeCommandType.CMD_GET_ENDPOINTS)
        .setRequest(new EdgeRequest.Builder(nodeInfo).build()).build();
    ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  private void testRead() throws Exception {
    logger.info("testRead()");
    EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().setValueAlias("/1/cnc14").build();

    EdgeEndpointInfo ep = new EdgeEndpointInfo.Builder(
        OPCUAMessageHandler.getInstance().getEndpointUrifromAddressable(addressable))
            .setFuture(null).build();
    EdgeMessage msg = null;

    msg = new EdgeMessage.Builder(ep).setCommand(EdgeCommandType.CMD_READ)
        .setMessageType(EdgeMessageType.SEND_REQUEST)
        .setRequest(new EdgeRequest.Builder(nodeInfo).build()).build();

    try {
      ProtocolManager.getProtocolManagerInstance().send(msg);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void testSub() throws Exception {
    logger.info("testSub()");
    EdgeSubRequest sub = new EdgeSubRequest.Builder(EdgeNodeIdentifier.Edge_Create_Sub)
        .setSamplingInterval(100.0).build();
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias("/1/cnc14").build();
    EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(
        OPCUAMessageHandler.getInstance().getEndpointUrifromAddressable(addressable))
            .setFuture(null).build();

    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB)
        .setMessageType(EdgeMessageType.SEND_REQUEST)
        .setRequest(new EdgeRequest.Builder(ep).setSubReq(sub).build()).build();
    ProtocolManager.getProtocolManagerInstance().send(msg);
  }
}
