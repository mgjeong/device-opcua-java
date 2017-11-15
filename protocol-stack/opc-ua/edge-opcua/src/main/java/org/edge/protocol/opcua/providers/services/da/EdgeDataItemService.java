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

package org.edge.protocol.opcua.providers.services.da;

import java.lang.reflect.Array;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.client.model.nodes.variables.DataItemNode;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.edge.protocol.mapper.api.EdgeMapper;
import org.edge.protocol.mapper.api.EdgeMapperCommon;
import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.client.EdgeResponse;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeMessageType;
import org.edge.protocol.opcua.api.common.EdgeNodeId;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeRequest;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edge.protocol.opcua.queue.ErrorHandler;
import org.edge.protocol.opcua.session.EdgeSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.google.common.collect.Lists.newArrayList;

public class EdgeDataItemService implements EdgeAttributeService {
  private DataItemNode node = null;
  private EdgeMapper mapper = null;

  private final int nameSpace;
  private final String endpointUri;

  private static Object lock = new Object();

  protected int readAccessLevel = 1;
  protected String dataType = null;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * @fn EdgeDataItemService()
   * @brief constructor of EdgeDataItemService class
   */
  public EdgeDataItemService() {
    nameSpace = EdgeOpcUaCommon.DEFAULT_NAMESPACE_INDEX;
    endpointUri = null;
  }

  /**
   * @fn EdgeDataItemService(int nameSpace, String endpointUri)
   * @brief constructor of EdgeDataItemService class
   */
  public EdgeDataItemService(int nameSpace, String endpointUri) {
    this.nameSpace = nameSpace;
    this.endpointUri = endpointUri;

    try {
      this.setMapper();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @fn DataItemNode getNodeInstance()
   * @brief get node instance
   * @return DataItemNode
   */
  private DataItemNode getNodeInstance() {
    synchronized (lock) {
      if (null == node)
        node = new DataItemNode(
            EdgeSessionManager.getInstance().getSession(endpointUri).getClientInstance(),
            new NodeId(nameSpace, EdgeNodeIdentifier.DataItemType.value()));
    }
    return node;
  }

  /**
   * @fn EdgeNodeInfo getNodeInfo(String valueAilas)
   * @brief get EdgeNodeInfo with the parameter to make nodeId of OPCUA library(Milo).
   * @prarm [in] valueAilas service provider key
   * @return EdgeNodeInfo
   */
  @Override
  public EdgeNodeInfo getNodeInfo(String valueAilas) {
    return new EdgeNodeInfo.Builder()
        .setEdgeNodeId(new EdgeNodeId.Builder(nameSpace, EdgeNodeIdentifier.DataItemType).build())
        .setValueAlias(valueAilas).build();
  }

  /**
   * @fn EdgeResult readSync(EdgeMessage msg)
   * @brief read node data synchronously
   * @param [in] EdgeMessage msg
   * @return result
   */
  @Override
  public EdgeResult readSync(EdgeMessage msg) throws Exception {
    Variant ret = null;
    EdgeNodeInfo ep = msg.getRequest().getEdgeNodeInfo();
    if (ep.getEdgeNodeID() == null) {
      return new EdgeResult.Builder(EdgeStatusCode.STATUS_PARAM_INVALID).build();
    }
    EdgeNodeIdentifier id = ep.getEdgeNodeID().getEdgeNodeIdentifier();

    try {
      if (EdgeNodeIdentifier.ReadValueId == id) {
        ret = readValue(getNodeInstance().getValue().get());
      } else if (EdgeNodeIdentifier.DataItemType_Definition == id) {
        ret = readDefinition(getNodeInstance().getDefinition().get());
      } else if (EdgeNodeIdentifier.DataItemType_ValuePrecision == id) {
        ret = readValuePrecision(getNodeInstance().getValuePrecision().get());
      } else {
        return new EdgeResult.Builder(EdgeStatusCode.STATUS_PARAM_INVALID).build();
      }

      if (ret != null && ret.isNotNull()) {
        EdgeEndpointInfo epInfo =
            new EdgeEndpointInfo.Builder(msg.getEdgeEndpointInfo().getEndpointUri())
                .setFuture(msg.getEdgeEndpointInfo().getFuture()).build();
        EdgeMessage inputData = new EdgeMessage.Builder(epInfo)
            .setMessageType(EdgeMessageType.GENERAL_RESPONSE)
            .setResponses(newArrayList(new EdgeResponse.Builder(ep, msg.getRequest().getRequestId())
                .setMessage(new EdgeVersatility.Builder(ret.getValue()).build()).build()))
            .build();
        ProtocolManager.getProtocolManagerInstance().getRecvDispatcher().putQ(inputData);
        return new EdgeResult.Builder(EdgeStatusCode.STATUS_OK).build();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build();
  }

  /**
   * @fn boolean checkInvalidTime(DateTime serverTime, DateTime sourceTime, int validMilliSec)
   * @brief check validation of time
   * @param [in] DateTime serverTime
   * @param [in] DateTime sourceTime
   * @param [in] int validMilliSec
   * @return boolean result
   */
  protected boolean checkInvalidTime(DateTime serverTime, DateTime sourceTime, int validMilliSec) {
    validMilliSec = 86400000; // 24h
    long now = DateTime.now().getJavaTime();

    if (now - serverTime.getJavaTime() > validMilliSec) {
      return false;
    } else if (now - sourceTime.getJavaTime() > validMilliSec) {
      return false;
    } else if (serverTime.getJavaTime() > now) {
      return false;
    } else if (sourceTime.getJavaTime() > now) {
      return false;
    }

    return true;
  }

  /**
   * @fn boolean checkNaNData(DataValue value)
   * @brief check validation of NaNData
   * @param [in] DataValue value
   * @return boolean result
   */
  protected boolean checkNaNData(DataValue value) {
    if (value.getValue().getDataType().get().getIdentifier() == EdgeNodeIdentifier.Double) {
      if (value.getValue().getValue().equals(Double.NaN)) {
        return false;
      }
    } else if (value.getValue().getDataType().get().getIdentifier() == EdgeNodeIdentifier.Float) {
      if (value.getValue().getValue().equals(Float.NaN)) {
        return false;
      }
    }
    return true;
  }

  /**
   * @fn DataValue checkValidation(DataValue value, EdgeMessage msg)
   * @brief check validation of value
   * @param [in] DataValue value
   * @param [in] EdgeMessage msg
   * @return DataValue
   */
  protected DataValue checkValidation(DataValue value, EdgeMessage msg) {
    EdgeNodeInfo ep = msg.getRequest().getEdgeNodeInfo();
    if (!checkNaNData(value)) {
      ErrorHandler.getInstance().addErrorMessage(ep,
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder("NAN value").build(), msg.getRequest().getRequestId());
      return null;
    }

    if (!checkInvalidTime(value.getServerTime(), value.getSourceTime(), 86400000)) {
      logger.info("serverTime: {} sourceTime : {}", value.getServerTime(), value.getSourceTime());
      return null;
    }

    if (value.getStatusCode().isBad()) {
      StatusCodes.lookup(value.getStatusCode().getValue()).ifPresent(values -> {
        logger.info("error type : {}", values[0]);
        ErrorHandler.getInstance().addErrorMessage(ep,
            new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
            new EdgeVersatility.Builder(values[0]).build(), msg.getRequest().getRequestId());
      });
      return null;
    }

    if (value.getValue().getValue().getClass().isArray()) {
      if (Array.getLength(value.getValue().getValue()) == 0) {
        ErrorHandler.getInstance().addErrorMessage(ep,
            new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
            new EdgeVersatility.Builder("Array is empty").build(), msg.getRequest().getRequestId());
        return null;
      }
    }

    return value;
  }

  /**
   * @fn CompletableFuture<DataValue> readAsyncValue(DataItemNode dNode, EdgeMessage msg)
   * @brief read values (Async)
   * @param [in] DataItemNode dNode
   * @param [in] EdgeMessage msg
   * @return CompletableFuture<DataValue>
   */
  protected CompletableFuture<DataValue> readAsyncValue(DataItemNode dNode, EdgeMessage msg) {
    CompletableFuture<DataValue> future = dNode.readValue().thenApply(values -> {
      return values;
    }).exceptionally(e -> {
      ErrorHandler.getInstance().addErrorMessage(msg.getRequest().getEdgeNodeInfo(),
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder(e.getMessage()).build(), msg.getRequest().getRequestId());
      logger.info("error type : {}", e.getMessage());
      return null;
    });

    CompletableFuture<NodeId> dataType = dNode.getDataType().thenApply(values -> {
      return values;
    }).exceptionally(e -> {
      return null;
    });

    return dataType.thenCombineAsync(future, (nId, values) -> {
      if (Optional.ofNullable(nId).isPresent() && Optional.ofNullable(values).isPresent()) {
        logger.info("{} {}", nId.getIdentifier(),
            values.getValue().getDataType().get().getIdentifier());
        if (nId.getIdentifier().equals(values.getValue().getDataType().get().getIdentifier())) {
          if (Optional.ofNullable(checkValidation(values, msg)).isPresent()) {
            return values;
          }
        }
      }
      return null;
    });
  }

  /**
   * @fn boolean checkStatusGood(StatusCode status)
   * @brief check status
   * @param [in] StatusCode status
   * @return boolean result
   */
  private boolean checkStatusGood(StatusCode status) {
    if (status.isGood() == false || status.getValue() == StatusCodes.Good_Clamped
        || status.getValue() == StatusCodes.Good_CompletesAsynchronously) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * @fn CompletableFuture<StatusCode> writeAsyncValue(DataItemNode dNode, EdgeMessage msg)
   * @brief write value (async)
   * @param [in] DataItemNode dNode
   * @param [in] EdgeMessage msg
   * @return CompletableFuture<StatusCode>
   */
  protected CompletableFuture<StatusCode> writeAsyncValue(DataItemNode dNode, EdgeMessage msg) {
    return dNode.writeValue(new DataValue(new Variant(msg.getRequest().getMessage().getValue()),
        null, null, null, null, null)).thenApply(status -> {
          if (checkStatusGood(status) == false) {
            String errorStatusCode = EdgeStatusCode.UNKNOWN_STATUS_CODE;
            Optional<String[]> statusCodes = StatusCodes.lookup(status.getValue());
            if (statusCodes.isPresent() == true) {
              errorStatusCode = statusCodes.get()[0];
            }
            logger.info("error type : {}", errorStatusCode);
            ErrorHandler.getInstance().addErrorMessage(msg.getRequest().getEdgeNodeInfo(),
                new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
                new EdgeVersatility.Builder(errorStatusCode).build(),
                msg.getRequest().getRequestId());
            return null;
          }
          return status;
        }).exceptionally(e -> {
          ErrorHandler.getInstance().addErrorMessage(msg.getRequest().getEdgeNodeInfo(),
              new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
              new EdgeVersatility.Builder(e.getMessage()).build(), msg.getRequest().getRequestId());
          logger.info("error type : {}", e.getMessage());
          return null;
        });
  }

  /**
   * @fn CompletableFuture<String> readAsyncDefinition(DataItemNode dNode, EdgeMessage msg)
   * @brief read definition (async)
   * @param [in] DataItemNode dNode
   * @param [in] EdgeMessage msg
   * @return CompletableFuture<String>
   */
  protected CompletableFuture<String> readAsyncDefinition(DataItemNode dNode, EdgeMessage msg) {
    return dNode.getDefinition().thenApply(values -> {
      return values;
    }).exceptionally(e -> {
      Optional.ofNullable(msg.getRequest().getEdgeNodeInfo()).ifPresent(endpoint -> {
        ErrorHandler.getInstance().addErrorMessage(endpoint,
            new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
            new EdgeVersatility.Builder(e.getMessage()).build(), msg.getRequest().getRequestId());
      });
      return null;
    });
  }

  /**
   * @fn CompletableFuture<LocalizedText> readAsyncDescription(DataItemNode dNode)
   * @brief read description (sync)
   * @param [in] DataItemNode dNode
   * @return CompletableFuture<LocalizedText>
   */
  protected CompletableFuture<LocalizedText> readAsyncDescription(DataItemNode dNode) {
    return dNode.getDescription().thenApply(value -> {
      return value;
    }).exceptionally(e -> {
      return null;
    });
  }

  /**
   * @fn CompletableFuture<Double> readAsyncValuePrecision(DataItemNode dNode, EdgeMessage msg)
   * @brief read value precision (sync)
   * @param [in] DataItemNode dNode
   * @param [in] EdgeMessage msg
   * @return CompletableFuture<Double>
   */
  protected CompletableFuture<Double> readAsyncValuePrecision(DataItemNode dNode, EdgeMessage msg)
      throws InterruptedException, ExecutionException {
    return dNode.getValuePrecision().thenApply(values -> {
      return values;
    }).exceptionally(e -> {
      Optional.ofNullable(msg.getRequest().getEdgeNodeInfo()).ifPresent(endpoint -> {
        ErrorHandler.getInstance().addErrorMessage(endpoint,
            new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
            new EdgeVersatility.Builder(e.getMessage()).build(), msg.getRequest().getRequestId());
      });
      return null;
    });
  }

  /**
   * @fn Variant readValue(Object value)
   * @brief read value from object
   * @param [in] Object value
   * @return Variant
   */
  protected Variant readValue(Object value) throws InterruptedException, ExecutionException {
    return new Variant(value);
  }

  /**
   * @fn Variant readDefinition(String definition)
   * @brief read definition
   * @param [in] String definition
   * @return Variant
   */
  protected Variant readDefinition(String definition)
      throws InterruptedException, ExecutionException {
    return new Variant(definition);
  }

  /**
   * @fn Variant readValuePrecision(Double varPrecision)
   * @brief read value precision
   * @param [in] Double varPrecision
   * @return Variant
   */
  protected Variant readValuePrecision(Double varPrecision)
      throws InterruptedException, ExecutionException {
    return new Variant(varPrecision);
  }

  /**
   * @fn EdgeResult write2(EdgeMessage msg)
   * @brief write edge message
   * @param [in] EdgeMessage msg
   * @return result
   */
  public EdgeResult write2(EdgeMessage msg) throws Exception {
    StatusCode status = getNodeInstance()
        .writeValue(new DataValue(new Variant(msg.getRequest().getMessage().getValue()))).get();

    // add status code into receive queue
    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(msg.getEdgeEndpointInfo().getEndpointUri())
            .setFuture(msg.getEdgeEndpointInfo().getFuture()).build();
    EdgeMessage inputData =
        new EdgeMessage.Builder(epInfo).setMessageType(EdgeMessageType.GENERAL_RESPONSE)
            .setResponses(newArrayList(new EdgeResponse.Builder(msg.getRequest().getEdgeNodeInfo(),
                msg.getRequest().getRequestId())
                    .setMessage(new EdgeVersatility.Builder(status).build()).build()))
            .build();
    ProtocolManager.getProtocolManagerInstance().getRecvDispatcher().putQ(inputData);

    return new EdgeResult.Builder(
        status.isGood() ? EdgeStatusCode.STATUS_OK : EdgeStatusCode.STATUS_ERROR).build();
  }

  /**
   * @fn EdgeNodeIdentifier getNodeType()
   * @brief get node type
   * @return EdgeNodeIdentifier
   */
  @Override
  public EdgeNodeIdentifier getNodeType() throws Exception {
    return EdgeNodeIdentifier.DataItemType;
  }

  /**
   * @fn EdgeResult readAsync(EdgeMessage msg)
   * @brief read edge message (async)
   * @param [in] EdgeMessage msg
   * @return result
   */
  @Override
  public EdgeResult readAsync(EdgeMessage msg) throws Exception {
    EdgeNodeInfo ep = msg.getRequest().getEdgeNodeInfo();
    EdgeNodeIdentifier id = EdgeNodeIdentifier.ReadValueId;
    if (ep.getEdgeNodeID() != null) {
      id = ep.getEdgeNodeID().getEdgeNodeIdentifier();
    }

    if (EdgeNodeIdentifier.ReadValueId == id) {
      readAsyncValue(getNodeInstance(), msg).thenAccept(values -> {
        Optional.ofNullable(values).ifPresent(value -> {
          addResponse(value, ep, msg);
        });
      });
    } else if (EdgeNodeIdentifier.DataItemType_Definition == id) {
      readAsyncDefinition(getNodeInstance(), msg).thenAccept(values -> {
        Optional.ofNullable(values).ifPresent(value -> {
          addResponse(value, ep, msg);
        });
      });
    } else if (EdgeNodeIdentifier.DataItemType_ValuePrecision == id) {
      readAsyncValuePrecision(getNodeInstance(), msg).thenAccept(values -> {
        Optional.ofNullable(values).ifPresent(value -> {
          addResponse(value, ep, msg);
        });
      });
    } else {
      ErrorHandler.getInstance().addErrorMessage(ep,
          new EdgeResult.Builder(EdgeStatusCode.STATUS_PARAM_INVALID).build(),
          msg.getRequest().getRequestId());
      return new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build();
    }

    return new EdgeResult.Builder(EdgeStatusCode.STATUS_OK).build();
  }

  private void addResponse(Object value, EdgeNodeInfo nodeInfo, EdgeMessage msg) {
    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(msg.getEdgeEndpointInfo().getEndpointUri())
            .setFuture(msg.getEdgeEndpointInfo().getFuture()).build();
    EdgeMessage inputData =
        new EdgeMessage.Builder(epInfo).setMessageType(EdgeMessageType.GENERAL_RESPONSE)
            .setResponses(
                newArrayList(new EdgeResponse.Builder(nodeInfo, msg.getRequest().getRequestId())
                    .setMessage(new EdgeVersatility.Builder(value).build()).build()))
            .build();
    ProtocolManager.getProtocolManagerInstance().getRecvDispatcher().putQ(inputData);
  }

  /**
   * @fn EdgeResult write(EdgeMessage msg)
   * @brief write edge message (async)
   * @param [in] EdgeMessage msg
   * @return result
   */
  @Override
  public EdgeResult write(EdgeMessage msg) throws Exception {
    writeAsyncValue(getNodeInstance(), msg).thenAccept(status -> {
      Optional.ofNullable(status).ifPresent(value -> {
        EdgeEndpointInfo epInfo =
            new EdgeEndpointInfo.Builder(msg.getEdgeEndpointInfo().getEndpointUri())
                .setFuture(msg.getEdgeEndpointInfo().getFuture()).build();
        EdgeMessage inputData = new EdgeMessage.Builder(epInfo)
            .setMessageType(EdgeMessageType.GENERAL_RESPONSE)
            .setResponses(newArrayList(new EdgeResponse.Builder(msg.getRequest().getEdgeNodeInfo(),
                msg.getRequest().getRequestId())
                    .setMessage(new EdgeVersatility.Builder(status).build()).build()))
            .build();
        ProtocolManager.getProtocolManagerInstance().getRecvDispatcher().putQ(inputData);
      });
    });
    return new EdgeResult.Builder(EdgeStatusCode.STATUS_OK).build();
  }

  /**
   * @fn void putData(EdgeEndpoint ep, Variant data)
   * @brief put variant data to edge endpoint
   * @param [in] edgeEndpoint
   * @param [in] data
   * @return void
   */
  public static void putData(EdgeNodeInfo ep, Variant data, EdgeEndpointInfo epInfo) {
    EdgeMessage inputData =
        new EdgeMessage.Builder(epInfo).setMessageType(EdgeMessageType.GENERAL_RESPONSE)
            .setResponses(
                newArrayList(new EdgeResponse.Builder(ep, EdgeOpcUaCommon.DEFAULT_REQUEST_ID)
                    .setMessage(new EdgeVersatility.Builder(data).build()).build()))
            .build();
    ProtocolManager.getProtocolManagerInstance().getRecvDispatcher().putQ(inputData);
  }

  /**
   * @fn void setMapper()
   * @brief set mapper
   * @return void
   */
  public void setMapper() throws Exception {
    mapper = new EdgeMapper();

    if (null != dataType) {
      mapper.addMappingData(EdgeMapperCommon.DEVICEOBJECT_TAG.name(), dataType);
    }
    mapper.addMappingData(EdgeMapperCommon.PROPERTYVALUE_TYPE.name(),
        EdgeNodeIdentifier.BaseDataType.name().toString());
    mapper.addMappingData(EdgeMapperCommon.PROPERTYVALUE_READWRITE.name(),
        EdgeIdentifier.convertAccessLevel(this.readAccessLevel));

    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(EdgeOpcUaCommon.WELL_KNOWN_LOCALHOST_URI.getValue()).build();
    EdgeMessage msg = new EdgeMessage.Builder(epInfo)
        .setRequest(new EdgeRequest.Builder(new EdgeNodeInfo.Builder().build()).build()).build();

    readAsyncDescription(getNodeInstance()).thenAccept(values -> {
      Optional.ofNullable(values).ifPresent(value -> {
        try {
          mapper.addMappingData(EdgeMapperCommon.DEVICEOBJECT_DESCRIPTION.name(), values.getText());
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    });

    readAsyncDefinition(getNodeInstance(), msg).thenAccept(values -> {
      Optional.ofNullable(values).ifPresent(value -> {
        try {
          mapper.addMappingData(EdgeMapperCommon.PROPERTYVALUE_ASSERTION.name(), values);
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    });

    readAsyncValuePrecision(getNodeInstance(), msg).thenAccept(values -> {
      Optional.ofNullable(values).ifPresent(value -> {
        try {
          mapper.addMappingData(EdgeMapperCommon.PROPERTYVALUE_PRECISION.name(), value.toString());
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    });
  }

  /**
   * @fn void getMapper()
   * @brief get mapper
   * @return EdgeMapper
   */
  public EdgeMapper getMapper() {
    return mapper;
  }

  /**
   * @fn void setProperty(VariableNode v)
   * @brief get property of node
   * @param [in] variable node
   * @return void
   */
  @Override
  public void setProperty(VariableNode v) throws Exception {
    if (v == null)
      return;
    readAccessLevel = Integer.parseInt(v.readAccessLevel().get().getValue().getValue().toString());
    // for (EdgeNodeIdentifier id : EdgeNodeIdentifier.values()) {
    // if (id.value() == Integer.parseInt(v.getDataType().get().getIdentifier().toString())) {
    // logger.info("data type is {}", id.name());
    // this.dataType = id.name();
    // break;
    // }
    // }
  }

  /**
   * @fn NodeId getNodeId()
   * @brief get node id
   * @return NodeId
   */
  @Override
  public NodeId getNodeId() {
    // TODO Auto-generated method stub
    return null;
  }
}
