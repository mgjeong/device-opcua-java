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

import static com.google.common.collect.Lists.newArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.sdk.server.identity.CompositeValidator;
import org.eclipse.milo.opcua.sdk.server.identity.UsernameIdentityValidator;
import org.eclipse.milo.opcua.sdk.server.identity.X509IdentityValidator;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.structured.BuildInfo;
import org.eclipse.milo.opcua.stack.core.types.structured.UserTokenPolicy;
import org.eclipse.milo.opcua.stack.server.tcp.SocketServers;
import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.common.EdgeEndpointConfig;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeNodeId;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.namespace.EdgeNamespace;
import org.edge.protocol.opcua.namespace.EdgeNamespaceManager;
import org.edge.protocol.opcua.node.loader.EdgeDataAccessLoader;
import org.edge.protocol.opcua.providers.services.method.EdgeMethodCaller;
import org.edge.protocol.opcua.session.auth.KeyStoreLoader;
import org.edge.protocol.opcua.session.auth.TestCertificateManager;
import org.edge.protocol.opcua.session.auth.TestCertificateValidator;

public class EdgeOpcUaServer {

  private OpcUaServer server;
  private int NameSpaceType = EdgeOpcUaCommon.DEFAULT_TYPE;
  private EdgeNamespace nameSpace = null;
  private static EdgeOpcUaServer edgeServer = null;
  private static Object lock = new Object();

  private EdgeOpcUaServer() {}

  /**
   * @fn EdgeOpcUaServer getInstance()
   * @brief get opcua server instance
   * @return server instance
   */
  public static EdgeOpcUaServer getInstance() {
    synchronized (lock) {
      if (null == edgeServer) {
        edgeServer = new EdgeOpcUaServer();
      }
      return edgeServer;
    }
  }

  /**
   * @fn close
   * @brief close server instance
   * @return void
   */
  public void close() {
    edgeServer = null;
  }

  /**
   * @fn void start()
   * @brief start server with default configuration data
   * @param [in] epInfo EdgeEndpointInfo
   * @return void
   */
  public EdgeResult start(EdgeEndpointInfo epInfo) throws Exception {
    EdgeEndpointConfig config = epInfo.getConfig();

    // allow anonymous access
    UsernameIdentityValidator usernameValidator = new UsernameIdentityValidator(true, challenge -> {
      String user0 = "user1";
      String pass0 = "password";

      char[] cs = new char[1000];
      Arrays.fill(cs, 'a');
      String user1 = new String(cs);
      String pass1 = new String(cs);

      boolean match0 =
          user0.equals(challenge.getUsername()) && pass0.equals(challenge.getPassword());

      boolean match1 =
          user1.equals(challenge.getUsername()) && pass1.equals(challenge.getPassword());

      return match0 || match1;
    });

    X509IdentityValidator x509IdentityValidator = new X509IdentityValidator(c -> true);

    List<UserTokenPolicy> userTokenPolicies =
        newArrayList(OpcUaServerConfig.USER_TOKEN_POLICY_ANONYMOUS,
            OpcUaServerConfig.USER_TOKEN_POLICY_USERNAME, OpcUaServerConfig.USER_TOKEN_POLICY_X509);

    KeyStoreLoader loader = new KeyStoreLoader().load();

    TestCertificateManager certificateManager =
        new TestCertificateManager(loader.getServerKeyPair(), loader.getServerCertificate());

    TestCertificateValidator certificateValidator =
        new TestCertificateValidator(loader.getClientCertificate());

    BuildInfo buildInfo = new BuildInfo("/edge", "samsung", "edgeSolution", "0.9", "0.1", null);

    OpcUaServerConfig serverConfig = OpcUaServerConfig.builder()
        .setApplicationName(LocalizedText.english(config.getApplicationName()))
        .setApplicationUri(config.getApplicationUri())
        .setBindAddresses(newArrayList(config.getBindAddress())).setBindPort(config.getBindPort())
        .setCertificateManager(certificateManager).setCertificateValidator(certificateValidator)
        .setSecurityPolicies(EnumSet.of(SecurityPolicy.None, SecurityPolicy.Basic128Rsa15))
        .setProductUri(config.getProductUri()).setServerName(config.getServerName())
        .setUserTokenPolicies(userTokenPolicies).setBuildInfo(buildInfo)
        .setIdentityValidator(new CompositeValidator(usernameValidator, x509IdentityValidator))
        .build();

    server = new OpcUaServer(serverConfig);
    server.startup().thenApply(sub -> {
      ProtocolManager.getProtocolManagerInstance().onStatusCallback(epInfo,
          EdgeStatusCode.STATUS_SERVER_STARTED);
      return new EdgeResult.Builder(EdgeStatusCode.STATUS_OK).build();
    }).exceptionally(e -> {

      return null;
    });
    return new EdgeResult.Builder(EdgeStatusCode.STATUS_OK).build();
  }

  /**
   * @fn void stop()
   * @brief stop server
   * @return void
   */
  public void stop() throws InterruptedException, ExecutionException {
    server.shutdown().get();
    SocketServers.shutdownAll().get();
  }

  /**
   * @fn void createNamespace(String namespaceUri, String rootNodeIdentifier, String
   *     rootNodeBrowseName, String rootNodeDisplayName)
   * @brief create Namespace in server side
   * @param [in] namespaceUri namespace alias to use
   * @param [in] rootNodeIdentifier path name in root node
   * @param [in] rootNodeBrowseName browse name in root node
   * @param [in] rootNodeDisplayName display name in root node
   * @return void
   */
  public void createNamespace(String namespaceUri, String rootNodeIdentifier,
      String rootNodeBrowseName, String rootNodeDisplayName) throws Exception {
    if (EdgeOpcUaCommon.URI_TYPE == NameSpaceType
        || EdgeOpcUaCommon.DEFAULT_TYPE == NameSpaceType) {
      // register a CttNamespace so we have some nodes to play with
      registerNamesapece(namespaceUri, rootNodeIdentifier, rootNodeBrowseName, rootNodeDisplayName);
    }
  }

  private void registerNamesapece(String namespaceUri, String rootNodeId, String rootBrowseName,
      String rootDisplayName) {
    server.getNamespaceManager().registerAndAdd(namespaceUri,
        // Lambda Expression
        (UShort idx) -> {
          nameSpace = new EdgeNamespace.Builder(server, idx, namespaceUri).setNodeId(rootNodeId)
              .setBrowseName(rootBrowseName).setDisplayName(rootDisplayName).build();
          try {
            EdgeNamespaceManager.getInstance().addNamespace(namespaceUri, nameSpace);
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          return nameSpace;
        });
  }

  public List<EdgeNodeId> getNodes() {
    return EdgeNamespaceManager.getInstance().getNodes(server);
  }

  public List<EdgeNodeId> getNodes(String BrowseName) {
    return EdgeNamespaceManager.getInstance().getNodes(server, BrowseName);
  }

  /**
   * @fn void terminate()
   * @brief close static classes in server side
   * @return void
   */
  public void terminate() throws Exception {
    EdgeDataAccessLoader.getInstance().close();
    EdgeNamespaceManager.getInstance().close();
    EdgeMethodCaller.getInstance().close();
  }
}
