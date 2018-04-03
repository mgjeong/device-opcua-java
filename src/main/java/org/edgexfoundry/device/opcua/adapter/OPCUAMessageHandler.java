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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.command.json.format.EdgeAttribute;
import org.command.json.format.EdgeElement;
import org.command.json.format.EdgeErrorIdentifier;
import org.command.json.format.EdgeFormatIdentifier;
import org.command.json.format.EdgeJsonFormatter;
import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.client.EdgeResponse;
import org.edge.protocol.opcua.api.client.EdgeSubRequest;
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
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edge.protocol.opcua.queue.ErrorHandler;
import org.edgexfoundry.device.opcua.adapter.metadata.OPCUACommandIdentifier;
import org.edgexfoundry.device.opcua.adapter.metadata.OPCUADefaultMetaData;
import org.edgexfoundry.device.opcua.opcua.OPCUADriver.DriverCallback;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.Protocol;
import org.edgexfoundry.domain.meta.ResourceOperation;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;

public class OPCUAMessageHandler {
  private final static EdgeXLogger logger =
      EdgeXLoggerFactory.getEdgeXLogger(OPCUAMessageHandler.class);
  private static OPCUAMessageHandler singleton = null;

  /**
   * construct OPCUAMessageHandler
   */
  private OPCUAMessageHandler() {}

  /**
   * Get OPCUAMessageHandler Instance (singleton)<br>
   * Use {@link #OPCUAMessageHandler()} to create OPCUAMessageHandler Instance
   * @return OPCUAMessageHandler instance
   */
  public synchronized static OPCUAMessageHandler getInstance() {
    if (singleton == null) {
      singleton = new OPCUAMessageHandler();
    }
    return singleton;
  }

  /**
   * Convert String from EdgeMessage format to EdgeElement format<br>
   * Use {@link org.edge.protocol.opcua.api.common.EdgeCommandType} to find operation<br>
   * Use {@link org.command.json.format.EdgeElement} to convert format<br>
   * Use {@link org.command.json.format.EdgeAttribute} to convert format<br>
   * Use
   * {@link org.command.json.format.EdgeJsonFormatter#encodeEdgeElementToJsonString(EdgeElement)} to
   * encode EdgeElement to JsonString<br>
   * Use {@link #getResponseElementForSubscription(EdgeMessage)} get response subscription data<br>
   * 
   * 
   * @param msg response message data format is
   *        {@link org.edge.protocol.opcua.api.common.EdgeMessage}
   * @return EdgeElement format string if operation is included in EdgeCommandType, otherwise null
   */
  public String convertEdgeMessagetoEdgeElement(EdgeMessage msg) {
    if (msg == null) {
      return null;
    }
    EdgeCommandType operation = msg.getCommand();
    if (operation == EdgeCommandType.CMD_READ || operation == EdgeCommandType.CMD_WRITE
        || operation == EdgeCommandType.CMD_METHOD) {
      EdgeElement edgeElement = new EdgeElement(operation.getValue());
      if (msg.getResponses() == null) {
        edgeElement.getEdgeAttributeList()
            .add(new EdgeAttribute(OPCUAMessageKeyIdentifier.VALUE.getValue(),
                EdgeFormatIdentifier.STRING_TYPE.getValue(),
                EdgeErrorIdentifier.EDGE_DS_ERROR_RESPONSE_NULL.getValue()));
      } else {
        for (EdgeResponse res : msg.getResponses()) {
          List<EdgeAttribute> edgeAttributeList = new ArrayList<EdgeAttribute>();
          edgeAttributeList.add(new EdgeAttribute(
              OPCUAMessageKeyIdentifier.VALUE_DESCRIPTOR.getValue(),
              EdgeFormatIdentifier.STRING_TYPE.getValue(), res.getEdgeNodeInfo().getValueAlias()));
          edgeAttributeList.add(new EdgeAttribute(OPCUAMessageKeyIdentifier.VALUE.getValue(),
              EdgeFormatIdentifier.STRING_TYPE.getValue(), res.getMessage().getValue().toString()));
          edgeElement.getEdgeAttributeList()
              .add(new EdgeAttribute(OPCUAMessageKeyIdentifier.RESPONSE_INFO.getValue(),
                  EdgeFormatIdentifier.ATTRIBUTES_TYPE.getValue(), edgeAttributeList));
        }
      }
      return EdgeJsonFormatter.encodeEdgeElementToJsonString(edgeElement);
    } else if (operation == EdgeCommandType.CMD_SUB) {
      return getResponseElementForSubscription(msg);
    } else {
      logger.info("command is not supported = {}", msg.getCommand());
    }
    return null;
  }

  /**
   * Convert String from EdgeDevice format to EdgeElement format<br>
   * Use {@link #getResponseElementForEndpoint(EdgeDevice)} get response subscription data<br>
   * 
   * @param device response message data format is
   *        {@link org.edge.protocol.opcua.api.common.EdgeDevice}
   * @return String format based EdgeElement if EdgeDevice is not null, otherwise null
   */
  public String convertEdgeDevicetoEdgeElement(EdgeDevice device) {
    if (device == null) {
      return null;
    }

    return getResponseElementForEndpoint(device);
  }

  /**
   * Get String Response for start, format based EdgeElement<br>
   * Use {@link org.command.json.format.EdgeElement} to convert format<br>
   * Use
   * {@link org.command.json.format.EdgeJsonFormatter#encodeEdgeElementToJsonString(EdgeElement)} to
   * encode EdgeElement to JsonString
   * 
   * @param status response message status format is
   *        {@link org.edge.protocol.opcua.api.common.EdgeStatusCode}
   * @return String format based EdgeElement
   */
  public String getResponseElementForStart(EdgeStatusCode status) {
    EdgeElement edgeElement = new EdgeElement(EdgeCommandType.CMD_START_CLIENT.getValue());
    if (status == null) {
      edgeElement.getEdgeAttributeList()
          .add(new EdgeAttribute(OPCUAMessageKeyIdentifier.VALUE.getValue(),
              EdgeFormatIdentifier.STRING_TYPE.getValue(),
              EdgeErrorIdentifier.EDGE_DS_ERROR_RESPONSE_NULL.getValue()));
    } else {
      edgeElement.getEdgeAttributeList()
          .add(new EdgeAttribute(OPCUAMessageKeyIdentifier.VALUE.getValue(),
              EdgeFormatIdentifier.STRING_TYPE.getValue(), status.toString()));
    }
    return EdgeJsonFormatter.encodeEdgeElementToJsonString(edgeElement);
  }

  /**
   * Get String Response for stop, format based EdgeElement<br>
   * Use {@link org.command.json.format.EdgeElement} to convert format<br>
   * Use
   * {@link org.command.json.format.EdgeJsonFormatter#encodeEdgeElementToJsonString(EdgeElement)} to
   * encode EdgeElement to JsonString
   * 
   * @param status response message status format is
   *        {@link org.edge.protocol.opcua.api.common.EdgeStatusCode}
   * @return String format based EdgeElement
   */
  public String getResponseElementForStop(EdgeStatusCode status) {
    EdgeElement edgeElement = new EdgeElement(EdgeCommandType.CMD_STOP_CLIENT.getValue());
    if (status == null) {
      edgeElement.getEdgeAttributeList()
          .add(new EdgeAttribute(OPCUAMessageKeyIdentifier.VALUE.getValue(),
              EdgeFormatIdentifier.STRING_TYPE.getValue(),
              EdgeErrorIdentifier.EDGE_DS_ERROR_RESPONSE_NULL.getValue()));
    } else {
      edgeElement.getEdgeAttributeList()
          .add(new EdgeAttribute(OPCUAMessageKeyIdentifier.VALUE.getValue(),
              EdgeFormatIdentifier.STRING_TYPE.getValue(), status.toString()));
    }
    return EdgeJsonFormatter.encodeEdgeElementToJsonString(edgeElement);
  }

  /**
   * Get String Response for subscription, format based EdgeElement<br>
   * Use {@link org.command.json.format.EdgeElement} to convert format<br>
   * Use {@link org.command.json.format.EdgeAttribute} to convert format<br>
   * Use
   * {@link org.command.json.format.EdgeJsonFormatter#encodeEdgeElementToJsonString(EdgeElement)} to
   * encode EdgeElement to JsonString
   * 
   * @param status response message status format is
   *        {@link org.edge.protocol.opcua.api.common.EdgeMessage}
   * @return String format based EdgeElement
   */
  private String getResponseElementForSubscription(EdgeMessage msg) {
    EdgeElement edgeElement = new EdgeElement(msg.getCommand().getValue());
    if (msg.getResponses() == null) {
      edgeElement.getEdgeAttributeList()
          .add(new EdgeAttribute(OPCUAMessageKeyIdentifier.VALUE.getValue(),
              EdgeFormatIdentifier.STRING_TYPE.getValue(),
              EdgeErrorIdentifier.EDGE_DS_ERROR_RESPONSE_NULL.getValue()));
    } else {
      for (EdgeResponse res : msg.getResponses()) {
        List<EdgeAttribute> edgeAttributeList = new ArrayList<EdgeAttribute>();
        edgeAttributeList.add(new EdgeAttribute(
            OPCUAMessageKeyIdentifier.VALUE_DESCRIPTOR.getValue(),
            EdgeFormatIdentifier.STRING_TYPE.getValue(), res.getEdgeNodeInfo().getValueAlias()));
        edgeAttributeList.add(new EdgeAttribute(OPCUAMessageKeyIdentifier.VALUE.getValue(),
            EdgeFormatIdentifier.STRING_TYPE.getValue(), res.getMessage().getValue().toString()));
        edgeElement.getEdgeAttributeList()
            .add(new EdgeAttribute(OPCUAMessageKeyIdentifier.RESPONSE_INFO.getValue(),
                EdgeFormatIdentifier.ATTRIBUTES_TYPE.getValue(), edgeAttributeList));
      }
    }
    return EdgeJsonFormatter.encodeEdgeElementToJsonString(edgeElement);
  }

  /**
   * Get String Response for getEndpoint, format based EdgeElement<br>
   * Use {@link org.command.json.format.EdgeElement} to convert format<br>
   * Use {@link org.command.json.format.EdgeAttribute} to convert format<br>
   * Use
   * {@link org.command.json.format.EdgeJsonFormatter#encodeEdgeElementToJsonString(EdgeElement)} to
   * encode EdgeElement to JsonString
   * 
   * @param status response message status format is
   *        {@link org.edge.protocol.opcua.api.common.EdgeDevice}
   * @return String format based EdgeElement
   */
  private String getResponseElementForEndpoint(EdgeDevice device) {
    EdgeElement edgeElement = new EdgeElement(EdgeCommandType.CMD_GET_ENDPOINTS.getValue());
    for (EdgeEndpointInfo ep : device.getEndpoints()) {
      List<EdgeAttribute> edgeAttributeList = new ArrayList<EdgeAttribute>();
      edgeAttributeList.add(new EdgeAttribute(OPCUAMessageKeyIdentifier.ENDPOINT_URI.getValue(),
          EdgeFormatIdentifier.STRING_TYPE.getValue(), ep.getEndpointUri()));
      edgeAttributeList.add(new EdgeAttribute(OPCUAMessageKeyIdentifier.APPLICATION_NAME.getValue(),
          EdgeFormatIdentifier.STRING_TYPE.getValue(), ep.getConfig().getApplicationName()));
      edgeAttributeList.add(new EdgeAttribute(OPCUAMessageKeyIdentifier.APPLICATION_URI.getValue(),
          EdgeFormatIdentifier.STRING_TYPE.getValue(), ep.getConfig().getApplicationUri()));
      edgeAttributeList
          .add(new EdgeAttribute(OPCUAMessageKeyIdentifier.SECURITY_POLICY_URI.getValue(),
              EdgeFormatIdentifier.STRING_TYPE.getValue(), ep.getConfig().getSecurityPolicyUri()));

      edgeElement.getEdgeAttributeList()
          .add(new EdgeAttribute(OPCUAMessageKeyIdentifier.ENDPOINT_INFOMATION.getValue(),
              EdgeFormatIdentifier.ATTRIBUTES_TYPE.getValue(), edgeAttributeList));
    }
    return EdgeJsonFormatter.encodeEdgeElementToJsonString(edgeElement);
  }

  /**
   * Convert command message from EdgeElement format to EdgeMessage format<br>
   * Use {@link #getStartMessage(EdgeElement, String, Addressable, CompletableFuture)} to convert
   * format<br>
   * Use {@link #getStopMessage(EdgeElement, String, Addressable, CompletableFuture)} to convert
   * format<br>
   * Use {@link #getReadMessage(EdgeElement, String, Addressable, CompletableFuture)} to convert
   * format<br>
   * Use {@link #getWriteMessage(EdgeElement, String, Addressable, CompletableFuture)} to convert
   * format<br>
   * Use {@link #getSubMessage(EdgeElement, String, Addressable, CompletableFuture)} to convert
   * format<br>
   * Use {@link #getMethodMessage(EdgeElement, String, Addressable, CompletableFuture)} to convert
   * format<br>
   * Use {@link #getEndpointMessage(EdgeElement, String, Addressable, CompletableFuture)} to convert
   * format<br>
   * 
   * @param element element object of json format
   * @param operation opcua command
   * @param providerKey provider key which node
   * @param addr addressable object
   * @param future CompletableFuture object
   * 
   * @throws Exception if convert fail
   * 
   * @return command message format is {@link org.edge.protocol.opcua.api.common.EdgeMessage}
   */
  public EdgeMessage convertEdgeElementToEdgeMessage(EdgeElement element, String operation,
      String providerKey, Addressable addr, CompletableFuture<String> future) throws Exception {
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
      logger.info("operation is not supported = {}", operation);
    }
    return msg;
  }

   /**
   * Get start command message from EdgeElement format<br>
   * Use {@link org.command.json.format.EdgeJsonFormatter#getStringValueByName(List, String)} to get
   * value from EdgeAttributes<br>
   * Use {@link #getEndpointUrifromAddressable(Addressable)} to get addressable
   * 
   * @param element element object of json format
   * @param providerKey provider key which node
   * @param addr addressable object
   * @param future CompletableFuture object
   * @return command message format is {@link org.edge.protocol.opcua.api.common.EdgeMessage}
   */
  private EdgeMessage getStartMessage(EdgeElement element, String providerKey, Addressable addr,
      CompletableFuture<String> future) throws Exception {
    String applicationName = EdgeJsonFormatter.getStringValueByName(element.getEdgeAttributeList(),
        OPCUAMessageKeyIdentifier.APPLICATION_NAME.getValue());
    String applicationUri = EdgeJsonFormatter.getStringValueByName(element.getEdgeAttributeList(),
        OPCUAMessageKeyIdentifier.APPLICATION_URI.getValue());

    if (applicationName == null) {
      applicationName = EdgeOpcUaCommon.DEFAULT_SERVER_APP_NAME.getValue();
    }
    if (applicationUri == null) {
      applicationUri = EdgeOpcUaCommon.DEFAULT_SERVER_APP_URI.getValue();
    }

    EdgeEndpointConfig endpointConfig =
        new EdgeEndpointConfig.Builder().setApplicationName(applicationName)
            .setApplicationUri(applicationUri).setViewNodeFlag(true).build();
    EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr))
        .setConfig(endpointConfig).setFuture(future).build();

    EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().build();
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_START_CLIENT)
        .setMessageType(EdgeMessageType.SEND_REQUEST)
        .setRequest(new EdgeRequest.Builder(nodeInfo).build()).build();
    return msg;
  }

  /**
   * Get stop command message from EdgeElement format<br>
   * Use {@link org.command.json.format.EdgeJsonFormatter#getStringValueByName(List, String)} to get
   * value from EdgeAttributes<br>
   * Use {@link #getEndpointUrifromAddressable(Addressable)} to get addressable
   * 
   * @param element element object of json format
   * @param providerKey provider key which node
   * @param addr addressable object
   * @param future CompletableFuture object
   * @return command message format is {@link org.edge.protocol.opcua.api.common.EdgeMessage}
   */
  private EdgeMessage getStopMessage(EdgeElement element, String providerKey, Addressable addr,
      CompletableFuture<String> future) throws Exception {

    EdgeNodeInfo endpoint = new EdgeNodeInfo.Builder().build();
    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr)).setFuture(future).build();

    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_STOP_CLIENT)
        .setRequest(new EdgeRequest.Builder(endpoint).build()).build();
    return msg;
  }

  /**
   * Get read command message from EdgeElement format<br>
   * Use {@link org.command.json.format.EdgeJsonFormatter#getStringValueByName(List, String)} to get
   * value from EdgeAttributes<br>
   * Use {@link #getEndpointUrifromAddressable(Addressable)} to get addressable<br>
   * Use {@link #getReadRequestDeviceList(List, List)} to get list of request
   * 
   * @param element element object of json format
   * @param providerKey provider key which node
   * @param addr addressable object
   * @param future CompletableFuture object
   * @return command message format is {@link org.edge.protocol.opcua.api.common.EdgeMessage}
   */
  private EdgeMessage getReadMessage(EdgeElement element, String providerKey, Addressable addr,
      CompletableFuture<String> future) throws Exception {
    EdgeMessage msg = null;
    if (providerKey.equals(OPCUACommandIdentifier.WELLKNOWN_COMMAND_GROUP.getValue().replace(
        OPCUADefaultMetaData.AFTER_REPLACE_WORD,
        OPCUADefaultMetaData.BEFORE_REPLACE_WORD)) == true) {
      List<EdgeRequest> requests = new ArrayList<EdgeRequest>();
      getReadRequestDeviceList(element.getEdgeAttributeList(), requests);

      EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr))
          .setFuture(future).build();
      msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
          .setMessageType(EdgeMessageType.SEND_REQUESTS).setRequests(requests).build();
    } else {
      EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().setValueAlias(providerKey).build();
      EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr))
          .setFuture(future).build();
      msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
          .setMessageType(EdgeMessageType.SEND_REQUEST)
          .setRequest(new EdgeRequest.Builder(nodeInfo).build()).build();
    }
    return msg;
  }

  /**
   * Get write command message from EdgeElement format<br>
   * Use {@link org.command.json.format.EdgeJsonFormatter#getStringValueByName(List, String)} to get
   * value from EdgeAttributes<br>
   * Use {@link #getEndpointUrifromAddressable(Addressable)} to get addressable<br>
   * Use {@link #getReadRequestDeviceList(List, List)} to get list of request
   * 
   * @param element element object of json format
   * @param providerKey provider key which node
   * @param addr addressable object
   * @param future CompletableFuture object
   * @return command message format is {@link org.edge.protocol.opcua.api.common.EdgeMessage}
   */
  private EdgeMessage getWriteMessage(EdgeElement element, String providerKey, Addressable addr,
      CompletableFuture<String> future) throws Exception {
    EdgeMessage msg = null;
    if (providerKey.equals(OPCUACommandIdentifier.WELLKNOWN_COMMAND_GROUP.getValue().replace(
        OPCUADefaultMetaData.AFTER_REPLACE_WORD,
        OPCUADefaultMetaData.BEFORE_REPLACE_WORD)) == true) {
      List<EdgeRequest> requests = new ArrayList<EdgeRequest>();
      getWriteRequestDeviceList(element.getEdgeAttributeList(), requests);
      EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr))
          .setFuture(future).build();
      msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_WRITE)
          .setMessageType(EdgeMessageType.SEND_REQUESTS).setRequests(requests).build();
    } else {

      Object inputValue = EdgeJsonFormatter.getObjectValueByName(element.getEdgeAttributeList(),
          OPCUAMessageKeyIdentifier.INPUT_ARGUMENT.getValue());
      EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().setValueAlias(providerKey).build();
      EdgeVersatility param = new EdgeVersatility.Builder(inputValue).build();

      EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr))
          .setFuture(future).build();
      msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_WRITE)
          .setMessageType(EdgeMessageType.SEND_REQUEST)
          .setRequest(new EdgeRequest.Builder(nodeInfo).setMessage(param).build()).build();
    }
    return msg;
  }

  /**
   * Get sub command message from EdgeElement format<br>
   * Use {@link org.command.json.format.EdgeJsonFormatter#getStringValueByName(List, String)} to get
   * value from EdgeAttributes<br>
   * Use {@link #getEndpointUrifromAddressable(Addressable)} to get addressable
   * 
   * @param element element object of json format
   * @param providerKey provider key which node
   * @param addr addressable object
   * @param future CompletableFuture object
   * @return command message format is {@link org.edge.protocol.opcua.api.common.EdgeMessage}
   */
  private EdgeMessage getSubMessage(EdgeElement element, String providerKey, Addressable addr,
      CompletableFuture<String> future) throws Exception {
    Object inputValue = EdgeJsonFormatter.getDoubleValueByName(element.getEdgeAttributeList(),
        OPCUAMessageKeyIdentifier.SAMPLING_INTERVAL.getValue());

    if(!Optional.ofNullable(inputValue).isPresent()) {
        throw new Exception("The parameter is not valid");
    }

    EdgeSubRequest sub = new EdgeSubRequest.Builder(EdgeNodeIdentifier.Edge_Create_Sub)
        .setSamplingInterval((Double) inputValue).build();
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(providerKey).build();
    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr)).setFuture(future).build();

    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB)
        .setMessageType(EdgeMessageType.SEND_REQUEST)
        .setRequest(new EdgeRequest.Builder(ep).setSubReq(sub).build()).build();

    return msg;
  }

  /**
   * Get method command message from EdgeElement format<br>
   * Use {@link org.command.json.format.EdgeJsonFormatter#getStringValueByName(List, String)} to get
   * value from EdgeAttributes<br>
   * Use {@link #getEndpointUrifromAddressable(Addressable)} to get addressable
   * 
   * @param element element object of json format
   * @param providerKey provider key which node
   * @param addr addressable object
   * @param future CompletableFuture object
   * @return command message format is {@link org.edge.protocol.opcua.api.common.EdgeMessage}
   */
  private EdgeMessage getMethodMessage(EdgeElement element, String providerKey, Addressable addr,
      CompletableFuture<String> future) throws Exception {
    Object inputValue = EdgeJsonFormatter.getObjectValueByName(element.getEdgeAttributeList(),
        OPCUAMessageKeyIdentifier.INPUT_ARGUMENT.getValue());

    if(!Optional.ofNullable(inputValue).isPresent()) {
        throw new Exception("The parameter is not valid");
    }

    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr)).setFuture(future).build();
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(providerKey).build();
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_METHOD)
        .setRequest(new EdgeRequest.Builder(ep)
            .setMessage(new EdgeVersatility.Builder(inputValue).build()).build())
        .build();
    return msg;
  }

  /**
   * Get getEndpoint command message from EdgeElement format<br>
   * Use {@link org.command.json.format.EdgeJsonFormatter#getStringValueByName(List, String)} to get
   * value from EdgeAttributes<br>
   * Use {@link #getEndpointUrifromAddressable(Addressable)} to get addressable
   * 
   * @param element element object of json format
   * @param providerKey provider key which node
   * @param addr addressable object
   * @param future CompletableFuture object
   * @return command message format is {@link org.edge.protocol.opcua.api.common.EdgeMessage}
   */
  private EdgeMessage getEndpointMessage(EdgeElement element, String providerKey, Addressable addr,
      CompletableFuture<String> future) throws Exception {

    EdgeEndpointConfig config = new EdgeEndpointConfig.Builder()
        .setApplicationName(EdgeOpcUaCommon.DEFAULT_SERVER_APP_NAME.getValue())
        .setApplicationUri(EdgeOpcUaCommon.DEFAULT_SERVER_APP_URI.getValue()).build();
    EdgeEndpointInfo ep = new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr))
        .setConfig(config).setFuture(future).build();
    EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().build();
    EdgeMessage msg = new EdgeMessage.Builder(ep).setCommand(EdgeCommandType.CMD_GET_ENDPOINTS)
        .setRequest(new EdgeRequest.Builder(nodeInfo).build()).build();
    return msg;
  }

  /**
   * Send command message and get response<br>
   * Use {@link org.edge.protocol.opcua.api.ProtocolManager#send(EdgeMessage)} to send message
   * 
   * @param msg command message
   * 
   * @throws Exception if it is fail to send and receive message
   * 
   * @return response message format is {@link org.edge.protocol.opcua.api.common.EdgeResult}
   */
  public EdgeResult sendMessage(EdgeMessage msg) throws Exception {
    if (null == msg) {
      return new EdgeResult.Builder(EdgeStatusCode.STATUS_PARAM_INVALID).build();
    }
    EdgeResult ret = null;
    try {
      ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return ret;
  }

  /**
   * Get endpoint URI from Addressable object<br>
   * Use {@link org.edgexfoundry.domain.meta.Addressable} to generate endpoint URI
   * 
   * @param addressable Device's addressable
   * @return Endpoint URI
   */
  public String getEndpointUrifromAddressable(Addressable addressable) {
    String endpointUri = "";

    if (null == addressable) {
      return null;
    }
    if (addressable.getProtocol() == Protocol.TCP) {
      endpointUri += String.format("%s", "opc.tcp://");
    } else {
      endpointUri += String.format("%s", "http://");
    }

    endpointUri += String.format("%s:%d/%s", addressable.getAddress(), addressable.getPort(),
        addressable.getPath());
    return endpointUri;
  }

  /**
   * Get list of request for read in list of EdgeAttribute<br>
   * Use {@link #getReadRequestDeviceList(List, List)} to generate list of request recursively
   * 
   * @param edgeAttributeList list of EdgeAttribute
   * @param requestList list of EdgeRequest
   */
  private void getReadRequestDeviceList(List<EdgeAttribute> edgeAttributeList,
      List<EdgeRequest> requestList) {
    for (EdgeAttribute edgeAttr : edgeAttributeList) {
      if (edgeAttr.getName()
          .equals(OPCUAMessageKeyIdentifier.VALUE_DESCRIPTOR.getValue()) == true) {
        EdgeNodeInfo ep =
            new EdgeNodeInfo.Builder().setValueAlias((String) edgeAttr.getValue()).build();
        requestList.add(new EdgeRequest.Builder(ep).build());
      } else if (edgeAttr.getDataType()
          .equals(EdgeFormatIdentifier.ATTRIBUTES_TYPE.getValue()) == true) {
        if (edgeAttr.getValue() instanceof List) {
          getReadRequestDeviceList(
              EdgeJsonFormatter.convertEdgeAttributeListFromObject(edgeAttr.getValue()),
              requestList);
        }
      }
    }
  }

  /**
   * Get list of request for write in list of EdgeAttribute<br>
   * Use {@link #getReadRequestDeviceList(List, List)} to generate list of request recursively
   * 
   * @param edgeAttributeList list of EdgeAttribute
   * @param requestList list of EdgeRequest
   */
  private void getWriteRequestDeviceList(List<EdgeAttribute> edgeAttributeList,
      List<EdgeRequest> requestList) {
    String valueDescriptorName = null;
    EdgeVersatility param = null;
    for (EdgeAttribute edgeAttr : edgeAttributeList) {
      System.out.println(EdgeJsonFormatter.encodeEdgeAttributeToJsonString(edgeAttr));
      if (edgeAttr.getName()
          .equals(OPCUAMessageKeyIdentifier.VALUE_DESCRIPTOR.getValue()) == true) {
        valueDescriptorName = (String) edgeAttr.getValue();
      } else if (edgeAttr.getName()
          .equals(OPCUAMessageKeyIdentifier.INPUT_ARGUMENT.getValue()) == true) {
        param = new EdgeVersatility.Builder(edgeAttr.getValue()).build();
      } else if (edgeAttr.getDataType()
          .equals(EdgeFormatIdentifier.ATTRIBUTES_TYPE.getValue()) == true) {
        if (edgeAttr.getValue() instanceof List) {
          getWriteRequestDeviceList(
              EdgeJsonFormatter.convertEdgeAttributeListFromObject(edgeAttr.getValue()),
              requestList);
        }
      }
    }
    if (valueDescriptorName != null) {
      EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(valueDescriptorName).build();
      requestList.add(new EdgeRequest.Builder(ep).setMessage(param).build());
    }
  }
}
