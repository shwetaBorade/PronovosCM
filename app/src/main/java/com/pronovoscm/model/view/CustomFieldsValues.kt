package com.pronovoscm.model.view

import com.google.gson.annotations.SerializedName

data class CustomFieldsValues(
    var customFields: CustomFields?=null
)
data class CustomFields(
    @SerializedName("issue_tracking_custom_id") var issueTrackingCustomId: Long?    = null,
    @SerializedName("issue_tracking_custom_mobile_id") var issueTrackingCustomMobileId: Long? = null,
    @SerializedName("issue_tracking_id") var issueTrackingId: Int?    = null,
    @SerializedName("issue_section_id") var issueSectionId: Int?    = null,
    @SerializedName("issue_tracking_items_id") var issueTrackingItemsId: Int?    = null,
    @SerializedName("tracking_item_types_id") var trackingItemTypesId: Int?    = null,
    @SerializedName("value") var value: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null
)

enum class InputTypes{
    text,
    number,
    checkbox,
    selectbox,
    radio,
    phone,
    date,
    currency,
}
