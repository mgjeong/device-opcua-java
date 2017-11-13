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

package org.edge.protocol.opcua.api.server;

import org.edge.protocol.opcua.api.common.EdgeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeNodeId;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;

public class EdgeNodeItem {
  private final String browseName;
  private EdgeIdentifier nodeType;
  private EdgeNodeIdentifier daNodeIdentifier;
  private int accessLevel;
  private int userAccessLevel;
  private int writeMask;
  private int userWriteMask;
  private boolean forward;
  private Object[][] variableItemSet;
  private EdgeNodeId sourceNodeId;

  public static class Builder {
    private final String browseName;
    private EdgeIdentifier nodeType = EdgeIdentifier.VARIABLE_NODE;
    private EdgeNodeIdentifier daNodeIdentifier = EdgeNodeIdentifier.VariableNode;
    private int accessLevel = EdgeIdentifier.getAccessLevel(EdgeIdentifier.READ_WRITE);
    private int userAccessLevel = EdgeIdentifier.getAccessLevel(EdgeIdentifier.READ_WRITE);
    private int writeMask = 0;
    private int userWriteMask = 0;
    private EdgeNodeId sourceNodeId;
    private boolean forward = true;
    private Object[][] variableItemSet = null;

    public Builder(String browseName) {
      this.browseName = browseName;
    }

    /**
     * @fn Builder setEdgeNodeType(EdgeIdentifier nodeType)
     * @brief set setEdgeNodeType
     * @param [in] nodeType such as VARIABLE_NODE, VARIABLE_TYPE_NODE, METHOD_NODE, OBJECT_NODE,
     *        ARRAY_NODE, OBJECT_TYPE_NODE, REFERENCE_TYPE_NODE, VIEW_NODE, DATA_TYPE_NODE
     */
    public Builder setEdgeNodeType(EdgeIdentifier nodeType) {
      this.nodeType = nodeType;
      return this;
    }

    /**
     * @fn Builder setDataAccessNodeId(EdgeNodeIdentifier id)
     * @brief set Node Identifier - this function is available in Data Access Node
     * @param [in] id EdgeNodeIdentifier related Data Access
     */
    public Builder setDataAccessNodeId(EdgeNodeIdentifier id) {
      this.daNodeIdentifier = id;
      return this;
    }

    /**
     * @fn Builder setVariableItemSet(Object[][] nodeSet)
     * @brief set Parameter related Variable type such as VARIABLE_NODE, VARIABLE_TYPE_NODE,
     *        ARRAY_NODE, other type will be ignored.
     * @param [in] variableItems object set related variable (please refer EdgeSampleCommon.java)
     */
    public Builder setVariableItemSet(Object[][] variableItems) {
      this.variableItemSet = variableItems;
      return this;
    }

    public Builder setAccessLevel(int level) {
      this.accessLevel = level;
      return this;
    }

    public Builder setUserAccessLevel(int level) {
      this.userAccessLevel = level;
      return this;
    }

    public Builder setWriteMask(int mask) {
      this.writeMask = mask;
      return this;
    }

    public Builder setUserWriteMask(int mask) {
      this.userWriteMask = mask;
      return this;
    }

    /**
     * @fn Builder setForward()
     * @brief set Forward(reference direction) default detection is forward
     * @param [in] forward
     */
    public Builder setForward(boolean forward) {
      this.forward = forward;
      return this;
    }

    /**
     * @fn Builder setSourceNode(EdgeNodeId targetNodeId
     * @brief set source node which has HasComponent. this component will be Target Node if there is
     *        no set in EdgeNodeItem. source node will be root node.
     * @param [in] forward
     */
    public Builder setSourceNode(EdgeNodeId nodeId) {
      this.sourceNodeId = nodeId;
      return this;
    }

    public EdgeNodeItem build() {
      return new EdgeNodeItem(this);
    }
  }

  private EdgeNodeItem(Builder builder) {
    browseName = builder.browseName;
    nodeType = builder.nodeType;
    daNodeIdentifier = builder.daNodeIdentifier;
    variableItemSet = builder.variableItemSet;
    accessLevel = builder.accessLevel;
    userAccessLevel = builder.userAccessLevel;
    writeMask = builder.writeMask;
    userWriteMask = builder.userWriteMask;
    forward = builder.forward;
    sourceNodeId = builder.sourceNodeId;
  }

  /**
   * @fn String getBrowseName()
   * @brief get nodeName
   * @return nodeName
   */
  public String getBrowseName() {
    return browseName;
  }

  /**
   * @fn EdgeIdentifier getEdgeNodeType()
   * @brief get EdgeNodeType such as VARIABLE_NODE, VARIABLE_TYPE_NODE, METHOD_NODE, OBJECT_NODE,
   *        ARRAY_NODE, OBJECT_TYPE_NODE, REFERENCE_TYPE_NODE, VIEW_NODE, DATA_TYPE_NODE
   * @return nodeType
   */
  public EdgeIdentifier getEdgeNodeType() {
    return nodeType;
  }

  /**
   * @fn EdgeNodeIdentifier getDataAccessNodeId()
   * @brief get Node Identifier - this function is available in Data Access Node
   * @return daNodeIdentifier
   */
  public EdgeNodeIdentifier getDataAccessNodeId() {
    return daNodeIdentifier;
  }

  /**
   * @fn Object[][] getVariableItemSet()
   * @brief get AccessLevel
   * @return accessLevel
   */
  public Object[][] getVariableItemSet() {
    return variableItemSet;
  }

  /**
   * @fn int getAccessLevel()
   * @brief get AccessLevel
   * @return accessLevel
   */
  public int getAccessLevel() {
    return accessLevel;
  }

  /**
   * @fn int getUserAccessLevel()
   * @brief get UserAccessLevel
   * @return userAccessLevel
   */
  public int getUserAccessLevel() {
    return userAccessLevel;
  }

  /**
   * @fn int getWriteMask()
   * @brief get WriteMask
   * @return writeMask
   */
  public int getWriteMask() {
    return writeMask;
  }

  /**
   * @fn int getUserWriteMask()
   * @brief get UserWriteMask
   * @return userWriteMask
   */
  public int getUserWriteMask() {
    return userWriteMask;
  }

  /**
   * @fn int getForward()
   * @brief get Forward(reference direction) default detection is forward
   * @return forward
   */
  public boolean getForward() {
    return forward;
  }

  /**
   * @fn EdgeNodeId getSourceNode()
   * @brief get Source NodeId
   * @return sourceNodeId
   */
  public EdgeNodeId getSourceNode() {
    return sourceNodeId;
  }
}
