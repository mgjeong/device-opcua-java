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

package org.edge.protocol.opcua.command;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeMessageType;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeRequest;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.providers.EdgeAttributeProvider;
import org.edge.protocol.opcua.providers.EdgeServices;
import org.edge.protocol.opcua.providers.EdgeViewProvider;
import org.edge.protocol.opcua.providers.services.browse.EdgeBrowseService;
import org.edge.protocol.opcua.providers.services.browse.EdgeViewService;
import org.edge.protocol.opcua.queue.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Browse class provide function for browse and execute
 */
public class Browse implements Command {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * @fn void execute(CompletableFuture<EdgeResult> future, EdgeMessage msg)
   * @brief excute with EdgeMessage
   * @prarm [in] future
   * @param [in] msg
   * @return void
   */
  @Override
  public void execute(CompletableFuture<EdgeResult> future, EdgeMessage msg) throws Exception {
    EdgeResult ret = browse("", msg);
    if (ret != null && ret.getStatusCode() != EdgeStatusCode.STATUS_OK) {
      ErrorHandler.getInstance().addErrorMessage(msg.getRequest().getEdgeNodeInfo(), ret,
          msg.getRequest().getRequestId());
    }
    future.complete(ret);
  }

  /**
   * @fn void browse(String indent, EdgeMessage msg)
   * @brief browse by EdgeBrowseService
   * @prarm [in] indent
   * @param [in] msg
   * @return void
   */
  private EdgeResult browse(String indent, EdgeMessage msg) throws Exception {
    logger.info("browse");
    List<EdgeRequest> requests = new ArrayList<EdgeRequest>();
    String valueVlias = null;
    if (msg.getMessageType() == EdgeMessageType.SEND_REQUEST) {
      valueVlias = msg.getRequest().getEdgeNodeInfo().getValueAlias();
      if (valueVlias.equals(EdgeOpcUaCommon.WELL_KNOWN_DISCOVERY.getValue())) {
        requests.add(msg.getRequest());
      } else {
        EdgeViewProvider viewProvider = EdgeServices.getViewProvider(valueVlias);
        EdgeViewService service = viewProvider.getViewService(valueVlias);
        requests.add(new EdgeRequest.Builder(
            new EdgeNodeInfo.Builder().setEdgeNodeId(service.getEdgeNodeId()).build()).build());
      }
    } else if (msg.getMessageType() == EdgeMessageType.SEND_REQUESTS) {
      for (EdgeRequest req : msg.getRequests()) {
        if (req.getEdgeNodeInfo().getValueAlias()
            .equals(EdgeOpcUaCommon.WELL_KNOWN_DISCOVERY.getValue())) {
          requests.add(req);
        } else {
          valueVlias = req.getEdgeNodeInfo().getValueAlias();
          EdgeViewProvider viewProvider = EdgeServices.getViewProvider(valueVlias);
          EdgeViewService service = viewProvider.getViewService(valueVlias);
          requests.add(new EdgeRequest.Builder(
              new EdgeNodeInfo.Builder().setEdgeNodeId(service.getEdgeNodeId()).build()).build());
        }
      }
    } else {
      return new EdgeResult.Builder(EdgeStatusCode.STATUS_PARAM_INVALID).build();
    }
    EdgeMessage browseMsg = new EdgeMessage.Builder(msg.getEdgeEndpointInfo())
        .setBrowseParameter(msg.getBrowseParameter()).setMessageType(EdgeMessageType.SEND_REQUESTS)
        .setRequests(requests).build();

    // EdgeViewProvider viewProvider = EdgeServices.getViewProvider(valueVlias);
    // EdgeViewService service = viewProvider.getViewService(valueVlias);
    // service.browseView(browseMsg);

    try {
      EdgeAttributeProvider attributeProvider =
          EdgeServices.getAttributeProvider(EdgeOpcUaCommon.WELL_KNOWN_DISCOVERY.getValue());
      EdgeBrowseService service = attributeProvider.getBrowseService();
      service.browse(indent, browseMsg);
    } catch (InterruptedException | ExecutionException e) {
      ErrorHandler.getInstance().addErrorMessage(browseMsg.getRequests().get(0).getEdgeNodeInfo(),
          new EdgeResult.Builder(EdgeStatusCode.STATUS_INTERNAL_ERROR).build(),
          browseMsg.getRequests().get(0).getRequestId());
    }

    return new EdgeResult.Builder(EdgeStatusCode.STATUS_OK).build();
  }
}
