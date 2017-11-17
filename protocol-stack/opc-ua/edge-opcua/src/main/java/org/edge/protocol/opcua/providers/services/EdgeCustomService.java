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
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.DiagnosticInfo;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.eclipse.milo.opcua.stack.core.types.structured.WriteValue;
import org.edge.protocol.mapper.api.EdgeMapper;
import org.edge.protocol.mapper.api.EdgeMapperCommon;
import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.client.EdgeDiagnosticInfo;
import org.edge.protocol.opcua.api.client.EdgeResponse;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeMessageType;
import org.edge.protocol.opcua.api.common.EdgeNodeId;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edge.protocol.opcua.providers.services.da.EdgeAttributeService;
import org.edge.protocol.opcua.queue.ErrorHandler;
import org.edge.protocol.opcua.session.EdgeSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeCustomService implements EdgeAttributeService {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final int nameSpace;
  private final String browseName;
  private int readAccessLevel = 1;
  private String dataType = null;
  private EdgeMapper mapper = null;

  public static class Builder {
    private final int nameSpace;
    private final String browseName;

    public Builder(int nameSpace, String browseName) {
      this.nameSpace = nameSpace;
      this.browseName = browseName;
    }

    public EdgeCustomService build() {
      return new EdgeCustomService(this);
    }
  }

  private EdgeCustomService(Builder builder) {
    nameSpace = builder.nameSpace;
    browseName = builder.browseName;
  }

  public String getBrowseName() {
    return browseName;
  }

  /**
   * @fn EdgeNodeInfo getNodeInfo(String valueAilas)
   * @brief get EdgeNodeInfo with the parameter to make nodeId of OPCUA library(Milo).
   * @prarm [in] valueAilas service provider key
   * @return EdgeNodeInfo
   */
  @Override
  public EdgeNodeInfo getNodeInfo(String valueAilas) {
    logger.info("getEndpoint : nameSpace={}, browseName", nameSpace, browseName);
    return new EdgeNodeInfo.Builder()
        .setEdgeNodeId(new EdgeNodeId.Builder(nameSpace, browseName).build())
        .setValueAlias(valueAilas).build();
  }

  /**
   * @fn EdgeResult readSync(EdgeEndpoint ep)
   * @brief read data from target Node in server (sync method) and response will be checked in
   *        onResonseMessage Callback.
   * @prarm [in] msg edge message
   * @return result
   */
  @Override
  public EdgeResult readSync(EdgeMessage msg) throws Exception {
    logger.info("readSync - browseName={}", browseName);
    EdgeNodeInfo ep = msg.getRequest().getEdgeNodeInfo();
    NodeId nodeId = new NodeId(nameSpace, browseName);
    VariableNode vNode = null;
    boolean isGood = true;
    try {
      vNode =
          EdgeSessionManager.getInstance().getSession(msg.getEdgeEndpointInfo().getEndpointUri())
              .getClientInstance().getAddressSpace().createVariableNode(nodeId);
      logger.debug("response : id={}, value={}, dataType={})",
          vNode.getNodeId().get().getIdentifier(), vNode.getValue().get(),
          vNode.getDataType().getNow(nodeId));

      EdgeEndpointInfo epInfo =
          new EdgeEndpointInfo.Builder(msg.getEdgeEndpointInfo().getEndpointUri())
              .setFuture(msg.getEdgeEndpointInfo().getFuture()).build();
      EdgeMessage inputData = new EdgeMessage.Builder(epInfo)
          .setMessageType(EdgeMessageType.GENERAL_RESPONSE)
          .setResponses(newArrayList(new EdgeResponse.Builder(ep, msg.getRequest().getRequestId())
              .setMessage(new EdgeVersatility.Builder(vNode.getValue().get()).build()).build()))
          .build();
      ProtocolManager.getProtocolManagerInstance().getRecvDispatcher().putQ(inputData);

    } catch (Exception ex) {
      logger.info("exception throw={}", ex.getMessage());
      isGood = false;
    }
    return new EdgeResult.Builder(isGood ? EdgeStatusCode.STATUS_OK : EdgeStatusCode.STATUS_ERROR)
        .build();
  }

  private boolean checkStatusGood(StatusCode status) {
    if (status.isGood() == false || status.getValue() == StatusCodes.Good_Clamped
        || status.getValue() == StatusCodes.Good_CompletesAsynchronously) {
      return false;
    } else {
      return true;
    }
  }

  protected CompletableFuture<Map<String, Object>> writeData(EdgeMessage msg) {
    EdgeNodeInfo ep = msg.getRequest().getEdgeNodeInfo();

    WriteValue writeValue =
        new WriteValue(new NodeId(nameSpace, browseName), AttributeId.Value.uid(), null,
            new DataValue(new Variant(msg.getRequest().getMessage().getValue())));
    return EdgeSessionManager.getInstance().getSession(msg.getEdgeEndpointInfo().getEndpointUri())
        .getClientInstance()

        // TODO UA-milo commit
        // .write(newArrayList(writeValue),
        // UInteger.valueOf(msg.getRequest().getReturnDiagnostic()))

        .write(newArrayList(writeValue)).thenApply(value -> {
          StatusCode status = value.getResults()[0];
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
          Map<String, Object> data = new HashMap<String, Object>();
          data.put(EdgeNodeIdentifier.DiagnosticInfo.name(),
              checkDiagnosticInfo(value.getResults().length, value.getDiagnosticInfos(),
                  msg.getRequest().getReturnDiagnostic()));
          data.put(EdgeNodeIdentifier.StatusCode.name(), value.getResults()[0]);
          return data;
        }).exceptionally(e -> {
          logger.info("error type : {}", e.getMessage());
          ErrorHandler.getInstance().addErrorMessage(msg.getRequest().getEdgeNodeInfo(),
              new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
              new EdgeVersatility.Builder(e.getMessage()).build(), msg.getRequest().getRequestId());
          return null;
        });
  }

  /**
   * @fn EdgeResult write(EdgeMessage msg)
   * @brief write data to target Node in server (async method) and response will be checked in
   *        onResonseMessage Callback.
   * @prarm [in] msg write value
   * @return result
   */
  @Override
  public EdgeResult write(EdgeMessage msg) throws Exception {
    logger.info("write - data={}", msg.getRequest().getMessage().getValue());
    writeData(msg).thenAccept(values -> {
      Optional.ofNullable(values).ifPresent(value -> {
        EdgeEndpointInfo epInfo =
            new EdgeEndpointInfo.Builder(msg.getEdgeEndpointInfo().getEndpointUri())
                .setFuture(msg.getEdgeEndpointInfo().getFuture()).build();
        EdgeMessage inputData = new EdgeMessage.Builder(epInfo)
            .setMessageType(EdgeMessageType.GENERAL_RESPONSE)
            .setResponses(newArrayList(new EdgeResponse.Builder(msg.getRequest().getEdgeNodeInfo(),
                msg.getRequest().getRequestId()).setMessage(
                    new EdgeVersatility.Builder(value.get(EdgeNodeIdentifier.StatusCode.name()))
                        .build())
                    .setDiagnosticInfo(
                        (EdgeDiagnosticInfo) values.get(EdgeNodeIdentifier.DiagnosticInfo.name()))
                    .build()))
            .build();
        ProtocolManager.getProtocolManagerInstance().getRecvDispatcher().putQ(inputData);
      });
    });

    return new EdgeResult.Builder(EdgeStatusCode.STATUS_OK).build();
  }

  /**
   * @fn EdgeNodeIdentifier getNodeType()
   * @brief get edge service type related attribute service. Node Type is including
   *        Edge_Node_Class_Type, Edge_Node_ServerInfo_Type, Edge_Node_Custom_Type in
   *        EdgeNodeIdentifier
   * @return service node type (EdgeNodeIdentifier.Edge_Node_Custom_Type)
   */
  @Override
  public EdgeNodeIdentifier getNodeType() throws Exception {
    return EdgeNodeIdentifier.Edge_Node_Custom_Type;
  }

  protected boolean compareNumber(long number, long standard) {
    if (number > standard) {
      return true;
    }
    return false;
  }

  protected boolean checkMaxAge(DateTime timeStamp, DateTime now, double maxAge) {
    long diff = now.getJavaTime() - timeStamp.getJavaTime();
    logger.debug("now : {} timestamp : {} diff: {} max Age : {}", now.getJavaDate(),
        timeStamp.getJavaDate(), diff, maxAge);

    if (maxAge != 0 && diff > maxAge) {
      return false;
    }
    return true;
  }

  protected boolean checkInvalidTime(DateTime serverTime, DateTime sourceTime, int validMilliSec,
      TimestampsToReturn stamp) {
    long now = DateTime.now().getJavaTime();
    Boolean ret = true;

    if (TimestampsToReturn.Both == stamp) {
      if (0 == serverTime.getJavaTime() || 0 == sourceTime.getJavaTime()) {
        ret = false;
      }

      if (compareNumber(now - serverTime.getJavaTime(), validMilliSec)) {
        ret = false;
      } else if (compareNumber(now - sourceTime.getJavaTime(), validMilliSec)) {
        ret = false;
      } else if (compareNumber(serverTime.getJavaTime(), now)) {
        ret = false;
      } else if (compareNumber(sourceTime.getJavaTime(), now)) {
        ret = false;
      }
    } else if (TimestampsToReturn.Source == stamp) {
      if (0 == sourceTime.getJavaTime()) {
        ret = false;
      }

      if (compareNumber(now - sourceTime.getJavaTime(), validMilliSec)) {
        ret = false;
      } else if (compareNumber(sourceTime.getJavaTime(), now)) {
        ret = false;
      }
    } else if (TimestampsToReturn.Server == stamp) {
      if (0 == serverTime.getJavaTime()) {
        ret = false;
      }

      if (compareNumber(now - serverTime.getJavaTime(), validMilliSec)) {
        ret = false;
      } else if (compareNumber(serverTime.getJavaTime(), now)) {
        ret = false;
      }
    }

    return ret;
  }

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

  protected EdgeDiagnosticInfo checkDiagnosticInfo(int nodesToProcess,
      DiagnosticInfo[] diagnosticInfo, int returnDiagnostic) {
    String msg = null;
    int diagnosticInfoLength = 0;

    if (Optional.ofNullable(diagnosticInfo).isPresent()) {
      diagnosticInfoLength = diagnosticInfo.length;
    }

    if (0 == returnDiagnostic && 0 == diagnosticInfoLength) {
      msg = null;
    } else if (diagnosticInfoLength == nodesToProcess) {
      DiagnosticInfo info = diagnosticInfo[0];
      return new EdgeDiagnosticInfo.Builder(info.getSymbolicId(), info.getLocalizedText(),
          info.getAdditionalInfo(), info.getInnerStatusCode(), info.getInnerDiagnosticInfo())
              .build();
    } else if (0 != returnDiagnostic && 0 == diagnosticInfoLength) {
      msg = "no diagnostics were returned even though returnDiagnostic requested.";
    } else {
      int mismatchedNum = Math.abs(nodesToProcess - diagnosticInfo.length);
      msg = "mismatched of " + mismatchedNum + " entries returned";
    }

    return new EdgeDiagnosticInfo.Builder().setMessage(msg).build();
  }

  protected DataValue checkValidation(DataValue value, EdgeMessage msg, TimestampsToReturn stamp,
      double maxAge) {
    EdgeNodeInfo ep = msg.getRequest().getEdgeNodeInfo();
    if (!checkNaNData(value)) {
      ErrorHandler.getInstance().addErrorMessage(ep,
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder("NAN value").build(), msg.getRequest().getRequestId());
      return null;
    }

    if (!checkInvalidTime(value.getServerTime(), value.getSourceTime(), 86400000, stamp)) {
      logger.debug("serverTime: {} sourceTime : {}", value.getServerTime(), value.getSourceTime());
      ErrorHandler.getInstance().addErrorMessage(ep,
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder("Invalid Time").build(), msg.getRequest().getRequestId());
      return null;
    }

    if (value.getStatusCode().isBad()) {
      StatusCodes.lookup(value.getStatusCode().getValue()).ifPresent(values -> {
        logger.debug("error type : {}", values[0]);
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

  private CompletableFuture<Map<String, Object>> readData(EdgeMessage msg) throws Exception {
    TimestampsToReturn timestamp = TimestampsToReturn.Both;
    double maxAge = 200;

    EdgeNodeInfo ep = msg.getRequest().getEdgeNodeInfo();
    UaVariableNode node = new UaVariableNode(EdgeSessionManager.getInstance()
        .getSession(msg.getEdgeEndpointInfo().getEndpointUri()).getClientInstance(),
        new NodeId(nameSpace, browseName));

    ReadValueId readValueId = new ReadValueId(new NodeId(nameSpace, browseName),
        AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);

    CompletableFuture<Map<String, Object>> future =
        EdgeSessionManager.getInstance().getSession(msg.getEdgeEndpointInfo().getEndpointUri())

            // TODO UA-milo commit
            // .getClientInstance().read(maxAge, timestamp, newArrayList(readValueId),
            // UInteger.valueOf(msg.getRequest().getReturnDiagnostic()))

            .getClientInstance().read(maxAge, timestamp, newArrayList(readValueId))
            .thenApply(values -> {
              if (values.getResults().length < 1) { // Only 1 Request in this method
                ErrorHandler.getInstance().addErrorMessage(ep,
                    new EdgeResult.Builder(EdgeStatusCode.STATUS_READ_LESS_RESPONSE).build(),
                    msg.getRequest().getRequestId());
                return null;
              }
              if (!checkMaxAge(values.getResponseHeader().getTimestamp(), DateTime.now(),
                  maxAge * 2)) {
                return null;
              }
              Map<String, Object> data = new HashMap<String, Object>();
              data.put(EdgeNodeIdentifier.DiagnosticInfo.name(),
                  checkDiagnosticInfo(values.getResults().length, values.getDiagnosticInfos(),
                      msg.getRequest().getReturnDiagnostic()));
              data.put(EdgeNodeIdentifier.DataValue.name(), values.getResults()[0]);
              return data;
            }).exceptionally(e -> {
              logger.info("error type : {}", e.getMessage());
              ErrorHandler.getInstance().addErrorMessage(ep,
                  new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
                  new EdgeVersatility.Builder(e.getMessage()).build(),
                  msg.getRequest().getRequestId());
              return null;
            });

    CompletableFuture<NodeId> dataType = node.getDataType().thenApply(value -> {
      return value;
    }).exceptionally(e -> {
      return null;
    });

    return dataType.thenCombineAsync(future, (nId, value) -> {
      if (Optional.ofNullable(nId).isPresent() && Optional.ofNullable(value).isPresent()) {
        DataValue dataValue = (DataValue) value.get(EdgeNodeIdentifier.DataValue.name());
        logger.debug("Type : {} {}", nId.getIdentifier(),
            dataValue.getValue().getDataType().get().getIdentifier());
        if (nId.getIdentifier().equals(dataValue.getValue().getDataType().get().getIdentifier())) {
          if (Optional.ofNullable(checkValidation(dataValue, msg, timestamp, maxAge)).isPresent()) {
            return value;
          }
        } else {
          ErrorHandler.getInstance().addErrorMessage(ep,
              new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
              new EdgeVersatility.Builder("server data type is mismtach").build(),
              msg.getRequest().getRequestId());
        }
      }
      return null;
    });
  }

  /**
   * @fn EdgeResult readAsync(EdgeEndpoint ep)
   * @brief read data from target Node in server (async method) this function is worked on single
   *        target node. multi access is supported on EdgeGroupService. and response will be checked
   *        in onResonseMessage Callback. and error message will be checked in onErrorMessage
   *        Callback.
   * @prarm [in] msg edge message
   * @return result
   */
  @Override
  public EdgeResult readAsync(EdgeMessage msg) {
    logger.info("readAsync - browseName={}", browseName);
    EdgeNodeInfo ep = msg.getRequest().getEdgeNodeInfo();
    boolean isGood = true;
    try {
      readData(msg).thenAccept(values -> {
        Optional.ofNullable(values).ifPresent(value -> {
          DataValue dataValue = (DataValue) values.get(EdgeNodeIdentifier.DataValue.name());
          EdgeEndpointInfo epInfo =
              new EdgeEndpointInfo.Builder(msg.getEdgeEndpointInfo().getEndpointUri())
              .setFuture(msg.getEdgeEndpointInfo().getFuture()).build();
          EdgeMessage inputData = new EdgeMessage.Builder(epInfo)
              .setMessageType(EdgeMessageType.GENERAL_RESPONSE)
              .setResponses(
                  newArrayList(new EdgeResponse.Builder(ep, msg.getRequest().getRequestId())
                      .setDiagnosticInfo(
                          (EdgeDiagnosticInfo) values.get(EdgeNodeIdentifier.DiagnosticInfo.name()))
                      .setMessage(new EdgeVersatility.Builder(dataValue.getValue()).build())
                      .build()))
              .build();
          ProtocolManager.getProtocolManagerInstance().getRecvDispatcher().putQ(inputData);
        });
      });
    } catch (Exception e) {
      isGood = false;
      e.printStackTrace();
    }
    logger.info("readAsyc is called");
    return new EdgeResult.Builder(isGood ? EdgeStatusCode.STATUS_OK : EdgeStatusCode.STATUS_ERROR)
        .build();
  }

  /**
   * @fn EdgeMapper getMapper()
   * @brief get mapper instance this function is provided metadata such as access-level, data-type.
   * @return mapper instance
   */
  public EdgeMapper getMapper() {
    if (mapper == null) {
      mapper = new EdgeMapper();
      try {
        mapper.addMappingData(EdgeMapperCommon.PROPERTYVALUE_READWRITE.name(),
            EdgeIdentifier.convertAccessLevel(this.readAccessLevel));

        if (null != this.dataType) {
          mapper.addMappingData(EdgeMapperCommon.DEVICEOBJECT_ATTRIBUTE_DATATYPE.name(),
              this.dataType);
        }
      } catch (Exception e) {
        e.printStackTrace();
        ErrorHandler.getInstance().addErrorMessage(new EdgeNodeInfo.Builder().build(),
            new EdgeResult.Builder(EdgeStatusCode.STATUS_INTERNAL_ERROR).build(),
            EdgeOpcUaCommon.DEFAULT_REQUEST_ID);
      }
    }
    return mapper;
  }

  /**
   * @fn void setProperty(VariableNode v)
   * @brief set Property Data for Mapper
   * @return void
   */
  @Override
  public void setProperty(VariableNode v) throws Exception {
    if (v == null)
      return;
    readAccessLevel = Integer.parseInt(v.readAccessLevel().get().getValue().getValue().toString());
    // for (EdgeNodeIdentifier id : EdgeNodeIdentifier.values()) {
    // if (id.value() ==
    // Integer.parseInt(v.getDataType().get().getIdentifier().toString())) {
    // logger.info("data type is {}", id.name());
    // this.dataType = id.name();
    // break;
    // }
    // }
  }

  @Override
  public NodeId getNodeId() {
    // TODO Auto-generated method stub
    return new NodeId(nameSpace, browseName);
  }
}
