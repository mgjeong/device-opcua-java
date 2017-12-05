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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.edgexfoundry.controller.DeviceProfileClient;
import org.edgexfoundry.domain.meta.Command;
import org.edgexfoundry.domain.meta.DeviceObject;
import org.edgexfoundry.domain.meta.DeviceProfile;
import org.edgexfoundry.domain.meta.ProfileResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeviceProfileGenerator {

  @Autowired
  private DeviceProfileClient deviceProfileClient;

  private DeviceProfileGenerator() {}

  public DeviceProfile generate(String deviceProfileName, List<DeviceObject> deviceObjectList,
      List<ProfileResource> profileResourceList, List<Command> commandList) {
    DeviceProfile deviceProfile = new DeviceProfile();
    deviceProfile.setOrigin(new Timestamp(System.currentTimeMillis()).getTime());
    deviceProfile.setCreated(new Timestamp(System.currentTimeMillis()).getTime());
    deviceProfile.setName(deviceProfileName);
    deviceProfile.setManufacturer(OPCUADefaultMetaData.MANUFACTURER.getValue());
    deviceProfile.setModel(OPCUADefaultMetaData.MODEL.getValue());
    deviceProfile.setDescription(OPCUADefaultMetaData.DESCRIPTION_DEVICEPROFILE.getValue());
    deviceProfile.setObjects(OPCUADefaultMetaData.OBJ.getValue());
    String[] labels =
        {OPCUADefaultMetaData.LABEL1.getValue(), OPCUADefaultMetaData.LABEL2.getValue()};
    deviceProfile.setLabels(labels);

    deviceProfile.setDeviceResources(deviceObjectList);
    deviceProfile.setResources(profileResourceList);
    deviceProfile.setCommands(commandList);

    return deviceProfile;
  }

  public DeviceProfile update(String deviceProfileName, Command command) {
    DeviceProfile deviceProfile = deviceProfileClient.deviceProfileForName(deviceProfileName);
    deviceProfile.addCommand(command);
    return deviceProfile;
  }

  public DeviceProfile update(String deviceProfileName, DeviceObject deviceObject) {
    DeviceProfile deviceProfile = deviceProfileClient.deviceProfileForName(deviceProfileName);
    List<DeviceObject> deviceObjectList = deviceProfile.getDeviceResources();
    deviceObjectList.add(deviceObject);
    deviceProfile.setDeviceResources(deviceObjectList);
    return deviceProfile;
  }

  public DeviceProfile update(String deviceProfileName, ProfileResource profileResource) {
    DeviceProfile deviceProfile = deviceProfileClient.deviceProfileForName(deviceProfileName);
    List<ProfileResource> profileResourceList = deviceProfile.getResources();
    profileResourceList.add(profileResource);
    deviceProfile.setResources(profileResourceList);
    return deviceProfile;
  }
}
