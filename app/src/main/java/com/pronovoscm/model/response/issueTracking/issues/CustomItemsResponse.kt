package com.pronovoscm.model.response.issueTracking.issues

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.pronovoscm.model.view.ImpactAndRootCause
import kotlinx.android.parcel.Parcelize

data class CustomItemsResponse(
    @SerializedName("status"  ) var status  : Int?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : CustomItemsData?   = CustomItemsData()
)
data class CustomItemsData (

    @SerializedName("pj_tracking_items" ) var pjTrackingItems : ArrayList<PjTrackingItems> = arrayListOf(),
    @SerializedName("responseCode"      ) var responseCode    : Int?                       = null,
    @SerializedName("responseMsg"       ) var responseMsg     : String?                    = null

)

data class PjTrackingItems (

    @SerializedName("issue_tracking_items_id"    ) var issueTrackingItemsId    : Int?    = null,
    @SerializedName("issue_tracking_sections_id" ) var issueTrackingSectionsId : Int?    = null,
    @SerializedName("item_name"                  ) var itemName                : String? = null,
    @SerializedName("sort_order"                 ) var sortOrder               : Int?    = null,
    @SerializedName("tenant_id"                  ) var tenantId                : Int?    = null,
    @SerializedName("users_id"                   ) var usersId                 : Int?    = null,
    @SerializedName("additional_data"            ) var additionalData          : ArrayList<AdditionalData> = arrayListOf(),
    @SerializedName("tracking_item_types_id"     ) var trackingItemTypesId     : Int?    = null,
    @SerializedName("created_at"                 ) var createdAt               : String? = null,
    @SerializedName("updated_at"                 ) var updatedAt               : String? = null,
    @SerializedName("deleted_at"                 ) var deletedAt               : String? = null

)
@Parcelize
data class AdditionalData (

    @SerializedName("name"  ) var name  : String? = null,
    @SerializedName("order" ) var order : String? = null,
    @SerializedName("id"    ) var id    : String? = null
) : Parcelable,ImpactAndRootCause

@Parcelize
data class TimeZone (

    @SerializedName("name"  ) var name  : String? = null,
    @SerializedName("id"    ) var id    : String? = null
) : Parcelable,ImpactAndRootCause