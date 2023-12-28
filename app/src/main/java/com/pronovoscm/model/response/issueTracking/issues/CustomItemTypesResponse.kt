package com.pronovoscm.model.response.issueTracking.issues

import com.google.gson.annotations.SerializedName

data class CustomItemTypesResponse(
    @SerializedName("status"  ) var status  : Int?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : CustomItemTypesData?   = CustomItemTypesData()
)

data class CustomItemTypesData (

    @SerializedName("tracking_item_types" ) var trackingItemTypes : ArrayList<TrackingItemTypes> = arrayListOf(),
    @SerializedName("responseCode"        ) var responseCode      : Int?                         = null,
    @SerializedName("responseMsg"         ) var responseMsg       : String?                      = null

)

data class TrackingItemTypes (

    @SerializedName("tracking_item_types_id" ) var trackingItemTypesId : Int?    = null,
    @SerializedName("label"                  ) var label               : String? = null,
    @SerializedName("type"                   ) var type                : String? = null,
    @SerializedName("options"                ) var options             : String? = null,
    @SerializedName("created_at"             ) var createdAt           : String? = null,
    @SerializedName("updated_at"             ) var updatedAt           : String? = null,
    @SerializedName("deleted_at"             ) var deletedAt           : String? = null

)

