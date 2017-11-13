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
import org.edge.protocol.opcua.providers.services.browse.EdgeViewService;
import org.edge.protocol.opcua.providers.services.sub.EdgeMonitoredItemService;

public class EdgeViewProvider extends EdgeBaseProvider {
  private Map<String, EdgeViewService> viewServices;

  /**
   * @fn EdgeViewProvider(EdgeMonitoredItemService monitor, EdgeBrowseService browse)
   * @brief constructor
   * @prarm [in] monitor EdgeMonitoredItemService instance
   * @prarm [in] browse EdgeBrowseService instance
   * @return void
   */
  public EdgeViewProvider(EdgeMonitoredItemService monitor, EdgeBrowseService browse) {
    super(monitor, browse);
    this.viewServices = null;
  }

  /**
   * @fn EdgeViewProvider registerViewService(String name, EdgeViewService view)
   * @brief register View Service
   * @prarm [in] name provider key value
   * @param [in] view EdgeViewService instance
   * @return void
   */
  public EdgeViewProvider registerViewService(String name, EdgeViewService view) {
    if (viewServices == null)
      viewServices = new ConcurrentHashMap<String, EdgeViewService>();
    viewServices.put(name, view);
    return this;
  }

  /**
   * @fn EdgeViewService getViewService(String name)
   * @brief get EdgeViewService instance
   * @prarm [in] name provider key value
   * @return EdgeViewService instance
   */
  public EdgeViewService getViewService(String name) {
    EdgeViewService view = null;
    try {
      view = viewServices.get(name);
    } catch (Exception e) {
      throw new IllegalArgumentException("no service registered with name: " + name);
    }
    return view;
  }
}
