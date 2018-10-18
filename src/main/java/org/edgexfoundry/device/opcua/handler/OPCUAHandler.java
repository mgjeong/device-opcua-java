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

package org.edgexfoundry.device.opcua.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.command.json.format.EdgeData;
import org.command.json.format.EdgeElement;
import org.command.json.format.EdgeJsonFormatter;
import org.edge.protocol.opcua.api.common.EdgeCommandType;
import org.edgexfoundry.device.opcua.adapter.metadata.OPCUACommandIdentifier;
import org.edgexfoundry.device.opcua.data.ObjectStore;
import org.edgexfoundry.device.opcua.data.ProfileStore;
import org.edgexfoundry.device.opcua.domain.OPCUAObject;
import org.edgexfoundry.device.opcua.domain.ScanList;
import org.edgexfoundry.device.opcua.domain.Transaction;
import org.edgexfoundry.device.opcua.opcua.DeviceDiscovery;
import org.edgexfoundry.device.opcua.opcua.OPCUADriver;
import org.edgexfoundry.device.opcua.opcua.ObjectTransform;
import org.edgexfoundry.domain.core.Reading;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.ResourceOperation;
import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OPCUAHandler {

  private static final EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(OPCUAHandler.class);

  @Autowired
  private OPCUADriver driver;

  @Autowired
  private DeviceDiscovery discover;

  @Autowired
  private ProfileStore profiles;

  @Autowired
  private ObjectTransform transform;

  @Autowired
  private ObjectStore objectCache;

  @Autowired
  private CoreDataMessageHandler processor;

  @Value("${opcua.device.init:#{null}}")
  private String opcuaInit;
  @Value("${opcua.device.init.args:#{null}}")
  private String opcuaInitArgs;
  @Value("${opcua.device.remove:#{null}}")
  private String opcuaRemove;
  @Value("${opcua.device.remove.args:#{null}}")
  private String opcuaRemoveArgs;

  public Map<String, Transaction> transactions = new HashMap<String, Transaction>();

  public void initialize() {
    if (driver != null) {
      driver.initialize();
    }
  }

  public void initializeDevice(Device device) {
    if (opcuaInit != null && commandExists(device, opcuaInit)) {
      executeCommand(device, opcuaInit, opcuaInitArgs);
    }

    logger.info("Initialized Device: " + device.getName());
  }

  public void disconnectDevice(Device device) {
    if (opcuaRemove != null && commandExists(device, opcuaRemove)) {
      executeCommand(device, opcuaRemove, opcuaRemoveArgs);
    }

    driver.disconnectDevice(device.getAddressable());
    logger.info("Disconnected Device: " + device.getName());
  }

  public void scan() {
    ScanList availableList = null;
    availableList = driver.discover();
    discover.provision(availableList);
  }

  public boolean commandExists(Device device, String command) {
    Map<String, Map<String, List<ResourceOperation>>> cmdsForDevice =
        profiles.getCommands().get(device.getName());
    Map<String, List<ResourceOperation>> op = cmdsForDevice.get(command.toLowerCase());
    if (op == null) {
      return false;
    }

    return true;
  }

  public List<EdgeElement> executeCommand(Device device, String cmd, String arguments) {
    // set immediate flag to false to read from object cache of last readings
    Boolean immediate = true;
    Transaction transaction = new Transaction();
    String transactionId = transaction.getTransactionId();
    transactions.put(transactionId, transaction);
    executeOperations(device, cmd, arguments, immediate, transactionId);

    synchronized (transactions) {
      while (!transactions.get(transactionId).isFinished()) {
        try {
          transactions.wait();
        } catch (InterruptedException e) {
          // Exit quietly on break
          return null;
        }
      }
    }

    // When command is wellknown~stopcommand, it doesn't send to coredata.
    if(OPCUACommandIdentifier.WELLKNOWN_COMMAND_STOP.getValue().equals(cmd)) {
      return null;
    }

    List<Reading> readings = transactions.get(transactionId).getReadings();
    transactions.remove(transactionId);

    return processor.sendCoreData(device.getName(), readings,
        profiles.getObjects().get(device.getName()));
  }

  private void executeOperations(Device device, String commandName, String arguments,
      Boolean immediate, String transactionId) {
    String method = (arguments == null) ? "get" : "set";

    String deviceName = device.getName();
    String deviceId = device.getId();
    // get the objects for this device
    Map<String, OPCUAObject> objects = profiles.getObjects().get(deviceName);

    // get the operations for this device's object operation method
    List<ResourceOperation> operations =
        getResourceOperations(deviceName, deviceId, transactionId, commandName, method);
    Map<String, ResourceOperation> operationsMap = new HashMap<String, ResourceOperation>();
    for (ResourceOperation operation : operations) {
      operationsMap.put(operation.getOperation(), operation);
    }

    // parse arguments
    List<EdgeElement> elementList = null;
    if (arguments != null) {
      EdgeData data = EdgeJsonFormatter.decodeJsonStringToEdgeData(arguments);
      elementList = data.getEdgeElementList();
    } else {
      // get command == read command
      elementList = new ArrayList<EdgeElement>();
      elementList.add(new EdgeElement(EdgeCommandType.CMD_READ.getValue()));
    }

    for (EdgeElement edgeElement : elementList) {
      ResourceOperation operation = operationsMap.get(edgeElement.getElementTitle());
      if (operation == null) {
        continue;
      }

      String objectName = operation.getObject();
      OPCUAObject object = getOPCUAObject(objects, objectName, transactionId);

      // command operation for client processing
      if (requiresQuery(immediate, method, device, operation)) {
        String opId = transactions.get(transactionId).newOpId();
        new Thread(
            () -> driver.process(operation, device, object, edgeElement, transactionId, opId))
                .start();;
      }

    }
  }

  private Boolean requiresQuery(boolean immediate, String method, Device device,
      ResourceOperation operation) {
    // if the immediate flag is set
    if (immediate) {
      return true;
    }
    // if the resource operation method is a set
    if (method.equals("set")) {
      return true;
    }
    // if the objectCache has no values
    if (objectCache.get(device, operation) == null) {
      return true;
    }

    return false;
  }

  private OPCUAObject getOPCUAObject(Map<String, OPCUAObject> objects, String objectName,
      String transactionId) {
    OPCUAObject object = objects.get(objectName);

    if (object == null) {
      logger.error("Object " + objectName + " not found");
      String opId = transactions.get(transactionId).newOpId();
      completeTransaction(transactionId, opId, new ArrayList<Reading>());
      throw new NotFoundException("DeviceObject", objectName);
    }

    return object;
  }

  private List<ResourceOperation> getResourceOperations(String deviceName, String deviceId,
      String transactionId, String commandName, String method) {
    // get this device's resources map
    Map<String, Map<String, List<ResourceOperation>>> resources =
        profiles.getCommands().get(deviceName);

    if (resources == null) {
      logger.error("Command requested for unknown device " + deviceName);
      String opId = transactions.get(transactionId).newOpId();
      completeTransaction(transactionId, opId, new ArrayList<Reading>());
      throw new NotFoundException("Device", deviceId);
    }

    // get the get and set resources for this device's object
    Map<String, List<ResourceOperation>> resource = resources.get(commandName.toLowerCase());

    if (resource == null || resource.get(method) == null) {
      logger.error("Resource " + commandName + " not found");
      String opId = transactions.get(transactionId).newOpId();
      completeTransaction(transactionId, opId, new ArrayList<Reading>());
      throw new NotFoundException("Command", commandName);
    }

    // get the operations for this device's object operation method
    return resource.get(method);
  }

  public void completeTransaction(String transactionId, String opId, List<Reading> readings) {
    synchronized (transactions) {
      transactions.get(transactionId).finishOp(opId, readings);
      transactions.notifyAll();
    }
  }
}
