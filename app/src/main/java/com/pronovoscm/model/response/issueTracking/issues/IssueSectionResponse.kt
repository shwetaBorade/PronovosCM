package com.pronovoscm.model.response.issueTracking.issues

import com.google.gson.annotations.SerializedName

data class IssueSectionResponse(
    @SerializedName("status"  ) var status  : Int?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : SectionData?   = SectionData()
)

data class  SectionData (

    @SerializedName("pj_tracking_sections" ) var pjTrackingSections : ArrayList<PjTrackingSections> = arrayListOf(),
    @SerializedName("responseCode"         ) var responseCode       : Int?                          = null,
    @SerializedName("responseMsg"          ) var responseMsg        : String?                       = null

)

data class PjTrackingSections (

    @SerializedName("issue_tracking_sections_id" ) var issueTrackingSectionsId : Int?    = null,
    @SerializedName("section_name"               ) var sectionName             : String? = null,
    @SerializedName("sort_order"                 ) var sortOrder               : Int?    = null,
    @SerializedName("tenant_id"                  ) var tenantId                : Int?    = null,
    @SerializedName("users_id"                   ) var usersId                 : Int?    = null,
    @SerializedName("created_at"                 ) var createdAt               : String? = null,
    @SerializedName("updated_at"                 ) var updatedAt               : String? = null,
    @SerializedName("deleted_at"                 ) var deletedAt               : String? = null

)
