package com.pronovoscm.model.response.projectinfo;

import com.google.gson.annotations.SerializedName;

public class Info {
    @SerializedName("pj_schedule")
    private PjSchedule pjSchedule;
    @SerializedName("pj_site")
    private PjSite pjSite;
    @SerializedName("pj_info")
    private PjInfo pjInfo;

    public PjSchedule getPjSchedule() {
        return pjSchedule;
    }

    public void setPjSchedule(PjSchedule pjSchedule) {
        this.pjSchedule = pjSchedule;
    }

    public PjSite getPjSite() {
        return pjSite;
    }

    public void setPjSite(PjSite pjSite) {
        this.pjSite = pjSite;
    }

    public PjInfo getPjInfo() {
        return pjInfo;
    }

    public void setPjInfo(PjInfo pjInfo) {
        this.pjInfo = pjInfo;
    }
}
