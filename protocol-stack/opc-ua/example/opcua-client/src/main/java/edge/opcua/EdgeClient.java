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

import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.edge.protocol.opcua.api.common.EdgeCommandType;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.ProtocolManager.DiscoveryCallback;
import org.edge.protocol.opcua.api.ProtocolManager.ReceivedMessageCallback;
import org.edge.protocol.opcua.api.ProtocolManager.StatusCallback;
import org.edge.protocol.opcua.api.client.EdgeBrowseParameter;
import org.edge.protocol.opcua.api.common.EdgeBrowseResult;
import org.edge.protocol.opcua.api.common.EdgeConfigure;
import org.edge.protocol.opcua.api.common.EdgeDevice;
import org.edge.protocol.opcua.api.client.EdgeDiagnosticInfo;
import org.edge.protocol.opcua.api.client.EdgeResponse;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeEndpointConfig;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeMessageType;
import org.edge.protocol.opcua.api.common.EdgeNodeId;
import org.edge.protocol.opcua.api.common.EdgeRequest;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.api.client.EdgeSubRequest;
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edge.protocol.opcua.example.EdgeSampleCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeClient {
  private static final Logger logger = LoggerFactory.getLogger(EdgeClient.class);

  private static Thread autoThread = null;
  private static boolean autoFlag = false;
  private static boolean startFlag = false;
  private static Thread quitThread = null;

  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_YELLOW = "\u001B[33m";
  private static final String ANSI_PURPLE = "\u001B[35m";

  private static String endpointUri = EdgeOpcUaCommon.DEFAULT_ENDPOINT.getValue();
  private static String defaultSecureType =
      "http://opcfoundation.org/UA/SecurityPolicy#Basic128Rsa15";
  private static EdgeEndpointInfo epInfo;
  private static List<String> methodProviders = null;
  private static List<String> attributeProviders = null;
  private static List<String> viewProviders = null;

  public static void main(String[] args) throws Exception {
    quitThread = new Thread() {
      public void run() {
        try {
          while (!Thread.currentThread().isInterrupted()) {
            Thread.sleep(1000);
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    };

    autoThread = new Thread() {
      public void run() {
        try {
          autoFlag = true;
          init();
          startClient(defaultSecureType);
          startFlag = true;
        } catch (Exception e) {
          e.printStackTrace();
        }
      };
    };
    startCommand();
  }

  public static void startClient(String securePolicyType) throws Exception {
    EdgeEndpointConfig endpointConfig = new EdgeEndpointConfig.Builder()
        .setApplicationName(EdgeOpcUaCommon.DEFAULT_SERVER_APP_NAME.getValue())
        .setApplicationUri(EdgeOpcUaCommon.DEFAULT_SERVER_APP_URI.getValue())
        .setSecurityPolicyUri(securePolicyType).build();
    EdgeEndpointInfo ep =
        new EdgeEndpointInfo.Builder(endpointUri).setConfig(endpointConfig).build();
    EdgeNodeInfo endpoint = new EdgeNodeInfo.Builder().build();

    // start Client
    EdgeMessage msg = new EdgeMessage.Builder(ep).setCommand(EdgeCommandType.CMD_START_CLIENT)
        .setRequest(new EdgeRequest.Builder(endpoint).build()).build();
    ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  public static void init() throws Exception {
    EdgeConfigure configure = new EdgeConfigure.Builder().setRecvCallback(receiver)
        .setStatusCallback(statusCallback).setDiscoveryCallback(discoveryCallback).build();

    ProtocolManager protocolManager = ProtocolManager.getProtocolManagerInstance();
    protocolManager.configure(configure);
  }

  public static void terminate() throws Exception {
    if (startFlag) {
      autoThread.interrupt();
      stopClient();
      quitThread.interrupt();
    }
    ProtocolManager.getProtocolManagerInstance().close();;
  }

  private static void stopClient() throws Exception {
    EdgeNodeInfo endpoint = new EdgeNodeInfo.Builder().build();
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_STOP_CLIENT)
        .setRequest(new EdgeRequest.Builder(endpoint).build()).build();
    ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  public static void testBrowse() throws Exception {
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
        .setEdgeNodeId(new EdgeNodeId.Builder(EdgeNodeIdentifier.RootFolder).build())
        .setValueAlias(EdgeOpcUaCommon.WELL_KNOWN_DISCOVERY.getValue()).build();
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_BROWSE)
        .setBrowseParameter(new EdgeBrowseParameter.Builder().build())
        .setRequest(new EdgeRequest.Builder(ep).build()).build();
    ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  public static void testBrowses() throws Exception {
    EdgeNodeInfo ep1 =
        new EdgeNodeInfo.Builder().setValueAlias(EdgeOpcUaCommon.WELL_KNOWN_DISCOVERY.getValue())
            .setEdgeNodeId(new EdgeNodeId.Builder(EdgeNodeIdentifier.Server).build()).build();
    EdgeNodeInfo ep2 =
        new EdgeNodeInfo.Builder().setValueAlias(EdgeOpcUaCommon.WELL_KNOWN_DISCOVERY.getValue())
            .setEdgeNodeId(new EdgeNodeId.Builder(EdgeNodeIdentifier.RootFolder).build()).build();
    EdgeNodeInfo ep3 =
        new EdgeNodeInfo.Builder().setValueAlias(EdgeOpcUaCommon.WELL_KNOWN_DISCOVERY.getValue())
            .setEdgeNodeId(new EdgeNodeId.Builder(EdgeNodeIdentifier.RootFolder).build()).build();
    EdgeBrowseParameter browseParam = new EdgeBrowseParameter.Builder().build();
    List<EdgeRequest> requestList = newArrayList(new EdgeRequest.Builder(ep1).build(),
        new EdgeRequest.Builder(ep2).build(), new EdgeRequest.Builder(ep3).build());
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_BROWSE)
        .setBrowseParameter(browseParam).setMessageType(EdgeMessageType.SEND_REQUESTS)
        .setRequests(requestList).build();
    ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  public static void testSub(EdgeNodeIdentifier type) throws Exception {
    if (type == EdgeNodeIdentifier.Edge_Node_Custom_Type) {
      Scanner scanner = new Scanner(System.in);
      System.out.print("[select sensor] : ");
      String sensor = scanner.next();
      EdgeSubRequest sub = new EdgeSubRequest.Builder(EdgeNodeIdentifier.Edge_Create_Sub)
          .setSamplingInterval(1000.0).build();
      EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(sensor).build();

      EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB)
          .setRequest(new EdgeRequest.Builder(ep).setSubReq(sub).build()).build();
      ProtocolManager.getProtocolManagerInstance().send(msg);

    } else if (type == EdgeNodeIdentifier.Edge_Node_Class_Type) {
      EdgeSubRequest sub = new EdgeSubRequest.Builder(EdgeNodeIdentifier.Edge_Create_Sub)
          .setSamplingInterval(3000.0).build();
      EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
          .setValueAlias(EdgeSampleCommon.KEY_URI_DA_TEMPORATURE1.getValue()).build();
      EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB)
          .setRequest(new EdgeRequest.Builder(ep).setSubReq(sub).build()).build();
      ProtocolManager.getProtocolManagerInstance().send(msg);
    }
  }

  public static void testSubModification(String provider) throws Exception {
    EdgeSubRequest sub = new EdgeSubRequest.Builder(EdgeNodeIdentifier.Edge_Modify_Sub)
        .setSamplingInterval(3000.0).build();
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(provider).build();

    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB)
        .setRequest(new EdgeRequest.Builder(ep).setSubReq(sub).build()).build();
    ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  public static void testSubDelete(String provider) throws Exception {
    EdgeSubRequest sub = new EdgeSubRequest.Builder(EdgeNodeIdentifier.Edge_Delete_Sub).build();
    EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(provider).build();

    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_SUB)
        .setRequest(new EdgeRequest.Builder(ep).setSubReq(sub).build()).build();
    ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  public static void testRead(String serviceName, EdgeNodeIdentifier type) throws Exception {
    System.out.println(ANSI_GREEN + "\n------------------------" + ANSI_RESET);
    System.out.println(ANSI_YELLOW + " Read Req/Res " + ANSI_RESET);
    System.out.println(ANSI_GREEN + "------------------------\n" + ANSI_RESET);
    if (type == EdgeNodeIdentifier.Edge_Node_Custom_Type) {
      EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(serviceName).build();
      EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
          .setRequest(new EdgeRequest.Builder(ep).setReturnDiagnostic(1).build()).build();
      ProtocolManager.getProtocolManagerInstance().send(msg);
    } else if (type == EdgeNodeIdentifier.Edge_Node_Class_Type) {
      EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(serviceName).build();
      EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
          .setRequest(new EdgeRequest.Builder(ep).build()).build();
      ProtocolManager.getProtocolManagerInstance().send(msg);
    } else if (type == EdgeNodeIdentifier.Edge_Node_ServerInfo_Type) {
      EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
          .setEdgeNodeId(
              new EdgeNodeId.Builder(EdgeNodeIdentifier.Server_ServerStatus_State).build())
          .setValueAlias(serviceName).build();
      EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
          .setRequest(new EdgeRequest.Builder(ep).build()).build();
      ProtocolManager.getProtocolManagerInstance().send(msg);
    }
  }

  public static void testReadGroup() throws Exception {
    EdgeNodeInfo ep1 = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_LINE_CNC14.getValue()).build();
    EdgeNodeInfo ep2 = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_LINE_TEMPORATURE.getValue()).build();
    EdgeNodeInfo ep3 = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_LINE_CNC100.getValue()).build();

    List<EdgeRequest> requests = newArrayList(new EdgeRequest.Builder(ep1).build(),
        new EdgeRequest.Builder(ep2).build(), new EdgeRequest.Builder(ep3).build());
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_READ)
        .setMessageType(EdgeMessageType.SEND_REQUESTS).setRequests(requests).build();
    ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  public static void testWriteGroup() throws Exception {
    EdgeNodeInfo ep1 = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_LINE_CNC14.getValue()).build();
    EdgeNodeInfo ep2 = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_LINE_TEMPORATURE.getValue()).build();
    EdgeNodeInfo ep3 = new EdgeNodeInfo.Builder()
        .setValueAlias(EdgeSampleCommon.KEY_URI_LINE_CNC100.getValue()).build();

    List<EdgeRequest> requests = newArrayList(
        new EdgeRequest.Builder(ep1).setMessage(new EdgeVersatility.Builder("OFF").build()).build(),
        new EdgeRequest.Builder(ep2).setMessage(new EdgeVersatility.Builder(uint(12345)).build())
            .build(),
        new EdgeRequest.Builder(ep3).setMessage(new EdgeVersatility.Builder(uint(10000)).build())
            .build());
    EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_WRITE)
        .setMessageType(EdgeMessageType.SEND_REQUESTS).setRequests(requests).build();
    ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  public static void testGetEndpoint() throws Exception {
    EdgeEndpointConfig config = new EdgeEndpointConfig.Builder()
        .setApplicationName(EdgeOpcUaCommon.DEFAULT_SERVER_APP_NAME.getValue())
        .setApplicationUri(EdgeOpcUaCommon.DEFAULT_SERVER_APP_URI.getValue()).build();
    EdgeEndpointInfo ep = new EdgeEndpointInfo.Builder(endpointUri).setConfig(config).build();
    EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder().build();
    EdgeMessage msg = new EdgeMessage.Builder(ep).setCommand(EdgeCommandType.CMD_GET_ENDPOINTS)
        .setRequest(new EdgeRequest.Builder(nodeInfo).build()).build();
    ProtocolManager.getProtocolManagerInstance().send(msg);
  }

  @SuppressWarnings("resource")
  public static void startCommand() throws Exception {
    String operator = "";
    Scanner scanner = new Scanner(System.in);

    printMenu();
    init();

    while (true) {
      System.out.print("[INPUT COMMAND] : ");
      operator = scanner.next();
      if (operator.equals("start")) {
        System.out.println(ANSI_GREEN + "\n------------------------" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + " Start Client " + ANSI_RESET);
        System.out.println(ANSI_GREEN + "------------------------\n" + ANSI_RESET);

        System.out.print("[PLEASE INPUT SERVER ENDPOINT URI] : ");
        String ipAddress = scanner.next();
        endpointUri = ipAddress;
        epInfo = new EdgeEndpointInfo.Builder(endpointUri).build();
        testGetEndpoint();
        quitThread.start();
        startFlag = true;
      } else if (operator.equals("quit")) {
        terminate();
        break;
      } else if (operator.equals("read_s")) {
        testRead(EdgeOpcUaCommon.WELL_KNOWN_SERVER_NODE.getValue(),
            EdgeNodeIdentifier.Edge_Node_ServerInfo_Type);
      } else if (operator.equals("read_t")) {
        String sensor = "";
        System.out.print("[select sensor] : ");
        sensor = scanner.next();
        testRead(sensor, EdgeNodeIdentifier.Edge_Node_Custom_Type);
      } else if (operator.equals(EdgeCommandType.CMD_WRITE.getValue())) {
        System.out.print("[select sensor] : ");
        String sensor = scanner.next();

        System.out.print("[input variable to change] : ");
        Object valueToChange = scanner.next();

        System.out.println(ANSI_GREEN + "\n------------------------" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + " Write Req/Res " + ANSI_RESET);
        System.out.println(ANSI_GREEN + "------------------------\n" + ANSI_RESET);

        EdgeNodeInfo ep = new EdgeNodeInfo.Builder().setValueAlias(sensor).build();
        EdgeMessage writeMsg =
            new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_WRITE)
                .setRequest(new EdgeRequest.Builder(ep)
                    .setMessage(new EdgeVersatility.Builder(valueToChange).build()).build())
                .build();
        ProtocolManager.getProtocolManagerInstance().send(writeMsg);

      } else if (operator.equals(EdgeCommandType.CMD_SUB.getValue())) {
        System.out.println(ANSI_GREEN + "\n------------------------" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + " Sub Req/Res " + ANSI_RESET);
        System.out.println(ANSI_GREEN + "------------------------\n" + ANSI_RESET);
        testSub(EdgeNodeIdentifier.Edge_Node_Custom_Type);
      } else if (operator.equals("help")) {
        printMenu();
      } else if (operator.equals("browse")) {
        testBrowse();
      } else if (operator.equals("browse_m")) {
        testBrowses();
      } else if (operator.equals("sub_modify")) {
        testSubModification(EdgeSampleCommon.TARGET_NODE_CNC100.getValue());
      } else if (operator.equals("sub_delete")) {
        testSubDelete(EdgeSampleCommon.TARGET_NODE_CNC100.getValue());
      } else if (operator.equals("read_d")) {
        testRead(EdgeSampleCommon.KEY_URI_DA_TEMPORATURE1.getValue(),
            EdgeNodeIdentifier.Edge_Node_Class_Type);
      } else if (operator.equals("sub_d")) {
        testSub(EdgeNodeIdentifier.Edge_Node_Class_Type);
      } else if (operator.equals("method")) {
        EdgeNodeInfo ep = new EdgeNodeInfo.Builder()
            .setValueAlias(EdgeSampleCommon.KEY_URI_METHOD_SQRT.getValue()).build();
        EdgeMessage msg = new EdgeMessage.Builder(epInfo).setCommand(EdgeCommandType.CMD_METHOD)
            .setRequest(new EdgeRequest.Builder(ep)
                .setMessage(new EdgeVersatility.Builder(16.0).build()).build())
            .build();
        ProtocolManager.getProtocolManagerInstance().send(msg);
      } else if (operator.equals("auto")) {
        autoThread.start();
      } else if (operator.equals("endpoint")) {
        testGetEndpoint();
      } else if (operator.equals("read_gp")) {
        testReadGroup();
      } else if (operator.equals("write_gp")) {
        testWriteGroup();
      } else if (operator.equals("provider")) {
        printProvider();
      } else {
        System.out.println("[WARN] invaild input");
      }
    }
    logger.info("finished");
  }

  static ReceivedMessageCallback receiver = new ReceivedMessageCallback() {
    @Override
    public void onResponseMessages(EdgeMessage data) {
      logger.info("onResponseMessages size = {}", data.getResponses().size());
      if (data.getResponses() != null) {
        for (EdgeResponse res : data.getResponses()) {
          if (res.getMessage() != null) {
            logger.info("onResponseMessages = {}", res.getMessage().getValue());
          }
          EdgeDiagnosticInfo info = res.getEdgeDiagnosticInfo();
          if (info != null) {
            logger.info("diagnostic Info : {} {} {} {} {} msg : {}", info.getSymbolicId(),
                info.getLocalizedText(), info.getAdditionalInfo(), info.getInnerStatusCode(),
                info.gettInnerDiagnosticInfo(), info.getMsg());
          }
        }
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
        int requestId) {
      for (EdgeBrowseResult response : responses) {
        logger.info("onBrowseMessage : browseName={}, reqId={}", response.getBrowseName(),
            requestId);
      }
    }
  };

  static DiscoveryCallback discoveryCallback = new DiscoveryCallback() {
    @Override
    public void onFoundEndpoint(EdgeDevice device) {
      logger.info("[Event] onFoundEndpoint");
      boolean rsaFlag = false;
      for (EdgeEndpointInfo ep : device.getEndpoints()) {
        logger.info(" > uri={}, SecurePolicy={}, Address={}, Port={}, ServerName={}",
            ep.getEndpointUri(), ep.getConfig().getSecurityPolicyUri(), device.getAddress(),
            device.getPort(), device.getServerName());
        if (ep.getConfig().getSecurityPolicyUri().equalsIgnoreCase(defaultSecureType)) {
          rsaFlag = true;
        }
      }

      if (rsaFlag == true) {
        try {
          startClient(defaultSecureType);
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }

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

      if (methodProviders == null)
        methodProviders = new ArrayList<String>();

      if (attributeProviders == null)
        attributeProviders = new ArrayList<String>();

      if (viewProviders == null)
        viewProviders = new ArrayList<String>();

      logger.info("onStart: status={} from={}", status, ep.getEndpointUri());
      if (status == EdgeStatusCode.STATUS_CLIENT_STARTED) {
        System.out.println(ANSI_GREEN + "\n------------------------" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + " Get Provider " + ANSI_RESET);
        System.out.println(ANSI_GREEN + "------------------------\n" + ANSI_RESET);
        for (String value : attiributeAliasList) {
          logger.info("attribute node valueAlias: " + value);
          attributeProviders.add(value);
        }

        for (String value : methodAliasList) {
          logger.info("method node valueAlias : " + value);
          methodProviders.add(value);
        }

        for (String value : viewAliasList) {
          logger.info("view node valueAlias : " + value);
          viewProviders.add(value);
        }
      }
      if (autoFlag == true) {
        try {
          testRead(EdgeOpcUaCommon.DEFAULT_SERVER_NAME.getValue(),
              EdgeNodeIdentifier.Edge_Node_ServerInfo_Type);
          testRead(EdgeSampleCommon.KEY_URI_LINE_CNC14.getValue(),
              EdgeNodeIdentifier.Edge_Node_Custom_Type);

          testRead(EdgeSampleCommon.KEY_URI_DA_TEMPORATURE1.getValue(),
              EdgeNodeIdentifier.Edge_Node_Class_Type);

          testSub(EdgeNodeIdentifier.Edge_Node_Class_Type);
          testSub(EdgeNodeIdentifier.Edge_Node_Custom_Type);
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

  static private void printProvider() {
    for (String value : attributeProviders) {
      logger.info("attribute node valueAlias : " + value);
    }

    for (String value : methodProviders) {
      logger.info("method node valueAlias : " + value);
    }

    for (String value : viewProviders) {
      logger.info("view node valueAlias : " + value);
    }
  }

  static public void printMenu() {
    System.out.println("--------------------opc ua--------------------");
    System.out.println("start : start opcua client and connect / init Provider");
    System.out.println("quit : terminate/stop opcua server/client and then quit");
    System.out.println("provider : get access providers related node");
    System.out.println("browse : browse with root node");
    System.out.println("browse_m : multiple browse with several nodes");
    System.out.println("read_s : read attribute for server status");
    System.out.println("read_t : read attribute for target node");
    System.out.println("read_gp : group read attribute from nodes");
    System.out.println("write : write attribute into node");
    System.out.println("write_gp : group write attribute into nodes");
    System.out.println("sub : Subscription");
    System.out.println("sub_modify : Modify Subscription");
    System.out.println("sub_delete : Delete Subscription");
    System.out.println("endpoint : get endpoints(connectable session) from server");
    System.out.println("auto : run full function test automatically");
    System.out.println("help : print menu");
  }
}
