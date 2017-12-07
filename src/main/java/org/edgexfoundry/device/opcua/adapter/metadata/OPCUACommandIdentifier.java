package org.edgexfoundry.device.opcua.adapter.metadata;

import java.util.Arrays;
import java.util.List;

public enum OPCUACommandIdentifier {
  WELLKNOWN_COMMAND(10, "wellknown_command"),
  ATTRIBUTE_COMMAND(11, "attribute_command"),
  METHOD_COMMAND(12, "method_command"),
  WELLKNOWN_COMMAND_GROUP(20, "wellknown~groupcommand"),
  WELLKNOWN_COMMAND_START(21, "wellknown~startcommand"),
  WELLKNOWN_COMMAND_STOP(22, "wellknown~stopcommand"),
  WELLKNOWN_COMMAND_GETENDPOINT(23, "wellknown~getendpointcommand");

  public static final List<OPCUACommandIdentifier> WELLKNOWN_COMMAND_LIST =
      Arrays.asList(OPCUACommandIdentifier.WELLKNOWN_COMMAND_GROUP,
          OPCUACommandIdentifier.WELLKNOWN_COMMAND_GETENDPOINT,
          OPCUACommandIdentifier.WELLKNOWN_COMMAND_START,
          OPCUACommandIdentifier.WELLKNOWN_COMMAND_STOP);

  private int code;
  private String value;

  /**
   * @fn OPCUACommandIdentifier(int code, String value)
   * @brief constructor
   * @param [in] code @int
   * @param [in] value @String
   */
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
