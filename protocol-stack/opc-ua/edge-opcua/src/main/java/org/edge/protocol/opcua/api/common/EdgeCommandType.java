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

public enum EdgeCommandType {
  CMD_READ(0, "read", "read async"),
  CMD_WRITE(1, "write", "write async"),
  CMD_SUB(2, "sub", "subscription"),
  CMD_START_SERVER(3, "start_server", "start server"),
  CMD_START_CLIENT(4, "start_client", "start client and connect to server"),
  CMD_STOP_SERVER(5, "stop_server", "stop server"),
  CMD_STOP_CLIENT(6, "stop_client", "stop client and disconnect"),
  CMD_GET_ENDPOINTS(7, "endpoint_discovery", "get endpoints from server"),
  CMD_BROWSE(8, "browse", "browse nodes from server"),
  CMD_METHOD(9, "method", "call method nodes from server");

  private int code;
  private String value;
  private String description;

  private EdgeCommandType(int code, String value, String description) {
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
