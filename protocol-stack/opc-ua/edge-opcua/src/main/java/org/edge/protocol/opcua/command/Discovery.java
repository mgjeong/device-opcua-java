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
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;

/**
 * This class provide discovery function
 */
public class Discovery implements Command {

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
    EdgeResult ret = discovery(msg);
    future.complete(ret);
  }

  /**
   * @fn EdgeResult discovery(EdgeMessage msg)
   * @brief discovery with EdgeMessage
   * @prarm [in] msg
   * @return EdgeResult
   */
  private EdgeResult discovery(EdgeMessage msg) throws Exception {
    return new EdgeResult.Builder(EdgeStatusCode.STATUS_NOT_SUPPROT).build();
  }
}
