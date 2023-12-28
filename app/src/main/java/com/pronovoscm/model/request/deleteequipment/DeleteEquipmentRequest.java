package com.pronovoscm.model.request.deleteequipment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeleteEquipmentRequest {


    @SerializedName("transfer_equipment_id")
    private int transferEquipmentId;
    @SerializedName("transfer_id")
    private int transferId;

    public int getTransferEquipmentId() {
        return transferEquipmentId;
    }

    public void setTransferEquipmentId(int transferEquipmentId) {
        this.transferEquipmentId = transferEquipmentId;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }
}
