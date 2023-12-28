package com.pronovoscm.model.response.signedurl;

import com.google.gson.annotations.SerializedName;

public class Formattributes {
    @SerializedName("X-Amz-Signature")
    private String XAmzSignature;
    @SerializedName("Policy")
    private String Policy;
    @SerializedName("X-Amz-Date")
    private String XAmzDate;
    @SerializedName("X-Amz-Algorithm")
    private String XAmzAlgorithm;
    @SerializedName("X-Amz-Credential")
    private String XAmzCredential;
    @SerializedName("key")
    private String key;

    public String getXAmzSignature() {
        return XAmzSignature;
    }

    public void setXAmzSignature(String XAmzSignature) {
        this.XAmzSignature = XAmzSignature;
    }

    public String getPolicy() {
        return Policy;
    }

    public void setPolicy(String policy) {
        Policy = policy;
    }

    public String getXAmzDate() {
        return XAmzDate;
    }

    public void setXAmzDate(String XAmzDate) {
        this.XAmzDate = XAmzDate;
    }

    public String getXAmzAlgorithm() {
        return XAmzAlgorithm;
    }

    public void setXAmzAlgorithm(String XAmzAlgorithm) {
        this.XAmzAlgorithm = XAmzAlgorithm;
    }

    public String getXAmzCredential() {
        return XAmzCredential;
    }

    public void setXAmzCredential(String XAmzCredential) {
        this.XAmzCredential = XAmzCredential;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    @Override
    public String toString() {
        return "Formattributes{" +
                // "Policy='" + Policy + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
