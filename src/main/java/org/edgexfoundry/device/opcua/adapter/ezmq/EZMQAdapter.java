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

package org.edgexfoundry.device.opcua.adapter.ezmq;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.edge.protocol.opcua.api.client.EdgeResponse;
import org.edge.protocol.opcua.api.common.EdgeMessage;
import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.core.Reading;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;

public class EZMQAdapter {
  private final static EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(EZMQAdapter.class);
  private static EZMQAdapter singleton = null;
  private static final int mPort = 5562;
  private static Publisher pub = null;

  private EZMQAdapter() {
    pub = Publisher.getInstance();
    pub.startPublisher(mPort);
  }

  /**
   * get EMFAdapter instance
   * 
   * @return EMFAdapter instance based singleton
   */
  public synchronized static EZMQAdapter getInstance() {
    if (singleton == null) {
      singleton = new EZMQAdapter();
    }
    return singleton;
  }

  /**
   * publish data related EdgeMessage.
   * 
   */
  public void publish(EdgeMessage data) {
    if (null == data) {
      return;
    }

    for (EdgeResponse res : data.getResponses()) {
      logger.info("onMonitoredMessage = {}", res.getMessage().getValue());
    }

    try {
      pub.publishEvent(getEvent(data));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * get Event Object. Name is set as provider key called ValueAlias. And Device is set as
   * endpointUri of opcua server. And others will be set as default value.
   * 
   * @fn Event getEvent(EdgeMessage data)
   */
  private Event getEvent(EdgeMessage data) {
    if (null == data) {
      return null;
    }

    List<Reading> readings = null;
    readings = new ArrayList<Reading>();
    Reading reading = new Reading();
    reading.setName(data.getResponses().get(0).getEdgeNodeInfo().getValueAlias());
    reading.setValue(data.getResponses().get(0).getMessage().getValue().toString());
    reading.setCreated(0);
    reading.setDevice(data.getEdgeEndpointInfo().getEndpointUri());
    reading.setModified(0);
    reading.setId("id1");
    reading.setOrigin(new Timestamp(System.currentTimeMillis()).getTime());
    reading.setPushed(new Timestamp(System.currentTimeMillis()).getTime());

    readings.add(reading);

    Event event = new Event(data.getEdgeEndpointInfo().getEndpointUri(), readings);
    event.setCreated(0);
    event.setModified(0);
    event.setId("id1");
    event.markPushed(new Timestamp(System.currentTimeMillis()).getTime());
    event.setOrigin(new Timestamp(System.currentTimeMillis()).getTime());

    return event;
  }
}
