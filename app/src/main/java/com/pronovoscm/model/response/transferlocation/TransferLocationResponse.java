package com.pronovoscm.model.response.transferlocation;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TransferLocationResponse {


    @SerializedName("data")
    private Data data;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public class Data {
        @SerializedName("responseMsg")
        private String responsemsg;
        @SerializedName("responseCode")
        private int responsecode;
        @SerializedName("locations")
        private List<Locations> locations;

        public String getResponsemsg() {
            return responsemsg;
        }

        public void setResponsemsg(String responsemsg) {
            this.responsemsg = responsemsg;
        }

        public int getResponsecode() {
            return responsecode;
        }

        public void setResponsecode(int responsecode) {
            this.responsecode = responsecode;
        }

        public List<Locations> getLocations() {
            return locations;
        }

        public void setLocations(List<Locations> locations) {
            this.locations = locations;
        }
    }

    public class Locations implements Serializable {
        @SerializedName("project_name")
        private String projectName;
        @SerializedName("address")
        private String address;
        @SerializedName("pj_projects_id")
        private int pjProjectsId;

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getPjProjectsId() {
            return pjProjectsId;
        }

        public void setPjProjectsId(int pjProjectsId) {
            this.pjProjectsId = pjProjectsId;
        }
    }
}
