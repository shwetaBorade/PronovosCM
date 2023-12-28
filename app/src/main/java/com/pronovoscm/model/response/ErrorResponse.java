package com.pronovoscm.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Models an API error response.
 */
public class ErrorResponse {

    @SerializedName("data")
    private Data data;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public ErrorResponse() {
    }

    public ErrorResponse(String message) {
        this.message = message;
    }

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

    public static class Data {
        @SerializedName("responseMsg")
        private String responsemsg;
        @SerializedName("responseCode")
        private int responsecode;

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
    }
}