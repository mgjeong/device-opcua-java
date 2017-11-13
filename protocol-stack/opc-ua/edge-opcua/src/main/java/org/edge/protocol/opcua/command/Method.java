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

package org.edge.protocol.opcua.command;

import java.util.concurrent.CompletableFuture;
import org.edge.protocol.opcua.api.common.EdgeCommandType;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.providers.EdgeMethodProvider;
import org.edge.protocol.opcua.providers.EdgeServices;
import org.edge.protocol.opcua.providers.services.method.EdgeMethodService;
import org.edge.protocol.opcua.queue.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provide read function
 */
public class Method implements Command {
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
    EdgeResult ret = method(msg);
    if (ret != null && ret.getStatusCode() != EdgeStatusCode.STATUS_OK) {
      ErrorHandler.getInstance().addErrorMessage(msg.getRequest().getEdgeNodeInfo(), ret,
          msg.getRequest().getRequestId());
    }
    future.complete(ret);
  }

  /**
   * @fn EdgeResult method(EdgeMessage msg)
   * @brief call method with EdgeMessage
   * @prarm [in] msg
   * @return EdgeResult
   */
  private EdgeResult method(EdgeMessage msg) throws Exception {
    logger.info("method command");

    EdgeResult ret = null;
   
    String methodName = msg.getRequest().getEdgeNodeInfo().getValueAlias();
    EdgeMethodProvider methodProvider = EdgeServices.getMethodProvider(methodName);
    EdgeMethodService methodService = methodProvider.getMethodService(methodName);
    
    if (msg.getCommand() == EdgeCommandType.CMD_METHOD) {
      try {
        ret = methodService.runMethodAsync(msg.getRequest().getEdgeNodeInfo(), msg.getRequest().getMessage(), msg.getEdgeEndpointInfo());
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        ErrorHandler.getInstance().addErrorMessage(msg.getRequest().getEdgeNodeInfo(),
            new EdgeResult.Builder(EdgeStatusCode.STATUS_INTERNAL_ERROR).build(),
            msg.getRequest().getRequestId());
      }
    }
    return ret;
  }
}
