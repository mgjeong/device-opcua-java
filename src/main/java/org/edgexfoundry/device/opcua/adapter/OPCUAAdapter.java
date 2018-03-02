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
import org.edge.protocol.opcua.api.common.EdgeBrowseResult;
import org.edge.protocol.opcua.api.common.EdgeConfigure;
import org.edge.protocol.opcua.api.common.EdgeDevice;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edgexfoundry.device.opcua.adapter.ezmq.EZMQAdapter;
import org.edgexfoundry.device.opcua.adapter.metadata.MetaDataType;
import org.edgexfoundry.device.opcua.opcua.OPCUADriver.DriverCallback;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.ResourceOperation;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;

public class OPCUAAdapter {
  private final static EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(OPCUAAdapter.class);
  private static OPCUAAdapter singleton = null;
  private DriverCallback driverCallback = null;

  /**
   * construct OPCUAAdapter <br>
   * Use {@link org.edge.protocol.opcua.api.common.EdgeConfigure} to register callback <br>
   * Use {@link org.edge.protocol.opcua.api.ProtocolManager#configure(EdgeConfigure)} to register
   * callback <br>
   * 
   * @param callback Callback Driver
   */
  private OPCUAAdapter(DriverCallback callback) {
    this.driverCallback = callback;

    // register callback
    EdgeConfigure configure = new EdgeConfigure.Builder().setRecvCallback(receiverCallback)
        .setStatusCallback(statusCallback).setDiscoveryCallback(discoveryCallback).build();

    ProtocolManager protocolManager = ProtocolManager.getProtocolManagerInstance();
    protocolManager.configure(configure);
  }

  /**
   * Get OPCUAAdapter Instance (singleton)<br>
   * 
   * @param callback Callback Driver
   * @return OPCUAAdapter instance
   */
  public synchronized static OPCUAAdapter getInstance(DriverCallback callback) {

    if (singleton == null) {
      singleton = new OPCUAAdapter(callback);
    }

    return singleton;
  }

  /**
   * Receive data from device<br>
   * Use {@link DriverCallback#onReceive(Device, ResourceOperation, String)} to create OPCUAAdapter
   * Instance <br>
   * 
   * @param data response message data format is
   *        {@link org.edge.protocol.opcua.api.common.EdgeMessage}
   */
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
  
  public void handleMonitoringData(EdgeMessage data) {
    receive(data);
    // publish
    EZMQAdapter.getInstance().publish(data);
  }

  /**
   * handling received data called message<br>
   * 
   * @see org.edge.protocol.opcua.api.ProtocolManager.ReceivedMessageCallback
   */
  ReceivedMessageCallback receiverCallback = new ReceivedMessageCallback() {
    /**
     * handling response data called message<br>
     * Use {@link OPCUAMessageHandler#convertEdgeMessagetoEdgeElement(EdgeMessage)} to convert
     * message format <br>
     * 
     * @param data response message data format is
     *        {@link org.edge.protocol.opcua.api.common.EdgeMessage}
     */
    @Override
    public void onResponseMessages(EdgeMessage data) {
      // TODO Auto-generated method stub
      for (EdgeResponse res : data.getResponses()) {
        logger.info("[res] " + res.getMessage().getValue().toString());
      }

      CompletableFuture<String> future = data.getEdgeEndpointInfo().getFuture();
      // TODO
      // response format based command data model should be generated in this place.
      String responseFormat =
          OPCUAMessageHandler.getInstance().convertEdgeMessagetoEdgeElement(data);
      if (future != null && responseFormat != null) {
        future.complete(responseFormat);
      } else {
        logger.error("EdgeElement data of the response message is invalid");
      }
    }

    /**
     * handling monitored data called message<br>
     * the data will be published into EMFAdapter by default.<br>
     * since the data is considered as streaming data.<br>
     * Use {@link OPCUAAdapter#receive(EdgeMessage)} to publish into EMFAdapter
     * 
     * @param data response message data format is
     *        {@link org.edge.protocol.opcua.api.common.EdgeMessage}
     */
    @Override
    public void onMonitoredMessage(EdgeMessage data) {
      // TODO Auto-generated method stub
      handleMonitoringData(data);
    }

    /**
     * handling error data called message<br>
     * Use {@link OPCUAMessageHandler#convertEdgeMessagetoEdgeElement(EdgeMessage)} to convert
     * message format <br>
     * 
     * @param data response message data format is
     *        {@link org.edge.protocol.opcua.api.common.EdgeMessage}
     */
    @Override
    public void onErrorMessage(EdgeMessage data) {
      // TODO Auto-generated method stub
    }

    /**
     * handling browse data called message<br>
     * 
     * @param endpoint Endpoint data
     * @param responses list of browse result
     * @param requestId id of request
     */
    @Override
    public void onBrowseMessage(EdgeNodeInfo endpoint, List<EdgeBrowseResult> responses,
        int requestId) {
      // TODO Auto-generated method stub

    }
  };


  /**
   * handling received data called message<br>
   * 
   * @see org.edge.protocol.opcua.api.ProtocolManager.DiscoveryCallback
   */
  DiscoveryCallback discoveryCallback = new DiscoveryCallback() {
    /**
     * handling endpoint data called message<br>
     * 
     * @param device EdgeDevice data
     */
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
      String responseFormat =
          OPCUAMessageHandler.getInstance().convertEdgeDevicetoEdgeElement(device);
      if (future != null && responseFormat != null) {
        future.complete(responseFormat);
      } else {
        logger.error("EdgeElement data of the onFoundEndpoint callback is invalid");
      }
    }

    /**
     * handling Device data called message<br>
     * 
     * @param device EdgeDevice data
     */
    @Override
    public void onFoundDevice(EdgeDevice device) {
      // TODO Auto-generated method stub
    }
  };

  StatusCallback statusCallback = new StatusCallback() {
    /**
     * it will be called when server or client is started. and service providers (based Attribute,
     * Method, View Type) list will be provided.<br>
     * Use {@link OPCUAMessageHandler#getResponseElementForStart(EdgeStatusCode)} to get start
     * response
     * 
     * @param ep endpoint to start
     * @param status status code
     * @param attiributeAliasList service provider list base on Object/Variable Type
     * @param methodAliasList service provider list base on Method Type
     * @param viewAliasList service provider list base on View Type
     */
    @Override
    public void onStart(EdgeEndpointInfo ep, EdgeStatusCode status,
        List<String> attiributeAliasList, List<String> methodAliasList,
        List<String> viewAliasList) {
      // TODO Auto-generated method stub

      CompletableFuture<String> future = ep.getFuture();

      // TODO
      // response format based command data model should be generated in this place.
      String responseFormat = OPCUAMessageHandler.getInstance().getResponseElementForStart(status);
      if (future != null && responseFormat != null) {
        future.complete(responseFormat);
      } else {
        logger.error("EdgeElement data of the onStart callback is invalid");
      }

      logger.info("onStart({})", ep.getEndpointUri());

      if (status == EdgeStatusCode.STATUS_CLIENT_STARTED) {
        logger.info("onStart(STATUS_CLIENT_STARTED)");
        try {
          driverCallback.onInit();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }

    /**
     * it will be called when server or client is stopped.<br>
     * Use {@link OPCUAMessageHandler#getResponseElementForStart(EdgeStatusCode)} to get stop
     * response
     * 
     * @param ep endpoint to start
     * @param status status code
     */
    @Override
    public void onStop(EdgeEndpointInfo ep, EdgeStatusCode status) {
      CompletableFuture<String> future = ep.getFuture();

      // TODO
      // response format based command data model should be generated in this place.
      String responseFormat = OPCUAMessageHandler.getInstance().getResponseElementForStop(status);
      if (future != null && responseFormat != null) {
        future.complete(responseFormat);
      } else {
        logger.error("EdgeElement data of the onStop callback is invalid");
      }
      // TODO Auto-generated method stub
      logger.info("onStop({})", ep.getEndpointUri());
      driverCallback.onDeleteCoreData();
      driverCallback.onDeleteMetaData(MetaDataType.ALL);
    }

    /**
     * it will be called when network status is up or down.<br>
     * 
     * @param ep endpoint to start
     * @param status status code
     */
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
}
