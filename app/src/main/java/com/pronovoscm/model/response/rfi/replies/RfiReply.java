package com.pronovoscm.model.response.rfi.replies;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RfiReply {
    @SerializedName("pj_rfi_replies_id")
    @Expose
    private Integer pjRfiRepliesId;
    @SerializedName("pj_rfi_id")
    @Expose
    private Integer pjRfiId;
    @SerializedName("users_id")
    @Expose
    private Integer usersId;
    @SerializedName("rfi_replies")
    @Expose
    private String rfiReplies;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("is_official_response")
    @Expose
    private Integer isOfficialResponse;

    public Integer getPjRfiRepliesId() {
        return pjRfiRepliesId;
    }

    public void setPjRfiRepliesId(Integer pjRfiRepliesId) {
        this.pjRfiRepliesId = pjRfiRepliesId;
    }

    public Integer getPjRfiId() {
        return pjRfiId;
    }

    public void setPjRfiId(Integer pjRfiId) {
        this.pjRfiId = pjRfiId;
    }

    public Integer getUsersId() {
        return usersId;
    }

    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }

    public String getRfiReplies() {
        return rfiReplies;
    }

    public void setRfiReplies(String rfiReplies) {
        this.rfiReplies = rfiReplies;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getIsOfficialResponse() {
        return isOfficialResponse;
    }

    public void setIsOfficialResponse(Integer isOfficialResponse) {
        this.isOfficialResponse = isOfficialResponse;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
