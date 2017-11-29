package org.edgexfoundry.device.opcua.metadata;

import java.util.ArrayList;
import java.util.List;

import org.edgexfoundry.device.opcua.DataDefaultValue;
import org.edgexfoundry.domain.meta.ProfileResource;
import org.edgexfoundry.domain.meta.ResourceOperation;

public class ProfileResourceGenerator {

    private ProfileResourceGenerator() {
    }

    private static ResourceOperation newGetOperation(String deviceInfoKey, String operation,
            int index) {
        ResourceOperation resourceOperation = new ResourceOperation();
        resourceOperation.setIndex(String.valueOf(index));
        resourceOperation.setOperation(operation);
        resourceOperation.setObject(deviceInfoKey);
        resourceOperation.setProperty(DataDefaultValue.PROPERTY_GET.getValue());
        resourceOperation.setResource(DataDefaultValue.RESOURCE.getValue());
        return resourceOperation;
    }

    private static ResourceOperation newPutOperation(String deviceInfoKey, String operation,
            int index) {
        ResourceOperation resourceOperation = new ResourceOperation();
        resourceOperation.setIndex(String.valueOf(index));
        resourceOperation.setOperation(operation);
        resourceOperation.setObject(deviceInfoKey);
        resourceOperation.setProperty(DataDefaultValue.PROPERTY_SET.getValue());
        resourceOperation.setParameter(DataDefaultValue.PARAMETER_OPERATION.getValue() + ","
                + DataDefaultValue.PARAMETER_VALUE.getValue());
        resourceOperation.setResource(DataDefaultValue.RESOURCE.getValue());
        return resourceOperation;
    }

    public static ProfileResource newProfileResource(String deviceInfoKey) {
        ProfileResource profileResource = new ProfileResource();
        profileResource.setName(deviceInfoKey);
        int getOperationIndex = 1;
        List<ResourceOperation> getList = new ArrayList<ResourceOperation>();
        // TODO set secondary and mappings
        getList.add(newGetOperation(deviceInfoKey, "read", getOperationIndex++));
        profileResource.setGet(getList);

        List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
        // TODO set secondary and mappings
        int putOperationIndex = 1;
        setList.add(newPutOperation(deviceInfoKey, "write", putOperationIndex++));
        setList.add(newPutOperation(deviceInfoKey, "sub", putOperationIndex++));
        setList.add(newPutOperation(deviceInfoKey, "method", putOperationIndex++));
        profileResource.setSet(setList);
        return profileResource;
    }
}