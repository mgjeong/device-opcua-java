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
   * construct OPCUACommandIdentifier
   * 
   * @param code code number of OPCUACommandIdentifier
   * @param value value of OPCUACommandIdentifier
   */
  private OPCUACommandIdentifier(int code, String value) {
    this.code = code;
    this.value = value;
  }

  /**
   * Get code number of OPCUACommandIdentifier
   * 
   * @return code number of OPCUACommandIdentifier
   */
  public int getCode() {
    return code;
  }

  /**
   * Get value of OPCUACommandIdentifier
   * 
   * @return value of OPCUACommandIdentifier
   */
  public String getValue() {
    return value;
  }
}
