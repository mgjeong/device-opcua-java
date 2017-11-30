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
 * @microservice:  device-sdk-tools
 * @author: Tyler Cox, Dell
 * @version: 1.0.0
 *******************************************************************************/
package org.edgexfoundry.device.opcua.opcua;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.command.json.format.EdgeElement;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edgexfoundry.controller.EventClient;
import org.edgexfoundry.device.opcua.adapter.OPCUAAdapter;
import org.edgexfoundry.device.opcua.adapter.OPCUAMessageHandler;
import org.edgexfoundry.device.opcua.data.DeviceStore;
import org.edgexfoundry.device.opcua.data.ObjectStore;
import org.edgexfoundry.device.opcua.data.ProfileStore;
import org.edgexfoundry.device.opcua.domain.OPCUAAttribute;
import org.edgexfoundry.device.opcua.domain.OPCUAObject;
import org.edgexfoundry.device.opcua.domain.ScanList;
import org.edgexfoundry.device.opcua.handler.OPCUAHandler;
import org.edgexfoundry.device.opcua.metadata.DeviceEnroller;
import org.edgexfoundry.device.opcua.metadata.MetaDataType;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.Protocol;
import org.edgexfoundry.domain.meta.ResourceOperation;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;

@Service
@Component
public class OPCUADriver {

    private final static EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(OPCUADriver.class);
	
    @Autowired
    ProfileStore profiles;

    @Autowired
    DeviceStore devices;

    @Autowired
    ObjectStore objectCache;

    @Autowired
    OPCUAHandler handler;

    @Autowired
    DeviceEnroller deviceEnroller;

    @Autowired
    EventClient eventClient;

    public ScanList discover() {
        ScanList scan = new ScanList();

        // TODO 4: [Optional] For discovery enabled device services:
        // Replace with OPCUA specific discovery mechanism
        // TODO 5: [Required] Remove next code block if discovery is not used
        for (int i = 0; i < 10; i++) {
            Map<String, String> identifiers = new HashMap<>();
            identifiers.put("name", String.valueOf(i));
            identifiers.put("address", "02:01:00:11:12:1" + String.valueOf(i));
            identifiers.put("interface", "default");
            scan.add(identifiers);
        }

        return scan;
    }

    // operation is get or set
    // Device to be written to
    // OPCUA Object to be written to
    // value is string to be written or null
    public void process(ResourceOperation operation, Device device, OPCUAObject object,
            EdgeElement edgeElement, String transactionId, String opId) {
        String result = "";

        // TODO 2: [Optional] Modify this processCommand call to pass any
        // additional required metadata from the profile to the driver stack
        /*
         * result = processCommand(operation.getOperation(),
         * device.getAddressable(), object.getAttributes(), value);
         */

        result = processCommand(operation.getOperation(), device.getAddressable(),
                object.getAttributes(), operation.getParameter(), edgeElement);

        objectCache.put(device, operation, result);
        handler.completeTransaction(transactionId, opId,
                objectCache.getResponses(device, operation));
    }

    // Modify this function as needed to pass necessary metadata from the device
    // and its profile to the driver interface
    public String processCommand(String operation, Addressable addressable,
            OPCUAAttribute attributes, String parameters, EdgeElement edgeElement) {
        String address = addressable.getPath();
        String intface = addressable.getAddress();
        logger.debug("ProcessCommand: " + operation + ", interface: " + intface + ", address: "
                + address + ", attributes: " + attributes + ", edgeElement: " + edgeElement);
        String result = "none";

        // Get command
        CompletableFuture<EdgeMessage> future = new CompletableFuture<>();
        future.whenComplete((message, ex) -> {
        });

        EdgeMessage msg = null;
        try {
			      msg = OPCUAMessageHandler.getInstance().convertEdgeElementToEdgeMessage(edgeElement, operation, 
			    		  attributes.getProviderKey(), addressable, future);
		    } catch (Exception e) {
			      // TODO Auto-generated catch block
			      e.printStackTrace();
		        }
        
        try {
			      OPCUAMessageHandler.getInstance().sendMessage(msg);
			      EdgeMessage retEdgeMessage = future.get(10, TimeUnit.SECONDS);
			      if (retEdgeMessage != null) {
			          result = retEdgeMessage.getResponses().get(0).getMessage().getValue().toString();
			             }
		    } catch (Exception e) {
            // TODO Auto-generated catch block
			      e.printStackTrace();
			      future.complete(null);
			    }       

        return result;
    }

    public void initialize() {
        // TODO 3: [Optional] Initialize the interface(s) here if necessary,
        // runs once on service startup

        try {
        		OPCUAAdapter.getInstance(callback).testStartServer();
        	  OPCUAAdapter.getInstance(callback).startAdapter();	         		  	
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void disconnectDevice(Addressable address) {
        // TODO 6: [Optional] Disconnect devices here using driver level
        // operations

    }

    public String getEndpointUrifromAddressable(Addressable addressable) {
        String endpointUri = "";
        if (addressable.getProtocol() == Protocol.TCP) {
            endpointUri += String.format("%s", "opc.tcp://");
        } else {
            endpointUri += String.format("%s", "http://");
        }

        endpointUri += String.format("%s:%d/%s", addressable.getAddress(), addressable.getPort(),
                addressable.getPath());
        return endpointUri;
    }

    DriverCallback callback = new DriverCallback() {

		@Override
		public void onReceive(Device device, ResourceOperation operation, String result) {
			// TODO Auto-generated method stub
			objectCache.put(device, operation, result);
		}

		@Override
		public void onDeleteCoreData() {
			// TODO Auto-generated method stub
			deviceEnroller.cleanCoreData();
		}

		@Override
		public void onDeleteMetaData(MetaDataType type) {
			// TODO Auto-generated method stub
			deviceEnroller.cleanMetaData(type);			
		}

		@Override
		public void onInit() {
			// TODO Auto-generated method stub
			deviceEnroller.initialize();
		}
    };

    public interface DriverCallback {
    	  public void onInit();

        public void onReceive(Device device, ResourceOperation operation, String result);

        public void onDeleteCoreData();

        public void onDeleteMetaData(MetaDataType type);
      }
}