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

import java.util.concurrent.CompletableFuture;

public class EdgeEndpointInfo {
  private String endpointUri;
  private EdgeEndpointConfig config;
  private CompletableFuture<EdgeMessage> future = null;

  public static class Builder {
    private String endpointUri = null;;
    private EdgeEndpointConfig config = null;
    private CompletableFuture<EdgeMessage> future = null;

    public Builder(String endpointUri) {
      this.endpointUri = endpointUri;
    }

    /**
     * @fn Builder setConfig(EdgeEndpointConfig config)
     * @brief set EdgeEndpointConfig for server setting
     * @param [in] config server configuration
     * @return this
     */
    public Builder setConfig(EdgeEndpointConfig config) {
      this.config = config;
      return this;
    }

    /**
     * @fn Builder setFuture(CompletableFuture<EdgeMessage> future)
     * @brief set future instance of CompletableFuture<EdgeMessage>
     * @param [in] future CompletableFuture<EdgeMessage> future
     * @return this
     */
    public Builder setFuture(CompletableFuture<EdgeMessage> future) {
      this.future = future;
      return this;
    }

    /**
     * @fn EdgeEndpoint build()
     * @brief create EdgeEndpoint instance (builder)
     * @return EdgeEndpoint instance
     */
    public EdgeEndpointInfo build() {
      return new EdgeEndpointInfo(this);
    }
  }

  /**
   * @fn EdgeEndpoint(Builder builder)
   * @brief constructor
   * @param [in] builder EdgeEndpoint Builder
   */
  private EdgeEndpointInfo(Builder builder) {
    endpointUri = builder.endpointUri;
    config = builder.config;
    future = builder.future;
  }

  /**
   * @fn String getEndpointUri()
   * @brief get endpoint uri
   * @return endpointUri
   */
  public String getEndpointUri() {
    return endpointUri;
  }

  /**
   * @fn EdgeEndpointConfig getConfig()
   * @brief get configuration of server setting
   * @return config
   */
  public EdgeEndpointConfig getConfig() {
    return config;
  }

  /**
   * @fn String CompletableFuture<EdgeMessage> getFuture()
   * @brief get CompletableFuture
   * @return future
   */
  public CompletableFuture<EdgeMessage> getFuture() {
    return future;
  }
}
