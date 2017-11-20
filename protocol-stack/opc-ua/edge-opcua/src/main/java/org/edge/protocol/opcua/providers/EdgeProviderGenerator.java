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

package org.edge.protocol.opcua.providers;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.toList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseResultMask;
import org.eclipse.milo.opcua.stack.core.types.enumerated.IdType;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;
import org.edge.protocol.opcua.api.common.EdgeNodeId;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.providers.services.EdgeCustomService;
import org.edge.protocol.opcua.providers.services.EdgeServerService;
import org.edge.protocol.opcua.providers.services.browse.EdgeBrowseService;
import org.edge.protocol.opcua.providers.services.browse.EdgeViewService;
import org.edge.protocol.opcua.providers.services.da.EdgeAnalogItemService;
import org.edge.protocol.opcua.providers.services.da.EdgeArrayItemService;
import org.edge.protocol.opcua.providers.services.da.EdgeAttributeService;
import org.edge.protocol.opcua.providers.services.da.EdgeDataItemService;
import org.edge.protocol.opcua.providers.services.da.EdgeImageItemService;
import org.edge.protocol.opcua.providers.services.da.EdgeMultiStateDiscreteService;
import org.edge.protocol.opcua.providers.services.da.EdgeMultiStateValueDiscreteService;
import org.edge.protocol.opcua.providers.services.da.EdgeNDimensionArrayItemService;
import org.edge.protocol.opcua.providers.services.da.EdgeTwoStateDiscreteService;
import org.edge.protocol.opcua.providers.services.da.EdgeXYArrayItemService;
import org.edge.protocol.opcua.providers.services.da.EdgeYArrayItemService;
import org.edge.protocol.opcua.providers.services.method.EdgeMethodService;
import org.edge.protocol.opcua.providers.services.sub.EdgeMonitoredItemService;
import org.edge.protocol.opcua.session.EdgeOpcUaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeProviderGenerator {
  private final static Logger logger = LoggerFactory.getLogger(EdgeProviderGenerator.class);
  private static EdgeProviderGenerator providerGenerator = null;
  private static final int maxStackSize = 100;
  private static String uriStack[] = new String[maxStackSize];
  private static HashSet<NodeId> visitedNode = new HashSet<NodeId>();
  private static HashSet<NodeId> visitedViewNode = new HashSet<NodeId>();
  private static List<NodeId> viewNodeList = new ArrayList<NodeId>();

  /**
   * @fn EdgeProviderGenerator getInstance()
   * @brief get provider generator instance
   * @return providerGenerator instance
   */
  public synchronized static EdgeProviderGenerator getInstance() {

    if (providerGenerator == null) {
      providerGenerator = new EdgeProviderGenerator();
    }
    return providerGenerator;
  }

  private static String generateUri(int uriStackTop) {
    StringBuffer sb = new StringBuffer();
    for (int i = 1; i < uriStackTop; i++) {
      sb.append("/").append(uriStack[i]);
    }
    return sb.toString();
  }

  private static VariableNode getVariableNode(NodeId id, EdgeOpcUaClient client) {
    try {
      VariableNode v =
          (VariableNode) client.getClientInstance().getAddressSpace().createNode(id).get();
      return v;

    } catch (InterruptedException | ExecutionException | NumberFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  private static EdgeAttributeService generateServerService(NodeId id, EdgeOpcUaClient client) {
    try {
      EdgeAttributeService service = EdgeServerService.getInstance();
      service.setProperty(getVariableNode(id, client));
      return service;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private static EdgeAttributeService generateDataAccessItemService(NodeId id,
      EdgeOpcUaClient client) {
    try {
      int idIntValue = Integer.parseInt(id.getIdentifier().toString());

      EdgeAttributeService service = null;
      if (idIntValue == EdgeNodeIdentifier.AnalogItemType.value()) {
        service =
            new EdgeAnalogItemService(id.getNamespaceIndex().intValue(), client.getEndpoint());
        service.setProperty(getVariableNode(id, client));
      } else if (idIntValue == EdgeNodeIdentifier.ImageItemType.value()) {
        service = new EdgeImageItemService(id.getNamespaceIndex().intValue(), client.getEndpoint());
        service.setProperty(getVariableNode(id, client));
      } else if (idIntValue == EdgeNodeIdentifier.DataItemType.value()) {
        service = new EdgeDataItemService(id.getNamespaceIndex().intValue(), client.getEndpoint());
        service.setProperty(getVariableNode(id, client));
      } else if (idIntValue == EdgeNodeIdentifier.ArrayItemType.value()) {
        service = new EdgeArrayItemService(id.getNamespaceIndex().intValue(), client.getEndpoint());
        service.setProperty(getVariableNode(id, client));
      } else if (idIntValue == EdgeNodeIdentifier.MultiStateDiscreteType.value()) {
        service = new EdgeMultiStateDiscreteService(id.getNamespaceIndex().intValue(),
            client.getEndpoint());
        service.setProperty(getVariableNode(id, client));
      } else if (idIntValue == EdgeNodeIdentifier.MultiStateValueDiscreteType.value()) {
        service = new EdgeMultiStateValueDiscreteService(id.getNamespaceIndex().intValue(),
            client.getEndpoint());
        service.setProperty(getVariableNode(id, client));
      } else if (idIntValue == EdgeNodeIdentifier.NDimensionArrayItemType.value()) {
        service = new EdgeNDimensionArrayItemService(id.getNamespaceIndex().intValue(),
            client.getEndpoint());
        service.setProperty(getVariableNode(id, client));
      } else if (idIntValue == EdgeNodeIdentifier.TwoStateDiscreteType.value()) {
        service = new EdgeTwoStateDiscreteService(id.getNamespaceIndex().intValue(),
            client.getEndpoint());
        service.setProperty(getVariableNode(id, client));
      } else if (idIntValue == EdgeNodeIdentifier.XYArrayItemType.value()) {
        service =
            new EdgeXYArrayItemService(id.getNamespaceIndex().intValue(), client.getEndpoint());
        service.setProperty(getVariableNode(id, client));
      } else if (idIntValue == EdgeNodeIdentifier.YArrayItemType.value()) {
        service =
            new EdgeYArrayItemService(id.getNamespaceIndex().intValue(), client.getEndpoint());
        service.setProperty(getVariableNode(id, client));
      }

      return service;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private static EdgeAttributeService generateCustomService(NodeId id, EdgeOpcUaClient client) {
    try {
      EdgeAttributeService service =
          new EdgeCustomService.Builder(id.getNamespaceIndex().intValue(),
              id.getIdentifier().toString()).build();
      service.setProperty(getVariableNode(id, client));
      return service;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private static void generateAttributeProvider(NodeId id, String browseName,
      EdgeOpcUaClient client, int uriStackTop) {
    EdgeAttributeService service = null;
    String uri = null;
    if (id.getType() == IdType.Numeric) {
      int idIntValue = Integer.parseInt(id.getIdentifier().toString());
      if (idIntValue == EdgeNodeIdentifier.Server_ServerStatus_BuildInfo.value()) {
        uri = EdgeOpcUaCommon.WELL_KNOWN_SERVER_NODE.getValue();
        service = generateServerService(id, client);
      } else if (idIntValue == EdgeNodeIdentifier.AnalogItemType.value()
          || idIntValue == EdgeNodeIdentifier.ImageItemType.value()
          || idIntValue == EdgeNodeIdentifier.DataItemType.value()
          || idIntValue == EdgeNodeIdentifier.ArrayItemType.value()
          || idIntValue == EdgeNodeIdentifier.MultiStateDiscreteType.value()
          || idIntValue == EdgeNodeIdentifier.MultiStateValueDiscreteType.value()
          || idIntValue == EdgeNodeIdentifier.NDimensionArrayItemType.value()
          || idIntValue == EdgeNodeIdentifier.TwoStateDiscreteType.value()
          || idIntValue == EdgeNodeIdentifier.XYArrayItemType.value()
          || idIntValue == EdgeNodeIdentifier.YArrayItemType.value()) {
        // DataAccess Node
        uri = generateUri(uriStackTop);
        logger.info("DataAccess(Numeric type) Node = " + browseName + ", " + uri);
        service = generateDataAccessItemService(id, client);
      } else {
      }
    } else if (id.getType() == IdType.String) {
      // custom Node
      if (uriStackTop < 1) {
        logger.info("uriStackTop is lower than 1");
        return;
      }

      uri = generateUri(uriStackTop);
      logger.info("Custom(String type) Node : " + browseName);
      service = generateCustomService(id, client);
    } else {
      // TODO
      // other types is not supported.
    }

    if (service != null && uri != null) {
      EdgeAttributeProvider provider =
          new EdgeAttributeProvider(EdgeMonitoredItemService.getInstance(),
              EdgeBrowseService.getInstance()).registerAttributeService(uri, service);
      EdgeServices.registerAttributeProvider(uri, provider);
    }
  }

  private static EdgeMethodProvider generateMethodProvider(NodeId objectId, NodeId methodId,
      int uriStackTop) {
    try {
      String uri = generateUri(uriStackTop);
      EdgeMethodProvider provider = new EdgeMethodProvider(EdgeMonitoredItemService.getInstance(),
          EdgeBrowseService.getInstance()).registerMethodService(uri,
              new EdgeMethodService.Builder(objectId, methodId).build());
      EdgeServices.registerMethodProvider(uri, provider);
      return provider;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private static EdgeViewProvider generateViewProvider(EdgeNodeId nodeId, int uriStackTop) {
    try {
      String uri = generateUri(uriStackTop);
      EdgeViewProvider provider = new EdgeViewProvider(EdgeMonitoredItemService.getInstance(),
          EdgeBrowseService.getInstance()).registerViewService(uri,
              new EdgeViewService.Builder(nodeId).build());
      EdgeServices.registerViewProvider(uri, provider);
      return provider;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private static EdgeResult generateProviderAll(NodeId id, String browseName, NodeClass classType,
      EdgeOpcUaClient client, final int uriStackTop) {
    boolean ret = true;
    visitedNode.add(id);

    if (classType == NodeClass.Variable) {
      generateAttributeProvider(id, browseName, client, uriStackTop);
    }
    BrowseDescription browse = new BrowseDescription(id, BrowseDirection.Forward,
        Identifiers.References, true,
        uint(NodeClass.Object.getValue() | NodeClass.View.getValue() | NodeClass.Method.getValue()
            | NodeClass.Variable.getValue() | NodeClass.ReferenceType.getValue()),
        uint(BrowseResultMask.All.getValue()));
    try {
      BrowseResult browseResult = client.getClientInstance().browse(browse).get();
      List<ReferenceDescription> references = toList(browseResult.getReferences());
      for (ReferenceDescription rd : references) {
        rd.getNodeId().local().ifPresent(nodeId -> {
          if (visitedNode.contains(nodeId) == false) {
            NodeClass nextNodeClass = rd.getNodeClass();
            String nextBrowseName = rd.getBrowseName().getName();
            uriStack[uriStackTop] = nextBrowseName;

            if (nextNodeClass == NodeClass.Method) {
              generateMethodProvider(id, nodeId, uriStackTop + 1);
            } else if (nextNodeClass == NodeClass.View) {
              EdgeNodeId edgeNodeId = new EdgeNodeId.Builder(nodeId.getNamespaceIndex().intValue(),
                  nodeId.getIdentifier().toString()).build();
              logger.info("generator - view ={}, {}", nodeId.getNamespaceIndex().intValue(),
                  nodeId.getIdentifier().toString());
              generateViewProvider(edgeNodeId, uriStackTop + 1);
            }
            generateProviderAll(nodeId, nextBrowseName, nextNodeClass, client, uriStackTop + 1);
          }
        });
      }
    } catch (InterruptedException | ExecutionException e) {
      ret = false;
    }

    return new EdgeResult.Builder(ret ? EdgeStatusCode.STATUS_OK : EdgeStatusCode.STATUS_ERROR)
        .build();
  }

  private static void getViewNodeList(NodeId id, EdgeOpcUaClient client) {
    visitedViewNode.add(id);

    BrowseDescription browse = new BrowseDescription(id, BrowseDirection.Forward,
        Identifiers.References, true, uint(NodeClass.Object.getValue() | NodeClass.View.getValue()),
        uint(BrowseResultMask.All.getValue()));
    try {
      BrowseResult browseResult = client.getClientInstance().browse(browse).get();
      List<ReferenceDescription> references = toList(browseResult.getReferences());
      for (ReferenceDescription rd : references) {
        rd.getNodeId().local().ifPresent(nodeId -> {
          if (visitedViewNode.contains(nodeId) == false) {
            NodeClass nodeClass = rd.getNodeClass();

            if (nodeClass == NodeClass.View) {
              viewNodeList.add(nodeId);
            }
            getViewNodeList(nodeId, client);
          }
        });
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return;
    }
  }

  /**
   * @fn EdgeResult initializeProvider(NodeId id, String browseName, NodeClass classType,
   *     EdgeOpcUaClient client, final int uriStackTop)
   * @brief initialize Provider through browsing node data from server.
   * @prarm [in] id root nod protocolManager = null;e ID to browse
   * @prarm [in] browseName browse name
   * @prarm [in] classType available node class type (NodeClass.Variable)
   * @prarm [in] client EdgeOpcUaClient instance to browse
   * @prarm [in] viewEnabled the flag whether view type is set as the target node of initialize
   *        provider.
   * @return result
   */
  public EdgeResult initializeProvider(NodeId id, String browseName, NodeClass classType,
      EdgeOpcUaClient client, boolean viewEnabled) {
    EdgeResult ret = null;
    visitedNode.clear();

    if (true == viewEnabled) {
      visitedViewNode.clear();
      viewNodeList.clear();
      getViewNodeList(id, client);
      for (NodeId nodeId : viewNodeList) {
        ret = generateProviderAll(nodeId, browseName, classType, client, 0);
      }
    } else {
      ret = generateProviderAll(id, browseName, classType, client, 0);
    }

    return ret;
  }

  public void close() {
    providerGenerator = null;
  }
}
