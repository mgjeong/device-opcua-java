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

import java.util.Random;
import org.edge.protocol.opcua.api.client.EdgeSubRequest;

public class EdgeRequest {
  private EdgeVersatility value;
  private EdgeSubRequest subMsg;
  private final EdgeNodeInfo nodeInfo;
  private final int requestId;
  private final int returnDiagnostic;
  private static Random random = new Random();
  private static int seed = Integer.MAX_VALUE;

  public static class Builder {
    private EdgeVersatility value = null;
    private EdgeSubRequest subMsg = null;
    private final EdgeNodeInfo nodeInfo;
    private int requestId = getRandom();
    private int returnDiagnostic = 0;

    public Builder(EdgeNodeInfo nodeInfo) {
      this.nodeInfo = nodeInfo;
    }

    /**
     * @fn Builder setMessage(EdgeVersatility value)
     * @brief set EdgeVersatility to write
     * @param [in] value the parameter of EdgeVersatility type
     * @return this
     */
    public Builder setMessage(EdgeVersatility value) {
      this.value = value;
      return this;
    }

    /**
     * @fn Builder setSubReq(EdgeSubRequest req)
     * @brief set EdgeSubRequest
     * @param [in] req a request of subscription
     * @return this
     */
    public Builder setSubReq(EdgeSubRequest req) {
      this.subMsg = req;
      return this;
    }

    /**
     * @fn Builder setReturnDiagnostic(int diagnostic)
     * @brief set ReturnDiagnostic
     * @param [in] diagnostic ReturnDiagnostic
     * @return this
     */
    public Builder setReturnDiagnostic(int diagnostic) {
      this.returnDiagnostic = diagnostic;
      return this;
    }

    /**
     * @fn EdgeRequest build()
     * @brief create EdgeRequest instance (builder)
     * @return EdgeRequest instance
     */
    public EdgeRequest build() {
      return new EdgeRequest(this);
    }
  }

  /**
   * @fn EdgeRequest(Builder builder)
   * @brief constructor
   * @param [in] builder EdgeRequest Builder
   */
  private EdgeRequest(Builder builder) {
    value = builder.value;
    nodeInfo = builder.nodeInfo;
    subMsg = builder.subMsg;
    requestId = builder.requestId;
    returnDiagnostic = builder.returnDiagnostic;
  }

  /**
   * @fn EdgeVersatility getMessage()
   * @brief get request message to write
   * @return value
   */
  public EdgeVersatility getMessage() {
    return value;
  }

  /**
   * @fn EdgeEndpoint getEdgeNodeInfo()
   * @brief get endpoint
   * @return endpoint
   */
  public EdgeNodeInfo getEdgeNodeInfo() {
    return nodeInfo;
  }

  /**
   * @fn EdgeSubRequest getSubRequest()
   * @brief get subscription parameter
   * @return subMsg
   */
  public EdgeSubRequest getSubRequest() {
    return subMsg;
  }

  /**
   * @fn int getRequestId()
   * @brief get request id
   * @return requestId
   */
  public int getRequestId() {
    return requestId;
  }

  /**
   * @fn int getReturnDiagnostic()
   * @brief get returnDiagnostic
   * @return returnDiagnostic
   */
  public int getReturnDiagnostic() {
    return returnDiagnostic;
  }

  /**
   * @fn int getRandom()
   * @brief get random integer value
   * @return random value
   */
  private static int getRandom() {
    return random.nextInt(seed);
  }
}
