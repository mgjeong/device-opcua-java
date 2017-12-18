package org.edgexfoundry.device.opcua.adapter.metadata;

import java.util.ArrayList;
import java.util.List;
import org.edge.protocol.mapper.api.EdgeMapper;
import org.edge.protocol.mapper.api.EdgeMapperCommon;
import org.edge.protocol.opcua.api.common.EdgeCommandType;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.providers.EdgeAttributeProvider;
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

  /**
   * Initialize MetaData<br>
   * Use {@link DeviceObjectGenerator#generate(String, String)} to generate DeviceObject<br>
   * Use {@link #createWellKnownSetList(String)} to create list of wellknown command<br>
   * Use {@link ProfileResourceGenerator#generate(String, List, List)} to generate
   * ProfileResource<br>
   * Use {@link CommandGenerator#generate(String, String)} to generate Command<br>
   * Use {@link DeviceProfileGenerator#generate(String, List, List, List)} to generate
   * DeviceProfile<br>
   * Use {@link DeviceEnroller#addDeviceProfileToMetaData(DeviceProfile)} to add DeviceProfile to
   * MetaData<br>
   * Use {@link DeviceGenerator#generate(String)} to generate Device<br>
   * Use {@link DeviceEnroller#addDeviceToMetaData(Device)} to add Device to MetaData
   * 
   * @param name name of Device
   */
  public void initMetaData(String name) {
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

    if (null != deviceProfileGenerator && null != deviceEnroller && null != deviceGenerator) {
      DeviceProfile deviceProfile =
          deviceProfileGenerator.generate(name, deviceObjectList, profileResourceList, commandList);
      deviceEnroller.addDeviceProfileToMetaData(deviceProfile);
      Device device = deviceGenerator.generate(name);
      deviceEnroller.addDeviceToMetaData(device);
    } else {
      logger.error("metadata instacne is invalid");
    }
  }

  /**
   * Initialize Addressable<br>
   * Use {@link AddressableGenerator#generate(String)} to generate DeviceObject<br>
   * Use {@link DeviceEnroller#addAddressableToMetaData(Addressable)} to add Addressable to MetaData
   * 
   * @param name name of Addressable
   */
  public void initAddressable(String name) {
    Addressable addressable = AddressableGenerator.generate(name);
    deviceEnroller.addAddressableToMetaData(addressable);
  }


  /**
   * Add Attribute Service to DeviecProfile<br>
   * Use {@link org.edge.protocol.opcua.providers.EdgeServices#getAttributeProvider(String)} to get
   * EdgeAttribute Provider<br>
   * Use {@link #createAttributeGetResourceOperation(String)} to EdgeAttribute
   * GetResourceOperation<br>
   * Use {@link #createAttributeSetResourceOperation(String)} to EdgeAttribute
   * GetResourceOperation<br>
   * Use {@link ProfileResourceGenerator#generate(String, List, List)} to generate
   * ProfileResource<br>
   * Use {@link DeviceProfileGenerator#update(String, ProfileResource)} to update ProfileResource to
   * DeviceProfile<br>
   * Use {@link CommandGenerator#generate(String, String)} to generate Command<br>
   * Use {@link DeviceProfileGenerator#update(String, Command)} to update Comand to
   * DeviceProfile<br>
   * Use {@link DeviceObjectGenerator#generate(String, String)} to generate DeviceObject<br>
   * Use {@link DeviceProfileGenerator#update(String, DeviceObject)} to update DeviceObject to
   * DeviceProfile<br>
   * Use {@link DeviceEnroller#updateDeviceProfileToMetaData(DeviceProfile)} to update DeviceProfile
   * to MetaData
   * 
   * @param deviceProfileName name of DeviceProfile
   * @param commandType Type of Command ( attribute or method or wellknown )
   * @param keyList list of provider key
   */
  public void updateAttributeService(String deviceProfileName, String commandType,
      ArrayList<String> keyList) {
    if (keyList == null) {
      return;
    }
    for (String providerKey : keyList) {
      EdgeAttributeProvider provider = EdgeServices.getAttributeProvider(providerKey);
      EdgeMapper mapper = null;
      if (provider != null) {
        mapper = provider.getAttributeService(providerKey).getMapper();
      }
      if (mapper == null) {
        mapper = new EdgeMapper();
      }

      String deviceInfoName = providerKey.replaceAll(OPCUADefaultMetaData.BEFORE_REPLACE_WORD,
          OPCUADefaultMetaData.AFTER_REPLACE_WORD);

      List<ResourceOperation> getList = createAttributeGetResourceOperation(deviceInfoName);
      List<ResourceOperation> setList = createAttributeSetResourceOperation(deviceInfoName);
      ProfileResource profileResource =
          ProfileResourceGenerator.generate(deviceInfoName, getList, setList);

      if (deviceProfileGenerator == null || deviceEnroller == null) {
        return;
      }
      DeviceProfile deviceProfile =
          deviceProfileGenerator.update(deviceProfileName, profileResource);
      deviceEnroller.updateDeviceProfileToMetaData(deviceProfile);

      Command command = CommandGenerator.generate(deviceInfoName,
          mapper.getMappingData(EdgeMapperCommon.PROPERTYVALUE_READWRITE.name()));
      deviceProfile = deviceProfileGenerator.update(deviceProfileName, command);
      deviceEnroller.updateDeviceProfileToMetaData(deviceProfile);

      DeviceObject deviceObject = DeviceObjectGenerator.generate(deviceInfoName, commandType);
      deviceProfile = deviceProfileGenerator.update(deviceProfileName, deviceObject);
      deviceEnroller.updateDeviceProfileToMetaData(deviceProfile);
    }
  }

  /**
   * Add method Service to DeviecProfile<br>
   * Use {@link #createAttributeGetResourceOperation(String)} to EdgeAttribute
   * GetResourceOperation<br>
   * Use {@link #createAttributeSetResourceOperation(String)} to EdgeAttribute
   * GetResourceOperation<br>
   * Use {@link ProfileResourceGenerator#generate(String, List, List)} to generate
   * ProfileResource<br>
   * Use {@link DeviceProfileGenerator#update(String, ProfileResource)} to update ProfileResource to
   * DeviceProfile<br>
   * Use {@link CommandGenerator#generate(String, String)} to generate Command<br>
   * Use {@link DeviceProfileGenerator#update(String, Command)} to update Comand to
   * DeviceProfile<br>
   * Use {@link DeviceObjectGenerator#generate(String, String)} to generate DeviceObject<br>
   * Use {@link DeviceProfileGenerator#update(String, DeviceObject)} to update DeviceObject to
   * DeviceProfile<br>
   * Use {@link DeviceEnroller#updateDeviceProfileToMetaData(DeviceProfile)} to update DeviceProfile
   * to MetaData
   * 
   * @param deviceProfileName name of DeviceProfile
   * @param commandType Type of Command ( attribute or method or wellknown )
   * @param keyList list of provider key
   */
  public void updateMethodService(String deviceProfileName, String commandType,
      ArrayList<String> keyList) {
    if (keyList == null) {
      return;
    }

    for (String providerKey : keyList) {
      String deviceInfoName = providerKey.replaceAll(OPCUADefaultMetaData.BEFORE_REPLACE_WORD,
          OPCUADefaultMetaData.AFTER_REPLACE_WORD);

      List<ResourceOperation> getList = createMethodGetResourceOperation(deviceInfoName);
      List<ResourceOperation> setList = createMethodSetResourceOperation(deviceInfoName);
      ProfileResource profileResource =
          ProfileResourceGenerator.generate(deviceInfoName, getList, setList);

      if (deviceProfileGenerator == null || deviceEnroller == null) {
        return;
      }
      DeviceProfile deviceProfile =
          deviceProfileGenerator.update(deviceProfileName, profileResource);
      deviceEnroller.updateDeviceProfileToMetaData(deviceProfile);

      Command command = CommandGenerator.generate(deviceInfoName, null);
      deviceProfile = deviceProfileGenerator.update(deviceProfileName, command);
      deviceEnroller.updateDeviceProfileToMetaData(deviceProfile);

      DeviceObject deviceObject = DeviceObjectGenerator.generate(deviceInfoName, commandType);
      deviceProfile = deviceProfileGenerator.update(deviceProfileName, deviceObject);
      deviceEnroller.updateDeviceProfileToMetaData(deviceProfile);
    }
  }

  /**
   * update MetaData<br>
   * Use {@link #getAttributeProviderKeyList()} to get list of method provider key from opcua<br>
   * Use {@link #updateAttributeService(String, String, ArrayList)} to update EdgeAttribute
   * Service<br>
   * Use {@link #getMethodProviderKeyList()} to get list of method provider key from opcua<br>
   * Use {@link #updateMethodService(String, String, ArrayList)} to update EdgeAttribute Service
   * 
   * @param deviceProfileName name of DeviceProfile
   */
  public void updateMetaData(String deviceProfileName) {
    updateAttributeService(deviceProfileName, OPCUACommandIdentifier.ATTRIBUTE_COMMAND.getValue(),
        getAttributeProviderKeyList());
    updateMethodService(deviceProfileName, OPCUACommandIdentifier.METHOD_COMMAND.getValue(),
        getMethodProviderKeyList());
  }

  /**
   * get list of EdgeAttribute provider key from opcua<br>
   * Use {@link org.edge.protocol.opcua.providers.EdgeServices#getAttributeProviderKeyList()} to get
   * list of EdgeAttribute provider key from opcua<br>
   * 
   * @param list of EdgeAttribute provider key
   */
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

  /**
   * get list of method provider key from opcua<br>
   * Use {@link org.edge.protocol.opcua.providers.EdgeServices#getMethodProviderKeyList()} to get
   * list of method provider key from opcua<br>
   * 
   * @param list of method provider key
   */
  private static ArrayList<String> getMethodProviderKeyList() {
    ArrayList<String> methodProviderKeyList = new ArrayList<String>();
    for (String providerKey : EdgeServices.getMethodProviderKeyList()) {
      methodProviderKeyList.add(providerKey);
    }
    return methodProviderKeyList;
  }


  /**
   * Create Well-Known Command such as group read/write, start, stop, getEndpoint<br>
   * Use {@link #createGroupResourceOperation(String)} to create ResourceOperation for Group
   * command<br>
   * Use {@link #createStartServiceOperation(String)} to create ResourceOperation for Start
   * command<br>
   * Use {@link #createStopServiceOperation(String)} to create ResourceOperation for Stop
   * command<br>
   * Use {@link #createGetEndpointServiceOperation(String)} to create ResourceOperation for
   * GetEndpoint command<br>
   * 
   * @param list of ResourceOperation
   */
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

  /**
   * create ResourceOperation related Get for read command<br>
   * Use {@link ProfileResourceGenerator#createGetOperation(String, String, int)} to create
   * ResourceOperation for get command<br>
   * 
   * @param list of ResourceOperation
   */
  private List<ResourceOperation> createAttributeGetResourceOperation(String deviceInfoKey) {
    int getOperationIndex = startOperationIndex;
    List<ResourceOperation> getList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    getList.add(ProfileResourceGenerator.createGetOperation(deviceInfoKey,
        EdgeCommandType.CMD_READ.getValue(), getOperationIndex++));
    return getList;
  }

  /**
   * create ResourceOperation related Set for write / subscription command<br>
   * Use {@link ProfileResourceGenerator#createPutOperation(String, String, int)} to create
   * ResourceOperation for put command<br>
   * 
   * @param list of ResourceOperation
   */
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

  /**
   * create ResourceOperation related Get for method types of the opcua<br>
   * Use {@link ProfileResourceGenerator#createGetOperation(String, String, int)} to create
   * ResourceOperation for get command<br>
   * 
   * @param list of ResourceOperation
   */
  private List<ResourceOperation> createMethodGetResourceOperation(String deviceInfoKey) {
    int getOperationIndex = startOperationIndex;
    List<ResourceOperation> getList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    getList.add(ProfileResourceGenerator.createGetOperation(deviceInfoKey,
        EdgeCommandType.CMD_READ.getValue(), getOperationIndex++));
    return getList;
  }

  /**
   * create ResourceOperation related Set for method types of the opcua<br>
   * Use {@link ProfileResourceGenerator#createPutOperation(String, String, int)} to create
   * ResourceOperation for put command<br>
   * 
   * @param list of ResourceOperation
   */
  private List<ResourceOperation> createMethodSetResourceOperation(String deviceInfoKey) {
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperationIndex;
    setList.add(ProfileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_METHOD.getValue(), putOperationIndex++));
    return setList;
  }

  /**
   * create ResourceOperation related Set for group access<br>
   * Use {@link ProfileResourceGenerator#createPutOperation(String, String, int)} to create
   * ResourceOperation for put command<br>
   * 
   * @param list of ResourceOperation
   */
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

  /**
   * create ResourceOperation related Set for start command<br>
   * Use {@link ProfileResourceGenerator#createPutOperation(String, String, int)} to create
   * ResourceOperation for put command<br>
   * 
   * @param list of ResourceOperation
   */
  private List<ResourceOperation> createStartServiceOperation(String deviceInfoKey) {
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperationIndex;
    setList.add(ProfileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_START_CLIENT.getValue(), putOperationIndex++));
    return setList;
  }

  /**
   * create ResourceOperation related Set for stop command<br>
   * Use {@link ProfileResourceGenerator#createPutOperation(String, String, int)} to create
   * ResourceOperation for put command<br>
   * 
   * @param list of ResourceOperation
   */
  private List<ResourceOperation> createStopServiceOperation(String deviceInfoKey) {
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperationIndex;
    setList.add(ProfileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_STOP_CLIENT.getValue(), putOperationIndex++));
    return setList;
  }

  /**
   * create ResourceOperation related Set for getEndpoint command<br>
   * Use {@link ProfileResourceGenerator#createPutOperation(String, String, int)} to create
   * ResourceOperation for put command<br>
   * 
   * @param list of ResourceOperation
   */
  private List<ResourceOperation> createGetEndpointServiceOperation(String deviceInfoKey) {
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperationIndex;
    setList.add(ProfileResourceGenerator.createPutOperation(deviceInfoKey,
        EdgeCommandType.CMD_GET_ENDPOINTS.getValue(), putOperationIndex++));
    return setList;
  }
}
