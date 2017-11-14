package org.device.opcua.sample;

import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Protocol;

public class DummyAddressable {

	private static Addressable dummy;
	//private static Addressable dummy2;


	Addressable getDummy() {
		if (dummy == null) {
			// TODO generate randomly
			dummy = new Addressable("dummy", Protocol.TCP, "192.168.22.2", "dummy", 1123);
		}

		return dummy;
	}
	
	/*Addressable getDummy2() {
		if (dummy2 == null) {
			// TODO generate randomly
			dummy2 = new Addressable("dummy2", Protocol.TCP, "192.168.22.3", "dummy2", 1123);
		}

		return dummy2;
	}*/
}
