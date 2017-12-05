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
import org.edge.protocol.opcua.api.common.EdgeCommandType;
import org.edgexfoundry.domain.meta.ProfileResource;
import org.edgexfoundry.domain.meta.ResourceOperation;

public class ProfileResourceGenerator {
  private final static int startOperarionIndex = 1;

  private ProfileResourceGenerator() {}

  private static ResourceOperation createGetOperation(String deviceInfoKey, String operation,
      int index) {
    ResourceOperation resourceOperation = new ResourceOperation();
    resourceOperation.setIndex(String.valueOf(index));
    resourceOperation.setOperation(operation);
    resourceOperation.setObject(deviceInfoKey);
    resourceOperation.setProperty(OPCUADefaultMetaData.PROPERTY_GET.getValue());
    resourceOperation.setResource(OPCUADefaultMetaData.RESOURCE.getValue());
    return resourceOperation;
  }

  private static ResourceOperation createPutOperation(String deviceInfoKey, String operation,
      int index) {
    ResourceOperation resourceOperation = new ResourceOperation();
    resourceOperation.setIndex(String.valueOf(index));
    resourceOperation.setOperation(operation);
    resourceOperation.setObject(deviceInfoKey);
    resourceOperation.setProperty(OPCUADefaultMetaData.PROPERTY_SET.getValue());
    resourceOperation.setParameter(OPCUADefaultMetaData.PARAMETER_OPERATION.getValue() + ","
        + OPCUADefaultMetaData.PARAMETER_VALUE.getValue());
    resourceOperation.setResource(OPCUADefaultMetaData.RESOURCE.getValue());
    return resourceOperation;
  }

  public static ProfileResource generate(String deviceInfoKey, String deviceType) {
    ProfileResource profileResource = new ProfileResource();
    profileResource.setName(deviceInfoKey);
    if (OPCUACommandIdentifier.ATTRIBUTE_COMMAND.getValue().equals(deviceType) == true) {
      setAttributeServiceOperation(deviceInfoKey, profileResource);
    } else if (OPCUACommandIdentifier.WELLKNOWN_COMMAND_GROUP.getValue()
        .equals(deviceInfoKey) == true) {
      setGroupServiceOperation(deviceInfoKey, profileResource);
    } else if (OPCUACommandIdentifier.WELLKNOWN_COMMAND_START.getValue()
        .equals(deviceInfoKey) == true) {
      setStartServiceOperation(deviceInfoKey, profileResource);
    } else if (OPCUACommandIdentifier.WELLKNOWN_COMMAND_STOP.getValue()
        .equals(deviceInfoKey) == true) {
      setStopServiceOperation(deviceInfoKey, profileResource);
    } else if (OPCUACommandIdentifier.WELLKNOWN_COMMAND_GETENDPOINT.getValue()
        .equals(deviceInfoKey) == true) {
      setGetEndpointServiceOperation(deviceInfoKey, profileResource);
    } 
    return profileResource;
  }

  private static void setAttributeServiceOperation(String deviceInfoKey,
      ProfileResource profileResource) {
    int getOperationIndex = startOperarionIndex;
    List<ResourceOperation> getList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    getList.add(createGetOperation(deviceInfoKey, EdgeCommandType.CMD_READ.getValue(),
        getOperationIndex++));
    profileResource.setGet(getList);

    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperarionIndex;
    setList.add(createPutOperation(deviceInfoKey, EdgeCommandType.CMD_WRITE.getValue(),
        putOperationIndex++));
    setList.add(
        createPutOperation(deviceInfoKey, EdgeCommandType.CMD_SUB.getValue(), putOperationIndex++));
    profileResource.setSet(setList);
  }

  private static void setGroupServiceOperation(String deviceInfoKey,
      ProfileResource profileResource) {
    profileResource.setGet(null);

    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperarionIndex;
    setList.add(createPutOperation(deviceInfoKey, EdgeCommandType.CMD_READ.getValue(),
        putOperationIndex++));
    setList.add(createPutOperation(deviceInfoKey, EdgeCommandType.CMD_WRITE.getValue(),
        putOperationIndex++));
    setList.add(
        createPutOperation(deviceInfoKey, EdgeCommandType.CMD_SUB.getValue(), putOperationIndex++));
    profileResource.setSet(setList);
  }
  
  private static void setStartServiceOperation(String deviceInfoKey,
      ProfileResource profileResource) {
    profileResource.setGet(null);
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperarionIndex;
    setList.add(createPutOperation(deviceInfoKey, EdgeCommandType.CMD_START_CLIENT.getValue(),
        putOperationIndex++));
    profileResource.setSet(setList);
  }
  
  private static void setStopServiceOperation(String deviceInfoKey,
      ProfileResource profileResource) {
    profileResource.setGet(null);
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperarionIndex;
    setList.add(createPutOperation(deviceInfoKey, EdgeCommandType.CMD_STOP_CLIENT.getValue(),
        putOperationIndex++));
    profileResource.setSet(setList);
  }
  
  private static void setGetEndpointServiceOperation(String deviceInfoKey,
      ProfileResource profileResource) {
    profileResource.setGet(null);
    List<ResourceOperation> setList = new ArrayList<ResourceOperation>();
    // TODO set secondary and mappings
    int putOperationIndex = startOperarionIndex;
    setList.add(createPutOperation(deviceInfoKey, EdgeCommandType.CMD_GET_ENDPOINTS.getValue(),
        putOperationIndex++));
    profileResource.setSet(setList);
  }
}
