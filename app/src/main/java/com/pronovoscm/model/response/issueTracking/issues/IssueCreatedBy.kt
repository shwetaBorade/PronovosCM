package com.pronovoscm.model.response.issueTracking.issues

import com.google.gson.annotations.SerializedName

data class IssueCreatedBy(
        @SerializedName("users_id")
        val usersId: Long,
        @SerializedName("name")
        val name: String,
        @SerializedName("last_name")
        val lastName: String,
        @SerializedName("full_name")
        val fullName: String
)
