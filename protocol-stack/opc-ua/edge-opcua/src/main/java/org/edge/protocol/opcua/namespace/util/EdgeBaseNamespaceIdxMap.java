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

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;

/**
 * This interface provide function for namespace index map
 */
public interface EdgeBaseNamespaceIdxMap extends ConcurrentMap<Integer, UShort> {

   /**
    * @fn void addNode(Integer type, UShort idx)
    * @brief add node
    * @param type
    * @param idx
    * @return void
    */
  default void addNode(Integer type, UShort idx) {
    put(type, idx);
  }

  /**
   * @fn containsNodeType(Integer type)
   * @brief check contains node type
   * @param type
   * @return
   */
  default boolean containsNodeType(Integer type) {
    return containsKey(type);
  }

  /**
   * @fn Optional<UShort> getIndex(Integer type)
   * @brief get index
   * @param type
   */
  default Optional<UShort> getIndex(Integer type) {
    return Optional.ofNullable(get(type));
  }

  /**
   * @fn Optional<UShort> removeIndex(Integer type)
   * @brief remove index
   * @param type
   */
  default Optional<UShort> removeIndex(Integer type) {
    return Optional.ofNullable(remove(type));
  }

}
