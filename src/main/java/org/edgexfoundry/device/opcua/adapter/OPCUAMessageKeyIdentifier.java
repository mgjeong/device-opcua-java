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

package org.edgexfoundry.device.opcua.adapter;

public enum OPCUAMessageKeyIdentifier {
  OPERATION(0, "operation", "command operation"),
  INPUT_ARGUMENT(1, "input_argument", "input value in put command operation"),
  RESULT(2, "result", "command result status"),
  VALUE(3, "value", "command result value"),
  VALUE_DESCRIPTOR(4, "value_descriptor", "command valuedescriptor name"),
  SAMPLING_INTERVAL(5, "sampling_interval", "sampling interval"), 
  APPLICATION_NAME(6, "application_name", "application name for server"),
  APPLICATION_URI(7, "application_uri", "application uri for server"),
  SECURITY_POLICY_URI (8, "security_policy_uri", "security policy uri for server"),
  ENDPOINT_URI(9, "endpoint_uri", "endpoint uri for server"), 
  ENDPOINT_INFOMATION(10, "endpoint_information", "endpoint infomation for server"), 
  RESPONSE_INFO(11,"response_information", "response infomation");

  private int code;
  private String value;
  private String description;

  /**
   * construct OPCUAMessageKeyIdentifier
   * 
   * @param code code number of OPCUAMessageKeyIdentifier
   * @param value message of OPCUAMessageKeyIdentifier
   * @param description description of OPCUAMessageKeyIdentifier
   */
  private OPCUAMessageKeyIdentifier(int code, String value, String description) {
    this.code = code;
    this.value = value;
    this.description = description;
  }

  /**
   * Get code number of OPCUAMessageKeyIdentifier
   * 
   * @return code number of OPCUAMessageKeyIdentifier
   */
  public int getCode() {
    return code;
  }

  /**
   * Get value of OPCUAMessageKeyIdentifier
   * 
   * @return value of OPCUAMessageKeyIdentifier
   */
  public String getValue() {
    return value;
  }

  /**
   * Get description of OPCUAMessageKeyIdentifier
   * 
   * @return description of OPCUAMessageKeyIdentifier
   */
  public String getDescription() {
    return description;
  }
}
