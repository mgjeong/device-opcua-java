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

import org.edge.protocol.mapper.api.EdgeMapper;
import org.edge.protocol.mapper.api.EdgeMapperCommon;
import org.edge.protocol.opcua.providers.EdgeServices;
import org.edgexfoundry.domain.meta.DeviceObject;
import org.edgexfoundry.domain.meta.ProfileProperty;
import org.edgexfoundry.domain.meta.PropertyValue;
import org.edgexfoundry.domain.meta.Units;

public class DeviceObjectGenerator {

  private DeviceObjectGenerator() {}

  private static String getAttributeInfo(String deviceInfoKey, String id, String deviceType) {
    if (OPCUACommandIdentifier.WELLKNOWN_COMMAND.getValue().equals(deviceType) == true
        || OPCUACommandIdentifier.METHOD_COMMAND.getValue().equals(deviceType) == true){
      if(id.equals(EdgeMapperCommon.PROPERTYVALUE_READWRITE.name())){
        return "readwrite";
      }else{
        return null;
      }
    }
    deviceInfoKey = deviceInfoKey.replaceAll(OPCUADefaultMetaData.REPLACE_DEVICE_NAME, "/");
    EdgeMapper mapper = EdgeServices.getAttributeProvider(deviceInfoKey)
        .getAttributeService(deviceInfoKey).getMapper();
    if (mapper == null) {
      return null;
    } else {
      return mapper.getMappingData(id);
    }
  }

  private static PropertyValue createPropertyValue(String deviceInfoKey, String deviceType) {
    PropertyValue propertyValue = new PropertyValue();
    propertyValue.setType(OPCUADefaultMetaData.TYPE.getValue());
    propertyValue.setReadWrite(
        getAttributeInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_READWRITE.name(), deviceType));
    propertyValue.setMinimum(
        getAttributeInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_MIN.name(), deviceType));
    propertyValue.setMaximum(
        getAttributeInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_MAX.name(), deviceType));
    propertyValue.setDefaultValue(OPCUADefaultMetaData.DEFAULTVALUE.getValue());
    propertyValue.setSize(OPCUADefaultMetaData.SIZE.getValue());
    propertyValue.setPrecision(
        getAttributeInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_PRECISION.name(), deviceType));
    propertyValue.setLSB(OPCUADefaultMetaData.LSB.getValue());

    ///// optional
    propertyValue.setAssertion(
        getAttributeInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_ASSERTION.name(), deviceType));
    propertyValue.setScale(
        getAttributeInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_SCALE.name(), deviceType));

    return propertyValue;
  }

  private static Units createUnits(String deviceInfoKey, String deviceType) {
    Units units = new Units();
    units.setType(OPCUADefaultMetaData.TYPE.getValue());
    units.setReadWrite(
        getAttributeInfo(deviceInfoKey, EdgeMapperCommon.UNITS_READWRITE.name(), deviceType));
    units.setDefaultValue(OPCUADefaultMetaData.DEFAULTVALUE.getValue());
    return units;
  }

  private static ProfileProperty createProfileProperty(String deviceInfoKey, String deviceType) {
    ProfileProperty profileProperty = new ProfileProperty();
    PropertyValue propertyValue = createPropertyValue(deviceInfoKey, deviceType);
    profileProperty.setValue(propertyValue);
    Units units = createUnits(deviceInfoKey, deviceType);
    profileProperty.setUnits(units);
    return profileProperty;
  }

  public static DeviceObject generate(String deviceInfoKey, String deviceType) {
    DeviceObject deviceObj = new DeviceObject();
    deviceObj.setName(deviceInfoKey);
    deviceObj.setTag(getAttributeInfo(deviceInfoKey, EdgeMapperCommon.DEVICEOBJECT_TAG.name(), deviceType));
    deviceObj.setDescription(
        getAttributeInfo(deviceInfoKey, EdgeMapperCommon.DEVICEOBJECT_DESCRIPTION.name(), deviceType));
    ProfileProperty property = createProfileProperty(deviceInfoKey, deviceType);
    deviceObj.setProperties(property);
    deviceObj.setAttributes(new DeviceObjectAttributeInfo.Builder(deviceInfoKey)
        .setDataType(getAttributeInfo(deviceInfoKey,
            EdgeMapperCommon.DEVICEOBJECT_ATTRIBUTE_DATATYPE.name(), deviceType))
        .build());
    return deviceObj;
  }
}
