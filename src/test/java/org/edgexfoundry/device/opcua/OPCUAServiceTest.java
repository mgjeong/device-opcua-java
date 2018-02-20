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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

public class OPCUAServiceTest {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  public static ConfigurableApplicationContext ctx;

  @Before
  public void start() {
    logger.info("start");
  }

  @After
  public void stop() throws Exception {
    logger.info("stop");
  }

  @Test
  public void testOPCUAdapter() throws Exception {
    logger.info("[TEST] testOPCUAdapter");
    OPCUAAdapterTest opcuaAdapter = new OPCUAAdapterTest();
    opcuaAdapter.test_startAdapter();
    opcuaAdapter.test_startAdapter_without_param();
    logger.info("[PASS] testOPCUAdapter");
  }

  @Test
  public void testOPCUAMessageHandler() throws Exception {
    logger.info("[TEST] testOPCUAMessageHandler");
    OPCUAMessageHandlerTest messageHandler = new OPCUAMessageHandlerTest();
    messageHandler.test_convertEdgeDevicetoEdgeElement_with_EdgeDevice();
    messageHandler.test_convertEdgeDevicetoEdgeElement_without_EdgeDevice();
    messageHandler.test_convertEdgeMessagetoEdgeElement_without_EdgeMessage();
    messageHandler.test_convertEdgeMessagetoEdgeElement_with_read_EdgeMessage();
    messageHandler.test_convertEdgeMessagetoEdgeElement_with_read_responses();
    messageHandler.test_convertEdgeMessagetoEdgeElement_with_write_EdgeMessage();
    messageHandler.test_convertEdgeMessagetoEdgeElement_with_sub_EdgeMessage();
    messageHandler.test_convertEdgeMessagetoEdgeElement_with_method_EdgeMessage();
    messageHandler.test_convertEdgeMessagetoEdgeElement_with_getendpoint_EdgeMessage();
    messageHandler.test_convertEdgeElementToEdgeMessage_getEndpoint();
    messageHandler.test_convertEdgeElementToEdgeMessage_group_read();
    messageHandler.test_convertEdgeElementToEdgeMessage_group_write();
    messageHandler.test_convertEdgeElementToEdgeMessage_invalid_operation();
    messageHandler.test_convertEdgeElementToEdgeMessage_method();
    messageHandler.test_convertEdgeElementToEdgeMessage_read();
    messageHandler.test_convertEdgeElementToEdgeMessage_start();
    messageHandler.test_convertEdgeElementToEdgeMessage_stop();
    messageHandler.test_convertEdgeElementToEdgeMessage_write();
    messageHandler.test_convertEdgeElementToEdgeMessage_sub();
    messageHandler.test_convertEdgeElementToEdgeMessage_write();
    messageHandler.test_getEndpointUrifromAddressable_with_Addressable();
    messageHandler.test_getEndpointUrifromAddressable_without_Addressable();
    messageHandler.test_getResponseElementForStart_with_status();
    messageHandler.test_getResponseElementForStart_without_status();
    messageHandler.test_getResponseElementForStop_with_status();
    messageHandler.test_getResponseElementForStop_without_status();
    messageHandler.test_sendMessage_with_EdgeMessage();
    messageHandler.test_sendMessage_without_EdgeMessage();
    logger.info("[PASS] testOPCUAMessageHandler");
  }

  @Test
  public void testEZMQAdapter() throws Exception {
    logger.info("[TEST] testEZMQAdapter");
    EZMQAdapterTest ezmqAdapter = new EZMQAdapterTest();
    ezmqAdapter.test_publish_with_EdgeMessage();
    ezmqAdapter.test_publish_without_EdgeMessage();
    ezmqAdapter.test_publisher_publishEvent();
    ezmqAdapter.test_publisher_publishEvent_with_topic();
    ezmqAdapter.test_publisher_publishEvent_with_topics();
    logger.info("[PASS] testEZMQAdapter");
  }

  @Test
  public void testOPCUADeviceEnroller() throws Exception {
    logger.info("[TEST] testOPCUADeviceEnroller");
    OPCUADeviceEnrollerTest enroller = new OPCUADeviceEnrollerTest();
    logger.info("[PASS] testOPCUADeviceEnroller");
  }


  @Test
  public void testOPCUAEventGenerator() throws Exception {
    logger.info("[TEST] testOPCUAEventGenerator");
    OPCUAEventGeneratorTest generator = new OPCUAEventGeneratorTest();
    generator.test_generate();
    generator.test_generate_without_deviceName();
    generator.test_generate_without_value();
    generator.test_generate_with_empty_deviceName();
    generator.test_generate_with_empty_value();
    logger.info("[PASS] testOPCUAEventGenerator");
  }

  @Test
  public void testOPCUAMetaDataGenerator() throws Exception {
    logger.info("[TEST] testOPCUAMetaDataGenerator");
    OPCUAMetaDataGeneratorTest generator = new OPCUAMetaDataGeneratorTest();
    generator.test_addressable_generate();
    generator.test_addressable_generate_without_name();
    generator.test_addressable_generate_empty_name();
    generator.test_command_generate();
    generator.test_command_generate_without_name();
    generator.test_command_generate_empty_name();
    generator.test_device_generate_without_deviceProfileClient();
    generator.test_device_generate_without_name();
    generator.test_device_generate_empty_name();
    generator.test_deviceobject_generate();
    generator.test_deviceobject_generate_without_name();
    generator.test_deviceobject_generate_empty_name();
    generator.test_deviceProfile_generate();
    generator.test_deviceProfile_generate_with_name();
    generator.test_deviceProfile_generate_without_name();
    generator.test_deviceProfile_update_with_command();
    generator.test_deviceProfile_update_without_command();
    generator.test_deviceProfile_update_with_deviceObject();
    generator.test_deviceProfile_update_without_deviceObject();
    generator.test_deviceProfile_update_without_profileResource();
    generator.test_resourceOperation_update_with_params();
    generator.test_resourceOperation_update_without_name();
    generator.test_resourceOperation_update_without_lists();
    generator.test_resourceOperation_createGetOperation_with_params();
    generator.test_resourceOperation_createGetOperation_without_object();
    generator.test_resourceOperation_createGetOperation_with_empty_object();
    generator.test_resourceOperation_createPutOperation_with_params();
    generator.test_resourceOperation_createPutOperation_without_object();
    generator.test_resourceOperation_createPutOperation_with_empty_object();
    generator.test_metadataGenerator_initMetadata_without_spring_instaces();
    generator.test_metadataGenerator_updateMetadata_without_spring_instaces();
    generator.test_metadataGenerator_updateMetadata_without_name();
    generator.test_metadataGenerator_updateMetadata_with_empty_name();
    logger.info("[PASS] testOPCUAMetaDataGenerator");
  }

  @Test
  public void testOPCUAMetaDataManager() throws Exception {
    logger.info("[TEST] testOPCUAMetaDataManager");
    OPCUAMetaDataManagerTest generator = new OPCUAMetaDataManagerTest();
    generator.test_DeviceObjectAttributeInfo();
    generator.test_DeviceObjectAttributeInfo_without_key();
    generator.test_MetaDataTyp();
    generator.test_OPCUACommandIdentifier_code();
    generator.test_OPCUADefaultMetaData_code();
    generator.test_OPCUADefaultMetaData_value();
    logger.info("[PASS] testOPCUAMetaDataManager");
  }

  @Test
  public void testOPCUAValueDescriptorGenerator() throws Exception {
    logger.info("[TEST] testOPCUAValueDescriptorGenerator");
    OPCUAValueDescriptorGeneratorTest generator = new OPCUAValueDescriptorGeneratorTest();
    generator.test_generate();
    generator.test_generate_with_empty_deviceName();
    generator.test_generate_without_deviceName();
    logger.info("[PASS] testOPCUAValueDescriptorGenerator");
  }
}
