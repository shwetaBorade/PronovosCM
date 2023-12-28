package com.pronovoscm.model;

public enum UnloadingEnum {

    SELECT("Select One"),
    FORKLIFT("Forklift"),
    CRANE("Crane"),
    OTHER("Other");
    private final String name;

    private UnloadingEnum(String s) {
        name = s;
    }


    @Override
    public String toString() {
        return this.name;
    }
}
