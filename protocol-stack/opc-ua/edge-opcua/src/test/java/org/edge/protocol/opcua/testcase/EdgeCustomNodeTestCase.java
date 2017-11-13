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

package org.edge.protocol.opcua.testcase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeCommandType;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeRequest;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edge.protocol.opcua.api.server.EdgeNodeItem;
import org.edge.protocol.opcua.api.server.EdgeNodeType;
import org.edge.protocol.opcua.example.EdgeSampleCommon;
import org.edge.protocol.opcua.session.EdgeOpcUaServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeCustomNodeTestCase {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final String endpointUri = EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue();
  private EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(endpointUri).build();

  public void testInitCustomNode(EdgeOpcUaServer server) throws Exception {
    server.createNamespace(EdgeOpcUaCommon.DEFAULT_NAMESPACE.getValue(),
        EdgeOpcUaCommon.DEFAULT_ROOT_NODE_INFO.getValue(),
        EdgeOpcUaCommon.DEFAULT_ROOT_NODE_INFO.getValue(),
        EdgeOpcUaCommon.DEFAULT_ROOT_NODE_INFO.getValue());

    String namespace = EdgeOpcUaCommon.DEFAULT_NAMESPACE.getValue();
    EdgeNodeItem item = new EdgeNodeItem.Builder(EdgeSampleCommon.SERVER_NODE_LINE1.getValue())
        .setEdgeNodeType(EdgeIdentifier.MILTI_FOLDER_NODE_TYPE)
        .setVariableItemSet(EdgeSampleCommon.LINE_NODES).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.SERVER_NODE_ROBOT.getValue())
        .setEdgeNodeType(EdgeIdentifier.VARIABLE_NODE)
        .setVariableItemSet(EdgeSampleCommon.ROBOT_NODES).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.SERVER_NODE_GLASS.getValue())
        .setEdgeNodeType(EdgeIdentifier.SINGLE_FOLDER_NODE_TYPE)
        .setAccessLevel(EdgeIdentifier.getAccessLevel(EdgeIdentifier.READ_ONLY))
        .setVariableItemSet(EdgeSampleCommon.GLASS_VARIABLE_NODES).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    logger.info("[PASS] : testInitCustomNode");
  }

  public void testRead() throws Exception {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_LINE_CNC14.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg =
        new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
            .setRequest(new EdgeRequest.Builder(ep).build()).build();
    assertNotNull(msg);

    logger.info("[RUN] : testRead - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_OK, ret.getStatusCode());
    logger.info("[PASS] : testRead");
  }

  public void testReadWithoutEndpoint() throws Exception {
    EdgeMessage msg =
        new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
            .setRequest(new EdgeRequest.Builder(null).build()).build();
    assertNotNull(msg);

    logger.info("[RUN] : testReadWithoutEndpoint - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testReadWithoutEndpoint");
  }

  public void testReadWithoutCommand() throws Exception {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_LINE_CNC14.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(null)
        .setRequest(new EdgeRequest.Builder(ep).build()).build();
    assertNotNull(msg);

    logger.info("[RUN] : testReadWithoutCommand - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testReadWithoutCommand");
  }

  public void testReadWithoutValueAilas() throws Exception {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(null).build();
    assertNotNull(ep);

    EdgeMessage msg =
        new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
            .setRequest(new EdgeRequest.Builder(ep).build()).build();
    assertNotNull(msg);

    logger
        .info("[RUN] : testReadWithoutValueAilas - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testReadWithoutValueAilas");
  }

  public void testReadWithoutMessage() throws Exception {
    logger.info("[RUN] : testReadWithoutMessage");
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(null);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testReadWithoutMessage");
  }

  public void testWrite() throws Exception {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_LINE_CNC14.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg = new EdgeMessage.Builder(epInfo)
        .setCommand(EdgeCommandType.CMD_WRITE).setRequest(new EdgeRequest.Builder(ep)
            .setMessage(new EdgeVersatility.Builder("OFF").build()).build())
        .build();
    assertNotNull(msg);

    logger.info("[RUN] : testWrite - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_OK, ret.getStatusCode());
    logger.info("[PASS] : testWrite");
  }

  public void testWriteWithoutEndpoint() throws Exception {
    EdgeMessage msg = new EdgeMessage.Builder(epInfo)
        .setCommand(EdgeCommandType.CMD_WRITE).setRequest(new EdgeRequest.Builder(null)
            .setMessage(new EdgeVersatility.Builder("OFF").build()).build())
        .build();
    assertNotNull(msg);

    logger
        .info("[RUN] : testWriteWithoutEndpoint - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testWriteWithoutEndpoint");
  }

  public void testWriteWithoutValueAilas() throws Exception {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(null).build();
    assertNotNull(ep);

    EdgeMessage msg = new EdgeMessage.Builder(epInfo)
        .setCommand(EdgeCommandType.CMD_WRITE).setRequest(new EdgeRequest.Builder(ep)
            .setMessage(new EdgeVersatility.Builder("OFF").build()).build())
        .build();
    assertNotNull(msg);

    logger.info(
        "[RUN] : testWriteWithoutValueAilas - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testWriteWithoutValueAilas");
  }

  public void testWriteWithoutCommand() throws Exception {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_LINE_CNC14.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(null).setRequest(
        new EdgeRequest.Builder(ep).setMessage(new EdgeVersatility.Builder("OFF").build()).build())
        .build();
    assertNotNull(msg);

    logger.info("[RUN] : testWriteWithoutCommand - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testWriteWithoutCommand");
  }

  public void testWriteWithoutMessage() throws Exception {
    logger.info("[RUN] : testWriteWithoutMessage");
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(null);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testWriteWithoutMessage");
  }
}
