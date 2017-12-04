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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.providers.EdgeServices;
import org.edgexfoundry.domain.meta.Command;
import org.edgexfoundry.domain.meta.DeviceObject;
import org.edgexfoundry.domain.meta.DeviceProfile;
import org.edgexfoundry.domain.meta.ProfileResource;

public class DeviceProfileGenerator {
  private static ArrayList<String> attributeProviderKeyList = null;

  private DeviceProfileGenerator() {}

  public static DeviceProfile generateDeviceProfile(String deviceInfoKey) {
    DeviceProfile deviceProfile = new DeviceProfile();
    deviceProfile.setOrigin(new Timestamp(System.currentTimeMillis()).getTime());
    deviceProfile.setCreated(new Timestamp(System.currentTimeMillis()).getTime());
    deviceProfile.setName(deviceInfoKey);
    deviceProfile.setManufacturer(OPCUADefaultMetaData.MANUFACTURER.getValue());
    deviceProfile.setModel(OPCUADefaultMetaData.MODEL.getValue());
    deviceProfile.setDescription(OPCUADefaultMetaData.DESCRIPTION_DEVICEPROFILE.getValue());
    deviceProfile.setObjects(OPCUADefaultMetaData.OBJ.getValue());
    String[] labels = {OPCUADefaultMetaData.LABEL1.getValue(), OPCUADefaultMetaData.LABEL2.getValue()};
    deviceProfile.setLabels(labels);

    List<DeviceObject> deviceObjectList = new ArrayList<DeviceObject>();
    List<ProfileResource> profileResourceList = new ArrayList<ProfileResource>();
    List<Command> commandList = new ArrayList<Command>();

    for (String providerKey : getAttributeProviderKeyList()) {
      deviceObjectList.add(DeviceObjectGenerator.generateDeviceObject(providerKey,
          OPCUACommandIdentifier.ATTRIBUTE_COMMAND.getValue()));
      profileResourceList.add(ProfileResourceGenerator.generateProfileResource(providerKey,
          OPCUACommandIdentifier.ATTRIBUTE_COMMAND.getValue()));
      commandList.add(CommandGenerator.newCommand(providerKey,
          OPCUACommandIdentifier.ATTRIBUTE_COMMAND.getValue()));
    }
    deviceObjectList.add(DeviceObjectGenerator.generateDeviceObject(
        OPCUACommandIdentifier.WELLKNOWN_COMMAND_GROUP.getValue(),
        OPCUACommandIdentifier.WELLKNOWN_COMMAND.getValue()));
    profileResourceList.add(ProfileResourceGenerator.generateProfileResource(
        OPCUACommandIdentifier.WELLKNOWN_COMMAND_GROUP.getValue(),
        OPCUACommandIdentifier.WELLKNOWN_COMMAND.getValue()));
    commandList.add(
        CommandGenerator.newCommand(OPCUACommandIdentifier.WELLKNOWN_COMMAND_GROUP.getValue(),
            OPCUACommandIdentifier.WELLKNOWN_COMMAND.getValue()));
    deviceProfile.setDeviceResources(deviceObjectList);
    deviceProfile.setResources(profileResourceList);
    deviceProfile.setCommands(commandList);

    return deviceProfile;
  }

  public static ArrayList<String> getAttributeProviderKeyList() {
    if (attributeProviderKeyList == null) {
      attributeProviderKeyList = new ArrayList<String>();
      for (String deviceInfoKey : EdgeServices.getAttributeProviderKeyList()) {
        if (deviceInfoKey.equals(EdgeOpcUaCommon.WELL_KNOWN_GROUP.getValue())) {
          continue;
        }
        attributeProviderKeyList
            .add(deviceInfoKey.replaceAll("/", OPCUADefaultMetaData.REPLACE_DEVICE_NAME));
      }
    }
    return attributeProviderKeyList;
  }

}
