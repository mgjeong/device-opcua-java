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

public class EdgeEndpointConfig {
  private int requestTimeout;
  private String applicationName;
  private String applicationUri;
  private String productUri;
  private String securityPolicyUri;
  private String serverName;
  private String bindAddress;
  private int bindPort;
  private boolean viweNodeFalg;

  public static class Builder {
    private int requestTimeout = 60000;
    private String applicationName = EdgeOpcUaCommon.DEFAULT_SERVER_APP_NAME.getValue();
    private String applicationUri = EdgeOpcUaCommon.DEFAULT_SERVER_URI.getValue();
    private String productUri = EdgeOpcUaCommon.DEFAULT_PRODUCT_URI.getValue();
    private String securityPolicyUri = null;
    private String serverName = EdgeOpcUaCommon.DEFAULT_SERVER_NAME.getValue();
    private String bindAddress = EdgeOpcUaCommon.WELL_KNOWN_LOCALHOST_ADDRESS.getValue();
    private int bindPort = 12686;
    private boolean viweNodeFalg = true;

    public Builder() {}

    /**
     * @fn Builder setApplicationName(String val)
     * @brief set application name
     * @param [in] val application name
     * @return this
     */
    public Builder setApplicationName(String val) {
      applicationName = val;
      return this;
    }

    /**
     * @fn Builder setApplicationUri(String val)
     * @brief set application uri
     * @param [in] val application uri
     * @return this
     */
    public Builder setApplicationUri(String val) {
      applicationUri = val;
      return this;
    }
    
    /**
     * @fn Builder setProductUri(String val)
     * @brief set product uri
     * @param [in] val product uri
     * @return this
     */
    public Builder setProductUri(String val) {
      productUri = val;
      return this;
    }

    /**
     * @fn Builder setSecurityPolicyUri(String val)
     * @brief set security policy uri
     * @param [in] val security policy uri
     * @return this
     */
    public Builder setSecurityPolicyUri(String val) {
      securityPolicyUri = val;
      return this;
    }

    /**
     * @fn String setServerName(String val)
     * @brief set Server Name
     * @param [in] val server Name
     * @return this
     */
    public Builder setServerName(String val) {
      serverName = val;
      return this;
    }
    
    /**
     * @fn Builder setbindAddress(String addr)
     * @brief set bind address
     * @param [in] addr address
     * @return this
     */
    public Builder setbindAddress(String addr) {
      bindAddress = addr;
      return this;
    }

    /**
     * @fn Builder setbindPort(int port)
     * @brief set bind port
     * @param [in] port port
     * @return this
     */
    public Builder setbindPort(int port) {
      bindPort = port;
      return this;
    }

    /**
     * @fn Builder setViewNodeFlag(boolean flag)
     * @brief set configure which initialize provider with only view node
     * @param [in] flag View Node flag is set
     * @return this
     */
    public Builder setViewNodeFlag(boolean flag) {
      viweNodeFalg = flag;
      return this;
    }
    
    /**
     * @fn EdgeEndpointConfig build()
     * @brief create EdgeEndpointConfig instance (builder)
     * @return EdgeEndpointConfig
     */
    public EdgeEndpointConfig build() {
      return new EdgeEndpointConfig(this);
    }
  }

  /**
   * @fn EdgeEndpointConfig(Builder builder)
   * @brief constructor
   */
  private EdgeEndpointConfig(Builder builder) {
    requestTimeout = builder.requestTimeout;
    applicationName = builder.applicationName;
    applicationUri = builder.applicationUri;
    productUri = builder.productUri;
    securityPolicyUri = builder.securityPolicyUri;
    serverName = builder.serverName;
    bindAddress = builder.bindAddress;
    bindPort = builder.bindPort;
    viweNodeFalg = builder.viweNodeFalg;
  }

  /**
   * @fn int getRequestTimeout()
   * @brief get request time-out
   * @return time-out
   */
  public int getRequestTimeout() {
    return requestTimeout;
  }

  /**
   * @fn String getApplicationName()
   * @brief get Application name
   * @return application name
   */
  public String getApplicationName() {
    return applicationName;
  }

  /**
   * @fn String getApplicationUri()
   * @brief get Application uri
   * @return application uri
   */
  public String getApplicationUri() {
    return applicationUri;
  }

  /**
   * @fn String getProductUri()
   * @brief get product uri
   * @return productUri
   */
  public String getProductUri() {
    return productUri;
  }
  
  /**
   * @fn String getSecurityPolicyUri()
   * @brief get Security Policy Uri
   * @return Security Policy Uri
   */
  public String getSecurityPolicyUri() {
    return securityPolicyUri;
  }

  /**
   * @fn String getServerName()
   * @brief get Server Name
   * @return serverName
   */
  public String getServerName() {
    return serverName;
  }
  
  /**
   * @fn String getBindAddress()
   * @brief get bindaddress
   * @return bind address
   */
  public String getBindAddress() {
    return bindAddress;
  }

  /**
   * @fn int getBindPort()
   * @brief get bindPort
   * @return bindPort
   */
  public int getBindPort() {
    return bindPort;
  }
  
  /**
   * @fn boolean getViewNodeFlag()
   * @brief get viweNodeFalg
   * @return viweNodeFalg
   */
  public boolean getViewNodeFlag() {
    return viweNodeFalg;
  }
}
