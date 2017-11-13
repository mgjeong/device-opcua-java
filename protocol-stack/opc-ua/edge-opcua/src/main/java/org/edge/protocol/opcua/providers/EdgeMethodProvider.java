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

package org.edge.protocol.opcua.providers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.edge.protocol.opcua.providers.services.browse.EdgeBrowseService;
import org.edge.protocol.opcua.providers.services.method.EdgeMethodService;
import org.edge.protocol.opcua.providers.services.sub.EdgeMonitoredItemService;

public class EdgeMethodProvider extends EdgeBaseProvider {
  private Map<String, EdgeMethodService> methodServices;

  /**
   * @fn EdgeMethodProvider(EdgeMonitoredItemService monitor, EdgeBrowseService browse)
   * @brief constructor
   * @prarm [in] monitor EdgeMonitoredItemService instance
   * @prarm [in] browse EdgeBrowseService instance
   * @return void
   */
  public EdgeMethodProvider(EdgeMonitoredItemService monitor, EdgeBrowseService browse) {
    super(monitor, browse);
    this.methodServices = null;
  }

  /**
   * @fn EdgeMethodProvider registerMethodService(String name, EdgeMethodService method)
   * @brief register Method Service
   * @prarm [in] name provider key value
   * @param [in] method EdgeMethodService instance
   * @return void
   */
  public EdgeMethodProvider registerMethodService(String name, EdgeMethodService method) {
    if (methodServices == null)
      methodServices = new ConcurrentHashMap<String, EdgeMethodService>();
    methodServices.put(name, method);
    return this;
  }

  /**
   * @fn EdgeMethodService getMethodService(String name)
   * @brief get EdgeMethodService instance
   * @prarm [in] name provider key value
   * @return EdgeMethodService instance
   */
  public EdgeMethodService getMethodService(String name) {
    EdgeMethodService method = null;
    try {
      method = methodServices.get(name);
    } catch (Exception e) {
      throw new IllegalArgumentException("no service registered with name: " + name);
    }
    return method;
  }
}
