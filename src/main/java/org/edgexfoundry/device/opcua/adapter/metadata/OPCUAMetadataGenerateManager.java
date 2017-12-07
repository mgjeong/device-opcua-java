package org.edgexfoundry.device.opcua.adapter.metadata;

import java.util.ArrayList;
import java.util.List;
import org.edge.protocol.mapper.api.EdgeMapper;
import org.edge.protocol.mapper.api.EdgeMapperCommon;
import org.edge.protocol.opcua.api.common.EdgeCommandType;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.providers.EdgeServices;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Command;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.DeviceObject;
import org.edgexfoundry.domain.meta.DeviceProfile;
import org.edgexfoundry.domain.meta.ProfileResource;
import org.edgexfoundry.domain.meta.ResourceOperation;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OPCUAMetadataGenerateManager {
  private final static EdgeXLogger logger =
      EdgeXLoggerFactory.getEdgeXLogger(OPCUAMetadataGenerateManager.class);

  @Autowired
  private DeviceEnroller deviceEnroller;

  @Autowired
  private DeviceGenerator deviceGenerator;

  @Autowired
  private DeviceProfileGenerator deviceProfileGenerator;

  private final static int startOperationIndex = 1;

  public void initMetaData() {
    
    if (deviceEnroller == null || deviceProfileGenerator == null || deviceGenerator == null) {
      logger.error("metadata instacne is invalid");
      return;
    }
      
    String name = OPCUADefaultMetaData.DEVICE_NAME.getValue();
    Addressable addressable = AddressableGenerator.generate(name);
    deviceEnroller.addAddressableToMetaData(addressable);

    List<DeviceObject> deviceObjectList = new ArrayList<DeviceObject>();
    List<ProfileResource> profileResourceList = new ArrayList<ProfileResource>();
    List<Command> commandList = new ArrayList<Command>();

    String command_type = OPCUACommandIdentifier.WELLKNOWN_COMMAND.getValue();
    for (OPCUACommandIdentifier wellknownCommand : OPCUACommandIdentifier.WELLKNOWN_COMMAND_LIST) {
      String commandName = wellknownCommand.getValue();
      deviceObjectList.add(DeviceObjectGenerator.generate(commandName, command_type));
      List<ResourceOperation> setList = createWellKnownSetList(commandName);
      List<ResourceOperation> getList = null;
      profileResourceList.add(ProfileResourceGenerator.generate(commandName, getList, setList));
      commandList.add(CommandGenerator.generate(commandName, null));
    }
    DeviceProfile deviceProfile =
        deviceProfileGenerator.generate(name, deviceObjectList, profileResourceList, commandList);
    deviceEnroller.addDeviceProfileToMetaData(deviceProfile);
    Device device = deviceGenerator.generate(name);
    deviceEnroller.addDeviceToMetaData(device);
  }

  private void updateAttributeService(String deviceProfileName, String commandType) {
    for (String providerKey : getAttributeProviderKeyList()) {
      EdgeMapper mapper = EdgeServices.getAttributeProvider(providerKey)
          .getAttributeService(providerKey).getMapper();
      if (mapper == null) {
        mapper = new EdgeMapper();
      }

      String deviceInfoName = providerKey.replaceAll("/", OPCUADefaultMetaData.REPLACE_DEVICE_NAME);

      Command command = CommandGenerator.generate(deviceInfoName,
          mapper.getMappingData(EdgeMapperCommon.PROPERTYVALUE_READWRITE.name()));
      DeviceProfile deviceProfile = deviceProfileGenerator.update(deviceProfileName, command);
      deviceEnroller.updateDeviceProfileToMetaData(deviceProfile);

      DeviceObject deviceObject = DeviceObjectGenerator.generate(deviceInfoName, commandType);
      deviceProfile = deviceProfileGenerator.update(deviceProfileName, deviceObject);
      deviceEnroller.updateDeviceProfileToMetaData(deviceProfile);

      List<ResourceOperation> getList = createAttributeGetResourceOperation(deviceInfoName);
      List<ResourceOperation> setList = createAttributeSetResourceOperation(deviceInfoName);
      ProfileResource profileResource =
          ProfileResourceGenerator.generate(deviceInfoName, getList, setList);
      deviceProfile = deviceProfileGenerator.update(deviceProfileName, profileResource);
      deviceEnroller.updateDeviceProfileToMetaData(deviceProfile);
    }
  }

  private void updateMethodService(String deviceProfileName, String commandType) {
    for (String providerKey : getMethodProviderKeyList()) {
      String deviceInfoName = providerKey.replaceAll("/", OPCUADefaultMetaData.REPLACE_DEVICE_NAME);

      Command command = CommandGenerator.generate(deviceInfoName, null);
      DeviceProfile deviceProfile = deviceProfileGenerator.update(deviceProfileName, command);
      deviceEnroller.updateDeviceProfileToMetaData(deviceProfile);

      DeviceObject deviceObject = DeviceObjectGenerator.generate(deviceInfoName, commandType);
      deviceProfile = deviceProfileGenerator.update(deviceProfileName, deviceObject);
      deviceEnroller.updateDeviceProfileToMetaData(deviceProfile);

      List<ResourceOperation> getList = createMethodGetResourceOperation(deviceInfoName);
      List<ResourceOperation> setList = createMethodSetResourceOperation(deviceInfoName);
      ProfileResource profileResource =
          ProfileResourceGenerator.generate(deviceInfoName, getList, setList);
      deviceProfile = deviceProfileGenerator.update(deviceProfileName, profileResource);
      deviceEnroller.updateDeviceProfileToMetaData(deviceProfile);
    }
  }

  public void updateMetaData(String deviceProfileName) {
    updateAttributeService(deviceProfileName, OPCUACommandIdentifier.ATTRIBUTE_COMMAND.getValue());
    updateMethodService(deviceProfileName, OPCUACommandIdentifier.METHOD_COMMAND.getValue());
  }

  private static ArrayList<String> getAttributeProviderKeyList() {
    ArrayList<String> attributeProviderKeyList = new ArrayList<String>();
    for (String providerKey : EdgeServices.getAttributeProviderKeyList()) {
      if (providerKey.equals(EdgeOpcUaCommon.WELL_KNOWN_GROUP.getValue())) {
        continue;
      }
      attributeProviderKeyList.add(providerKey);
    }
    return attributeProviderKeyList;
  }

  private static ArrayList<String> getMethodProviderKeyList() {
    ArrayList<String> methodProviderKeyList = new ArrayList<String>();
    for (String providerKey : EdgeServices.getMethodProviderKeyList()) {
      methodProviderKeyList.add(providerKey);
    }
    return methodProviderKeyList;
  }

  // ProfileResource
  private List<ResourceOperation> createWellKnownSetList(String deviceInfoKey) {
    List<ResourceOperation> setList = null;
    if (OPCUACommandIdentifier.WELLKNOWN_COMMAND_GROUP.getValue().equals(deviceInfoKey) == true) {
      setList = createGroupResourceOperation(deviceInfoKey);
    } else if (OPCUACommandIdentifier.WELLKNOWN_COMMAND_START.getValue()
        .equals(deviceInfoKey) == true) {
      setList = createStartServiceOperation(deviceInfoKey);
    } else if (OPCUACommandIdentifier.WELLKNOWN_COMMAND_STOP.getValue()
        .equals(deviceInfoKey) == true) {
      setList = createStopServiceOperation(deviceInfoKey);
    } else if (OPCUACommandIdentifier.WELLKNOWN_COMMAND_GETENDPOINT.getValue()
        .equals(deviceInfoKey) == true) {
      setList = createGetEndpointServiceOperation(deviceInfoKey);
    } else {
      return null;
    }
    return setList;
  }

  private List<ResourceOperation> createAttributeGetResourceOperation(String deviceInfoKey) {
    int getOperationIndex = startOperationIndex;
    List<ResourceOperation> getList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    getList.add(ProfileResourceGenerator.createGetOperation(deviceInfoKey,
        EdgeCommandType.CMD_READ.getValue(), getOperationIndex++));
    return getList;
  }

  private List<ResourceOperation> createAttributeSetResourceOperation(String deviceInfoKey) {
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperationIndex;
    setList.add(ProfileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_WRITE.getValue(), putOperationIndex++));
    setList.add(ProfileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_SUB.getValue(), putOperationIndex++));
    return setList;
  }

  private List<ResourceOperation> createMethodGetResourceOperation(String deviceInfoKey) {
    int getOperationIndex = startOperationIndex;
    List<ResourceOperation> getList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    getList.add(ProfileResourceGenerator.createGetOperation(deviceInfoKey,
        EdgeCommandType.CMD_READ.getValue(), getOperationIndex++));
    return getList;
  }

  private List<ResourceOperation> createMethodSetResourceOperation(String deviceInfoKey) {
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperationIndex;
    setList.add(ProfileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_METHOD.getValue(), putOperationIndex++));
    return setList;
  }


  private List<ResourceOperation> createGroupResourceOperation(String deviceInfoKey) {
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperationIndex;
    setList.add(ProfileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_READ.getValue(), putOperationIndex++));
    setList.add(ProfileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_WRITE.getValue(), putOperationIndex++));
    setList.add(ProfileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_SUB.getValue(), putOperationIndex++));
    return setList;
  }

  private List<ResourceOperation> createStartServiceOperation(String deviceInfoKey) {
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperationIndex;
    setList.add(ProfileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_START_CLIENT.getValue(), putOperationIndex++));
    return setList;
  }

  private List<ResourceOperation> createStopServiceOperation(String deviceInfoKey) {
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperationIndex;
    setList.add(ProfileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_STOP_CLIENT.getValue(), putOperationIndex++));
    return setList;
  }

  private List<ResourceOperation> createGetEndpointServiceOperation(String deviceInfoKey) {
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperationIndex;
    setList.add(ProfileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_GET_ENDPOINTS.getValue(), putOperationIndex++));
    return setList;
  }
}
