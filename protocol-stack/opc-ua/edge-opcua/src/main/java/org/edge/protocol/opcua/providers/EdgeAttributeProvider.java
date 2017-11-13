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
import org.edge.protocol.opcua.providers.services.EdgeGroupService;
import org.edge.protocol.opcua.providers.services.browse.EdgeBrowseService;
import org.edge.protocol.opcua.providers.services.da.EdgeAttributeService;
import org.edge.protocol.opcua.providers.services.sub.EdgeMonitoredItemService;

public class EdgeAttributeProvider extends EdgeBaseProvider {

  private Map<String, EdgeAttributeService> attributeServices;

  /**
   * @fn EdgeAttributeProvider(EdgeMonitoredItemService monitor, EdgeBrowseService browse)
   * @brief constructor
   * @prarm [in] monitor EdgeMonitoredItemService instance
   * @prarm [in] browse EdgeBrowseService instance
   * @return void
   */
  public EdgeAttributeProvider(EdgeMonitoredItemService monitor, EdgeBrowseService browse) {
    super(monitor, browse);
    this.attributeServices = null;
  }

  /**
   * @fn EdgeAttributeProvider(EdgeGroupService group)
   * @brief constructor
   * @prarm [in] group EdgeGroupService instance
   * @return void
   */
  public EdgeAttributeProvider(EdgeGroupService group) {
    super(group);
    this.attributeServices = null;
  }

  /**
   * @fn EdgeAttributeProvider registerAttributeService(String name, EdgeAttributeService method)
   * @brief register Attribute Service
   * @prarm [in] name provider key value
   * @param [in] method EdgeAttributeService instance
   * @return void
   */
  public EdgeAttributeProvider registerAttributeService(String name, EdgeAttributeService attr) {
    if (attributeServices == null)
      attributeServices = new ConcurrentHashMap<String, EdgeAttributeService>();
    attributeServices.put(name, attr);
    return this;
  }

  /**
   * @fn EdgeAttributeService getAttributeService(String name)
   * @brief get EdgeAttributeService instance
   * @prarm [in] name provider key value
   * @return EdgeAttributeService instance
   */
  public EdgeAttributeService getAttributeService(String name) {
    EdgeAttributeService attr = null;
    try {
      attr = attributeServices.get(name);
    } catch (NullPointerException e) {
      throw new IllegalArgumentException("no service registered with name: " + name);
    }
    return attr;
  }
}
