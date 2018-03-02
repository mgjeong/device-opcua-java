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

import org.edgexfoundry.controller.AddressableClient;
import org.edgexfoundry.controller.DeviceProfileClient;
import org.edgexfoundry.controller.DeviceServiceClient;
import org.edgexfoundry.domain.meta.AdminState;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.OperatingState;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

@ImportResource("spring-config.xml")
@Component
public class DeviceGenerator {
  private final static EdgeXLogger logger =
      EdgeXLoggerFactory.getEdgeXLogger(DeviceGenerator.class);

  // service name
  @Value("${service.name}")
  private static String serviceName;

  @Autowired
  private DeviceServiceClient deviceServiceClient;

  @Autowired
  private AddressableClient addressableClient;

  @Autowired
  private DeviceProfileClient deviceProfileClient;

  /**
   * construct AddressableGenerator <br>
   */
  public DeviceGenerator() {}

  /**
   * generate Device <br>
   * Use {@link org.edgexfoundry.domain.meta.Device#Device()} to generate Device instance
   * 
   * @param name Device name
   * @return created Device
   */
  @SuppressWarnings("deprecation")
  public Device generate(String name) {
    if (name == null || name.isEmpty()) {
      return null;
    }

    Device device = new Device();
    if (device == null) {
      return null;
    }
    
    device.setAdminState(AdminState.unlocked);
    device.setDescription(OPCUADefaultMetaData.DESCRIPTION_DEVICE.getValue());
    device.setLastConnected(OPCUADefaultMetaData.DEFAULT_LAST_CONNECTED);
    device.setLastReported(OPCUADefaultMetaData.DEFAULT_LAST_REPORTED);
    device.setLocation(OPCUADefaultMetaData.LOCATION.getValue());
    device.setName(name);
    device.setOperatingState(OperatingState.enabled);
    device.setOrigin(OPCUADefaultMetaData.DEFAULT_ORIGIN);
    if (deviceProfileClient != null) {
      device.setProfile(deviceProfileClient.deviceProfileForName(name));
    }
    String[] labels =
        {OPCUADefaultMetaData.LABEL1.getValue(), OPCUADefaultMetaData.LABEL2.getValue()};
    device.setLabels(labels);

    try {
      device.setAddressable(addressableClient.addressableForName(name));
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      serviceName = OPCUADefaultMetaData.DEVICESERVICE_NAME.getValue();
      device.setService(deviceServiceClient.deviceServiceForName(serviceName));
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    return device;
  }

}
