/******************************************************************
 *
 * Copyright 2017 Samsung Electronics All Rights Reserved.
 *
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 ******************************************************************/

package org.edge.protocol.opcua.api.client;

import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;

public class EdgeSubRequest {
  private boolean enableCTT;
  private final EdgeNodeIdentifier subType;
  private double samplingInterval;
  private double publishingInterval;
  private int lifetimeCount;
  private int maxKeepAliveCount;
  private int maxNotificationsPerPublish;
  private boolean publishingEnabled;
  private byte priority;
  private int queueSize;

  public static class Builder {
    private boolean enableCTT = false;
    private final EdgeNodeIdentifier subType;
    private double samplingInterval = 1000.0;
    private double publishingInterval = 0.0;
    // KeepAlive every ~10-12s or every publishingInterval if longer.
    private int maxKeepAliveCount = Math.max(1, (int) Math.ceil(10000.0 / publishingInterval));
    // Lifetime must be 3x (or greater) the keep-alive count.
    private int lifetimeCount = maxKeepAliveCount * 6;
    private int maxNotificationsPerPublish = 1;
    private boolean publishingEnabled = true;
    private byte priority = 0;
    private int queueSize = 50;

    public Builder(EdgeNodeIdentifier subType) {
      this.subType = subType;
    }

    /**
     * @fn Builder setSamplingInterval(int value)
     * @brief set Sampling Interval
     * @param [in] value Sampling Interval
     * @return this
     */
    public Builder setSamplingInterval(double value) {
      this.samplingInterval = value;
      return this;
    }

    /**
     * @fn Builder setLifetimeCount(int value)
     * @brief set LifetimeCount
     * @param [in] value LifetimeCount
     * @return this
     */
    private Builder setLifetimeCount(int value) {
      this.lifetimeCount = value;
      return this;
    }

    /**
     * @fn Builder setMaxKeepAliveCount(int value)
     * @brief set MaxKeepAliveCount
     * @param [in] value MaxKeepAliveCount
     * @return this
     */
    private Builder setMaxKeepAliveCount(int value) {
      this.maxKeepAliveCount = value;
      return this;
    }

    /**
     * @fn Builder setMaxNotificationsPerPublish(int value)
     * @brief set MaxNotificationsPerPublish (modify only)
     * @param [in] value MaxNotificationsPerPublish
     * @return this
     */
    public Builder setMaxNotificationsPerPublish(int value) {
      this.maxNotificationsPerPublish = value;
      return this;
    }

    /**
     * @fn Builder setPublishingFlag(boolean value)
     * @brief set PublishingFlag (modify or setPublish only)
     * @param [in] value PublishingFlag
     * @return this
     */
    public Builder setPublishingFlag(boolean value) {
      this.publishingEnabled = value;
      return this;
    }

    /**
     * @fn Builder setPriority(byte value)
     * @brief set Priority (modify only)
     * @param [in] value Priority
     * @return this
     */
    public Builder setPriority(byte value) {
      this.priority = value;
      return this;
    }

    /**
     * @fn Builder setQueueSize(int value)
     * @brief set Queue Size
     * @param [in] value Queue Size
     * @return this
     */
    public Builder setQueueSize(int value) {
      this.queueSize = value;
      return this;
    }

    /**
     * @fn EdgeSubRequest build()
     * @brief create EdgeSubRequest instance (builder)
     * @return EdgeSubRequest instance
     */
    public EdgeSubRequest build() {
      return new EdgeSubRequest(this);
    }
  }

  /**
   * @fn EdgeSubRequest(Builder builder)
   * @brief constructor
   * @param [in] builder EdgeSubRequest Builder
   */
  private EdgeSubRequest(Builder builder) {
    enableCTT = builder.enableCTT;
    samplingInterval = builder.samplingInterval;
    subType = builder.subType;
    publishingInterval = builder.publishingInterval;
    lifetimeCount = builder.lifetimeCount;
    maxKeepAliveCount = builder.maxKeepAliveCount;
    maxNotificationsPerPublish = builder.maxNotificationsPerPublish;
    publishingEnabled = builder.publishingEnabled;
    priority = builder.priority;
    queueSize = builder.queueSize;
  }

  /**
   * @fn double getSamplingInterval()
   * @brief get samplingInterval
   * @return samplingInterval
   */
  public double getSamplingInterval() {
    return samplingInterval;
  }

  /**
   * @fn EdgeNodeIdentifier getSubType()
   * @brief get subscription Type (create, modify, delete)
   * @return subType
   */
  public EdgeNodeIdentifier getSubType() {
    return subType;
  }

  /**
   * @fn double getPublishingInterval()
   * @brief get publishingInterval
   * @return publishingInterval
   */
  public double getPublishingInterval() {
    return publishingInterval;
  }

  /**
   * @fn int getLifetimeCount()
   * @brief get lifetimeCount
   * @return lifetimeCount
   */
  public int getLifetimeCount() {
    return maxKeepAliveCount * 6;
  }

  /**
   * @fn int getMaxKeepAliveCount()
   * @brief get maxKeepAliveCount
   * @return maxKeepAliveCount
   */
  public int getMaxKeepAliveCount() {
    return Math.max(1, (int) Math.ceil(10000.0 / publishingInterval));
  }

  /**
   * @fn int getMaxNotificationsPerPublish()
   * @brief get maxNotificationsPerPublish
   * @return maxNotificationsPerPublish
   */
  public int getMaxNotificationsPerPublish() {
    return maxNotificationsPerPublish;
  }

  /**
   * @fn boolean getPublishingFlag()
   * @brief get publishingEnabled
   * @return publishingEnabled
   */
  public boolean getPublishingFlag() {
    return publishingEnabled;
  }

  /**
   * @fn byte getPriority()
   * @brief get priority
   * @return priority
   */
  public byte getPriority() {
    return priority;
  }

  /**
   * @fn int getQueueSize()
   * @brief get queue size
   * @return queueSize.
   */
  public int getQueueSize() {
    return queueSize;
  }

  /**
   * @fn boolean getCTTFlag()
   * @brief enable subscription logic for CTT 
   * @return true or false for setting.
   */
  public boolean getCTTFlag() {
    return enableCTT;
  }
}
