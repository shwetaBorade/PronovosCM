package com.pronovoscm.model.response.issueTracking.impactAndRootCause

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ImpactAndRootCauseResponseData(
        @SerializedName("impacts_and_root_causes")
        @Expose
        var impactAndRootCausesList: List<ImpactAndRootCause>? = null,

        @SerializedName("responseCode")
        @Expose
        var responseCode: Int? = null,

        @SerializedName("responseMsg")
        @Expose
        var responseMsg: String? = null,
)