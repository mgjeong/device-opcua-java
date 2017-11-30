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
import org.edge.protocol.opcua.api.common.EdgeCommandType;
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
        getList.add(newGetOperation(deviceInfoKey, EdgeCommandType.CMD_READ.getValue(), getOperationIndex++));
        profileResource.setGet(getList);

        List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
        // TODO set secondary and mappings
        int putOperationIndex = 1;
        setList.add(newPutOperation(deviceInfoKey, EdgeCommandType.CMD_WRITE.getValue(), putOperationIndex++));
        setList.add(newPutOperation(deviceInfoKey, EdgeCommandType.CMD_SUB.getValue(), putOperationIndex++));
        setList.add(newPutOperation(deviceInfoKey, EdgeCommandType.CMD_METHOD.getValue(), putOperationIndex++));
        profileResource.setSet(setList);
        return profileResource;
    }
}