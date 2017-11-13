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

package org.edge.protocol.opcua.providers.services.da;

import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.edge.protocol.mapper.api.EdgeMapper;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.providers.EdgeBaseService;

public interface EdgeAttributeService extends EdgeBaseService {

  /**
   * @fn EdgeResult readSync(EdgeMessage msg)
   * @brief read node data synchronously
   * @param [in] EdgeMessage msg
   * @return result
   */
  public EdgeResult readSync(EdgeMessage msg) throws Exception;

  /**
   * @fn EdgeResult write(EdgeMessage msg)
   * @brief write edge message
   * @param [in] EdgeMessage msg
   * @return result
   */
  public EdgeResult write(EdgeMessage msg) throws Exception;

  /**
   * @fn EdgeNodeIdentifier getNodeType()
   * @brief get node type
   * @return EdgeNodeIdentifier
   */
  public EdgeNodeIdentifier getNodeType() throws Exception;

  /**
   * @fn EdgeResult readAsync(EdgeMessage msg)
   * @brief read node data asynchronously
   * @param [in] EdgeMessage msg
   * @return result
   */
  public EdgeResult readAsync(EdgeMessage msg) throws Exception;

  /**
   * @fn void setProperty(VariableNode node)
   * @brief set property of node
   * @param [in] VariableNode node
   * @return void
   */
  public void setProperty(VariableNode node) throws Exception;

  /**
   * @fn EdgeMapper getMapper()
   * @brief get mapper
   * @return EdgeMapper
   */
  public EdgeMapper getMapper();

  /**
   * @fn EdgeEndpoint getNodeInfo(String valueAilas)
   * @brief get NodeInfo
   * @param [in] String valueAilas
   * @return EdgeNodeInfo
   */
  public EdgeNodeInfo getNodeInfo(String valueAilas);

  /**
   * @fn NodeId getNodeId()
   * @brief get node id
   * @return NodeId
   */
  public NodeId getNodeId();
}
