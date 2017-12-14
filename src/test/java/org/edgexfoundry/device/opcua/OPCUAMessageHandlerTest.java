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
package org.edgexfoundry.device.opcua;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import org.command.json.format.EdgeAttribute;
import org.command.json.format.EdgeElement;
import org.command.json.format.EdgeFormatIdentifier;
import org.edge.protocol.opcua.api.client.EdgeResponse;
import org.edge.protocol.opcua.api.common.EdgeCommandType;
import org.edge.protocol.opcua.api.common.EdgeDevice;
import org.edge.protocol.opcua.api.common.EdgeEndpointConfig;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeRequest;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edge.protocol.opcua.example.EdgeSampleCommon;
import org.edgexfoundry.device.opcua.adapter.OPCUAMessageHandler;
import org.edgexfoundry.device.opcua.adapter.metadata.OPCUACommandIdentifier;
import org.edgexfoundry.device.opcua.adapter.metadata.OPCUADefaultMetaData;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Protocol;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OPCUAMessageHandlerTest {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  Addressable addressable = new Addressable(OPCUADefaultMetaData.NAME.getValue(), Protocol.TCP,
      OPCUADefaultMetaData.ADDRESS.getValue(), OPCUADefaultMetaData.PATH.getValue(),
      OPCUADefaultMetaData.ADDRESSABLE_PORT);
  String providerKey = "/1/cnc100";

  @Test
  public void test_convertEdgeDevicetoEdgeElement_without_EdgeDevice() throws Exception {
    logger.info("[RUN] test_convertEdgeDevicetoEdgeElement_without_EdgeDevice");
    EdgeDevice device = null;
    String element = OPCUAMessageHandler.getInstance().convertEdgeDevicetoEdgeElement(device);
    assertNull(element);
    logger.info("[PASS] test_convertEdgeDevicetoEdgeElement_without_EdgeDevice");
  }

  @Test
  public void test_convertEdgeDevicetoEdgeElement_with_EdgeDevice() throws Exception {
    logger.info("[RUN] test_convertEdgeDevicetoEdgeElement_with_EdgeDevice");
    ArrayList<EdgeEndpointInfo> endpointList = new ArrayList<EdgeEndpointInfo>();
    endpointList.add(new EdgeEndpointInfo.Builder(EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue())
        .setConfig(new EdgeEndpointConfig.Builder().setSecurityPolicyUri("RSA128").build())
        .build());

    EdgeDevice device =
        new EdgeDevice.Builder(EdgeOpcUaCommon.WELL_KNOWN_LOCALHOST_ADDRESS.getValue(), 12686)
            .setServerName(EdgeOpcUaCommon.DEFAULT_SERVER_NAME.getValue())
            .setEndpoints(endpointList).build();
    String element = OPCUAMessageHandler.getInstance().convertEdgeDevicetoEdgeElement(device);
    assertNotNull(element);
    logger.info("[PASS] test_convertEdgeDevicetoEdgeElement_with_EdgeDevice");
  }

  @Test
  public void test_convertEdgeMessagetoEdgeElement_without_EdgeMessage() throws Exception {
    logger.info("[RUN] test_convertEdgeMessagetoEdgeElement_without_EdgeMessage");

    EdgeMessage msg = null;
    String element = OPCUAMessageHandler.getInstance().convertEdgeMessagetoEdgeElement(msg);
    assertNull(element);
    logger.info("[PASS] test_convertEdgeMessagetoEdgeElement_without_EdgeMessage");
  }

  @Test
  public void test_getResponseElementForStart_with_status() throws Exception {
    logger.info("[RUN] test_getResponseElementForStart_with_status");

    String element = OPCUAMessageHandler.getInstance()
        .getResponseElementForStart(EdgeStatusCode.STATUS_CLIENT_STARTED);
    assertNotNull(element);
    logger.info("[PASS] test_getResponseElementForStart_with_status");
  }

  @Test
  public void test_getResponseElementForStart_without_status() throws Exception {
    logger.info("[RUN] test_getResponseElementForStart_without_status");

    String element = OPCUAMessageHandler.getInstance().getResponseElementForStart(null);
    assertNotNull(element);
    logger.info("[PASS] test_getResponseElementForStart_without_status");
  }

  @Test
  public void test_getResponseElementForStop_with_status() throws Exception {
    logger.info("[RUN] test_getResponseElementForStop_with_status");

    String element = OPCUAMessageHandler.getInstance()
        .getResponseElementForStop(EdgeStatusCode.STATUS_STOP_CLIENT);
    assertNotNull(element);
    logger.info("[PASS] test_getResponseElementForStop_with_status");
  }

  @Test
  public void test_getResponseElementForStop_without_status() throws Exception {
    logger.info("[RUN] test_getResponseElementForStop_without_status");

    String element = OPCUAMessageHandler.getInstance().getResponseElementForStop(null);
    assertNotNull(element);
    logger.info("[PASS] test_getResponseElementForStop_without_status");
  }

  @Test
  public void test_convertEdgeMessagetoEdgeElement_with_read_EdgeMessage() throws Exception {
    logger.info("[RUN] test_convertEdgeMessagetoEdgeElement_with_read_EdgeMessage");

    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue()).build();
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ).build();
    String element = OPCUAMessageHandler.getInstance().convertEdgeMessagetoEdgeElement(msg);
    assertNotNull(element);
    logger.info("[PASS] test_convertEdgeMessagetoEdgeElement_with_read_EdgeMessage");
  }

  @Test
  public void test_convertEdgeMessagetoEdgeElement_with_read_responses() throws Exception {
    logger.info("[RUN] test_convertEdgeMessagetoEdgeElement_with_read_responses");

    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue()).build();
    EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().setValueAlias(providerKey).build();
    EdgeResponse res = new EdgeResponse.Builder(nodeInfo, EdgeOpcUaCommon.DEFAULT_REQUEST_ID)
        .setMessage(new EdgeVersatility.Builder(100).build()).build();
    ArrayList<EdgeResponse> responses = new ArrayList<EdgeResponse>();
    responses.add(res);
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
        .setResponses(responses).build();
    String element = OPCUAMessageHandler.getInstance().convertEdgeMessagetoEdgeElement(msg);
    assertNotNull(element);
    logger.info("[PASS] test_convertEdgeMessagetoEdgeElement_with_read_responses");
  }

  @Test
  public void test_convertEdgeMessagetoEdgeElement_with_write_EdgeMessage() throws Exception {
    logger.info("[RUN] test_convertEdgeMessagetoEdgeElement_with_write_EdgeMessage");

    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue()).build();
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_WRITE).build();
    String element = OPCUAMessageHandler.getInstance().convertEdgeMessagetoEdgeElement(msg);
    assertNotNull(element);
    logger.info("[PASS] test_convertEdgeMessagetoEdgeElement_with_write_EdgeMessage");
  }

  @Test
  public void test_convertEdgeMessagetoEdgeElement_with_sub_EdgeMessage() throws Exception {
    logger.info("[RUN] test_convertEdgeMessagetoEdgeElement_with_sub_EdgeMessage");

    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue()).build();
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB).build();
    String element = OPCUAMessageHandler.getInstance().convertEdgeMessagetoEdgeElement(msg);
    assertNotNull(element);
    logger.info("[PASS] test_convertEdgeMessagetoEdgeElement_with_sub_EdgeMessage");
  }

  @Test
  public void test_convertEdgeMessagetoEdgeElement_with_sub_responses() throws Exception {
    logger.info("[RUN] test_convertEdgeMessagetoEdgeElement_with_sub_responses");

    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue()).build();
    EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().setValueAlias(providerKey).build();
    EdgeResponse res = new EdgeResponse.Builder(nodeInfo, EdgeOpcUaCommon.DEFAULT_REQUEST_ID)
        .setMessage(new EdgeVersatility.Builder(100).build()).build();
    ArrayList<EdgeResponse> responses = new ArrayList<EdgeResponse>();
    responses.add(res);
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB)
        .setResponses(responses).build();
    String element = OPCUAMessageHandler.getInstance().convertEdgeMessagetoEdgeElement(msg);
    assertNotNull(element);
    logger.info("[PASS] test_convertEdgeMessagetoEdgeElement_with_sub_responses");
  }

  @Test
  public void test_convertEdgeMessagetoEdgeElement_with_method_EdgeMessage() throws Exception {
    logger.info("[RUN] test_convertEdgeMessagetoEdgeElement_with_method_EdgeMessage");

    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue()).build();
    EdgeMessage msg =
        new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_METHOD).build();
    String element = OPCUAMessageHandler.getInstance().convertEdgeMessagetoEdgeElement(msg);
    assertNotNull(element);
    logger.info("[PASS] test_convertEdgeMessagetoEdgeElement_with_method_EdgeMessage");
  }

  @Test
  public void test_convertEdgeMessagetoEdgeElement_with_getendpoint_EdgeMessage() throws Exception {
    logger.info("[RUN] test_convertEdgeMessagetoEdgeElement_with_getendpoint_EdgeMessage");

    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue()).build();
    EdgeMessage msg =
        new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_GET_ENDPOINTS).build();
    String element = OPCUAMessageHandler.getInstance().convertEdgeMessagetoEdgeElement(msg);
    assertNull(element);
    logger.info("[PASS] test_convertEdgeMessagetoEdgeElement_with_getendpoint_EdgeMessage");
  }

  @Test
  public void test_getEndpointUrifromAddressable_with_Addressable() throws Exception {
    logger.info("[RUN] test_getEndpointUrifromAddressable_with_Addressable");

    String endpointURI =
        OPCUAMessageHandler.getInstance().getEndpointUrifromAddressable(addressable);
    assertNotNull(endpointURI);
    logger.info("[PASS] test_getEndpointUrifromAddressable_with_Addressable");
  }

  @Test
  public void test_getEndpointUrifromAddressable_without_Addressable() throws Exception {
    logger.info("[RUN] test_getEndpointUrifromAddressable_without_Addressable");

    Addressable addressable = null;

    String endpointURI =
        OPCUAMessageHandler.getInstance().getEndpointUrifromAddressable(addressable);
    assertNull(endpointURI);
    logger.info("[PASS] test_getEndpointUrifromAddressable_without_Addressable");
  }

  @Test
  public void test_sendMessage_with_EdgeMessage() throws Exception {
    logger.info("[RUN] test_sendMessage_with_EdgeMessage");

    EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().build();
    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue()).build();
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_START_CLIENT)
        .setRequest(new EdgeRequest.Builder(nodeInfo).build()).build();
    EdgeResult ret = OPCUAMessageHandler.getInstance().sendMessage(msg);
    assertEquals(EdgeStatusCode.STATUS_OK, ret.getStatusCode());
    logger.info("[PASS] test_sendMessage_with_EdgeMessage");
  }

  @Test
  public void test_sendMessage_without_EdgeMessage() throws Exception {
    logger.info("[RUN] test_sendMessage_without_EdgeMessage");

    EdgeMessage msg = null;
    EdgeResult ret = OPCUAMessageHandler.getInstance().sendMessage(msg);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] test_sendMessage_without_EdgeMessage");
  }

  @Test
  public void test_convertEdgeElementToEdgeMessage_read() throws Exception {
    logger.info("[RUN] test_convertEdgeElementToEdgeMessage_read");

    String operation = EdgeCommandType.CMD_READ.getValue();
    EdgeElement element = new EdgeElement(operation);
    element.getEdgeAttributeList().add(new EdgeAttribute("value_descriptor",
        EdgeFormatIdentifier.STRING_TYPE.getValue(), providerKey));

    CompletableFuture<String> future = null;
    EdgeMessage msg = OPCUAMessageHandler.getInstance().convertEdgeElementToEdgeMessage(element,
        operation, providerKey, addressable, future);
    assertNotNull(msg);
    logger.info("[PASS] test_convertEdgeElementToEdgeMessage_read");
  }

  @Test
  public void test_convertEdgeElementToEdgeMessage_group_read() throws Exception {
    logger.info("[RUN] test_convertEdgeElementToEdgeMessage_group_read");

    String operation = EdgeCommandType.CMD_READ.getValue();
    String providerKey = OPCUACommandIdentifier.WELLKNOWN_COMMAND_GROUP.getValue()
        .replace(OPCUADefaultMetaData.AFTER_REPLACE_WORD, OPCUADefaultMetaData.BEFORE_REPLACE_WORD);
    EdgeElement element = new EdgeElement(operation);
    element.getEdgeAttributeList().add(new EdgeAttribute("value_descriptor",
        EdgeFormatIdentifier.STRING_TYPE.getValue(), providerKey));

    CompletableFuture<String> future = null;
    EdgeMessage msg = OPCUAMessageHandler.getInstance().convertEdgeElementToEdgeMessage(element,
        operation, providerKey, addressable, future);
    assertNotNull(msg);
    logger.info("[PASS] test_convertEdgeElementToEdgeMessage_group_read");
  }

  @Test
  public void test_convertEdgeElementToEdgeMessage_write() throws Exception {
    logger.info("[RUN] test_convertEdgeElementToEdgeMessage_write");

    String operation = EdgeCommandType.CMD_WRITE.getValue();
    EdgeElement element = new EdgeElement(operation);
    element.getEdgeAttributeList().add(new EdgeAttribute("value_descriptor",
        EdgeFormatIdentifier.STRING_TYPE.getValue(), providerKey));
    element.getEdgeAttributeList().add(
        new EdgeAttribute("input_argument", EdgeFormatIdentifier.INTEGER_TYPE.getValue(), 100));

    CompletableFuture<String> future = null;
    EdgeMessage msg = OPCUAMessageHandler.getInstance().convertEdgeElementToEdgeMessage(element,
        operation, providerKey, addressable, future);
    assertNotNull(msg);
    logger.info("[PASS] test_convertEdgeElementToEdgeMessage_write");
  }

  @Test
  public void test_convertEdgeElementToEdgeMessage_group_write() throws Exception {
    logger.info("[RUN] test_convertEdgeElementToEdgeMessage_group_write");

    String operation = EdgeCommandType.CMD_WRITE.getValue();
    String providerKey = OPCUACommandIdentifier.WELLKNOWN_COMMAND_GROUP.getValue()
        .replace(OPCUADefaultMetaData.AFTER_REPLACE_WORD, OPCUADefaultMetaData.BEFORE_REPLACE_WORD);

    EdgeElement element = new EdgeElement(operation);
    element.getEdgeAttributeList().add(new EdgeAttribute("value_descriptor",
        EdgeFormatIdentifier.STRING_TYPE.getValue(), providerKey));
    element.getEdgeAttributeList().add(
        new EdgeAttribute("input_argument", EdgeFormatIdentifier.INTEGER_TYPE.getValue(), 100));

    CompletableFuture<String> future = null;
    EdgeMessage msg = OPCUAMessageHandler.getInstance().convertEdgeElementToEdgeMessage(element,
        operation, providerKey, addressable, future);
    assertNotNull(msg);
    logger.info("[PASS] test_convertEdgeElementToEdgeMessage_group_write");
  }

  @Test
  public void test_convertEdgeElementToEdgeMessage_sub() throws Exception {
    logger.info("[RUN] test_convertEdgeElementToEdgeMessage_sub");

    String operation = EdgeCommandType.CMD_SUB.getValue();
    EdgeElement element = new EdgeElement(operation);
    element.getEdgeAttributeList().add(
        new EdgeAttribute("sampling_interval", EdgeFormatIdentifier.DOUBLE_TYPE.getValue(), 1000.0));
    
    CompletableFuture<String> future = null;
    EdgeMessage msg = OPCUAMessageHandler.getInstance().convertEdgeElementToEdgeMessage(element,
        operation, providerKey, addressable, future);
    assertNotNull(msg);
    logger.info("[PASS] test_convertEdgeElementToEdgeMessage_sub");
  }

  @Test
  public void test_convertEdgeElementToEdgeMessage_start() throws Exception {
    logger.info("[RUN] test_convertEdgeElementToEdgeMessage_start");

    String operation = EdgeCommandType.CMD_START_CLIENT.getValue();
    EdgeElement element = new EdgeElement(operation);

    CompletableFuture<String> future = null;
    EdgeMessage msg = OPCUAMessageHandler.getInstance().convertEdgeElementToEdgeMessage(element,
        operation, providerKey, addressable, future);
    assertNotNull(msg);
    logger.info("[PASS] test_convertEdgeElementToEdgeMessage_start");
  }

  @Test
  public void test_convertEdgeElementToEdgeMessage_stop() throws Exception {
    logger.info("[RUN] test_convertEdgeElementToEdgeMessage_stop");

    String operation = EdgeCommandType.CMD_STOP_CLIENT.getValue();
    EdgeElement element = new EdgeElement(operation);

    CompletableFuture<String> future = null;
    EdgeMessage msg = OPCUAMessageHandler.getInstance().convertEdgeElementToEdgeMessage(element,
        operation, providerKey, addressable, future);
    assertNotNull(msg);
    logger.info("[PASS] test_convertEdgeElementToEdgeMessage_stop");
  }

  @Test
  public void test_convertEdgeElementToEdgeMessage_method() throws Exception {
    logger.info("[RUN] test_convertEdgeElementToEdgeMessage_method");

    String operation = EdgeCommandType.CMD_METHOD.getValue();
    EdgeElement element = new EdgeElement(operation);
    element.getEdgeAttributeList()
        .add(new EdgeAttribute("input_argument", EdgeFormatIdentifier.STRING_TYPE.getValue(), 100));

    CompletableFuture<String> future = null;
    EdgeMessage msg = OPCUAMessageHandler.getInstance().convertEdgeElementToEdgeMessage(element,
        operation, providerKey, addressable, future);
    assertNotNull(msg);
    logger.info("[PASS] test_convertEdgeElementToEdgeMessage_method");
  }

  @Test
  public void test_convertEdgeElementToEdgeMessage_getEndpoint() throws Exception {
    logger.info("[RUN] test_convertEdgeElementToEdgeMessage_getEndpoint");

    String operation = EdgeCommandType.CMD_GET_ENDPOINTS.getValue();
    EdgeElement element = new EdgeElement(operation);
    element.getEdgeAttributeList().add(new EdgeAttribute("value_descriptor",
        EdgeFormatIdentifier.STRING_TYPE.getValue(), providerKey));

    CompletableFuture<String> future = null;
    EdgeMessage msg = OPCUAMessageHandler.getInstance().convertEdgeElementToEdgeMessage(element,
        operation, providerKey, addressable, future);
    assertNotNull(msg);
    logger.info("[PASS] test_convertEdgeElementToEdgeMessage_getEndpoint");
  }

  @Test
  public void test_convertEdgeElementToEdgeMessage_invalid_operation() throws Exception {
    logger.info("[RUN] test_convertEdgeElementToEdgeMessage_invalid_operation");

    String operation = "unknown";
    EdgeElement element = new EdgeElement(operation);
    element.getEdgeAttributeList().add(new EdgeAttribute("value_descriptor",
        EdgeFormatIdentifier.STRING_TYPE.getValue(), providerKey));

    CompletableFuture<String> future = null;
    EdgeMessage msg = OPCUAMessageHandler.getInstance().convertEdgeElementToEdgeMessage(element,
        operation, providerKey, addressable, future);
    assertNull(msg);
    logger.info("[PASS] test_convertEdgeElementToEdgeMessage_invalid_operation");
  }

}
