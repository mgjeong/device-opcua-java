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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.edge.protocol.opcua.api.client.EdgeResponse;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edgexfoundry.device.opcua.adapter.ezmq.EZMQAdapter;
import org.edgexfoundry.device.opcua.adapter.ezmq.Publisher;
import org.edgexfoundry.device.opcua.adapter.metadata.MetaDataType;
import org.edgexfoundry.device.opcua.opcua.OPCUADriver.DriverCallback;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.ResourceOperation;
import org.edgexfoundry.ezmq.domain.core.Event;
import org.edgexfoundry.ezmq.domain.core.Reading;
import org.edgexfoundry.ezmq.EZMQErrorCode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EZMQAdapterTest {
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
  public void test_publish_with_EdgeMessage() throws Exception {
    logger.info("[TEST] test_publish_with_EdgeMessage");

    List<EdgeResponse> responses = new ArrayList<EdgeResponse>();
    EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().setValueAlias("test_valueAlias").build();
    EdgeResponse res = new EdgeResponse.Builder(nodeInfo, EdgeOpcUaCommon.DEFAULT_REQUEST_ID)
        .setMessage(new EdgeVersatility.Builder("test_value").build()).build();
    responses.add(res);
    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue()).build();
    EdgeMessage data = new EdgeMessage.Builder(epInfo).setResponses(responses).build();
    EZMQAdapter.getInstance().publish(data);
    logger.info("[PASS] test_publish_with_EdgeMessage");
  }

  @Test
  public void test_publish_without_EdgeMessage() throws Exception {
    logger.info("[TEST] test_publish_with_EdgeMessage");
    EdgeMessage data = null;
    EZMQAdapter.getInstance().publish(data);
    logger.info("[PASS] test_publish_with_EdgeMessage");
  }

  @Test
  public void test_publisher_publishEvent() throws Exception {
    logger.info("[TEST] test_publisher_publishEvent");
    EZMQErrorCode code = Publisher.getInstance().startPublisher(0);
    assertEquals(EZMQErrorCode.EZMQ_OK, code);
    code = Publisher.getInstance().publishEvent(getTestEvent());
    assertEquals(EZMQErrorCode.EZMQ_OK, code);
    code = Publisher.getInstance().stopPublisher();
    assertEquals(EZMQErrorCode.EZMQ_OK, code);
    logger.info("[PASS] test_publisher_publishEvent");
  }

  @Test
  public void test_publisher_publishEvent_with_topic() throws Exception {
    logger.info("[TEST] test_publisher_publishEvent_with_topic");
    String topic = "topic";
    EZMQErrorCode code = Publisher.getInstance().startPublisher(0);
    assertEquals(EZMQErrorCode.EZMQ_OK, code);
    code = Publisher.getInstance().publishEvent(topic, getTestEvent());
    assertEquals(EZMQErrorCode.EZMQ_OK, code);
    code = Publisher.getInstance().stopPublisher();
    assertEquals(EZMQErrorCode.EZMQ_OK, code);
    logger.info("[PASS] test_publisher_publishEvent_with_topic");
  }

  @Test
  public void test_publisher_publishEvent_with_topics() throws Exception {
    logger.info("[TEST] test_publisher_publishEvent_with_topics");
    List<String> topics = new ArrayList<String>();
    topics.add("topic1");
    topics.add("topic2");
    EZMQErrorCode code = Publisher.getInstance().startPublisher(0);
    assertEquals(EZMQErrorCode.EZMQ_OK, code);
    code = Publisher.getInstance().publishEvent(topics, getTestEvent());
    assertEquals(EZMQErrorCode.EZMQ_OK, code);
    code = Publisher.getInstance().stopPublisher();
    assertEquals(EZMQErrorCode.EZMQ_OK, code);
    logger.info("[PASS] test_publisher_publishEvent_with_topics");
  }

  private Event getTestEvent() {
    List<Reading> readings = null;
    readings = new ArrayList<Reading>();
    Reading reading = new Reading();
    reading.setName("test_valueAlias");
    reading.setValue("test_value");
    reading.setCreated(0);
    reading.setDevice(EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue());
    reading.setModified(0);
    reading.setId("id1");
    reading.setOrigin(new Timestamp(System.currentTimeMillis()).getTime());
    reading.setPushed(new Timestamp(System.currentTimeMillis()).getTime());

    readings.add(reading);

    Event event = new Event(EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue(), readings);
    event.setCreated(0);
    event.setModified(0);
    event.setId("id1");
    event.markPushed(new Timestamp(System.currentTimeMillis()).getTime());
    event.setOrigin(new Timestamp(System.currentTimeMillis()).getTime());
    return event;
  }
}
