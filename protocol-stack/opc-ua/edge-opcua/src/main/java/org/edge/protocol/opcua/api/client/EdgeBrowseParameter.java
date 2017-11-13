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

package org.edge.protocol.opcua.api.client;

public class EdgeBrowseParameter {
  public static int DIRECTION_FORWARD = 0;
  public static int DIRECTION_INVERSE = 1;
  public static int DIRECTION_BOTH = 2;
  int direction;
  int maxReferencesPerNode;

  public static class Builder {
    int direction = DIRECTION_FORWARD;
    int maxReferencesPerNode = 0;

    public Builder() {}

    /**
     * @fn Builder setDirection(int direction)
     * @brief set browsing direction
     * @param [in] direction direction information
     * @return this
     */
    public Builder setDirection(int direction) {
      this.direction = direction;
      return this;
    }

    /**
     * @fn Builder setMaxReferencesPerNode(int maxReferencesPerNode)
     * @brief set MaxReferencesPerNode
     * @param [in] maxReferencesPerNode
     * @return this
     */
    public Builder setMaxReferencesPerNode(int maxReferencesPerNode) {
      this.maxReferencesPerNode = maxReferencesPerNode;
      return this;
    }

    /**
     * @fn EdgeBrowseParameter build()
     * @brief create EdgeBrowseParameter instance (builder)
     * @return EdgeBrowseParameter instance
     */
    public EdgeBrowseParameter build() {
      return new EdgeBrowseParameter(this);
    }
  }

  /**
   * @fn EdgeBrowseParameter(Builder builder)
   * @brief constructor
   * @param [in] builder EdgeBrowseParameter Builder
   */
  private EdgeBrowseParameter(Builder builder) {
    direction = builder.direction;
    maxReferencesPerNode = builder.maxReferencesPerNode;
  }

  /**
   * @fn int getDirection()
   * @brief get direction
   * @return direction
   */
  public int getDirection() {
    return direction;
  }

  /**
   * @fn int getMaxReferenceValue()
   * @brief get maxReferencesPer
   * @return maxReferencesPerNode
   */
  public int getMaxReferenceValue() {
    return maxReferencesPerNode;
  }

  /**
   * @fn boolean isMaxReferenceValue()
   * @brief get maxReferencesPerFlag
   * @return true or false
   */
  public boolean isMaxReferenceValue() {
    return (maxReferencesPerNode != 0);
  }
}
