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

package edge.opcua;

import java.util.List;
import java.util.Scanner;
import org.eclipse.milo.opcua.sdk.core.WriteMask;
import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.ProtocolManager.DiscoveryCallback;
import org.edge.protocol.opcua.api.ProtocolManager.ReceivedMessageCallback;
import org.edge.protocol.opcua.api.ProtocolManager.StatusCallback;
import org.edge.protocol.opcua.api.client.EdgeResponse;
import org.edge.protocol.opcua.api.common.EdgeBrowseResult;
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
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edge.protocol.opcua.api.server.EdgeNodeItem;
import org.edge.protocol.opcua.api.server.EdgeNodeType;
import org.edge.protocol.opcua.api.server.EdgeReference;
import org.edge.protocol.opcua.api.server.EdgeArgumentType;
import org.edge.protocol.opcua.api.common.EdgeCommandType;
import org.edge.protocol.opcua.example.EdgeSampleCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeServer {
  private static final Logger logger = LoggerFactory.getLogger(EdgeServer.class);
  private static Thread analogItemThread = null;
  private static Thread samplingThread = null;
  private static Thread quitThread = null;
  private static boolean stopFlag = false;
  private static boolean startFlag = false;

  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_YELLOW = "\u001B[33m";
  private static final String ANSI_PURPLE = "\u001B[35m";

  private static String ipAddress;
  private static String endpointUri =
      "opc.tcp://localhost:12686/" + EdgeOpcUaCommon.DEFAULT_SERVER_NAME.getValue();
  private static EdgeEndpointInfo epInfo;

  private static final int END_TIME = 70;
  private static final int SERVER_CLOSE_TIME = 700;

  public static final EdgeNodeIdentifier[] Data_Access_Nodes =
      new EdgeNodeIdentifier[] {EdgeNodeIdentifier.AnalogItemType, EdgeNodeIdentifier.DataItemType,
          EdgeNodeIdentifier.ArrayItemType, EdgeNodeIdentifier.TwoStateDiscreteType,
          EdgeNodeIdentifier.MultiStateDiscreteType, EdgeNodeIdentifier.MultiStateValueDiscreteType,
          EdgeNodeIdentifier.YArrayItemType, EdgeNodeIdentifier.XYArrayItemType,
          EdgeNodeIdentifier.ImageItemType, EdgeNodeIdentifier.NDimensionArrayItemType};

  public static void main(String[] args) throws Exception {
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
            logger.info("CNC 100 spin cycle - {}", i);
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
    startCommand();
  }

  public static void testNodeModification(int i, EdgeNodeIdentifier type) throws Exception {
    String npName = EdgeOpcUaCommon.DEFAULT_NAMESPACE.getValue();
    if (type == EdgeNodeIdentifier.Edge_Node_Class_Type) {
      ProtocolManager.getProtocolManagerInstance().modifyDataAccessNodeValue(npName,
          EdgeNodeIdentifier.AnalogItemType, new EdgeVersatility.Builder(i).build());
    } else if (type == EdgeNodeIdentifier.Edge_Node_Custom_Type) {
      ProtocolManager.getProtocolManagerInstance().modifyVariableNodeValue(npName,
          EdgeSampleCommon.TARGET_NODE_CNC100.getValue(), new EdgeVersatility.Builder(i).build());
    }
  }

  public static void init() throws Exception {
    EdgeConfigure configure = new EdgeConfigure.Builder().setRecvCallback(receiver)
        .setStatusCallback(statusCallback).setDiscoveryCallback(discoveryCallback).build();

    ProtocolManager protocolManager = ProtocolManager.getProtocolManagerInstance();
    protocolManager.configure(configure);
  }

  private static void startServer() throws Exception {
    EdgeEndpointConfig endpointConfig =
        new EdgeEndpointConfig.Builder().setbindAddress(ipAddress).setbindPort(12686).build();
    EdgeEndpointInfo ep =
        new EdgeEndpointInfo.Builder(endpointUri).setConfig(endpointConfig).build();
    EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().build();
    EdgeMessage msg = new EdgeMessage.Builder(ep).setCommand(EdgeCommandType.CMD_START_SERVER)
        .setRequest(new EdgeRequest.Builder(nodeInfo).build()).build();
    ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  private static void stopServer() throws Exception {
    EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().build();
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_STOP_SERVER)
        .setRequest(new EdgeRequest.Builder(nodeInfo).build()).build();
    ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  public static void getNodes() throws Exception {
    List<EdgeNodeId> nodes = ProtocolManager.getProtocolManagerInstance().getNodes();
    for (EdgeNodeId node : nodes) {
      logger.info("node : namespace={}, identifier={}", node.getNameSpace(), node.getIdentifier());
    }
  }

  public static void getNodes(String browseName) throws Exception {
    List<EdgeNodeId> nodes = ProtocolManager.getProtocolManagerInstance().getNodes(browseName);
    for (EdgeNodeId node : nodes) {
      logger.info("node : namespace={}, identifier={}", node.getNameSpace(), node.getIdentifier());
    }
  }

  public static void terminate() throws Exception {
    if (startFlag) {
      analogItemThread.interrupt();
      samplingThread.interrupt();

      stopServer();
      quitThread.interrupt();
    }
    ProtocolManager.getProtocolManagerInstance().close();
  }

  public static void testCreateNodes() throws Exception {
    String namespace = EdgeOpcUaCommon.DEFAULT_NAMESPACE.getValue();
    EdgeNodeId rootNodeId = ProtocolManager.getProtocolManagerInstance()
        .getNodes(EdgeOpcUaCommon.DEFAULT_ROOT_NODE_INFO.getValue()).get(0);

    System.out.println(ANSI_GREEN + "\n------------------------" + ANSI_RESET);
    System.out.println(ANSI_YELLOW + " Create Variable Node " + ANSI_RESET);
    System.out.println(ANSI_GREEN + "------------------------\n" + ANSI_RESET);

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

    System.out.println(ANSI_GREEN + "\n------------------------" + ANSI_RESET);
    System.out.println(ANSI_YELLOW + " Create Array Node " + ANSI_RESET);
    System.out.println(ANSI_GREEN + "------------------------\n" + ANSI_RESET);

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

    System.out.println(ANSI_GREEN + "\n------------------------" + ANSI_RESET);
    System.out.println(ANSI_YELLOW + " Create Method Node " + ANSI_RESET);
    System.out.println(ANSI_GREEN + "------------------------\n" + ANSI_RESET);

    EdgeNodeId methodNodeId = ProtocolManager.getProtocolManagerInstance()
        .getNodes(EdgeSampleCommon.SERVER_NODE_LINE7.getValue()).get(0);
    item = new EdgeNodeItem.Builder(EdgeSampleCommon.METHOD_SQRT.getValue())
        .setSourceNode(methodNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Method_Type, new EdgeTestMethodSqrt(),
        EdgeArgumentType.IN_OUT_ARGUMENTS);

    item = new EdgeNodeItem.Builder(EdgeSampleCommon.METHOD_ABS.getValue())
        .setSourceNode(methodNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Method_Type, new EdgeTestMethodSqrt(),
        EdgeArgumentType.IN_OUT_ARGUMENTS);

    item = new EdgeNodeItem.Builder("print(x)").setSourceNode(methodNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Method_Type, new EdgeTestMethodPrint(),
        EdgeArgumentType.IN_ARGUMENT);

    item = new EdgeNodeItem.Builder("shutdown()").setSourceNode(methodNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Method_Type, new EdgeTestMethodShutDown(),
        EdgeArgumentType.VOID_ARGUMENT);

    item = new EdgeNodeItem.Builder("version()").setSourceNode(methodNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Method_Type, new EdgeTestMethodVersion(),
        EdgeArgumentType.OUT_ARGUMENT);

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

    System.out.println(ANSI_GREEN + "\n------------------------" + ANSI_RESET);
    System.out.println(ANSI_YELLOW + " Create View Node " + ANSI_RESET);
    System.out.println(ANSI_GREEN + "------------------------\n" + ANSI_RESET);

    EdgeNodeId viewNodeId = ProtocolManager.getProtocolManagerInstance().getNodes("Line").get(0);
    item = new EdgeNodeItem.Builder(EdgeSampleCommon.VIEW_NAME.getValue())
        .setEdgeNodeType(EdgeIdentifier.VIEW_NODE).setSourceNode(viewNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    viewNodeId = ProtocolManager.getProtocolManagerInstance().getNodes("Line_7").get(0);
    item = new EdgeNodeItem.Builder(EdgeSampleCommon.VIEW_NAME.getValue())
        .setEdgeNodeType(EdgeIdentifier.VIEW_NODE).setSourceNode(viewNodeId).build();
    ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
        EdgeNodeType.Edge_Node_Custom_Type);

    // for (EdgeNodeIdentifier daNodeId : Data_Access_Nodes) {
    // item = new EdgeNodeItem.Builder(EdgeSampleCommon.TARGET_NODE_ROBOT.getValue())
    // .setDataAccessNodeId(daNodeId).setSourceNode(rootNodeId).build();
    // ProtocolManager.getProtocolManagerInstance().createNode(namespace, item,
    // EdgeNodeType.Edge_Node_Classical_DataAccess_Type);
    // }
  }

  private static void createNamespace() {
    try {
      ProtocolManager.getProtocolManagerInstance().createNamespace(
          EdgeOpcUaCommon.DEFAULT_NAMESPACE.getValue(),
          EdgeOpcUaCommon.DEFAULT_ROOT_NODE_INFO.getValue(),
          EdgeOpcUaCommon.DEFAULT_ROOT_NODE_INFO.getValue(),
          EdgeOpcUaCommon.DEFAULT_ROOT_NODE_INFO.getValue());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("resource")
  public static void startCommand() throws Exception {
    String operator = "";
    Scanner scanner = new Scanner(System.in);

    printMenu();
    while (!stopFlag) {
      System.out.print("[INPUT COMMAND] : ");
      operator = scanner.nextLine();
      if (stopFlag) {
        break;
      } else if (operator.equals("start")) {
        // start Server
        System.out.println(ANSI_GREEN + "\n------------------------" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + " Start Server " + ANSI_RESET);
        System.out.println(ANSI_GREEN + "------------------------\n" + ANSI_RESET);

        System.out.print("[PLEASE INPUT SERVER ADDRESS] : ");
        ipAddress = scanner.nextLine();
        endpointUri =
            "opc.tcp://" + ipAddress + ":12686/" + EdgeOpcUaCommon.DEFAULT_SERVER_NAME.getValue();
        epInfo = new EdgeEndpointInfo.Builder(endpointUri).build();
        init();
        startServer();
        startFlag = true;
      } else if (operator.equals("start CNC")) {
        System.out.println(ANSI_GREEN + "\n------------------------" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + " Increase CNC SPIN Cycle" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "------------------------\n" + ANSI_RESET);
        if (startFlag) {
          samplingThread.start();
        } else {
          System.out.println("[WARN] server is not initialized");
        }
      } else if (operator.equals("quit")) {
        stopFlag = true;
        terminate();
        break;
      } else if (operator.equals("getnode")) {
        getNodes();
      } else if (operator.equals("getnode2")) {
        getNodes("Line");
      } else if (operator.equals("help")) {
        printMenu();
      } else {
        System.out.println("[WARN] invaild input");
      }
    }
    logger.info("finished");
  }

  static ReceivedMessageCallback receiver = new ReceivedMessageCallback() {
    @Override
    public void onResponseMessages(EdgeMessage data) {
      for (EdgeResponse res : data.getResponses()) {
        logger.info("onResponseMessages = {}", res.getMessage().getValue());
      }
    }

    @Override
    public void onMonitoredMessage(EdgeMessage data) {
      for (EdgeResponse res : data.getResponses()) {
        logger.info("onMonitoredMessage = {}", res.getMessage().getValue());
      }
    }

    @Override
    public void onErrorMessage(EdgeMessage data) {
      logger.info("onErrorMessage : code={}", data.getResult().getStatusCode());
      for (EdgeResponse res : data.getResponses()) {
        if (res != null && res.getMessage() != null) {
          logger.info("onErrorMessage detailed msg={}", res.getMessage().getValue());
        }
      }
    }

    @Override
    public void onBrowseMessage(EdgeNodeInfo endpoint, List<EdgeBrowseResult> responses,
        int requestId) {}
  };

  static DiscoveryCallback discoveryCallback = new DiscoveryCallback() {
    @Override
    public void onFoundEndpoint(EdgeDevice device) {}

    @Override
    public void onFoundDevice(EdgeDevice device) {
      logger.info("[Event] onFoundDevice is not supported");
    }
  };

  static StatusCallback statusCallback = new StatusCallback() {

    @Override
    public void onStart(EdgeEndpointInfo ep, EdgeStatusCode status,
        List<String> attiributeAliasList, List<String> methodAliasList,
        List<String> viewAliasList) {
      if (status == EdgeStatusCode.STATUS_SERVER_STARTED) {
        createNamespace();
        try {
          testCreateNodes();
          analogItemThread.start();
          quitThread.start();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    @Override
    public void onStop(EdgeEndpointInfo ep, EdgeStatusCode status) {}

    @Override
    public void onNetworkStatus(EdgeEndpointInfo ep, EdgeStatusCode status) {
      logger.info("onNetworkStatus: status={} from={}", status, ep.getEndpointUri());
    }
  };

  static public void printMenu() {
    System.out.println("--------------------opc ua--------------------");

    System.out.println("start : start opcua server and createNode");
    System.out.println("start CNC : start CNC server cycle");
    System.out.println("getnode : get node information");
    System.out.println("getnode2 : get node information with browseName");
    System.out.println("quit : terminate/stop opcua server/client and then quit");
    System.out.println("help : print menu");
  }
}
