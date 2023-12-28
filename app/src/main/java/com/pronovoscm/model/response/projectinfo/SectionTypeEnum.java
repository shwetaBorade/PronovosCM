package com.pronovoscm.model.response.projectinfo;

public enum SectionTypeEnum {
    NORMAL_TEXT_FIELD(0), PHONE(1), ADDRESS(2), CAMERA_LINK(3);

    private final int value;

    SectionTypeEnum(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
