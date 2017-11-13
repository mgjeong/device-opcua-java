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

package org.edge.protocol.opcua.api.common;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

public class EdgeNodeId {
  private int nameSpace;
  private String nodeUri;
  private EdgeNodeIdentifier nodeIdentifier;
  private EdgeNodeType type;
  private NodeId nodeId;

  public static class Builder {
    private int nameSpace = EdgeOpcUaCommon.SYSTEM_NAMESPACE_INDEX;
    private String nodeUri = null;
    private EdgeNodeIdentifier nodeIdentifier = EdgeNodeIdentifier.ReadValueId;
    private EdgeNodeType type = EdgeNodeType.OP;
    private NodeId nodeId = null;
    
    public Builder(int nameSpace, EdgeNodeIdentifier nodeIdentifier) {
      this.nameSpace = nameSpace;
      this.nodeIdentifier = nodeIdentifier;
      this.type = EdgeNodeType.INTEGER;
      this.nodeId = new NodeId(nameSpace, nodeIdentifier.value()); 
    }

    public Builder(int nameSpace, String nodeUri) {
      this.nameSpace = nameSpace;
      this.nodeUri = nodeUri;
      this.type = EdgeNodeType.STRING;
      this.nodeId = new NodeId(nameSpace, nodeUri);
    }

    public Builder(EdgeNodeIdentifier nodeIdentifier) {
      this.nodeIdentifier = nodeIdentifier;
      this.type = EdgeNodeType.INTEGER;
      this.nodeId = new NodeId(nameSpace, nodeIdentifier.value()); 
    }

    public Builder(String nodeUri) {
      this.nodeUri = nodeUri;
      this.type = EdgeNodeType.STRING;
      this.nodeId = new NodeId(nameSpace, nodeUri);
    }

    /**
     * @fn EdgeNodeId build()
     * @brief EdgeNodeId instance creator
     * @return EdgeNodeId instance
     */
    public EdgeNodeId build() {
      return new EdgeNodeId(this);
    }
  }

  /**
   * @fn EdgeNodeId(Builder builder)
   * @brief constructor
   * @param [in] builder EdgeNodeId Builder
   */
  private EdgeNodeId(Builder builder) {
    nameSpace = builder.nameSpace;
    nodeUri = builder.nodeUri;
    nodeIdentifier = builder.nodeIdentifier;
    type = builder.type;
    nodeId = builder.nodeId;
  }

  /**
   * @fn int getNameSpace()
   * @brief get nameSpace
   * @return nameSpace
   */
  public int getNameSpace() {
    return nameSpace;
  }

  /**
   * @fn String getEdgeNodeUri()
   * @brief get edgeNode URI
   * @return nodeUri
   */
  public String getEdgeNodeUri() {
    return nodeUri;
  }

  /**
   * @fn Object getIdentifier()
   * @brief get node identifier
   * @return id object
   */
  public Object getIdentifier() {
      return getNodeId().getIdentifier();
  }
  
  public String toParseableString() {
    return nodeId.toParseableString();
}

  public EdgeNodeType getEdgeNodeType() {
    return type;
  }
  
  private NodeId getNodeId() {
    return nodeId;
  }
  
  /**
   * @fn EdgeNodeIdentifier getEdgeNodeIdentifier()
   * @brief get edgeNode ID
   * @return edgeNodeId
   */
  public EdgeNodeIdentifier getEdgeNodeIdentifier() {
    return nodeIdentifier;
  }
}
