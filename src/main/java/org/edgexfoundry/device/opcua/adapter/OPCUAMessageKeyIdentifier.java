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

  private OPCUAMessageKeyIdentifier(int code, String value, String description) {
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
