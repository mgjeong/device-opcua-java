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

package org.edge.protocol.opcua.namespace;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.ServerNodeMap;
import org.eclipse.milo.opcua.sdk.server.nodes.ServerNode;
import org.edge.protocol.opcua.api.common.EdgeNodeId;
import org.edge.protocol.opcua.namespace.util.EdgeAbstractNamespaceMap;
import org.edge.protocol.opcua.namespace.util.EdgeBaseNamespaceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provide function for manage namespace
 */
public class EdgeNamespaceManager {
  private static final Logger logger = LoggerFactory.getLogger(EdgeNamespaceManager.class);
  private final EdgeBaseNamespaceMap namespaceMap = new EdgeNamespaceMap();
  private static EdgeNamespaceManager namespace = null;
  private static Object lock = new Object();

  private EdgeNamespaceManager() {}

  /**
   * @fn EdgeNamespaceManager getInstance()
   * @brief get EdgeNamespaceManager Instance
   * @return EdgeNamespaceManager Instance
   */
  public static EdgeNamespaceManager getInstance() {
    synchronized (lock) {
      if (null == namespace) {
        namespace = new EdgeNamespaceManager();
      }
      return namespace;
    }
  }

  /**
   * @fn void close()
   * @brief close EdgeNamespaceManager Instance
   * @return void
   */
  public void close() {
    namespaceMap.clear();
    namespace = null;
  }

  /**
   * @fn void addNamespace(String id, EdgeNamespace value)
   * @brief add namespace into namespaceMap to manage namespaces
   * @param [in] id namespace id
   * @param [in] value namespace instance
   * @return void
   */
  public void addNamespace(String id, EdgeNamespace value) throws Exception {
    if (false == namespaceMap.containsKey(id)) {
      namespaceMap.put(id, value);
    }
  }

  /**
   * @fn EdgeNamespace getNamespace(String id)
   * @brief get namespace
   * @param [in] id namespace id
   * @return EdgeNamespace instance
   */
  public EdgeNamespace getNamespace(String id) {
    return namespaceMap.getNode(id).get();
  }

  /**
   * @fn List<EdgeNodeId> getNodes(OpcUaServer server)
   * @brief get nodes from node manager
   * @param [in] server server context
   * @return EdgeNodeId list
   */
  public List<EdgeNodeId> getNodes(OpcUaServer server) {
    List<EdgeNodeId> nodes = new ArrayList<EdgeNodeId>();
    ServerNodeMap nodeManager = server.getNodeMap();

    for (ServerNode node : nodeManager.values()) {
      EdgeNodeId nodeId = new EdgeNodeId.Builder(node.getNodeId().getNamespaceIndex().intValue(),
          node.getNodeId().getIdentifier().toString()).build();
      nodes.add(nodeId);
    }
    return nodes;
  }

  /**
   * @fn List<EdgeNodeId> getNodes(OpcUaServer server)
   * @brief get nodes from node manager
   * @param [in] server server context
   * @param [in] browseName browse name
   * @return EdgeNodeId list
   */
  public List<EdgeNodeId> getNodes(OpcUaServer server, String browseName) {
    List<EdgeNodeId> nodes = new ArrayList<EdgeNodeId>();
    ServerNodeMap nodeManager = server.getNodeMap();

    for (ServerNode node : nodeManager.values()) {
//      if (node.getBrowseName().getName().equals(browseName)) {
      if (node.getBrowseName().getName().contains(browseName)) {
        EdgeNodeId nodeId = new EdgeNodeId.Builder(node.getNodeId().getNamespaceIndex().intValue(),
            node.getNodeId().getIdentifier().toString()).build();
        nodes.add(nodeId);
      }
    }
    return nodes;
  }

  private static class EdgeNamespaceMap extends EdgeAbstractNamespaceMap {
  }
}
