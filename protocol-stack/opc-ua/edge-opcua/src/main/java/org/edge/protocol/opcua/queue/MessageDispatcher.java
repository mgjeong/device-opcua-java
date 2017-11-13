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

package org.edge.protocol.opcua.queue;

import java.util.concurrent.LinkedBlockingQueue;
import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeMessageType;

public class MessageDispatcher extends Thread {
  private LinkedBlockingQueue<EdgeMessage> mQueue;

  public MessageDispatcher() {

    this.mQueue = new LinkedBlockingQueue<EdgeMessage>();
  }

  /**
   * @fn void terminate()
   * @brief teminate queue
   * @return void
   */
  public void terminate() {
    mQueue.clear();
  }

  /**
   * @fn boolean putQ(EdgeMessage data)
   * @brief put data into queue
   * @param [in] data EdgeMessage
   * @return success(true) or failure(false)
   */
  synchronized public boolean putQ(EdgeMessage data) {

    if (data == null) {
      return false;
    }

    try {
      mQueue.put(data);

    } catch (InterruptedException e) {
      // Implement exception handling..
      return false;
    }

    return true;
  }

  /**
   * @fn void run()
   * @brief runner for queuing thread
   * @return void
   */
  @Override
  public void run() {

    while (!Thread.currentThread().isInterrupted()) {
      EdgeMessage data = null;

      try {
        data = mQueue.take();
        handleMessage(data);

      } catch (InterruptedException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
  }

  /**
   * @fn void handleMessage(EdgeMessage data)
   * @brief dispatch the message classified by EdgeMessageType
   * @prarm [in] EdgeMessage data
   * @return void
   */
  private void handleMessage(EdgeMessage data) {

    if (EdgeMessageType.SEND_REQUEST == data.getMessageType()
        || EdgeMessageType.SEND_REQUESTS == data.getMessageType()) {
      ProtocolManager send = ProtocolManager.getProtocolManagerInstance();
      send.onSendMessage(data);
    } else if (EdgeMessageType.GENERAL_RESPONSE == data.getMessageType()
        || EdgeMessageType.BROWSE_RESPONSE == data.getMessageType()) {
      ProtocolManager receiver = ProtocolManager.getProtocolManagerInstance();
      try {
        receiver.onResponseMessage(data);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else if (EdgeMessageType.REPORT == data.getMessageType()) {
      ProtocolManager receiver = ProtocolManager.getProtocolManagerInstance();
      try {
        receiver.onMonitoredMessage(data);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else if (EdgeMessageType.ERROR == data.getMessageType()) {
      ProtocolManager receiver = ProtocolManager.getProtocolManagerInstance();
      try {
        receiver.onErrorCallback(data);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
}
