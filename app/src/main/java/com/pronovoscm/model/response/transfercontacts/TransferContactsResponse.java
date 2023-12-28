package com.pronovoscm.model.response.transfercontacts;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransferContactsResponse {


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
        @SerializedName("contacts")
        private List<Contacts> contacts;

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

        public List<Contacts> getContacts() {
            return contacts;
        }

        public void setContacts(List<Contacts> contacts) {
            this.contacts = contacts;
        }
    }


}
