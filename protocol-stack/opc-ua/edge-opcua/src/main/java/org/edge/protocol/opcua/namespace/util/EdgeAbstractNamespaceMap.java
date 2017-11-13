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

package org.edge.protocol.opcua.namespace.util;

import java.util.concurrent.ConcurrentMap;
import org.edge.protocol.opcua.namespace.EdgeNamespace;
import com.google.common.collect.ForwardingConcurrentMap;
import com.google.common.collect.MapMaker;

/**
 * This class provide function for namespace map
 */
public class EdgeAbstractNamespaceMap extends ForwardingConcurrentMap<String, EdgeNamespace>
    implements EdgeBaseNamespaceMap {
  private final ConcurrentMap<String, EdgeNamespace> namespaceMap;

  /**
   * CTOR
   */
  public EdgeAbstractNamespaceMap() {
    MapMaker mapMaker = new MapMaker();

    namespaceMap = makeNodeMap(mapMaker);
  }

  /**
   * @fn ConcurrentMap<String, EdgeNamespace> makeNodeMap(MapMaker mapMaker)
   * @brief make node map
   * @param [in] mapMaker
   * @return ConcurrentMap<String, EdgeNamespace>
   */
  protected ConcurrentMap<String, EdgeNamespace> makeNodeMap(MapMaker mapMaker) {
    return mapMaker.makeMap();
  }

  /**
   * @fn ConcurrentMap<String, EdgeNamespace> delegate()
   * @brief get namespace map
   * @return ConcurrentMap<Integer, UShort>
   */
  @Override
  protected final ConcurrentMap<String, EdgeNamespace> delegate() {
    return namespaceMap;
  }
}
