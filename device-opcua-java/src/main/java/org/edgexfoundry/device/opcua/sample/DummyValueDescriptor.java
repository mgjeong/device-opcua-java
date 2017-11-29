package org.edgexfoundry.device.opcua.sample;

import java.util.ArrayList;
import java.util.List;

import org.edgexfoundry.domain.common.IoTType;
import org.edgexfoundry.domain.common.ValueDescriptor;

public class DummyValueDescriptor {

	private static String name = "DummyObject";
	//private static String name2 = "DummyValue00999";
	private static ValueDescriptor dummy = null;
	//private static ValueDescriptor dummy2 = null;


	static public String getName() {
		return name;
	}

	ValueDescriptor getDummy() {
		if (null == dummy) {
			dummy = new ValueDescriptor();
			dummy.setName(name);
			dummy.setMin("dum");
			dummy.setMax("dumdum");
			dummy.setType(IoTType.J);
			dummy.setUomLabel("dummmy");
			dummy.setDefaultValue("dum");
			dummy.setFormatting("%s");
			List<String> labelList = new ArrayList<String>();
			labelList.add("dum");
			labelList.add("dumdum");
			String[] labels = new String[labelList.size()];
			dummy.setLabels(labelList.toArray(labels));
		}
		return dummy;
	}
	/*
	ValueDescriptor getDummy2(String name) {
		if (null == dummy2) {
		  dummy2 = new ValueDescriptor();
			dummy2.setName(name);
			dummy2.setMin("dum2");
			dummy2.setMax("dumdum2");
			dummy2.setType(IoTType.J);
			dummy2.setUomLabel("dummmy2");
			dummy2.setDefaultValue("dum2");
			dummy2.setFormatting("%s");
			List<String> labelList = new ArrayList<String>();
			labelList.add("dum2");
			labelList.add("dumdum2");
			String[] labels = new String[labelList.size()];
			dummy2.setLabels(labelList.toArray(labels));
		}
		return dummy2;
	}
*/
}
