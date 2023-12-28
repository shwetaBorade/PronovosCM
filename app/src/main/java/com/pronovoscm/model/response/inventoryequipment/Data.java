package com.pronovoscm.model.response.inventoryequipment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("equipments")
    private List<Equipments> equipments;

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

    public List<Equipments> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<Equipments> equipments) {
        this.equipments = equipments;
    }
}
