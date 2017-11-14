package org.device.opcua.emf;

import java.util.List;

import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.emf.EMFAPI;
import org.edgexfoundry.emf.EMFCallback;
import org.edgexfoundry.emf.EMFErrorCode;
import org.edgexfoundry.emf.EMFPublisher;
import org.edgexfoundry.emf.EMFStatusCode;

public class Publisher {
	private static Publisher singleton;
	private static EMFAPI apiInstance;
	private static EMFPublisher pubInstance;
	private static EMFCallback mCallback;
	private static EMFErrorCode result = EMFErrorCode.EMF_ERROR;

	private static void callbackFactory() {
		mCallback = new EMFCallback() {

			public void onStopCB(EMFErrorCode code) {
				System.out.println("onStopCB Called Code: " + code);
			}

			public void onStartCB(EMFErrorCode code) {
				System.out.println("onStartCB Called Code: " + code);
			}

			public void onErrorCB(EMFErrorCode code) {
				System.out.println("onErrorCB Called Code: " + code);
			}
		};
	}

	private Publisher() {
		apiInstance = EMFAPI.getInstance();
		EMFStatusCode status = apiInstance.getStatus();
		if (status != EMFStatusCode.EMF_Initialized) {
			apiInstance.initialize();
		}
		callbackFactory();
	}

	public static Publisher getInstance() {
		if (singleton == null) {
			singleton = new Publisher();
		}
		return singleton;
	}

	public EMFErrorCode startPublisher(int port) {
		pubInstance = new EMFPublisher(port, mCallback);
		result = pubInstance.start();

		if (result != EMFErrorCode.EMF_OK) {
			pubInstance = null;
			System.out.println("Could not start EMF...");
		}
		return result;
	}

	public EMFErrorCode stopPublisher() {
		result = pubInstance.stop();

		if (result != EMFErrorCode.EMF_OK) {
			pubInstance = null;
			System.out.println("Publisher already stopped");
		}
		return result;
	}

	public EMFErrorCode publishEvent(Event event) {
		EMFErrorCode ret = EMFErrorCode.EMF_ERROR;
		if (event == null) {
			System.out.println("Delivered argument is null");
		} else {
			ret = pubInstance.publish(event);
		}

		return ret;
	}

	public EMFErrorCode publishEvent(String topic, Event event) {
		EMFErrorCode ret = EMFErrorCode.EMF_ERROR;
		if (event == null || topic == null) {
			System.out.println("Delivered arguments is null");
		} else {
			ret = pubInstance.publish(topic, event);
		}

		return ret;
	}

	public EMFErrorCode publishEvent(List<String> topics, Event event) {
		EMFErrorCode ret = EMFErrorCode.EMF_ERROR;
		if (event == null || topics == null) {
			System.out.println("Delivered arguments is null");
		} else {
			ret = pubInstance.publish(topics, event);
		}

		return ret;
	}
}
