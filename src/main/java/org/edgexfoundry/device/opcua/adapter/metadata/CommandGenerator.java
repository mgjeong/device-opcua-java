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

import java.util.ArrayList;
import java.util.List;
import org.edge.protocol.mapper.api.EdgeResponseCode;
import org.edgexfoundry.domain.meta.Command;
import org.edgexfoundry.domain.meta.Get;
import org.edgexfoundry.domain.meta.Put;
import org.edgexfoundry.domain.meta.Response;

public class CommandGenerator {

  private CommandGenerator() {

  }

  private static Get createGetOperation(String deviceInfoKey) {
    Get get = new Get();
    get.setPath(OPCUADefaultMetaData.DEFAULT_ROOT_PATH + deviceInfoKey);
    for (EdgeResponseCode code : EdgeResponseCode.values()) {
      List<String> expected = new ArrayList<>();
      expected.add(String.valueOf(code.getValue()));
      get.addResponse(new Response(code.getCode(), code.getDescription(), expected));
    }
    return get;
  }

  private static Put createPutOperation(String deviceInfoKey) {
    List<String> parametNames = new ArrayList<>();
    parametNames.add(OPCUADefaultMetaData.PARAMETER_OPERATION.getValue());
    parametNames.add(OPCUADefaultMetaData.PARAMETER_VALUE.getValue());

    Put put = new Put();
    put.setPath(OPCUADefaultMetaData.DEFAULT_ROOT_PATH + deviceInfoKey);
    put.setParameterNames(parametNames);

    for (EdgeResponseCode code : EdgeResponseCode.values()) {
      List<String> expected = new ArrayList<>();
      expected.add(String.valueOf(code.getValue()));
      put.addResponse(new Response(code.getCode(), code.getDescription(), expected));
    }
    return put;
  }

  public static Command generate(String name, String readwrite) {
    if (name == null || name.isEmpty()) {
      return null;
    }
    
    Command command = new Command();
    command.setName(name);

    Get get = null;
    Put put = null;
    if (readwrite != null && readwrite.equals(OPCUADefaultMetaData.READ_ONLY) == true) {
      get = createGetOperation(name);
    } else if (readwrite != null && readwrite.equals(OPCUADefaultMetaData.WRITE_ONLY) == true) {
      put = createPutOperation(name);
    } else {
      get = createGetOperation(name);
      put = createPutOperation(name);
    }
    command.setGet(get);
    command.setPut(put);
    return command;
  }
}
