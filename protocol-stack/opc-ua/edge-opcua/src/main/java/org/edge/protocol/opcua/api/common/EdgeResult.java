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

package org.edge.protocol.opcua.api.common;

/**
 * This class provide builder for EdgeResult
 */
public class EdgeResult {
  private EdgeNodeInfo endpoint;
  private final EdgeStatusCode code;

  /**
   * Nested class for build EdgeResult
   */
  public static class Builder {
    private final EdgeStatusCode code;
    private EdgeNodeInfo endpoint = null;

    /**
     * CTOR
     * @param code
     */
    public Builder(EdgeStatusCode code) {
      this.code = code;
    }

    /**
     * @fn Builder setEndpoint(EdgeEndpoint endpoint)
     * @brief set endpoint
     * @param [in] endpoint
     * @return Builder
     */
    public Builder setEndpoint(EdgeNodeInfo endpoint) {
      this.endpoint = endpoint;
      return this;
    }

    /**
     * @fn EdgeResult build()
     * @brief Build EdgeResult
     * @return EdgeResult
     */
    public EdgeResult build() {
      return new EdgeResult(this);
    }
  }

  /**
   * CTOR
   * @param builder
   */
  private EdgeResult(Builder builder) {
    endpoint = builder.endpoint;
    code = builder.code;
  }

  /**
   * @fn EdgeStatusCode getStatusCode()
   * @brief Get EdgeStatusCode
   * @return EdgeStatusCode
   */
  public EdgeStatusCode getStatusCode() {
    return code;
  }

  /**
   * @fn EdgeEndpoint getEndpoint()
   * @brief Get EdgeEndpoint
   * @return EdgeEndpoint
   */
  public EdgeNodeInfo getEndpoint() {
    return endpoint;
  }

}
