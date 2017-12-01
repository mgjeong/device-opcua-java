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

package org.edgexfoundry.device.opcua.metadata;

import org.edge.protocol.mapper.api.EdgeMapper;
import org.edge.protocol.mapper.api.EdgeMapperCommon;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.providers.EdgeServices;
import org.edgexfoundry.device.opcua.DataDefaultValue;
import org.edgexfoundry.device.opcua.adapter.OPCUAMessageKeyIdentifier;
import org.edgexfoundry.domain.meta.DeviceObject;
import org.edgexfoundry.domain.meta.ProfileProperty;
import org.edgexfoundry.domain.meta.PropertyValue;
import org.edgexfoundry.domain.meta.Units;

public class DeviceObjectGenerator {

  private DeviceObjectGenerator() {}

  private static String getDeviceInfo(String deviceInfoKey, String id, String deviceType) {
    if (OPCUAMessageKeyIdentifier.WELLKNOWN_COMMAND.getValue().equals(deviceType) == true)
      return null;
    deviceInfoKey = deviceInfoKey.replaceAll(DataDefaultValue.REPLACE_DEVICE_NAME, "/");
    EdgeMapper mapper = EdgeServices.getAttributeProvider(deviceInfoKey)
        .getAttributeService(deviceInfoKey).getMapper();
    if (mapper == null) {
      return null;
    } else {
      return mapper.getMappingData(id);
    }
  }

  private static PropertyValue newValue(String deviceInfoKey, String deviceType) {
    PropertyValue propertyValue = new PropertyValue();
    propertyValue.setType(DataDefaultValue.TYPE.getValue());
    propertyValue.setReadWrite(
        getDeviceInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_READWRITE.name(), deviceType));
    propertyValue.setMinimum(
        getDeviceInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_MIN.name(), deviceType));
    propertyValue.setMaximum(
        getDeviceInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_MAX.name(), deviceType));
    propertyValue.setDefaultValue(DataDefaultValue.DEFAULTVALUE.getValue());
    propertyValue.setSize(DataDefaultValue.SIZE.getValue());
    propertyValue.setPrecision(
        getDeviceInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_PRECISION.name(), deviceType));
    propertyValue.setLSB(DataDefaultValue.LSB.getValue());

    ///// optional
    propertyValue.setAssertion(
        getDeviceInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_ASSERTION.name(), deviceType));
    propertyValue.setScale(
        getDeviceInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_SCALE.name(), deviceType));

    return propertyValue;
  }

  private static Units newUnits(String deviceInfoKey, String deviceType) {
    Units units = new Units();
    units.setType(DataDefaultValue.TYPE.getValue());
    units.setReadWrite(
        getDeviceInfo(deviceInfoKey, EdgeMapperCommon.UNITS_READWRITE.name(), deviceType));
    units.setDefaultValue(DataDefaultValue.DEFAULTVALUE.getValue());
    return units;
  }

  private static ProfileProperty newProperty(String deviceInfoKey, String deviceType) {
    ProfileProperty profileProperty = new ProfileProperty();
    PropertyValue propertyValue = newValue(deviceInfoKey, deviceType);
    profileProperty.setValue(propertyValue);
    Units units = newUnits(deviceInfoKey, deviceType);
    profileProperty.setUnits(units);
    return profileProperty;
  }

  public static DeviceObject newDeviceObject(String deviceInfoKey, String deviceType) {
    DeviceObject dObj = new DeviceObject();
    dObj.setName(deviceInfoKey);
    dObj.setTag(getDeviceInfo(deviceInfoKey, EdgeMapperCommon.DEVICEOBJECT_TAG.name(), deviceType));
    dObj.setDescription(
        getDeviceInfo(deviceInfoKey, EdgeMapperCommon.DEVICEOBJECT_DESCRIPTION.name(), deviceType));
    ProfileProperty dProp = newProperty(deviceInfoKey, deviceType);
    dObj.setProperties(dProp);
    dObj.setAttributes(new DeviceObjectAttributeInfo.Builder(deviceInfoKey)
        .setDataType(getDeviceInfo(deviceInfoKey,
            EdgeMapperCommon.DEVICEOBJECT_ATTRIBUTE_DATATYPE.name(), deviceType))
        .build());
    return dObj;
  }
}