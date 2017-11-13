/******************************************************************
 *
 * Copyright 2017 Samsung Electronics All Rights Reserved.
 *
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************/

package org.edge.protocol.opcua.api.common;

public enum EdgeStatusCode {
  STATUS_OK(0, ""), 
  STATUS_ERROR(1, "request error"), 
  STATUS_ALREADY_INIT(2, ""),
  STATUS_CONNECTED(3, ""),
  STATUS_DISCONNECTED(4, ""),
  STATUS_SERVER_STARTED(5, ""),
  STATUS_CLIENT_STARTED(6, ""),
  STATUS_STOP_SERVER(7, ""),
  STATUS_STOP_CLIENT(8, ""),
  STATUS_SERVICE_RESULT_BAD(9, "service result is not good"),
  
  STATUS_ENQUEUE_ERROR(20, ""),
  STATUS_READ_LESS_RESPONSE(26, "Return fewer Results than the number of nodes specified in the nodesToRead parameter."),

  STATUS_NOT_REGISTER(30, ""), 
  STATUS_INAVAILD_PROVIDER(31, ""), 
  STATUS_INTERNAL_ERROR(32, ""), 
  STATUS_PARAM_INVALID(33, ""), 
  STATUS_NOT_ACCESS_PERMISSION(34, ""),
  STATUS_NOT_START_SERVER (35, "server is not started"),

  STATUS_WRITE_LESS_RESPONSE(50, "contains 1 less record than all responses"),
  STATUS_WRITE_EMPTY_RESULT(51, "service result is good, but an empty result is returned"),
  STATUS_WRITE_TOO_MANY_RESPONSE(52, "result is more than requests"), 

  STATUS_VIEW_NOIDID_UNKNOWN_ALL_RESULTS(60, "all of results has Bad_NodeIdUnknown error"),
  STATUS_VIEW_CONTINUATION_DATA_EMPTY(61, "continuation is availale. but there is no data(empty)"),
  STATUS_VIEW_REFERENCE_DATA_INVALID(62, "reference is not availale"),
  STATUS_VIEW_RESULT_STATUS_CODE_BAD(63, "status code of the result has bad code"),
  STATUS_VIEW_CONTINUATION_POINT_REUSED(64, "continuationpoint is re-used"),
  STATUS_VIEW_REFERENCE_DIRECTION_WRONG(65, "result contains data whose direction does not match the search criteria"),
  STATUS_VIEW_DIRECTION_NOT_MATCH(66, "result direction is different with browse request"),
  STATUS_VIEW_REFERENCE_DATA_NOT_MATCH(67, "first result contains data that is of a type that does not match"),
  STATUS_VIEW_NOTINCLUDE_NODECLASS(68, "nodeclass is not include in browse description nodeclass mask"),
  STATUS_VIEW_BROWSERESULT_EMPTY(69, "browse result is empty"),
  STATUS_VIEW_BROWSEREQUEST_SIZEOVER(70, "browse request's size is over maximum size"),
  
  STATUS_SUB_PUB_INTERVAL_DIFFERENCE(100, "check the revised value and if it is different to the requested"), 
  STATUS_SUB_LIFETIME_DIFFERENCE(101, "check the revised value and if it is different to the requested"),
  STATUS_SUB_MAX_KEEPALIVE_DIFFERENCE(102, "check the revised value and if it is different to the requested"), 
  STATUS_SUB_NOTIFICATION_TIME_INVALID(103, "publishTime to a time in the future"), 
  STATUS_SUB_MAX_NOTIFICATION_NOT_MATCH(104, "max notification per publish is not matched"),
  STATUS_SUB_ID_INVALID(105, "The subscription id is not valid"),
  STATUS_SUB_NOTHING_TO_DO(106, "There was nothing to do because the client passed a list of operations with no elements"),
  STATUS_SUB_TOO_MANY_OPERATION(107, "The server has reached the maximum number of queued publish requests"),
  STATUS_SUB_LIB_INTERNAL_ERROR(108, "An internal error occurred as a result of a programming or configuration error"),
  STATUS_SUB_SEQUENCE_NUMBER_UNKNOWN(109, "The sequence number is unknown to the server"),
  STATUS_SUB_SEQUENCE_NUMBER_INVALID(110, "The sequence number is not valid"),
  STATUS_SUB_NO_SUBSCRIPTION(111, "there is no subscription"),
  STATUS_SUB_TOO_MANY_PUBLISH_REQUESTS(113, "there aris no subscription"),
  STATUS_SUB_DATA_LOSS(114, "subscription publish data loss"),
  STATUS_SUB_SETPUBLISH_EMPTY_RESULT(130, "service result is good, but an empty result is returned"),
  STATUS_SUB_DELETE_ITEM_INCREASE(140, "service result is good, but the length of result is increase"),
  STATUS_SUB_DELETE_ITEM_DECREASE(141, "service result is good, but the length of result is decrease"),
  
  STAUTS_MONITOR_ALL_ITEMS_ERROR(150, "all items are set to same error codes"),
  STATUS_MONITOR_SAMPLING_INVERVAL_INVALID(151, "sampling interval is invalid"),
  STATUS_MONITOR_QUEUE_SIZE_INVALID(152, "queue size is invalid"),

  STATUS_NOT_SUPPROT(300, "this function is not suppored");

  private int code;
  private String description;

  public static String UNKNOWN_STATUS_CODE = "Unknwon_StatusCode";
  public static String CONTINUATIONPOINT_NULL = "ContinuationPoint is null";
  public static String CONTINUATIONPOINT_EMPTY = "ContinuationPoint is empty";
  public static String CONTINUATIONPOINT_LONG = "ContinuationPoint is very long";
  public static String BROWSENAME_NULL = "BrowseName is null";
  public static String BROWSENAME_EMPTY = "BrowseName is empty";
  public static String BROWSENAME_LONG = "BrowseName is very long";
  public static String DISPLAYNAME_NULL = "DisplayName is null";
  public static String DISPLAYNAME_EMPTY = "DisplayName is empty";
  public static String DISPLAYNAME_LONG = "DisplayName is very long";
  public static String NODECLASS_NULL = "NodeClass is null";
  public static String NODECLASS_INVALID = "NodeClass has invalid value";
  public static String NODEID_NULL = "NodeId is null";
  public static String NODEID_SERVERINDEX = "NodeId's server index is not zero";
  public static String REFERENCETYPEID_NULL = "ReferenceTypeId is null";

  private EdgeStatusCode(int code, String description) {
    this.code = code;
    this.description = description;
  }

  public int getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }
}
