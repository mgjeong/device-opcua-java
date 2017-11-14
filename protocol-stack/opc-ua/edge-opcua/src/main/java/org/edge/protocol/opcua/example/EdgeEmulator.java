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

package org.edge.protocol.opcua.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.eclipse.milo.opcua.sdk.core.WriteMask;
import org.edge.protocol.mapper.api.EdgeMapper;
import org.edge.protocol.mapper.api.EdgeMapperCommon;
import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.client.EdgeSubRequest;
import org.edge.protocol.opcua.api.common.EdgeCommandType;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeMessageType;
import org.edge.protocol.opcua.api.common.EdgeNodeId;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeRequest;
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edge.protocol.opcua.api.server.EdgeArgumentType;
import org.edge.protocol.opcua.api.server.EdgeNodeItem;
import org.edge.protocol.opcua.api.server.EdgeNodeType;
import org.edge.protocol.opcua.api.server.EdgeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provide functions for emulator
 */
public class EdgeEmulator {
    private final Logger logger = LoggerFactory.getLogger(EdgeEmulator.class);
    private Thread analogItemThread = null;
    private Thread samplingThread = null;
    private Thread quitThread = null;
    private boolean stopFlag = false;
    private EdgeEndpointInfo epInfo = null;
    private EdgeMapper mapper = null;

    // update data module in server side
    private File file = null;
    private boolean readFileFlag = true;

    private final int END_TIME = 70;
    private final int SERVER_CLOSE_TIME = 180;

    public final EdgeNodeIdentifier[] Data_Access_Nodes = new EdgeNodeIdentifier[] {
            EdgeNodeIdentifier.AnalogItemType, EdgeNodeIdentifier.DataItemType,
            EdgeNodeIdentifier.ArrayItemType, EdgeNodeIdentifier.TwoStateDiscreteType,
            EdgeNodeIdentifier.MultiStateDiscreteType,
            EdgeNodeIdentifier.MultiStateValueDiscreteType, EdgeNodeIdentifier.YArrayItemType,
            EdgeNodeIdentifier.XYArrayItemType, EdgeNodeIdentifier.ImageItemType,
            EdgeNodeIdentifier.NDimensionArrayItemType };

    /**
     * @fn void startServer(EdgeEndpoint ep)
     * @brief start emul server
     * @param ep
     * @param filename
     *            file name which want to read
     * @throws Exception
     * @return void
     */
    public void startServer(EdgeEndpointInfo ep, String fileName) throws Exception {
        if (fileName.isEmpty() == false) {
            file = new File(fileName);
        }
        epInfo = ep;
        startServer();
    }

    /**
     * @fn void startClient(EdgeEndpoint ep)
     * @brief start emul client
     * @param ep
     * @throws Exception
     * @return void
     */
    public void startClient(EdgeEndpointInfo ep) throws Exception {
        startClient();
    }

    public void initServerNodes() throws Exception {
        createNamespace();
        try {
            testCreateNodes();
            if (analogItemThread != null) {
                analogItemThread.start();
            }
            if (samplingThread != null) {
                samplingThread.start();
            }
            if (quitThread != null) {
                quitThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @fn void initMapper()
     * @brief initialize mapper
     * @return void
     */
    public void initMapper() {
        if (null == mapper) {
            mapper = new EdgeMapper();
        }
        try {
            mapper.addMappingData(EdgeMapperCommon.PROPERTYVALUE_TYPE.toString(), "S");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @fn String getMapper(String id)
     * @brief get Mapper by id as String
     * @param id
     * @return String
     */
    public String getMapper(String id) {
        return mapper.getMappingData(id);
    }

    /**
     * @fn void run()
     * @brief run
     * @return void
     */
    public void runAutoUpdateServer() throws Exception {
        analogItemThread = new Thread() {
            public void run() {
                try {
                    for (int i = 0; i < END_TIME; i++) {
                        try {
                            testNodeModification(i + 430, EdgeNodeIdentifier.Edge_Node_Class_Type);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Thread.sleep(1000);

                        if (Thread.interrupted()) {
                            break;
                        }
                    }
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                }
            };
        };

        samplingThread = new Thread() {
            public void run() {
                try {
                    for (int i = 0; i < 100000; i++) {
                        try {
                            testNodeModification(i++, EdgeNodeIdentifier.Edge_Node_Custom_Type);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Thread.sleep(100);

                        if (Thread.interrupted()) {
                            break;
                        }
                    }
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                }
            };
        };

        quitThread = new Thread() {
            public void run() {
                try {
                    for (int i = 0; i < SERVER_CLOSE_TIME; i++) {
                        Thread.sleep(1000);

                        if (Thread.interrupted()) {
                            logger.info("quitThread");
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                if (stopFlag != true) {
                    logger.info("Server close.");
                    try {
                        stopFlag = true;
                        terminate();
                        Thread.currentThread().interrupt();
                        System.out.print("Please Input Any Key...");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        };

        // testRead(EdgeSampleCommon.KEY_URI_LINE_CNC14.getValue(),
        // EdgeNodeIdentifier.Edge_Node_Custom_Type);
        // testRead(EdgeSampleCommon.KEY_URI_DA_TEMPORATURE1.getValue(),
        // EdgeNodeIdentifier.Edge_Node_Class_Type);
        // testSub(EdgeNodeIdentifier.Edge_Node_Class_Type);
    }

    public void runAutoUpdateServerWithFile() throws Exception {
        while (readFileFlag) {
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader reader = new BufferedReader(fileReader);
                String line = null;
                String npName = EdgeOpcUaCommon.DEFAULT_NAMESPACE.getValue();
                while ((line = reader.readLine()) != null) {

                    ProtocolManager.getProtocolManagerInstance().modifyVariableNodeValue(npName,
                            EdgeSampleCommon.TARGET_NODE_CNC14.getValue(),
                            new EdgeVersatility.Builder(line).build());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                reader.close();
                fileReader.close();

            } catch (Exception ex) {
                System.out.println("read file error");
            }
        }
    }

    /**
     * @fn void terminate()
     * @brief terminate
     * @return void
     */
    public void terminate() throws Exception {
        if (analogItemThread != null) {
            analogItemThread.interrupt();
        }
        if (samplingThread != null) {
            samplingThread.interrupt();
        }
        readFileFlag = false;
        stopServer();
        stopClient();
        if (quitThread != null) {
            quitThread.interrupt();
        }
    }

    /**
     * @fn void testCreateNodes()
     * @brief create test nodes
     * @return void
     */
    public void testCreateNodes() throws Exception {
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
                .setVariableItemSet(EdgeSampleCommon.WRITE_ONLY_NODES).setSourceNode(rootNodeId)
                .build();
        ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
                EdgeNodeType.Edge_Node_Custom_Type);

        /*
         * AccessLevel : Read Only UserAccessLevel : Read Only
         */
        item = new EdgeNodeItem.Builder(EdgeSampleCommon.SERVER_NODE_READ_ONLY.getValue())
                .setEdgeNodeType(EdgeIdentifier.SINGLE_FOLDER_NODE_TYPE)
                .setAccessLevel(EdgeIdentifier.getAccessLevel(EdgeIdentifier.READ))
                .setUserAccessLevel(EdgeIdentifier.getAccessLevel(EdgeIdentifier.READ))
                .setVariableItemSet(EdgeSampleCommon.READ_ONLY_NODES).setSourceNode(rootNodeId)
                .build();
        ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
                EdgeNodeType.Edge_Node_Custom_Type);

        /*
         * AccessLevel : Read Only UserAccessLevel : NONE
         */
        item = new EdgeNodeItem.Builder(EdgeSampleCommon.SERVER_NODE_READ_NOT_USER.getValue())
                .setEdgeNodeType(EdgeIdentifier.SINGLE_FOLDER_NODE_TYPE)
                .setAccessLevel(EdgeIdentifier.getAccessLevel(EdgeIdentifier.READ))
                .setUserAccessLevel(EdgeIdentifier.getAccessLevel(EdgeIdentifier.NONE))
                .setVariableItemSet(EdgeSampleCommon.READ_ONLY_NODES).setSourceNode(rootNodeId)
                .build();
        ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
                EdgeNodeType.Edge_Node_Custom_Type);

        /*
         * AccessLevel : Write Only UserAccessLevel : NONE
         */
        item = new EdgeNodeItem.Builder(EdgeSampleCommon.SERVER_NODE_WRITE_NOT_USER.getValue())
                .setEdgeNodeType(EdgeIdentifier.SINGLE_FOLDER_NODE_TYPE)
                .setAccessLevel(EdgeIdentifier.getAccessLevel(EdgeIdentifier.WRITE))
                .setUserAccessLevel(EdgeIdentifier.getAccessLevel(EdgeIdentifier.NONE))
                .setVariableItemSet(EdgeSampleCommon.WRITE_ONLY_NODES).setSourceNode(rootNodeId)
                .build();
        ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
                EdgeNodeType.Edge_Node_Custom_Type);

        item = new EdgeNodeItem.Builder(EdgeSampleCommon.SERVER_NODE_LINE7.getValue())
                .setEdgeNodeType(EdgeIdentifier.SINGLE_FOLDER_NODE_TYPE).setSourceNode(rootNodeId)
                .build();
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
                namespace, EdgeSampleCommon.TARGET_NODE_LINE3_UINT.getValue(), namespace)
                        .setForward(false).build();
        ProtocolManager.getProtocolManagerInstance().addReference(reference);

        reference = new EdgeReference.Builder(EdgeSampleCommon.OBJECT_NAME.getValue(), namespace,
                EdgeSampleCommon.TARGET_NODE_LINE4_UINT.getValue(), namespace).setForward(false)
                        .build();
        ProtocolManager.getProtocolManagerInstance().addReference(reference);

        reference = new EdgeReference.Builder(EdgeSampleCommon.OBJECT_NAME.getValue(), namespace,
                EdgeSampleCommon.TARGET_NODE_LINE5_UINT.getValue(), namespace).setForward(false)
                        .build();
        ProtocolManager.getProtocolManagerInstance().addReference(reference);

        reference = new EdgeReference.Builder(EdgeSampleCommon.TARGET_NODE_LINE3_UINT.getValue(),
                namespace, EdgeSampleCommon.TARGET_NODE_LINE4_UINT.getValue(), namespace)
                        .setForward(true)
                        .setReferenceId(EdgeNodeIdentifier.NonHierarchicalReferences).build();
        ProtocolManager.getProtocolManagerInstance().addReference(reference);

        item = new EdgeNodeItem.Builder(EdgeSampleCommon.OBJECT_TYPE_NAME.getValue())
                .setEdgeNodeType(EdgeIdentifier.OBJECT_TYPE_NODE).setSourceNode(rootNodeId).build();
        ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
                EdgeNodeType.Edge_Node_Custom_Type);

        item = new EdgeNodeItem.Builder(EdgeSampleCommon.REFERENCE_TYPE_NAME.getValue())
                .setEdgeNodeType(EdgeIdentifier.REFERENCE_TYPE_NODE).setSourceNode(rootNodeId)
                .build();
        ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
                EdgeNodeType.Edge_Node_Custom_Type);

        EdgeNodeId viewNodeId = ProtocolManager.getProtocolManagerInstance().getNodes("Line")
                .get(0);
        item = new EdgeNodeItem.Builder(EdgeSampleCommon.VIEW_NAME.getValue())
                .setEdgeNodeType(EdgeIdentifier.VIEW_NODE).setSourceNode(viewNodeId).build();
        ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
                EdgeNodeType.Edge_Node_Custom_Type);

        for (EdgeNodeIdentifier daNodeId : Data_Access_Nodes) {
            item = new EdgeNodeItem.Builder(EdgeSampleCommon.TARGET_NODE_ROBOT.getValue())
                    .setDataAccessNodeId(daNodeId).setSourceNode(rootNodeId).build();
            ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
                    EdgeNodeType.Edge_Node_Classical_DataAccess_Type);
        }
    }

    /**
     * @fn void runClientSub()
     * @brief run temp sensor
     * @return void
     */
    public void runClientSub() {
        try {
            testSub(EdgeNodeIdentifier.Edge_Node_Custom_Type);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @fn void testSub(EdgeNodeIdentifier type)
     * @brief sub for test
     * @param [in]
     *            type
     * @return void
     */
    public void testSub(EdgeNodeIdentifier type) throws Exception {
        if (type == EdgeNodeIdentifier.Edge_Node_Custom_Type) {
            EdgeSubRequest sub = new EdgeSubRequest.Builder(EdgeNodeIdentifier.Edge_Create_Sub)
                    .setSamplingInterval(100.0).build();
            EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias("/1/cnc14").build();

            EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB)
                    .setMessageType(EdgeMessageType.SEND_REQUEST)
                    .setRequest(new EdgeRequest.Builder(ep).setSubReq(sub).build()).build();
            ProtocolManager.getProtocolManagerInstance().send(msg);

        } else if (type == EdgeNodeIdentifier.Edge_Node_Class_Type) {
            EdgeSubRequest sub = new EdgeSubRequest.Builder(EdgeNodeIdentifier.Edge_Create_Sub)
                    .setSamplingInterval(3000.0).build();
            EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
                    .setValueAlias(EdgeSampleCommon.KEY_URI_DA_TEMPORATURE1.getValue()).build();
            EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB)
                    .setMessageType(EdgeMessageType.SEND_REQUEST)
                    .setRequest(new EdgeRequest.Builder(ep).setSubReq(sub).build()).build();
            ProtocolManager.getProtocolManagerInstance().send(msg);
        }
    }

    /**
     * @fn void testRead(String serviceName, EdgeNodeIdentifier type)
     * @brief read for test
     * @param [in]
     *            serviceName
     * @param [in]
     *            type
     * @return void
     */
    public void testRead(String serviceName, EdgeNodeIdentifier type) throws Exception {
        if (type == EdgeNodeIdentifier.Edge_Node_Custom_Type) {
            EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(serviceName).build();
            EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
                    .setMessageType(EdgeMessageType.SEND_REQUEST)
                    .setRequest(new EdgeRequest.Builder(ep).setReturnDiagnostic(1).build()).build();
            ProtocolManager.getProtocolManagerInstance().send(msg);
        } else if (type == EdgeNodeIdentifier.Edge_Node_Class_Type) {
            EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(serviceName).build();
            EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
                    .setMessageType(EdgeMessageType.SEND_REQUEST)
                    .setRequest(new EdgeRequest.Builder(ep).build()).build();
            ProtocolManager.getProtocolManagerInstance().send(msg);
        } else if (type == EdgeNodeIdentifier.Edge_Node_ServerInfo_Type) {
            EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setEdgeNodeId(
                    new EdgeNodeId.Builder(EdgeNodeIdentifier.Server_ServerStatus_State).build())
                    .setValueAlias(serviceName).build();
            EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
                    .setMessageType(EdgeMessageType.SEND_REQUEST)
                    .setRequest(new EdgeRequest.Builder(ep).build()).build();
            ProtocolManager.getProtocolManagerInstance().send(msg);
        }
    }

    /**
     * @fn void testNodeModification(int i)
     * @brief MOdification test for Node
     * @param [in]
     *            i
     * @return void
     */
    public static void testNodeModification(int i, EdgeNodeIdentifier type) throws Exception {
        String npName = EdgeOpcUaCommon.DEFAULT_NAMESPACE.getValue();
        if (type == EdgeNodeIdentifier.Edge_Node_Class_Type) {
            ProtocolManager.getProtocolManagerInstance().modifyDataAccessNodeValue(npName,
                    EdgeNodeIdentifier.AnalogItemType, new EdgeVersatility.Builder(i).build());
        } else if (type == EdgeNodeIdentifier.Edge_Node_Custom_Type) {
            ProtocolManager.getProtocolManagerInstance().modifyVariableNodeValue(npName,
                    EdgeSampleCommon.TARGET_NODE_CNC14.getValue(),
                    new EdgeVersatility.Builder("Spin Cycle-" + String.valueOf(i)).build());
        }
    }

    /**
     * @fn void startServer()
     * @brief start server
     * @return void
     */
    private void startServer() throws Exception {
        EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().build();
        EdgeMessage msg = new EdgeMessage.Builder(epInfo)
                .setCommand(EdgeCommandType.CMD_START_SERVER)
                .setMessageType(EdgeMessageType.SEND_REQUEST)
                .setRequest(new EdgeRequest.Builder(nodeInfo).build()).build();
        ProtocolManager.getProtocolManagerInstance().send(msg);
    }

    /**
     * @fn void stopServer()
     * @brief stop server
     * @return void
     */
    private void stopServer() throws Exception {
        EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().build();
        EdgeMessage msg = new EdgeMessage.Builder(epInfo)
                .setCommand(EdgeCommandType.CMD_STOP_SERVER)
                .setMessageType(EdgeMessageType.SEND_REQUEST)
                .setRequest(new EdgeRequest.Builder(nodeInfo).build()).build();
        ProtocolManager.getProtocolManagerInstance().send(msg);
    }

    /**
     * @fn void startClient()
     * @brief start server
     * @return void
     */
    public void startClient() throws Exception {
        EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().build();
        EdgeMessage msg = new EdgeMessage.Builder(epInfo)
                .setCommand(EdgeCommandType.CMD_START_CLIENT)
                .setMessageType(EdgeMessageType.SEND_REQUEST)
                .setRequest(new EdgeRequest.Builder(nodeInfo).build()).build();
        ProtocolManager.getProtocolManagerInstance().send(msg);
    }

    /**
     * @fn void stopClient()
     * @brief stop client
     * @return void
     */
    private void stopClient() throws Exception {
        EdgeNodeInfo endpoint = new EdgeNodeInfo.Builder().build();
        EdgeMessage msg = new EdgeMessage.Builder(epInfo)
                .setCommand(EdgeCommandType.CMD_STOP_CLIENT)
                .setMessageType(EdgeMessageType.SEND_REQUEST)
                .setRequest(new EdgeRequest.Builder(endpoint).build()).build();
        ProtocolManager.getProtocolManagerInstance().send(msg);
    }

    /**
     * @fn void createNamespace()
     * @brief create Namespace
     * @return void
     */
    private void createNamespace() {
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
    }
}
