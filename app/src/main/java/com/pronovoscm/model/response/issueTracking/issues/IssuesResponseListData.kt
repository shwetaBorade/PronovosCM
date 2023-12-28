package com.pronovoscm.model.response.issueTracking.issues

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class IssuesResponseListData(
        @SerializedName("issue_trackings")
        @Expose
        var issuesList: List<IssueTracking>? = null,

        @SerializedName("responseCode")
        @Expose
        var responseCode: Int? = null,

        @SerializedName("responseMsg")
        @Expose
        var responseMsg: String? = null
)