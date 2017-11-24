package org.edgexfoundry.device.opcua.metadata;

import org.edgexfoundry.device.opcua.DataDefaultValue;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Protocol;

public class AddressableGenerator {

    private AddressableGenerator() {
    }

    private static Addressable addressable = null;

    static Addressable newAddressable() {
        if (addressable == null) {
            // TODO generate randomly
            addressable = new Addressable(DataDefaultValue.NAME.getValue(), Protocol.TCP,
                    DataDefaultValue.ADDRESS.getValue(), DataDefaultValue.PATH.getValue(),
                    DataDefaultValue.ADDRESSABLE_PORT);
        }
        return addressable;
    }

    static Addressable updateAddressable() {
        if (addressable == null) {
            // TODO generate randomly
            addressable = new Addressable(DataDefaultValue.NAME.getValue(), Protocol.TCP,
                    DataDefaultValue.ADDRESS1.getValue(), DataDefaultValue.PATH.getValue(),
                    DataDefaultValue.ADDRESSABLE_PORT);
        }
        return addressable;
    }

    static Addressable getAddressable() {
        return addressable;
    }

}
