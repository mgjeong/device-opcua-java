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
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeRequest;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edge.protocol.opcua.example.EdgeSampleCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeMethodTestCase {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final String endpointUri = EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue();
  private EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(endpointUri).build();

  public void testRunMethodService(Double param) {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_METHOD_SQRT.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg = new EdgeMessage.Builder(epInfo)
        .setCommand(EdgeCommandType.CMD_METHOD).setRequest(new EdgeRequest.Builder(ep)
            .setMessage(new EdgeVersatility.Builder(param).build()).build())
        .build();
    assertNotNull(msg);

    logger.info("[RUN] : testRunMethodService - requestID : " + msg.getRequest().getRequestId());
    EdgeResult ret = new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build();
    try {
      ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_OK, ret.getStatusCode());
    logger.info("[PASS] : testRunMethodService");
  }

  public void testRunMethodServiceWithoutEndpoint(Double param) {
    EdgeMessage msg = new EdgeMessage.Builder(epInfo)
        .setCommand(EdgeCommandType.CMD_METHOD).setRequest(new EdgeRequest.Builder(null)
            .setMessage(new EdgeVersatility.Builder(param).build()).build())
        .build();
    assertNotNull(msg);

    logger.info("[RUN] : testRunMethodServiceWithoutEndpoint - requestID : "
        + msg.getRequest().getRequestId());
    EdgeResult ret = new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build();
    try {
      ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testRunMethodServiceWithoutEndpoint");
  }

  public void testRunMethodServiceWithoutCommand(Double param) {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_METHOD_SQRT.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(null).setRequest(
        new EdgeRequest.Builder(ep).setMessage(new EdgeVersatility.Builder(param).build()).build())
        .build();
    assertNotNull(msg);

    logger.info("[RUN] : testRunMethodServiceWithoutCommand - requestID : "
        + msg.getRequest().getRequestId());
    EdgeResult ret = new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build();
    try {
      ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testRunMethodServiceWithoutCommand");
  }

  public void testRunMethodServiceWithoutValueAilas(Double param) {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(null).build();
    assertNotNull(ep);

    EdgeMessage msg = new EdgeMessage.Builder(epInfo)
        .setCommand(EdgeCommandType.CMD_METHOD).setRequest(new EdgeRequest.Builder(ep)
            .setMessage(new EdgeVersatility.Builder(param).build()).build())
        .build();
    assertNotNull(msg);

    logger.info("[RUN] : testRunMethodServiceWithoutValueAilas - requestID : "
        + msg.getRequest().getRequestId());
    EdgeResult ret = new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build();
    try {
      ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testRunMethodServiceWithoutValueAilas");
  }

  public void testRunMethodServiceWithoutMessage(Double param) {
    logger.info("[RUN] : testRunMethodServiceWithoutMessage");
    EdgeResult ret = new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build();
    try {
      ret = ProtocolManager.getProtocolManagerInstance().send(null);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testRunMethodServiceWithoutMessage");
  }

  public void testRunMethodServiceWithoutParam() {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_METHOD_SQRT.getValue()).build();
    assertNotNull(ep);

    EdgeMessage msg =
        new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_METHOD)
            .setRequest(new EdgeRequest.Builder(ep).build()).build();
    assertNotNull(msg);

    logger.info("[RUN] : testRunMethodServiceWithoutParam - requestID : "
        + msg.getRequest().getRequestId());
    EdgeResult ret = new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build();
    try {
      ret = ProtocolManager.getProtocolManagerInstance().send(msg);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    assertNotNull(ret);
    assertEquals(EdgeStatusCode.STATUS_PARAM_INVALID, ret.getStatusCode());
    logger.info("[PASS] : testRunMethodServiceWithoutParam");
  }
}
