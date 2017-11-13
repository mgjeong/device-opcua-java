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
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeNodeId;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeRequest;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeServerReadTestCase {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final String endpointUri = EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue();
  private EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(endpointUri).build();

  public void testReadServerStatus() throws Exception {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setEdgeNodeId(new EdgeNodeId.Builder(EdgeNodeIdentifier.Server_ServerStatus_State).build())
        .setValueAlias(EdgeOpcUaCommon.WELL_KNOWN_SERVER_NODE.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg =
        new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
            .setRequest(new EdgeRequest.Builder(ep).build()).build();
    assertNotNull(msg);

    logger.info("[RUN] : testReadServerStatus - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_OK, ret.getStatusCode());
    logger.info("[PASS] : testReadServerStatus");
  }

  public void testReadServerStatusWithoutValueAilas() throws Exception {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setEdgeNodeId(new EdgeNodeId.Builder(EdgeNodeIdentifier.Server_ServerStatus_State).build())
        .setValueAlias(null).build();
    assertNotNull(ep);

    EdgeMessage msg =
        new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
            .setRequest(new EdgeRequest.Builder(ep).build()).build();
    assertNotNull(msg);

    logger.info("[RUN] : testReadServerStatusWithoutValueAilas - requestID : "
        + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testReadServerStatusWithoutValueAilas");
  }

  public void testReadServerWithoutEndpoint() throws Exception {
    EdgeMessage msg =
        new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
            .setRequest(new EdgeRequest.Builder(null).build()).build();
    assertNotNull(msg);

    logger.info(
        "[RUN] : testReadServerWithoutEndpoint - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testReadServerWithoutEndpoint");
  }

  public void testReadServerWithoutCommand() throws Exception {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setEdgeNodeId(new EdgeNodeId.Builder(EdgeNodeIdentifier.Server_ServerStatus_State).build())
        .setValueAlias(EdgeOpcUaCommon.WELL_KNOWN_SERVER_NODE.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(null)
        .setRequest(new EdgeRequest.Builder(ep).build()).build();
    assertNotNull(msg);

    logger.info(
        "[RUN] : testReadServerWithoutCommand - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testReadServerWithoutCommand");
  }

  public void testReadServerWithoutMessageType() throws Exception {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setEdgeNodeId(new EdgeNodeId.Builder(EdgeNodeIdentifier.Server_ServerStatus_State).build())
        .setValueAlias(EdgeOpcUaCommon.WELL_KNOWN_SERVER_NODE.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg =
        new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
            .setMessageType(null).setRequest(new EdgeRequest.Builder(ep).build()).build();
    assertNotNull(msg);

    logger.info(
        "[RUN] : testReadServerWithoutCommand - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testReadServerWithoutCommand");
  }

  public void testReadServerWithoutMessage() throws Exception {
    logger.info("[RUN] : testReadServerWithoutMessage");
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(null);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testReadServerWithoutMessage");
  }
}
