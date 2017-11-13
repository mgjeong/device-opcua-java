package org.edge.protocol.mapper.api;

public enum EdgeResponseCode {
    OUT_OF_MEMORY(0x80030000L, "Not enough memory to complete the operation."),
    BAD_NOT_FOUND(0x803E0000L,
            "A requested item was not found or a search operation ended without success."),
    INTERNAL_ERROR(0x80020000L,
            "An internal error occurred as a result of a programming or configuration error."),
    TIMEOUT(0x800A0000L, "The operation timed out."),
    NOT_SUPPORT(0x803D0000L, "The requested operation is not supported."),
    NETWORK_DOWN(0x80840000L, "The request could not be sent because of a network interruption."),
    OK(200, "ok");

    private long value;
    private String description;

    private EdgeResponseCode(long value, String description) {
        this.value = value;
        this.description = description;
    }

    public long getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return this.toString();
    }
}
