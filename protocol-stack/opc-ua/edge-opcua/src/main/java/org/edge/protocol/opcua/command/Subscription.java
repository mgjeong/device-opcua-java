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

import java.util.concurrent.CompletableFuture;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.providers.EdgeAttributeProvider;
import org.edge.protocol.opcua.providers.EdgeServices;
import org.edge.protocol.opcua.providers.services.da.EdgeAttributeService;
import org.edge.protocol.opcua.providers.services.sub.EdgeMonitoredItemService;
import org.edge.protocol.opcua.queue.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provide subscribe function
 */
public class Subscription implements Command {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * @fn void execute(CompletableFuture<EdgeResult> future, EdgeMessage msg)
   * @brief excute with EdgeMessage
   * @prarm [in] future
   * @prarm [in] msg
   * @return void
   */
  @Override
  public void execute(CompletableFuture<EdgeResult> future, EdgeMessage msg) throws Exception {
    // TODO
    // it needs to check connection state

    // start browsing at root folder
    EdgeResult ret = subscribe(msg);
    if (ret.getStatusCode() != EdgeStatusCode.STATUS_OK) {
      ErrorHandler.getInstance().addErrorMessage(msg.getRequest().getEdgeNodeInfo(), ret,
          msg.getRequest().getRequestId());
    }
    future.complete(ret);
  }

  /**
   * @fn EdgeResult subscribe(EdgeMessage msg)
   * @brief subscribe with EdgeMessage
   * @prarm [in] msg
   * @return EdgeResult
   */
  private EdgeResult subscribe(EdgeMessage msg) throws Exception {
    String serviceName = msg.getRequest().getEdgeNodeInfo().getValueAlias();
    EdgeAttributeProvider attributeProvider = EdgeServices.getAttributeProvider(serviceName);
    EdgeAttributeService service = attributeProvider.getAttributeService(serviceName);
    EdgeMonitoredItemService sub = attributeProvider.getMonitoredItemService();

    logger.info("command - request id = {}", msg.getRequest().getRequestId());
    EdgeResult ret = sub.subscription(msg.getRequest(),
        service.getNodeInfo(msg.getRequest().getEdgeNodeInfo().getValueAlias()),
        msg.getEdgeEndpointInfo());
    return ret;
  }
}
