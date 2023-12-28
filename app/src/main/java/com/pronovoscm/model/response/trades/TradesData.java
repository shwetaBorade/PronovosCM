package com.pronovoscm.model.response.trades;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TradesData {
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("trades")
    private List<Trades> trades;

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

    public List<Trades> getTrades() {
        return trades;
    }

    public void setTrades(List<Trades> trades) {
        this.trades = trades;
    }
}
