package com.pronovoscm.model.response.issueTracking.impactAndRootCause

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ImpactAndRootCause(
        @SerializedName("pj_issues_causeimpact_id")
        @Expose
        val pjIssueCauseImpactId: Long? = null,

        @SerializedName("name")
        @Expose
        val name: String? = null,

        @SerializedName("is_impact")
        @Expose
        val impactStatus: Int? = null,

        @SerializedName("created_at")
        @Expose
        val createdAt: String? = null,

        @SerializedName("updated_at")
        @Expose
        val updatedAt: String? = null,

        @SerializedName("icon")
        @Expose
        val iconUrl: String? = null,

        @SerializedName("mobile_icon")
        @Expose
        val mobileIcon: String? = null
)