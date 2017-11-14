package org.device.opcua.metadata;

import org.device.opcua.DataDefaultValue;
import org.edgexfoundry.controller.AddressableClient;
import org.edgexfoundry.controller.DeviceProfileClient;
import org.edgexfoundry.controller.DeviceServiceClient;
import org.edgexfoundry.domain.meta.AdminState;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.OperatingState;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

@ImportResource("spring-config.xml")
@Component
public class DeviceGenerator {
    private final static EdgeXLogger logger = EdgeXLoggerFactory
            .getEdgeXLogger(DeviceGenerator.class);

    // service name
    @Value("${service.name}")
    private static String serviceName;

    @Autowired
    private DeviceServiceClient deviceServiceClient;

    @Autowired
    private AddressableClient addressableClient;

    @Autowired
    private DeviceProfileClient deviceProfileClient;

    private DeviceGenerator() {
    }

    public Device newDevice(String deviceInfoKey) {
        Device device = new Device();
        device.setAdminState(AdminState.unlocked);
        device.setDescription(DataDefaultValue.DESCRIPTION_DEVICE.getValue());
        device.setLastConnected(DataDefaultValue.DEFAULT_LAST_CONNECTED);
        device.setLastReported(DataDefaultValue.DEFAULT_LAST_REPORTED);
        device.setLocation(DataDefaultValue.LOCATION.getValue());
        device.setName(deviceInfoKey);
        device.setOperatingState(OperatingState.enabled);
        device.setOrigin(DataDefaultValue.DEFAULT_ORIGIN);
        device.setProfile(deviceProfileClient.deviceProfileForName(deviceInfoKey));
        String[] labels = { DataDefaultValue.LABEL1.getValue(),
                DataDefaultValue.LABEL2.getValue() };
        device.setLabels(labels);

        try {
            device.setAddressable(addressableClient
                    .addressableForName(AddressableGenerator.getAddressable().getName()));
        } catch (Exception e) {
            logger.debug("Could not set Addressable for device msg: " + e.getMessage());
            return null;
        }
        try {
            serviceName = "device-service-opcua";
            device.setService(deviceServiceClient.deviceServiceForName(serviceName));
        } catch (Exception e) {
            logger.error("Could not get deviceService by name msg: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return device;
    }

}