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
import org.edge.protocol.opcua.api.client.EdgeSubRequest;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeCommandType;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeRequest;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.example.EdgeSampleCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeSubscriptionTestCase {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final String endpointUri = EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue();
  private EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(endpointUri).build();
  
  public void testCreateSub() throws Exception {
    EdgeSubRequest sub = new EdgeSubRequest.Builder(EdgeNodeIdentifier.Edge_Create_Sub)
        .setSamplingInterval(1000.0).build();
    assertNotNull(sub);

    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_LINE_CNC14.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB)
        .setRequest(new EdgeRequest.Builder(ep).setSubReq(sub).build()).build();
    assertNotNull(msg);

    logger.info("[RUN] : testCreateSub - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_OK, ret.getStatusCode());
    logger.info("[PASS] : testCreateSub");
  }

  public void testCreateSubWithoutEndpoint() throws Exception {
    EdgeSubRequest sub = new EdgeSubRequest.Builder(EdgeNodeIdentifier.Edge_Create_Sub)
        .setSamplingInterval(1000.0).build();
    assertNotNull(sub);

    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB)
        .setRequest(new EdgeRequest.Builder(null).setSubReq(sub).build()).build();
    assertNotNull(msg);

    logger.info(
        "[RUN] : testCreateSubWithoutEndpoint - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testCreateSubWithoutEndpoint");
  }

  public void testCreateSubWithoutValueAilas() throws Exception {
    EdgeSubRequest sub = new EdgeSubRequest.Builder(EdgeNodeIdentifier.Edge_Create_Sub)
        .setSamplingInterval(1000.0).build();
    assertNotNull(sub);

    EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(null).build();
    assertNotNull(ep);

    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB)
        .setRequest(new EdgeRequest.Builder(ep).setSubReq(sub).build()).build();
    assertNotNull(msg);

    logger.info(
        "[RUN] : testCreateSubWithoutValueAilas - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testCreateSubWithoutValueAilas");
  }

  public void testCreateSubWithoutCommand() throws Exception {
    EdgeSubRequest sub = new EdgeSubRequest.Builder(EdgeNodeIdentifier.Edge_Create_Sub)
        .setSamplingInterval(1000.0).build();
    assertNotNull(sub);

    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_LINE_CNC14.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg =
        new EdgeMessage.Builder(epInfo).setCommand(null)
            .setRequest(new EdgeRequest.Builder(ep).setSubReq(sub).build()).build();
    assertNotNull(msg);

    logger.info(
        "[RUN] : testCreateSubWithoutCommand - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testCreateSubWithoutCommand");
  }

  public void testCreateSubWithoutSubReqNode() throws Exception {
    EdgeSubRequest sub = new EdgeSubRequest.Builder(null).setSamplingInterval(1000.0).build();
    assertNotNull(sub);

    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_LINE_CNC14.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB)
        .setRequest(new EdgeRequest.Builder(ep).setSubReq(sub).build()).build();
    assertNotNull(msg);

    logger.info(
        "[RUN] : testCreateSubWithoutSubReqNode - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testCreateSubWithoutSubReqNode");
  }

  public void testCreateSubWithoutSubReq() throws Exception {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_LINE_CNC14.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB)
        .setRequest(new EdgeRequest.Builder(ep).build()).build();
    assertNotNull(msg);

    logger.info(
        "[RUN] : testCreateSubWithoutSubReq - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testCreateSubWithoutSubReq");
  }

  public void testCreateSubWithoutMessage() throws Exception {
    logger.info("[RUN] : testCreateSubWithoutMessage");
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(null);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testCreateSubWithoutMessage");
  }

  public void testModifySub() throws Exception {
    EdgeSubRequest sub = new EdgeSubRequest.Builder(EdgeNodeIdentifier.Edge_Modify_Sub)
        .setSamplingInterval(3000.0).build();
    assertNotNull(sub);

    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_LINE_CNC14.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB)
        .setRequest(new EdgeRequest.Builder(ep).setSubReq(sub).build()).build();
    assertNotNull(msg);

    logger.info("[RUN] : testModifySub - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_OK, ret.getStatusCode());
    logger.info("[PASS] : testModifySub");
  }

  public void testDeleteSub() throws Exception {
    EdgeSubRequest sub = new EdgeSubRequest.Builder(EdgeNodeIdentifier.Edge_Delete_Sub).build();
    assertNotNull(sub);

    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_LINE_CNC14.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB)
        .setRequest(new EdgeRequest.Builder(ep).setSubReq(sub).build()).build();
    assertNotNull(msg);

    logger.info("[RUN] : testDeleteSub - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_OK, ret.getStatusCode());
    logger.info("[PASS] : testDeleteSub");
  }
}
