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

package org.edge.protocol.opcua.testcase;

import java.util.HashMap;
import java.util.List;
import org.eclipse.milo.opcua.sdk.core.WriteMask;
import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.ProtocolManager.DiscoveryCallback;
import org.edge.protocol.opcua.api.ProtocolManager.ReceivedMessageCallback;
import org.edge.protocol.opcua.api.ProtocolManager.StatusCallback;
import org.edge.protocol.opcua.api.common.EdgeBrowseResult;
import org.edge.protocol.opcua.api.common.EdgeCommandType;
import org.edge.protocol.opcua.api.common.EdgeConfigure;
import org.edge.protocol.opcua.api.common.EdgeDevice;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeEndpointConfig;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeNodeId;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeRequest;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.api.server.EdgeArgumentType;
import org.edge.protocol.opcua.api.server.EdgeNodeItem;
import org.edge.protocol.opcua.api.server.EdgeNodeType;
import org.edge.protocol.opcua.api.server.EdgeReference;
import org.edge.protocol.opcua.example.EdgeSampleCommon;
import org.edge.protocol.opcua.example.EdgeTestMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeDeviceServiceOpcUaTest {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private HashMap<String, String> serverInfo = new HashMap<String, String>();
  private static String endpointUri = EdgeOpcUaCommon.DEFAULT_SERVER_URI.getValue()
      + EdgeOpcUaCommon.DEFAULT_SERVER_NAME.getValue();

  public void init() throws Exception {
    EdgeConfigure configure = new EdgeConfigure.Builder().setRecvCallback(receiver)
        .setStatusCallback(statusCallback).setDiscoveryCallback(discoveryCallback).build();

    ProtocolManager protocolManager = ProtocolManager.getProtocolManagerInstance();
    protocolManager.configure(configure);
  }

  private void initProvider() throws Exception {
    try {
      ProtocolManager.getProtocolManagerInstance().createNamespace(
          EdgeOpcUaCommon.DEFAULT_NAMESPACE.getValue(),
          EdgeOpcUaCommon.DEFAULT_ROOT_NODE_INFO.getValue(),
          EdgeOpcUaCommon.DEFAULT_ROOT_NODE_INFO.getValue(),
          EdgeOpcUaCommon.DEFAULT_ROOT_NODE_INFO.getValue());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    String namespace = EdgeOpcUaCommon.DEFAULT_NAMESPACE.getValue();
    EdgeNodeId rootNodeId = ProtocolManager.getProtocolManagerInstance()
        .getNodes(EdgeOpcUaCommon.DEFAULT_ROOT_NODE_INFO.getValue()).get(0);

    EdgeNodeItem item = new EdgeNodeItem.Builder(EdgeSampleCommon.SERVER_NODE_LINE1.getValue())
        .setEdgeNodeType(EdgeIdentifier.MILTI_FOLDER_NODE_TYPE)
        .setVariableItemSet(EdgeSampleCommon.LINE_NODES).setSourceNode(rootNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_ServerInfo_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.SERVER_NODE_LONG_PATH.getValue())
        .setEdgeNodeType(EdgeIdentifier.MILTI_FOLDER_NODE_TYPE)
        .setVariableItemSet(EdgeSampleCommon.LINE_NODES).setSourceNode(rootNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.SERVER_NODE_ROBOT.getValue())
        .setEdgeNodeType(EdgeIdentifier.VARIABLE_NODE)
        .setVariableItemSet(EdgeSampleCommon.ROBOT_NODES).setSourceNode(rootNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.SERVER_NODE_GLASS.getValue())
        .setEdgeNodeType(EdgeIdentifier.SINGLE_FOLDER_NODE_TYPE)
        .setAccessLevel(EdgeIdentifier.getAccessLevel(EdgeIdentifier.READ_WRITE))
        .setUserAccessLevel(EdgeIdentifier.getAccessLevel(EdgeIdentifier.READ_WRITE))
        .setWriteMask(WriteMask.Historizing.getValue() + WriteMask.ValueRank.getValue())
        .setUserWriteMask(WriteMask.Historizing.getValue() + WriteMask.ValueRank.getValue())
        .setVariableItemSet(EdgeSampleCommon.GLASS_VARIABLE_NODES).setSourceNode(rootNodeId)
        .build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.SERVER_NODE_ARRAY.getValue())
        .setEdgeNodeType(EdgeIdentifier.ARRAY_NODE)
        .setAccessLevel(EdgeIdentifier.getAccessLevel(EdgeIdentifier.READ_WRITE))
        .setWriteMask(WriteMask.Historizing.getValue() + WriteMask.ValueRank.getValue())
        .setVariableItemSet(EdgeSampleCommon.ARRAY_NODES).setSourceNode(rootNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    /*
     * AccessLevel : Write Only UserAccessLevel : Write Only
     */
    item = new EdgeNodeItem.Builder(EdgeSampleCommon.SERVER_NODE_WRITE_ONLY.getValue())
        .setEdgeNodeType(EdgeIdentifier.SINGLE_FOLDER_NODE_TYPE)
        .setAccessLevel(EdgeIdentifier.getAccessLevel(EdgeIdentifier.WRITE))
        .setUserAccessLevel(EdgeIdentifier.getAccessLevel(EdgeIdentifier.WRITE))
        .setWriteMask(WriteMask.Historizing.getValue() + WriteMask.ValueRank.getValue())
        .setUserWriteMask(WriteMask.Historizing.getValue() + WriteMask.ValueRank.getValue())
        .setVariableItemSet(EdgeSampleCommon.WRITE_ONLY_NODES).setSourceNode(rootNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    /*
     * AccessLevel : Read Only UserAccessLevel : Read Only
     */
    item = new EdgeNodeItem.Builder(EdgeSampleCommon.SERVER_NODE_READ_ONLY.getValue())
        .setEdgeNodeType(EdgeIdentifier.SINGLE_FOLDER_NODE_TYPE)
        .setAccessLevel(EdgeIdentifier.getAccessLevel(EdgeIdentifier.READ))
        .setUserAccessLevel(EdgeIdentifier.getAccessLevel(EdgeIdentifier.READ))
        .setVariableItemSet(EdgeSampleCommon.READ_ONLY_NODES).setSourceNode(rootNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    /*
     * AccessLevel : Read Only UserAccessLevel : NONE
     */
    item = new EdgeNodeItem.Builder(EdgeSampleCommon.SERVER_NODE_READ_NOT_USER.getValue())
        .setEdgeNodeType(EdgeIdentifier.SINGLE_FOLDER_NODE_TYPE)
        .setAccessLevel(EdgeIdentifier.getAccessLevel(EdgeIdentifier.READ))
        .setUserAccessLevel(EdgeIdentifier.getAccessLevel(EdgeIdentifier.NONE))
        .setVariableItemSet(EdgeSampleCommon.READ_ONLY_NODES).setSourceNode(rootNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    /*
     * AccessLevel : Write Only UserAccessLevel : NONE
     */
    item = new EdgeNodeItem.Builder(EdgeSampleCommon.SERVER_NODE_WRITE_NOT_USER.getValue())
        .setEdgeNodeType(EdgeIdentifier.SINGLE_FOLDER_NODE_TYPE)
        .setAccessLevel(EdgeIdentifier.getAccessLevel(EdgeIdentifier.WRITE))
        .setUserAccessLevel(EdgeIdentifier.getAccessLevel(EdgeIdentifier.NONE))
        .setVariableItemSet(EdgeSampleCommon.WRITE_ONLY_NODES).setSourceNode(rootNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.SERVER_NODE_LINE7.getValue())
        .setEdgeNodeType(EdgeIdentifier.SINGLE_FOLDER_NODE_TYPE).setSourceNode(rootNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    EdgeNodeId methodNodeId = ProtocolManager.getProtocolManagerInstance()
        .getNodes(EdgeSampleCommon.SERVER_NODE_LINE7.getValue()).get(0);
    item = new EdgeNodeItem.Builder(EdgeSampleCommon.METHOD_SQRT.getValue())
        .setSourceNode(methodNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Method_Type, new EdgeTestMethod(),
        EdgeArgumentType.IN_OUT_ARGUMENTS);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.DATA_TYPE_NAME.getValue())
        .setEdgeNodeType(EdgeIdentifier.DATA_TYPE_NODE).setSourceNode(rootNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.OBJECT_NAME.getValue())
        .setEdgeNodeType(EdgeIdentifier.OBJECT_NODE).setSourceNode(rootNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    EdgeReference reference = new EdgeReference.Builder(EdgeSampleCommon.OBJECT_NAME.getValue(),
        namespace, EdgeSampleCommon.TARGET_NODE_LINE3_UINT.getValue(), namespace).setForward(false)
            .build();
    ProtocolManager.getProtocolManagerInstance().addReference(reference);

    reference = new EdgeReference.Builder(EdgeSampleCommon.OBJECT_NAME.getValue(), namespace,
        EdgeSampleCommon.TARGET_NODE_LINE4_UINT.getValue(), namespace).setForward(false).build();
    ProtocolManager.getProtocolManagerInstance().addReference(reference);

    reference = new EdgeReference.Builder(EdgeSampleCommon.OBJECT_NAME.getValue(), namespace,
        EdgeSampleCommon.TARGET_NODE_LINE5_UINT.getValue(), namespace).setForward(false).build();
    ProtocolManager.getProtocolManagerInstance().addReference(reference);

    reference = new EdgeReference.Builder(EdgeSampleCommon.TARGET_NODE_LINE3_UINT.getValue(),
        namespace, EdgeSampleCommon.TARGET_NODE_LINE4_UINT.getValue(), namespace).setForward(true)
            .setReferenceId(EdgeNodeIdentifier.NonHierarchicalReferences).build();
    ProtocolManager.getProtocolManagerInstance().addReference(reference);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.OBJECT_TYPE_NAME.getValue())
        .setEdgeNodeType(EdgeIdentifier.OBJECT_TYPE_NODE).setSourceNode(rootNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.REFERENCE_TYPE_NAME.getValue())
        .setEdgeNodeType(EdgeIdentifier.REFERENCE_TYPE_NODE).setSourceNode(rootNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    EdgeNodeId viewNodeId = ProtocolManager.getProtocolManagerInstance().getNodes("Line").get(0);
    item = new EdgeNodeItem.Builder(EdgeSampleCommon.VIEW_NAME.getValue())
        .setEdgeNodeType(EdgeIdentifier.VIEW_NODE).setSourceNode(viewNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.TARGET_NODE_ROBOT.getValue())
        .setDataAccessNodeId(EdgeNodeIdentifier.AnalogItemType).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Classical_DataAccess_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.TARGET_NODE_ROBOT.getValue())
        .setDataAccessNodeId(EdgeNodeIdentifier.ImageItemType).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Classical_DataAccess_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.TARGET_NODE_CNC14.getValue())
        .setDataAccessNodeId(EdgeNodeIdentifier.DataItemType).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Classical_DataAccess_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.TARGET_NODE_CNC14.getValue())
        .setDataAccessNodeId(EdgeNodeIdentifier.AnalogItemType).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Classical_DataAccess_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.TARGET_NODE_CNC14.getValue())
        .setDataAccessNodeId(EdgeNodeIdentifier.TwoStateDiscreteType).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Classical_DataAccess_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.TARGET_NODE_CNC14.getValue())
        .setDataAccessNodeId(EdgeNodeIdentifier.MultiStateDiscreteType).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Classical_DataAccess_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.TARGET_NODE_CNC14.getValue())
        .setDataAccessNodeId(EdgeNodeIdentifier.MultiStateValueDiscreteType).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Classical_DataAccess_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.TARGET_NODE_CNC14.getValue())
        .setDataAccessNodeId(EdgeNodeIdentifier.ArrayItemType).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Classical_DataAccess_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.TARGET_NODE_CNC14.getValue())
        .setDataAccessNodeId(EdgeNodeIdentifier.YArrayItemType).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Classical_DataAccess_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.TARGET_NODE_CNC14.getValue())
        .setDataAccessNodeId(EdgeNodeIdentifier.XYArrayItemType).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Classical_DataAccess_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.TARGET_NODE_CNC14.getValue())
        .setDataAccessNodeId(EdgeNodeIdentifier.ImageItemType).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Classical_DataAccess_Type);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.TARGET_NODE_CNC14.getValue())
        .setDataAccessNodeId(EdgeNodeIdentifier.NDimensionArrayItemType).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Classical_DataAccess_Type);
  }

  @Before
  public void start() {
    logger.info("IN - start");
    try {
      init();
      startServer();
      Thread.sleep(5000);
      startClient();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    logger.info("OUT - start");
  }

  public void startClient() throws Exception {
    EdgeEndpointConfig endpointConfig = new EdgeEndpointConfig.Builder()
        .setApplicationName(EdgeOpcUaCommon.DEFAULT_SERVER_APP_NAME.getValue())
        .setApplicationUri(EdgeOpcUaCommon.DEFAULT_SERVER_APP_URI.getValue()).build();
    EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().build();

    // start Client
    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(endpointUri).setConfig(endpointConfig).build();
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_START_CLIENT)
        .setRequest(new EdgeRequest.Builder(nodeInfo).build()).build();
    ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  private void startServer() throws Exception {
    EdgeEndpointConfig endpointConfig = new EdgeEndpointConfig.Builder()
        .setApplicationName(EdgeOpcUaCommon.DEFAULT_SERVER_APP_NAME.getValue())
        .setApplicationUri(EdgeOpcUaCommon.DEFAULT_SERVER_APP_URI.getValue()).build();
    EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().build();

    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(endpointUri).setConfig(endpointConfig).build();
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_START_SERVER)
        .setRequest(new EdgeRequest.Builder(nodeInfo).build()).build();
    ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  @After
  public void stop() throws Exception {
    logger.info("stop");
    stopClient();
    stopServer();
    serverInfo.clear();
    Thread.sleep(2000);
    ProtocolManager.getProtocolManagerInstance().close();
  }

  @Test
  public void testBrowse() throws Exception {
    logger.info("[TEST] testBrowse");
    Thread.sleep(5000);
    EdgeBrowseTestCase browse = new EdgeBrowseTestCase();
    browse.testBrowseRootSync();
    browse.testBrowseServerSync();
    browse.testBrowseRootSyncWithoutValueAilas();
    browse.testBrowseRootSyncWithoutEndpoint();
    browse.testBrowseRootSyncWithoutCommand();
    browse.testBrowseRootSyncWithoutMessage();
    browse.testBrowseRootSyncWithoutParam();
    Thread.sleep(5000);
  }

  @Test
  public void testReadServerInfo() throws Exception {
    logger.info("[TEST] testReadServerInfo");
    Thread.sleep(5000);
    EdgeServerReadTestCase serverInfo = new EdgeServerReadTestCase();
    serverInfo.testReadServerStatus();
    serverInfo.testReadServerStatusWithoutValueAilas();
    serverInfo.testReadServerWithoutEndpoint();
    serverInfo.testReadServerWithoutCommand();
    serverInfo.testReadServerWithoutMessage();
    Thread.sleep(5000);
  }

  @Test
  public void testSubscription() throws Exception {
    logger.info("[TEST] testSubscription");
    Thread.sleep(2000);
    EdgeSubscriptionTestCase tc = new EdgeSubscriptionTestCase();
    tc.testCreateSubWithoutCommand();
    tc.testCreateSubWithoutEndpoint();
    tc.testCreateSubWithoutMessage();
    tc.testCreateSubWithoutValueAilas();
    tc.testCreateSubWithoutSubReq();
    tc.testCreateSubWithoutSubReqNode();
    tc.testCreateSub();
    tc.testModifySub();
    Thread.sleep(1000);
    tc.testDeleteSub();
    Thread.sleep(5000);
  }

  @Test
  public void testCustomNode() throws Exception {
    logger.info("[TEST] testCustomNode");
    Thread.sleep(5000);
    EdgeCustomNodeTestCase node = new EdgeCustomNodeTestCase();
    node.testRead();
    node.testReadWithoutCommand();
    node.testReadWithoutEndpoint();
    node.testReadWithoutValueAilas();
    node.testReadWithoutMessage();
    node.testWrite();
    node.testWriteWithoutCommand();
    node.testWriteWithoutEndpoint();
    node.testWriteWithoutValueAilas();
    node.testWriteWithoutMessage();
    Thread.sleep(5000);
  }

  @Test
  public void testWriteDataItemNode() throws Exception {
    logger.info("[TEST] testWriteDataItemNode");
    Thread.sleep(5000);
    EdgeDataAccessWriteTestCase tc = new EdgeDataAccessWriteTestCase();
    tc.testAnalogItemNode();
    tc.testArrayItemNode();
    tc.testDataItemNode();
    tc.testImageItemNode();
    tc.testMultiStateValueDiscreteNode();
    tc.testNDimensionArrayItemNode();
    tc.testTwoStateDiscreteNode();
    tc.testYArrayItemNode();
    Thread.sleep(5000);
  }

  @Test
  public void testReadDataItemNode() throws Exception {
    logger.info("[TEST] testReadDataItemNode");
    Thread.sleep(5000);
    EdgeDataAccessReadTestCase tc = new EdgeDataAccessReadTestCase();
    tc.testAnalogItemNode();
    tc.testArrayItemNode();
    tc.testDataItemNode();
    tc.testImageItemNode();
    tc.testMultiStateDiscreteNode();
    tc.testMultiStateValueDiscreteNode();
    tc.testNDimensionArrayItemNode();
    tc.testTwoStateDiscreteNode();
    tc.testXYArrayItemNode();
    tc.testYArrayItemNode();
    Thread.sleep(5000);
  }

  @Test
  public void testMethodProvider() throws Exception {
    logger.info("[TEST] testMethodProvider");
    double inputParam = 16.0;
    Thread.sleep(5000);
    EdgeMethodTestCase method = new EdgeMethodTestCase();
    method.testRunMethodService(inputParam);
    method.testRunMethodServiceWithoutEndpoint(inputParam);
    method.testRunMethodServiceWithoutCommand(inputParam);
    method.testRunMethodServiceWithoutValueAilas(inputParam);
    method.testRunMethodServiceWithoutParam();
    method.testRunMethodServiceWithoutMessage(inputParam);
    Thread.sleep(5000);
  }

  ReceivedMessageCallback receiver = new ReceivedMessageCallback() {
    @Override
    public void onResponseMessages(EdgeMessage data) {}

    @Override
    public void onMonitoredMessage(EdgeMessage data) {}

    @Override
    public void onErrorMessage(EdgeMessage data) {
      // TODO Auto-generated method stub
    }

    @Override
    public void onBrowseMessage(EdgeNodeInfo endpoint, List<EdgeBrowseResult> responses,
        int requestId) {
      // TODO Auto-generated method stub

    }
  };

  DiscoveryCallback discoveryCallback = new DiscoveryCallback() {
    @Override
    public void onFoundEndpoint(EdgeDevice device) {
      // TODO Auto-generated method stub
    }


    @Override
    public void onFoundDevice(EdgeDevice device) {
      // TODO Auto-generated method stub
    }
  };

  StatusCallback statusCallback = new StatusCallback() {

    @Override
    public void onStart(EdgeEndpointInfo ep, EdgeStatusCode status,
        List<String> attiributeAliasList, List<String> methodAliasList,
        List<String> viewAliasList) {
      logger.info("onStart: status={} from={}", status, ep.getEndpointUri());

      if (status == EdgeStatusCode.STATUS_SERVER_STARTED) {
        try {
          initProvider();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }

    @Override
    public void onStop(EdgeEndpointInfo ep, EdgeStatusCode status) {
      // TODO Auto-generated method stub

    }

    @Override
    public void onNetworkStatus(EdgeEndpointInfo ep, EdgeStatusCode status) {
      // TODO Auto-generated method stub
    }
  };

  private void stopServer() throws Exception {
    EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(endpointUri).build();
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_STOP_SERVER)
        .setRequest(new EdgeRequest.Builder(new EdgeNodeInfo.Builder().build()).build()).build();
    ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  private void stopClient() throws Exception {
    EdgeEndpointInfo epInfo = new EdgeEndpointInfo.Builder(endpointUri).build();
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_STOP_CLIENT)
        .setRequest(new EdgeRequest.Builder(new EdgeNodeInfo.Builder().build()).build()).build();
    ProtocolManager.getProtocolManagerInstance().send(msg);
  }
}
