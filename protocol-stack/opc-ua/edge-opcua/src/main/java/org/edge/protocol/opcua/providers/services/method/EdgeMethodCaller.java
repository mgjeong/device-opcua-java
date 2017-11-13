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

package org.edge.protocol.opcua.providers.services.method;

import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.l;
import java.util.concurrent.CompletableFuture;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.structured.CallMethodRequest;
import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.client.EdgeResponse;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeMessageType;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edge.protocol.opcua.session.EdgeOpcUaClient;
import org.edge.protocol.opcua.session.EdgeSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.google.common.collect.Lists.newArrayList;

public class EdgeMethodCaller {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private static EdgeMethodCaller caller = null;
  private static Object lock = new Object();

  private EdgeMethodCaller() {

  }

  /**
   * @fn EdgeMethodCaller getInstance()
   * @brief get EdgeMethodCaller Instance
   * @return EdgeMethodCaller Instance
   */
  public static EdgeMethodCaller getInstance() {
    synchronized (lock) {
      if (null == caller) {
        caller = new EdgeMethodCaller();
      }
      return caller;
    }
  }

  /**
   * @fn void close()
   * @brief close EdgeMethodCaller Instance
   * @return void
   */
  public void close() {
    caller = null;
  }

  /**
   * @fn EdgeResult executeAsync(EdgeEndpoint ep, EdgeVersatility param)
   * @brief execute with parameter for endpoint (Async)
   * @param [in] msg edge message
   * @param [in] param parameter
   * @param [in] objectId object Id
   * @param [in] methodId method Id
   * @return result
   */
  public EdgeResult executeAsync(EdgeMessage msg, EdgeVersatility param, NodeId objectId,
      NodeId methodId) throws Exception {
    EdgeNodeInfo ep = msg.getRequest().getEdgeNodeInfo();

    if (ep.getEdgeNodeID() == null) {
      new EdgeResult.Builder(EdgeStatusCode.STATUS_PARAM_INVALID).build();
    }

    logger.info("runMethod method={}", ep.getMethodName());
    EdgeOpcUaClient client =
        EdgeSessionManager.getInstance().getSession(msg.getEdgeEndpointInfo().getEndpointUri());
    CompletableFuture<Void> future = new CompletableFuture<>();
    boolean isGood = true;

    try {
      callMethodAsync(client, objectId, methodId, param).exceptionally(ex -> {
        logger.error("error invoking sqrt()", ex);
        return -1.0;
      }).thenAccept(v -> {
        logger.info("method result={}", v);

        EdgeEndpointInfo epInfo =
            new EdgeEndpointInfo.Builder(msg.getEdgeEndpointInfo().getEndpointUri()).build();
        EdgeMessage inputData =
            new EdgeMessage.Builder(epInfo).setMessageType(EdgeMessageType.GENERAL_RESPONSE)
                .setResponses(
                    newArrayList(new EdgeResponse.Builder(ep, msg.getRequest().getRequestId())
                        .setMessage(new EdgeVersatility.Builder(v).build()).build()))
                .build();
        ProtocolManager.getProtocolManagerInstance().getRecvDispatcher().putQ(inputData);

        future.complete(null);
      });
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      isGood = false;
    }
    return new EdgeResult.Builder(isGood ? EdgeStatusCode.STATUS_OK : EdgeStatusCode.STATUS_ERROR)
        .build();
  }

  /**
   * @fn CompletableFuture<Double> callMethodAsync(EdgeOpcUaClient client, NodeId pNodeId, NodeId
   *     mNodeId, Double input)
   * @brief Call method (Async)
   * @param [in] EdgeOpcUaClient client
   * @param [in] NodeId pNodeId
   * @param [in] NodeId mNodeId
   * @param [in] EdgeVariant param
   * @return CompletableFuture<Double>
   */
  private CompletableFuture<Double> callMethodAsync(EdgeOpcUaClient client, NodeId pNodeId,
      NodeId mNodeId, EdgeVersatility param) throws Exception {
    logger.info("callMethodAsync");
    CallMethodRequest request =
        new CallMethodRequest(pNodeId, mNodeId, new Variant[] {new Variant(param.getValue())});

    return client.getClientInstance().call(request).thenCompose(result -> {
      StatusCode statusCode = result.getStatusCode();

      if (statusCode.isGood()) {
        Double value = (Double) l(result.getOutputArguments()).get(0).getValue();
        return CompletableFuture.completedFuture(value);
      } else {
        CompletableFuture<Double> f = new CompletableFuture<>();
        f.completeExceptionally(new UaException(statusCode));
        return f;
      }
    });
  }
}
