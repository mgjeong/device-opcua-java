package org.edgexfoundry.device.opcua.adapter;

public enum OPCUAMessageKeyIdentifier {
	OPERATION(0, "operation", "command operation"),
	INPUT_ARGUMENT(1, "input_argument", "input value in put command operation"), 
	RESULT(2, "result", "command result value"),
	VALUE_DESCRIPTOR(3, "value_descriptor", "command valuedescriptor name"),
	SAMPLING_INTERVAL(4, "sampling_interval", "sampling interval"),
	APPLICATION_NAME(5, "application_name", "application name for server"),
	APPLICATION_URI(6, "application_uri", "application uri for server");

	private int code;
	private String value;
	private String description;

	private OPCUAMessageKeyIdentifier(int code, String value, String description) {
		this.code = code;
		this.value = value;
		this.description = description;
	}

	public int getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}
}
