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

import java.util.concurrent.ConcurrentMap;
import com.google.common.collect.ForwardingConcurrentMap;
import com.google.common.collect.MapMaker;

public class EdgeAbstractSessionMap extends ForwardingConcurrentMap<String, EdgeOpcUaClient>
    implements EdgeBaseSessionMap {
  private final ConcurrentMap<String, EdgeOpcUaClient> sessionMap;

  /* Create abstract session map with node */
  public EdgeAbstractSessionMap() {
    MapMaker mapMaker = new MapMaker();

    sessionMap = makeNodeMap(mapMaker);
  }

  /**
   * @fn ConcurrentMap<String, EdgeOpcUaClient> makeNodeMap(MapMaker mapMaker)
   * @brief get concurrent map
   * @param [in] String, EdgeOpcUaClient
   * @return map created by MapMaker
   */
  protected ConcurrentMap<String, EdgeOpcUaClient> makeNodeMap(MapMaker mapMaker) {
    return mapMaker.makeMap();
  }

  @Override
  protected final ConcurrentMap<String, EdgeOpcUaClient> delegate() {
    return sessionMap;
  }
}
