package com.pronovoscm.model.response.issueTracking.issues

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class IssuesResponse {
    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: IssuesResponseListData? = null
}