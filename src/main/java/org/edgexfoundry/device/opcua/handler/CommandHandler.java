/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @microservice: device-opcua-java
 * @author: Tyler Cox, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.device.opcua.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.command.json.format.EdgeData;
import org.command.json.format.EdgeFormatIdentifier;
import org.command.json.format.EdgeJsonFormatter;
import org.edgexfoundry.device.opcua.Initializer;
import org.edgexfoundry.device.opcua.data.DeviceStore;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.exception.controller.LockedException;
import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommandHandler {

  private static final EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(CommandHandler.class);

  @Autowired
  OPCUAHandler OPCUA;

  @Autowired
  DeviceStore devices;

  @Autowired
  Initializer init;

  public String getResponse(String deviceId, String cmd, String arguments) {
    if (init.isServiceLocked()) {
      logger.error("GET request cmd: " + cmd + " with device service locked on: " + deviceId);
      throw new LockedException("GET request cmd: " + cmd
          + " with device service locked on: " + deviceId);
    }

    if (devices.isDeviceLocked(deviceId)) {
      logger.error("GET request cmd: " + cmd + " with device locked on: " + deviceId);
      throw new LockedException("GET request cmd: " + cmd + " with device locked on: " + deviceId);
    }
    
    Device device = devices.getDeviceById(deviceId);
    if (OPCUA.commandExists(device, cmd)) {
      EdgeData resultData = new EdgeData(EdgeFormatIdentifier.DEFAULT_VERSION.getValue(),
          EdgeFormatIdentifier.OPCUA_TYPE.getValue(), OPCUA.executeCommand(device, cmd, arguments));
      return EdgeJsonFormatter.encodeEdgeDataToJsonString(resultData);
    } else {
      logger.error("Command: " + cmd + " does not exist for device with id: " + deviceId);
      throw new NotFoundException("Command", cmd);
    }
  }

  public String getResponses(String cmd, String arguments) {

    if (init.isServiceLocked()) {
      logger.error("GET request cmd: " + cmd + " with device service locked ");
      throw new LockedException("GET request cmd: " + cmd + " with device locked");
    }
    
    EdgeData resultData = new EdgeData(EdgeFormatIdentifier.DEFAULT_VERSION.getValue(),
        EdgeFormatIdentifier.OPCUA_TYPE.getValue());

    for (String deviceId: devices.getDevices().entrySet().stream()
        .map(d -> d.getValue().getId()).collect(Collectors.toList())) {
      if (devices.isDeviceLocked(deviceId)) {
        continue;
      }
      
      Device device = devices.getDeviceById(deviceId);
      if (OPCUA.commandExists(device, cmd)) {
        resultData.getEdgeElementList().addAll(OPCUA.executeCommand(device, cmd, arguments));
      }
    }
    return EdgeJsonFormatter.encodeEdgeDataToJsonString(resultData);
  }
}
