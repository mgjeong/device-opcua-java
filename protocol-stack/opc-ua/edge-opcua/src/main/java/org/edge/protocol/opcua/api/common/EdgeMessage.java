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

package org.edge.protocol.opcua.api.common;

import java.util.List;
import org.edge.protocol.opcua.api.client.EdgeBrowseParameter;
import org.edge.protocol.opcua.api.client.EdgeResponse;

public class EdgeMessage {
  private EdgeMessageType type;
  private EdgeCommandType command;
  private EdgeEndpointInfo endpointInfo;
  private EdgeRequest request;
  private List<EdgeRequest> requests;
  private List<EdgeResponse> responses;
  private EdgeResult result;
  private EdgeBrowseParameter browseMsg;
  private List<EdgeBrowseResult> browseResult;

  public static class Builder {
    private EdgeMessageType type = EdgeMessageType.SEND_REQUEST;
    private EdgeCommandType command = EdgeCommandType.CMD_READ;
    private EdgeEndpointInfo endpointInfo = null;
    private EdgeRequest request = null;
    private List<EdgeRequest> requests = null;
    private List<EdgeResponse> responses = null;
    private EdgeResult result = null;
    private EdgeBrowseParameter browseMsg = null;
    private List<EdgeBrowseResult> browseResult = null;

    /**
     * @fn default Builder()
     */
    public Builder(EdgeEndpointInfo endpointInfo) {
      this.endpointInfo = endpointInfo;
    }

    /**
     * @fn Builder setRequest(EdgeRequest request)
     * @brief set single request information
     * @param [in] request send request
     * @return this
     */
    public Builder setRequest(EdgeRequest request) {
      this.request = request;
      return this;
    }

    /**
     * @fn Builder setRequests(List<EdgeRequest> requests)
     * @brief set multiple request information ( Max Request Size is 10 in Browse )
     * @param [in] requests send request list
     * @return this
     */
    public Builder setRequests(List<EdgeRequest> requests) {
      this.requests = requests;
      return this;
    }


    /**
     * @fn Builder setBrowseReq(EdgeBrowseRequest req)
     * @brief set browse message
     * @param [in] browseMsg
     * @return this
     */
    public Builder setBrowseParameter(EdgeBrowseParameter req) {
      this.browseMsg = req;
      return this;
    }

    /**
     * @fn Builder setBrowseResult(List<EdgeBrowseResult> browseResponses)
     * @brief set browse result
     * @param [in] browseResult
     * @return this
     */
    public Builder setBrowseResult(List<EdgeBrowseResult> browseResult) {
      this.browseResult = browseResult;
      return this;
    }

    /**
     * @fn Builder setResponses(List<EdgeResponse> responses)
     * @brief set multiple response information
     * @param [in] responses received response information list
     * @return this
     */
    public Builder setResponses(List<EdgeResponse> responses) {
      this.responses = responses;
      return this;
    }

    /**
     * @fn Builder setResult(EdgeResult result)
     * @brief set result you can find detailed result data in EdgeStatusCode
     * @param [in] responses received response information list
     * @return this
     */
    public Builder setResult(EdgeResult result) {
      this.result = result;
      return this;
    }

    /**
     * @fn Builder setMessageType(EdgeMessageType type)
     * @brief set message type it appears that message type such as single request, multiple
     *        request, response message, event message, etc
     * @param [in] type message type
     * @return this
     */
    public Builder setMessageType(EdgeMessageType type) {
      this.type = type;
      return this;
    }

    /**
     * @fn Builder setCommand(String cmd)
     * @brief set command(operation) you can find detailed command information as CMD_XXX in
     *        EdgeOpcUaCommon enum class
     * @param [in] cmd command
     * @return this
     */
    public Builder setCommand(EdgeCommandType cmd) {
      this.command = cmd;
      return this;
    }

    /**
     * @fn EdgeMessage build()
     * @brief EdgeMesssage instance creator
     * @return EdgeMessage instance
     */
    public EdgeMessage build() {
      return new EdgeMessage(this);
    }
  }

  /**
   * @fn EdgeMessage(Builder builder)
   * @brief constructor
   * @param [in] builder EdgeMessage Builder
   */
  private EdgeMessage(Builder builder) {
    request = builder.request;
    requests = builder.requests;
    responses = builder.responses;
    type = builder.type;
    command = builder.command;
    result = builder.result;
    browseMsg = builder.browseMsg;
    browseResult = builder.browseResult;
    endpointInfo = builder.endpointInfo;
  }

  /**
   * @fn EdgeRequest getRequest()
   * @brief get request
   * @return single request
   */
  public EdgeRequest getRequest() {
    return request;
  }

  /**
   * @fn List<EdgeRequest> getRequests()
   * @brief get request list
   * @return multiple request
   */
  public List<EdgeRequest> getRequests() {
    return requests;
  }

  /**
   * @fn List<EdgeResponse> getResponses()
   * @brief get response list
   * @return multiple response
   */
  public List<EdgeResponse> getResponses() {
    return responses;
  }

  /**
   * @fn EdgeBrowseRequest getBrowseMsg()
   * @brief get browse configuration
   * @return configuration
   */
  public EdgeBrowseParameter getBrowseParameter() {
    return browseMsg;
  }

  /**
   * @fn List<EdgeBrowseResult> getBrowseResults()
   * @brief get browse results
   * @return browse results
   */
  public List<EdgeBrowseResult> getBrowseResults() {
    return browseResult;
  }

  /**
   * @fn EdgeMessageType getMessageType()
   * @brief get message type it can be found in EdgeMessageType
   * @return message type
   */
  public EdgeMessageType getMessageType() {
    return type;
  }

  /**
   * @fn String getCommand()
   * @brief get message type it can be found in CMD_XXX of EdgeOpcUaCommon
   * @return command string
   */
  public EdgeCommandType getCommand() {
    return command;
  }

  /**
   * @fn EdgeEndpointInfo getEdgeEndpointInfo()
   * @brief get endpointInfo
   * @return endpointInfo
   */
  public EdgeEndpointInfo getEdgeEndpointInfo() {
    return endpointInfo;
  }

  /**
   * @fn EdgeResult getResult()
   * @brief get result you can find detailed result as status code in EdgeStatusCode
   * @return result
   */
  public EdgeResult getResult() {
    return result;
  }
}
