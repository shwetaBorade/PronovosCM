package com.pronovoscm.model.request.transferdetails;

import com.google.gson.annotations.SerializedName;

public class TransferDetailRequest {

    @SerializedName("transfer_id")
    private int transferId;

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }
}
