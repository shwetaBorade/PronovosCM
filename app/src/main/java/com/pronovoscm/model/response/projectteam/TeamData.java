package com.pronovoscm.model.response.projectteam;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TeamData {
    @SerializedName("team")
    @Expose
    private List<Team> team = null;
    @SerializedName("responseCode")
    @Expose
    private Integer responseCode;
    @SerializedName("responseMsg")
    @Expose
    private String responseMsg;

    public List<Team> getTeam() {

        Collections.sort(team, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {
                if (o1.getOrder() != null && o2.getOrder() != null)
                    return o1.getOrder().compareTo(o2.getOrder());
                else return 0;
            }
        });

        return team;
    }

    public void setTeam(List<Team> team) {
        this.team = team;
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

}
