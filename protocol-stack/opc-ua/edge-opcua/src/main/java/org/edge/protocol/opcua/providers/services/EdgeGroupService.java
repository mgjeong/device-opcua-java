/******************************************************************
 *
 * Copyright 2017 Samsung Electronics All Rights Reserved.
 *
 *
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
 ******************************************************************/

package org.edge.protocol.opcua.providers.services;

import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableNode;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.WriteResponse;
import org.eclipse.milo.opcua.stack.core.types.structured.WriteValue;
import org.edge.protocol.mapper.api.EdgeMapper;
import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.client.EdgeResponse;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeMessageType;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeRequest;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edge.protocol.opcua.providers.EdgeAttributeProvider;
import org.edge.protocol.opcua.providers.EdgeServices;
import org.edge.protocol.opcua.providers.services.da.EdgeAttributeService;
import org.edge.protocol.opcua.queue.ErrorHandler;
import org.edge.protocol.opcua.session.EdgeSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeGroupService implements EdgeAttributeService {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private static EdgeGroupService service = null;
  private static Object lock = new Object();
  private double maxAge = 0.0;

  /**
   * @fn EdgeGroupService getInstance()
   * @brief get EdgeGroupService Instance
   * @return EdgeGroupService Instance
   */
  public static EdgeGroupService getInstance() {
    synchronized (lock) {
      if (null == service) {
        service = new EdgeGroupService();
      }
      return service;
    }
  }

  private CompletableFuture<List<DataValue>> readData(String endpointUri, EdgeMessage messages) {
    List<NodeId> nodeIds = new ArrayList<NodeId>();
    for (EdgeRequest req : messages.getRequests()) {
      // get NodoID from each services
      String serviceName = req.getEdgeNodeInfo().getValueAlias();
      EdgeAttributeProvider attributeProvider = EdgeServices.getAttributeProvider(serviceName);
      EdgeAttributeService service = attributeProvider.getAttributeService(serviceName);
      EdgeNodeIdentifier nodeType = null;
      try {
        nodeType = service.getNodeType();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if (nodeType == EdgeNodeIdentifier.Edge_Node_Custom_Type) {
        nodeIds.add(service.getNodeId());
      }
    }
    return EdgeSessionManager.getInstance().getSession(endpointUri).getClientInstance()
        .readValues(maxAge, TimestampsToReturn.Both, nodeIds);
  }

  /**
   * @fn EdgeResult readAsync(EdgeMessage msg)
   * @brief read async data for requests
   * @param [IN] msg edge message set
   * @return result
   */
  @Override
  public EdgeResult readAsync(EdgeMessage msg) throws Exception {
    // TODO Auto-generated method stub
    String endpointUri = msg.getEdgeEndpointInfo().getEndpointUri();
    List<EdgeResponse> responses = new ArrayList<EdgeResponse>();

    readData(endpointUri, msg).thenAccept(values -> {

      int index = 0;
      for (DataValue var : values) {
        logger.info("response size={}, var={} result={}", values.size(), var.getValue().getValue(),
            var.getStatusCode());

        if (var.getStatusCode().isGood() == false) {
          ErrorHandler.getInstance().addErrorMessage(msg.getRequests().get(index).getEdgeNodeInfo(),
              new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
              new EdgeVersatility.Builder(var.getStatusCode()).build(),
              msg.getRequests().get(index).getRequestId());
          continue;
        }
        responses.add(new EdgeResponse.Builder(msg.getRequests().get(index).getEdgeNodeInfo(),
            msg.getRequests().get(index).getRequestId())
                .setMessage(new EdgeVersatility.Builder(var.getValue().getValue()).build())
                .build());
        index++;
      }

      EdgeEndpointInfo epInfo =
          new EdgeEndpointInfo.Builder(msg.getEdgeEndpointInfo().getEndpointUri())
              .setFuture(msg.getEdgeEndpointInfo().getFuture()).build();
      EdgeMessage inputData = new EdgeMessage.Builder(epInfo)
          .setMessageType(EdgeMessageType.GENERAL_RESPONSE).setResponses(responses).build();
      ProtocolManager.getProtocolManagerInstance().getRecvDispatcher().putQ(inputData);

    }).exceptionally(e -> {
      logger.info("error type : {}", e.getMessage());
      return null;
    });
    logger.info("readAsyc is called");
    return new EdgeResult.Builder(EdgeStatusCode.STATUS_OK).build();
  }

  @Override
  public EdgeResult readSync(EdgeMessage msg) throws Exception {
    // TODO Auto-generated method stub
    logger.info("not support");
    return null;
  }

  private CompletableFuture<WriteResponse> writeData(String endpointUri, EdgeMessage messages)
      throws InterruptedException, ExecutionException {
    List<WriteValue> wrtievalue = new ArrayList<WriteValue>();

    for (EdgeRequest req : messages.getRequests()) {
      // get NodoID from each services
      String serviceName = req.getEdgeNodeInfo().getValueAlias();
      EdgeAttributeProvider attributeProvider = EdgeServices.getAttributeProvider(serviceName);
      EdgeAttributeService service = attributeProvider.getAttributeService(serviceName);
      EdgeNodeIdentifier nodeType = null;
      try {
        nodeType = service.getNodeType();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if (nodeType == EdgeNodeIdentifier.Edge_Node_Custom_Type) {
        logger.info("value = {}", req.getMessage().getValue());
        wrtievalue.add(new WriteValue(service.getNodeId(), uint(AttributeId.Value.id()), null,
            new DataValue(new Variant(req.getMessage().getValue()))));
      }
    }
    return EdgeSessionManager.getInstance().getSession(endpointUri).getClientInstance()
        .write(wrtievalue);
  }

  /**
   * @fn EdgeResult write(EdgeMessage msg)
   * @brief write data for several nodes.
   * @param [IN] msg edge message set
   * @return result
   */
  @Override
  public EdgeResult write(EdgeMessage msg) throws Exception {
    String endpointUri = msg.getEdgeEndpointInfo().getEndpointUri();
    List<EdgeResponse> responses = new ArrayList<EdgeResponse>();

    writeData(endpointUri, msg).thenApply(response -> {

      boolean isError = false;
      EdgeStatusCode statusCode = EdgeStatusCode.STATUS_OK;
      if (response.getResults().length == 0) {
        statusCode = EdgeStatusCode.STATUS_WRITE_EMPTY_RESULT;
        isError = true;
      } else if (msg.getRequests().size() > response.getResults().length) {
        statusCode = EdgeStatusCode.STATUS_WRITE_LESS_RESPONSE;
        isError = true;
      } else if (msg.getRequests().size() < response.getResults().length) {
        statusCode = EdgeStatusCode.STATUS_WRITE_TOO_MANY_RESPONSE;
        isError = true;
      }

      if (isError == true) {
        ErrorHandler.getInstance().addErrorMessage(msg.getRequests().get(0).getEdgeNodeInfo(),
            new EdgeResult.Builder(statusCode).build(), msg.getRequests().get(0).getRequestId());
      } else {
        int index = 0;
        for (StatusCode code : response.getResults()) {
          logger.info("response status code={}", code);

          if (code.isGood() != true) {
            ErrorHandler.getInstance().addErrorMessage(
                msg.getRequests().get(index).getEdgeNodeInfo(),
                new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
                new EdgeVersatility.Builder(code).build(),
                msg.getRequests().get(index).getRequestId());
            if (response.getResponseHeader().getServiceResult().isGood() != true) {
              continue;
            }

          }
          responses.add(new EdgeResponse.Builder(msg.getRequests().get(index).getEdgeNodeInfo(),
              msg.getRequests().get(index).getRequestId())
                  .setMessage(new EdgeVersatility.Builder(code).build()).build());
          index++;
        }

        EdgeEndpointInfo epInfo =
            new EdgeEndpointInfo.Builder(msg.getEdgeEndpointInfo().getEndpointUri())
                .setFuture(msg.getEdgeEndpointInfo().getFuture()).build();
        EdgeMessage inputData = new EdgeMessage.Builder(epInfo)
            .setMessageType(EdgeMessageType.GENERAL_RESPONSE).setResponses(responses).build();
        ProtocolManager.getProtocolManagerInstance().getRecvDispatcher().putQ(inputData);
      }
      return new EdgeResult.Builder(statusCode).build();
    }).exceptionally(e -> {
      logger.info("error type : {}", e.getMessage());
      return new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build();
    });
    logger.info("write is called");
    return new EdgeResult.Builder(EdgeStatusCode.STATUS_OK).build();
  }

  @Override
  public EdgeNodeIdentifier getNodeType() throws Exception {
    // TODO Auto-generated method stub
    logger.info("not support");
    return null;
  }

  @Override
  public void setProperty(VariableNode node) throws Exception {
    // TODO Auto-generated method stub
    logger.info("not support");

  }

  @Override
  public EdgeMapper getMapper() {
    // TODO Auto-generated method stub
    logger.info("not support");
    return null;
  }

  @Override
  public NodeId getNodeId() {
    // TODO Auto-generated method stub
    logger.info("not support");
    return null;
  }

  @Override
  public EdgeNodeInfo getNodeInfo(String valueAilas) {
    // TODO Auto-generated method stub
    return null;
  }

}
