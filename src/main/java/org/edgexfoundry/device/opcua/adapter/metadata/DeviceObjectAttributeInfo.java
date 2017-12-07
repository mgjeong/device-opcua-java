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

package org.edgexfoundry.device.opcua.adapter.metadata;

public class DeviceObjectAttributeInfo {
  final private String providerKey;
  private String dataType;

  public static class Builder {
    final private String providerKey;
    private String dataType;

    /**
     * @fn Builder(String providerKey)
     * @brief constructor
     * @param [in] providerKey @String
     */
    public Builder(String providerKey) {
      if (null != providerKey) {
        this.providerKey = providerKey.replace(OPCUADefaultMetaData.AFTER_REPLACE_WORD,
            OPCUADefaultMetaData.BEFORE_REPLACE_WORD);
      } else {
        this.providerKey = providerKey;
      }
    }

    /**
     * @fn Builder setDataType(String dataType)
     * @brief set Data Type
     * @param [in] dataType @String
     * @return @Builder
     */
    public Builder setDataType(String dataType) {
      this.dataType = dataType;
      return this;
    }

    /**
     * @fn DeviceObjectAttributeInfo build()
     * @brief create DeviceObjectAttributeInfo instance (builder)
     * @return DeviceObjectAttributeInfo instance
     */
    public DeviceObjectAttributeInfo build() {
      return new DeviceObjectAttributeInfo(this);
    }
  }

  /**
   * @fn DeviceObjectAttributeInfo(Builder builder)
   * @brief constructor
   * @param [in] builder @Builder
   */
  DeviceObjectAttributeInfo(Builder builder) {
    this.providerKey = builder.providerKey;
    this.dataType = builder.dataType;
  }

  /**
   * @fn String getProviderKey()
   * @brief get ProviderKey
   * @return @String
   */
  public String getProviderKey() {
    return providerKey;
  }

  /**
   * @fn String getDataType()
   * @brief get DataType
   * @return @String
   */
  public String getDataType() {
    return dataType;
  }
}
