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

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.eclipse.milo.opcua.sdk.client.model.nodes.variables.AnalogItemNode;
import org.eclipse.milo.opcua.sdk.client.model.nodes.variables.PropertyNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.structured.EUInformation;
import org.eclipse.milo.opcua.stack.core.types.structured.Range;
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
import static com.google.common.collect.Lists.newArrayList;

public class EdgeAnalogItemService extends EdgeDataItemService {
  private AnalogItemNode node = null;
  private EdgeMapper mapper = null;

  private final int nameSpace;
  private final String endpointUri;

  private static Object lock = new Object();

  /**
   * @fn EdgeAnalogItemService(int nameSpace, String endpointUri)
   * @brief get EdgeBrowseService Instance
   * @return EdgeBrowseService Instance
   */
  public EdgeAnalogItemService(int nameSpace, String endpointUri) {
    this.nameSpace = nameSpace;
    this.endpointUri = endpointUri;

    try {
      this.setMapper();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @fn AnalogItemNode getNodeInstance()
   * @brief get AnalogItemNode instance
   * @return AnalogItemNode
   */
  private AnalogItemNode getNodeInstance() {
    synchronized (lock) {
      if (null == node)
        node = new AnalogItemNode(
            EdgeSessionManager.getInstance().getSession(endpointUri).getClientInstance(),
            new NodeId(nameSpace, EdgeNodeIdentifier.AnalogItemType.value()));
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
        .setEdgeNodeId(new EdgeNodeId.Builder(nameSpace, EdgeNodeIdentifier.AnalogItemType).build())
        .setValueAlias(valueAilas).build();
  }

  /**
   * @fn EdgeResult readSync(EdgeMessage msg)
   * @brief overriding method for read sync
   * @param [in] EdgeMessage msg
   * @return EdgeResult
   */
  @Override
  public EdgeResult readSync(EdgeMessage msg) throws Exception {
    Variant ret = null;
    EdgeNodeInfo ep = msg.getRequest().getEdgeNodeInfo();

    EdgeNodeIdentifier id = EdgeNodeIdentifier.ReadValueId;
    if (ep.getEdgeNodeID() != null) {
      id = ep.getEdgeNodeID().getEdgeNodeIdentifier();
    }

    if (EdgeNodeIdentifier.ReadValueId == id) {
      ret = readValue(getNodeInstance().getValue().get());
    } else if (EdgeNodeIdentifier.AnalogItemType_Definition == id) {
      ret = readDefinition(getNodeInstance().getDefinition().get());
    } else if (EdgeNodeIdentifier.AnalogItemType_InstrumentRange == id) {
      ret = readInstrumentRange();
    } else if (EdgeNodeIdentifier.AnalogItemType_EngineeringUnits == id) {
      ret = readEngineeringUnits();
    } else if (EdgeNodeIdentifier.AnalogItemType_EURange == id) {
      ret = readEURange();
    } else if (EdgeNodeIdentifier.AnalogItemType_ValuePrecision == id) {
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
    return new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build();
  }

  /**
   * @fn Variant readInstrumentRange()
   * @brief read instrument range
   * @return Variant
   */
  private Variant readInstrumentRange() throws InterruptedException, ExecutionException {
    ExtensionObject extensionObj =
        (ExtensionObject) getNodeInstance().instrumentRange().get().getValue().get();
    Range instrumentRange = extensionObj.decode();
    HashMap<String, String> range = new HashMap<String, String>();
    range.put("High", instrumentRange.getHigh().toString());
    range.put("Low", instrumentRange.getLow().toString());

    return new Variant(range);
  }

  /**
   * @fn Variant readEngineeringUnits()
   * @brief read engineering units
   * @return Variant
   */
  private Variant readEngineeringUnits() throws InterruptedException, ExecutionException {
    ExtensionObject extensionObj =
        (ExtensionObject) getNodeInstance().engineeringUnits().get().getValue().get();
    EUInformation euInfo = extensionObj.decode();
    HashMap<String, String> info = new HashMap<String, String>();
    info.put("Description", euInfo.getDescription().getText());
    info.put("DisplayName", euInfo.getDisplayName().getText());

    return new Variant(info);
  }

  /**
   * @fn Variant readEURange()
   * @brief read EU range
   * @return Variant
   */
  private Variant readEURange() throws InterruptedException, ExecutionException {
    ExtensionObject extensionObj =
        (ExtensionObject) getNodeInstance().eURange().get().getValue().get();
    Range EURange = extensionObj.decode();
    HashMap<String, String> range = new HashMap<String, String>();
    range.put("High", EURange.getHigh().toString());
    range.put("Low", EURange.getLow().toString());

    return new Variant(range);
  }

  /**
   * @fn CompletableFuture<PropertyNode> readAsyncData(EdgeNodeIdentifier id, EdgeMessage msg)
   * @brief read data (async)
   * @param [in] EdgeNodeIdentifier id
   * @param [in] EdgeMessage msg
   * @return CompletableFuture<PropertyNode>
   */
  private CompletableFuture<PropertyNode> readAsyncData(EdgeNodeIdentifier id, EdgeMessage msg) {
    CompletableFuture<PropertyNode> future = null;
    EdgeNodeInfo ep = msg.getRequest().getEdgeNodeInfo();
    if (EdgeNodeIdentifier.AnalogItemType_InstrumentRange == id) {
      future = getNodeInstance().instrumentRange().thenApply(property -> {
        return property;
      }).exceptionally(e -> {
        Optional.ofNullable(ep).ifPresent(endpoint -> {
          ErrorHandler.getInstance().addErrorMessage(endpoint,
              new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
              new EdgeVersatility.Builder(e.getMessage()).build(), msg.getRequest().getRequestId());
        });
        return null;
      });
    } else if (EdgeNodeIdentifier.AnalogItemType_EngineeringUnits == id) {
      future = getNodeInstance().engineeringUnits().thenApply(property -> {
        return property;
      }).exceptionally(e -> {
        Optional.ofNullable(ep).ifPresent(endpoint -> {
          ErrorHandler.getInstance().addErrorMessage(endpoint,
              new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
              new EdgeVersatility.Builder(e.getMessage()).build(), msg.getRequest().getRequestId());
        });
        return null;
      });
    } else if (EdgeNodeIdentifier.AnalogItemType_EURange == id) {
      future = getNodeInstance().eURange().thenApply(property -> {
        return property;
      }).exceptionally(e -> {
        Optional.ofNullable(ep).ifPresent(endpoint -> {
          ErrorHandler.getInstance().addErrorMessage(ep,
              new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
              new EdgeVersatility.Builder(e.getMessage()).build(), msg.getRequest().getRequestId());
        });
        return null;
      });
    }
    return future;
  }

  /**
   * @fn CompletableFuture<HashMap<String, String>> convertProperty(PropertyNode property,
   *     EdgeNodeIdentifier id)
   * @brief convert the property of edge node
   * @param [in] PropertyNode property
   * @param [in] EdgeNodeIdentifier id
   * @return CompletableFuture<HashMap<String, String>>
   */
  private CompletableFuture<HashMap<String, String>> convertProperty(PropertyNode property,
      EdgeNodeIdentifier id) {
    CompletableFuture<HashMap<String, String>> future = null;
    if (EdgeNodeIdentifier.AnalogItemType_InstrumentRange == id
        || EdgeNodeIdentifier.AnalogItemType_EURange == id) {
      future = property.getValue().thenApply(value -> {
        return convertToRangeInfo(value);
      });
    } else if (EdgeNodeIdentifier.AnalogItemType_EngineeringUnits == id) {
      future = property.getValue().thenApply(value -> {
        return convertToEUInfo(value);
      });
    }
    return future;
  }

  /**
   * @fn HashMap<String, String> convertToRangeInfo(Object obj)
   * @brief convert object to range information
   * @param [in] Object obj
   * @return HashMap<String, String>
   */
  private HashMap<String, String> convertToRangeInfo(Object obj) {
    ExtensionObject extensionObj = (ExtensionObject) obj;
    Range range = extensionObj.decode();
    HashMap<String, String> info = new HashMap<String, String>();
    info.put("High", range.getHigh().toString());
    info.put("Low", range.getLow().toString());

    return info;
  }

  /**
   * @fn HashMap<String, String> convertToEUInfo(Object opj)
   * @brief convert object to EU information
   * @param [in] Object obj
   * @return HashMap<String, String>
   */
  private HashMap<String, String> convertToEUInfo(Object opj) {
    ExtensionObject extensionObj = (ExtensionObject) opj;
    EUInformation euInfo = extensionObj.decode();
    HashMap<String, String> info = new HashMap<String, String>();
    info.put("Description", euInfo.getDescription().getText());
    info.put("DisplayName", euInfo.getDisplayName().getText());

    return info;
  }

  /**
   * @fn EdgeResult write(EdgeMessage msg)
   * @brief write edge message
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
   * @fn EdgeNodeIdentifier getNodeType()
   * @brief get note type with analog item type
   * @param [in] EdgeMessage msg
   * @return EdgeNodeIdentifier with AnalogItemType
   */
  @Override
  public EdgeNodeIdentifier getNodeType() throws Exception {
    return EdgeNodeIdentifier.AnalogItemType;
  }

  /**
   * @fn EdgeResult readAsync(EdgeMessage msg)
   * @brief read node data asynchronously
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
          EdgeEndpointInfo epInfo =
              new EdgeEndpointInfo.Builder(msg.getEdgeEndpointInfo().getEndpointUri())
                  .setFuture(msg.getEdgeEndpointInfo().getFuture()).build();
          EdgeMessage inputData = new EdgeMessage.Builder(epInfo)
              .setMessageType(EdgeMessageType.GENERAL_RESPONSE)
              .setResponses(
                  newArrayList(new EdgeResponse.Builder(ep, msg.getRequest().getRequestId())
                      .setMessage(new EdgeVersatility.Builder(value.getValue().getValue()).build())
                      .build()))
              .build();
          ProtocolManager.getProtocolManagerInstance().getRecvDispatcher().putQ(inputData);
        });
      });
    } else if (EdgeNodeIdentifier.AnalogItemType_Definition == id) {
      readAsyncDefinition(getNodeInstance(), msg).thenAccept(values -> {
        Optional.ofNullable(values).ifPresent(value -> {
          EdgeEndpointInfo epInfo =
              new EdgeEndpointInfo.Builder(msg.getEdgeEndpointInfo().getEndpointUri())
                  .setFuture(msg.getEdgeEndpointInfo().getFuture()).build();
          EdgeMessage inputData =
              new EdgeMessage.Builder(epInfo).setMessageType(EdgeMessageType.GENERAL_RESPONSE)
                  .setResponses(
                      newArrayList(new EdgeResponse.Builder(ep, msg.getRequest().getRequestId())
                          .setMessage(new EdgeVersatility.Builder(value).build()).build()))
                  .build();
          ProtocolManager.getProtocolManagerInstance().getRecvDispatcher().putQ(inputData);
        });
      });
    } else if (EdgeNodeIdentifier.AnalogItemType_InstrumentRange == id
        || EdgeNodeIdentifier.AnalogItemType_EngineeringUnits == id
        || EdgeNodeIdentifier.AnalogItemType_EURange == id) {
      final EdgeNodeIdentifier ID = id;
      readAsyncData(id, msg).thenAccept(property -> {
        Optional.ofNullable(property).ifPresent(value -> {
          convertProperty(property, ID).thenAccept(info -> {
            EdgeEndpointInfo epInfo =
                new EdgeEndpointInfo.Builder(msg.getEdgeEndpointInfo().getEndpointUri())
                    .setFuture(msg.getEdgeEndpointInfo().getFuture()).build();
            EdgeMessage inputData = new EdgeMessage.Builder(epInfo)
                .setMessageType(EdgeMessageType.GENERAL_RESPONSE)
                .setResponses(
                    newArrayList(new EdgeResponse.Builder(ep, msg.getRequest().getRequestId())
                        .setMessage(new EdgeVersatility.Builder(info).build()).build()))
                .build();
            ProtocolManager.getProtocolManagerInstance().getRecvDispatcher().putQ(inputData);
          });
        });
      });
    } else if (EdgeNodeIdentifier.AnalogItemType_ValuePrecision == id) {
      readAsyncValuePrecision(getNodeInstance(), msg).thenAccept(values -> {
        Optional.ofNullable(values).ifPresent(value -> {
          EdgeEndpointInfo epInfo =
              new EdgeEndpointInfo.Builder(msg.getEdgeEndpointInfo().getEndpointUri())
                  .setFuture(msg.getEdgeEndpointInfo().getFuture()).build();
          EdgeMessage inputData = new EdgeMessage.Builder(epInfo)
              .setMessageType(EdgeMessageType.GENERAL_RESPONSE)
              .setResponses(
                  newArrayList(new EdgeResponse.Builder(ep, msg.getRequest().getRequestId())
                      .setMessage(new EdgeVersatility.Builder(values).build()).build()))
              .build();
          ProtocolManager.getProtocolManagerInstance().getRecvDispatcher().putQ(inputData);
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

  /**
   * @fn void setMapper()
   * @brief set edge mapper
   * @return void
   */
  @Override
  public void setMapper() throws Exception {
    mapper = new EdgeMapper();

    if (null != dataType) {
      mapper.addMappingData(EdgeMapperCommon.DEVICEOBJECT_ATTRIBUTE_DATATYPE.name(), dataType);
    }
    mapper.addMappingData(EdgeMapperCommon.PROPERTYVALUE_TYPE.name(),
        EdgeNodeIdentifier.Number.name().toString());
    mapper.addMappingData(EdgeMapperCommon.PROPERTYVALUE_READWRITE.name(),
        EdgeIdentifier.convertAccessLevel(readAccessLevel));

    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(EdgeOpcUaCommon.WELL_KNOWN_LOCALHOST_URI.getValue()).build();
    EdgeMessage msg = new EdgeMessage.Builder(epInfo)
        .setMessageType(EdgeMessageType.GENERAL_RESPONSE)
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
    readAsyncData(EdgeNodeIdentifier.AnalogItemType_EngineeringUnits, msg).thenAccept(property -> {
      Optional.ofNullable(property).ifPresent(value -> {
        convertProperty(property, EdgeNodeIdentifier.AnalogItemType_EngineeringUnits)
            .thenAccept(values -> {
              try {
                mapper.addMappingData(EdgeMapperCommon.UNITS_TYPES.name(),
                    values.get("DisplayName"));
              } catch (Exception e) {
                e.printStackTrace();
              }
            });
      });
    });
    readAsyncData(EdgeNodeIdentifier.AnalogItemType_EURange, msg).thenAccept(property -> {
      Optional.ofNullable(property).ifPresent(value -> {
        convertProperty(property, EdgeNodeIdentifier.AnalogItemType_EURange).thenAccept(values -> {
          try {
            mapper.addMappingData(EdgeMapperCommon.PROPERTYVALUE_MAX.name(), values.get("High"));
            mapper.addMappingData(EdgeMapperCommon.PROPERTYVALUE_MIN.name(), values.get("Low"));
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
      });
    });
  }

  /**
   * @fn void getMapper()
   * @brief get edge mapper
   * @return EdgeMapper
   */
  public EdgeMapper getMapper() {
    return mapper;
  }
}
