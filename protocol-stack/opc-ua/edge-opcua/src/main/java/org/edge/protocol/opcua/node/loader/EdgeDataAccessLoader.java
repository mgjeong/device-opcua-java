/******************************************************************
 *
 * Copyright 2017 Samsung Electronics All Rights Reserved.
 *
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************/

package org.edge.protocol.opcua.node.loader;

import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.api.ServerNodeMap;
import org.eclipse.milo.opcua.sdk.server.model.nodes.variables.AnalogItemNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.variables.ArrayItemNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.variables.DataItemNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.variables.ImageItemNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.variables.MultiStateDiscreteNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.variables.MultiStateValueDiscreteNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.variables.NDimensionArrayItemNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.variables.TwoStateDiscreteNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.variables.XYArrayItemNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.variables.YArrayItemNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.enumerated.AxisScaleEnumeration;
import org.eclipse.milo.opcua.stack.core.types.structured.AxisInformation;
import org.eclipse.milo.opcua.stack.core.types.structured.EUInformation;
import org.eclipse.milo.opcua.stack.core.types.structured.EnumValueType;
import org.eclipse.milo.opcua.stack.core.types.structured.Range;
import org.eclipse.milo.opcua.stack.core.types.structured.XVType;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.server.EdgeNodeItem;
import org.edge.protocol.opcua.namespace.util.EdgeAbstractNamespaceIdxMap;
import org.edge.protocol.opcua.namespace.util.EdgeBaseNamespaceIdxMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeDataAccessLoader {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  // TODO
  // this variable is for test to set in server side. it will be removed.
  public static AnalogItemNode analogItemNode;

  private final EdgeBaseNamespaceIdxMap namespaceIndexMap = new EdgeDANamespaceIndexMap();

  private static EdgeDataAccessLoader loader = null;
  private static Object lock = new Object();

  private EdgeDataAccessLoader() {}

  /**
   * @fn EdgeDataAccessLoader getInstance()
   * @brief get EdgeDataAccessLoader Instance
   * @return EdgeDataAccessLoader Instance
   */
  public static EdgeDataAccessLoader getInstance() {
    synchronized (lock) {
      if (null == loader) {
        loader = new EdgeDataAccessLoader();
      }
      return loader;
    }
  }

  /**
   * @fn void close()
   * @brief close EdgeDataAccessLoader Instance
   * @return void
   */
  public void close() {
    loader = null;
  }

  private void addReference(DataItemNode cNode, UaVariableNode pNode) {
    pNode.addReference(new Reference(pNode.getNodeId(), Identifiers.HasComponent,
        cNode.getNodeId().expanded(), cNode.getNodeClass(), true));
    logger.debug("addReference variableType folder : {}-{}", pNode.getNodeId(),
        cNode.getNodeClass());
  }

  private UShort getNamespaceIdx(EdgeNodeIdentifier type) {
    UShort idx;
    if (!namespaceIndexMap.containsKey(type.value())) {
      idx = UShort.valueOf(EdgeOpcUaCommon.DEFAULT_NAMESPACE_INDEX);
    } else {
      idx = namespaceIndexMap.get(type.value());
    }
    logger.debug("current namespace idx {}-{}", type.value(), idx);
    namespaceIndexMap.put(type.value(), UShort.valueOf(idx.intValue() + 1));
    logger.debug("current namespace idx {}-{}", type.value(), UShort.valueOf(idx.intValue() + 1));
    return idx;
  }

  private void buildDataItemNode(ServerNodeMap nodeMap, UaVariableNode pNode, EdgeNodeItem item) {
    UShort namespaceIdx = getNamespaceIdx(EdgeNodeIdentifier.DataItemType);
    logger.info("current namespace idx {}-{}", namespaceIdx, namespaceIdx);

    DataItemNode dataItemNode = new DataItemNode(nodeMap,
        NodeId.parse("ns=" + namespaceIdx + ";i=" + EdgeNodeIdentifier.DataItemType.value()),
        new QualifiedName(namespaceIdx, "DataItemType"), new LocalizedText("en", "Temperature"),
        new LocalizedText("en", "this is DataItemType -DA"), UInteger.valueOf(1),
        UInteger.valueOf(1), new DataValue(new Variant(UInteger.valueOf(100))), Identifiers.Number,
        Integer.valueOf(-2), null, Unsigned.ubyte(item.getAccessLevel()),
        Unsigned.ubyte(item.getUserAccessLevel()), Double.valueOf(5.0), false);
    nodeMap.put(dataItemNode.getNodeId(), dataItemNode);
    addReference(dataItemNode, pNode);

    dataItemNode.setDefinition("T = 10 * t");
    dataItemNode.setValuePrecision(Double.valueOf(10.0));

  }

  private void buildAnalogItemNode(ServerNodeMap nodeMap, UaVariableNode pNode, EdgeNodeItem item) {
    UShort namespaceIdx = getNamespaceIdx(EdgeNodeIdentifier.AnalogItemType);

    analogItemNode = new AnalogItemNode(nodeMap,
        NodeId.parse("ns=" + namespaceIdx + ";i=" + EdgeNodeIdentifier.AnalogItemType.value()),
        new QualifiedName(namespaceIdx, "AnalogItemType"), new LocalizedText("en", "Pressure"),
        new LocalizedText("en", "this is AnalogItemType - DA"), UInteger.valueOf(1),
        UInteger.valueOf(1), new DataValue(new Variant(UInteger.valueOf(400))), Identifiers.Number,
        Integer.valueOf(0), null, Unsigned.ubyte(item.getAccessLevel()),
        Unsigned.ubyte(item.getUserAccessLevel()), Double.valueOf(5.0), false);
    nodeMap.put(analogItemNode.getNodeId(), analogItemNode);
    addReference(analogItemNode, pNode);

    analogItemNode.setDefinition("P = F / A");
    analogItemNode.setValuePrecision(Double.valueOf(10.0));
    analogItemNode.setInstrumentRange(new Range(Double.valueOf(100), Double.valueOf(1200)));
    analogItemNode.setEURange(new Range(Double.valueOf(300), Double.valueOf(400)));
    analogItemNode.setEngineeringUnits(new EUInformation("P", Integer.valueOf(0),
        new LocalizedText("en", "EU example"), new LocalizedText("en", "It is just example EU")));
  }

  private void buildTwoStateDiscreteNode(ServerNodeMap nodeMap, UaVariableNode pNode,
      EdgeNodeItem item) {
    UShort namespaceIdx = getNamespaceIdx(EdgeNodeIdentifier.TwoStateDiscreteType);

    TwoStateDiscreteNode twoStateDiscreteNode = new TwoStateDiscreteNode(nodeMap,
        NodeId
            .parse("ns=" + namespaceIdx + ";i=" + EdgeNodeIdentifier.TwoStateDiscreteType.value()),
        new QualifiedName(namespaceIdx, "TwoStateDiscreteType"), new LocalizedText("en", "Openess"),
        new LocalizedText("en", "this is TwoStateDiscreteNode - DA"), UInteger.valueOf(1),
        UInteger.valueOf(1), new DataValue(new Variant(Boolean.valueOf(true))), Identifiers.Boolean,
        Integer.valueOf(0), null, Unsigned.ubyte(item.getAccessLevel()),
        Unsigned.ubyte(item.getUserAccessLevel()), Double.valueOf(5.0), false);
    nodeMap.put(twoStateDiscreteNode.getNodeId(), twoStateDiscreteNode);
    addReference(twoStateDiscreteNode, pNode);

    twoStateDiscreteNode.setDefinition("True/False");
    twoStateDiscreteNode.setValuePrecision(Double.valueOf(10.0));
    twoStateDiscreteNode.setTrueState(new LocalizedText("en", "Open"));
    twoStateDiscreteNode.setFalseState(new LocalizedText("en", "Close"));
  }

  private void buildMultiStateDiscreteNode(ServerNodeMap nodeMap, UaVariableNode pNode,
      EdgeNodeItem item) {
    UShort namespaceIdx = getNamespaceIdx(EdgeNodeIdentifier.MultiStateDiscreteType);

    MultiStateDiscreteNode multiStateDiscreteNode = new MultiStateDiscreteNode(nodeMap,
        NodeId.parse(
            "ns=" + namespaceIdx + ";i=" + EdgeNodeIdentifier.MultiStateDiscreteType.value()),
        new QualifiedName(namespaceIdx, "MultiStateDiscreteType"),
        new LocalizedText("en", "Openess"),
        new LocalizedText("en", "this is MultistateDiscreteNode"), UInteger.valueOf(1),
        UInteger.valueOf(1), new DataValue(new Variant(UInteger.valueOf(0))), Identifiers.UInteger,
        Integer.valueOf(0), null, Unsigned.ubyte(item.getAccessLevel()),
        Unsigned.ubyte(item.getUserAccessLevel()), Double.valueOf(5.0), false);
    nodeMap.put(multiStateDiscreteNode.getNodeId(), multiStateDiscreteNode);
    addReference(multiStateDiscreteNode, pNode);

    multiStateDiscreteNode.setDefinition("Openess");
    multiStateDiscreteNode.setValuePrecision(Double.valueOf(10.0));
    LocalizedText enumString[] = {new LocalizedText("en", "Open"), new LocalizedText("en", "Close"),
        new LocalizedText("en", "In Transit")};
    multiStateDiscreteNode.setEnumStrings(enumString);
  }

  private void builMultiStateValueDiscreteNode(ServerNodeMap nodeMap, UaVariableNode pNode,
      EdgeNodeItem item) {
    UShort namespaceIdx = getNamespaceIdx(EdgeNodeIdentifier.MultiStateValueDiscreteType);

    MultiStateValueDiscreteNode multiStateValueDiscreteNode = new MultiStateValueDiscreteNode(
        nodeMap,
        NodeId.parse(
            "ns=" + namespaceIdx + ";i=" + EdgeNodeIdentifier.MultiStateValueDiscreteType.value()),
        new QualifiedName(namespaceIdx, "MultiStateValueDiscreteType"),
        new LocalizedText("en", "MultiStateValueDiscreteType"),
        new LocalizedText("en", "this is MultiStateValueDiscreteType"), UInteger.valueOf(1),
        UInteger.valueOf(1), new DataValue(new Variant(Long.valueOf(1))), Identifiers.Number,
        Integer.valueOf(0), null, Unsigned.ubyte(item.getAccessLevel()),
        Unsigned.ubyte(item.getUserAccessLevel()), Double.valueOf(5.0), false);
    nodeMap.put(multiStateValueDiscreteNode.getNodeId(), multiStateValueDiscreteNode);
    addReference(multiStateValueDiscreteNode, pNode);

    multiStateValueDiscreteNode.setDefinition("multiStateValueDiscrete");
    multiStateValueDiscreteNode.setValuePrecision(Double.valueOf(10.0));

    EnumValueType[] enumValue = {
        new EnumValueType(Long.valueOf(1), new LocalizedText("en", "gap_1"),
            new LocalizedText("en", "first gap")),
        new EnumValueType(Long.valueOf(2), new LocalizedText("en", "gap_2"),
            new LocalizedText("en", "second gap")),
        new EnumValueType(Long.valueOf(4), new LocalizedText("en", "gap_3"),
            new LocalizedText("en", "third gap")),};

    multiStateValueDiscreteNode.setEnumValues(enumValue);
    multiStateValueDiscreteNode.setValueAsText(new LocalizedText("en", "collection of gap"));
  }

  private void buildImageItemNode(ServerNodeMap nodeMap, UaVariableNode pNode, EdgeNodeItem item) {
    UInteger[] array = new UInteger[2];
    array[0] = UInteger.valueOf(10);
    array[1] = UInteger.valueOf(15);

    Integer[][] array_contents = new Integer[10][15];
    for (int i = 0; i < array[0].intValue(); i++) {
      for (int j = 0; j < array[1].intValue(); j++) {
        array_contents[i][j] = 255;
      }
    }

    UShort namespaceIdx = getNamespaceIdx(EdgeNodeIdentifier.ImageItemType);

    ImageItemNode imageItemNode = new ImageItemNode(nodeMap,
        NodeId.parse("ns=" + namespaceIdx + ";i=" + EdgeNodeIdentifier.ImageItemType.value()),
        new QualifiedName(namespaceIdx, "ImageItemType"), new LocalizedText("en", "Image"),
        new LocalizedText("en", "this is ImageItemNode - DA"), UInteger.valueOf(1),
        UInteger.valueOf(1), new DataValue(new Variant(array_contents)), Identifiers.BaseDataType,
        Integer.valueOf(2), array, Unsigned.ubyte(item.getAccessLevel()),
        Unsigned.ubyte(item.getUserAccessLevel()), Double.valueOf(5.0), false);

    imageItemNode.setXAxisDefinition(
        new AxisInformation(new EUInformation(), new Range(Double.valueOf(10), Double.valueOf(20)),
            new LocalizedText("en", "text"), AxisScaleEnumeration.Linear, new Double[3]));
    imageItemNode.setYAxisDefinition(
        new AxisInformation(new EUInformation(), new Range(Double.valueOf(10), Double.valueOf(20)),
            new LocalizedText("en", "text"), AxisScaleEnumeration.Linear, new Double[4]));
    imageItemNode.setDefinition("CNC Image");
    imageItemNode.setValuePrecision(Double.valueOf(10.0));
    imageItemNode.setInstrumentRange(new Range(Double.valueOf(100), Double.valueOf(1200)));
    imageItemNode.setEURange(new Range(Double.valueOf(300), Double.valueOf(400)));
    imageItemNode.setTitle(new LocalizedText("en", "Image"));
    imageItemNode.setEngineeringUnits(new EUInformation("P", Integer.valueOf(0),
        new LocalizedText("en", "EU example"), new LocalizedText("en", "It is just example EU")));
    imageItemNode.setAxisScaleType(AxisScaleEnumeration.Linear);

    nodeMap.put(imageItemNode.getNodeId(), imageItemNode);
    addReference(imageItemNode, pNode);
  }

  private void buildArrayItemNode(ServerNodeMap nodeMap, UaVariableNode pNode, EdgeNodeItem item) {
    ArrayItemNode arrayItemNode;
    UInteger[] array = new UInteger[2];
    array[0] = UInteger.valueOf(1);
    array[1] = UInteger.valueOf(5);

    Integer[] array_contents = {1, 0, 0, 0, 0};

    UShort namespaceIdx = getNamespaceIdx(EdgeNodeIdentifier.ArrayItemType);

    arrayItemNode = new ArrayItemNode(nodeMap,
        NodeId.parse("ns=" + namespaceIdx + ";i=" + EdgeNodeIdentifier.ArrayItemType.value()),
        new QualifiedName(namespaceIdx, "ArrayItemType"), new LocalizedText("en", "Array"),
        new LocalizedText("en", "this is ArrayItemNode"), UInteger.valueOf(1), UInteger.valueOf(1),
        new DataValue(new Variant(array_contents)), Identifiers.BaseDataType, Integer.valueOf(0),
        array, Unsigned.ubyte(item.getAccessLevel()), Unsigned.ubyte(item.getUserAccessLevel()),
        Double.valueOf(5.0), false);

    arrayItemNode.setTitle(new LocalizedText("en", "ArrayItemNode"));
    arrayItemNode.setAxisScaleType(AxisScaleEnumeration.Linear);
    arrayItemNode.setDefinition("ARRAY");
    arrayItemNode.setValuePrecision(Double.valueOf(10.0));
    arrayItemNode.setInstrumentRange(new Range(Double.valueOf(100), Double.valueOf(1200)));
    arrayItemNode.setEURange(new Range(Double.valueOf(300), Double.valueOf(400)));
    arrayItemNode.setEngineeringUnits(new EUInformation("P", Integer.valueOf(0),
        new LocalizedText("en", "EU example"), new LocalizedText("en", "It is just example EU")));

    nodeMap.put(arrayItemNode.getNodeId(), arrayItemNode);
    addReference(arrayItemNode, pNode);
  }

  private void buildYArrayItemNode(ServerNodeMap nodeMap, UaVariableNode pNode, EdgeNodeItem item) {
    YArrayItemNode yArrayItemNode;
    UInteger[] array = {UInteger.valueOf(0)};

    Integer[] array_contents = {-80, -80, -80, 0, 0};

    UShort namespaceIdx = getNamespaceIdx(EdgeNodeIdentifier.YArrayItemType);

    yArrayItemNode = new YArrayItemNode(nodeMap,
        NodeId.parse("ns=" + namespaceIdx + ";i=" + EdgeNodeIdentifier.YArrayItemType.value()),
        new QualifiedName(namespaceIdx, "YArrayItemType"), new LocalizedText("en", "YArray"),
        new LocalizedText("en", "this is YArrayItemNode"), UInteger.valueOf(1), UInteger.valueOf(1),
        new DataValue(new Variant(array_contents)), Identifiers.BaseDataType, Integer.valueOf(1),
        array, Unsigned.ubyte(item.getAccessLevel()), Unsigned.ubyte(item.getUserAccessLevel()),
        Double.valueOf(5.0), false);

    yArrayItemNode.setXAxisDefinition(new AxisInformation(
        new EUInformation("http://www.opcfoundation.org/UA/units/un/cefact", Integer.valueOf(2),
            new LocalizedText("en", "kHz"), new LocalizedText("en", "kilohertz")),
        new Range(Double.valueOf(0), Double.valueOf(25)), new LocalizedText("en", "text"),
        AxisScaleEnumeration.Linear, new Double[3]));
    yArrayItemNode.setTitle(new LocalizedText("en", "Magnitude"));
    yArrayItemNode.setDefinition("Magnitude");
    yArrayItemNode.setInstrumentRange(new Range(Double.valueOf(-90), Double.valueOf(5)));
    yArrayItemNode.setEURange(new Range(Double.valueOf(-90), Double.valueOf(2)));
    yArrayItemNode
        .setEngineeringUnits(new EUInformation("http://www.opcfoundation.org/UA/units/un/cefact",
            Integer.valueOf(2), new LocalizedText("en", "dB"), new LocalizedText("en", "decibel")));
    yArrayItemNode.setValuePrecision(Double.valueOf(10.0));
    yArrayItemNode.setAxisScaleType(AxisScaleEnumeration.Ln);

    nodeMap.put(yArrayItemNode.getNodeId(), yArrayItemNode);
    addReference(yArrayItemNode, pNode);
  }

  private void buildXYArrayItemNode(ServerNodeMap nodeMap, UaVariableNode pNode,
      EdgeNodeItem item) {
    XYArrayItemNode xyArrayItemNode;

    UInteger[] array = {UInteger.valueOf(0)};
    XVType xvValue = new XVType(Double.valueOf(2), Float.valueOf(2));

    UShort namespaceIdx = getNamespaceIdx(EdgeNodeIdentifier.XYArrayItemType);

    xyArrayItemNode = new XYArrayItemNode(nodeMap,
        NodeId.parse("ns=" + namespaceIdx + ";i=" + EdgeNodeIdentifier.XYArrayItemType.value()),
        new QualifiedName(namespaceIdx, "XYArrayItemType"), new LocalizedText("en", "XYArray"),
        new LocalizedText("en", "this is XYArrayItemNode"), UInteger.valueOf(1),
        UInteger.valueOf(1), new DataValue(new Variant(xvValue)), Identifiers.XVType,
        Integer.valueOf(1), array, Unsigned.ubyte(item.getAccessLevel()),
        Unsigned.ubyte(item.getUserAccessLevel()), Double.valueOf(5.0), false);

    xyArrayItemNode.setXAxisDefinition(new AxisInformation(
        new EUInformation("http://www.opcfoundation.org/UA/units/un/cefact", Integer.valueOf(2),
            new LocalizedText("en", "kHz"), new LocalizedText("en", "kilohertz")),
        new Range(Double.valueOf(0), Double.valueOf(25)), new LocalizedText("en", "text"),
        AxisScaleEnumeration.Linear, new Double[3]));
    xyArrayItemNode.setTitle(new LocalizedText("en", "Magnitude"));
    xyArrayItemNode.setDefinition("Magnitude");
    xyArrayItemNode.setInstrumentRange(new Range(Double.valueOf(-90), Double.valueOf(5)));
    xyArrayItemNode.setEURange(new Range(Double.valueOf(-90), Double.valueOf(2)));
    xyArrayItemNode
        .setEngineeringUnits(new EUInformation("http://www.opcfoundation.org/UA/units/un/cefact",
            Integer.valueOf(2), new LocalizedText("en", "dB"), new LocalizedText("en", "decibel")));
    xyArrayItemNode.setValuePrecision(Double.valueOf(10.0));
    xyArrayItemNode.setAxisScaleType(AxisScaleEnumeration.Linear);

    nodeMap.put(xyArrayItemNode.getNodeId(), xyArrayItemNode);
    addReference(xyArrayItemNode, pNode);
  }

  private void buildNDimensionItemNode(ServerNodeMap nodeMap, UaVariableNode pNode,
      EdgeNodeItem item) {
    NDimensionArrayItemNode nDimensionArrayItemNode;

    Integer[] array_contents = {-80, -80, -80, 0, 0};
    UInteger[] array = {UInteger.valueOf(0)};

    UShort namespaceIdx = getNamespaceIdx(EdgeNodeIdentifier.NDimensionArrayItemType);

    nDimensionArrayItemNode = new NDimensionArrayItemNode(nodeMap,
        NodeId.parse(
            "ns=" + namespaceIdx + ";i=" + EdgeNodeIdentifier.NDimensionArrayItemType.value()),
        new QualifiedName(namespaceIdx, "NDimensionArrayItemType"),
        new LocalizedText("en", "NDimensionArrayItemType"),
        new LocalizedText("en", "this is NDimensionArrayItemTypeNode"), UInteger.valueOf(1),
        UInteger.valueOf(1), new DataValue(new Variant(array_contents)), Identifiers.BaseDataType,
        Integer.valueOf(0), array, Unsigned.ubyte(item.getAccessLevel()),
        Unsigned.ubyte(item.getUserAccessLevel()), Double.valueOf(5.0), false);

    nDimensionArrayItemNode.setTitle(new LocalizedText("en", "Magnitude"));
    nDimensionArrayItemNode.setDefinition("Magnitude");
    nDimensionArrayItemNode.setInstrumentRange(new Range(Double.valueOf(-90), Double.valueOf(5)));
    nDimensionArrayItemNode.setEURange(new Range(Double.valueOf(-90), Double.valueOf(2)));
    nDimensionArrayItemNode
        .setEngineeringUnits(new EUInformation("http://www.opcfoundation.org/UA/units/un/cefact",
            Integer.valueOf(2), new LocalizedText("en", "dB"), new LocalizedText("en", "decibel")));
    nDimensionArrayItemNode.setValuePrecision(Double.valueOf(10.0));
    nDimensionArrayItemNode.setAxisScaleType(AxisScaleEnumeration.Ln);

    AxisInformation infos[] = {
        new AxisInformation(
            new EUInformation("http://www.opcfoundation.org/UA/units/un/cefact", Integer.valueOf(2),
                new LocalizedText("en", "kHz"), new LocalizedText("en", "kilohertz")),
            new Range(Double.valueOf(0), Double.valueOf(25)), new LocalizedText("en", "text"),
            AxisScaleEnumeration.Linear, new Double[3]),
        new AxisInformation(
            new EUInformation("http://www.opcfoundation.org/UA/units/un/cefact", Integer.valueOf(3),
                new LocalizedText("en", "kHz"), new LocalizedText("en", "kilohertz")),
            new Range(Double.valueOf(4), Double.valueOf(50)), new LocalizedText("en", "text"),
            AxisScaleEnumeration.Linear, new Double[3])};
    nDimensionArrayItemNode.setAxisDefinition(infos);

    nodeMap.put(nDimensionArrayItemNode.getNodeId(), nDimensionArrayItemNode);
    addReference(nDimensionArrayItemNode, pNode);
  }

  /**
   * @fn void buildNodes(ServerNodeMap nodeMap, UaVariableNode node, EdgeNodeItem item)
   * @brief load data access types to make simple server node
   * @param [in] nodeMap ServerNodeMap
   * @param [in] node UaVariableNode
   * @param [in] item EdgeNodeItem
   * @return void
   */
  public void buildNodes(ServerNodeMap nodeMap, UaVariableNode node, EdgeNodeItem item) {
    EdgeNodeIdentifier type = item.getDataAccessNodeId();

    if (EdgeNodeIdentifier.DataItemType == type) {
      buildDataItemNode(nodeMap, node, item);
    } else if (EdgeNodeIdentifier.AnalogItemType == type) {
      buildAnalogItemNode(nodeMap, node, item);
    } else if (EdgeNodeIdentifier.TwoStateDiscreteType == type) {
      buildTwoStateDiscreteNode(nodeMap, node, item);
    } else if (EdgeNodeIdentifier.MultiStateDiscreteType == type) {
      buildMultiStateDiscreteNode(nodeMap, node, item);
    } else if (EdgeNodeIdentifier.MultiStateValueDiscreteType == type) {
      builMultiStateValueDiscreteNode(nodeMap, node, item);
    } else if (EdgeNodeIdentifier.ArrayItemType == type) {
      buildArrayItemNode(nodeMap, node, item);
    } else if (EdgeNodeIdentifier.YArrayItemType == type) {
      buildYArrayItemNode(nodeMap, node, item);
    } else if (EdgeNodeIdentifier.XYArrayItemType == type) {
      buildXYArrayItemNode(nodeMap, node, item);
    } else if (EdgeNodeIdentifier.ImageItemType == type) {
      buildImageItemNode(nodeMap, node, item);
    } else if (EdgeNodeIdentifier.NDimensionArrayItemType == type) {
      buildNDimensionItemNode(nodeMap, node, item);
    } else {
      buildDataItemNode(nodeMap, node, item);
    }
  }

  private static class EdgeDANamespaceIndexMap extends EdgeAbstractNamespaceIdxMap {
  }
}
