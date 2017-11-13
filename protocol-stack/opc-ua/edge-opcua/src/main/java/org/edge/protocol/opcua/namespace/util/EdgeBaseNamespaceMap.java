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
import org.edge.protocol.opcua.namespace.EdgeNamespace;

/**
 * This interface provide function for handle EdgeBaseNamespaceMap
 */
public interface EdgeBaseNamespaceMap extends ConcurrentMap<String, EdgeNamespace> {

  /**
   * @fn void addNode(EdgeNamespace namespace)
   * @brief add EdgeNamespace
   * @param [in] namespace
   * @return void
   */
  default void addNode(EdgeNamespace namespace) {
    put(namespace.getNamespaceUri(), namespace);
  }

  /**
   * @fn boolean containsSession(EdgeNamespace node)
   * @brief check contains sessions
   * @param [in] node
   * @return boolean
   */
  default boolean containsSession(EdgeNamespace node) {
    return containsEndpoint(node.getNamespaceUri());
  }

  /**
   * @fn boolean containsEndpoint(String id)
   * @brief check contains endpoint
   * @param [in] id
   * @return boolean
   */
  default boolean containsEndpoint(String id) {
    return containsKey(id);
  }

  /**
   * @fn Optional<EdgeNamespace> getNode(String id)
   * @param [in] id
   * @return Optional<EdgeNamespace>
   */
  default Optional<EdgeNamespace> getNode(String id) {
    return Optional.ofNullable(get(id));
  }

  /**
   * @fn Optional<EdgeNamespace> removeNode(String id)
   * @param [in] id
   * @return Optional<EdgeNamespace>
   */
  default Optional<EdgeNamespace> removeNode(String id) {
    return Optional.ofNullable(remove(id));
  }

}
