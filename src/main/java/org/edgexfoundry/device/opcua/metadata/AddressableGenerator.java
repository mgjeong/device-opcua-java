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
