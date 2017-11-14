package org.edgexfoundry.samsung.opcua.metadata;

import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Protocol;
import org.edgexfoundry.samsung.opcua.DataDefaultValue;

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

    static Addressable getAddressable() {
        return addressable;
    }

}
