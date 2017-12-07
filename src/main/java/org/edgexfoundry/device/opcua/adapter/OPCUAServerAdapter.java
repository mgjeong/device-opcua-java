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

package org.edgexfoundry.device.opcua.adapter;

import org.edge.protocol.opcua.api.common.EdgeEndpointConfig;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.example.EdgeEmulator;
import org.edgexfoundry.device.opcua.adapter.metadata.OPCUADefaultMetaData;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Protocol;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;

public class OPCUAServerAdapter {
  private final static EdgeXLogger logger =
      EdgeXLoggerFactory.getEdgeXLogger(OPCUAServerAdapter.class);
  private EdgeEmulator opcUaEmulator;
  private static OPCUAServerAdapter singleton = null;
  private String endpointUri = null;
  private Addressable addressable = null;

  /**
   * @fn OPCUAServerAdapter()
   * @brief constructor
   */
  private OPCUAServerAdapter() {
    opcUaEmulator = new EdgeEmulator();
  }

  /**
   * @fn OPCUAServerAdapter getInstance()
   * @brief Get OPCUAServerAdapter Instance (singleton)
   * @return @OPCUAServerAdapter
   */
  public synchronized static OPCUAServerAdapter getInstance() {

    if (singleton == null) {
      singleton = new OPCUAServerAdapter();
    }

    return singleton;
  }

  /**
   * @fn void runServer() throws Exception
   * @brief Run OPCUA Server
   */
  public void runServer() throws Exception {
    // Create Namespace and nodes
    opcUaEmulator.runAutoUpdateServer();
    opcUaEmulator.initServerNodes();
  }

  /**
   * @fn void startOPCUAAdapter() throws Exception
   * @brief Start OPCUA Adapter
   */
  public void startOPCUAAdapter() throws Exception {
    // 1. run discovery device
    // TODO
    // we need to support like discovery-seed micro-service

    // 2. create addressable (by default)
    addressable = new Addressable(OPCUADefaultMetaData.NAME.getValue(), Protocol.TCP,
        OPCUADefaultMetaData.ADDRESS.getValue(), OPCUADefaultMetaData.PATH.getValue(),
        OPCUADefaultMetaData.ADDRESSABLE_PORT);

    // 3. get EdgeEndpoint URI
    endpointUri = getEndpointUrifromAddressable(addressable);

    // 4. startServer
    EdgeEndpointConfig endpointConfig = new EdgeEndpointConfig.Builder()
        .setApplicationName(EdgeOpcUaCommon.DEFAULT_SERVER_APP_NAME.getValue())
        .setApplicationUri(EdgeOpcUaCommon.DEFAULT_SERVER_APP_URI.getValue()).build();
    EdgeEndpointInfo ep =
        new EdgeEndpointInfo.Builder(endpointUri).setConfig(endpointConfig).build();
    opcUaEmulator.startServer(ep, "");
  }

  /**
   * @fn String getEndpointUrifromAddressable(Addressable addressable)
   * @brief Get EndpointUri from Addressable 
   * @param [in] addressable @Addressable
   * @return @String
   */
  private String getEndpointUrifromAddressable(Addressable addressable) {
    String endpointUri = "";
    if (addressable.getProtocol() == Protocol.TCP) {
      endpointUri += String.format("%s", "opc.tcp://");
    } else {
      endpointUri += String.format("%s", "http://");
    }

    endpointUri += String.format("%s:%d/%s", addressable.getAddress(), addressable.getPort(),
        addressable.getPath());
    return endpointUri;
  }
}
