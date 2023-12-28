package com.pronovoscm.model.response.rfi.replies;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RfiRepliesData {
    @SerializedName("rfi_replies")
    @Expose
    private List<RfiReply> rfiReplies = null;
    @SerializedName("responseCode")
    @Expose
    private Integer responseCode;
    @SerializedName("responseMsg")
    @Expose
    private String responseMsg;

    public List<RfiReply> getRfiReplies() {
        return rfiReplies;
    }

    public void setRfiReplies(List<RfiReply> rfiReplies) {
        this.rfiReplies = rfiReplies;
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
