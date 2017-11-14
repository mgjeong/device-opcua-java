package org.edgexfoundry.samsung.opcua.metadata;

import org.edgexfoundry.samsung.opcua.DataDefaultValue;

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
