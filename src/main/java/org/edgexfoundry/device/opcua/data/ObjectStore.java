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

package org.edgexfoundry.device.opcua.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.edgexfoundry.device.opcua.domain.OPCUAObject;
import org.edgexfoundry.device.opcua.handler.CoreDataMessageHandler;
import org.edgexfoundry.device.opcua.opcua.ObjectTransform;
import org.edgexfoundry.domain.core.Reading;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.OperatingState;
import org.edgexfoundry.domain.meta.PropertyValue;
import org.edgexfoundry.domain.meta.ResourceOperation;
import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;
import com.google.gson.JsonObject;

@Repository
public class ObjectStore {
  private final static EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(ObjectStore.class);

  @Value("${data.transform:true}")
  private Boolean transformData;

  @Autowired
  private ProfileStore profiles;

  @Autowired
  private ObjectTransform transform;

  @Autowired
  private CoreDataMessageHandler processor;

  @Value("${data.cache.size:1}")
  private int CACHE_SIZE;

  private Map<String, Map<String, List<String>>> objectCache = new HashMap<>();

  private Map<String, Map<String, List<Reading>>> responseCache = new HashMap<>();

  public Boolean getTransformData() {
    return transformData;
  }

  public void setTransformData(Boolean transform) {
    transformData = transform;
  }

  public void put(Device device, ResourceOperation operation, String value) {
    if (value == null || value.equals("") || value.equals("{}"))
      return;
    List<OPCUAObject> objectsList = createObjectsList(operation, device);

    String deviceId = device.getId();
    List<Reading> readings = new ArrayList<>();
    logger.debug("ObjectsList : {}", objectsList);
    for (OPCUAObject obj : objectsList) {
      String objectName = obj.getName();
      String result = transformResult(value, obj, device, operation);
      logger.debug("value TransFromResult : {}", result);
      Reading reading = processor.buildReading(objectName, result, device.getName());
      readings.add(reading);

      synchronized (objectCache) {
        if (objectCache.get(deviceId) == null)
          objectCache.put(deviceId, new HashMap<String, List<String>>());
        if (objectCache.get(deviceId).get(objectName) == null)
          objectCache.get(deviceId).put(objectName, new ArrayList<String>());
        objectCache.get(deviceId).get(objectName).add(0, result);
        if (objectCache.get(deviceId).get(objectName).size() == CACHE_SIZE)
          objectCache.get(deviceId).get(objectName).remove(CACHE_SIZE - 1);
      }
    }

    String operationId =
        objectsList.stream().map(o -> o.getName()).collect(Collectors.toList()).toString();

    synchronized (responseCache) {
      if (responseCache.get(deviceId) == null)
        responseCache.put(deviceId, new HashMap<String, List<Reading>>());
      // Add operationIndex in operation key, At the same time, the same order was used.
      // @author jeongin.kim@samsung.com
      responseCache.get(deviceId).put(operationId + operation.getIndex(), readings);
    }
  }

  private List<OPCUAObject> createObjectsList(ResourceOperation operation, Device device) {
    Map<String, OPCUAObject> objects = profiles.getObjects().get(device.getName());

    logger.debug("Raw objects : {}", profiles.getObjects());
    logger.debug("objects : {}", objects);
    logger.debug("Device name : {}", device.getName());

    List<OPCUAObject> objectsList = new ArrayList<OPCUAObject>();
    if (operation != null && objects != null) {
      OPCUAObject object = objects.get(operation.getObject());
      logger.debug("OPCUAObject's name : {}", object.getName());
      logger.debug("OPCUAObject's parameter name : {}", operation.getParameter());

      /*
       * Delete Check ValueDescriptor Exist and Get(and Create) ValueDescriptor Using
       * operation.parameter Because OPCUAObject name is changed operation.parameter, This is
       * Dangerous
       */
      if (profiles.descriptorExists(object.getName())) {
        logger.info("ValueDescriptor(OPCUAObject) Exist in ProfileStore");
        objectsList.add(object);
      }

      if (operation.getSecondary() != null)
        for (String secondary : operation.getSecondary())
          if (profiles.descriptorExists(secondary))
            objectsList.add(objects.get(secondary));
    }
    logger.debug("OPCUAObjectsList : {}", objectsList);
    return objectsList;
  }

  private String transformResult(String result, OPCUAObject object, Device device,
      ResourceOperation operation) {

    PropertyValue propValue = object.getProperties().getValue();

    String transformResult = transform.transform(propValue, result);

    // if there is an assertion set for the object on a get command, test it
    // if it fails the assertion, pass error to core services (disable
    // device?)
    if (propValue.getAssertion() != null)
      if (!transformResult.equals(propValue.getAssertion().toString())) {
        device.setOperatingState(OperatingState.disabled);
        return "Assertion failed with value: " + transformResult;
      }

    Map<String, String> mappings = operation.getMappings();

    if (mappings != null && mappings.containsKey(transformResult))
      transformResult = mappings.get(transformResult);

    return transformResult;
  }

  public String get(String deviceId, String object) {
    return get(deviceId, object, 1).get(0);
  }

  private List<String> get(String deviceId, String object, int i) {
    if (objectCache.get(deviceId) == null || objectCache.get(deviceId).get(object) == null
        || objectCache.get(deviceId).get(object).size() < i)
      return null;
    return objectCache.get(deviceId).get(object).subList(0, i);
  }

  public JsonObject get(Device device, ResourceOperation operation) {
    JsonObject jsonObject = new JsonObject();
    List<OPCUAObject> objectsList = createObjectsList(operation, device);
    for (OPCUAObject obj : objectsList) {
      String objectName = obj.getName();
      jsonObject.addProperty(objectName, get(device.getId(), objectName));
    }
    return jsonObject;
  }

  public List<Reading> getResponses(Device device, ResourceOperation operation) {
    String deviceId = device.getId();
    List<OPCUAObject> objectsList = createObjectsList(operation, device);
    if (objectsList == null)
      throw new NotFoundException("device", deviceId);
    String operationId =
        objectsList.stream().map(o -> o.getName()).collect(Collectors.toList()).toString();
    if (responseCache.get(deviceId) == null
        || responseCache.get(deviceId).get(operationId + operation.getIndex()) == null)
      return new ArrayList<Reading>();
    return responseCache.get(deviceId).get(operationId + operation.getIndex());
  }

}
