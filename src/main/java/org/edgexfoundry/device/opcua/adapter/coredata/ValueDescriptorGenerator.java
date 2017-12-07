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

package org.edgexfoundry.device.opcua.adapter.coredata;

import org.edgexfoundry.device.opcua.adapter.metadata.OPCUADefaultMetaData;
import org.edgexfoundry.domain.common.IoTType;
import org.edgexfoundry.domain.common.ValueDescriptor;

public class ValueDescriptorGenerator {

  private ValueDescriptorGenerator() {}

  public static ValueDescriptor generate(String name) {
    if (name == null || name.isEmpty()) {
      return null;
    }

    ValueDescriptor valueDescriptor = new ValueDescriptor();
    valueDescriptor.setName(name);
    valueDescriptor.setMin(OPCUADefaultMetaData.MIN.getValue());
    valueDescriptor.setMax(OPCUADefaultMetaData.MAX.getValue());
    valueDescriptor.setType(IoTType.J);
    valueDescriptor.setUomLabel(OPCUADefaultMetaData.UOMLABEL.getValue());
    valueDescriptor.setDefaultValue(OPCUADefaultMetaData.DEFAULTVALUE.getValue());
    valueDescriptor.setFormatting("%s");
    String[] labels =
        {OPCUADefaultMetaData.LABEL1.getValue(), OPCUADefaultMetaData.LABEL2.getValue()};
    valueDescriptor.setLabels(labels);
    return valueDescriptor;
  }
}
