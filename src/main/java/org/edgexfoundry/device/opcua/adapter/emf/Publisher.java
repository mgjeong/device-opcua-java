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
   * @fn void callbackFactory()
   * @brief handling callback related ezmq.
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
   * @fn Publisher()
   * @brief construct
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
   * @fn Publisher getInstance()
   * @brief get instance of publisher class based singleton.
   */
  public synchronized static Publisher getInstance() {
    if (singleton == null) {
      singleton = new Publisher();
    }
    return singleton;
  }

  /**
   * @fn EMFErrorCode startPublisher(int port)
   * @brief start publisher
   * @param [in] port subscriber port
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
   * @fn EMFErrorCode stopPublisher()
   * @brief stop publisher
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
   * @fn EMFErrorCode publishEvent(Event event)
   * @brief publish Event Data 
   * @param [in] event publish data based Event Class
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
   * @fn EMFErrorCode publishEvent(String topic, Event event)
   * @brief publish Event Data
   * @param [in] topic topic
   * @param [in] event publish data based Event Class 
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
   * @fn EMFErrorCode publishEvent(List<String> topics, Event event)
   * @brief publish Event Data
   * @param [in] topics topic list
   * @param [in] event publish data based Event Class 
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
