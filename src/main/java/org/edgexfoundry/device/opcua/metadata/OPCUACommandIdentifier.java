package org.edgexfoundry.device.opcua.metadata;

public enum OPCUACommandIdentifier {
  WELLKNOWN_COMMAND(10, "wellknown_command"), ATTRIBUTE_COMMAND(11,
      "attribute_command"), METHOD_COMMAND(12,
          "method_command"), WELLKNOWN_COMMAND_GROUP(20, "wellknown~groupcommand");

  private int code;
  private String value;

  private OPCUACommandIdentifier(int code, String value) {
    this.code = code;
    this.value = value;
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
}
