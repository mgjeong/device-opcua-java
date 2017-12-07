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
import org.edgexfoundry.device.opcua.adapter.metadata.AddressableGenerator;
import org.edgexfoundry.device.opcua.adapter.metadata.CommandGenerator;
import org.edgexfoundry.device.opcua.adapter.metadata.DeviceGenerator;
import org.edgexfoundry.device.opcua.adapter.metadata.DeviceObjectGenerator;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Command;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.DeviceObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OPCUAMetaDataGeneratorTest {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void test_addressable_generate() throws Exception {
    logger.info("[TEST] test_addressable_generate");
    String name = "name";
    Addressable addressable = AddressableGenerator.generate(name);
    assertNotNull(addressable);
    logger.info("[PASS] test_addressable_generate");
  }

  @Test
  public void test_addressable_generate_without_name() throws Exception {
    logger.info("[TEST] test_addressable_generate_without_name");
    Addressable addressable = AddressableGenerator.generate(null);
    assertNull(addressable);
    logger.info("[PASS] test_addressable_generate_without_name");
  }

  @Test
  public void test_addressable_generate_empty_name() throws Exception {
    logger.info("[TEST] test_addressable_generate_empty_name");
    Addressable addressable = AddressableGenerator.generate("");
    assertNull(addressable);
    logger.info("[PASS] test_addressable_generate_empty_name");
  }
  
  @Test
  public void test_command_generate() throws Exception {
    logger.info("[TEST] test_command_generate");
    String name = "name";
    String value = "value";
    Command command = CommandGenerator.generate(name, value);
    assertNotNull(command);
    logger.info("[PASS] test_command_generate");
  }

  @Test
  public void test_command_generate_without_name() throws Exception {
    logger.info("[TEST] test_command_generate_without_name");
    String value = "value";
    Command command = CommandGenerator.generate(null, value);
    assertNull(command);
    logger.info("[PASS] test_command_generate_without_name");
  }

  @Test
  public void test_command_generate_empty_name() throws Exception {
    logger.info("[TEST] test_command_generate_empty_name");
    String value = "value";
    Command command = CommandGenerator.generate("", value);
    assertNull(command);
    logger.info("[PASS] test_command_generate_empty_name");
  }
  
//  @Test
//  public void test_device_generate() throws Exception {
//    logger.info("[TEST] test_device_generate");
//    String name = "name";
//    DeviceGenerator generator = new DeviceGenerator();
//    Device device = generator.generate(name);
//    assertNotNull(device);
//    logger.info("[PASS] test_device_generate");
//  }
//
//  @Test
//  public void test_device_generate_without_name() throws Exception {
//    logger.info("[TEST] test_device_generate_without_name");
//    DeviceGenerator generator = new DeviceGenerator();
//    Device device = generator.generate(null);
//    assertNull(device);
//    logger.info("[PASS] test_device_generate_without_name");
//  }
//
//  @Test
//  public void test_device_generate_empty_name() throws Exception {
//    logger.info("[TEST] test_device_generate_empty_name");
//    DeviceGenerator generator = new DeviceGenerator();
//    Device device = generator.generate("");
//    assertNull(device);
//    logger.info("[PASS] test_device_generate_empty_name");
//  }
  
//  @Test
//  public void test_deviceobject_generate() throws Exception {
//    logger.info("[TEST] test_deviceobject_generate");
//    String name = "name";
//    String value = "value";
//    DeviceObject deviceobject = DeviceObjectGenerator.generate(name, value);
//    assertNotNull(deviceobject);
//    logger.info("[PASS] test_deviceobject_generate");
//  }
//
//  @Test
//  public void test_deviceobject_generate_without_name() throws Exception {
//    logger.info("[TEST] test_deviceobject_generate_without_name");
//    String value = "value";
//    DeviceObject deviceobject = DeviceObjectGenerator.generate(null, value);
//    assertNull(deviceobject);
//    logger.info("[PASS] test_deviceobject_generate_without_name");
//  }
//
//  @Test
//  public void test_deviceobject_generate_empty_name() throws Exception {
//    logger.info("[TEST] test_deviceobject_generate_empty_name");
//    String value = "value";
//    DeviceObject deviceobject = DeviceObjectGenerator.generate("", value);
//    assertNull(deviceobject);
//    logger.info("[PASS] test_deviceobject_generate_empty_name");
//  }
  
//  @Test
//  public void test_deviceProfile_generate() throws Exception {
//    logger.info("[TEST] test_deviceobject_generate");
//    String name = "name";
//    String value = "value";
//    DeviceObject deviceobject = DeviceProfileGenerator.generate(name, value);
//    assertNotNull(deviceobject);
//    logger.info("[PASS] test_deviceobject_generate");
//  }
//
//  @Test
//  public void test_deviceobject_generate_without_name() throws Exception {
//    logger.info("[TEST] test_deviceobject_generate_without_name");
//    String value = "value";
//    DeviceObject deviceobject = DeviceProfileGenerator.generate(null, value);
//    assertNull(deviceobject);
//    logger.info("[PASS] test_deviceobject_generate_without_name");
//  }
//
//  @Test
//  public void test_deviceobject_generate_empty_name() throws Exception {
//    logger.info("[TEST] test_deviceobject_generate_empty_name");
//    String value = "value";
//    DeviceObject deviceobject = DeviceProfileGenerator.generate("", value);
//    assertNull(deviceobject);
//    logger.info("[PASS] test_deviceobject_generate_empty_name");
//  }
  
  
}
