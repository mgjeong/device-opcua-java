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

package org.edge.protocol.opcua.namespace;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.AccessContext;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.MethodInvocationHandler;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.api.Namespace;
import org.eclipse.milo.opcua.sdk.server.nodes.AttributeContext;
import org.eclipse.milo.opcua.sdk.server.nodes.ServerNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaMethodNode;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.eclipse.milo.opcua.stack.core.types.structured.WriteValue;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edge.protocol.opcua.api.server.EdgeArgumentType;
import org.edge.protocol.opcua.api.server.EdgeNodeItem;
import org.edge.protocol.opcua.api.server.EdgeReference;
import org.edge.protocol.opcua.node.EdgeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Lists;

/**
 * This class provide functions for Namespace
 */
public class EdgeNamespace implements Namespace {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final EdgeNode edgeNode;
  private final SubscriptionModel subscriptionModel;
  private final OpcUaServer serverContext;
  private final UShort namespaceIndex;
  private final String namespaceUri;
  private String rootNodeIdentifier;
  private String rootNodeBrowseName;
  private String rootNodeDisplayName;

  /**
   * This nested class provide function for build EdgeNamespace
   */
  public static class Builder {
    private final OpcUaServer serverContext;
    private final UShort namespaceIndex;
    private final String namespaceUri;
    private String rootNodeIdentifier = null;
    private String rootNodeBrowseName = null;
    private String rootNodeDisplayName = null;

    /**
     * @brief CTOR
     * @param [in] server
     * @param [in] idx
     * @param [in] uri
     */
    public Builder(OpcUaServer server, UShort idx, String uri) {
      this.serverContext = server;
      this.namespaceIndex = idx;
      this.namespaceUri = uri;
    }

    /**
     * @fn Builder setNodeId(String id)
     * @brief set node id
     * @param id
     * @return Builder
     */
    public Builder setNodeId(String id) {
      rootNodeIdentifier = id;
      return this;
    }

    /**
     * @fn Builder setBrowseName(String name)
     * @brief set browse name
     * @param name
     * @return Builder
     */
    public Builder setBrowseName(String name) {
      rootNodeBrowseName = name;
      return this;
    }

    /**
     * @fn Builder setDisplayName(String name)
     * @brief set display name
     * @param name
     * @return Builder
     */
    public Builder setDisplayName(String name) {
      rootNodeDisplayName = name;
      return this;
    }

    /**
     * @fn EdgeNamespace build()
     * @brief build EdgeNamespace
     * @return EdgeNamespace
     */
    public EdgeNamespace build() {
      return new EdgeNamespace(this);
    }
  }

  /**
   * @brief CTOR
   * @param builder
   */
  private EdgeNamespace(Builder builder) {
    serverContext = builder.serverContext;
    namespaceIndex = builder.namespaceIndex;
    namespaceUri = builder.namespaceUri;
    rootNodeIdentifier = builder.rootNodeIdentifier;
    rootNodeBrowseName = builder.rootNodeBrowseName;
    rootNodeDisplayName = builder.rootNodeDisplayName;
    subscriptionModel = new SubscriptionModel(serverContext, this);
    edgeNode = new EdgeNode(serverContext, namespaceIndex, rootNodeIdentifier, rootNodeBrowseName,
        rootNodeDisplayName);
  }

  /**
   * @fn EdgeNode getEdgeNode()
   * @brief get EdgeNode
   * @return EdgeNode
   */
  public EdgeNode getEdgeNode() {
    return edgeNode;
  }

  /**
   * @fn UShort getNamespaceIndex()
   * @brief get namespaceIndex
   * @return namespaceIndex
   */
  @Override
  public UShort getNamespaceIndex() {
    return namespaceIndex;
  }

  /**
   * @fn UShort getNamespaceUri()
   * @brief get namespaceUri
   * @return namespaceUri
   */
  @Override
  public String getNamespaceUri() {
    return namespaceUri;
  }

  /**
   * @fn CompletableFuture<List<Reference>> browse(AccessContext context, NodeId nodeId)
   * @brief browse
   * @param [in] context AccessContext (not used)
   * @param [in] nodeId nodeId
   * @return CompletableFuture<List<Reference>>
   */
  @Override
  public CompletableFuture<List<Reference>> browse(AccessContext context, NodeId nodeId) {
    ServerNode node = edgeNode.getNodeMap().get(nodeId);

    if (node != null) {
      return CompletableFuture.completedFuture(node.getReferences());
    } else {
      CompletableFuture<List<Reference>> f = new CompletableFuture<>();
      f.completeExceptionally(new UaException(StatusCodes.Bad_NodeIdUnknown));
      return f;
    }
  }

  /**
   * @fn read(ReadContext context, Double maxAge, TimestampsToReturn timestamps, List<ReadValueId>
   *     readValueIds)
   * @brief read
   * @param [in] context ReadContext
   * @param [in] maxAge maxAge
   * @param [in] timestamps timestamps
   * @param [in] readValueIds List<ReadValueId>
   * @return void
   */
  @Override
  public void read(ReadContext context, Double maxAge, TimestampsToReturn timestamps,
      List<ReadValueId> readValueIds) {
    List<DataValue> results = Lists.newArrayListWithCapacity(readValueIds.size());

    for (ReadValueId id : readValueIds) {
      ServerNode node = edgeNode.getNodeMap().get(id.getNodeId());

      if (node != null) {
        DataValue value = node.readAttribute(new AttributeContext(context), id.getAttributeId(),
            timestamps, id.getIndexRange());

        if (logger.isTraceEnabled()) {
          Variant variant = value.getValue();
          Object o = variant != null ? variant.getValue() : null;
          logger.trace("Read value={} from attributeId={} of {}", o, id.getAttributeId(),
              id.getNodeId());
        }

        results.add(value);
      } else {
        results.add(new DataValue(new StatusCode(StatusCodes.Bad_NodeIdUnknown)));
      }
    }

    context.complete(results);
  }

  /**
   * @fn write(WriteContext context, List<WriteValue> writeValues)
   * @brief write
   * @param [in] context WriteContext
   * @param [in] writeValues List<WriteValue>
   * @return void
   */
  @Override
  public void write(WriteContext context, List<WriteValue> writeValues) {
    List<StatusCode> results = Lists.newArrayListWithCapacity(writeValues.size());

    for (WriteValue writeValue : writeValues) {
      try {
        ServerNode node = edgeNode.getNodeMap().getNode(writeValue.getNodeId())
            .orElseThrow(() -> new UaException(StatusCodes.Bad_NodeIdUnknown));

        node.writeAttribute(new AttributeContext(context), writeValue.getAttributeId(),
            writeValue.getValue(), writeValue.getIndexRange());

        if (logger.isTraceEnabled()) {
          Variant variant = writeValue.getValue().getValue();
          Object o = variant != null ? variant.getValue() : null;
          logger.trace("Wrote value={} to attributeId={} of {}", o, writeValue.getAttributeId(),
              writeValue.getNodeId());
        }

        results.add(StatusCode.GOOD);
      } catch (UaException e) {
        results.add(e.getStatusCode());
      }
    }

    context.complete(results);
  }

  @Override
  public Optional<MethodInvocationHandler> getInvocationHandler(NodeId methodId) {
    ServerNode node = edgeNode.getNodeMap().get(methodId);

    if (node instanceof UaMethodNode) {
      return ((UaMethodNode) node).getInvocationHandler();
    } else {
      return Optional.empty();
    }
  }

  /**
   * @fn EdgeResult createNode(EdgeNodeItem item)
   * @brief create variable node depend on OPC-UA on server side (createNamespace should be called before)
   * @param [in] item node item
   * @return result
   */
  public EdgeResult createNodes(EdgeNodeItem item) throws Exception {
    return edgeNode.addNodes(item);
  }

  /**
   * @fn addReferences(EdgeReference reference)
   * @brief add reference with node
   * @param [in] EdgeRefernce reference
   * @return result
   */
  public EdgeResult addReferences(EdgeReference reference) throws Exception {
    return edgeNode.addReference(reference);
  }

  /**
   * @fn EdgeResult modifyVariableNodeValue(String nodeUri, EdgeVersatility value)
   * @brief modify value of target variable node on server side
   * @param [in] nodeUri node uri related variable node
   * @param [in] value modified value
   * @return result
   */
  public EdgeResult modifyNode(String nodeUri, EdgeVersatility value) throws Exception{
    return edgeNode.modifyNode(nodeUri, new Variant(value.getValue()));
  }

  /**
   * @fn EdgeResult modifyVariableNodeValue(EdgeNodeIdentifier type, EdgeVersatility value)
   * @brief modify value of target variable node on server side
   * @param [in] id data access node id
   * @param [in] value modified value
   * @return result
   */
  public EdgeResult modifyNode(EdgeNodeIdentifier id, EdgeVersatility value) throws Exception{
    return edgeNode.modifyNode(id, new Variant(value.getValue()));
  }

  /**
   * @fn EdgeResult createDataAccessNode(EdgeNodeItem item)
   * @brief create data access node depend on OPC-UA on server side (createNamespace should be called before)
   * @param [in] item node item
   * @return result
   */
  public EdgeResult createDataAccessNode(EdgeNodeItem item) throws Exception{
    return edgeNode.addDataAccessNode(item);
  }

  /**
   * @fn EdgeResult createMethodNode(EdgeNodeItem item)
   * @brief create method node depend on OPC-UA on server side (createNamespace should be called before)
   * @param [in] item node item
   * @param [in] methodObj method class
   * @param [in] type the argument type of the method
   * @return result
   */
  public EdgeResult createMethodNode(EdgeNodeItem item, Object methodObj, EdgeArgumentType type) throws Exception {
    return edgeNode.addMethodNode(item, methodObj, type);
  }

  @Override
  public void onDataItemsCreated(List<DataItem> arg0) {
    // TODO Auto-generated method stub
    subscriptionModel.onDataItemsCreated(arg0);
  }

  @Override
  public void onDataItemsDeleted(List<DataItem> arg0) {
    // TODO Auto-generated method stub
    subscriptionModel.onDataItemsDeleted(arg0);
  }

  @Override
  public void onDataItemsModified(List<DataItem> arg0) {
    // TODO Auto-generated method stub
    subscriptionModel.onDataItemsModified(arg0);
  }

  @Override
  public void onMonitoringModeChanged(List<MonitoredItem> arg0) {
    // TODO Auto-generated method stub
    subscriptionModel.onMonitoringModeChanged(arg0);
  }
}
