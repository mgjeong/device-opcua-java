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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.common.EdgeCommandType;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeRequest;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edgexfoundry.device.opcua.adapter.OPCUAAdapter;
import org.edgexfoundry.device.opcua.adapter.OPCUAMessageHandler;
import org.edgexfoundry.device.opcua.adapter.metadata.MetaDataType;
import org.edgexfoundry.device.opcua.adapter.metadata.OPCUADefaultMetaData;
import org.edgexfoundry.device.opcua.opcua.OPCUADriver.DriverCallback;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.Protocol;
import org.edgexfoundry.domain.meta.ResourceOperation;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OPCUAAdapterTest {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  DriverCallback callback = new DriverCallback() {

    @Override
    public void onReceive(Device device, ResourceOperation operation, String result) {
      // TODO Auto-generated method stub
    }

    @Override
    public void onDeleteCoreData() {
      // TODO Auto-generated method stub
    }

    @Override
    public void onDeleteMetaData(MetaDataType type) {
      // TODO Auto-generated method stub
    }

    @Override
    public void onInit() {
      // TODO Auto-generated method stub
    }
  };

  @Test
  public void test_startAdapter() throws Exception {
    logger.info("[TEST] test_startAdapter");
    OPCUAAdapter adapter = OPCUAAdapter.getInstance(callback);
    assertNotNull(adapter);
    logger.info("[PASS] test_startAdapter");
  }

  @Test
  public void test_startAdapter_without_param() throws Exception {
    logger.info("[TEST] test_startAdapter_without_param");
    OPCUAAdapter adapter = OPCUAAdapter.getInstance(null);
    assertNotNull(adapter);
    logger.info("[PASS] test_startAdapter_without_param");
  }
}
