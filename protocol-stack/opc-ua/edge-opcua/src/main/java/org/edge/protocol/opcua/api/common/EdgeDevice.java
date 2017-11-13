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

import java.util.ArrayList;

public class EdgeDevice {
  private final String address;
  private final int port;
  private String serverName;
  private ArrayList<EdgeEndpointInfo> endpoints;

  public static class Builder {
    private final String address;
    private final int port;
    private String serverName;
    private ArrayList<EdgeEndpointInfo> endpoints;

    public Builder(String addr, int port) {
      this.address = addr;
      this.port = port;
    }

    public Builder setEndpoints(ArrayList<EdgeEndpointInfo> ep) {
      this.endpoints = ep;
      return this;
    }

    public Builder setServerName(String name) {
      this.serverName = name;
      return this;
    }

    /**
     * @fn EdgeDevice build()
     * @brief create EdgeDevice instance (builder)
     * @return EdgeDevice instance
     */
    public EdgeDevice build() {
      return new EdgeDevice(this);
    }
  }

  /**
   * @fn EdgeDevice(Builder builder)
   * @brief constructor
   * @param [in] builder EdgeDevice Builder
   */
  private EdgeDevice(Builder builder) {
    address = builder.address;
    port = builder.port;
    endpoints = builder.endpoints;
    serverName = builder.serverName;
  }

  /**
   * @fn String getAddress()
   * @brief get address
   * @return address
   */
  public String getAddress() {
    return address;
  }

  /**
   * @fn int getPort()
   * @brief get port
   * @return port
   */
  public int getPort() {
    return port;
  }

  /**
   * @fn ArrayList<EdgeEndpointInfo> getEndpoints()
   * @brief get getEndpoint list
   * @return endpoints
   */
  public ArrayList<EdgeEndpointInfo> getEndpoints() {
    return endpoints;
  }

  /**
   * @fn String getServerName()
   * @brief get Server Name
   * @return serverName
   */
  public String getServerName() {
    return serverName;
  }
}
