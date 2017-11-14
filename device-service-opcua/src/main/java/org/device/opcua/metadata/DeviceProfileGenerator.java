package org.device.opcua.metadata;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.device.opcua.DataDefaultValue;
import org.edgexfoundry.domain.meta.Command;
import org.edgexfoundry.domain.meta.DeviceObject;
import org.edgexfoundry.domain.meta.DeviceProfile;
import org.edgexfoundry.domain.meta.ProfileResource;

public class DeviceProfileGenerator {

    private DeviceProfileGenerator() {
    }

    public static DeviceProfile NewtDeviceProfile(String deviceInfoKey) {
        DeviceProfile deviceProfile = null;
        deviceProfile = new DeviceProfile();
        deviceProfile.setOrigin(new Timestamp(System.currentTimeMillis()).getTime());
        deviceProfile.setCreated(new Timestamp(System.currentTimeMillis()).getTime());
        deviceProfile.setName(deviceInfoKey);
        deviceProfile.setManufacturer(DataDefaultValue.MANUFACTURER.getValue());
        deviceProfile.setModel(DataDefaultValue.MODEL.getValue());
        deviceProfile.setDescription(DataDefaultValue.DESCRIPTION_DEVICEPROFILE.getValue());
        deviceProfile.setObjects(DataDefaultValue.OBJ.getValue());
        String[] labels = {DataDefaultValue.LABEL1.getValue(), DataDefaultValue.LABEL2.getValue()};
        deviceProfile.setLabels(labels);

        List<DeviceObject> deviceObjectList = new ArrayList<DeviceObject>();
        deviceObjectList.add(DeviceObjectGenerator.newDeviceObject(deviceInfoKey));
        deviceProfile.setDeviceResources(deviceObjectList);

        List<ProfileResource> profileResourceList = new ArrayList<ProfileResource>();
        profileResourceList.add(ProfileResourceGenerator.newProfileResource(deviceInfoKey));
        deviceProfile.setResources(profileResourceList);

        List<Command> commandList = new ArrayList<Command>();
        commandList.add(CommandGenerator.newCommand(deviceInfoKey));
        deviceProfile.setCommands(commandList);

        return deviceProfile;
    }

}