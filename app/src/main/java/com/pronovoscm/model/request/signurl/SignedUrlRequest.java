package com.pronovoscm.model.request.signurl;

import com.google.gson.annotations.SerializedName;

public class SignedUrlRequest {

    @SerializedName("expire_time")
    private String expire_time;

    @SerializedName("type")
    private String type;

    public SignedUrlRequest(String type) {
        this.type = type;
        this.expire_time="3600";
    }

    public String getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(String expire_time) {
        this.expire_time = expire_time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
