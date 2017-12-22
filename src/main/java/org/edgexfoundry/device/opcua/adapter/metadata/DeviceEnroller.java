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
  private DeviceStore deviceStore;

  private static final boolean enableDeleteEvent = false;

  /**
   * construct DeviceEnroller <br>
   */
  private DeviceEnroller() {}

  /**
   * Add Addressable to metadata <br>
   * Use {@link org.edgexfoundry.controller.AddressableClient#add(Addressable)} to add addressable
   * 
   * @param addressable added addressable
   * @return added addressable if success, otherwise null
   */
  public Addressable addAddressableToMetaData(Addressable addressable) {
    if (null == addressable || addressableClient == null) {
      logger.debug("addAddressableToMetaData has failed");
      return null;
    }

    Addressable retAddressable = null;
    try {
      String addressableId = addressableClient.add(addressable);
      logger.debug("Add addressable successfully msg: " + addressableId);
      retAddressable = addressableClient.addressable(addressableId);
    } catch (Exception e) {
      logger.debug("Could not set addressable to metadata msg: " + e.getMessage());
    }
    return retAddressable;
  }

  /**
   * Add DeviceProfile to metadata <br>
   * Use {@link org.edgexfoundry.controller.DeviceProfileClient#add(DeviceProfile)} to add DeviceProfile
   * 
   * @param deviceProfile added DeviceProfile
   * @return added deviceProfile if success, otherwise null
   */
  public DeviceProfile addDeviceProfileToMetaData(DeviceProfile deviceProfile) {
    if (null == deviceProfile || deviceProfileClient == null) {
      logger.debug("addDeviceProfileToMetaData has failed");
      return null;
    }

    DeviceProfile retDeviceProfile = null;
    try {
      String deviceProfileId = deviceProfileClient.add(deviceProfile);
      logger.debug("Add deviceProfile successfully msg: " + deviceProfileId);
      retDeviceProfile = deviceProfileClient.deviceProfile(deviceProfileId);
    } catch (Exception e) {
      logger.error("Could not add deviceProfile to metadata msg: " + e.getMessage());
    }
    return retDeviceProfile;
  }

  /**
   * Update DeviceProfile to metadata <br>
   * Use {@link org.edgexfoundry.controller.DeviceProfileClient#update(DeviceProfile)} to add DeviceProfile
   * 
   * @param deviceProfile updated DeviceProfile
   * @return true if success, otherwise null
   */
  public boolean updateDeviceProfileToMetaData(DeviceProfile deviceProfile) {
    if (null == deviceProfile || deviceProfileClient == null) {
      logger.debug("updateDeviceProfileToMetaData has failed");
      return false;
    }

    if (true == deviceProfileClient.update(deviceProfile)) {
      deviceStore.updateProfile(deviceProfile.getId());
    }
    return true;
  }

  /**
   * Add Device to metadata <br>
   * Use {@link org.edgexfoundry.controller.DeviceClient#add(Device)} to add Device
   * 
   * @param device added Device
   * @return added Device if success, otherwise null
   */
  public Device addDeviceToMetaData(Device device) {
    if (null == device || deviceClient == null) {
      logger.debug("addDeviceToMetaData has failed");
      return null;
    }

    Device retDevice = null;
    try {
      String deviceId = deviceClient.add(device);
      logger.debug("Add device successfully msg: " + deviceId);
      retDevice = deviceClient.device(deviceId);
      deviceStore.add(deviceId);
    } catch (Exception e) {
      logger.error("Could not add device to metadata msg: " + e.getMessage());
    }
    return retDevice;
  }

  // TODO
  // we can get events below 100. but can not set limits
  // I will modify it when I can set a limit.
  /**
   * Delete Event in coredata <br>
   * Use {@link org.edgexfoundry.controller.EventClient#delete(String)} to delete event
   */
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


  // TODO
  // we can get events below 100. but can not set limits
  // I will modify it when I can set a limit.
  /**
   * Delete ValueDescriptor in coredata <br>
   * Use {@link org.edgexfoundry.controller.ValueDescriptorClient#delete(String)} to delete ValueDescriptor
   */
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

  /**
   * Delete Device in metadata <br>
   * Use {@link org.edgexfoundry.controller.DeviceClient#delete(String)} to delete Device
   */
  private void deleteDevice() {
    for (Device device : deviceClient.devices()) {
      try {
        logger.debug("remove device from  metadata ret: " + deviceClient.delete(device.getId()));
      } catch (Exception e) {
        logger.debug("Could not remove deviceprofile from metadata msg: " + e.getMessage());
      }
    }
  }

  /**
   * Delete DeviceProfile in metadata <br>
   * Use {@link org.edgexfoundry.controller.DeviceProfileClient#delete(String)} to delete DeviceProfile
   */
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

  /**
   * Delete Addressable in metadata <br>
   * Use {@link org.edgexfoundry.controller.AddressableClient#delete(String)} to delete Addressable
   */
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
  }

  /**
   * Clean MetaData <br>
   * Use {@link #deleteDevice()} to delete Device in metadata
   * Use {@link #deleteDeviceProfile()} to delete DeviceProfile in metadata
   * Use {@link #deleteAddressable()} to delete Addressable in metadata
   * 
   * @param type metadata clean type {@link MetaDataType}
   */
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

  /**
   * Clean CoreData <br>
   * Use {@link #deleteEvent()} to delete Event in coredata
   * Use {@link #deleteValueDescriptor()} to delete ValueDescriptor in coredata
   */
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
