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

import java.util.ArrayList;
import java.util.List;

import org.edge.protocol.mapper.api.EdgeMapper;
import org.edge.protocol.mapper.api.EdgeMapperCommon;
import org.edge.protocol.mapper.api.EdgeResponseCode;
import org.edge.protocol.opcua.providers.EdgeServices;
import org.edgexfoundry.device.opcua.DataDefaultValue;
import org.edgexfoundry.domain.meta.Command;
import org.edgexfoundry.domain.meta.Get;
import org.edgexfoundry.domain.meta.Put;
import org.edgexfoundry.domain.meta.Response;

public class CommandGenerator {

    private CommandGenerator() {

    }

    private static String getDeviceInfo(String deviceInfoKey, String id) {
        deviceInfoKey = deviceInfoKey.replaceAll(DataDefaultValue.REPLACE_DEVICE_NAME, "/");
        EdgeMapper mapper = EdgeServices.getAttributeProvider(deviceInfoKey)
            .getAttributeService(deviceInfoKey).getMapper();
        if(mapper == null){
          return null;
        }else{
          return mapper.getMappingData(id);
        }
    }

    private static Get newGetOperation(String deviceInfoKey) {
        String readwrite = getDeviceInfo(deviceInfoKey,
                EdgeMapperCommon.PROPERTYVALUE_READWRITE.name());
        if (readwrite != null && readwrite.equals(DataDefaultValue.WRITEPONLY) == true) {
            return null;
        }
        Get get = new Get();
        get.setPath(DataDefaultValue.DEFAULT_ROOT_PATH + deviceInfoKey);
        for (EdgeResponseCode code : EdgeResponseCode.values()) {
            List<String> expected = new ArrayList<>();
            expected.add(String.valueOf(code.getValue()));
            get.addResponse(new Response(code.getCode(), code.getDescription(), expected));
        }
        return get;
    }

    private static Put newPutOperation(String deviceInfoKey) {
        String readwrite = getDeviceInfo(deviceInfoKey,
                EdgeMapperCommon.PROPERTYVALUE_READWRITE.name());
        if (readwrite != null && readwrite.equals(DataDefaultValue.READONLY) == true) {
            return null;
        }
        List<String> parametNames = new ArrayList<>();
        parametNames.add(DataDefaultValue.PARAMETER_OPERATION.getValue());
        parametNames.add(DataDefaultValue.PARAMETER_VALUE.getValue());

        Put put = new Put();
        put.setPath(DataDefaultValue.DEFAULT_ROOT_PATH + deviceInfoKey);
        put.setParameterNames(parametNames);

        for (EdgeResponseCode code : EdgeResponseCode.values()) {
            List<String> expected = new ArrayList<>();
            expected.add(String.valueOf(code.getValue()));
            put.addResponse(new Response(code.getCode(), code.getDescription(), expected));
        }
        return put;
    }

    public static Command newCommand(String deviceInfoKey) {
        Command command = new Command();
        command.setName(deviceInfoKey);
        command.setGet(newGetOperation(deviceInfoKey));
        command.setPut(newPutOperation(deviceInfoKey));
        return command;
    }
}