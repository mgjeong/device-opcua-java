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

package org.edge.protocol.opcua.providers;

public enum EdgeServiceIdentifier {
  READ(1), WRITE(2), SUB(3), ALARM(4), CON_ACK(5), DISCOVERY(6), STREAM(7);

  private final int key;

  EdgeServiceIdentifier(int key) {
    this.key = key;
  }

  /**
   * @fn int value()
   * @brief get value from the enum
   * @return value
   */
  public int value() {
    return key;
  }
}
