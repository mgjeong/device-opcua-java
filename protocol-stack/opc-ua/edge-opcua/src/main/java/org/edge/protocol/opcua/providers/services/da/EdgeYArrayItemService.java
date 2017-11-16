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
import org.eclipse.milo.opcua.sdk.client.model.nodes.variables.PropertyNode;
import org.eclipse.milo.opcua.sdk.client.model.nodes.variables.YArrayItemNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.structured.AxisInformation;
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

public class EdgeYArrayItemService extends EdgeArrayItemService {
  private YArrayItemNode node = null;
  private EdgeMapper mapper = null;

  private final int nameSpace;
  private final String endpointUri;

  private static Object lock = new Object();

  private final Logger logger = LoggerFactory.getLogger(getClass());

  public EdgeYArrayItemService(int nameSpace, String endpointUri) {
    this.nameSpace = nameSpace;
    this.endpointUri = endpointUri;
  }

  private YArrayItemNode getNodeInstance() {
    synchronized (lock) {
      if (null == node)
        node = new YArrayItemNode(
            EdgeSessionManager.getInstance().getSession(endpointUri).getClientInstance(),
            new NodeId(nameSpace, EdgeNodeIdentifier.YArrayItemType.value()));
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
        .setEdgeNodeId(new EdgeNodeId.Builder(nameSpace, EdgeNodeIdentifier.YArrayItemType).build())
        .setValueAlias(valueAilas).build();
  }

  @Override
  public EdgeResult readSync(EdgeMessage msg) throws Exception {
    Variant ret = null;
    EdgeNodeInfo ep = msg.getRequest().getEdgeNodeInfo();
    EdgeNodeIdentifier id = EdgeNodeIdentifier.ReadValueId;
    if (ep.getEdgeNodeID() != null) {
      id = ep.getEdgeNodeID().getEdgeNodeIdentifier();
    }

    if (EdgeNodeIdentifier.ReadValueId == id)
      ret = readValue(getNodeInstance().getValue().get());
    else if (EdgeNodeIdentifier.YArrayItemType_Definition == id)
      ret = readDefinition(getNodeInstance().getDefinition().get());
    else if (EdgeNodeIdentifier.YArrayItemType_AxisScaleType == id)
      ret = readAxisScaleType(getNodeInstance().axisScaleType().get().getValue().get());
    else if (EdgeNodeIdentifier.YArrayItemType_EngineeringUnits == id)
      ret = readEngineeringUnits(getNodeInstance().engineeringUnits().get().getValue().get());
    else if (EdgeNodeIdentifier.YArrayItemType_EURange == id)
      ret = readEURange(getNodeInstance().eURange().get().getValue().get());
    else if (EdgeNodeIdentifier.YArrayItemType_ValuePrecision == id)
      ret = readValuePrecision(getNodeInstance().getValuePrecision().get());
    else if (EdgeNodeIdentifier.YArrayItemType_Title == id)
      ret = readTitle(getNodeInstance().getTitle().get().getText());
    else if (EdgeNodeIdentifier.YArrayItemType_InstrumentRange == id)
      ret = readInstrumentRange(getNodeInstance().instrumentRange().get().getValue().get());
    else if (EdgeNodeIdentifier.YArrayItemType_XAxisDefinition == id)
      ret = readXAxisDefinition();
    else
      return new EdgeResult.Builder(EdgeStatusCode.STATUS_PARAM_INVALID).build();

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

  private Variant readXAxisDefinition() throws InterruptedException, ExecutionException {
    return new Variant(
        convertToAxisDefInfo(getNodeInstance().xAxisDefinition().get().getValue().get()));
  }

  private CompletableFuture<PropertyNode> readAsyncXAxisDefinition(EdgeMessage msg) {
    return getNodeInstance().xAxisDefinition().thenApply(value -> {
      return value;
    }).exceptionally(e -> {
      Optional.ofNullable(msg.getRequest().getEdgeNodeInfo()).ifPresent(endpoint -> {
        ErrorHandler.getInstance().addErrorMessage(endpoint,
            new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
            new EdgeVersatility.Builder(e.getMessage()).build(), msg.getRequest().getRequestId());
      });
      return null;
    });
  }

  private CompletableFuture<HashMap<String, String>> convertAxisProperty(PropertyNode property) {
    return property.getValue().thenApply(values -> {
      return convertToAxisDefInfo(values);
    });
  }

  private HashMap<String, String> convertToAxisDefInfo(Object obj) {
    ExtensionObject extensionObject = (ExtensionObject) obj;
    AxisInformation axisInfo = (AxisInformation) extensionObject.decode();
    String prefix = "X_";

    HashMap<String, String> info = new HashMap<String, String>();
    info.put(prefix + "Title", axisInfo.getTitle().getText().toString());
    info.put(prefix + "High", axisInfo.getEURange().getHigh().toString());
    info.put(prefix + "Low", axisInfo.getEURange().getLow().toString());
    logger.info("AxisDefinition Title : {}", axisInfo.getTitle().getText().toString());

    return info;
  }

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
          addResponse(values.getValue().getValue(), ep, msg);
        });
      });
    } else if (EdgeNodeIdentifier.YArrayItemType_Definition == id) {
      readAsyncDefinition(getNodeInstance(), msg).thenAccept(values -> {
        Optional.ofNullable(values).ifPresent(value -> {
          addResponse(values, ep, msg);
        });
      });
    } else if (EdgeNodeIdentifier.YArrayItemType_EURange == id) {
      readAsyncEURange(getNodeInstance(), msg).thenAccept(property -> {
        Optional.ofNullable(property).ifPresent(value -> {
          convertProperty(property, EdgeNodeIdentifier.Range).thenAccept(info -> {
            addResponse(info, ep, msg);
          });
        });
      });
    } else if (EdgeNodeIdentifier.YArrayItemType_InstrumentRange == id) {
      readAsyncInstrumentRange(getNodeInstance(), msg).thenAccept(property -> {
        Optional.ofNullable(property).ifPresent(value -> {
          convertProperty(property, EdgeNodeIdentifier.Range).thenAccept(info -> {
            addResponse(info, ep, msg);
          });
        });
      });
    } else if (EdgeNodeIdentifier.YArrayItemType_EngineeringUnits == id) {
      readAsyncEngineeringUnits(getNodeInstance(), msg).thenAccept(property -> {
        Optional.ofNullable(property).ifPresent(value -> {
          convertProperty(property, EdgeNodeIdentifier.EUInformation).thenAccept(info -> {
            addResponse(info, ep, msg);
          });
        });
      });
    } else if (EdgeNodeIdentifier.YArrayItemType_ValuePrecision == id) {
      readAsyncValuePrecision(getNodeInstance(), msg).thenAccept(values -> {
        Optional.ofNullable(values).ifPresent(value -> {
          addResponse(values, ep, msg);
        });
      });
    } else if (EdgeNodeIdentifier.YArrayItemType_Title == id) {
      readAsyncTitle(getNodeInstance(), msg).thenAccept(values -> {
        Optional.ofNullable(values).ifPresent(value -> {
          addResponse(values, ep, msg);
        });
      });
    } else if (EdgeNodeIdentifier.YArrayItemType_AxisScaleType == id) {
      readAsyncAxisScaleType(getNodeInstance(), msg).thenAccept(property -> {
        Optional.ofNullable(property).ifPresent(values -> {
          property.getValue().thenAccept(value -> {
            addResponse(value, ep, msg);
          });
        });
      });
    } else if (EdgeNodeIdentifier.YArrayItemType_XAxisDefinition == id) {
      readAsyncXAxisDefinition(msg).thenAccept(property -> {
        Optional.ofNullable(property).ifPresent(values -> {
          convertAxisProperty(property).thenAccept(info -> {
            addResponse(info, ep, msg);
          });
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

  @Override
  public EdgeNodeIdentifier getNodeType() throws Exception {
    return EdgeNodeIdentifier.YArrayItemType;
  }

  @Override
  public void setMapper() throws Exception {
    mapper = new EdgeMapper();

    if (null != dataType) {
      mapper.addMappingData(EdgeMapperCommon.DEVICEOBJECT_ATTRIBUTE_DATATYPE.name(), dataType);
    }

    mapper.addMappingData(EdgeMapperCommon.PROPERTYVALUE_TYPE.name(),
        EdgeNodeIdentifier.BaseDataType.name().toString());
    mapper.addMappingData(EdgeMapperCommon.PROPERTYVALUE_READWRITE.name(),
        EdgeIdentifier.convertAccessLevel(readAccessLevel));

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
          mapper.addMappingData(EdgeMapperCommon.PROPERTYVALUE_PRECISION.name(), values.toString());
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    });
    readAsyncEngineeringUnits(getNodeInstance(), msg).thenAccept(property -> {
      Optional.ofNullable(property).ifPresent(value -> {
        convertProperty(property, EdgeNodeIdentifier.EUInformation).thenAccept(values -> {
          try {
            mapper.addMappingData(EdgeMapperCommon.UNITS_TYPES.name(), values.get("DisplayName"));
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
      });
    });
    readAsyncEURange(getNodeInstance(), msg).thenAccept(property -> {
      convertProperty(property, EdgeNodeIdentifier.Range).thenAccept(values -> {
        Optional.ofNullable(property).ifPresent(value -> {
          readAsyncXAxisDefinition(msg).thenAccept(definition -> {
            Optional.ofNullable(definition).ifPresent(infos -> {
              convertAxisProperty(definition).thenAccept(info -> {
                try {
                  mapper.addMappingData(EdgeMapperCommon.PROPERTYVALUE_MIN.name(),
                      "X:" + info.get("X_Low") + " Y:" + values.get("Low"));
                  mapper.addMappingData(EdgeMapperCommon.PROPERTYVALUE_MAX.name(),
                      "X:" + info.get("X_High") + " Y:" + values.get("High"));
                } catch (Exception e) {
                  e.printStackTrace();
                }
              });
            });
          });
        });
      });
    });
    readAsyncAxisScaleType(getNodeInstance(), msg).thenAccept(property -> {
      Optional.ofNullable(property).ifPresent(value -> {
        property.getValue().thenAccept(values -> {
          try {
            mapper.addMappingData(EdgeMapperCommon.PROPERTYVALUE_SCALE.name(), values.toString());
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
      });
    });
  }

  public EdgeMapper getMapper() {
    return mapper;
  }
}
