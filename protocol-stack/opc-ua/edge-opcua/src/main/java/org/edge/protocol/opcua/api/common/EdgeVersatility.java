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

package org.edge.protocol.opcua.api.common;

import java.sql.Date;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

public class EdgeVersatility {
  private final Object value;

  public static class Builder {
    private final Object value;

    public Builder(Object value) {
      try {
        getClassType(value);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      this.value = value;
    }

    private boolean isArray(Object value) {
      return value.getClass().isArray();
    }

    private Class<?> getClassType(Object value) throws Exception {
      Class<?> classType = isArray(value) ? value.getClass().getComponentType() : value.getClass();
      if (EdgeVersatility.class.equals(classType)) {
        throw new IllegalArgumentException("EdgeVersatility cannot contain itself");
      }

      return classType;
    }

    /**
     * @fn EdgeVersatility build()
     * @brief create EdgeVersatility instance (builder)
     * @return EdgeVersatility instance
     */
    public EdgeVersatility build() {
      return new EdgeVersatility(this);
    }
  }

  /**
   * @fn EdgeVersatility(Builder builder)
   * @brief constructor
   * @param [in] builder EdgeVersatility Builder
   */
  private EdgeVersatility(Builder builder) {
    value = builder.value;
  }

  /**
   * @fn EdgeNodeIdentifier getVariableType()
   * @brief get Variable Type
   * @param [in] builder EdgeVersatility Builder
   * @return Node Identifier
   */
  public EdgeNodeIdentifier getVariableType() {
    return BUILT_IN_TYPES.get(value.getClass());
  }

  /**
   * @fn boolean isNull()
   * @brief check whether the value is null or not
   * @return true(null) or false(not null)
   */
  public boolean isNull() {
    return value == null;
  }
  
  /**
   * @fn Object getValue()
   * @brief get value
   * @return value
   */
  public Object getValue() {
    return value;
  }

  private final BiMap<Class<?>, EdgeNodeIdentifier> BUILT_IN_TYPES =
      ImmutableBiMap.<Class<?>, EdgeNodeIdentifier>builder()
          .put(Long.class, EdgeNodeIdentifier.Int64).put(Float.class, EdgeNodeIdentifier.Float)
          .put(Double.class, EdgeNodeIdentifier.Double).put(String.class, EdgeNodeIdentifier.String)
          .put(Boolean.class, EdgeNodeIdentifier.Boolean).put(Byte.class, EdgeNodeIdentifier.Byte)
          .put(Short.class, EdgeNodeIdentifier.Int16).put(Integer.class, EdgeNodeIdentifier.Integer)
          .put(Date.class, EdgeNodeIdentifier.Date).build();
}
