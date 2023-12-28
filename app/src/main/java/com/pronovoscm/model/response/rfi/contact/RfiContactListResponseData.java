package com.pronovoscm.model.response.rfi.contact;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RfiContactListResponseData {

    @SerializedName("rfi_contacts")
    @Expose
    private List<RfiContact> rfiContacts = null;
    @SerializedName("responseCode")
    @Expose
    private Integer responseCode;
    @SerializedName("responseMsg")
    @Expose
    private String responseMsg;

    public List<RfiContact> getRfiContacts() {
        return rfiContacts;
    }

    public void setRfiContacts(List<RfiContact> rfiContacts) {
        this.rfiContacts = rfiContacts;
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
