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

package org.edge.protocol.opcua.api.common;

public enum EdgeMessageType {
  SERVER_INFO(0, "server information"),
  SERVER_INFO_PRODUCT_URI(1, "specific product uri for device"),
  GENERAL_RESPONSE(10, "General Data"),
  BROWSE_RESPONSE(11, "Browse Response"),
  REPORT(20, "Report Data"),
  SAMPLING(21, "Sampling Data"),
  SEND_REQUEST(30, "send message"),
  SEND_REQUESTS(31, "send muli-message"),
  ERROR(40, "error message");

  private int code;
  private String description;

  private EdgeMessageType(int code, String description) {
    this.code = code;
    this.description = description;
  }

  public int getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }
}
