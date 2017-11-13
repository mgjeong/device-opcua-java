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
import org.edge.protocol.opcua.api.client.EdgeBrowseParameter;
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

public class EdgeBrowseTestCase {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final String endpointUri = EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue();
  private EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(endpointUri).build();

  public EdgeBrowseTestCase() {}

  public void testBrowseRootSync() throws Exception {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setEdgeNodeId(new EdgeNodeId.Builder(EdgeOpcUaCommon.SYSTEM_NAMESPACE_INDEX,
            EdgeNodeIdentifier.RootFolder).build())
        .setValueAlias(EdgeOpcUaCommon.WELL_KNOWN_DISCOVERY.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg =
        new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_BROWSE)
            .setBrowseParameter(new EdgeBrowseParameter.Builder().build())
            .setRequest(new EdgeRequest.Builder(ep).build()).build();
    assertNotNull(msg);

    logger.info("[RUN] : testBrowseRootSync - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertEquals(EdgeStatusCode.STATUS_OK, ret.getStatusCode());
    logger.info("[PASS] : testBrowseRootSync");
  }

  public void testBrowseRootSyncWithoutValueAilas() throws Exception {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setEdgeNodeId(new EdgeNodeId.Builder(EdgeOpcUaCommon.SYSTEM_NAMESPACE_INDEX,
            EdgeNodeIdentifier.RootFolder).build())
        .setValueAlias(null).build();
    assertNotNull(ep);

    EdgeMessage msg =
        new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_BROWSE)
            .setBrowseParameter(new EdgeBrowseParameter.Builder().build())
            .setRequest(new EdgeRequest.Builder(ep).build()).build();
    assertNotNull(msg);

    logger.info("[RUN] : testBrowseRootSyncWithoutValueAilas - requestID : "
        + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testBrowseRootSyncWithoutValueAilas");
  }

  public void testBrowseRootSyncWithoutEndpoint() throws Exception {
    EdgeMessage msg =
        new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_BROWSE)
            .setBrowseParameter(new EdgeBrowseParameter.Builder().build())
            .setRequest(new EdgeRequest.Builder(null).build()).build();
    assertNotNull(msg);

    logger.info("[RUN] : testBrowseRootSyncWithoutEndpoint - requestID : "
        + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testBrowseRootSyncWithoutEndpoint");
  }

  public void testBrowseRootSyncWithoutCommand() throws Exception {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setEdgeNodeId(new EdgeNodeId.Builder(EdgeOpcUaCommon.SYSTEM_NAMESPACE_INDEX,
            EdgeNodeIdentifier.RootFolder).build())
        .setValueAlias(EdgeOpcUaCommon.WELL_KNOWN_DISCOVERY.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(null)
        .setBrowseParameter(new EdgeBrowseParameter.Builder().build())
        .setRequest(new EdgeRequest.Builder(ep).build()).build();
    assertNotNull(msg);

    logger.info("[RUN] : testBrowseRootSyncWithoutCommand - requestID : "
        + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testBrowseRootSyncWithoutCommand");
  }

  public void testBrowseRootSyncWithoutMessage() throws Exception {
    logger.info("[RUN] : testBrowseRootSyncWithoutCommand");
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(null);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testBrowseRootSyncWithoutBrowseParam");
  }

  public void testBrowseServerSync() throws Exception {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setEdgeNodeId(new EdgeNodeId.Builder(EdgeOpcUaCommon.SYSTEM_NAMESPACE_INDEX,
            EdgeNodeIdentifier.RootFolder).build())
        .setValueAlias(EdgeOpcUaCommon.WELL_KNOWN_DISCOVERY.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg =
        new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_BROWSE)
            .setBrowseParameter(new EdgeBrowseParameter.Builder().build())
            .setRequest(new EdgeRequest.Builder(ep).build()).build();
    assertNotNull(msg);

    logger.info("[RUN] : testBrowseServerSync - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertEquals(EdgeStatusCode.STATUS_OK, ret.getStatusCode());
    logger.info("[PASS] : testBrowseServerSync");
  }

  public void testBrowseRootSyncWithoutParam() throws Exception {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setEdgeNodeId(new EdgeNodeId.Builder(EdgeOpcUaCommon.SYSTEM_NAMESPACE_INDEX,
            EdgeNodeIdentifier.RootFolder).build())
        .setValueAlias(EdgeOpcUaCommon.WELL_KNOWN_DISCOVERY.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg =
        new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_BROWSE)
            .setRequest(new EdgeRequest.Builder(ep).build()).build();
    assertNotNull(msg);

    logger.info(
        "[RUN] : testBrowseRootSyncWithoutParam - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testBrowseRootSyncWithoutParam");
  }
}
