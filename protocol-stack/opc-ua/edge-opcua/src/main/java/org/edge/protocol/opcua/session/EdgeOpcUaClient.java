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

package org.edge.protocol.opcua.session;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.SessionActivityListener;
import org.eclipse.milo.opcua.sdk.client.api.UaSession;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeNodeId;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.command.EdgeBaseClient;
import org.edge.protocol.opcua.providers.EdgeAttributeProvider;
import org.edge.protocol.opcua.providers.EdgeProviderGenerator;
import org.edge.protocol.opcua.providers.EdgeServices;
import org.edge.protocol.opcua.providers.services.EdgeCustomService;
import org.edge.protocol.opcua.providers.services.EdgeGroupService;
import org.edge.protocol.opcua.providers.services.browse.EdgeBrowseService;
import org.edge.protocol.opcua.providers.services.method.EdgeMethodCaller;
import org.edge.protocol.opcua.providers.services.sub.EdgeMonitoredItemService;
import org.edge.protocol.opcua.queue.ErrorHandler;
import org.edge.protocol.opcua.session.auth.KeyStoreLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeOpcUaClient implements EdgeBaseClient {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final OpcUaClient client;
  private final String endpointUri;
  private String securityUri = null;
  private boolean viewNodeEnabled = true;

  public EdgeOpcUaClient(EdgeEndpointInfo epInfo) throws Exception {
    this.endpointUri = epInfo.getEndpointUri();
    this.client = configure(epInfo);
    this.viewNodeEnabled = epInfo.getConfig().getViewNodeFlag();
  }

  Thread connectThread = new Thread() {
    public void run() {
      try {
        connectWithActivityListener();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        ErrorHandler.getInstance().addErrorMessage(new EdgeNodeInfo.Builder().build(),
            new EdgeResult.Builder(EdgeStatusCode.STATUS_INTERNAL_ERROR).build(),
            EdgeOpcUaCommon.DEFAULT_REQUEST_ID);
      }
    };
  };

  Thread providerThread = new Thread() {
    public void run() {
      try {
        initEdgeProvider();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        ErrorHandler.getInstance().addErrorMessage(new EdgeNodeInfo.Builder().build(),
            new EdgeResult.Builder(EdgeStatusCode.STATUS_INTERNAL_ERROR).build(),
            EdgeOpcUaCommon.DEFAULT_REQUEST_ID);
      }
    };
  };

  SessionActivityListener listener = new SessionActivityListener() {
    @Override
    public void onSessionActive(UaSession session) {
      logger.info("onSessionActive session={}, {}", session.getSessionId(),
          session.getSessionName());

      providerThread.start();

      EdgeEndpointInfo ep = new EdgeEndpointInfo.Builder(endpointUri).build();
      ProtocolManager.getProtocolManagerInstance().onStatusCallback(ep,
          EdgeStatusCode.STATUS_CONNECTED);
    }

    @Override
    public void onSessionInactive(UaSession session) {
      logger.info("onSessionInactive session={}, {}", session.getSessionId(),
          session.getSessionName());
      deinitEdgeProvider();

      EdgeEndpointInfo ep = new EdgeEndpointInfo.Builder(endpointUri).build();
      ProtocolManager.getProtocolManagerInstance().onStatusCallback(ep,
          EdgeStatusCode.STATUS_DISCONNECTED);
    }
  };

  /**
   * @fn String getEndpoint()
   * @brief get endpoint uri information
   * @return endpoint uri
   */
  public String getEndpoint() {
    return endpointUri;
  }

  /**
   * @fn OpcUaClient getClientInstance()
   * @brief get client instance related Milo lib
   * @return client instance
   */
  public OpcUaClient getClientInstance() {
    return client;
  }

  private OpcUaClient configure(EdgeEndpointInfo ep) throws Exception {
    EndpointDescription[] endpoints = UaTcpStackClient.getEndpoints(ep.getEndpointUri()).get();

    for (int i = 0; i < endpoints.length; i++) {
      logger.info("endpoint={}, {}, {}", endpoints[i].getEndpointUrl(),
          endpoints[i].getSecurityLevel(), endpoints[i].getSecurityPolicyUri());
    }
    if (ep.getConfig() == null || ep.getConfig().getSecurityPolicyUri() == null) {
      securityUri = SecurityPolicy.None.getSecurityPolicyUri();
      logger.info("set default security policy -> {}", securityUri);
    } else {
      securityUri = ep.getConfig().getSecurityPolicyUri();
      logger.info("set security policy -> {}", securityUri);
    }

    EndpointDescription endpoint =
        Arrays.stream(endpoints).filter(e -> e.getSecurityPolicyUri().equals(securityUri))
            .findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));

    OpcUaClientConfig clientConfig = null;
    KeyStoreLoader loader = new KeyStoreLoader().load();

    if (ep.getConfig() == null) {
      clientConfig = OpcUaClientConfig.builder()
          .setApplicationName(
              LocalizedText.english(EdgeOpcUaCommon.DEFAULT_SERVER_APP_NAME.getValue()))
          .setApplicationUri(EdgeOpcUaCommon.DEFAULT_SERVER_APP_URI.getValue())
          .setEndpoint(endpoint).setCertificate(loader.getClientCertificate())
          .setKeyPair(loader.getClientKeyPair()).setRequestTimeout(uint(60000)).build();

    } else {
      clientConfig = OpcUaClientConfig.builder()
          .setApplicationName(LocalizedText.english(ep.getConfig().getApplicationName()))
          .setApplicationUri(ep.getConfig().getApplicationUri()).setEndpoint(endpoint)
          .setCertificate(loader.getClientCertificate()).setKeyPair(loader.getClientKeyPair())
          .setRequestTimeout(uint(ep.getConfig().getRequestTimeout())).build();
    }

    return new OpcUaClient(clientConfig);
  }

  private void connectWithActivityListener() throws Exception {
    logger.info("opcua connect");
    client.addSessionActivityListener(listener);
    client.connect().get();
  }

  private void registerCommonEdgeProvider() {
    EdgeAttributeProvider discoveryServiceProvider =
        new EdgeAttributeProvider(EdgeMonitoredItemService.getInstance(),
            EdgeBrowseService.getInstance()).registerAttributeService(
                EdgeOpcUaCommon.WELL_KNOWN_DISCOVERY.getValue(),
                new EdgeCustomService.Builder(0, EdgeOpcUaCommon.WELL_KNOWN_DISCOVERY.getValue())
                    .build());
    EdgeServices.registerAttributeProvider(EdgeOpcUaCommon.WELL_KNOWN_DISCOVERY.getValue(),
        discoveryServiceProvider);

    EdgeAttributeProvider groupServiceProvider =
        new EdgeAttributeProvider(EdgeGroupService.getInstance());
    EdgeServices.registerAttributeProvider(EdgeOpcUaCommon.WELL_KNOWN_GROUP.getValue(),
        groupServiceProvider);
  }

  /**
   * @fn void initEdgeProvider()
   * @brief initialize service provider generator
   * @return void
   */
  public void initEdgeProvider() {
    logger.info("initEdgeProvider");

    registerCommonEdgeProvider();

    EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder()
        .setEdgeNodeId(new EdgeNodeId.Builder(EdgeOpcUaCommon.SYSTEM_NAMESPACE_INDEX,
            EdgeNodeIdentifier.RootFolder).build())
        .setValueAlias(EdgeOpcUaCommon.WELL_KNOWN_DISCOVERY.getValue()).build();

    EdgeProviderGenerator.getInstance().initializeProvider(
        new NodeId(nodeInfo.getEdgeNodeID().getNameSpace(),
            nodeInfo.getEdgeNodeID().getEdgeNodeIdentifier().value()),
        null, NodeClass.Object, this, viewNodeEnabled);

    EdgeEndpointInfo ep = new EdgeEndpointInfo.Builder(endpointUri).build();
    ProtocolManager.getProtocolManagerInstance().onStatusCallback(ep,
        EdgeStatusCode.STATUS_CLIENT_STARTED);
  }

  /**
   * @fn void deinitEdgeProvider()
   * @brief de-initialize service provider generator
   * @return void
   */
  public void deinitEdgeProvider() {
    EdgeServices.removeMethodProvider();
    EdgeServices.removeAttributeProvider();
  }

  /**
   * @fn void connect(String endpoint) throws Exception
   * @brief connect to endpoint
   * @param [in] endpoint target endpoint
   * @return void
   */
  public void connect(String endpoint) throws Exception {
    connectWithActivityListener();
    // connectThread.start();
  }

  /**
   * @fn void disconnect() throws Exception
   * @brief connect to endpoint
   * @return void
   */
  public void disconnect() throws Exception {
    try {
      client.disconnect().get();
      logger.info("disconnected");
    } catch (InterruptedException | ExecutionException e) {
      logger.warn("Error disconnecting client.", e);
      ErrorHandler.getInstance().addErrorMessage(
          new EdgeResult.Builder(EdgeStatusCode.STATUS_INTERNAL_ERROR).build(),
          EdgeOpcUaCommon.DEFAULT_REQUEST_ID);
    }
  }

  /**
   * @fn void terminate() throws Exception
   * @brief close opcua client
   * @return void
   */
  public void terminate() throws Exception {
    EdgeMethodCaller.getInstance().close();
    EdgeProviderGenerator.getInstance().close();
  }
}
