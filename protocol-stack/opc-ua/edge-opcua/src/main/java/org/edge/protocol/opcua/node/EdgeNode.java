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

package org.edge.protocol.opcua.node;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ubyte;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.core.ValueRank;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.ServerNodeMap;
import org.eclipse.milo.opcua.sdk.server.model.nodes.variables.AnalogItemNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaDataTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaMethodNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaObjectNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaObjectTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaReferenceTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaViewNode;
import org.eclipse.milo.opcua.sdk.server.util.AnnotationBasedInvocationHandler;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.edge.protocol.opcua.api.common.EdgeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeNodeId;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeNodeType;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.api.server.EdgeArgumentType;
import org.edge.protocol.opcua.api.server.EdgeNodeItem;
import org.edge.protocol.opcua.api.server.EdgeReference;
import org.edge.protocol.opcua.namespace.EdgeNamespaceManager;
import org.edge.protocol.opcua.node.loader.EdgeDataAccessLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;

public class EdgeNode {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final ServerNodeMap nodeManager;
  private final UaFolderNode rootNode;
  private final UShort namespaceIndex;
  private final OpcUaServer serverContext;
  private UaObjectNode folderNode = null;

  private UaObjectNode lineNumNode;

  /**
   * @fn EdgeNode(OpcUaServer server, UShort namespaceIndex, String rootNodeIdentifier, String
   *     rootNodeBrowseName, String rootNodeDisplayName)
   * @brief constructor (root node is set)
   * @param [in] server target server instance
   * @param [in] namespaceIndex namespaceIndex to make root NodeId
   * @param [in] rootNodeIdentifier node Identifier to make root NodeId
   * @param [in] rootNodeBrowseName browse name to make UaFolderNode
   * @param [in] rootNodeDisplayName display name to make UaFolderNode
   * @return void
   */
  public EdgeNode(OpcUaServer server, UShort namespaceIndex, String rootNodeIdentifier,
      String rootNodeBrowseName, String rootNodeDisplayName) {
    this.serverContext = server;
    this.nodeManager = serverContext.getNodeMap();
    this.namespaceIndex = namespaceIndex;

    NodeId folderNodeId = new NodeId(namespaceIndex, rootNodeIdentifier);

    rootNode = new UaFolderNode(nodeManager, folderNodeId,
        new QualifiedName(namespaceIndex, rootNodeBrowseName),
        LocalizedText.english(rootNodeDisplayName));
    nodeManager.addNode(rootNode);

    // Make sure our new folder shows up under the server's Objects folder
    try {
      serverContext.getUaNamespace().addReference(Identifiers.ObjectsFolder, Identifiers.Organizes,
          true, folderNodeId.expanded(), NodeClass.Object);
    } catch (UaException e1) {
      e1.printStackTrace();
    }
  }

  /**
   * @fn ServerNodeMap getNodeMap()
   * @brief get node map
   * @return nodeManager
   */
  public ServerNodeMap getNodeMap() {
    return nodeManager;
  }

  /**
   * @fn EdgeResult addNodes(EdgeNodeItem item)
   * @brief add nodes by EdgeNodeItem parameter - VARIABLE_NODE, ARRAY_NODE, VARIABLE_TYPE_NODE
   *        should be needed to set variableItemSet in EdgeNodeItem
   * @return result
   */
  public EdgeResult addNodes(EdgeNodeItem item) throws Exception {
    EdgeStatusCode code = EdgeStatusCode.STATUS_OK;
    if (EdgeIdentifier.VARIABLE_NODE == item.getEdgeNodeType()) {

      if (item.getVariableItemSet() == null) {
        code = EdgeStatusCode.STATUS_PARAM_INVALID;
      } else {
        for (Object[] obj : item.getVariableItemSet()) {
          try {
            addVariableNode(item, obj);
            logger.info("add variable node = {}\n", obj[0]);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    } else if (EdgeIdentifier.ARRAY_NODE == item.getEdgeNodeType()) {

      if (item.getVariableItemSet() == null) {
        code = EdgeStatusCode.STATUS_PARAM_INVALID;
      } else {

        for (Object[] obj : item.getVariableItemSet()) {
          try {
            addArrayNode(item, obj);
            logger.info("add array node = {}\n", obj[0]);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    } else if (EdgeIdentifier.VARIABLE_TYPE_NODE == item.getEdgeNodeType()) {
      if (item.getVariableItemSet() == null) {
        code = EdgeStatusCode.STATUS_PARAM_INVALID;
      } else {
        for (Object[] obj : item.getVariableItemSet()) {
          try {
            addVariableTypeNode(item, obj);
            logger.info("add variable type node = {}\n", obj[0]);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    } else if (EdgeIdentifier.OBJECT_NODE == item.getEdgeNodeType()) {
      logger.info("add object node = {}\n", item.getBrowseName());
      addObjectNode(item);
    } else if (EdgeIdentifier.OBJECT_TYPE_NODE == item.getEdgeNodeType()) {
      addObjectTypeNode(item);
    } else if (EdgeIdentifier.REFERENCE_TYPE_NODE == item.getEdgeNodeType()) {
      addReferenceTypeNode(item);
    } else if (EdgeIdentifier.DATA_TYPE_NODE == item.getEdgeNodeType()) {
      addDataTypeNode(item);
    } else if (EdgeIdentifier.VIEW_NODE == item.getEdgeNodeType()) {
      addViewNode(item);
    } else {
      addFolderNode(item);
    }
    return new EdgeResult.Builder(code).build();
  }

  /**
   * @fn EdgeResult addDataAccessNode(EdgeNodeItem item)
   * @brief initialize Data Access Node (for test)
   * @param [in] item EdgeNodeItem
   * @return result
   */
  public EdgeResult addDataAccessNode(EdgeNodeItem item) {
    EdgeStatusCode code = EdgeStatusCode.STATUS_OK;
    UaVariableNode node =
        (UaVariableNode) nodeManager.get(new NodeId(namespaceIndex, item.getBrowseName()));

    if (null == node) {
      logger.error("pNode is not available={}", item.getBrowseName());
      code = EdgeStatusCode.STATUS_ERROR;
    } else {
      try {
        EdgeDataAccessLoader.getInstance().buildNodes(nodeManager, node, item);
      } catch (Exception e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
        code = EdgeStatusCode.STATUS_ERROR;
      }
    }
    return new EdgeResult.Builder(code).build();
  }

  /**
   * @fn EdgeResult addMethodNode(EdgeNodeItem item)
   * @brief initialize Custom Method Node (for test)
   * @param [in] item EdgeNodeItem
   * @param [in] methodObj the object of the method class
   * @param [in] type the argument type of the method
   * @return result
   */
  public EdgeResult addMethodNode(EdgeNodeItem item, Object methodObj, EdgeArgumentType type)
      throws Exception {
    EdgeStatusCode code = EdgeStatusCode.STATUS_OK;
    UaNode node = (UaNode) nodeManager.get(new NodeId(item.getSourceNode().getNameSpace(),
        (String) item.getSourceNode().getIdentifier()));
    if (node != null) {
      logger.info("add method node ={}", node.getBrowseName());
      addCustomMethodNode(node, item.getBrowseName(), methodObj, type);
    } else {
      logger.info("node is not available");
      code = EdgeStatusCode.STATUS_ERROR;
    }
    return new EdgeResult.Builder(code).build();
  }

  private UaVariableNode addVariableNode(UaObjectNode pNode, EdgeNodeItem item, Object[] obj)
      throws Exception {
    String name = (String) obj[0];
    Preconditions.checkArgument(name == null || name.length() < 1000, "name is too long");

    NodeId typeId = (NodeId) obj[1];
    Variant variant = (Variant) obj[2];

    UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(nodeManager)
        .setNodeId(new NodeId(namespaceIndex, item.getBrowseName() + name))
        .setAccessLevel(ubyte(item.getAccessLevel()))
        .setUserAccessLevel(ubyte(item.getUserAccessLevel())).setHistorizing(true)
        .setWriteMask(UInteger.valueOf(item.getWriteMask()))
        .setUserWriteMask(UInteger.valueOf(item.getUserWriteMask()))
        .setBrowseName(new QualifiedName(namespaceIndex, name))
        .setDisplayName(LocalizedText.english(name)).setDataType(typeId)
        .setTypeDefinition(Identifiers.BaseDataVariableType).build();

    node.setValue(new DataValue(variant));

    pNode.addReference(new Reference(pNode.getNodeId(), Identifiers.Organizes,
        node.getNodeId().expanded(), node.getNodeClass(), item.getForward()));
    node.addReference(new Reference(node.getNodeId(), Identifiers.Organizes,
        pNode.getNodeId().expanded(), pNode.getNodeClass(), item.getForward() ? false : true));

    logger.info("Added reference: {} -> {}", pNode.getNodeId(), node.getNodeId());

    nodeManager.put(node.getNodeId(), node);

    return node;
  }

  private UaVariableNode addVariableNode(EdgeNodeItem item, Object[] obj) throws Exception {
    String name = (String) obj[0];
    Preconditions.checkArgument(name == null || name.length() < 1000, "name is too long");

    NodeId typeId = (NodeId) obj[1];
    Variant variant = (Variant) obj[2];

    UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(nodeManager)
        .setNodeId(new NodeId(namespaceIndex, item.getBrowseName() + name))
        .setAccessLevel(ubyte(item.getAccessLevel()))
        .setUserAccessLevel(ubyte(item.getUserAccessLevel())).setHistorizing(true)
        .setWriteMask(UInteger.valueOf(item.getWriteMask()))
        .setUserWriteMask(UInteger.valueOf(item.getUserWriteMask()))
        .setBrowseName(new QualifiedName(namespaceIndex, name))
        .setDisplayName(LocalizedText.english(name)).setDataType(typeId)
        .setTypeDefinition(Identifiers.BaseDataVariableType).build();

    node.setValue(new DataValue(variant));
    UaNode pNode = getUaNode(item.getSourceNode());

    pNode.addReference(new Reference(pNode.getNodeId(), Identifiers.Organizes,
        node.getNodeId().expanded(), node.getNodeClass(), item.getForward()));
    node.addReference(new Reference(node.getNodeId(), Identifiers.Organizes,
        pNode.getNodeId().expanded(), pNode.getNodeClass(), !item.getForward()));

    logger.info("Added reference: {} -> {}", pNode.getNodeId(), node.getNodeId());

    nodeManager.put(node.getNodeId(), node);

    return node;
  }

  private UaVariableNode addArrayNode(EdgeNodeItem item, Object[] obj) throws Exception {
    String name = (String) obj[0];
    Preconditions.checkArgument(name == null || name.length() < 1000, "name is too long");

    NodeId typeId = (NodeId) obj[1];
    Variant variant = (Variant) obj[2];
    UInteger[] array = {UInteger.valueOf(0)};

    UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(nodeManager)
        .setNodeId(new NodeId(namespaceIndex, item.getBrowseName() + name))
        .setAccessLevel(ubyte(item.getAccessLevel()))
        .setUserAccessLevel(ubyte(item.getUserAccessLevel()))
        .setWriteMask(UInteger.valueOf(item.getWriteMask()))
        .setUserWriteMask(UInteger.valueOf(item.getUserWriteMask()))
        .setBrowseName(new QualifiedName(namespaceIndex, name))
        .setDisplayName(LocalizedText.english(name)).setDataType(typeId)
        .setValueRank(ValueRank.OneDimension.getValue()).setArrayDimensions(array)
        .setTypeDefinition(Identifiers.BaseDataVariableType).build();

    node.setValue(new DataValue(variant));
    UaNode pNode = getUaNode(item.getSourceNode());

    pNode.addReference(new Reference(pNode.getNodeId(), Identifiers.Organizes,
        node.getNodeId().expanded(), node.getNodeClass(), true));
    node.addReference(new Reference(node.getNodeId(), Identifiers.Organizes,
        pNode.getNodeId().expanded(), pNode.getNodeClass(), false));

    logger.info("Added reference: {} -> {}", pNode.getNodeId(), node.getNodeId());

    nodeManager.put(node.getNodeId(), node);

    return node;
  }

  private UaObjectNode addObjectNode(EdgeNodeItem item) {
    UaNode pNode = getUaNode(item.getSourceNode());

    UaObjectNode node = new UaObjectNode.UaObjectNodeBuilder(nodeManager)
        .setNodeId(new NodeId(namespaceIndex, item.getBrowseName()))
        .setWriteMask(UInteger.valueOf(item.getWriteMask()))
        .setUserWriteMask(UInteger.valueOf(item.getUserWriteMask()))
        .setBrowseName(new QualifiedName(namespaceIndex, item.getBrowseName()))
        .setDisplayName(LocalizedText.english(item.getBrowseName()))
        .setTypeDefinition(Identifiers.BaseObjectType).build();

    pNode.addReference(new Reference(pNode.getNodeId(), Identifiers.Organizes,
        node.getNodeId().expanded(), node.getNodeClass(), true));
    node.addReference(new Reference(node.getNodeId(), Identifiers.Organizes,
        pNode.getNodeId().expanded(), pNode.getNodeClass(), false));

    logger.info("Added reference: {} -> {}", pNode.getNodeId(), node.getNodeId());

    nodeManager.put(node.getNodeId(), node);
    return node;
  }

  private UaVariableTypeNode addVariableTypeNode(EdgeNodeItem item, Object[] obj) throws Exception {
    String name = (String) obj[0];
    Preconditions.checkArgument(name == null || name.length() < 1000, "name is too long");

    NodeId typeId = (NodeId) obj[1];
    Variant variant = (Variant) obj[2];

    UaNode pNode = getUaNode(item.getSourceNode());

    UaVariableTypeNode node =
        new UaVariableTypeNode(nodeManager, new NodeId(namespaceIndex, item.getBrowseName() + name),
            new QualifiedName(namespaceIndex, name), LocalizedText.english(name), null,
            UInteger.valueOf(item.getWriteMask()), UInteger.valueOf(item.getWriteMask()),
            new DataValue(variant), typeId, null, null, false);

    pNode.addReference(new Reference(pNode.getNodeId(), Identifiers.Organizes,
        node.getNodeId().expanded(), node.getNodeClass(), true));
    node.addReference(new Reference(node.getNodeId(), Identifiers.Organizes,
        pNode.getNodeId().expanded(), pNode.getNodeClass(), false));

    logger.info("Added reference: {} -> {}", pNode.getNodeId(), node.getNodeId());
    nodeManager.put(node.getNodeId(), node);

    return node;
  }

  private UaObjectTypeNode addObjectTypeNode(EdgeNodeItem item) {
    UaNode pNode = getUaNode(item.getSourceNode());

    UaObjectTypeNode node = new UaObjectTypeNode(nodeManager,
        new NodeId(namespaceIndex, item.getBrowseName()),
        new QualifiedName(namespaceIndex, item.getBrowseName()),
        LocalizedText.english(item.getBrowseName()), null, UInteger.valueOf(item.getWriteMask()),
        UInteger.valueOf(item.getUserWriteMask()), false);

    pNode.addReference(new Reference(pNode.getNodeId(), Identifiers.Organizes,
        node.getNodeId().expanded(), node.getNodeClass(), true));
    node.addReference(new Reference(node.getNodeId(), Identifiers.Organizes,
        pNode.getNodeId().expanded(), pNode.getNodeClass(), false));

    logger.info("Added reference: {} -> {}", pNode.getNodeId(), node.getNodeId());
    nodeManager.put(node.getNodeId(), node);

    return node;
  }

  private UaReferenceTypeNode addReferenceTypeNode(EdgeNodeItem item) {
    UaNode pNode = getUaNode(item.getSourceNode());

    UaReferenceTypeNode node = new UaReferenceTypeNode(nodeManager,
        new NodeId(namespaceIndex, item.getBrowseName()),
        new QualifiedName(namespaceIndex, item.getBrowseName()),
        LocalizedText.english(item.getBrowseName()), null, UInteger.valueOf(item.getWriteMask()),
        UInteger.valueOf(item.getUserWriteMask()), false, false, null);

    pNode.addReference(new Reference(pNode.getNodeId(), Identifiers.Organizes,
        node.getNodeId().expanded(), node.getNodeClass(), true));
    node.addReference(new Reference(node.getNodeId(), Identifiers.Organizes,
        pNode.getNodeId().expanded(), pNode.getNodeClass(), false));

    logger.info("Added reference: {} -> {}", pNode.getNodeId(), node.getNodeId());
    nodeManager.put(node.getNodeId(), node);

    return node;
  }

  private UaDataTypeNode addDataTypeNode(EdgeNodeItem item) {
    UaNode pNode = getUaNode(item.getSourceNode());
    UaDataTypeNode node = new UaDataTypeNode(nodeManager,
        new NodeId(namespaceIndex, item.getBrowseName()),
        new QualifiedName(namespaceIndex, item.getBrowseName()),
        LocalizedText.english(item.getBrowseName()), null, UInteger.valueOf(item.getWriteMask()),
        UInteger.valueOf(item.getUserWriteMask()), false);

    pNode.addReference(new Reference(pNode.getNodeId(), Identifiers.Organizes,
        node.getNodeId().expanded(), node.getNodeClass(), true));
    node.addReference(new Reference(node.getNodeId(), Identifiers.Organizes,
        pNode.getNodeId().expanded(), pNode.getNodeClass(), false));

    logger.info("Added reference: {} -> {}", pNode.getNodeId(), node.getNodeId());
    nodeManager.put(node.getNodeId(), node);

    return node;
  }

  private UaViewNode addViewNode(EdgeNodeItem item) {
    UaNode pNode = null;
    UaViewNode viewNode = null;
    if (nodeManager.containsNodeId(new NodeId(namespaceIndex, item.getBrowseName())) == false) {
      viewNode = new UaViewNode(nodeManager, new NodeId(namespaceIndex, item.getBrowseName()),
          new QualifiedName(namespaceIndex, item.getBrowseName()),
          LocalizedText.english(item.getBrowseName()), null, UInteger.valueOf(item.getWriteMask()),
          UInteger.valueOf(item.getUserWriteMask()), true, null);

      // add view node into viewsFolder
      pNode = (UaNode) nodeManager.get(new NodeId(EdgeOpcUaCommon.SYSTEM_NAMESPACE_INDEX,
          EdgeNodeIdentifier.ViewsFolder.value()));
      pNode.addReference(new Reference(pNode.getNodeId(), Identifiers.Organizes,
          viewNode.getNodeId().expanded(), viewNode.getNodeClass(), true));
      viewNode.addReference(new Reference(viewNode.getNodeId(), Identifiers.Organizes,
          pNode.getNodeId().expanded(), pNode.getNodeClass(), false));

      logger.info("add view node = {}\n", item.getBrowseName());
    } else {
      viewNode = (UaViewNode) nodeManager.get(new NodeId(namespaceIndex, item.getBrowseName()));
    }

    if (item.getSourceNode() != null) {
      UaObjectNode cNode = (UaObjectNode) getUaNode(item.getSourceNode());
      addViewReference(viewNode, cNode);

      logger.info("Added reference for ViewNode: {}", viewNode.getNodeId());
      logger.info("Added reference for ViewNode2: {}", cNode.getNodeId());
    }
    nodeManager.put(viewNode.getNodeId(), viewNode);

    return viewNode;
  }

  private void addViewReference(UaViewNode pNode, UaObjectNode cNode) {
    pNode.addReference(new Reference(pNode.getNodeId(), Identifiers.Organizes,
        cNode.getNodeId().expanded(), cNode.getNodeClass(), true));
    cNode.addReference(new Reference(cNode.getNodeId(), Identifiers.Organizes,
        pNode.getNodeId().expanded(), pNode.getNodeClass(), false));
  }

  private UaObjectNode addSingleFolderNode(UaFolderNode pNode, String nodeName,
      Object[][] nodeItemSet) {
    UaFolderNode folderNode = new UaFolderNode(nodeManager, new NodeId(namespaceIndex, nodeName),
        new QualifiedName(namespaceIndex, nodeName), LocalizedText.english(nodeName));

    nodeManager.addNode(folderNode);
    pNode.addOrganizes(folderNode);
    return folderNode;
  }

  private UaObjectNode addMultiFolderNode(UaNode root, String path) {
    if (path.startsWith("/"))
      path = path.substring(1, path.length());
    String[] elements = path.split("/");

    LinkedList<UaObjectNode> folderNodes = processPathElements(Lists.newArrayList(elements),
        Lists.newArrayList(), Lists.newLinkedList());

    UaObjectNode firstNode = folderNodes.getFirst();

    if (!nodeManager.containsKey(firstNode.getNodeId())) {
      nodeManager.put(firstNode.getNodeId(), firstNode);

      nodeManager.get(root.getNodeId()).addReference(new Reference(root.getNodeId(),
          Identifiers.Organizes, firstNode.getNodeId().expanded(), firstNode.getNodeClass(), true));

      logger.info("Added reference: {} -> {}", root.getNodeId(), firstNode.getNodeId());
    }

    PeekingIterator<UaObjectNode> iterator = Iterators.peekingIterator(folderNodes.iterator());

    while (iterator.hasNext()) {
      UaObjectNode node = iterator.next();

      nodeManager.putIfAbsent(node.getNodeId(), node);

      if (iterator.hasNext()) {
        UaObjectNode next = iterator.peek();

        if (!nodeManager.containsKey(next.getNodeId())) {
          nodeManager.put(next.getNodeId(), next);

          nodeManager.get(node.getNodeId()).addReference(new Reference(node.getNodeId(),
              Identifiers.Organizes, next.getNodeId().expanded(), next.getNodeClass(), true));

          logger.info("Added reference: {} -> {}", node.getNodeId(), next.getNodeId());
        }
      }
    }
    return folderNodes.getLast();
  }

  private LinkedList<UaObjectNode> processPathElements(List<String> elements, List<String> path,
      LinkedList<UaObjectNode> nodes) {
    if (elements.size() == 1) {
      String name = elements.get(0);
      String prefix = String.join("/", path) + "/";
      if (!prefix.startsWith("/"))
        prefix = "/" + prefix;

      logger.debug("setBrowseName: {}", new QualifiedName(namespaceIndex, name));

      UaObjectNode node =
          UaObjectNode.builder(nodeManager).setNodeId(new NodeId(namespaceIndex, prefix + name))
              .setBrowseName(new QualifiedName(namespaceIndex, name))
              .setDisplayName(LocalizedText.english(name)).setTypeDefinition(Identifiers.FolderType)
              .build();

      nodes.add(node);

      return nodes;
    } else {
      String name = elements.get(0);
      String prefix = String.join("/", path) + "/";
      if (!prefix.startsWith("/"))
        prefix = "/" + prefix;

      logger.debug("setBrowseName: {}", new QualifiedName(namespaceIndex, name));

      lineNumNode =
          UaObjectNode.builder(nodeManager).setNodeId(new NodeId(namespaceIndex, prefix + name))
              .setBrowseName(new QualifiedName(namespaceIndex, name))
              .setDisplayName(LocalizedText.english(name)).setTypeDefinition(Identifiers.FolderType)
              .build();

      nodes.add(lineNumNode);
      path.add(name);

      return processPathElements(elements.subList(1, elements.size()), path, nodes);
    }
  }

  /**
   * @fn EdgeResult modifyNode(int ns, EdgeNodeIdentifier type, Variant var)
   * @brief modifyNode for General VariableNode (for test)
   * @param [in] nodeUri nodeUri to make target NodeId
   * @param [in] var changed value
   * @return result
   */
  public EdgeResult modifyNode(String nodeUri, Variant var) {
    EdgeStatusCode code = EdgeStatusCode.STATUS_OK;
    UaVariableNode node = (UaVariableNode) nodeManager.get(new NodeId(namespaceIndex, nodeUri));

    if (node != null) {
      node.setValue(new DataValue(var));
    } else {
      code = EdgeStatusCode.STATUS_ERROR;
    }
    return new EdgeResult.Builder(code).build();
  }

  /**
   * @fn EdgeResult modifyNode(int ns, EdgeNodeIdentifier type, Variant var)
   * @brief modifyNode for DataAccess (for test)
   * @param [in] ns namespace to make target NodeId
   * @param [in] type node type to make target NodeId
   * @param [in] var changed value
   * @return result
   */
  public EdgeResult modifyNode(EdgeNodeIdentifier type, Variant var) {
    EdgeStatusCode code = EdgeStatusCode.STATUS_OK;
    // TODO
    // this logger will be removed.
    // logger.info("Modify AnalogItem node = {}, value={}",
    // NodeId.parse("ns="
    // + UShort.valueOf(ns) + ";i=" + type.value()), var);
    AnalogItemNode node = (AnalogItemNode) nodeManager
        .get(NodeId.parse("ns=" + this.namespaceIndex + ";i=" + type.value()));

    if (node != null) {
      node.setValue(new DataValue(var));
    } else {
      code = EdgeStatusCode.STATUS_ERROR;
    }

    return new EdgeResult.Builder(code).build();
  }

  private void addFolderNode(EdgeNodeItem item) {

    if (EdgeIdentifier.SINGLE_FOLDER_NODE_TYPE == item.getEdgeNodeType()) {
      folderNode = addSingleFolderNode(rootNode, item.getBrowseName(), item.getVariableItemSet());
      if (Optional.ofNullable(item.getVariableItemSet()).isPresent()) {
        for (Object[] obj : item.getVariableItemSet()) {
          try {
            addVariableNode(folderNode, item, obj);
            logger.info("add variable node = {}\n", obj[0]);
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }
    } else if (EdgeIdentifier.MILTI_FOLDER_NODE_TYPE == item.getEdgeNodeType()) {
      folderNode = addMultiFolderNode(rootNode, item.getBrowseName());
      if (Optional.ofNullable(item.getVariableItemSet()).isPresent()) {
        for (Object[] obj : item.getVariableItemSet()) {
          try {
            addVariableNode(folderNode, item, obj);
            logger.info("add variable node = {}\n", obj[0]);
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }
    }
  }

  private EdgeResult addCustomMethodNode(UaNode pNode, String nodeName, Object method,
      EdgeArgumentType type) {
    EdgeStatusCode code = EdgeStatusCode.STATUS_OK;
    UaMethodNode methodNode =
        UaMethodNode.builder(nodeManager).setNodeId(new NodeId(namespaceIndex, nodeName))
            .setBrowseName(new QualifiedName(namespaceIndex, nodeName))
            .setDisplayName(new LocalizedText(null, nodeName))
            .setDescription(LocalizedText
                .english("Returns the correctly rounded positive square root of a double value."))
            .build();

    if (methodNode != null && pNode != null) {
      try {
        AnnotationBasedInvocationHandler invocationHandler =
            AnnotationBasedInvocationHandler.fromAnnotatedObject(nodeManager, method);

        if (type == EdgeArgumentType.IN_ARGUMENT) {
          methodNode.setProperty(UaMethodNode.InputArguments,
              invocationHandler.getInputArguments());
        } else if (type == EdgeArgumentType.OUT_ARGUMENT) {
          methodNode.setProperty(UaMethodNode.OutputArguments,
              invocationHandler.getOutputArguments());
        } else if (type == EdgeArgumentType.IN_OUT_ARGUMENTS) {
          methodNode.setProperty(UaMethodNode.InputArguments,
              invocationHandler.getInputArguments());
          methodNode.setProperty(UaMethodNode.OutputArguments,
              invocationHandler.getOutputArguments());
        }
        methodNode.setInvocationHandler(invocationHandler);

        nodeManager.addNode(methodNode);

        rootNode.addReference(new Reference(rootNode.getNodeId(), Identifiers.Organizes,
            methodNode.getNodeId().expanded(), methodNode.getNodeClass(), true));

        pNode.addReference(new Reference(pNode.getNodeId(), Identifiers.HasComponent,
            methodNode.getNodeId().expanded(), methodNode.getNodeClass(), true));

        methodNode.addReference(new Reference(methodNode.getNodeId(), Identifiers.HasComponent,
            pNode.getNodeId().expanded(), pNode.getNodeClass(), false));
      } catch (Exception e) {
        logger.error("Error creating sqrt() method.", e);
      }
    } else {
      code = EdgeStatusCode.STATUS_ERROR;
    }

    return new EdgeResult.Builder(code).build();
  }

  /**
   * @fn EdgeResult addReference(EdgeReference reference)
   * @brief add reference between source node and target node
   * @param [in] reference reference parameter - it can set direction and reference id such as
   *        NonHierarchicalReferences, HierarchicalReferences
   * @return result
   */
  public EdgeResult addReference(EdgeReference reference) {
    EdgeStatusCode code = EdgeStatusCode.STATUS_OK;
    UShort namespaceIdx = EdgeNamespaceManager.getInstance()
        .getNamespace(reference.getSourceNamespace()).getNamespaceIndex();
    UaNode sourceNode = (UaNode) serverContext.getNodeMap()
        .get(new NodeId(namespaceIdx, reference.getSourcePath()));
    namespaceIdx = EdgeNamespaceManager.getInstance().getNamespace(reference.getTargetNamespace())
        .getNamespaceIndex();
    UaNode targetNode =
        (UaNode) EdgeNamespaceManager.getInstance().getNamespace(reference.getTargetNamespace())
            .getEdgeNode().getNodeMap().get(new NodeId(namespaceIdx, reference.getTargetPath()));

    if (targetNode != null && sourceNode != null) {
      sourceNode.addReference(new Reference(sourceNode.getNodeId(),
          new NodeId(Unsigned.ushort(0),
              Unsigned.uint(reference.getReference().value().intValue())),
          targetNode.getNodeId().expanded(), targetNode.getNodeClass(), reference.getForward()));
      targetNode.addReference(new Reference(targetNode.getNodeId(),
          new NodeId(Unsigned.ushort(0),
              Unsigned.uint(reference.getReference().value().intValue())),
          sourceNode.getNodeId().expanded(), sourceNode.getNodeClass(), !reference.getForward()));
    } else {
      code = EdgeStatusCode.STATUS_ERROR;
    }

    return new EdgeResult.Builder(code).build();
  }

  private UaNode getUaNode(EdgeNodeId id) {
    if (id.getEdgeNodeType() == EdgeNodeType.INTEGER) {
      return (UaNode) nodeManager.get(new NodeId(id.getNameSpace(), (int) id.getIdentifier()));
    } else if (id.getEdgeNodeType() == EdgeNodeType.STRING) {
      return (UaNode) nodeManager.get(new NodeId(id.getNameSpace(), (String) id.getIdentifier()));
    } else if (id.getEdgeNodeType() == EdgeNodeType.UUID) {
      return (UaNode) nodeManager
          .get(new NodeId(id.getNameSpace(), (ByteString) id.getIdentifier()));
    } else {
      return null;
    }
  }
}
