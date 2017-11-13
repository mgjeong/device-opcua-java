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
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import com.google.common.collect.ForwardingConcurrentMap;
import com.google.common.collect.MapMaker;

/**
 * This class provide function for namespace index map
 */
public class EdgeAbstractNamespaceIdxMap extends ForwardingConcurrentMap<Integer, UShort>
    implements EdgeBaseNamespaceIdxMap {
  private final ConcurrentMap<Integer, UShort> namespaceIdxMap;

  /**
   * CTOR
   */
  public EdgeAbstractNamespaceIdxMap() {
    MapMaker mapMaker = new MapMaker();

    namespaceIdxMap = makeNodeMap(mapMaker);
  }

  /**
   * @fn ConcurrentMap<Integer, UShort> makeNodeMap(MapMaker mapMaker)
   * @brief make node mp
   * @param [in] mapMaker
   * @return
   */
  protected ConcurrentMap<Integer, UShort> makeNodeMap(MapMaker mapMaker) {
    return mapMaker.makeMap();
  }

  /**
   * @fn ConcurrentMap<Integer, UShort> delegate()
   * @brief get namespace index map
   * @return ConcurrentMap<Integer, UShort>
   */
  @Override
  protected final ConcurrentMap<Integer, UShort> delegate() {
    return namespaceIdxMap;
  }
}
