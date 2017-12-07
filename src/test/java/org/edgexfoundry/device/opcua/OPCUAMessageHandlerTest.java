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
import org.edgexfoundry.device.opcua.adapter.OPCUAMessageHandler;
import org.edgexfoundry.device.opcua.adapter.metadata.OPCUADefaultMetaData;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Protocol;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OPCUAMessageHandlerTest {
  private final Logger logger = LoggerFactory.getLogger(getClass());

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

    String element = OPCUAMessageHandler.getInstance()
        .getResponseElementForStart(null);
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

    String element = OPCUAMessageHandler.getInstance()
        .getResponseElementForStop(null);
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

    Addressable addressable = new Addressable(OPCUADefaultMetaData.NAME.getValue(), Protocol.TCP,
        OPCUADefaultMetaData.ADDRESS.getValue(), OPCUADefaultMetaData.PATH.getValue(),
        OPCUADefaultMetaData.ADDRESSABLE_PORT);

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

    EdgeNodeInfo endpoint = new EdgeNodeInfo.Builder().build();
    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue()).build();
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_START_CLIENT)
        .setRequest(new EdgeRequest.Builder(endpoint).build()).build();
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
}
