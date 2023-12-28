package com.pronovoscm.model.response.issueTracking.issues

import com.google.gson.annotations.SerializedName

data class Assignee(
    @SerializedName("assignee_id")
    var assigneeId   : Long,
    @SerializedName("assignee_name")
    var assigneeName : String? = null
)