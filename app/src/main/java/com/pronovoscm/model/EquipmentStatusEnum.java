package com.pronovoscm.model;

public enum EquipmentStatusEnum {

    ACTIVE("Active"),
    INACTIVE("Inactive");
    private final String name;

    private EquipmentStatusEnum(String s) {
        name = s;
    }


    @Override
    public String toString() {
        return this.name;
    }
}
