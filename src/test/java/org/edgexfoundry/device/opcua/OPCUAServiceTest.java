package org.edgexfoundry.device.opcua;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OPCUAServiceTest {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  
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
    messageHandler.test_getResponseElementForStart_with_status();
    messageHandler.test_getResponseElementForStart_without_status();
    messageHandler.test_getResponseElementForStop_with_status();
    messageHandler.test_getResponseElementForStop_without_status();
    messageHandler.test_convertEdgeMessagetoEdgeElement_without_EdgeMessage();
    messageHandler.test_convertEdgeMessagetoEdgeElement_with_read_EdgeMessage();
    messageHandler.test_convertEdgeMessagetoEdgeElement_with_write_EdgeMessage();
    messageHandler.test_convertEdgeMessagetoEdgeElement_with_sub_EdgeMessage();
    messageHandler.test_convertEdgeMessagetoEdgeElement_with_method_EdgeMessage();
    messageHandler.test_convertEdgeMessagetoEdgeElement_with_getendpoint_EdgeMessage();
    messageHandler.test_getEndpointUrifromAddressable_with_Addressable();
    messageHandler.test_getEndpointUrifromAddressable_without_Addressable();
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
}
