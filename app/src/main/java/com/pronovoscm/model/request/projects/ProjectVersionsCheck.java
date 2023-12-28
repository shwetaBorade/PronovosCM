package com.pronovoscm.model.request.projects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProjectVersionsCheck {


    @SerializedName("platform")
    @Expose
    private String platform;
    @SerializedName("version")
    @Expose
    private String version;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
