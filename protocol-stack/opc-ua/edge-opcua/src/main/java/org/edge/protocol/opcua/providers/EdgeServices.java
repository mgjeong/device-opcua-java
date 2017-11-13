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

package org.edge.protocol.opcua.providers;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.edge.protocol.opcua.api.common.EdgeNodeInfo;
import org.edge.protocol.opcua.api.common.EdgeOpcUaCommon;
import org.edge.protocol.opcua.api.common.EdgeResult;
import org.edge.protocol.opcua.api.common.EdgeStatusCode;
import org.edge.protocol.opcua.queue.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeServices {
  private final static Logger logger = LoggerFactory.getLogger(EdgeServices.class);

  private EdgeServices() {}

  private static final Map<String, EdgeAttributeProvider> attributeProviders =
      new ConcurrentHashMap<String, EdgeAttributeProvider>();
  private static final Map<String, EdgeMethodProvider> methodProviders =
      new ConcurrentHashMap<String, EdgeMethodProvider>();
  private static final Map<String, EdgeViewProvider> viewProviders =
      new ConcurrentHashMap<String, EdgeViewProvider>();
  public static final String DEFAULT_PROVIDER_NAME =
      EdgeOpcUaCommon.WELL_KNOWN_SERVER_NODE.getValue();

  /**
   * @fn void registerAttributeProvider(String name, EdgeAttributeProvider p)
   * @brief register new provider related attribute type of opcua.
   * @prarm [in] name provider key value
   * @param [in] p attribute type instance base on EdgeAttributeProvider
   * @return void
   */
  public static void registerAttributeProvider(String name, EdgeAttributeProvider p) {
    attributeProviders.put(name, p);
  }

  /**
   * @fn void registerMethodProvider(String name, EdgeMethodProvider methodProvider)
   * @brief register new provider related method type of opcua.
   * @prarm [in] name provider key value
   * @param [in] methodProvider method type instance base on EdgeMethodProvider
   * @return void
   */
  public static void registerMethodProvider(String name, EdgeMethodProvider methodProvider) {
    methodProviders.put(name, methodProvider);
  }

  /**
   * @fn void registerViewProvider(String name, EdgeViewProvider viewProvider)
   * @brief register new provider related method type of opcua.
   * @prarm [in] name provider key value
   * @param [in] viewProvider view type instance base on EdgeViewProvider
   * @return void
   */
  public static void registerViewProvider(String name, EdgeViewProvider viewProvider) {
    viewProviders.put(name, viewProvider);
  }

  /**
   * @fn void removeAttributeProvider()
   * @brief remove all providers related attribute type of opcua.
   * @return void
   */
  public static void removeAttributeProvider() {
    attributeProviders.clear();
  }

  /**
   * @fn void removeMethodProvider()
   * @brief remove all providers related method type of opcua.
   * @return void
   */
  public static void removeMethodProvider() {
    methodProviders.clear();
  }

  /**
   * @fn EdgeMethodProvider getMethodProvider(String name)
   * @brief get provider related method type of opcua.
   * @prarm [in] name provider key value
   * @return method type instance base on EdgeMethodProvider
   */
  public static EdgeMethodProvider getMethodProvider(String name) {
    EdgeMethodProvider methodProvider = methodProviders.get(name);
    if (null == methodProvider) {
      ErrorHandler.getInstance().addErrorMessage(new EdgeNodeInfo.Builder().build(),
          new EdgeResult.Builder(EdgeStatusCode.STATUS_INAVAILD_PROVIDER).build(),
          EdgeOpcUaCommon.DEFAULT_REQUEST_ID);
      throw new IllegalArgumentException("no method provider registered with name: " + name);
    }
    return methodProvider;
  }

  /**
   * @fn EdgeAttributeProvider getAttributeProvider(String name)
   * @brief get provider related attribute type of opcua.
   * @prarm [in] name provider key value
   * @return attribute type instance base on getAttributeProvider
   */
  public static EdgeAttributeProvider getAttributeProvider(String name) {
    EdgeAttributeProvider attributeProvider = attributeProviders.get(name);
    getAttributeProviderKeyList();
    if (null == attributeProvider) {
      logger.error("no provider registered with name : " + name);
      ErrorHandler.getInstance().addErrorMessage(new EdgeNodeInfo.Builder().build(),
          new EdgeResult.Builder(EdgeStatusCode.STATUS_INAVAILD_PROVIDER).build(),
          EdgeOpcUaCommon.DEFAULT_REQUEST_ID);
      throw new IllegalArgumentException("no provider registered with name: " + name);
    }
    return attributeProvider;
  }

  /**
   * @fn EdgeViewProvider getViewProvider(String name)
   * @brief get provider related attribute type of opcua.
   * @prarm [in] name provider key value
   * @return view type instance base on getViewProvider
   */
  public static EdgeViewProvider getViewProvider(String name) {
    EdgeViewProvider viewProvider = viewProviders.get(name);
    if (null == viewProvider) {
      logger.error("no provider registered with name : " + name);
      ErrorHandler.getInstance().addErrorMessage(new EdgeNodeInfo.Builder().build(),
          new EdgeResult.Builder(EdgeStatusCode.STATUS_INAVAILD_PROVIDER).build(),
          EdgeOpcUaCommon.DEFAULT_REQUEST_ID);
      throw new IllegalArgumentException("no provider registered with name: " + name);
    }
    return viewProvider;
  }

  /**
   * @fn ArrayList<String> getAttributeProviderKeyList()
   * @brief get all provider key.
   * @return all provider key list related attribute type
   */
  public static ArrayList<String> getAttributeProviderKeyList() {
    ArrayList<String> attributeProviderKeys = new ArrayList<String>(attributeProviders.keySet());
    for (String key : attributeProviderKeys) {
      logger.debug("service provider name : " + key);
    }
    return attributeProviderKeys;
  }

  /**
   * @fn ArrayList<String> getMethodProviderKeyList()
   * @brief get all provider key.
   * @return all provider key list related method type
   */
  public static ArrayList<String> getMethodProviderKeyList() {
    ArrayList<String> methodProviderKeys = new ArrayList<String>(methodProviders.keySet());
    return methodProviderKeys;
  }
  
  /**
   * @fn ArrayList<String> getViewProviderKeyList()
   * @brief get all provider key.
   * @return all provider key list related view type
   */
  public static ArrayList<String> getViewProviderKeyList() {
    ArrayList<String> viewProviderKeys = new ArrayList<String>(viewProviders.keySet());
    return viewProviderKeys;
  }
}
