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

import java.util.List;
import org.edgexfoundry.controller.AddressableClient;
import org.edgexfoundry.controller.DeviceClient;
import org.edgexfoundry.controller.DeviceProfileClient;
import org.edgexfoundry.controller.EventClient;
import org.edgexfoundry.controller.ReadingClient;
import org.edgexfoundry.controller.ValueDescriptorClient;
import org.edgexfoundry.device.opcua.data.DeviceStore;
import org.edgexfoundry.domain.common.ValueDescriptor;
import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.DeviceProfile;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeviceEnroller {
  private final static EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(DeviceEnroller.class);

  @Autowired
  private DeviceClient deviceClient;

  @Autowired
  private DeviceProfileClient deviceProfileClient;

  @Autowired
  private AddressableClient addressableClient;

  @Autowired
  private ValueDescriptorClient valueDescriptorClient;

  @Autowired
  private EventClient eventClient;

  @Autowired
  ReadingClient readingClient;

  @Autowired
  private DeviceGenerator deviceGenerator;

  @Autowired
  private DeviceStore deviceStore;

  private static Addressable addressable = null;
  private static final boolean enableDeleteEvent = false;

  private DeviceEnroller() {}

  private Addressable addAddressableToMetaData() {
    if (addressable == null) {
      addressable = AddressableGenerator.generateAddressable();
      try {
        addressableClient.add(addressable);
      } catch (Exception e) {
        logger.debug("Could not set addressable to metadata msg: " + e.getMessage());
      }
    }
    return addressable;
  }

  private Addressable updateAddressableToMetaData() {
    if (addressable == null) {
      addressable = AddressableGenerator.updateAddressable();
      try {
        addressableClient.update(addressable);
      } catch (Exception e) {
        logger.debug("Could not update addressable to metadata msg: " + e.getMessage());
      }
    }
    return addressable;
  }

  private DeviceProfile addDeviceProfileToMetaData(String deviceInfoKey) {
    DeviceProfile profile = DeviceProfileGenerator.generateDeviceProfile(deviceInfoKey);
    try {
      logger.debug("Add deviceProfile successfully msg: " + deviceProfileClient.add(profile));
    } catch (Exception e) {
      logger.error("Could not add deviceProfile to metadata msg: " + e.getMessage());
    }
    return profile;
  }

  private Device addDeviceToMetaData(String deviceInfoKey) {
    Device device = deviceGenerator.generateDevice(deviceInfoKey);
    try {
      String deviceId = deviceClient.add(device);
      logger.debug("Add device successfully msg: " + deviceId);
      device = deviceClient.deviceForName(device.getName());
      deviceStore.add(deviceId);
    } catch (Exception e) {
      logger.error("Could not add device to metadata msg: " + e.getMessage());
    }
    return device;
  }


  // TODO
  // we can get events below 100. but can not set limits
  // I will modify it when I can set a limit.
  @SuppressWarnings("unused")
  private void deleteEvent() {
    if (enableDeleteEvent == true) {
      for (Event event : eventClient.events()) {
        try {
          logger.debug("delete event successfully msg: " + eventClient.delete(event.getId()));
        } catch (Exception e) {
          logger.error("Could not delete event to coredata msg: " + e.getMessage());
        }
      }
    }
  }

  @SuppressWarnings("unused")
  private void deleteValueDescriptor() {
    if (enableDeleteEvent == true) {
      for (ValueDescriptor valueDescriptor : valueDescriptorClient.valueDescriptors()) {
        try {
          logger.debug("delete vauleDescriptor successfully msg: "
              + valueDescriptorClient.delete(valueDescriptor.getId()));
        } catch (Exception e) {
          logger.error("Could not delete vauleDescriptor to coredata msg: " + e.getMessage());
        }
      }
    }
  }

  private void deleteDevice() {
    for (Device device : deviceClient.devices()) {
      try {
        logger.debug("remove device from  metadata ret: " + deviceClient.delete(device.getId()));
      } catch (Exception e) {
        logger.debug("Could not remove deviceprofile from metadata msg: " + e.getMessage());
      }
    }
  }

  private void deleteDeviceProfile() {
    for (DeviceProfile deviceProfile : deviceProfileClient.deviceProfiles()) {
      try {
        logger.debug("remove profile from  metadata ret: "
            + deviceProfileClient.delete(deviceProfile.getId()));
      } catch (Exception e) {
        logger.debug("Could not remove profile from metadata msg: " + e.getMessage());
      }
    }
  }

  private void deleteAddressable() {
    List<Addressable> addressableList = null;
    addressableList = addressableClient.addressables();
    for (Addressable addressable : addressableList) {
      try {
        logger.debug("remove addressable from  metadata ret: "
            + addressableClient.delete(addressable.getId()));
      } catch (Exception e) {
        logger.debug("Could not remove addressable from metadata msg: " + e.getMessage());
      }
    }
    addressable = null;
  }

  public void initialize() {
    long start = System.currentTimeMillis();
    logger.info("Device Service Initializing Start ");

    configureMetaData();

    long end = System.currentTimeMillis();
    logger.info("Device Service Initialize Time is " + (end - start) / 1000.0 + "sec");
    // logger.info("" + attributeProviderKeyList.size());
  }

  private void configureMetaData() {
    try {
      addAddressableToMetaData();
      updateAddressableToMetaData();
      addDeviceProfileToMetaData("opcua");
      addDeviceToMetaData("opcua");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void cleanMetaData(MetaDataType type) {
    if (MetaDataType.DEVICE == type) {
      deleteDevice();
    } else if (MetaDataType.DEVICE_PROFILE == type) {
      deleteDeviceProfile();
    } else if (MetaDataType.ADDRESSABLE == type) {
      deleteAddressable();
    } else if (MetaDataType.ALL == type) {
      deleteDevice();
      deleteDeviceProfile();
      deleteAddressable();
    }
  }

  public void cleanCoreData() {
    // TODO
    // ValueDescriptor can be removed in coredata after remove all events in coredata.
    // it can be cause big confusion to configure device service.
    // since the event of other device service will be all removed
    // when we try to remove all events.
    deleteEvent();
    deleteValueDescriptor();
  }
}
