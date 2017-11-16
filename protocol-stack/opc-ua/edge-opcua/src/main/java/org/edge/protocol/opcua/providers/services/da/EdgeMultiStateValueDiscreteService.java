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
import org.eclipse.milo.opcua.sdk.client.model.nodes.variables.MultiStateValueDiscreteNode;
import org.eclipse.milo.opcua.sdk.client.model.nodes.variables.PropertyNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.structured.EnumValueType;
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

public class EdgeMultiStateValueDiscreteService extends EdgeDataItemService {
  private MultiStateValueDiscreteNode node = null;
  private EdgeMapper mapper = null;

  private final int nameSpace;
  private final String endpointUri;

  private static Object lock = new Object();

  private final Logger logger = LoggerFactory.getLogger(getClass());

  public EdgeMultiStateValueDiscreteService(int nameSpace, String endpointUri) {
    this.nameSpace = nameSpace;
    this.endpointUri = endpointUri;

    try {
      this.setMapper();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private MultiStateValueDiscreteNode getNodeInstance() {
    synchronized (lock) {
      if (null == node)
        node = new MultiStateValueDiscreteNode(
            EdgeSessionManager.getInstance().getSession(endpointUri).getClientInstance(),
            new NodeId(nameSpace, EdgeNodeIdentifier.MultiStateValueDiscreteType.value()));
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
    return new EdgeNodeInfo.Builder().setEdgeNodeId(
        new EdgeNodeId.Builder(nameSpace, EdgeNodeIdentifier.MultiStateValueDiscreteType).build())
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

    try {
      if (EdgeNodeIdentifier.ReadValueId == id)
        ret = readValue(getNodeInstance().getValue().get());
      else if (EdgeNodeIdentifier.MultiStateValueDiscreteType_Definition == id)
        ret = readDefinition(getNodeInstance().getDefinition().get());
      else if (EdgeNodeIdentifier.MultiStateValueDiscreteType_ValuePrecision == id)
        ret = readValuePrecision(getNodeInstance().getValuePrecision().get());
      else if (EdgeNodeIdentifier.MultiStateValueDiscreteType_EnumValues == id)
        ret = readEnumValues();
      else if (EdgeNodeIdentifier.MultiStateValueDiscreteType_ValueAsText == id)
        ret = readValueAsText();
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
    } catch (Exception e) {
      e.printStackTrace();
    }

    return new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build();
  }

  private Variant readEnumValues() throws InterruptedException, ExecutionException {
    return new Variant(
        convertToEnumValuesInfo(getNodeInstance().enumValues().get().getValue().get()));
  }

  private CompletableFuture<PropertyNode> readAsyncEnumValues(EdgeMessage msg) {
    return getNodeInstance().enumValues().thenApply(value -> {
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

  private Variant readValueAsText() throws InterruptedException, ExecutionException {
    return new Variant(getNodeInstance().getValueAsText().get().getText());
  }

  private CompletableFuture<LocalizedText> readAsyncValueAsText(EdgeMessage msg) {
    return getNodeInstance().getValueAsText().thenApply(value -> {
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

  private HashMap<String, String> convertToEnumValuesInfo(Object obj)
      throws InterruptedException, ExecutionException {
    ExtensionObject[] extensionObjects = (ExtensionObject[]) obj;
    EnumValueType enumValueType;
    HashMap<String, String> info = new HashMap<String, String>();

    for (ExtensionObject extensionObj : extensionObjects) {
      enumValueType = (EnumValueType) extensionObj.decode();
      info.put(enumValueType.getDisplayName().getText(), enumValueType.getValue().toString());
      logger.info("{} : {}", enumValueType.getDisplayName().getText(),
          enumValueType.getValue().toString());
    }

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
    } else if (EdgeNodeIdentifier.MultiStateValueDiscreteType_Definition == id) {
      readAsyncDefinition(getNodeInstance(), msg).thenAccept(values -> {
        Optional.ofNullable(values).ifPresent(value -> {
          addResponse(values, ep, msg);
        });
      });
    } else if (EdgeNodeIdentifier.MultiStateValueDiscreteType_ValuePrecision == id) {
      readAsyncValuePrecision(getNodeInstance(), msg).thenAccept(values -> {
        Optional.ofNullable(values).ifPresent(value -> {
          addResponse(values, ep, msg);
        });
      });
    } else if (EdgeNodeIdentifier.MultiStateValueDiscreteType_EnumValues == id) {
      readAsyncEnumValues(msg).thenAccept(property -> {
        Optional.ofNullable(property).ifPresent(values -> {
          property.getValue().thenAccept(value -> {
            Variant info = null;
            try {
              info = new Variant(convertToEnumValuesInfo(value));
            } catch (InterruptedException | ExecutionException e) {
              e.printStackTrace();
            }
            if (info != null) {
              addResponse(info.getValue(), ep, msg);
            }
          });
        });
      });
    } else if (EdgeNodeIdentifier.MultiStateValueDiscreteType_ValueAsText == id) {
      readAsyncValueAsText(msg).thenAccept(values -> {
        Optional.ofNullable(values).ifPresent(value -> {
          addResponse(values.getText(), ep, msg);
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
    return EdgeNodeIdentifier.MultiStateDiscreteType;
  }

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
  }

  public EdgeMapper getMapper() {
    return mapper;
  }
}
