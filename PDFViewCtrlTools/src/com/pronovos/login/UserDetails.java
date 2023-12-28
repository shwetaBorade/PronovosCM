package com.pronovos.login;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserDetails {
    @SerializedName("users_id")
    private int users_id;
    @SerializedName("tenant_id")
    private int tenantId;
    @SerializedName("responseMsg")
    private String responsemsg;
    @SerializedName("responseCode")
    private int responsecode;
    @SerializedName("permissions")
    private List<UserPermissions> permissions;
    @SerializedName("authToken")
    private String authtoken;
    @SerializedName("region_id")
    private int regionId;
    @SerializedName("lastname")
    private String lastname;
    @SerializedName("firstname")
    private String firstname;

    public int getUsers_id() {
        return users_id;
    }

    public void setUsers_id(int users_id) {
        this.users_id = users_id;
    }

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

    public List<UserPermissions> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<UserPermissions> permissions) {
        this.permissions = permissions;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }
}
