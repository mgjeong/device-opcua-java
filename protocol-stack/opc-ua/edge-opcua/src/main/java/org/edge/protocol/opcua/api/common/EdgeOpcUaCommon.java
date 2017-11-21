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

public enum EdgeOpcUaCommon {

  // well known uri
  WELL_KNOWN_DISCOVERY(0, "/server/discovery", "Well-known discovery uri"),
  WELL_KNOWN_GROUP(1, "/server/group", "Well-known group uri"),
  WELL_KNOWN_LOCALHOST_URI(3, "opc.tcp://localhost", "this local endpoint"),
  WELL_KNOWN_LOCALHOST_ADDRESS(4, "localhost", ""),
  WELL_KNOWN_SERVER_NODE(5, "/server", ""),
  WELL_KNOWN_SERVER_NODE_CURRENTTIME(6, "/server/currenttime", ""),
  WELL_KNOWN_SERVER_NODE_STATUS(7, "/server/status", ""),
  WELL_KNOWN_SERVER_NODE_BUILD_INFO(8, "/server/buildInfo", ""),
    
  // default values 
  DEFAULT_SERVER_NAME(100, "edge-opc-server", "default server name"),
  DEFAULT_SERVER_URI(101, "opc.tcp://localhost:12686/", "default server address and port"),
  DEFAULT_SERVER_APP_NAME(102, "Eclipse Milo Example Client", ""),
  DEFAULT_SERVER_APP_URI(103, "urn:eclipse:milo:examples:client", ""),
  DEFAULT_NAMESPACE(104, "edge-namespace", "default display-name for namespace"),
  DEFAULT_ROOT_NODE_INFO(105, "defaultRootNode", "defalult root node information"),
  DEFAULT_PRODUCT_URI(106, "urn:digitalpetri:opcua:sdk", "defalult product uri"),  
  
  DEFAULT_ENDPOINT(120, "opc.tcp://localhost:12686/edge-opc-server",
      "this default endpoint is for local test"),
  DEFAULT_ENDPOINT_WITH_CTT(121, "opc.tcp://localhost:4842/", "this default endpoint is for CTT");
  
  private int code;
  private String value;
  private String description;

  public static int DEFAULT_TYPE = 1;
  public static int URI_TYPE = 2;
  public static int SYSTEM_NAMESPACE_INDEX = 0;
  public static int DEFAULT_NAMESPACE_INDEX = 2;
  public static int DEFAULT_REQUEST_ID = 10000;
  public static final int MAX_BROWSEREQUEST_SIZE = 10;

  private EdgeOpcUaCommon(int code, String value, String description) {
    this.code = code;
    this.value = value;
    this.description = description;
  }

  /**
   * @fn int getCode()
   * @brief get enum code
   * @return code
   */
  public int getCode() {
    return code;
  }

  /**
   * @fn String getValue()
   * @brief get enum value
   * @return value
   */
  public String getValue() {
    return value;
  }

  /**
   * @fn String getDescription()
   * @brief get description
   * @return description
   */
  public String getDescription() {
    return description;
  }
}
