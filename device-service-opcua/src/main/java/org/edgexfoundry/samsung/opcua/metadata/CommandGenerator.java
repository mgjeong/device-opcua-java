package org.edgexfoundry.samsung.opcua.metadata;

import java.util.ArrayList;
import java.util.List;
import org.edge.protocol.mapper.api.EdgeMapper;
import org.edge.protocol.mapper.api.EdgeMapperCommon;
import org.edge.protocol.mapper.api.EdgeResponseCode;
import org.edge.protocol.opcua.providers.EdgeServices;
import org.edgexfoundry.domain.meta.Command;
import org.edgexfoundry.domain.meta.Get;
import org.edgexfoundry.domain.meta.Put;
import org.edgexfoundry.domain.meta.Response;
import org.edgexfoundry.samsung.opcua.DataDefaultValue;

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