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

package org.edge.protocol.opcua.api.client;

import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeVersatility;

public class EdgeResponse {
  private EdgeVersatility value;
  private final EdgeNodeInfo endpoint;
  private EdgeResult result;
  private final int requestId;
  private EdgeDiagnosticInfo diagnosticInfo;

  public static class Builder {
    private EdgeVersatility value = null;
    private final EdgeNodeInfo endpoint;
    private EdgeResult result = null;
    private final int requestId;
    private EdgeDiagnosticInfo diagnosticInfo = null;

    public Builder(EdgeNodeInfo endpoint, int requestId) {
      this.endpoint = endpoint;
      this.requestId = requestId;
    }

    /**
     * @fn Builder setMessage(EdgeVersatility value)
     * @brief set EdgeVersatility
     * @param [in] value the response value of the EdgeVersatility type
     * @return this
     */
    public Builder setMessage(EdgeVersatility value) {
      this.value = value;
      return this;
    }

    /**
     * @fn Builder setResult(EdgeResult result)
     * @brief set EdgeResult
     * @param [in] result result 
     * @return this
     */
    public Builder setResult(EdgeResult result) {
      this.result = result;
      return this;
    }

    /**
     * @fn Builder setDiagnosticInfo(EdgeDiagnosticInfo info)
     * @brief set EdgeDiagnosticInfo
     * @param [in] info EdgeDiagnosticInfo 
     * @return this
     */
    public Builder setDiagnosticInfo(EdgeDiagnosticInfo info) {
      this.diagnosticInfo = info;
      return this;
    }

    /**
     * @fn EdgeResponse build()
     * @brief create EdgeResponse instance (builder)
     * @return EdgeResponse instance
     */
    public EdgeResponse build() {
      return new EdgeResponse(this);
    }
  }

  /**
   * @fn EdgeResponse(Builder builder)
   * @brief constructor
   * @param [in] builder EdgeResponse Builder
   */
  private EdgeResponse(Builder builder) {
    value = builder.value;
    endpoint = builder.endpoint;
    result = builder.result;
    requestId = builder.requestId;
    diagnosticInfo = builder.diagnosticInfo;
  }

  /**
   * @fn EdgeVersatility getMessage()
   * @brief get message to respond
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
    return endpoint;
  }

  /**
   * @fn EdgeResult getResult()
   * @brief get result
   * @return result
   */
  public EdgeResult getResult() {
    return result;
  }

  /**
   * @fn int getRequestId()
   * @brief get request Id
   * @return requestId
   */
  public int getRequestId() {
    return requestId;
  }

  /**
   * @fn int getReturnDiagnostic()
   * @brief get diagnostic Information
   * @return diagnosticInfo
   */
  public EdgeDiagnosticInfo getEdgeDiagnosticInfo() {
    return diagnosticInfo;
  }
}
