package org.edgexfoundry.device.opcua.adapter.metadata;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OPCUAMetadataGenerateManager {

  @Autowired
  private DeviceEnroller deviceEnroller;

  @Autowired
  private DeviceGenerator deviceGenerator;

  @Autowired
  private DeviceProfileGenerator deviceProfileGenerator;

  @Autowired
  private ProfileResourceGenerator profileResourceGenerator;

  private final static int startOperarionIndex = 1;

  public void initMetaData() {
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
      profileResourceList.add(profileResourceGenerator.generate(commandName, getList, setList));
      commandList.add(CommandGenerator.generate(commandName, command_type));
    }
    DeviceProfile deviceProfile =
        deviceProfileGenerator.generate(name, deviceObjectList, profileResourceList, commandList);
    deviceEnroller.addDeviceProfileToMetaData(deviceProfile);
    Device device = deviceGenerator.generate(name);
    deviceEnroller.addDeviceToMetaData(device);
  }

  public void updateMetaData(String deviceProfileName) {
    String command_type = OPCUACommandIdentifier.ATTRIBUTE_COMMAND.getValue();
    for (String providerKey : getAttributeProviderKeyList()) {
      Command command = CommandGenerator.generate(providerKey, command_type);
      DeviceProfile deviceProfile = deviceProfileGenerator.update(deviceProfileName, command);
      deviceEnroller.updateDeviceProfileToMetaData(deviceProfile);

      DeviceObject deviceObject = DeviceObjectGenerator.generate(providerKey, command_type);
      deviceProfile = deviceProfileGenerator.update(deviceProfileName, deviceObject);
      deviceEnroller.updateDeviceProfileToMetaData(deviceProfile);

      List<ResourceOperation> getList = createAttributeGetResourceOperation(providerKey);
      List<ResourceOperation> setList = createAttributeSetResourceOperation(providerKey);
      ProfileResource profileResource =
          profileResourceGenerator.generate(providerKey, getList, setList);
      deviceProfile = deviceProfileGenerator.update(deviceProfileName, profileResource);
      deviceEnroller.updateDeviceProfileToMetaData(deviceProfile);
    }
  }

  private static ArrayList<String> getAttributeProviderKeyList() {
    ArrayList<String> attributeProviderKeyList = new ArrayList<String>();
    for (String deviceInfoKey : EdgeServices.getAttributeProviderKeyList()) {
      if (deviceInfoKey.equals(EdgeOpcUaCommon.WELL_KNOWN_GROUP.getValue())) {
        continue;
      }
      attributeProviderKeyList
          .add(deviceInfoKey.replaceAll("/", OPCUADefaultMetaData.REPLACE_DEVICE_NAME));
    }
    return attributeProviderKeyList;
  }

  // ProfileResource
  private List<ResourceOperation> createWellKnownSetList(String deviceInfoKey) {
    List<ResourceOperation> setList = null;
    if (OPCUACommandIdentifier.WELLKNOWN_COMMAND_GROUP.getValue()
        .equals(deviceInfoKey) == true) {
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
    int getOperationIndex = startOperarionIndex;
    List<ResourceOperation> getList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    getList.add(profileResourceGenerator.createGetOperation(deviceInfoKey,
        EdgeCommandType.CMD_READ.getValue(), getOperationIndex++));
    return getList;
  }

  private List<ResourceOperation> createAttributeSetResourceOperation(String deviceInfoKey) {
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperarionIndex;
    setList.add(profileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_WRITE.getValue(), putOperationIndex++));
    setList.add(profileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_SUB.getValue(), putOperationIndex++));
    return setList;
  }

  private List<ResourceOperation> createGroupResourceOperation(String deviceInfoKey) {
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperarionIndex;
    setList.add(profileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_READ.getValue(), putOperationIndex++));
    setList.add(profileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_WRITE.getValue(), putOperationIndex++));
    setList.add(profileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_SUB.getValue(), putOperationIndex++));
    return setList;
  }

  private List<ResourceOperation> createStartServiceOperation(String deviceInfoKey) {
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperarionIndex;
    setList.add(profileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_START_CLIENT.getValue(), putOperationIndex++));
    return setList;
  }

  private List<ResourceOperation> createStopServiceOperation(String deviceInfoKey) {
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperarionIndex;
    setList.add(profileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_STOP_CLIENT.getValue(), putOperationIndex++));
    return setList;
  }

  private List<ResourceOperation> createGetEndpointServiceOperation(String deviceInfoKey) {
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperarionIndex;
    setList.add(profileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_GET_ENDPOINTS.getValue(), putOperationIndex++));
    return setList;
  }
}
