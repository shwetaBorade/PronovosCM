package com.pronovoscm.model.response.projectinfo;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class Section {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("order")
    @Expose
    private Integer order;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("data")
    @Expose
    private List<SectionData> data = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public List<SectionData> getData() {
        if (data != null)
            Collections.sort(data, (o1, o2) -> {
                Log.d("SectionData", "compare: o1.getOrder() " + o1.getOrder() + "   " + o2.getOrder());
                return o1.getOrder().compareTo(o2.getOrder());
            });
        return data;
    }

    public void setData(List<SectionData> data) {
        if (data != null)
            Collections.sort(data, (o1, o2) -> {
                Log.d("SectionData", "compare: o1.getOrder() " + o1.getOrder() + "   " + o2.getOrder());
                return o1.getOrder().compareTo(o2.getOrder());
            });
        this.data = data;
    }

    @Override
    public String toString() {
        return "\n Section{" +
                "name='" + name + '\'' +
                ", order=" + order +
                ", active=" + active +
                //  ", data=" + data +
                '}';
    }
}
