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
import org.springframework.stereotype.Repository;
import org.edgexfoundry.controller.DeviceProfileClient;
import org.edgexfoundry.controller.ValueDescriptorClient;
import org.edgexfoundry.device.opcua.domain.OPCUAObject;
import org.edgexfoundry.domain.common.IoTType;
import org.edgexfoundry.domain.common.ValueDescriptor;
import org.edgexfoundry.domain.meta.Command;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.DeviceObject;
import org.edgexfoundry.domain.meta.DeviceProfile;
import org.edgexfoundry.domain.meta.ProfileResource;
import org.edgexfoundry.domain.meta.PropertyValue;
import org.edgexfoundry.domain.meta.ResourceOperation;
import org.edgexfoundry.domain.meta.Units;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;

@Repository
public class ProfileStore {
  private final static EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(ProfileStore.class);

  @Autowired
  private ValueDescriptorClient valueDescriptorClient;

  @Autowired
  private DeviceProfileClient deviceProfileClient;

  private List<ValueDescriptor> valueDescriptors = new ArrayList<ValueDescriptor>();

  // map (key of device name) to cache of each devices resources keyed by
  // resource name
  // mapped to resource operations arrays keyed by get or put operation
  private Map<String, Map<String, Map<String, List<ResourceOperation>>>> commands = new HashMap<>();

  public Map<String, Map<String, Map<String, List<ResourceOperation>>>> getCommands() {
    return commands;
  }

  // map (key of device name) to cache each devices profile objects by
  // profile
  // object key
  private Map<String, Map<String, OPCUAObject>> objects = new HashMap<>();

  public Map<String, Map<String, OPCUAObject>> getObjects() {
    return objects;
  }

  public void updateDevice(Device device) {
    removeDevice(device);
    addDevice(device);
  }

  public void removeDevice(Device device) {
    objects.remove(device.getName());
    commands.remove(device.getName());
  }

  public void addDevice(Device device) {

    // put the device's profile resources in the commands map
    Map<String, Map<String, List<ResourceOperation>>> deviceOperations = new HashMap<>();
    List<ValueDescriptor> descriptors;
    try {
      descriptors = valueDescriptorClient.valueDescriptors();
    } catch (Exception e) {
      descriptors = new ArrayList<ValueDescriptor>();
    }
    List<ResourceOperation> ops = new ArrayList<ResourceOperation>();

    // If profile is not complete, update it
    if (device.getProfile().getDeviceResources() == null) {
      DeviceProfile profile =
          deviceProfileClient.deviceProfileForName(device.getProfile().getName());
      device.setProfile(profile);
      addDevice(device);
      return;
    }
    List<String> usedDescriptors = new ArrayList<String>();
    for (Command command : device.getProfile().getCommands()) {
      usedDescriptors.addAll(command.associatedValueDescriptors());
    }
    for (ProfileResource resource : device.getProfile().getResources()) {
      Map<String, List<ResourceOperation>> operations =
          new HashMap<String, List<ResourceOperation>>();
      operations.put("get", resource.getGet());
      operations.put("set", resource.getSet());
      deviceOperations.put(resource.getName().toLowerCase(), operations);
      if (resource.getGet() != null)
        ops.addAll(resource.getGet());
      if (resource.getSet() != null)
        ops.addAll(resource.getSet());
    }
    // put the device's profile objects in the objects map
    // put the device's profile objects in the commands map if no resource
    // exists
    Map<String, OPCUAObject> deviceObjects = new HashMap<>();
    for (DeviceObject object : device.getProfile().getDeviceResources()) {
      OPCUAObject OPCUAObject = new OPCUAObject(object);

      PropertyValue value = object.getProperties().getValue();

      deviceObjects.put(object.getName(), OPCUAObject);

      // if there is no resource defined for an object, create one based
      // on the RW parameters
      if (!deviceOperations.containsKey(object.getName().toLowerCase())) {
        String readWrite = value.getReadWrite();

        Map<String, List<ResourceOperation>> operations =
            new HashMap<String, List<ResourceOperation>>();
        if (readWrite.toLowerCase().contains("r")) {
          ResourceOperation resource = new ResourceOperation("get", object.getName());
          List<ResourceOperation> getOp = new ArrayList<ResourceOperation>();
          getOp.add(resource);
          operations.put(resource.getOperation().toLowerCase(), getOp);
          ops.add(resource);
        }
        if (readWrite.toLowerCase().contains("w")) {
          ResourceOperation resource = new ResourceOperation("set", object.getName());
          List<ResourceOperation> setOp = new ArrayList<ResourceOperation>();
          setOp.add(resource);
          operations.put(resource.getOperation().toLowerCase(), setOp);
          ops.add(resource);
        }

        deviceOperations.put(object.getName().toLowerCase(), operations);
      }
      /*
       * Add automatically create ValueDescriptor using ObjectDeviceName Because Use
       * ObjectDeviceName as a ValueDescriptor
       * 
       * @Author jeongin.kim@samsung.com
       */

      int removeNum = 0;
      for (ValueDescriptor valueDescriptor : valueDescriptors) {
        if (valueDescriptor.getName().equals(object.getName()) == true) {
          break;
        }
        removeNum++;
      }

      ValueDescriptor descriptor = createDescriptor(object.getName(), object, device);

      if (removeNum < valueDescriptors.size()) {
        valueDescriptors.remove(removeNum);
      }
      valueDescriptors.add(descriptor);
    }
    objects.put(device.getName(), deviceObjects);
    commands.put(device.getName(), deviceOperations);
    logger.debug("Device name : {} , DeviceObjects : {}", device.getName(), deviceObjects);

    /*
     * Delete create ValueDescriptor using operation.parameter Because Not use operation.parameter
     * as a ValueDescriptor
     * 
     * @Author jeongin.kim@samsung.com
     */

  }

  private ValueDescriptor createDescriptor(String name, DeviceObject object, Device device) {
    PropertyValue value = object.getProperties().getValue();
    Units units = object.getProperties().getUnits();
    ValueDescriptor descriptor = new ValueDescriptor(name, value.getMinimum(), value.getMaximum(),
        IoTType.valueOf(value.getType().substring(0, 1)), units.getDefaultValue(),
        value.getDefaultValue(), "%s", null, object.getDescription());
    ValueDescriptor preDescriptor = valueDescriptorClient.valueDescriptorByName(name);
    if (preDescriptor == null) {
      try {
        descriptor.setId(valueDescriptorClient.add(descriptor));
      } catch (Exception e) {
        logger.error("Adding Value descriptor: " + descriptor.getName() + " failed with error "
            + e.getMessage());
      }
    } else {
      /*
       * TODO After all event deleted related this valueDescriptor, it is updated So, coredata
       * delete bug is fixed, then add this code
       * 
       * @Author jeongin.kim@samsung.com
       * 
       * if (compare(descriptor, preDescriptor) == false) {
       * 
       * try { update(preDescriptor, descriptor); valueDescriptorClient.update(preDescriptor); }
       * catch (Exception e) { logger.error("Update Value descriptor: " + descriptor.getName() +
       * " failed with error " + e.getMessage()); } }
       */
      descriptor = preDescriptor;
    }
    return descriptor;
  }

  /*
   * TODO After all event deleted related this valueDescriptor, it is updated So, coredata delete
   * bug is fixed, then add this code
   * 
   * @Author jeongin.kim@samsung.com
   * 
   * private boolean compare(ValueDescriptor a, ValueDescriptor b) { if
   * (a.getType().equals(b.getType()) && a.getUomLabel().equals(b.getUomLabel()) &&
   * a.getFormatting().equals(b.getFormatting()) && a.getName().equals(b.getName())) { return true;
   * } return false; }
   * 
   * private void update(ValueDescriptor a, ValueDescriptor b) { a.setMax(b.getMax());
   * a.setMin(b.getMin()); a.setType(b.getType()); a.setDescription(b.getDescription());
   * a.setDefaultValue(b.getDefaultValue()); a.setFormatting(b.getFormatting());
   * a.setLabels(b.getLabels()); a.setUomLabel(b.getUomLabel()); }
   */

  public List<ValueDescriptor> getValueDescriptors() {
    return valueDescriptors;
  }

  public boolean descriptorExists(String name) {
    return !getValueDescriptors().stream().filter(desc -> desc.getName().equals(name))
        .collect(Collectors.toList()).isEmpty();
  }
}
