package com.pronovoscm.model.view

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OptionList(
    var optionId:Int,
    var optionTitle:String
):Parcelable
