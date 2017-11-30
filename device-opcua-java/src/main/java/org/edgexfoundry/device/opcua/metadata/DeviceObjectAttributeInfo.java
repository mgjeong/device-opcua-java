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

package org.edgexfoundry.device.opcua.metadata;

import org.edgexfoundry.device.opcua.DataDefaultValue;

public class DeviceObjectAttributeInfo {
    final private String providerKey;
    private String dataType;

    public static class Builder {
        final private String providerKey;
        private String dataType;

        public Builder(String providerKey) {
            this.providerKey = providerKey.replace(DataDefaultValue.REPLACE_DEVICE_NAME, "/");
        }

        public Builder setDataType(String dataType) {
        	this.dataType = dataType;
        	return this;
        }

        public DeviceObjectAttributeInfo build() {
            return new DeviceObjectAttributeInfo(this);
        }
    }

    DeviceObjectAttributeInfo(Builder builder) {
        this.providerKey = builder.providerKey;
        this.dataType = builder.dataType;
    }

    public String getProviderKey() {
        return providerKey;
    }

    public String getDataType() {
    	return dataType;
    }
}
