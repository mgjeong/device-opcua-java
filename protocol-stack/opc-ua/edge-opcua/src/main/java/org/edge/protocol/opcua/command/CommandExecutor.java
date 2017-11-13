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
import java.util.concurrent.TimeUnit;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provide function for execute command
 */
public class CommandExecutor {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final CompletableFuture<EdgeResult> future = new CompletableFuture<>();

  private final Command command;

  /**
   * CTOR
   */
  public CommandExecutor(Command command) throws Exception {
    this.command = command;
  }

  /**
   * @fn void run(EdgeMessage msg)
   * @brief excute with EdgeMessage
   * @prarm [in] msg
   * @return void
   */
  public void run(EdgeMessage msg) {
    future.whenComplete((ret, ex) -> {
      logger.info("operation has called");
    });

    try {
      try {
        command.execute(future, msg);
        future.get(10, TimeUnit.SECONDS);
      } catch (Throwable t) {
        future.complete(new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build());
      }
    } catch (Throwable t) {
      future.completeExceptionally(t);
    }
  }
}
