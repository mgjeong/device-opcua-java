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

import static com.google.common.collect.Lists.newArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.ws.rs.DefaultValue;
import org.command.json.format.EdgeAttribute;
import org.command.json.format.EdgeAttributeList;
import org.command.json.format.EdgeElement;
import org.command.json.format.EdgeFormatIdentifier;
import org.command.json.format.EdgeJsonFormatter;
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
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edge.protocol.opcua.example.EdgeSampleCommon;
import org.edgexfoundry.device.opcua.DataDefaultValue;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Protocol;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;

public class OPCUAMessageHandler {
  private final static EdgeXLogger logger =
      EdgeXLoggerFactory.getEdgeXLogger(OPCUAMessageHandler.class);
  private static OPCUAMessageHandler singleton = null;

  private OPCUAMessageHandler() {}

  /**
   * @fn OPCUAMessageHandler getInstance()
   * @brief get OPCUAMessageHandler object
   * @return OPCUAMessageHandler singleton object
   */
  public static OPCUAMessageHandler getInstance() {
    if (singleton == null) {
      singleton = new OPCUAMessageHandler();
    }
    return singleton;
  }

  /**
   * @fn @EdgeMessage convertEdgeElementToEdgeMessage(@EdgeElement element, String operation, String
   *     providerKey, Addressable addr, CompletableFuture<@EdgeMessage> future)
   * @brief covert @EdgeElement to @EdgeMessage
   * @param [in] element element object of json format
   * @param [in] operation opcua command
   * @param [in] providerKey provider key which node
   * @param [in] addr addressable object
   * @param [in] future CompletableFuture object
   * @return @EdgeMessage
   */
  public EdgeMessage convertEdgeElementToEdgeMessage(EdgeElement element, String operation,
      String providerKey, Addressable addr, CompletableFuture<EdgeMessage> future)
      throws Exception {
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

  /**
   * @fn @EdgeMessage getStartMessage(@EdgeElement element, String providerKey, Addressable addr,
   *     CompletableFuture<@EdgeMessage> future)
   * @brief get @EdgeMessage which has start command
   * @param [in] element element object of json format
   * @param [in] providerKey provider key which node
   * @param [in] addr addressable object
   * @param [in] future CompletableFuture object
   * @return @EdgeMessage
   */
  private EdgeMessage getStartMessage(EdgeElement element, String providerKey, Addressable addr,
      CompletableFuture<EdgeMessage> future) throws Exception {

    EdgeEndpointConfig endpointConfig = new EdgeEndpointConfig.Builder()
        .setApplicationName(EdgeOpcUaCommon.DEFAULT_SERVER_APP_NAME.getValue())
        .setApplicationUri(EdgeOpcUaCommon.DEFAULT_SERVER_APP_URI.getValue()).build();
    EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr))
        .setConfig(endpointConfig).setFuture(future).build();

    EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().build();
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_START_CLIENT)
        .setMessageType(EdgeMessageType.SEND_REQUEST)
        .setRequest(new EdgeRequest.Builder(nodeInfo).build()).build();
    return msg;
  }

  /**
   * @fn @EdgeMessage getStoptMessage(@EdgeElement element, String providerKey, Addressable addr,
   *     CompletableFuture<@EdgeMessage> future)
   * @brief get @EdgeMessage which has stop command
   * @param [in] element element object of json format
   * @param [in] providerKey provider key which node
   * @param [in] addr addressable object
   * @param [in] future CompletableFuture object
   * @return @EdgeMessage
   */
  private EdgeMessage getStopMessage(EdgeElement element, String providerKey, Addressable addr,
      CompletableFuture<EdgeMessage> future) throws Exception {
    EdgeNodeInfo endpoint = new EdgeNodeInfo.Builder().build();
    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr)).setFuture(future).build();

    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_STOP_CLIENT)
        .setRequest(new EdgeRequest.Builder(endpoint).build()).build();
    return msg;
  }

  /**
   * @fn @EdgeMessage getReadMessage(@EdgeElement element, String providerKey, Addressable addr,
   *     CompletableFuture<@EdgeMessage> future)
   * @brief get @EdgeMessage which has read command
   * @param [in] element element object of json format
   * @param [in] providerKey provider key which node
   * @param [in] addr addressable object
   * @param [in] future CompletableFuture object
   * @return @EdgeMessage
   */
  private EdgeMessage getReadMessage(EdgeElement element, String providerKey, Addressable addr,
      CompletableFuture<EdgeMessage> future) throws Exception {
    EdgeMessage msg = null;
    if (providerKey.equals(OPCUAMessageKeyIdentifier.WELLKNOWN_COMMAND_GROUP.getValue()
        .replace(DataDefaultValue.REPLACE_DEVICE_NAME, "/")) == true) {
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
   * @fn @EdgeMessage getWriteMessage(@EdgeElement element, String providerKey, Addressable addr,
   *     CompletableFuture<@EdgeMessage> future)
   * @brief get @EdgeMessage which has write command
   * @param [in] element element object of json format
   * @param [in] providerKey provider key which node
   * @param [in] addr addressable object
   * @param [in] future CompletableFuture object
   * @return @EdgeMessage
   */
  private EdgeMessage getWriteMessage(EdgeElement element, String providerKey, Addressable addr,
      CompletableFuture<EdgeMessage> future) throws Exception {
    EdgeMessage msg = null;
    if (providerKey.equals(OPCUAMessageKeyIdentifier.WELLKNOWN_COMMAND_GROUP.getValue()
        .replace(DataDefaultValue.REPLACE_DEVICE_NAME, "/")) == true) {
      List<EdgeRequest> requests = new ArrayList<EdgeRequest>();
      getWriteRequestDeviceList(element.getEdgeAttributeList(), requests);
      EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr))
          .setFuture(future).build();
      msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
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
   * @fn @EdgeMessage getSubMessage(@EdgeElement element, String providerKey, Addressable addr,
   *     CompletableFuture<@EdgeMessage> future)
   * @brief get @EdgeMessage which has subscription command
   * @param [in] element element object of json format
   * @param [in] providerKey provider key which node
   * @param [in] addr addressable object
   * @param [in] future CompletableFuture object
   * @return @EdgeMessage
   */
  private EdgeMessage getSubMessage(EdgeElement element, String providerKey, Addressable addr,
      CompletableFuture<EdgeMessage> future) throws Exception {
    Object inputValue = EdgeJsonFormatter.getDoubleValueByName(element.getEdgeAttributeList(),
        OPCUAMessageKeyIdentifier.SAMPLING_INTERVAL.getValue());

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
   * @fn @EdgeMessage getMethodMessage(@EdgeElement element, String providerKey, Addressable addr,
   *     CompletableFuture<@EdgeMessage> future)
   * @brief get @EdgeMessage which has method command
   * @param [in] element element object of json format
   * @param [in] providerKey provider key which node
   * @param [in] addr addressable object
   * @param [in] future CompletableFuture object
   * @return @EdgeMessage
   */
  private EdgeMessage getMethodMessage(EdgeElement element, String providerKey, Addressable addr,
      CompletableFuture<EdgeMessage> future) throws Exception {
    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(getEndpointUrifromAddressable(addr)).setFuture(future).build();
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(providerKey).build();
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_METHOD)
        .setRequest(new EdgeRequest.Builder(ep)
            .setMessage(new EdgeVersatility.Builder(16.0).build()).build())
        .build();
    return msg;
  }

  /**
   * @fn @EdgeMessage getEndpointMessage(@EdgeElement element, String providerKey, Addressable addr,
   *     CompletableFuture<@EdgeMessage> future)
   * @brief get @EdgeMessage which has get-endpoint command
   * @param [in] element element object of json format
   * @param [in] providerKey provider key which node
   * @param [in] addr addressable object
   * @param [in] future CompletableFuture object
   * @return @EdgeMessage
   */
  private EdgeMessage getEndpointMessage(EdgeElement element, String providerKey, Addressable addr,
      CompletableFuture<EdgeMessage> future) throws Exception {
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
   * @fn @EdgeResult sendMessage(@EdgeMessage msg)
   * @brief send message
   * @param [in] msg @EdgeMessage
   * @return @EdgeResult
   */
  public EdgeResult sendMessage(EdgeMessage msg) throws Exception {
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
   * @fn String getEndpointUrifromAddressable(Addressable addressable)
   * @brief get endpoint URI from Addressable object
   * @param [in] addressable Addressable object
   * @return endpoint URI
   */
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

  private void getReadRequestDeviceList(List<EdgeAttribute> edgeAttributeList,
      List<EdgeRequest> requestList) {
    for (EdgeAttribute edgeAttr : edgeAttributeList) {
      if (edgeAttr.getName()
          .equals(OPCUAMessageKeyIdentifier.VALUE_DESCRIPTOR.getValue()) == true) {
        EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias((String) edgeAttr.getValue()).build();
        requestList.add(new EdgeRequest.Builder(ep).build());
      } else if (edgeAttr.getDataType()
          .equals(EdgeFormatIdentifier.ATTRIBUTES_TYPE.getValue()) == true) {
        if(edgeAttr.getValue() instanceof List){
          getReadRequestDeviceList((List<EdgeAttribute>)edgeAttr.getValue(), requestList); 
        } 
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void getWriteRequestDeviceList(List<EdgeAttribute> edgeAttributeList,
      List<EdgeRequest> requestList) {
    String valueDescriptorName = null;
    EdgeVersatility param = null;
    for (EdgeAttribute edgeAttr : edgeAttributeList) {
      if (edgeAttr.getName()
          .equals(OPCUAMessageKeyIdentifier.VALUE_DESCRIPTOR.getValue()) == true) {
        valueDescriptorName = (String) edgeAttr.getValue();
      } else if (edgeAttr.getName()
          .equals(OPCUAMessageKeyIdentifier.INPUT_ARGUMENT.getValue()) == true) {
        param = new EdgeVersatility.Builder(edgeAttr.getValue()).build();
      } else if (edgeAttr.getDataType()
          .equals(EdgeFormatIdentifier.ATTRIBUTES_TYPE.getValue()) == true) {
        
        if(edgeAttr.getValue() instanceof List){
          
          getWriteRequestDeviceList((List<EdgeAttribute>)edgeAttr.getValue(), requestList); 
        } 
      }
    }
    if (valueDescriptorName != null) {
      EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(valueDescriptorName).build();
      requestList.add(new EdgeRequest.Builder(ep).setMessage(param).build());
    }
  }
  
  private static List<EdgeAttribute> covertAttrubiteListFromObject(Object obj) {
    ArrayList arr = (ArrayList) obj;
    List<EdgeAttribute> attributeList = new ArrayList<EdgeAttribute>();
    for (int i = 0; i < arr.size(); i++) {
      attributeList.add((EdgeAttribute) arr.get(i));
    }
    return attributeList;
  }
}
