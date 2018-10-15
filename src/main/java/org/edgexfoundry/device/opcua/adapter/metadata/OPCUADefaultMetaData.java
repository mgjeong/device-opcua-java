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

package org.edgexfoundry.device.opcua.adapter.metadata;

public enum OPCUADefaultMetaData {

  MIN(0, "Min", "ValueDescriptor/DeviceObject Min Value"),
  MAX(1, "Max", "ValueDescriptor/DeviceObject Max Value"),
  UOMLABEL(2, "UomLabel", "ValueDescriptor Uomlabel Value"),
  DEFAULTVALUE(3, "DefaultValue", "ValueDescriptor/DeviceObject Default Value"),
  LABEL1(4, "Label1", "ValueDescriptor/Device/DeviceProfile Label Value"),
  LABEL2(5, "Label2", "ValueDescriptor/Device/DeviceProfile Label Value"),

  NAME(6, "edge-opc-ua", "Addressable Name Value for OPC-UA"),
  ADDRESS(7, "localhost", "Addressable Address Value for OPC-UA"),
  PATH(8, "edge-opc-server", "Addressable Path Value for OPC-UA"),
  ADDRESS1(9, "127.0.0.1", "Addressable Address Value for OPC-UA"),

  DESCRIPTION_DEVICE(14, "This is Device", "Device Description Value"),
  LABEL_DEVICE(15, "OPCUA", "Device Label Value"),
  LABEL_DEFAULT(16, "Default", "Device Label Value"),
  LOCATION(17, "{40lat;45long}", "Device Location Value"),

  TYPE(18, "S", "DeviceObject Type Value"),
  SIZE(19, "10", "DeviceObject Size Value"),
  PRECISION(20, "Precision", "DeviceObject Percision Value"),
  LSB(21, null, "DeviceObject LSB Value"),
  TAG(22, "Tag", "DeviceObject Tag Value"),
  DESCRIPTION_DEVICEOBJECT(23, "This is Device Object", "DeviceObject Description Value"),

  MANUFACTURER(24, "Manufacturer", "DeviceProfile Manufacturer Value"),
  MODEL(25, "Model", "DeviceProfile Model Value"),
  DESCRIPTION_DEVICEPROFILE(26, "This is DeviceProfile", "DeviceProfile Description Value"),
  OBJ(27, "{key1:value1, key2:value2}", "DeviceProfile OBJ Value"),

  INDEX(28, "1", "ProfileResource Index Value"),
  OBJECT(31, "Object", "ProfileResource Object Value"),
  PROPERTY_GET(32, "value", "ProfileResource Get Property Value"),
  PROPERTY_SET(33, "value", "ProfileResource Set Property Value"),
  PARAMETER(34, "Parameter", "ProfileResource Parameter Value"),
  PARAMETER_OPERATION(35, "operation", "ProfileResource Parameter Value"),
  PARAMETER_VALUE(36, "value", "ProfileResource Parameter Value"),
  RESOURCE(37, null, "ProfileResource Resource Value"),

  DEVICE_NAME(40, "opc.tcp://localhost:12686/edge-opc-server", "Device Name"),
  DEVICESERVICE_NAME(41, "device-opcua-java", "Device Service Name"),
  DEFAULT_SECURE_TYPE(50, "http://opcfoundation.org/UA/SecurityPolicy#None", "Default SECURE TYPE");

  public static final int ADDRESSABLE_PORT = 12686;
  public static final long DEFAULT_LAST_CONNECTED = 1000000;
  public static final long DEFAULT_LAST_REPORTED = 2000000;
  public static final long DEFAULT_ORIGIN = 123456789;
  public static final String DEFAULT_ROOT_PATH = "/api/v1/device/{deviceId}/";

  public static final String READ_ONLY = "Read";
  public static final String WRITE_ONLY = "Write";
  public static final String READ_WRITE = "ReadWrite";
  public static final String INSTANCE = "Instance";

  public static final String AFTER_REPLACE_WORD = "~";
  public static final String BEFORE_REPLACE_WORD = "/";

  private int code;
  private String value;
  private String description;

  /**
   * construct OPCUADefaultMetaData
   *
   * @param code code number of OPCUADefaultMetaData
   * @param value value of OPCUADefaultMetaData
   * @param description description of OPCUADefaultMetaData
   */
  private OPCUADefaultMetaData(int code, String value, String description) {
      this.code = code;
      this.value = value;
      this.description = description;
  }

  /**
   * Get code number of OPCUADefaultMetaData
   *
   * @return code number of OPCUADefaultMetaData
   */
  public int getCode() {
    return code;
  }

  /**
   * Get value of OPCUADefaultMetaData
   *
   * @return value of OPCUADefaultMetaData
   */
  public String getValue() {
    return value;
  }

  /**
   * Get description of OPCUADefaultMetaData
   *
   * @return description of OPCUADefaultMetaData
   */
  public String getDescription() {
      return description;
  }
}