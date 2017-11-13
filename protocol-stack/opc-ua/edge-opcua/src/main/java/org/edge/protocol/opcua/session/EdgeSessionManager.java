/******************************************************************
 *
 * Copyright 2017 Samsung Electronics All Rights Reserved.
 *
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************/

package org.edge.protocol.opcua.session;

import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;

public class EdgeSessionManager {
  private final EdgeBaseSessionMap sessionMap = new EdgeSessionMap();
  private static EdgeSessionManager session = null;
  private static Object lock = new Object();

  private EdgeSessionManager() {}

  /**
   * @fn EdgeSessionManager getInstance()
   * @brief get session manager instance
   * @return EdgeSessionManager instance
   */
  public static EdgeSessionManager getInstance() {
    synchronized (lock) {
      if (null == session) {
        session = new EdgeSessionManager();
      }
      return session;
    }
  }

  /**
   * @fn void close()
   * @brief close session manager instance
   * @return void
   */
  public void close() {
    session = null;
  }

  /**
   * @fn void configure(EdgeEndpointInfo ep)
   * @brief insert EdgeOpcUaClient instance with endpoint uri(key)
   * @param [in] ep EdgeEndpoint which has configuration information
   * @return void
   */
  public void configure(EdgeEndpointInfo ep) throws Exception {
    if (false == sessionMap.containsKey(ep.getEndpointUri())) {
      sessionMap.put(ep.getEndpointUri(), new EdgeOpcUaClient(ep));
    }
  }

  /**
   * @fn void connect(String endpoint)
   * @brief connect to endpoint (endpoint should be contained in sessionMap)
   * @param [in] endpoint endpoint uri
   * @return void
   */
  public void connect(String endpoint) {
    EdgeOpcUaClient client = sessionMap.getNode(endpoint).get();

    try {
      client.connect(endpoint);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * @fn void disconnect(String endpoint)
   * @brief disconnect to endpoint (endpoint should be contained in sessionMap)
   * @param [in] endpoint endpoint uri
   * @return void
   */
  public void disconnect(String endpoint) {
    EdgeOpcUaClient client = sessionMap.getNode(endpoint).get();

    try {
      client.disconnect();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * @fn EdgeOpcUaClient getSession(String endpoint)
   * @brief get EdgeOpcUaClient instance from sessionMap
   * @param [in] endpoint endpoint uri
   * @return EdgeOpcUaClient instance
   */
  public EdgeOpcUaClient getSession(String endpoint) {
    if (sessionMap.getNode(endpoint).isPresent() == true) {
      return sessionMap.getNode(endpoint).get();
    }
    return null;
  }

  private static class EdgeSessionMap extends EdgeAbstractSessionMap {
    // implement edge session map class
  }
}
