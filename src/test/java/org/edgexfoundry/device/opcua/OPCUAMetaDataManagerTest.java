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
import org.edgexfoundry.device.opcua.adapter.metadata.DeviceObjectAttributeInfo;
import org.edgexfoundry.device.opcua.adapter.metadata.MetaDataType;
import org.edgexfoundry.device.opcua.adapter.metadata.OPCUACommandIdentifier;
import org.edgexfoundry.device.opcua.adapter.metadata.OPCUADefaultMetaData;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OPCUAMetaDataManagerTest {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void test_MetaDataTyp() throws Exception {
    logger.info("[TEST] test_MetaDataTyp");
    for (MetaDataType type : MetaDataType.values()) {
      assertNotNull(type.getCode());
    }
    logger.info("[PASS] test_MetaDataTyp");
  }

  @Test
  public void test_OPCUADefaultMetaData_code() throws Exception {
    logger.info("[TEST] test_OPCUADefaultMetaData_code");
    for (OPCUADefaultMetaData data : OPCUADefaultMetaData.values()) {
      assertNotNull(data.getCode());
    }
    logger.info("[PASS] test_OPCUADefaultMetaData_code");
  }

  @Test
  public void test_OPCUACommandIdentifier_code() throws Exception {
    logger.info("[TEST] test_OPCUACommandIdentifier_code");
    for (OPCUACommandIdentifier id : OPCUACommandIdentifier.values()) {
      assertNotNull(id.getCode());
    }
    logger.info("[PASS] test_OPCUACommandIdentifier_code");
  }

  @Test
  public void test_OPCUADefaultMetaData_value() throws Exception {
    logger.info("[TEST] test_OPCUADefaultMetaData_value");
    for (OPCUADefaultMetaData data : OPCUADefaultMetaData.values()) {
      if (data == OPCUADefaultMetaData.LSB || data == OPCUADefaultMetaData.RESOURCE) {
        assertNull(data.getValue());
      } else {
        assertNotNull(data.getValue());
      }
    }
    logger.info("[PASS] test_OPCUADefaultMetaData_value");
  }

  @Test
  public void test_DeviceObjectAttributeInfo() throws Exception {
    logger.info("[TEST] test_DeviceObjectAttributeInfo");

    String providerKey = "key";
    String dataType = "type";
    DeviceObjectAttributeInfo attr =
        new DeviceObjectAttributeInfo.Builder(providerKey).setDataType(dataType).build();
    assertEquals(providerKey, attr.getProviderKey());
    assertEquals(dataType, attr.getDataType());
    logger.info("[PASS] test_DeviceObjectAttributeInfo");
  }

  @Test
  public void test_DeviceObjectAttributeInfo_without_key() throws Exception {
    logger.info("[TEST] test_DeviceObjectAttributeInfo_without_key");

    DeviceObjectAttributeInfo attr = new DeviceObjectAttributeInfo.Builder(null).build();
    assertNull(attr.getProviderKey());
    logger.info("[PASS] test_DeviceObjectAttributeInfo_without_key");
  }
}
