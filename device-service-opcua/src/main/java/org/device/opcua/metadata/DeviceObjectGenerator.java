package org.device.opcua.metadata;

import org.device.opcua.DataDefaultValue;
import org.edge.protocol.mapper.api.EdgeMapper;
import org.edge.protocol.mapper.api.EdgeMapperCommon;
import org.edge.protocol.opcua.providers.EdgeServices;
import org.edgexfoundry.domain.meta.DeviceObject;
import org.edgexfoundry.domain.meta.ProfileProperty;
import org.edgexfoundry.domain.meta.PropertyValue;
import org.edgexfoundry.domain.meta.Units;

public class DeviceObjectGenerator {

    private DeviceObjectGenerator() {
    }

    private static String getDeviceInfo(String deviceInfoKey, String id) {
        deviceInfoKey = deviceInfoKey.replaceAll(DataDefaultValue.REPLACE_DEVICE_NAME, "/");
        EdgeMapper mapper = EdgeServices.getAttributeProvider(deviceInfoKey)
            .getAttributeService(deviceInfoKey).getMapper();
        if(mapper == null){
          return null;
        }else{
          return mapper.getMappingData(id);
        }
    }

    private static PropertyValue newValue(String deviceInfoKey) {
        PropertyValue propertyValue = new PropertyValue();
        propertyValue.setType(DataDefaultValue.TYPE.getValue());
        propertyValue.setReadWrite(
                getDeviceInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_READWRITE.name()));
        propertyValue.setMinimum(
                getDeviceInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_MIN.name()));
        propertyValue.setMaximum(
                getDeviceInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_MAX.name()));
        propertyValue.setDefaultValue(DataDefaultValue.DEFAULTVALUE.getValue());
        propertyValue.setSize(DataDefaultValue.SIZE.getValue());
        propertyValue.setPrecision(
                getDeviceInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_PRECISION.name()));
        propertyValue.setLSB(DataDefaultValue.LSB.getValue());

        ///// optional
        propertyValue.setAssertion(
                getDeviceInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_ASSERTION.name()));
        propertyValue.setScale(
                getDeviceInfo(deviceInfoKey, EdgeMapperCommon.PROPERTYVALUE_SCALE.name()));

        return propertyValue;
    }

    private static Units newUnits(String deviceInfoKey) {
        Units units = new Units();
        units.setType(DataDefaultValue.TYPE.getValue());
        units.setReadWrite(getDeviceInfo(deviceInfoKey, EdgeMapperCommon.UNITS_READWRITE.name()));
        units.setDefaultValue(DataDefaultValue.DEFAULTVALUE.getValue());
        return units;
    }

    private static ProfileProperty newProperty(String deviceInfoKey) {
        ProfileProperty profileProperty = new ProfileProperty();
        PropertyValue propertyValue = newValue(deviceInfoKey);
        profileProperty.setValue(propertyValue);
        Units units = newUnits(deviceInfoKey);
        profileProperty.setUnits(units);
        return profileProperty;
    }

    public static DeviceObject newDeviceObject(String deviceInfoKey) {
        DeviceObject dObj = new DeviceObject();
        dObj.setName(deviceInfoKey);
        dObj.setTag(getDeviceInfo(deviceInfoKey, EdgeMapperCommon.DEVICEOBJECT_TAG.name()));
        dObj.setDescription(
                getDeviceInfo(deviceInfoKey, EdgeMapperCommon.DEVICEOBJECT_DESCRIPTION.name()));
        ProfileProperty dProp = newProperty(deviceInfoKey);
        dObj.setProperties(dProp);
        dObj.setAttributes(new DeviceObjectAttributeInfo.Builder(deviceInfoKey)
        		.setDataType(getDeviceInfo(deviceInfoKey, 
        				EdgeMapperCommon.DEVICEOBJECT_ATTRIBUTE_DATATYPE.name())).build());
        return dObj;
    }
}
