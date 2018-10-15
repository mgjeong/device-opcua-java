/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
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
 * @microservice: device-opcua-java
 * @author: Tyler Cox, Dell
 * @version: 1.0.0
 *******************************************************************************/
package org.edgexfoundry.device.opcua.controller;

import java.util.concurrent.Callable;
import org.edgexfoundry.device.opcua.handler.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;

@RestController
@RequestMapping("/api/v1/device")
public class CommandController {
  private static final EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(CommandHandler.class);

  @Autowired
  private CommandHandler command;

  // using String (datamodel-command-java format) for result
  // @author jeongin.kim@samsung.com
  @RequestMapping(value = "/{deviceId}/{cmd:.+}",
      method = {RequestMethod.PUT, RequestMethod.POST, RequestMethod.GET})
  public Callable<String> getCommand(@PathVariable String deviceId, @PathVariable String cmd,
      @RequestBody(required = false) String arguments) {
    Callable<String> callable = new Callable<String>() {
      @Override
      public String call() throws Exception {
        logger.info("command : {}", cmd);
        return command.getResponse(deviceId, cmd, arguments);
      }
    };
    return callable;
  }

  // using String (datamodel-command-java format) for result
  // @author jeongin.kim@samsung.com
  @RequestMapping(value = "/all/{cmd}",
      method = {RequestMethod.PUT, RequestMethod.POST, RequestMethod.GET})
  public Callable<String> getCommands(@PathVariable String cmd,
      @RequestBody(required = false) String arguments) {
    Callable<String> callable = new Callable<String>() {
      @Override
      public String call() throws Exception {
        return command.getResponses(cmd, arguments);
      }
    };
    return callable;
  }
}