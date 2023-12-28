package com.pronovoscm.model.response.issueTracking.impactAndRootCause

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ImpactAndRootCauseResponse {
    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: ImpactAndRootCauseResponseData? = null
}