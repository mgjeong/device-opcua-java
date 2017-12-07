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

import java.util.List;
import org.edgexfoundry.domain.meta.ProfileResource;
import org.edgexfoundry.domain.meta.ResourceOperation;

public class ProfileResourceGenerator {

  private ProfileResourceGenerator() {}

  public static ResourceOperation createGetOperation(String object, String operation, int index) {
    if (object == null || object.isEmpty()) {
      return null;
    }

    ResourceOperation resourceOperation = new ResourceOperation();
    resourceOperation.setIndex(String.valueOf(index));
    resourceOperation.setOperation(operation);
    resourceOperation.setObject(object);
    resourceOperation.setProperty(OPCUADefaultMetaData.PROPERTY_GET.getValue());
    resourceOperation.setResource(OPCUADefaultMetaData.RESOURCE.getValue());
    return resourceOperation;
  }

  public static ResourceOperation createPutOperation(String object, String operation, int index) {
    if (object == null || object.isEmpty()) {
      return null;
    }

    ResourceOperation resourceOperation = new ResourceOperation();
    resourceOperation.setIndex(String.valueOf(index));
    resourceOperation.setOperation(operation);
    resourceOperation.setObject(object);
    resourceOperation.setProperty(OPCUADefaultMetaData.PROPERTY_SET.getValue());
    resourceOperation.setParameter(OPCUADefaultMetaData.PARAMETER_OPERATION.getValue() + ","
        + OPCUADefaultMetaData.PARAMETER_VALUE.getValue());
    resourceOperation.setResource(OPCUADefaultMetaData.RESOURCE.getValue());
    return resourceOperation;
  }

  public static ProfileResource generate(String name, List<ResourceOperation> getList,
      List<ResourceOperation> setList) {
    if (name == null || name.isEmpty()) {
      return null;
    }

    ProfileResource profileResource = new ProfileResource();
    profileResource.setName(name);
    profileResource.setGet(getList);
    profileResource.setSet(setList);
    return profileResource;
  }
}
