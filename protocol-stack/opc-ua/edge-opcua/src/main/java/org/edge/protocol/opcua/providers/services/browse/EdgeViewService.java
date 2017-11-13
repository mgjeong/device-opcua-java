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

package org.edge.protocol.opcua.providers.services.browse;

import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseResultMask;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.ViewDescription;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeNodeId;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeNodeType;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edge.protocol.opcua.queue.ErrorHandler;
import org.edge.protocol.opcua.session.EdgeSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import java.util.ArrayList;
import java.util.List;

public class EdgeViewService {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private static final int BROWSE_DESCRIPTION_NODECLASS_MASK = NodeClass.Object.getValue()
      | NodeClass.Variable.getValue() | NodeClass.ReferenceType.getValue()
      | NodeClass.Method.getValue() | NodeClass.View.getValue();

  private final EdgeNodeId nodeId;

  public static class Builder {
    private final EdgeNodeId nodeId;

    public Builder(EdgeNodeId nodeId) {
      this.nodeId = nodeId;
    }

    public EdgeViewService build() {
      return new EdgeViewService(this);
    }
  }

  private EdgeViewService(Builder builder) {
    nodeId = builder.nodeId;
  }

  public EdgeNodeId getEdgeNodeId() {
    return nodeId;
  }

  public void browseView(EdgeMessage msg) {
    EdgeEndpointInfo epInfo = msg.getEdgeEndpointInfo();
    NodeId node = getNodeId(nodeId);
    ViewDescription view = new ViewDescription(node, DateTime.MIN_VALUE, uint(0));
    List<NodeId> nodeIds = new ArrayList<NodeId>();
    nodeIds.add(node);
    List<BrowseDescription> browseList =
        getBrowseDescriptions(nodeIds, msg.getBrowseParameter().getDirection());

    EdgeSessionManager.getInstance().getSession(epInfo.getEndpointUri()).getClientInstance()
        .browse(view, uint(0), browseList).thenApply(browseResult -> {
          logger.info("result : {}", browseResult.getResults()[0].getReferences()[0].getBrowseName());
          return browseResult;
        }).exceptionally(e -> {
          logger.info("error type : {}", e.getMessage());
          return null;
        });
  }

  private NodeId getNodeId(EdgeNodeId nodeId) {
    if (nodeId == null) {
      return null;
    }

    if (nodeId.getEdgeNodeType() == EdgeNodeType.STRING) {
      return new NodeId(nodeId.getNameSpace(), nodeId.getEdgeNodeUri());
    } else {
      if (nodeId.getEdgeNodeIdentifier().equals(EdgeNodeIdentifier.ReadValueId)) {
        return new NodeId(nodeId.getNameSpace(), EdgeNodeIdentifier.RootFolder.value());
      } else {
        return new NodeId(nodeId.getNameSpace(), nodeId.getEdgeNodeIdentifier().value());
      }
    }
  }

  private List<BrowseDescription> getBrowseDescriptions(List<NodeId> nodeIds, int direct) {
    List<BrowseDescription> browseList = new ArrayList<BrowseDescription>();

    BrowseDirection directionParam = BrowseDirection.Forward;
    if (BrowseDirection.Inverse.ordinal() == direct) {
      directionParam = BrowseDirection.Inverse;
    } else if (BrowseDirection.Both.ordinal() == direct) {
      directionParam = BrowseDirection.Both;
    }

    for (NodeId id : nodeIds) {
      BrowseDescription browse = new BrowseDescription(id, directionParam, Identifiers.References,
          true, uint(BROWSE_DESCRIPTION_NODECLASS_MASK), uint(BrowseResultMask.All.getValue()));

      browseList.add(browse);
    }
    return browseList;
  }
}
