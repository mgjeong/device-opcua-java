package org.edgexfoundry.samsung.opcua.coredata;

import java.util.ArrayList;
import java.util.List;

import org.edgexfoundry.domain.common.IoTType;
import org.edgexfoundry.domain.common.ValueDescriptor;
import org.edgexfoundry.samsung.opcua.DataDefaultValue;

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
