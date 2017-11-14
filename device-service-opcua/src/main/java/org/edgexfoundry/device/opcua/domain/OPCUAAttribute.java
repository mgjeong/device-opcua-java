/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @microservice: device-service-opcua
 * @author: Tyler Cox, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.device.opcua.domain;

import com.google.gson.Gson;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;

public class OPCUAAttribute {

  private static final EdgeXLogger logger =
	  EdgeXLoggerFactory.getEdgeXLogger(OPCUAAttribute.class);

  // Replace these attributes with the OPCUA
  // specific metadata needed by the OPCUA Driver
  private String providerKey;
	private String Instance;
	private String Type;

  public OPCUAAttribute(Object attributes) {
    try {
      Gson gson = new Gson();
      String jsonString = gson.toJson(attributes);
      OPCUAAttribute thisObject = gson.fromJson(jsonString, this.getClass());
      
			this.setInstance(thisObject.getInstance());
			this.setType(thisObject.getType());
			this.setProviderKey(thisObject.getProviderKey());
    } catch (Exception e) {
      logger.error("Cannot Construct OPCUAAttribute: " + e.getMessage());
    }
  }

  
	public String getInstance()
	{
		return Instance;
	}
	public String getType()
	{
		return Type;
	}

  public String getProviderKey() {
    return providerKey;
    }
    
	public void setInstance(String Instance)
	{
		this.Instance = Instance;
	}
	public void setType(String Type)
	{
		this.Type = Type;
	}

  public void setProviderKey(String deviceName) {
    this.providerKey = deviceName;
    }
}

