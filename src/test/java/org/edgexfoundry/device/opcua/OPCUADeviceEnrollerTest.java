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
package org.edgexfoundry.device.opcua;

import static org.junit.Assert.*;
import org.edgexfoundry.device.opcua.adapter.metadata.DeviceEnroller;
import org.edgexfoundry.device.opcua.adapter.metadata.MetaDataType;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.DeviceProfile;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class OPCUADeviceEnrollerTest {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void test_addAddressableToMetaData() throws Exception {
    logger.info("[TEST] test_addAddressableToMetaData");
    DeviceEnroller enroller = new DeviceEnroller();
    Addressable addressable = null;
    Addressable newAddr = enroller.addAddressableToMetaData(addressable);
    assertEquals(newAddr, null);
    logger.info("[PASS] test_addAddressableToMetaData");
  }

  @Test
  public void test_addDeviceProfileToMetaData() throws Exception {
    logger.info("[TEST] test_addDeviceProfileToMetaData");
    DeviceEnroller enroller = new DeviceEnroller();
    DeviceProfile deviceProfile = null;
    DeviceProfile newProfile = enroller.addDeviceProfileToMetaData(deviceProfile);
    assertEquals(newProfile, null);
    logger.info("[PASS] test_addDeviceProfileToMetaData");
  }

  @Test
  public void test_addDeviceToMetaData() throws Exception {
    logger.info("[TEST] test_addDeviceToMetaData");
    DeviceEnroller enroller = new DeviceEnroller();
    Device device = null;
    Device newDevice = enroller.addDeviceToMetaData(device);
    assertEquals(newDevice, null);
    logger.info("[PASS] test_addDeviceToMetaData");
  }

  @Test
  public void test_updateDeviceProfileToMetaData() throws Exception {
    logger.info("[TEST] test_updateDeviceProfileToMetaData");
    DeviceEnroller enroller = new DeviceEnroller();
    DeviceProfile deviceProfile = null;
    boolean ret = enroller.updateDeviceProfileToMetaData(deviceProfile);
    assertEquals(ret, false);
    logger.info("[PASS] test_updateDeviceProfileToMetaData");
  }

  @Test
  public void test_cleanMetaData() throws Exception {
    logger.info("[TEST] test_cleanMetaData");
    DeviceEnroller enroller = new DeviceEnroller();
    MetaDataType type = MetaDataType.DEVICE;
    enroller.cleanMetaData(type);

    type = MetaDataType.DEVICE_PROFILE;
    enroller.cleanMetaData(type);

    type = MetaDataType.ADDRESSABLE;
    enroller.cleanMetaData(type);

    type = MetaDataType.ALL;
    enroller.cleanMetaData(type);
    logger.info("[PASS] test_cleanMetaData");
  }

  @Test
  public void test_cleanCoreData() throws Exception {
    logger.info("[TEST] test_cleanCoreData");
    DeviceEnroller enroller = new DeviceEnroller();
    enroller.cleanCoreData();
    logger.info("[PASS] test_cleanCoreData");
  }
}
