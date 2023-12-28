package com.pronovoscm.model.view

import com.google.gson.annotations.SerializedName

data class IssueListItemCustomFields(
    @SerializedName("created_at")
    val created_at: String?,
    @SerializedName("issue_section_id")
    val issue_section_id: Int,
    @SerializedName("issue_tracking_custom_id")
    val issue_tracking_custom_id: Long,
    @SerializedName("issue_tracking_custom_mobile_id")
    val issue_tracking_custom_mobile_id: Long,
    @SerializedName("issue_tracking_id")
    val issue_tracking_id: Long,
    @SerializedName("issue_tracking_mobile_id")
    val issue_tracking_mobile_id: Long,
    @SerializedName("issue_tracking_items_id")
    val issue_tracking_items_id: Int,
    @SerializedName("tracking_item_types_id")
    val tracking_item_types_id: Int,
    @SerializedName("value")
    val value: String?,
)

