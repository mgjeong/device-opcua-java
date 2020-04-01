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
import java.util.ArrayList;
import java.util.List;
import org.edgexfoundry.device.opcua.adapter.metadata.AddressableGenerator;
import org.edgexfoundry.device.opcua.adapter.metadata.CommandGenerator;
import org.edgexfoundry.device.opcua.adapter.metadata.DeviceEnroller;
import org.edgexfoundry.device.opcua.adapter.metadata.DeviceGenerator;
import org.edgexfoundry.device.opcua.adapter.metadata.DeviceObjectGenerator;
import org.edgexfoundry.device.opcua.adapter.metadata.DeviceProfileGenerator;
import org.edgexfoundry.device.opcua.adapter.metadata.OPCUADefaultMetaData;
import org.edgexfoundry.device.opcua.adapter.metadata.OPCUAMetadataGenerateManager;
import org.edgexfoundry.device.opcua.adapter.metadata.ProfileResourceGenerator;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Command;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.DeviceObject;
import org.edgexfoundry.domain.meta.DeviceProfile;
import org.edgexfoundry.domain.meta.ProfileResource;
import org.edgexfoundry.domain.meta.ResourceOperation;
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

  @Test
  public void test_device_generate_without_deviceProfileClient() throws Exception {
    logger.info("[TEST] test_device_generate_without_deviceProfileClient");
    String name = "name";
    DeviceGenerator generator = new DeviceGenerator();
    Device device = generator.generate(name);
    assertNull(device);
    logger.info("[PASS] test_device_generate_without_deviceProfileClient");
  }

  @Test
  public void test_device_generate_without_name() throws Exception {
    logger.info("[TEST] test_device_generate_without_name");
    DeviceGenerator generator = new DeviceGenerator();
    Device device = generator.generate(null);
    assertNull(device);
    logger.info("[PASS] test_device_generate_without_name");
  }

  @Test
  public void test_device_generate_empty_name() throws Exception {
    logger.info("[TEST] test_device_generate_empty_name");
    DeviceGenerator generator = new DeviceGenerator();
    Device device = generator.generate("");
    assertNull(device);
    logger.info("[PASS] test_device_generate_empty_name");
  }

  @Test
  public void test_deviceobject_generate() throws Exception {
    logger.info("[TEST] test_deviceobject_generate");
    String name = "name";
    String value = "value";
    DeviceObject deviceobject = DeviceObjectGenerator.generate(name, value);
    assertNotNull(deviceobject);
    logger.info("[PASS] test_deviceobject_generate");
  }

  @Test
  public void test_deviceobject_generate_without_name() throws Exception {
    logger.info("[TEST] test_deviceobject_generate_without_name");
    String value = "value";
    DeviceObject deviceobject = DeviceObjectGenerator.generate(null, value);
    assertNull(deviceobject);
    logger.info("[PASS] test_deviceobject_generate_without_name");
  }

  @Test
  public void test_deviceobject_generate_empty_name() throws Exception {
    logger.info("[TEST] test_deviceobject_generate_empty_name");
    String value = "value";
    DeviceObject deviceobject = DeviceObjectGenerator.generate("", value);
    assertNull(deviceobject);
    logger.info("[PASS] test_deviceobject_generate_empty_name");
  }

  @Test
  public void test_deviceProfile_generate() throws Exception {
    logger.info("[TEST] test_deviceProfile_generate");
    DeviceProfileGenerator generator = new DeviceProfileGenerator();
    String name = "name";
    DeviceProfile profile = generator.generate(name, null, null, null);
    assertNotNull(profile);
    logger.info("[PASS] test_deviceProfile_generate");
  }

  @Test
  public void test_deviceProfile_generate_with_name() throws Exception {
    logger.info("[TEST] test_deviceProfile_generate");
    DeviceProfileGenerator generator = new DeviceProfileGenerator();
    DeviceProfile profile = generator.generate(null, null, null, null);
    assertNull(profile);
    logger.info("[PASS] test_deviceProfile_generate");
  }

  @Test
  public void test_deviceProfile_generate_without_name() throws Exception {
    logger.info("[TEST] test_deviceProfile_generate");
    DeviceProfileGenerator generator = new DeviceProfileGenerator();
    DeviceProfile profile = generator.generate(null, null, null, null);
    assertNull(profile);
    logger.info("[PASS] test_deviceProfile_generate");
  }

  @Test
  public void test_deviceProfile_update_with_command() throws Exception {
    logger.info("[TEST] test_deviceProfile_update_with_profileResource");
    DeviceProfileGenerator generator = new DeviceProfileGenerator();
    String name = "name";
    String value = "value";
    Command command = CommandGenerator.generate(name, value);
    DeviceProfile profile = generator.update(name, command);
    assertNull(profile);
    logger.info("[PASS] test_deviceProfile_update_with_profileResource");
  }

  @Test
  public void test_deviceProfile_update_without_command() throws Exception {
    logger.info("[TEST] test_deviceProfile_update_without_command");
    DeviceProfileGenerator generator = new DeviceProfileGenerator();
    String name = "name";
    Command command = null;
    DeviceProfile profile = generator.update(name, command);
    assertNull(profile);
    logger.info("[PASS] test_deviceProfile_update_without_command");
  }

  @Test
  public void test_deviceProfile_update_with_deviceObject() throws Exception {
    logger.info("[TEST] test_deviceProfile_update_with_deviceObject");
    DeviceProfileGenerator generator = new DeviceProfileGenerator();
    String name = "name";
    String value = "value";
    DeviceObject deviceobject = DeviceObjectGenerator.generate(name, value);
    DeviceProfile profile = generator.update(name, deviceobject);
    assertNull(profile);
    logger.info("[PASS] test_deviceProfile_update_with_deviceObject");
  }

  @Test
  public void test_deviceProfile_update_without_deviceObject() throws Exception {
    logger.info("[TEST] test_deviceProfile_update_without_deviceObject");
    DeviceProfileGenerator generator = new DeviceProfileGenerator();
    String name = "name";
    DeviceObject deviceobject = null;
    DeviceProfile profile = generator.update(name, deviceobject);
    assertNull(profile);
    logger.info("[PASS] test_deviceProfile_update_without_deviceObject");
  }

  @Test
  public void test_deviceProfile_update_without_profileResource() throws Exception {
    logger.info("[TEST] test_deviceProfile_update_without_profileResource");
    DeviceProfileGenerator generator = new DeviceProfileGenerator();
    String name = "name";
    ProfileResource resource = null;
    DeviceProfile profile = generator.update(name, resource);
    assertNull(profile);
    logger.info("[PASS] test_deviceProfile_update_without_profileResource");
  }

  // ResourceOperation
  @Test
  public void test_resourceOperation_update_with_params() throws Exception {
    logger.info("[TEST] test_resourceOperation_update_with_params");
    String name = "name";
    ResourceOperation operation = ProfileResourceGenerator.createGetOperation(name, "read", 1);
    List<ResourceOperation> getList = new ArrayList<ResourceOperation>();
    getList.add(operation);

    operation = ProfileResourceGenerator.createGetOperation(name, "write", 1);
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    setList.add(operation);

    ProfileResource resource = ProfileResourceGenerator.generate(name, getList, setList);
    assertNotNull(resource);
    logger.info("[PASS] test_resourceOperation_update_with_params");
  }

  @Test
  public void test_resourceOperation_update_without_name() throws Exception {
    logger.info("[TEST] test_resourceOperation_update_without_name");
    ProfileResource resource = ProfileResourceGenerator.generate(null, null, null);
    assertNull(resource);
    logger.info("[PASS] test_resourceOperation_update_without_name");
  }

  @Test
  public void test_resourceOperation_update_without_lists() throws Exception {
    logger.info("[TEST] test_resourceOperation_update_without_lists");
    String name = "name";
    ProfileResource resource = ProfileResourceGenerator.generate(name, null, null);
    assertNotNull(resource);
    logger.info("[PASS] test_resourceOperation_update_without_lists");
  }

  @Test
  public void test_resourceOperation_createGetOperation_with_params() throws Exception {
    logger.info("[TEST] test_resourceOperation_createGetOperation_with_params");
    String name = "name";
    ResourceOperation resource = ProfileResourceGenerator.createGetOperation(name, "read", 1);
    assertNotNull(resource);
    logger.info("[PASS] test_resourceOperation_createGetOperation_with_params");
  }

  @Test
  public void test_resourceOperation_createGetOperation_without_object() throws Exception {
    logger.info("[TEST] test_resourceOperation_createGetOperation_without_object");
    ResourceOperation resource = ProfileResourceGenerator.createGetOperation(null, "read", 1);
    assertNull(resource);
    logger.info("[PASS] test_resourceOperation_createGetOperation_without_object");
  }

  @Test
  public void test_resourceOperation_createGetOperation_with_empty_object() throws Exception {
    logger.info("[TEST] test_resourceOperation_createGetOperation_with_empty_object");
    ResourceOperation resource = ProfileResourceGenerator.createGetOperation("", "read", 1);
    assertNull(resource);
    logger.info("[PASS] test_resourceOperation_createGetOperation_with_empty_object");
  }

  @Test
  public void test_resourceOperation_createPutOperation_with_params() throws Exception {
    logger.info("[TEST] test_resourceOperation_createPutOperation_with_params");
    String name = "name";
    ResourceOperation resource = ProfileResourceGenerator.createPutOperation(name, "write", 1);
    assertNotNull(resource);
    logger.info("[PASS] test_resourceOperation_createPutOperation_with_params");
  }

  @Test
  public void test_resourceOperation_createPutOperation_without_object() throws Exception {
    logger.info("[TEST] test_resourceOperation_createPutOperation_without_object");
    ResourceOperation resource = ProfileResourceGenerator.createPutOperation(null, "write", 1);
    assertNull(resource);
    logger.info("[PASS] test_resourceOperation_createPutOperation_without_object");
  }

  @Test
  public void test_resourceOperation_createPutOperation_with_empty_object() throws Exception {
    logger.info("[TEST] test_resourceOperation_createPutOperation_with_empty_object");
    ResourceOperation resource = ProfileResourceGenerator.createPutOperation("", "write", 1);
    assertNull(resource);
    logger.info("[PASS] test_resourceOperation_createPutOperation_with_empty_object");
  }

  @Test
  public void test_metadataGenerator_initMetadata_without_spring_instaces() throws Exception {
    logger.info("[TEST] test_metadataGenerator_initMetadata_without_spring_instaces");
    OPCUAMetadataGenerateManager manager = new OPCUAMetadataGenerateManager();
    String name = OPCUADefaultMetaData.DEVICE_NAME.getValue().replaceAll(
        OPCUADefaultMetaData.BEFORE_REPLACE_WORD, OPCUADefaultMetaData.AFTER_REPLACE_WORD);
    manager.initMetaData(name);
    logger.info("[PASS] test_metadataGenerator_initMetadata_without_spring_instaces");
  }

  @Test
  public void test_metadataGenerator_updateMetadata_without_spring_instaces() throws Exception {
    logger.info("[TEST] test_metadataGenerator_updateMetadata_without_spring_instaces");
    OPCUAMetadataGenerateManager manager = new OPCUAMetadataGenerateManager();
    String deviceProfileName = "name";
    manager.updateMetaData(deviceProfileName);
    logger.info("[PASS] test_metadataGenerator_updateMetadata_without_spring_instaces");
  }

  @Test
  public void test_metadataGenerator_updateMetadata_without_name() throws Exception {
    logger.info("[TEST] test_metadataGenerator_updateMetadata_without_name");
    OPCUAMetadataGenerateManager manager = new OPCUAMetadataGenerateManager();
    manager.updateMetaData(null);
    logger.info("[PASS] test_metadataGenerator_updateMetadata_without_name");
  }

  @Test
  public void test_metadataGenerator_updateMetadata_with_empty_name() throws Exception {
    logger.info("[TEST] test_metadataGenerator_updateMetadata_with_empty_name");
    OPCUAMetadataGenerateManager manager = new OPCUAMetadataGenerateManager();
    manager.updateMetaData("");
    logger.info("[PASS] test_metadataGenerator_updateMetadata_with_empty_name");
  }
  
  @Test
  public void test_metadataGenerator_updateAttributeService() throws Exception {
    logger.info("[TEST] test_metadataGenerator_updateAttributeService");
    OPCUAMetadataGenerateManager manager = new OPCUAMetadataGenerateManager();
    ArrayList<String> list = new ArrayList<String>();
    list.add("list1");
    list.add("list2");
    list.add("list3");
    manager.updateAttributeService("name", "read", list); 
    logger.info("[PASS] test_metadataGenerator_updateAttributeService");
  }
  
  @Test
  public void test_metadataGenerator_updateAttributeServiceWithoutParam() throws Exception {
    logger.info("[TEST] test_metadataGenerator_updateAttributeServiceWithoutParam");
    OPCUAMetadataGenerateManager manager = new OPCUAMetadataGenerateManager();
    manager.updateAttributeService("name", "read", null); 
    logger.info("[PASS] test_metadataGenerator_updateAttributeServiceWithoutParam");
  }
  
  @Test
  public void test_metadataGenerator_updateMethodService() throws Exception {
    logger.info("[TEST] test_metadataGenerator_updateMethodService");
    OPCUAMetadataGenerateManager manager = new OPCUAMetadataGenerateManager();
    ArrayList<String> list = new ArrayList<String>();
    list.add("list1");
    list.add("list2");
    list.add("list3");
    manager.updateMethodService("name", "read", list); 
    logger.info("[PASS] test_metadataGenerator_updateMethodService");
  }
  
  @Test
  public void test_metadataGenerator_updateMethodServiceWithoutParam() throws Exception {
    logger.info("[TEST] test_metadataGenerator_updateMethodServiceWithoutParam");
    OPCUAMetadataGenerateManager manager = new OPCUAMetadataGenerateManager();
    manager.updateMethodService("name", "read", null); 
    logger.info("[PASS] test_metadataGenerator_updateMethodServiceWithoutParam");
  }
  
  
  @Test
  public void test_initAddressable() throws Exception {
    logger.info("[TEST] test_initAddressable");
    OPCUAMetadataGenerateManager manager = new OPCUAMetadataGenerateManager();
    manager.initAddressable("address");
    logger.info("[PASS] test_initAddressable");
  }
}
