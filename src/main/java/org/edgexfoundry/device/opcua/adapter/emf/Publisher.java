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

package org.edgexfoundry.device.opcua.adapter.emf;

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

  /**
   * handling callback related ezmq.
   * 
   * @fn void callbackFactory()
   */
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

  /**
   * construct publisher
   * 
   * @fn Publisher()
   */
  private Publisher() {
    apiInstance = EMFAPI.getInstance();
    EMFStatusCode status = apiInstance.getStatus();
    if (status != EMFStatusCode.EMF_Initialized) {
      apiInstance.initialize();
    }
    callbackFactory();
  }

  /**
   * get instance of publisher class based singleton.
   * 
   * @fn Publisher getInstance()
   */
  public synchronized static Publisher getInstance() {
    if (singleton == null) {
      singleton = new Publisher();
    }
    return singleton;
  }

  /**
   * start publisher
   * 
   * @fn EMFErrorCode startPublisher(int port)
   * @param port subscriber port
   */
  public EMFErrorCode startPublisher(int port) {
    pubInstance = new EMFPublisher(port, mCallback);
    result = pubInstance.start();

    if (result != EMFErrorCode.EMF_OK) {
      pubInstance = null;
      System.out.println("Could not start EMF...");
    }
    return result;
  }

  /**
   * stop publisher
   * 
   * @fn EMFErrorCode stopPublisher()
   */
  public EMFErrorCode stopPublisher() {
    result = pubInstance.stop();

    if (result != EMFErrorCode.EMF_OK) {
      pubInstance = null;
      System.out.println("Publisher already stopped");
    }
    return result;
  }

  /**
   * publish Event Data
   * 
   * @fn EMFErrorCode publishEvent(Event event)
   * @param event publish data based Event Class
   */
  public EMFErrorCode publishEvent(Event event) {
    EMFErrorCode ret = EMFErrorCode.EMF_ERROR;
    if (event == null) {
      System.out.println("Delivered argument is null");
    } else {
      ret = pubInstance.publish(event);
    }

    return ret;
  }

  /**
   * publish Event Data
   * 
   * @fn EMFErrorCode publishEvent(String topic, Event event)
   * 
   * @param topic topic
   * @param event publish data based Event Class
   */
  public EMFErrorCode publishEvent(String topic, Event event) {
    EMFErrorCode ret = EMFErrorCode.EMF_ERROR;
    if (event == null || topic == null) {
      System.out.println("Delivered arguments is null");
    } else {
      ret = pubInstance.publish(topic, event);
    }

    return ret;
  }

  /**
   * publish Event Data
   * 
   * @fn EMFErrorCode publishEvent(List<String> topics, Event event)
   * 
   * @param topics topic list
   * @param event publish data based Event Class
   */
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
