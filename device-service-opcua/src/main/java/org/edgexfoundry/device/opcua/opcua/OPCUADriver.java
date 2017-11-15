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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.ProtocolManager.DiscoveryCallback;
import org.edge.protocol.opcua.api.ProtocolManager.ReceivedMessageCallback;
import org.edge.protocol.opcua.api.ProtocolManager.StatusCallback;
import org.edge.protocol.opcua.api.client.EdgeResponse;
import org.edge.protocol.opcua.api.common.EdgeBrowseResult;
import org.edge.protocol.opcua.api.common.EdgeCommandType;
import org.edge.protocol.opcua.api.common.EdgeConfigure;
import org.edge.protocol.opcua.api.common.EdgeDevice;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeEndpointConfig;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeMessageType;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeRequest;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edge.protocol.opcua.example.EdgeEmulator;
import org.edgexfoundry.controller.EventClient;
import org.edgexfoundry.device.opcua.DataDefaultValue;
import org.edgexfoundry.device.opcua.coredata.EventGenerator;
import org.edgexfoundry.device.opcua.data.DeviceStore;
import org.edgexfoundry.device.opcua.data.ObjectStore;
import org.edgexfoundry.device.opcua.data.ProfileStore;
import org.edgexfoundry.device.opcua.domain.OPCUAAttribute;
import org.edgexfoundry.device.opcua.domain.OPCUAObject;
import org.edgexfoundry.device.opcua.domain.ScanList;
import org.edgexfoundry.device.opcua.emf.EMFAdapter;
import org.edgexfoundry.device.opcua.handler.OPCUAHandler;
import org.edgexfoundry.device.opcua.metadata.DeviceEnroller;
import org.edgexfoundry.device.opcua.metadata.MetaDataType;
import org.edgexfoundry.domain.core.Event;
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
    private String endpointUri = null;
    private EdgeEmulator opcUaAdapter;
    private boolean emfMode = false;
    
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
            String value, String transactionId, String opId) {
        String result = "";

        // TODO 2: [Optional] Modify this processCommand call to pass any
        // additional required metadata from the profile to the driver stack
        /*
         * result = processCommand(operation.getOperation(),
         * device.getAddressable(), object.getAttributes(), value);
         */

        result = processCommand(operation.getOperation(), device.getAddressable(),
                object.getAttributes(), operation.getParameter(), value);

        objectCache.put(device, operation, result);
        handler.completeTransaction(transactionId, opId,
                objectCache.getResponses(device, operation));
    }

    // Modify this function as needed to pass necessary metadata from the device
    // and its profile to the driver interface
    public String processCommand(String operation, Addressable addressable,
            OPCUAAttribute attributes, String parameters, String value) {
        String address = addressable.getPath();
        String intface = addressable.getAddress();
        logger.debug("ProcessCommand: " + operation + ", interface: " + intface + ", address: "
                + address + ", attributes: " + attributes + ", value: " + value);
        String result = "Default";

        // Get command
        CompletableFuture<EdgeMessage> future = new CompletableFuture<>();
        future.whenComplete((message, ex) -> {
        });

        EdgeNodeInfo nodeInfo = new EdgeNodeInfo.Builder()
                .setValueAlias(attributes.getProviderKey()).build();

        EdgeVersatility message = null;

        if (operation.toLowerCase().equals(EdgeCommandType.CMD_WRITE.getValue())) {
            message = new EdgeVersatility.Builder("OFF").build();
        }

        EdgeEndpointInfo ep = new EdgeEndpointInfo.Builder(endpointUri).setFuture(future).build();
        EdgeMessage msg = null;
        if (operation.toLowerCase().equals(EdgeCommandType.CMD_READ.getValue())) {
        	msg = new EdgeMessage.Builder(ep).setCommand(EdgeCommandType.CMD_READ)
                    .setMessageType(EdgeMessageType.SEND_REQUEST).setRequest(
                    		new EdgeRequest.Builder(nodeInfo).setMessage(message).build()).build();
        } else if (operation.toLowerCase().equals(EdgeCommandType.CMD_WRITE.getValue())) {
        	msg = new EdgeMessage.Builder(ep).setCommand(EdgeCommandType.CMD_WRITE)
                    .setMessageType(EdgeMessageType.SEND_REQUEST).setRequest(
                    		new EdgeRequest.Builder(nodeInfo).setMessage(message).build()).build();
        } else {
        	logger.debug("operation is not supported : " + operation);
        	return result;
        }  	       

        try {
            ProtocolManager.getProtocolManagerInstance().send(msg);
            if (operation.toLowerCase().equals(EdgeCommandType.CMD_READ.getValue()) ||
            		operation.toLowerCase().equals(EdgeCommandType.CMD_WRITE.getValue())) {
                EdgeMessage retEdgeMessage = future.get(10, TimeUnit.SECONDS);
                if (retEdgeMessage != null) {
                    result = retEdgeMessage.getResponses().get(0).getMessage().getValue().toString();
                }
            }
        } catch (Throwable t) {
            future.complete(null);
        }

        // TODO 1: [Required] OPCUA stack goes here, return the raw value from
        // TODO costumize here
        // the device as a string for processing
        /*
         * logger.debug("operation is  " + operation); if
         * (operation.toLowerCase().equals("get")) { Random rand = new Random();
         * result = Float.toString(rand.nextFloat() * 100); } else { result =
         * value; }
         */

        // EdgeServices.ReceiveCommand(attributes.getProviderKey());

        return result;
    }

    public void initialize() {
        // TODO 3: [Optional] Initialize the interface(s) here if necessary,
        // runs once on service startup
        try {
        	if (emfMode == true) {
        	    EMFAdapter.getInstance().startAdapter();
        	} else {
        		  opcUaAdapter = new EdgeEmulator();
              startOPCUAAdapter();	
        	}
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void disconnectDevice(Addressable address) {
        // TODO 6: [Optional] Disconnect devices here using driver level
        // operations

    }

    private void receive(EdgeMessage data) {
        // TODO 7: [Optional] Fill with your own implementation for handling
        // asynchronous data from the driver layer to the device service

    	for (EdgeResponse res : data.getResponses()) {
            logger.info(
                    "[FROM OPCUA Stack] Data received = {} topic={}, endpoint={}, namespace={}, "
                            + "edgenodeuri={}, methodname={}, edgenodeId={} ",
                    res.getMessage().getValue(), 
                    res.getEdgeNodeInfo().getValueAlias(),
                    data.getEdgeEndpointInfo().getEndpointUri(),
                    res.getEdgeNodeInfo().getEdgeNodeID().getNameSpace(),
                    res.getEdgeNodeInfo().getEdgeNodeID().getEdgeNodeUri(), 
                    res.getEdgeNodeInfo().getMethodName(),
                    res.getEdgeNodeInfo().getEdgeNodeID().getEdgeNodeIdentifier());

    	}

        Device device = null;
        String result = "";
        ResourceOperation operation = null;

        objectCache.put(device, operation, result);

    }

    ReceivedMessageCallback receiver = new ReceivedMessageCallback() {
        @Override
        public void onResponseMessages(EdgeMessage data) {
            // TODO Auto-generated method stub
            CompletableFuture<EdgeMessage> future = data.getEdgeEndpointInfo().getFuture();
            if (future != null) {
                future.complete(data);
            }
        }

        @Override
        public void onMonitoredMessage(EdgeMessage data) {
            // TODO Auto-generated method stub
            receive(data);

            for (EdgeResponse res : data.getResponses()) {
            	Event event = EventGenerator.newEvent(
            			res.getEdgeNodeInfo().getValueAlias().replaceAll("/", DataDefaultValue.REPLACE_DEVICE_NAME),
            			res.getMessage().toString());

                try {
                    logger.debug(event.getDevice() + " : [TO CoreData] Add Event successfully: "
                            + eventClient.add(event));
                } catch (Exception e) {
                    logger.error("Could not add Event to coredata msg: " + e.getMessage());
                }
            }
        }

		@Override
		public void onErrorMessage(EdgeMessage data) {
			// TODO Auto-generated method stub
			logger.info("[Error] onErrorMessage");
		}

		@Override
		public void onBrowseMessage(EdgeNodeInfo endpoint, List<EdgeBrowseResult> responses, int requestId) {
			// TODO Auto-generated method stub
			
		}
    };

    DiscoveryCallback discoveryCallback = new DiscoveryCallback() {
        @Override
        public void onFoundEndpoint(EdgeDevice device) {
            // TODO Auto-generated method stub
            logger.info("[Event] onFoundEndpoint");
            for (EdgeEndpointInfo ep : device.getEndpoints()) {
                logger.info("-> {}, {}", ep.getEndpointUri(),
                        ep.getConfig().getSecurityPolicyUri());
            }
        }

        @Override
        public void onFoundDevice(EdgeDevice device) {
            // TODO Auto-generated method stub
            logger.info("[Event] onFoundDevice is not supported");
        }
    };

    StatusCallback statusCallback = new StatusCallback() {

        @Override
        public void onStart(EdgeEndpointInfo ep, EdgeStatusCode status,
                List<String> attiributeAliasList, List<String> methodAliasList,
                List<String> viewAliasList) {
            // TODO Auto-generated method stub
            logger.info("onStart({})", ep.getEndpointUri());
            
            if (status == EdgeStatusCode.STATUS_CLIENT_STARTED) {
                try {
                    deviceEnroller.initialize();
                    opcUaAdapter.runClientSub();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }	
            } else if (status == EdgeStatusCode.STATUS_SERVER_STARTED) {
            	try {
              	  // Create Namespace and nodes
               	  opcUaAdapter.runAutoUpdateServer();
              	  opcUaAdapter.initServerNodes();
                  // startClient
                  opcUaAdapter.startClient(ep);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }	
            }
        }

        @Override
        public void onStop(EdgeEndpointInfo ep, EdgeStatusCode status) {
            // TODO Auto-generated method stub
            logger.info("onStop({})", ep.getEndpointUri());
            deviceEnroller.cleanCoreData();
            deviceEnroller.cleanMetaData(MetaDataType.ALL);
        }

        @Override
        public void onNetworkStatus(EdgeEndpointInfo ep, EdgeStatusCode status) {
            // TODO Auto-generated method stub
            logger.info("onNetworkStatus: status {} from {}", status, ep.getEndpointUri());
            if (EdgeStatusCode.STATUS_DISCONNECTED == status) {
                deviceEnroller.cleanCoreData();
                deviceEnroller.cleanMetaData(MetaDataType.DEVICE);
                deviceEnroller.cleanMetaData(MetaDataType.DEVICE_PROFILE);
            }
        }
    };

    public void startOPCUAAdapter() throws Exception {
        // 1. run discovery device
        // TODO
        // we need to support like discovery-seed micro-service

        // 2. create addressable (by default)
        Addressable addressable = new Addressable(DataDefaultValue.NAME.getValue(), Protocol.TCP,
                DataDefaultValue.ADDRESS.getValue(), DataDefaultValue.PATH.getValue(),
                DataDefaultValue.ADDRESSABLE_PORT);

        // 3. get EdgeEndpoint URI
        endpointUri = getEndpointUrifromAddressable(addressable);

        // 4. set configure
        EdgeConfigure configure = new EdgeConfigure.Builder().setRecvCallback(receiver)
                .setStatusCallback(statusCallback).setDiscoveryCallback(discoveryCallback)
                .build();
        
        ProtocolManager protocolManager = ProtocolManager.getProtocolManagerInstance();
        protocolManager.configure(configure);

        // startServer
        EdgeEndpointConfig endpointConfig = new EdgeEndpointConfig.Builder()
                .setApplicationName(EdgeOpcUaCommon.DEFAULT_SERVER_APP_NAME.getValue())
                .setApplicationUri(EdgeOpcUaCommon.DEFAULT_SERVER_APP_URI.getValue()).build();
        EdgeEndpointInfo ep = new EdgeEndpointInfo.Builder(endpointUri).setConfig(endpointConfig).build();
        opcUaAdapter.startServer(ep, "");
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
}
