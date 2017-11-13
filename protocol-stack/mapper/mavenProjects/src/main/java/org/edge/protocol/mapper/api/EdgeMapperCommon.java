package org.edge.protocol.mapper.api;

public enum EdgeMapperCommon {
    DEVICEPROFILE_NAME(0, ""),
    DEVICEPROFILE_MANUFACTURER(1, ""),
    DEVICEPROFILE_MODEL(2, ""),

    DEVICEOBJECT_NAME(5, ""),
    DEVICEOBJECT_TAG(6, ""),
    DEVICEOBJECT_DESCRIPTION(7, ""),
    DEVICEOBJECT_ATTRIBUTE_DATATYPE(8, ""),

    PROPERTYVALUE_TYPE(10, ""),
    PROPERTYVALUE_READWRITE(11, ""),
    PROPERTYVALUE_MIN(12, ""),
    PROPERTYVALUE_MAX(13, ""),
    PROPERTYVALUE_DEFAULTVALUE(14, ""),
    PROPERTYVALUE_SIZE(15, ""),
    PROPERTYVALUE_PRECISION(16, ""),
    PROPERTYVALUE_LSB(17, ""),
    PROPERTYVALUE_MASK(18, ""),
    PROPERTYVALUE_SHIFT(19, ""),
    PROPERTYVALUE_SCALE(20, ""),
    PROPERTYVALUE_OFFSET(21, ""),
    PROPERTYVALUE_BASE(22, ""),
    PROPERTYVALUE_ASSERTION(23, ""),
    UNITS_TYPES(24, ""),
    UNITS_READWRITE(25, ""),
    UNITS_DEFAULTVALUE(26, ""),

    PROFILERESOURCE_NAME(30, ""),
    RESOURCEOPERATION_OPERATION(31, ""),
    RESOURCEOPERATION_OBJECT(32, ""),
    RESOURCEOPERATION_PROPERTY(33, ""),
    RESOURCEOPERATION_PARAMETER(34, "");

    private int code;
    private String description;

    private EdgeMapperCommon(int code, String description) {
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