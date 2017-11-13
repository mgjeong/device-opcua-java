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

package org.edge.protocol.opcua.api.client;

import org.eclipse.milo.opcua.stack.core.types.builtin.DiagnosticInfo;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;

public class EdgeDiagnosticInfo {
  private int symbolicId;
  private int localizedText;
  private String additionalInfo;
  private StatusCode innerStatusCode;
  private DiagnosticInfo innerDiagnostics;
  private String msg;

  public static class Builder {
    private final int symbolicId;
    private final int localizedText;
    private final String additionalInfo;
    private final StatusCode innerStatusCode;
    private final DiagnosticInfo innerDiagnostics;
    private String msg;

    public Builder() {
      this.symbolicId = 0;
      this.localizedText = 0;
      this.additionalInfo = null;
      this.innerStatusCode = null;
      this.innerDiagnostics = null;
    }

    /**
     * @fn Builder(int symbolicId, int localizedText, String additionalInfo, StatusCode
     *     innerStatusCode, DiagnosticInfo innerDiagnostics)
     * @brief set builder
     * @param [in] symbolicId symbolic id
     * @param [in] localizedText localized Text
     * @param [in] additionalInfo additional Information
     * @param [in] innerStatusCode innerStatusCode
     * @param [in] innerDiagnostics innerDiagnostics
     * @return this
     */
    public Builder(int symbolicId, int localizedText, String additionalInfo,
        StatusCode innerStatusCode, DiagnosticInfo innerDiagnostics) {
      this.symbolicId = symbolicId;
      this.localizedText = localizedText;
      this.additionalInfo = additionalInfo;
      this.innerStatusCode = innerStatusCode;
      this.innerDiagnostics = innerDiagnostics;
    }

    /**
     * @fn Builder setMessage(String msg)
     * @brief set string message
     * @param [in] msg message
     * @return this
     */
    public Builder setMessage(String msg) {
      this.msg = msg;
      return this;
    }

    /**
     * @fn EdgeDiagnosticInfo build()
     * @brief create EdgeDiagnosticInfo instance (builder)
     * @return EdgeDiagnosticInfo instance
     */
    public EdgeDiagnosticInfo build() {
      return new EdgeDiagnosticInfo(this);
    }
  }

  /**
   * @fn EdgeDiagnosticInfo(Builder builder)
   * @brief constructor
   * @param [in] builder EdgeDiagnosticInfo Builder
   */
  private EdgeDiagnosticInfo(Builder builder) {
    symbolicId = builder.symbolicId;
    localizedText = builder.localizedText;
    additionalInfo = builder.additionalInfo;
    innerStatusCode = builder.innerStatusCode;
    innerDiagnostics = builder.innerDiagnostics;

    msg = builder.msg;
  }

  /**
   * @fn int getSymbolicId()
   * @brief get symbolic Id
   * @return symbolicId
   */
  public int getSymbolicId() {
    return symbolicId;
  }

  /**
   * @fn int getLocalizedText()
   * @brief get localized text
   * @return localizedText
   */
  public int getLocalizedText() {
    return localizedText;
  }

  /**
   * @fn String getAdditionalInfo()
   * @brief get additional information
   * @return additionalInfo
   */
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  /**
   * @fn StatusCode getInnerStatusCode()
   * @brief get inner StatusCode
   * @return innerStatusCode
   */
  public StatusCode getInnerStatusCode() {
    return innerStatusCode;
  }

  /**
   * @fn DiagnosticInfo gettInnerDiagnosticInfo()
   * @brief get inner diagnostics
   * @return innerDiagnostics
   */
  public DiagnosticInfo gettInnerDiagnosticInfo() {
    return innerDiagnostics;
  }

  /**
   * @fn String getMsg()
   * @brief get detailed message
   * @return msg
   */
  public String getMsg() {
    return msg;
  }
}
