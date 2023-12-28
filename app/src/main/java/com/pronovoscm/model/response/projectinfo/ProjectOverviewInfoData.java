package com.pronovoscm.model.response.projectinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProjectOverviewInfoData {
    @SerializedName("sections")
    @Expose
    private List<Section> sections = null;
    @SerializedName("responseCode")
    @Expose
    private Integer responseCode;
    @SerializedName("responseMsg")
    @Expose
    private String responseMsg;

    public List<Section> getSections() {
        if (sections != null)
            Collections.sort(sections, new Comparator<Section>() {
                @Override
                public int compare(Section o1, Section o2) {
                    return o1.getOrder().compareTo(o2.getOrder());
                }
            });
        return sections;


    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    @Override
    public String toString() {
        return "ProjectOverviewInfoData{" +
                "sections=" + sections +
                ", responseCode=" + responseCode +
                ", responseMsg='" + responseMsg + '\'' +
                '}';
    }
}
