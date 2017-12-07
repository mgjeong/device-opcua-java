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
import org.edgexfoundry.device.opcua.adapter.coredata.ValueDescriptorGenerator;
import org.edgexfoundry.domain.common.ValueDescriptor;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OPCUAValueDescriptorGeneratorTest {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void test_generate() throws Exception {
    logger.info("[TEST] test_generate");
    String deviceName = "deviceName";
    ValueDescriptor descriptor = ValueDescriptorGenerator.generate(deviceName);
    assertNotNull(descriptor);
    logger.info("[PASS] test_generate");
  }

  @Test
  public void test_generate_without_deviceName() throws Exception {
    logger.info("[TEST] test_generate_without_deviceName");
    ValueDescriptor descriptor = ValueDescriptorGenerator.generate(null);
    assertNull(descriptor);
    logger.info("[PASS] test_generate_without_deviceName");
  }

  @Test
  public void test_generate_with_empty_deviceName() throws Exception {
    logger.info("[TEST] test_generate_with_empty_deviceName");
    String deviceName = "";
    ValueDescriptor descriptor = ValueDescriptorGenerator.generate(deviceName);
    assertNull(descriptor);
    logger.info("[PASS] test_generate_with_empty_deviceName");
  }
}
