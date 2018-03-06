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

package org.edgexfoundry.device.opcua.adapter.ezmq;

import java.util.List;
import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.ezmq.EZMQAPI;
import org.edgexfoundry.ezmq.EZMQCallback;
import org.edgexfoundry.ezmq.EZMQErrorCode;
import org.edgexfoundry.ezmq.EZMQPublisher;
import org.edgexfoundry.ezmq.EZMQStatusCode;

public class Publisher {
  private static Publisher singleton;
  private static EZMQAPI apiInstance;
  private static EZMQPublisher pubInstance;
  private static EZMQCallback mCallback;

  /**
   * handling callback related ezmq.
   * 
   */
  private static void callbackFactory() {
    mCallback = new EZMQCallback() {

      public void onStopCB(EZMQErrorCode code) {
        System.out.println("onStopCB Called Code: " + code);
      }

      public void onStartCB(EZMQErrorCode code) {
        System.out.println("onStartCB Called Code: " + code);
      }

      public void onErrorCB(EZMQErrorCode code) {
        System.out.println("onErrorCB Called Code: " + code);
      }
    };
  }

  /**
   * construct publisher
   * 
   */
  private Publisher() {
    apiInstance = EZMQAPI.getInstance();
    EZMQStatusCode status = apiInstance.getStatus();
    if (status != EZMQStatusCode.EZMQ_Initialized) {
      apiInstance.initialize();
    }
    callbackFactory();
  }

  /**
   * get instance of publisher class based singleton.
   * 
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
   * @param port subscriber port
   * @return error code
   */
  public EZMQErrorCode startPublisher(int port) {
    pubInstance = new EZMQPublisher(port, mCallback);
    EZMQErrorCode ret = pubInstance.start();

    if (ret != EZMQErrorCode.EZMQ_OK) {
      pubInstance = null;
      System.out.println("Could not start EZMQ...");
    }
    return ret;
  }

  /**
   * stop publisher
   * 
   * @return error code
   */
  public EZMQErrorCode stopPublisher() {
      EZMQErrorCode ret = pubInstance.stop();

    if (ret != EZMQErrorCode.EZMQ_OK) {
      pubInstance = null;
      System.out.println("Publisher already stopped");
    }
    return ret;
  }

  /**
   * publish Event Data
   *
   * @param event publish data based Event Class
   * @return error code
   */
  public EZMQErrorCode publishEvent(Event event) {
    EZMQErrorCode ret = EZMQErrorCode.EZMQ_ERROR;
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
   * @param topic topic
   * @param event publish data based Event Class
   * @return error code
   */
  public EZMQErrorCode publishEvent(String topic, Event event) {
    EZMQErrorCode ret = EZMQErrorCode.EZMQ_ERROR;
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
   * @param topics topic list
   * @param event publish data based Event Class
   * @return error code
   */
  public EZMQErrorCode publishEvent(List<String> topics, Event event) {
    EZMQErrorCode ret = EZMQErrorCode.EZMQ_ERROR;
    if (event == null || topics == null) {
      System.out.println("Delivered arguments is null");
    } else {
      ret = pubInstance.publish(topics, event);
    }

    return ret;
  }
}
