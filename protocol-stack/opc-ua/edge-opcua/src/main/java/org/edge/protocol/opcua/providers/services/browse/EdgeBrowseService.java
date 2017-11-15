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

package org.edge.protocol.opcua.providers.services.browse;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.toList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseResultMask;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseNextResponse;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResponse;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.ViewDescription;
import org.edge.protocol.opcua.api.ProtocolManager;
import org.edge.protocol.opcua.api.client.EdgeBrowseParameter;
import org.edge.protocol.opcua.api.client.EdgeResponse;
import org.edge.protocol.opcua.api.common.EdgeBrowseResult;
import org.edge.protocol.opcua.api.common.EdgeEndpointInfo;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeNodeType;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edge.protocol.opcua.api.common.EdgeMessageType;
import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeRequest;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.api.common.EdgeVersatility;
import org.edge.protocol.opcua.queue.ErrorHandler;
import org.edge.protocol.opcua.session.EdgeOpcUaClient;
import org.edge.protocol.opcua.session.EdgeSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.google.common.collect.Lists.newArrayList;

public class EdgeBrowseService {

  private final Logger logger = LoggerFactory.getLogger(EdgeBrowseService.class);
  private static EdgeBrowseService service = null;
  private static Object lock = new Object();
  // private static HashSet<NodeId> visitedNode = new HashSet<NodeId>();
  private static List<ByteString> visitedCP = new ArrayList<ByteString>();
  private static final int BROWSE_DESCRIPTION_NODECLASS_MASK =
      NodeClass.Object.getValue() | NodeClass.Variable.getValue()
          | NodeClass.ReferenceType.getValue() | NodeClass.Method.getValue();

  private static HashMap<Integer, HashSet<NodeId>> visitedNodeId =
      new HashMap<Integer, HashSet<NodeId>>();
  private static final int visitedMapSize = EdgeOpcUaCommon.MAX_BROWSEREQUEST_SIZE + 10;
  private static int visitedMapQueue[] = new int[visitedMapSize];
  private static int visitedMapIdx = 0;

  private EdgeBrowseService() {

  }

  /**
   * @fn EdgeBrowseService getInstance()
   * @brief get EdgeBrowseService Instance
   * @return EdgeBrowseService Instance
   */
  public static EdgeBrowseService getInstance() {
    synchronized (lock) {
      if (null == service) {
        service = new EdgeBrowseService();
      }
      return service;
    }
  }

  private static void initializeVisitedNode(int requestId) {
    if (visitedNodeId.containsKey(visitedMapQueue[visitedMapIdx]) == true) {
      visitedNodeId.get(visitedMapQueue[visitedMapIdx]).clear();
      visitedNodeId.remove(visitedMapQueue[visitedMapIdx]);
    }
    visitedMapQueue[visitedMapIdx] = requestId;
    if (visitedMapIdx + 1 == visitedMapSize) {
      visitedMapIdx = 0;
    } else {
      visitedMapIdx++;
    }
    visitedNodeId.put(requestId, new HashSet<NodeId>());
  }

  /**
   * @fn void browse(String indent, EdgeEndpoint ep)
   * @brief browse target parent node and its child node
   * @param [in] indent empty space
   * @param [in] msg edge message set
   * @return void
   */
  public void browse(String indent, EdgeMessage msg) throws Exception {
    logger.info("Browse request size={}, msg type={}", msg.getRequests().size(),
        msg.getMessageType());
    EdgeOpcUaClient client =
        EdgeSessionManager.getInstance().getSession(msg.getEdgeEndpointInfo().getEndpointUri());
    List<NodeId> nodeIdList = new ArrayList<NodeId>();
    List<Integer> msgIdxList = new ArrayList<Integer>();
    if (msg.getMessageType() == EdgeMessageType.SEND_REQUEST) {
      initializeVisitedNode(msg.getRequest().getRequestId());
      nodeIdList.add(getNodeId(msg.getRequest().getEdgeNodeInfo()));
      msgIdxList.add(Integer.valueOf(0));
    } else if (msg.getMessageType() == EdgeMessageType.SEND_REQUESTS) {
      if (msg.getRequests().size() > EdgeOpcUaCommon.MAX_BROWSEREQUEST_SIZE) {
        logger.info("error type : {}", EdgeStatusCode.STATUS_VIEW_BROWSEREQUEST_SIZEOVER);
        ErrorHandler.getInstance().addErrorMessage(getEndpoint(msg, 0),
            new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
            new EdgeVersatility.Builder(
                EdgeStatusCode.STATUS_VIEW_BROWSEREQUEST_SIZEOVER.getDescription()).build(),
            getRequestId(msg, 0));
        return;
      }
      int msgIdx = 0;
      for (EdgeRequest req : msg.getRequests()) {
        initializeVisitedNode(req.getRequestId());
        nodeIdList.add(getNodeId(req.getEdgeNodeInfo()));
        msgIdxList.add(Integer.valueOf(msgIdx++));
      }
    }
    logger.info("browseNodeIdList : {}", nodeIdList);
    browse(nodeIdList, msgIdxList, client, msg);
  }

  /**
   * @fn void browseNext(String indent, EdgeEndpoint ep)
   * @brief browse target parent node and its child node
   * @param [in] indent empty space
   * @param [in] msg edge message set
   * @return void
   */
  public void browseNext(String indent, EdgeMessage msg) throws Exception {
    logger.info("not support");
  }

  private boolean checkStatusGood(StatusCode status) {
    if (status.isGood() == false || status.getValue() == StatusCodes.Good_Clamped
        || status.getValue() == StatusCodes.Good_CompletesAsynchronously) {
      return false;
    } else {
      return true;
    }
  }

  private CompletableFuture<List<BrowseResult>> requestBrowse(OpcUaClient client,
      List<BrowseDescription> browseList, EdgeMessage msg) {
    client.browse(browseList);
    return client.browse(browseList).thenApply(browseResult -> {
      logger.info("browse result: {}", browseResult);
      return browseResult;
    }).exceptionally(e -> {
      logger.info("error types : {}", e.getMessage());
      ErrorHandler.getInstance().addErrorMessage(getEndpoint(msg, 0),
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder(e.getMessage()).build(), getRequestId(msg, 0));
      return null;
    });
  }

  private boolean checkContinuationPoint(ByteString continuationPoint, EdgeNodeInfo ep, int reqId) {
    boolean ret = true;
    if (continuationPoint.bytes() == null) {
      // TODO
      // this case from CTT Error-006 in client subscription. however since all browse result can
      // come with continuation null byte.
      // it is needed to check TC scenario again.

      // logger.info("checkContinuationPoint1 error type : {}",
      // EdgeStatusCode.CONTINUATIONPOINT_NULL);
      // ErrorHandler.getInstance().addErrorMessage(ep,
      // new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
      // new EdgeVersatility.Builder(EdgeStatusCode.CONTINUATIONPOINT_NULL).build(), reqId);
      // ret = false;
    } else if (continuationPoint.length() == 0) {
      logger.info("checkContinuationPoint error type : {}", EdgeStatusCode.CONTINUATIONPOINT_EMPTY);
      ErrorHandler.getInstance().addErrorMessage(ep,
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder(EdgeStatusCode.CONTINUATIONPOINT_EMPTY).build(), reqId);
      ret = false;
    } else if (continuationPoint.length() >= 1000) {
      logger.info("checkContinuationPoint error type : {}", EdgeStatusCode.CONTINUATIONPOINT_LONG);
      ErrorHandler.getInstance().addErrorMessage(ep,
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder(EdgeStatusCode.CONTINUATIONPOINT_LONG).build(), reqId);
      ret = false;
    }
    return ret;
  }

  // Check the Err-010 TC of the View CTT
  private boolean checkBrowseName(String browseName, EdgeNodeInfo ep, int reqId) {
    boolean ret = true;
    if (browseName == null) {
      logger.info("error type : {}", EdgeStatusCode.BROWSENAME_NULL);
      ErrorHandler.getInstance().addErrorMessage(ep,
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder(EdgeStatusCode.BROWSENAME_NULL).build(), reqId);
      ret = false;
    } else if (browseName.isEmpty() == true) {
      logger.info("error type : {}", EdgeStatusCode.BROWSENAME_EMPTY);
      ErrorHandler.getInstance().addErrorMessage(ep,
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder(EdgeStatusCode.BROWSENAME_EMPTY).build(), reqId);
      ret = false;
    } else if (browseName.length() >= 1000) {
      logger.info("error type : {}", EdgeStatusCode.BROWSENAME_LONG);
      ErrorHandler.getInstance().addErrorMessage(ep,
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder(EdgeStatusCode.BROWSENAME_LONG).build(), reqId);
      ret = false;
    }
    return ret;
  }

  // Check the Err-011 TC of the View CTT
  private boolean checkDisplayName(String displayName, EdgeNodeInfo ep, int reqId) {
    boolean ret = true;
    if (displayName == null) {
      logger.info("error type : {}", EdgeStatusCode.DISPLAYNAME_NULL);
      ErrorHandler.getInstance().addErrorMessage(ep,
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder(EdgeStatusCode.DISPLAYNAME_NULL).build(), reqId);
      ret = false;
    } else if (displayName.isEmpty() == true) {
      logger.info("error type : {}", EdgeStatusCode.DISPLAYNAME_EMPTY);
      ErrorHandler.getInstance().addErrorMessage(ep,
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder(EdgeStatusCode.DISPLAYNAME_EMPTY).build(), reqId);
      ret = false;
    } else if (displayName.length() >= 1000) {
      logger.info("error type : {}", EdgeStatusCode.DISPLAYNAME_LONG);
      ErrorHandler.getInstance().addErrorMessage(ep,
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder(EdgeStatusCode.DISPLAYNAME_LONG).build(), reqId);
      ret = false;
    }
    return ret;
  }

  // Check the Err-012 TC of the View CTT
  private boolean checkNodeClass(NodeClass nodeClass, EdgeNodeInfo ep, int reqId) {
    boolean ret = true;
    if (nodeClass == null) {
      logger.info("error type : {}", EdgeStatusCode.NODECLASS_NULL);
      ErrorHandler.getInstance().addErrorMessage(ep,
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder(EdgeStatusCode.NODECLASS_NULL).build(), reqId);
      ret = false;
    } else if (NodeClass.from(nodeClass.getValue()) == null) {
      logger.info("error type : {}", EdgeStatusCode.NODECLASS_INVALID);
      ErrorHandler.getInstance().addErrorMessage(ep,
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder(EdgeStatusCode.NODECLASS_INVALID).build(), reqId);
      ret = false;
    } else if ((nodeClass.getValue() & BROWSE_DESCRIPTION_NODECLASS_MASK) == 0) {
      String errorMsg = EdgeStatusCode.STATUS_VIEW_NOTINCLUDE_NODECLASS.getDescription();
      logger.info("error type : {}", errorMsg);
      ErrorHandler.getInstance().addErrorMessage(ep,
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder(errorMsg).build(), reqId);
      ret = false;
    }
    return ret;
  }

  private boolean checkNodeId(ExpandedNodeId nodeId, EdgeNodeInfo ep, int reqId) {
    boolean ret = true;
    if (nodeId.isNull()) {
      logger.info("error type : {}", EdgeStatusCode.NODEID_NULL);
      ErrorHandler.getInstance().addErrorMessage(ep,
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder(EdgeStatusCode.NODEID_NULL).build(), reqId);
      ret = false;
    } else if (nodeId.getServerIndex() != 0) {
      logger.info("error type : {}", EdgeStatusCode.NODEID_SERVERINDEX);
      ErrorHandler.getInstance().addErrorMessage(ep,
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder(EdgeStatusCode.NODEID_SERVERINDEX).build(), reqId);
      ret = false;
    }
    return ret;
  }

  private boolean checkReferenceTypeId(NodeId responseReferenceTypeId, EdgeNodeInfo ep, int reqId) {
    boolean ret = true;
    if (responseReferenceTypeId.isNull()) {
      logger.info("error type : {}", EdgeStatusCode.REFERENCETYPEID_NULL);
      ErrorHandler.getInstance().addErrorMessage(ep,
          new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
          new EdgeVersatility.Builder(EdgeStatusCode.REFERENCETYPEID_NULL).build(), reqId);
      ret = false;
    }
    return ret;
  }

  private void addVisitedNodeId(NodeId id, int requestId) {
    visitedNodeId.get(requestId).add(id);
  }

  private List<BrowseDescription> getBrowseDescriptions(List<NodeId> nodeIdList,
      List<Integer> msgIdxList, EdgeMessage msg) {
    List<BrowseDescription> browseList = new ArrayList<BrowseDescription>();
    int direct = msg.getBrowseParameter().getDirection();
    BrowseDirection directionParam = BrowseDirection.Forward;
    if (BrowseDirection.Inverse.ordinal() == direct) {
      directionParam = BrowseDirection.Inverse;
    } else if (BrowseDirection.Both.ordinal() == direct) {
      directionParam = BrowseDirection.Both;
    }
    int idx = 0;
    for (NodeId id : nodeIdList) {
      addVisitedNodeId(id, getRequestId(msg, msgIdxList.get(idx++)));
      BrowseDescription browse = new BrowseDescription(id, directionParam, Identifiers.References,
          true, uint(BROWSE_DESCRIPTION_NODECLASS_MASK), uint(BrowseResultMask.All.getValue()));
      browseList.add(browse);
    }
    return browseList;
  }

  private EdgeResult browseNext(String indent, EdgeOpcUaClient client, EdgeMessage msg) {
    return new EdgeResult.Builder(EdgeStatusCode.STATUS_NOT_SUPPROT).build();
  }

  private EdgeResult browse(List<NodeId> nodeIdList, List<Integer> msgIdxList,
      EdgeOpcUaClient client, EdgeMessage msg) {
    List<BrowseDescription> browseList = getBrowseDescriptions(nodeIdList, msgIdxList, msg);
    logger.info("browseList: {}", browseList);
    if (isMaxReferenceValue(msg)) {
      logger.info("requestBrowseWithMaxReference");
      requestBrowseWithMaxReference(client.getClientInstance(), browseList, msg)
          .thenApply(browseResponse -> {
            logger.info("continuationPoint = {}, ret={}",
                browseResponse.getResults()[0].getContinuationPoint().bytes(),
                browseResponse.getResponseHeader().getServiceResult());

            List<ByteString> bytes = new ArrayList<ByteString>();
            bytes.add(browseResponse.getResults()[0].getContinuationPoint());

            requestBrowseNext(client.getClientInstance(), msg, bytes).thenApply(response -> {
              checkBrowseNextErrorStatus(response, msg, browseResponse);
              return response;
            });
            return browseResponse;
          });
    } else {
      logger.info("requestBrowse");
      requestBrowse(client.getClientInstance(), browseList, msg).thenAccept(browseResults -> {
        if (Optional.ofNullable(browseResults).isPresent()) {
          if (browseResults.isEmpty() == true) {
            ErrorHandler.getInstance().addErrorMessage(getEndpoint(msg, 0),
                new EdgeResult.Builder(EdgeStatusCode.STATUS_VIEW_BROWSERESULT_EMPTY).build(),
                new EdgeVersatility.Builder(
                    EdgeStatusCode.STATUS_VIEW_BROWSERESULT_EMPTY.getDescription()).build(),
                getRequestId(msg, 0));
          }
          int retIdx = 0;
          boolean isError = false;
          int nodeIdUnknownCount = 0;
          for (BrowseResult ret : browseResults) {
            StatusCode status = ret.getStatusCode();
            int msgIdx = msgIdxList.get(retIdx);
            EdgeNodeInfo ep = getEndpoint(msg, msgIdx);
            int reqId = getRequestId(msg, msgIdx);
            int direct = getDirection(msg);

            if (checkStatusGood(status) == false) {
              logger.info("error");
              String errorStatusCode = EdgeStatusCode.UNKNOWN_STATUS_CODE;
              Optional<String[]> statusCodes = StatusCodes.lookup(status.getValue());
              if (statusCodes.isPresent() == true) {
                errorStatusCode = statusCodes.get()[0];
              }
              logger.info("error types : {}", errorStatusCode);
              if (ret.getStatusCode().getValue() == StatusCodes.Bad_NodeIdUnknown) {
                nodeIdUnknownCount++;
              }

              // Check Err-005 of basic View CTT
              if (nodeIdUnknownCount == browseResults.size()) {
                ErrorHandler.getInstance().addErrorMessage(ep,
                    new EdgeResult.Builder(EdgeStatusCode.STATUS_VIEW_NOIDID_UNKNOWN_ALL_RESULTS)
                        .build(),
                    new EdgeVersatility.Builder(
                        EdgeStatusCode.STATUS_VIEW_NOIDID_UNKNOWN_ALL_RESULTS.getDescription())
                            .build(),
                    reqId);
              } else {
                ErrorHandler.getInstance().addErrorMessage(ep,
                    new EdgeResult.Builder(EdgeStatusCode.STATUS_ERROR).build(),
                    new EdgeVersatility.Builder(errorStatusCode).build(), reqId);
              }
              isError = true;
            }

            checkContinuationPoint(ret.getContinuationPoint(), ep, reqId);
            List<ReferenceDescription> references = toList(ret.getReferences());
            List<NodeId> nextNodeIdList = new ArrayList<NodeId>();
            List<Integer> nextMsgIdxList = new ArrayList<Integer>();
            for (ReferenceDescription rd : references) {
              // logger.debug("direction = {}, cur_direct = {}", direct, rd.getIsForward());
              // logger.info("browseName = {}", rd.getBrowseName());

              if ((direct == EdgeBrowseParameter.DIRECTION_FORWARD && rd.getIsForward() == false)
                  || (direct == EdgeBrowseParameter.DIRECTION_INVERSE
                      && rd.getIsForward() == true)) {
                callErrorMessageCB(msg, EdgeStatusCode.STATUS_VIEW_DIRECTION_NOT_MATCH, msgIdx);
              }

              if (checkBrowseName(rd.getBrowseName().getName(), ep, reqId) == false)
                isError = true;
              if (checkNodeClass(rd.getNodeClass(), ep, reqId) == false)
                isError = true;
              if (checkDisplayName(rd.getDisplayName().getText(), ep, reqId) == false)
                isError = true;
              if (checkNodeId(rd.getNodeId(), ep, reqId) == false)
                isError = true;
              if (checkReferenceTypeId(rd.getReferenceTypeId(), ep, reqId) == false)
                isError = true;
              // recursively browse to children
              if (isError == false) {
                List<EdgeBrowseResult> browseResponses = new ArrayList<EdgeBrowseResult>();
                EdgeBrowseResult res = new EdgeBrowseResult.Builder()
                    .setBrowseName(rd.getBrowseName().toString()).build();
                browseResponses.add(res);
                callResponseMessage(msg, browseResponses, msgIdx);
              }

              try {
                rd.getNodeId().local().ifPresent(nodeId -> {
                  if (visitedNodeId.get(reqId).contains(nodeId) == false) {
                    nextNodeIdList.add(nodeId);
                    nextMsgIdxList.add(msgIdx);
                  }
                });
              } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
            if (nextNodeIdList.isEmpty() == false) {
              browse(nextNodeIdList, nextMsgIdxList, client, msg);
            }
            retIdx++;
          }
        } else {
          return;
        }
      });
    }
    return new EdgeResult.Builder(EdgeStatusCode.STATUS_OK).build();
  }

  private CompletableFuture<BrowseResponse> requestBrowseWithMaxReference(OpcUaClient client,
      List<BrowseDescription> browseList, EdgeMessage msg) {
    logger.info("requestBrowseWithMaxRefer");
    return client.browse(new ViewDescription(), uint(getMaxReference(msg)), browseList)
        .thenApply(browseResult -> {
          return browseResult;
        }).exceptionally(e -> {
          logger.info("error type : {}", e.getMessage());
          ErrorHandler.getInstance().addErrorMessage(getEndpoint(msg, 0),
              new EdgeResult.Builder(EdgeStatusCode.STATUS_SERVICE_RESULT_BAD).build(),
              new EdgeVersatility.Builder(EdgeStatusCode.STATUS_SERVICE_RESULT_BAD).build(),
              EdgeOpcUaCommon.DEFAULT_REQUEST_ID);
          return null;
        });
  }

  private CompletableFuture<BrowseNextResponse> requestBrowseNext(OpcUaClient client,
      EdgeMessage msg, List<ByteString> continuationPoint) {
    logger.info("requestBrowseNext");
    return client.browseNext(false, continuationPoint).thenApply(browseNextResponse -> {
      return browseNextResponse;
    }).exceptionally(e -> {
      logger.info("error type : {}", e.getMessage());
      ErrorHandler.getInstance().addErrorMessage(getEndpoint(msg, 0),
          new EdgeResult.Builder(EdgeStatusCode.STATUS_SERVICE_RESULT_BAD).build(),
          new EdgeVersatility.Builder(EdgeStatusCode.STATUS_SERVICE_RESULT_BAD).build(),
          EdgeOpcUaCommon.DEFAULT_REQUEST_ID);
      return null;
    });
  }

  private NodeId getNodeId(EdgeNodeInfo ep) {
    if (ep.getEdgeNodeID() == null) {
      return null;
    }

    if (ep.getEdgeNodeID().getEdgeNodeType() == EdgeNodeType.STRING) {
      return new NodeId(ep.getEdgeNodeID().getNameSpace(), ep.getEdgeNodeID().getEdgeNodeUri());
    } else {
      if (ep.getEdgeNodeID().getEdgeNodeIdentifier().equals(EdgeNodeIdentifier.ReadValueId)) {
        return new NodeId(ep.getEdgeNodeID().getNameSpace(), EdgeNodeIdentifier.RootFolder.value());
      } else {
        return new NodeId(ep.getEdgeNodeID().getNameSpace(),
            ep.getEdgeNodeID().getEdgeNodeIdentifier().value());
      }
    }
  }

  private EdgeNodeInfo getEndpoint(EdgeMessage msg, int msgIdx) {
    if (msg.getMessageType() == EdgeMessageType.SEND_REQUEST) {
      return msg.getRequest().getEdgeNodeInfo();
    } else {
      return msg.getRequests().get(msgIdx).getEdgeNodeInfo();
    }
  }

  private int getRequestId(EdgeMessage msg, int msgIdx) {
    if (msg.getMessageType() == EdgeMessageType.SEND_REQUEST) {
      return msg.getRequest().getRequestId();
    } else {
      return msg.getRequests().get(msgIdx).getRequestId();
    }
  }

  private int getDirection(EdgeMessage msg) {
    return msg.getBrowseParameter().getDirection();
  }

  private int getMaxReference(EdgeMessage msg) {
    return msg.getBrowseParameter().getMaxReferenceValue();
  }

  private boolean isMaxReferenceValue(EdgeMessage msg) {
    return msg.getBrowseParameter().isMaxReferenceValue();
  }

  private void checkBrowseNextErrorStatus(BrowseNextResponse response, EdgeMessage msg,
      BrowseResponse browseResponse) {
    EdgeStatusCode code = EdgeStatusCode.STATUS_OK;
    int retIdx = 0;
    for (BrowseResult ret : response.getResults()) {
      logger.info("browseNext ret = {} ", ret.getStatusCode());
      if (visitedCP.contains(ret.getContinuationPoint())) {
        code = EdgeStatusCode.STATUS_VIEW_CONTINUATION_POINT_REUSED;
        callErrorMessageCB(msg, code, retIdx);
      } else {
        visitedCP.add(ret.getContinuationPoint());
      }

      if (retIdx == 0 && ret.getStatusCode().isGood() == false) {
        code = EdgeStatusCode.STATUS_VIEW_RESULT_STATUS_CODE_BAD;
      } else if (retIdx == 0 && (ret.getContinuationPoint() != null && ret.getReferences() == null)
          || (ret.getStatusCode().isGood() && ret.getReferences().length == 0)) {
        code = EdgeStatusCode.STATUS_VIEW_REFERENCE_DATA_INVALID;
        callErrorMessageCB(msg, code, retIdx);
      }

      int referIdx = 0;
      int direct = getDirection(msg);
      for (ReferenceDescription reference : ret.getReferences()) {
        logger.info("direction = {}, cur_direct = {}", direct, reference.getIsForward());
        if ((direct == EdgeBrowseParameter.DIRECTION_FORWARD && reference.getIsForward() == false)
            || (direct == EdgeBrowseParameter.DIRECTION_INVERSE
                && reference.getIsForward() == true)) {
          code = EdgeStatusCode.STATUS_VIEW_DIRECTION_NOT_MATCH;
          callErrorMessageCB(msg, code, retIdx);
        } else if (browseResponse.getResults()[0].getReferences()[referIdx]
            .getTypeDefinition() != reference.getTypeDefinition()) {
          code = EdgeStatusCode.STATUS_VIEW_REFERENCE_DATA_NOT_MATCH;
          callErrorMessageCB(msg, code, retIdx);
        }
        referIdx++;
      }
      retIdx++;
    }
  }

  private void callErrorMessageCB(EdgeMessage msg, EdgeStatusCode code, int msgIdx) {
    ErrorHandler.getInstance().addErrorMessage(getEndpoint(msg, msgIdx),
        new EdgeResult.Builder(code).build(),
        new EdgeVersatility.Builder(code.getDescription()).build(), getRequestId(msg, msgIdx));
  }

  private void callResponseMessage(EdgeMessage msg, List<EdgeBrowseResult> browseRet, int msgIdx) {
    EdgeEndpointInfo epInfo =
        new EdgeEndpointInfo.Builder(msg.getEdgeEndpointInfo().getEndpointUri())
            .setFuture(msg.getEdgeEndpointInfo().getFuture()).build();
    EdgeMessage inputData = new EdgeMessage.Builder(epInfo).setBrowseResult(browseRet)
        .setResponses(newArrayList(
            new EdgeResponse.Builder(getEndpoint(msg, msgIdx), getRequestId(msg, msgIdx)).build()))
        .setMessageType(EdgeMessageType.BROWSE_RESPONSE).build();
    ProtocolManager.getProtocolManagerInstance().getRecvDispatcher().putQ(inputData);
  }
}
