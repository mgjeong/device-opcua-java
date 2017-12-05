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

import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Protocol;

public class AddressableGenerator {

  private AddressableGenerator() {}

  private static Addressable addressable = null;

  static Addressable generate() {
    if (addressable == null) {
      // TODO generate randomly
      addressable = new Addressable(OPCUADefaultMetaData.NAME.getValue(), Protocol.TCP,
          OPCUADefaultMetaData.ADDRESS.getValue(), OPCUADefaultMetaData.PATH.getValue(),
          OPCUADefaultMetaData.ADDRESSABLE_PORT);
    }
    return addressable;
  }

  static Addressable update() {
    if (addressable == null) {
      // TODO generate randomly
      addressable = new Addressable(OPCUADefaultMetaData.NAME.getValue(), Protocol.TCP,
          OPCUADefaultMetaData.ADDRESS1.getValue(), OPCUADefaultMetaData.PATH.getValue(),
          OPCUADefaultMetaData.ADDRESSABLE_PORT);
    }
    return addressable;
  }

  static Addressable getAddressable() {
    return addressable;
  }

}
