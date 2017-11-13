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
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
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
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edge.protocol.opcua.providers.EdgeAttributeProvider;
import org.edge.protocol.opcua.providers.EdgeServices;
import org.edge.protocol.opcua.providers.services.EdgeCustomService;
import org.edge.protocol.opcua.providers.services.browse.EdgeBrowseService;
import org.edge.protocol.opcua.providers.services.da.EdgeAnalogItemService;
import org.edge.protocol.opcua.providers.services.da.EdgeArrayItemService;
import org.edge.protocol.opcua.providers.services.da.EdgeDataItemService;
import org.edge.protocol.opcua.providers.services.da.EdgeImageItemService;
import org.edge.protocol.opcua.providers.services.da.EdgeMultiStateDiscreteService;
import org.edge.protocol.opcua.providers.services.da.EdgeMultiStateValueDiscreteService;
import org.edge.protocol.opcua.providers.services.da.EdgeNDimensionArrayItemService;
import org.edge.protocol.opcua.providers.services.da.EdgeTwoStateDiscreteService;
import org.edge.protocol.opcua.providers.services.da.EdgeXYArrayItemService;
import org.edge.protocol.opcua.providers.services.da.EdgeYArrayItemService;
import org.edge.protocol.opcua.providers.services.sub.EdgeMonitoredItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeDataAccessWriteTestCase {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private int namespace = EdgeOpcUaCommon.DEFAULT_NAMESPACE_INDEX;
  private static String endpointUri = EdgeOpcUaCommon.DEFAULT_SERVER_URI.getValue()
      + EdgeOpcUaCommon.DEFAULT_SERVER_NAME.getValue();
  private EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(endpointUri).build();

  public void testCreateDiscoveryProvider() {
    EdgeAttributeProvider commonProvider =
        new EdgeAttributeProvider(EdgeMonitoredItemService.getInstance(),
            EdgeBrowseService.getInstance()).registerAttributeService(
                EdgeOpcUaCommon.WELL_KNOWN_DISCOVERY.getValue(),
                new EdgeCustomService.Builder(0, EdgeOpcUaCommon.WELL_KNOWN_DISCOVERY.getValue())
                    .build());
    assertNotNull(commonProvider);
    EdgeServices.registerAttributeProvider(EdgeOpcUaCommon.WELL_KNOWN_DISCOVERY.getValue(),
        commonProvider);

    logger.info("[PASS] : testCreateDiscoveryProvider");
  }

  private void registerProvider(String uri, EdgeNodeIdentifier idx) {
    EdgeAttributeProvider provider = null;
    if (idx.equals(EdgeNodeIdentifier.DataItemType)) {
      provider = new EdgeAttributeProvider(EdgeMonitoredItemService.getInstance(),
          EdgeBrowseService.getInstance()).registerAttributeService(uri,
              new EdgeDataItemService(namespace, endpointUri));
    } else if (idx.equals(EdgeNodeIdentifier.AnalogItemType)) {
      provider = new EdgeAttributeProvider(EdgeMonitoredItemService.getInstance(),
          EdgeBrowseService.getInstance()).registerAttributeService(uri,
              new EdgeAnalogItemService(namespace, endpointUri));
    } else if (idx.equals(EdgeNodeIdentifier.TwoStateDiscreteType)) {
      provider = new EdgeAttributeProvider(EdgeMonitoredItemService.getInstance(),
          EdgeBrowseService.getInstance()).registerAttributeService(uri,
              new EdgeTwoStateDiscreteService(namespace, endpointUri));
    } else if (idx.equals(EdgeNodeIdentifier.MultiStateDiscreteType)) {
      provider = new EdgeAttributeProvider(EdgeMonitoredItemService.getInstance(),
          EdgeBrowseService.getInstance()).registerAttributeService(uri,
              new EdgeMultiStateDiscreteService(namespace, endpointUri));
    } else if (idx.equals(EdgeNodeIdentifier.MultiStateValueDiscreteType)) {
      provider = new EdgeAttributeProvider(EdgeMonitoredItemService.getInstance(),
          EdgeBrowseService.getInstance()).registerAttributeService(uri,
              new EdgeMultiStateValueDiscreteService(namespace, endpointUri));
    } else if (idx.equals(EdgeNodeIdentifier.ArrayItemType)) {
      provider = new EdgeAttributeProvider(EdgeMonitoredItemService.getInstance(),
          EdgeBrowseService.getInstance()).registerAttributeService(uri,
              new EdgeArrayItemService(namespace, endpointUri));
    } else if (idx.equals(EdgeNodeIdentifier.YArrayItemType)) {
      provider = new EdgeAttributeProvider(EdgeMonitoredItemService.getInstance(),
          EdgeBrowseService.getInstance()).registerAttributeService(uri,
              new EdgeYArrayItemService(namespace, endpointUri));
    } else if (idx.equals(EdgeNodeIdentifier.XYArrayItemType)) {
      provider = new EdgeAttributeProvider(EdgeMonitoredItemService.getInstance(),
          EdgeBrowseService.getInstance()).registerAttributeService(uri,
              new EdgeXYArrayItemService(namespace, endpointUri));
    } else if (idx.equals(EdgeNodeIdentifier.ImageItemType)) {
      provider = new EdgeAttributeProvider(EdgeMonitoredItemService.getInstance(),
          EdgeBrowseService.getInstance()).registerAttributeService(uri,
              new EdgeImageItemService(namespace, endpointUri));
    } else if (idx.equals(EdgeNodeIdentifier.NDimensionArrayItemType)) {
      provider = new EdgeAttributeProvider(EdgeMonitoredItemService.getInstance(),
          EdgeBrowseService.getInstance()).registerAttributeService(uri,
              new EdgeNDimensionArrayItemService(namespace, endpointUri));
    }

    EdgeServices.registerAttributeProvider(uri, provider);
  }

  public void testDataItemNode() throws Exception {
    String uri = "/dataItem";

    registerProvider(uri, EdgeNodeIdentifier.DataItemType);

    assertEquals(EdgeStatusCode.STATUS_OK, writeValue(uri, EdgeNodeIdentifier.DataItemType,
        new EdgeVersatility.Builder(UInteger.valueOf(400)).build()).getStatusCode());

    logger.info("[PASS] : testDataItemNode");
  }

  public void testAnalogItemNode() throws Exception {
    String uri = "/analogItem";

    registerProvider(uri, EdgeNodeIdentifier.AnalogItemType);

    assertEquals(EdgeStatusCode.STATUS_OK, writeValue(uri, EdgeNodeIdentifier.AnalogItemType,
        new EdgeVersatility.Builder(UInteger.valueOf(100)).build()).getStatusCode());

    logger.info("[PASS] : testAnalogItemNode");
  }

  public void testTwoStateDiscreteNode() throws Exception {
    String uri = "/twoStateDiscrete";

    registerProvider(uri, EdgeNodeIdentifier.TwoStateDiscreteType);

    assertEquals(EdgeStatusCode.STATUS_OK, writeValue(uri, EdgeNodeIdentifier.TwoStateDiscreteType,
        new EdgeVersatility.Builder(Boolean.valueOf(false)).build()).getStatusCode());

    logger.info("[PASS] : testTwoStateDiscreteNode");
  }

  public void testMultiStateValueDiscreteNode() throws Exception {
    String uri = "/multiStateValueDiscrete";

    registerProvider(uri, EdgeNodeIdentifier.MultiStateValueDiscreteType);

    assertEquals(EdgeStatusCode.STATUS_OK,
        writeValue(uri, EdgeNodeIdentifier.MultiStateValueDiscreteType,
            new EdgeVersatility.Builder(Long.valueOf(2)).build()).getStatusCode());

    logger.info("[PASS] : testMultiStateValueDiscreteNode");
  }

  public void testArrayItemNode() throws Exception {
    String uri = "/arrayItem";

    registerProvider(uri, EdgeNodeIdentifier.ArrayItemType);

    Integer[] writeValue = {1, 1, 1, 1, 1};

    assertEquals(EdgeStatusCode.STATUS_OK, writeValue(uri, EdgeNodeIdentifier.ArrayItemType,
        new EdgeVersatility.Builder(writeValue).build()).getStatusCode());

    logger.info("[PASS] : testArrayItemNode");
  }

  public void testYArrayItemNode() throws Exception {
    String uri = "/yArrayItem";

    registerProvider(uri, EdgeNodeIdentifier.YArrayItemType);

    Integer[] writeValue = {-40, -40, -40, 0, 0};

    assertEquals(EdgeStatusCode.STATUS_OK, writeValue(uri, EdgeNodeIdentifier.YArrayItemType,
        new EdgeVersatility.Builder(writeValue).build()).getStatusCode());

    logger.info("[PASS] : testYArrayItemNode");
  }

  public void testImageItemNode() throws Exception {
    String uri = "/imageItem";

    registerProvider(uri, EdgeNodeIdentifier.ImageItemType);

    Integer[][] writeValue = new Integer[10][14];
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 14; j++) {
        writeValue[i][j] = 0;
      }
    }

    assertEquals(EdgeStatusCode.STATUS_OK, writeValue(uri, EdgeNodeIdentifier.ImageItemType,
        new EdgeVersatility.Builder(writeValue).build()).getStatusCode());

    logger.info("[PASS] : testImageItemNode");
  }

  public void testNDimensionArrayItemNode() throws Exception {
    String uri = "/nDimensionArrayItem";

    registerProvider(uri, EdgeNodeIdentifier.NDimensionArrayItemType);

    Integer[] writeValue = {-40, -40, -80, 0, 0};

    assertEquals(EdgeStatusCode.STATUS_OK,
        writeValue(uri, EdgeNodeIdentifier.NDimensionArrayItemType,
            new EdgeVersatility.Builder(writeValue).build()).getStatusCode());

    logger.info("[PASS] : testNDimensionArrayItemNode");
  }

  private EdgeResult writeValue(String uri, EdgeNodeIdentifier nId, EdgeVersatility value)
      throws Exception {
    EdgeNodeInfo ep = getEndpoint(uri, nId);
    assertNotNull(ep);
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_WRITE)
        .setRequest(new EdgeRequest.Builder(ep).setMessage(value).build()).build();

    logger.info("[RUN] : testWriteDataItemNode - requestID : " + msg.getRequest().getRequestId());
    return ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  private EdgeNodeInfo getEndpoint(String uri, EdgeNodeIdentifier nId) {
    return new EdgeNodeInfo.Builder().setEdgeNodeId(new EdgeNodeId.Builder(namespace, nId).build())
        .setValueAlias(uri).build();
  }
}
