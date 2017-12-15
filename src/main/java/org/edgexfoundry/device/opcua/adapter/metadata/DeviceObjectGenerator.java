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

  /**
   * construct DeviceObjectGenerator <br>
   */
  private DeviceObjectGenerator() {}

  
  /**
   * Get Attribute information from opcua device<br>
   * Use {@link org.edge.protocol.mapper.api.EdgeMappe} to get attribute information
   * 
   * @param name key of opcua client provider service
   * @param id mapping id in EdgeMapper
   * @param deviceType type of command (wellknown/method/attribute)
   * 
   * @return attribute information
   */
  private static String getAttributeInfo(String name, String id, String deviceType) {
    if (OPCUACommandIdentifier.WELLKNOWN_COMMAND.getValue().equals(deviceType) == true
        || OPCUACommandIdentifier.METHOD_COMMAND.getValue().equals(deviceType) == true) {
      if (id.equals(EdgeMapperCommon.PROPERTYVALUE_READWRITE.name())) {
        return OPCUADefaultMetaData.READ_WRITE;
      } else {
        return null;
      }
    }
    name = name.replaceAll(OPCUADefaultMetaData.AFTER_REPLACE_WORD,
        OPCUADefaultMetaData.BEFORE_REPLACE_WORD);
    try {
      EdgeMapper mapper =
          EdgeServices.getAttributeProvider(name).getAttributeService(name).getMapper();
      if (mapper == null) {
        return null;
      } else {
        return mapper.getMappingData(id);
      }
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Create PropertyValue<br>
   * Use {@link #getAttributeInfo(String, String, String)} to get attribute information
   * 
   * @param deviceInfoKey key of opcua client provider service
   * @param deviceType type of command (wellknown/method/attribute)
   * 
   * @return created PropertyValue
   */
  private static PropertyValue createPropertyValue(String deviceInfoKey, String deviceType) {
    PropertyValue propertyValue = new PropertyValue();
    propertyValue.setType(OPCUADefaultMetaData.TYPE.getValue());
    propertyValue.setReadWrite(getAttributeInfo(deviceInfoKey,
        EdgeMapperCommon.PROPERTYVALUE_READWRITE.name(), deviceType));
    propertyValue.setMinimum(
        getAttributeInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_MIN.name(), deviceType));
    propertyValue.setMaximum(
        getAttributeInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_MAX.name(), deviceType));
    propertyValue.setDefaultValue(OPCUADefaultMetaData.DEFAULTVALUE.getValue());
    propertyValue.setSize(OPCUADefaultMetaData.SIZE.getValue());
    propertyValue.setPrecision(getAttributeInfo(deviceInfoKey,
        EdgeMapperCommon.PROPERTYVALUE_PRECISION.name(), deviceType));
    propertyValue.setLSB(OPCUADefaultMetaData.LSB.getValue());

    ///// optional
    propertyValue.setAssertion(getAttributeInfo(deviceInfoKey,
        EdgeMapperCommon.PROPERTYVALUE_ASSERTION.name(), deviceType));
    propertyValue.setScale(
        getAttributeInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_SCALE.name(), deviceType));

    return propertyValue;
  }

  /**
   * Create Units<br>
   * Use {@link #getAttributeInfo(String, String, String)} to get attribute information
   * 
   * @param deviceInfoKey key of opcua client provider service
   * @param deviceType type of command (wellknown/method/attribute)
   * 
   * @return created Units
   */
  private static Units createUnits(String deviceInfoKey, String deviceType) {
    Units units = new Units();
    units.setType(OPCUADefaultMetaData.TYPE.getValue());
    units.setReadWrite(
        getAttributeInfo(deviceInfoKey, EdgeMapperCommon.UNITS_READWRITE.name(), deviceType));
    units.setDefaultValue(OPCUADefaultMetaData.DEFAULTVALUE.getValue());
    return units;
  }

  /**
   * Create ProfileProperty<br>
   * Use {@link #createPropertyValue(String, String)} to create PropertyValue
   * Use {@link #createUnits(String, String)} to create Units
   * 
   * @param deviceInfoKey key of opcua client provider service
   * @param deviceType type of command (wellknown/method/attribute)
   * 
   * @return created ProfileProperty
   */
  private static ProfileProperty createProfileProperty(String deviceInfoKey, String deviceType) {
    ProfileProperty profileProperty = new ProfileProperty();
    PropertyValue propertyValue = createPropertyValue(deviceInfoKey, deviceType);
    profileProperty.setValue(propertyValue);
    Units units = createUnits(deviceInfoKey, deviceType);
    profileProperty.setUnits(units);
    return profileProperty;
  }

  /**
   * @fn DeviceObject generate(String name, String deviceType)
   * @brief Generate DeviceObject
   * @param [in] name @String
   * @param [in] deviceType @String
   * @return @DeviceObject
   */
  
  /**
   * Generate DeviceObject<br>
   * Use {@link #createProfileProperty(String, String)} to create ProfileProperty
   * 
   * @param name name which matched with Command and ProfileResource
   * @param deviceType type of command (wellknown/method/attribute)
   * 
   * @return generated DeviceObject
   */
  public static DeviceObject generate(String name, String deviceType) {
    if (name == null || name.isEmpty()) {
      return null;
    }

    DeviceObject deviceObj = new DeviceObject();
    deviceObj.setName(name);
    deviceObj.setTag(getAttributeInfo(name, EdgeMapperCommon.DEVICEOBJECT_TAG.name(), deviceType));
    deviceObj.setDescription(
        getAttributeInfo(name, EdgeMapperCommon.DEVICEOBJECT_DESCRIPTION.name(), deviceType));
    ProfileProperty property = createProfileProperty(name, deviceType);
    deviceObj.setProperties(property);
    deviceObj.setAttributes(new DeviceObjectAttributeInfo.Builder(name).setDataType(
        getAttributeInfo(name, EdgeMapperCommon.DEVICEOBJECT_ATTRIBUTE_DATATYPE.name(), deviceType))
        .build());
    return deviceObj;
  }
}
