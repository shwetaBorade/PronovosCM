package com.pronovoscm.model.response.projectinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SectionData {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("order")
    @Expose
    private Integer order;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "\n SectionData{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", type=" + type +
                ", order=" + order +
                '}';
    }
}
