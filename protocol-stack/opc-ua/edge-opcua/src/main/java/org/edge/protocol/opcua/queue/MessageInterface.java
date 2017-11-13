/******************************************************************
 *
 * Copyright 2017 Samsung Electronics All Rights Reserved.
 *
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************/

package org.edge.protocol.opcua.queue;

import org.edge.protocol.opcua.api.common.EdgeMessage;

public interface MessageInterface {

  /**
   * @fn void onResponseMessage(EdgeMessage msg)
   * @brief callback related response message. The callback called onResponseMessages in
   *        ReceivedMessageCallback will be called inside.
   * @param [in] msg response message as EdgeMessage
   * @return void
   */
  public void onResponseMessage(EdgeMessage msg) throws Exception;

  /**
   * @fn void onMonitoredMessage(EdgeMessage msg)
   * @brief callback related monitoring message. The callback called onMonitoredMessage in
   *        ReceivedMessageCallback will be called inside.
   * @param [in] msg monitoring message as EdgeMessage
   * @return void
   */
  public void onMonitoredMessage(EdgeMessage msg) throws Exception;

  /**
   * @fn void onSendMessage(EdgeMessage msg)
   * @brief process send request message from send queue
   * @param [in] msg send message as EdgeMessage
   * @return void
   */
  public void onSendMessage(EdgeMessage msg) throws Exception;

  /**
   * @fn void onErrorCallback(EdgeMessage msg)
   * @brief callback related error message. The callback called onErrorMessage in
   *        ReceivedMessageCallback will be called inside.
   * @param [in] msg error message as EdgeMessage
   * @return void
   */
  public void onErrorCallback(EdgeMessage msg) throws Exception;
}
