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

package org.edge.protocol.opcua.providers.services.method;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeRequest;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeVersatility;

public class EdgeMethodService {
  private final NodeId methodId;
  private final NodeId objectId;

  public static class Builder {
    private final NodeId methodId;
    private final NodeId objectId;

    /**
     * @fn Builder(NodeId objectId, NodeId methodId)
     * @brief Constructor of Builder class
     * @param [in] objectId object Id
     * @param [in] methodId method Id
     */
    public Builder(NodeId objectId, NodeId methodId) {
      this.objectId = objectId;
      this.methodId = methodId;
    }

    /**
     * @fn EdgeMethodService build()
     * @brief build edge method service
     * @return instance of EdgeMethodService
     */
    public EdgeMethodService build() {
      return new EdgeMethodService(this);
    }
  }

  /**
   * @fn EdgeMethodService(Builder builder)
   * @brief Constructor of EdgeMethodService class
   * @param [in] Builder builder
   */
  private EdgeMethodService(Builder builder) {
    objectId = builder.objectId;
    methodId = builder.methodId;
  }

  /**
   * @fn NodeId getMethodId()
   * @brief get method name to make NodeId(MethodId)
   * @return method ID
   */
  public NodeId getMethodId() {
    return methodId;
  }

  /**
   * @fn NodeId getObjectId()
   * @brief get object node uri to make NodeId(ObjectId)
   * @return object node uri
   */
  public NodeId getObjectId() {
    return objectId;
  }

  /**
   * @fn EdgeResult runMethodAsync(EdgeVersatility param)
   * @brief call method caller
   * @param [in] ep endpoint
   * @param [in] param parameter (other parameter type will be supported next version)
   * @param [in] epInfo endpoint information
   * @return result
   */
  public EdgeResult runMethodAsync(EdgeNodeInfo ep, EdgeVersatility param, EdgeEndpointInfo epInfo)
      throws Exception {
    return EdgeMethodCaller.getInstance().executeAsync(
        new EdgeMessage.Builder(epInfo).setRequest(new EdgeRequest.Builder(ep).build()).build(),
        param, getObjectId(), getMethodId());
  }
}
