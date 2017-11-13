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
import java.util.concurrent.CompletableFuture;
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
import org.edge.protocol.opcua.providers.EdgeAttributeProvider;
import org.edge.protocol.opcua.providers.EdgeServices;
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

public class EdgeDataAccessReadTestCase {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private int namespace = EdgeOpcUaCommon.DEFAULT_NAMESPACE_INDEX;
  private EdgeNodeInfo ep;
  private static String endpointUri = EdgeOpcUaCommon.DEFAULT_SERVER_URI.getValue()
      + EdgeOpcUaCommon.DEFAULT_SERVER_NAME.getValue();
  private EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(endpointUri).build();

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
    CompletableFuture<EdgeResult> future;

    registerProvider(uri, EdgeNodeIdentifier.DataItemType);

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.DataItemType_Definition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.DataItemType_ValuePrecision).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.DataItemType_Definition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.DataItemType_ValuePrecision).getStatusCode());

    logger.info("[PASS] : testDataItemNode");
  }

  public void testAnalogItemNode() throws Exception {
    String uri = "/analogItem";
    CompletableFuture<EdgeResult> future;

    registerProvider(uri, EdgeNodeIdentifier.AnalogItemType);

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.AnalogItemType_Definition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.AnalogItemType_ValuePrecision).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.AnalogItemType_EURange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.AnalogItemType_InstrumentRange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.AnalogItemType_EngineeringUnits).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.AnalogItemType_Definition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.AnalogItemType_ValuePrecision).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.AnalogItemType_EURange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.AnalogItemType_InstrumentRange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.AnalogItemType_EngineeringUnits).getStatusCode());

    logger.info("[PASS] : testAnalogItemNode");
  }

  public void testTwoStateDiscreteNode() throws Exception {
    String uri = "/twoStateDiscrete";

    registerProvider(uri, EdgeNodeIdentifier.TwoStateDiscreteType);

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.TwoStateDiscreteType_Definition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.TwoStateDiscreteType_ValuePrecision).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.TwoStateDiscreteType_TrueState).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.TwoStateDiscreteType_FalseState).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.TwoStateDiscreteType_Definition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.TwoStateDiscreteType_ValuePrecision).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.TwoStateDiscreteType_TrueState).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.TwoStateDiscreteType_FalseState).getStatusCode());

    logger.info("[PASS] : testTwoStateDiscreteNode");
  }

  public void testMultiStateDiscreteNode() throws Exception {
    String uri = "/multiStateDiscrete";

    registerProvider(uri, EdgeNodeIdentifier.MultiStateDiscreteType);

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.MultiStateDiscreteType_Definition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.MultiStateDiscreteType_ValuePrecision).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.MultiStateDiscreteType_EnumStrings).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.MultiStateDiscreteType_Definition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.MultiStateDiscreteType_ValuePrecision)
            .getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.MultiStateDiscreteType_EnumStrings).getStatusCode());

    logger.info("[PASS] : testMultiStateDiscreteNode");
  }

  public void testMultiStateValueDiscreteNode() throws Exception {
    String uri = "/multiStateValueDiscrete";

    registerProvider(uri, EdgeNodeIdentifier.MultiStateValueDiscreteType);

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.MultiStateValueDiscreteType_Definition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.MultiStateValueDiscreteType_ValuePrecision)
            .getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.MultiStateValueDiscreteType_EnumValues).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.MultiStateValueDiscreteType_ValueAsText).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.MultiStateValueDiscreteType_Definition)
            .getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.MultiStateValueDiscreteType_ValuePrecision)
            .getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.MultiStateValueDiscreteType_EnumValues)
            .getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.MultiStateValueDiscreteType_ValueAsText)
            .getStatusCode());

    logger.info("[PASS] : testMultiStateValueDiscreteNode");
  }

  public void testArrayItemNode() throws Exception {
    String uri = "/arrayItem";

    registerProvider(uri, EdgeNodeIdentifier.ArrayItemType);

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ArrayItemType_Title).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ArrayItemType_Definition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ArrayItemType_ValuePrecision).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ArrayItemType_EURange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ArrayItemType_InstrumentRange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ArrayItemType_EngineeringUnits).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ArrayItemType_AxisScaleType).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ArrayItemType_Title).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ArrayItemType_Definition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ArrayItemType_ValuePrecision).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ArrayItemType_EURange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ArrayItemType_InstrumentRange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ArrayItemType_EngineeringUnits).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ArrayItemType_AxisScaleType).getStatusCode());

    logger.info("[PASS] : testArrayItemNode");
  }

  public void testYArrayItemNode() throws Exception {
    String uri = "/yArrayItem";

    registerProvider(uri, EdgeNodeIdentifier.YArrayItemType);

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.YArrayItemType_Title).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.YArrayItemType_Definition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.YArrayItemType_ValuePrecision).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.YArrayItemType_EURange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.YArrayItemType_InstrumentRange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.YArrayItemType_EngineeringUnits).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.YArrayItemType_XAxisDefinition).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.YArrayItemType_AxisScaleType).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.YArrayItemType_Title).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.YArrayItemType_Definition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.YArrayItemType_ValuePrecision).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.YArrayItemType_EURange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.YArrayItemType_InstrumentRange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.YArrayItemType_EngineeringUnits).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.YArrayItemType_XAxisDefinition).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.YArrayItemType_AxisScaleType).getStatusCode());

    Thread.sleep(1000);
    logger.info("[PASS] : testYArrayItemNode");
  }

  public void testXYArrayItemNode() throws Exception {
    String uri = "/xyArrayItem";

    registerProvider(uri, EdgeNodeIdentifier.XYArrayItemType);

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.XYArrayItemType_Title).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.XYArrayItemType_Definition).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.XYArrayItemType_ValuePrecision).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.XYArrayItemType_EURange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.XYArrayItemType_InstrumentRange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.XYArrayItemType_EngineeringUnits).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.XYArrayItemType_XAxisDefinition).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.XYArrayItemType_AxisScaleType).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.XYArrayItemType_Title).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.XYArrayItemType_Definition).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.XYArrayItemType_ValuePrecision).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.XYArrayItemType_EURange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.XYArrayItemType_InstrumentRange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.XYArrayItemType_EngineeringUnits).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.XYArrayItemType_XAxisDefinition).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.XYArrayItemType_AxisScaleType).getStatusCode());

    Thread.sleep(1000);

    logger.info("[PASS] : testXYArrayItemNode");
  }

  public void testImageItemNode() throws Exception {
    String uri = "/imageItem";
    CompletableFuture<EdgeResult> future;

    registerProvider(uri, EdgeNodeIdentifier.ImageItemType);

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ImageItemType_Title).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ImageItemType_Definition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ImageItemType_ValuePrecision).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ImageItemType_EURange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ImageItemType_InstrumentRange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ImageItemType_EngineeringUnits).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ImageItemType_XAxisDefinition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ImageItemType_YAxisDefinition).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ImageItemType_AxisScaleType).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ImageItemType_Title).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ImageItemType_ValuePrecision).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ImageItemType_EURange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ImageItemType_InstrumentRange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ImageItemType_EngineeringUnits).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ImageItemType_XAxisDefinition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ImageItemType_YAxisDefinition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ImageItemType_AxisScaleType).getStatusCode());

    Thread.sleep(1000);
    logger.info("[PASS] : testImageItemNode");
  }

  public void testNDimensionArrayItemNode() throws Exception {
    String uri = "/nDimensionArrayItem";

    registerProvider(uri, EdgeNodeIdentifier.NDimensionArrayItemType);

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.NDimensionArrayItemType_Title).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.NDimensionArrayItemType_Definition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.NDimensionArrayItemType_ValuePrecision).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.NDimensionArrayItemType_EURange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.NDimensionArrayItemType_InstrumentRange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.NDimensionArrayItemType_EngineeringUnits).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.NDimensionArrayItemType_AxisDefinition).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValue(uri, EdgeNodeIdentifier.NDimensionArrayItemType_AxisScaleType).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.ReadValueId).getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.NDimensionArrayItemType_Title).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.NDimensionArrayItemType_Definition).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.NDimensionArrayItemType_ValuePrecision)
            .getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.NDimensionArrayItemType_EURange).getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.NDimensionArrayItemType_InstrumentRange)
            .getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.NDimensionArrayItemType_EngineeringUnits)
            .getStatusCode());
    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.NDimensionArrayItemType_AxisDefinition)
            .getStatusCode());

    assertEquals(EdgeStatusCode.STATUS_OK,
        getValueAsync(uri, EdgeNodeIdentifier.NDimensionArrayItemType_AxisScaleType)
            .getStatusCode());

    Thread.sleep(1000);

    logger.info("[PASS] : testNDimensionArrayItemNode");
  }

  private EdgeResult getValue(String uri, EdgeNodeIdentifier nId) throws Exception {
    ep = getEndpoint(uri, nId);
    assertNotNull(ep);
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
        .setRequest(new EdgeRequest.Builder(ep).build()).build();
    logger.info("[RUN] : testReadDataItemNode - requestID : " + msg.getRequest().getRequestId());

    return ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  private EdgeResult getValueAsync(String uri, EdgeNodeIdentifier nId) throws Exception {
    ep = getEndpoint(uri, nId);
    assertNotNull(ep);
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
        .setRequest(new EdgeRequest.Builder(ep).build()).build();

    logger.info("[RUN] : testReadDataItemNode - requestID : " + msg.getRequest().getRequestId());
    return ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  private EdgeNodeInfo getEndpoint(String uri, EdgeNodeIdentifier nId) {
    return new EdgeNodeInfo.Builder().setEdgeNodeId(new EdgeNodeId.Builder(namespace, nId).build())
        .setValueAlias(uri).build();
  }
}
