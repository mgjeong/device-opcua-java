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

package org.edgexfoundry.device.opcua.coredata;

import org.edgexfoundry.device.opcua.DataDefaultValue;
import org.edgexfoundry.domain.common.IoTType;
import org.edgexfoundry.domain.common.ValueDescriptor;

public class ValueDescriptorGenerator {

    private ValueDescriptorGenerator() {
    }

    public static ValueDescriptor newValueDescriptor(String DeviceName) {
        ValueDescriptor valueDescriptor = new ValueDescriptor();
        valueDescriptor.setName(DeviceName);
        valueDescriptor.setMin(DataDefaultValue.MIN.getValue());
        valueDescriptor.setMax(DataDefaultValue.MAX.getValue());
        valueDescriptor.setType(IoTType.J);
        valueDescriptor.setUomLabel(DataDefaultValue.UOMLABEL.getValue());
        valueDescriptor.setDefaultValue(DataDefaultValue.DEFAULTVALUE.getValue());
        valueDescriptor.setFormatting("%s");
        String[] labels = {DataDefaultValue.LABEL1.getValue(), DataDefaultValue.LABEL2.getValue()};
        valueDescriptor.setLabels(labels);
        return valueDescriptor;
    }

}
